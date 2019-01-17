和c7 类似, 不同在在于请求resource的时候, 不是向资源服务器请求用户信息, 而是向认证服务器请求解析access token 的方式.

资源服务器的设置不同:
    resource:
      jwt:
        key-uri: http://127.0.0.1:8888/oauth/token_key

authentication server 这个接口返回的内容类似如下, 前者是签名算法, 后者是密钥
{"alg":"HMACSHA256","value":"merryyou"}
provider 添加了包spring-security-jwt, 提供了对应的功能, 在c7 中请求/oauth/token_key 这个接口返回的是null.
client 的principal 是解析access token 得到的结果.
访问这个url 需要提供base64 编码的用户名/ 密码.