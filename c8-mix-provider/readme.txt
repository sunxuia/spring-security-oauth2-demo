一个同时提供自定登录和通过github 登录的sso 提供方, 客户端使用c7-oauth2-provider 访问.
如果要除了github 之外还有其他地方放登录, 可以参考 c5 的实行方式来进行.
*** 将github 中的回调url 设为 http://127.0.0.1:8888/login/github ***