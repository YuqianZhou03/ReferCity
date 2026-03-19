Page({
  data: {
    isParsing: false, // 初始状态为 false
    // 必须与 WXML 中的 {{resumeData.xxx}} 结构严格对应
    resumeData: {
      name: '',
      edu: '',
      skills: '',
      experience: ''
    }
  },

  onLoad: function() {
    this.loadInitialData();
  },

  // 核心：处理所有输入框的监听
  onInput: function(e) {
    const field = e.currentTarget.dataset.field; // 对应 WXML 中的 data-field
    const value = e.detail.value;
    
    // 使用变量名动态更新对象属性
    this.setData({
      [`resumeData.${field}`]: value
    });
  },

  // 获取已有数据进行回填
  loadInitialData: function() {
    const emailPrefix = wx.getStorageSync('emailPrefix');
    if (!emailPrefix) return;

    wx.request({
      url: `http://localhost:8080/api/resume/status?email=${emailPrefix}`,
      method: 'GET',
      success: (res) => {
        // 假设后端返回的对象是 { exists: true, resume: { name: '...', edu: '...' } }
        if (res.data && res.data.exists && res.data.resume) {
          this.setData({
            resumeData: res.data.resume
          });
        }
      }
    });
  },

  /**
     * 核心新功能：上传 PDF 并利用 AI 自动填充
  */
  uploadResume: function() {
    // 1. 让用户选择文件
    wx.chooseMessageFile({
      count: 1,
      type: 'file',
      extension: ['pdf'], 
      success: (res) => {
        const filePath = res.tempFiles[0].path;
        
        // --- 动画开启 ---
        // 设置 isParsing 为 true，此时 WXML 中的动画类和扫描线会立即生效
        this.setData({ isParsing: true });

        // 2. 执行上传
        wx.uploadFile({
          url: 'http://localhost:8080/api/resume/upload', 
          filePath: filePath,
          name: 'file', 
          success: (uploadRes) => {
            // --- 动画结束 ---
            this.setData({ isParsing: false });
            
            try {
              // 注意：wx.uploadFile 返回的是字符串，需要解析
              const parsedData = JSON.parse(uploadRes.data);
              
              if (parsedData) {
                // 3. 核心：回填数据
                // 此时页面上的输入框会伴随动画结束瞬间“填满”
                this.setData({
                  resumeData: {
                    name: parsedData.name || '',
                    edu: parsedData.edu || '',
                    skills: parsedData.skills || '',
                    experience: parsedData.experience || ''
                  }
                });

                wx.showToast({ 
                  title: 'AI 解析填充成功', 
                  icon: 'success',
                  duration: 2000
                });
              }
            } catch (e) {
              console.error("JSON 解析错误:", e);
              wx.showToast({ title: '数据格式异常', icon: 'none' });
            }
          },
          fail: (err) => {
            // --- 异常处理：也要记得关闭动画 ---
            this.setData({ isParsing: false });
            console.error("上传失败:", err);
            wx.showToast({ title: '服务器连接失败', icon: 'none' });
          }
        });
      },
      fail: () => {
        // 用户取消选择文件，不执行任何操作
      }
    });
  },

  // 提交到后端
  saveResume: function() {
    const emailPrefix = wx.getStorageSync('emailPrefix');
    if (!emailPrefix) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      return;
    }

    wx.showLoading({ title: '同步中...', mask: true });

    wx.request({
      url: 'http://localhost:8080/api/resume/save',
      method: 'POST',
      data: {
        email: emailPrefix,
        ...this.data.resumeData // 展开对象，直接发送 name, edu, skills, experience
      },
      success: (res) => {
        wx.hideLoading();
        wx.showToast({ title: '已同步云端', icon: 'success' });
        // 成功后延迟返回
        setTimeout(() => { wx.navigateBack(); }, 1000);
      },
      fail: () => {
        wx.hideLoading();
        wx.showToast({ title: '网络异常', icon: 'none' });
      }
    });
  }
});