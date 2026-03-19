// pages/home/home.js
Page({
  data: {
    jobList: [] // 初始设为空，等待后端数据填充
  },

  // 生命周期函数--监听页面加载
  onLoad: function() {
    this.fetchJobs();
  },

  // 下拉刷新：让用户能看到最新职位
  onPullDownRefresh: function() {
    this.fetchJobs();
  },

  fetchJobs: function() {
    wx.showLoading({ title: '正在获取职位...' });
    
    wx.request({
      url: 'http://localhost:8080/api/jobs/list', // 这里的路径必须和 JobController 一致
      method: 'GET',
      success: (res) => {
        console.log(">>> 成功获取职位列表：", res.data);
        // 这里的 res.data 就是后端返回的 List<Job>
        this.setData({
          jobList: res.data
        });
        wx.stopPullDownRefresh(); // 停止下拉刷新动画
      },
      fail: (err) => {
        console.error("职位获取失败：", err);
        wx.showToast({ title: '加载失败，请检查后端', icon: 'none' });
      },
      complete: () => {
        wx.hideLoading();
      }
    });
  },

  // 点击职位卡片跳转详情
  goToDetail: function(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: '/pages/jobDetail/jobDetail?id=' + id });
  }
});