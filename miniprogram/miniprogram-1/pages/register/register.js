Page({
  data: {
    emailPrefix: ''
  },

  // 监听输入
  handleEmailInput: function(e) {
    this.setData({
      emailPrefix: e.detail.value
    });
  },

  handleRegister: function() {
    // 1. 基础校验
    const email = this.data.emailPrefix;
    if (!email) {
      wx.showToast({ title: '请先输入邮箱前缀', icon: 'none' });
      return;
    }

    // 2. 开启 Loading
    wx.showLoading({ title: '身份认证中...', mask: true });

    // 3. 执行微信登录获取 Code
    wx.login({
      success: (loginRes) => {
        if (loginRes.code) {
          // 4. 发送到后端
          wx.request({
            url: 'http://localhost:8080/api/register',
            method: 'POST',
            data: {
              emailPrefix: email,
              code: loginRes.code
            },
            success: (res) => {
              wx.hideLoading();
              
              if (res.data.code === 200) {
                // --- 【关键修改点】：注册成功，立即持久化身份标识 ---
                // 这样跳转到首页或详情页后，wx.getStorageSync('emailPrefix') 就能拿到值了
                wx.setStorageSync('emailPrefix', email);
                console.log("注册成功，身份标识已存入缓存:", email);

                wx.showToast({
                  title: '注册成功',
                  icon: 'success',
                  duration: 1500
                });

                // 延迟 1.5 秒后跳转
                setTimeout(() => {
                  wx.redirectTo({
                    url: '/pages/home/home',
                    fail: (err) => {
                      console.error("跳转首页失败，请检查 app.json 路径:", err);
                    }
                  });
                }, 1500);

              } else {
                // 处理业务错误（如已注册）
                wx.showModal({
                  title: '提示',
                  content: res.data.msg || '认证未通过',
                  showCancel: false
                });
              }
            },
            fail: (err) => {
              wx.hideLoading();
              wx.showModal({
                title: '网络异常',
                content: '连接后端失败，请检查服务器是否开启',
                showCancel: false
              });
            }
          });
        } else {
          wx.hideLoading();
          wx.showToast({ title: '微信登录失败', icon: 'none' });
        }
      },
      fail: () => {
        wx.hideLoading();
        wx.showToast({ title: '调用微信登录失败', icon: 'none' });
      }
    });
  }
});