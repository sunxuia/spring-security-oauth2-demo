使用github 的oauth2 功能来登录(不使用自带的表单验证)
*** 需要注意现在github 这个应用设置的回调页面为 /login ***
准备工作 :
1. 申请github 的一个oauth2 的应用 https://github.com/settings/applications/new
   - 申请过的可以在 https://github.com/settings/developers 中找到相关的信息
   - 回调url 是http://localhost:8080/login,
     这个地址需要和回调传递的参数一致, spring security 默认在认证的时候服务器会把回调函数发给github 认证服务器, 如果发送的是
     127.0.0.1/login, 那么就与登记的localhost:8080/login, 不一致, 会返回一个要求一致的错误 (重定向到error页面)
     不过如果登记的是/login,  而传递的redirect_uri 是/login/github,  则这样github 的认证服务器会去调用/login/github 这个而不出错.

2. 在application.yml 中填入相应的id, 密码 和对应url
3. 验证完成之后就可以在principle 中找到用户的相关信息, github 的用户信息的url 是https://api.github.com/user
  - 在程序中通过访问localhost:8080/ 可以访问到

验证的相关流程参见 https://developer.github.com/apps/building-oauth-apps/authorizing-oauth-apps/