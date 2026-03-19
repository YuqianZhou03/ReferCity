Page({
  data: {
    jobId: null,
    job: null,
    hasResume: false, 
    userResumeData: null, 
    loading: false,
    // AI匹配分析结果
    aiResult: null,
    displayAnalysis: "", // 用于逐字显示的文本
    displayTips: []      // 用于逐行显示的锦囊
  },

  onLoad: function (options) {
    const id = options.id;
    if (id) {
      this.setData({ jobId: id });
      this.fetchJobData(id);
    }
  },

  onShow: function () {
    this.fetchUserResumeStatus();
  },

  // 1. 获取职位详情
  fetchJobData: function (id) {
    wx.request({
      url: `http://localhost:8080/api/job-details/${id}`,
      method: 'GET',
      success: (res) => {
        this.setData({ job: res.data });
      }
    });
  },

  // 2. 从后端检查简历 
  fetchUserResumeStatus: function () {
    const emailPrefix = wx.getStorageSync('emailPrefix'); 
    wx.request({
      url: `http://localhost:8080/api/resume/status?email=${emailPrefix}`,
      method: 'GET',
      success: (res) => {
        if (res.data && res.data.exists) {
          this.setData({ 
            hasResume: true,
            userResumeData: res.data.content 
          });
        } else {
          this.setData({ hasResume: false });
        }
      }
    });
  },

  // 3. 跳转
  goToResumeInput: function () {
    wx.navigateTo({
      url: '/pages/resumeEdit/resumeEdit'
    });
  },

  /**
     * 4. 核心：一键 AI 分析 (适配真实后端)
  */
  startAIAnalysis: function () {
    if (!this.data.hasResume) {
      wx.showModal({
        title: '提示',
        content: '请先上传或填写您的简历信息，以便 AI 进行精准匹配。',
        confirmText: '去填写',
        success: (res) => { if (res.confirm) this.goToResumeInput(); }
      });
      return;
    }

    // 进入加载状态
    this.setData({ loading: true, aiResult: null });

    const emailPrefix = wx.getStorageSync('emailPrefix');

    // --- RAG强化学习prompt合成 ---
    const job = this.data.job;
    const jobTitle = job ? job.title : "该岗位"; // 对应你的 String title
    const company = job ? job.company : "大厂";   // 对应你的 String company
    const jd = job ? job.description : "";        // 对应你的 String description

    // 构造发送给后端的 Prompt，增加 JD 维度，让搜索更准
    const userQuery = `我想面试${company}的${jobTitle}岗位。职位要求是：${jd}。请结合库里的校友面经，为我生成匹配分析和面试建议。`;

    // 发起真实请求
    wx.request({
      url: 'http://localhost:8080/api/ai/match', // 你的后端匹配接口
      method: 'POST',
      data: {
        jobId: this.data.jobId,
        email: emailPrefix,
        query: userQuery
      },
      success: (res) => {
        if (res.statusCode === 200 && res.data) {
          const aiData = res.data; // 包含 matchRate, analysis, tips, radarImageUrl 等
          const targetScore = aiData.matchRate;

          // 1. 停止加载，展示卡片
          this.setData({
            loading: false,
            // 关键：先将 matchRate 设为 0 以便执行动画
            aiResult: { ...aiData, matchRate: 0 } ,
            displayAnalysis: "",  //优势与短板
            displayTips: [] //面试集锦
          }, () => {
            // 2. 启动数字跳动动画
            this.animateScore(targetScore);
            this.startStreamingEffect(aiData.analysis, aiData.tips); // 开始流式文字
          });

          // 3. 反馈与滚动
          wx.vibrateShort();
          wx.showToast({ title: '匹配成功', icon: 'success' });
          setTimeout(() => {
            wx.pageScrollTo({ selector: '.ai-result-card', duration: 400 });
          }, 200);

        } else {
          this.handleAnalysisError();
        }
      },
      fail: () => {
        this.handleAnalysisError();
      }
    });
  },

  handleAnalysisError: function() {
      this.setData({ loading: false });
      wx.showToast({ title: '分析失败，请稍后重试', icon: 'none' });
    },

    // 定义流式展示函数
  startStreamingEffect: function(fullText, fullTips) {
    let charIndex = 0;
    const speed = 30; // 逐字速度（毫秒）

    // 1. 优势与短板：逐字蹦出
    const textTimer = setInterval(() => {
      if (charIndex < fullText.length) {
        charIndex++;
        this.setData({
          displayAnalysis: fullText.substring(0, charIndex)
        });
        // 这里的滚动可以让页面跟着文字走
        if (charIndex % 10 === 0) { 
          wx.pageScrollTo({ selector: '.analysis-box', duration: 100 }); 
        }
      } else {
        clearInterval(textTimer);
        // 2. 文本播完后，开始逐条弹出面试锦囊
        this.startTipsStreaming(fullTips);
      }
    }, speed);
  },

  startTipsStreaming: function(tips) {
    let tipIndex = 0;
    const tipTimer = setInterval(() => {
      if (tipIndex < tips.length) {
        this.setData({
          [`displayTips[${tipIndex}]`]: tips[tipIndex]
        });
        tipIndex++;
      } else {
        clearInterval(tipTimer);
      }
    }, 500); // 每隔 0.5 秒出一道题
  },

  /**
   * 动画函数保持不变，它会自动更新 aiResult.matchRate
   */
  animateScore: function(target) {
    let current = 0;
    const timer = setInterval(() => {
      current += 2;
      if (current >= target) {
        current = target;
        clearInterval(timer);
      }
      this.setData({ 'aiResult.matchRate': current });
    }, 20);
  }
});