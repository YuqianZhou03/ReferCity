Page({
  onLoad: function() {
    // 1. 获取临时登录凭证
    wx.login({
      success: (res) => {
        if (res.code) {
          // 2. 发送到后端检查是否已注册
          wx.request({
            url: 'http://localhost:8080/api/checkLogin', 
            method: 'POST',
            data: { code: res.code },
            success: (response) => {
              console.log(">>> 后端返回的登录检查结果：", response.data);
              
              const isRegistered = response.data.registered;
              
              if (isRegistered === true || isRegistered === 'true') {
                // 【关键修改点】：保存用户身份标识
                // 只有存了 emailPrefix，后续详情页和简历页才能正常查询数据库
                if (response.data.emailPrefix) {
                  wx.setStorageSync('emailPrefix', response.data.emailPrefix);
                  console.log("身份标识已同步至缓存:", response.data.emailPrefix);
                }

                console.log("识别成功，跳转首页...");
                wx.redirectTo({
                  url: '/pages/home/home',
                  fail: (err) => {
                    console.error("跳转首页失败:", err);
                  }
                });
              } else {
                console.log("未注册，跳转注册页...");
                // 如果未注册，建议清空旧的缓存，防止身份混淆
                wx.removeStorageSync('emailPrefix');
                wx.redirectTo({
                  url: '/pages/register/register'
                });
              }
              
            },
            fail: (err) => {
              console.error("网络请求失败:", err);
              // 网络失败，保底去注册页
              wx.redirectTo({ url: '/pages/register/register' });
            }
          });
        }
      }
    });
  }
});