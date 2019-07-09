# cb-jwt-client 使用jwt 进行验证改进

相比cb-jwt-client 增加了使用jwt 验证和配置jwt 认证头的内容.

最后会将jwt 的内容放到Authorization 头返回给客户端. (认证成功后返回302 跳转到认证前想要访问的地址的时候).

## 步骤 :

0. http://127.0.0.1:8888/ 登录, 否则还要加上处理登录页面的情况

0. 8080 启动时候访问8888 获得jwt 的密钥, {"alg":"HMACSHA256","value":"secret"}.

0. 发起请求(http://127.0.0.1:8080/authenticated)

    ```http request
    GET /authenticated HTTP/1.1
    Host: 127.0.0.1:8080
    Connection: keep-alive
    User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36
    Accept: */*
    Accept-Encoding: gzip, deflate, br
    Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
    Cookie: JSESSIONID=CF6027F2B2BDE3A3391CD78B86A8FCAE

    ```
    
    jsessionid 的cookie 是http://127.0.0.1:8888 中的session id.
    
    请求的URL 需要认证, 所以返回302 要求跳转到登录页面.
    
    ```http response
    302
    Set-Cookie : OAUTH2_CLIENT_SESION=3B9CC8AC317BAB4BE1B2ED57A5E9BEF2; Path=/; HttpOnly
    X-Content-Type-Options : nosniff
    X-XSS-Protection : 1; mode=block
    Cache-Control : no-cache, no-store, max-age=0, must-revalidate
    Pragma : no-cache
    Expires : 0
    X-Frame-Options : DENY
    Location : http://127.0.0.1:8080/login

    ```

0. 根据返回指示执行跳转登录URL (http://127.0.0.1:8080/login)

    ```http request
    GET /login HTTP/1.1
    Host: 127.0.0.1:8080
    Connection: keep-alive
    User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36
    Accept: */*
    Accept-Encoding: gzip, deflate, br
    Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
    Cookie: JSESSIONID=CF6027F2B2BDE3A3391CD78B86A8FCAE; OAUTH2_CLIENT_SESION=3B9CC8AC317BAB4BE1B2ED57A5E9BEF2

    ```
    
    这里登录的URL 返回302, 要求跳转到认证网站.
    
    ```http response
    302
    X-Content-Type-Options : nosniff
    X-XSS-Protection : 1; mode=block
    Cache-Control : no-cache, no-store, max-age=0, must-revalidate
    Pragma : no-cache
    Expires : 0
    X-Frame-Options : DENY
    Location : http://127.0.0.1:8888/oauth/authorize?client_id=ssoclient-1&redirect_uri=http://127.0.0.1:8080/login&response_type=code&state=fcYJFP

    ```

0. 根据返回指示跳转认证网页 (http://127.0.0.1:8888/oauth/authorize)

    ```http request
    GET /oauth/authorize?client_id=ssoclient-1&redirect_uri=http%3A%2F%2F127.0.0.1%3A8080%2Flogin&response_type=code&state=fcYJFP HTTP/1.1
    Host: 127.0.0.1:8888
    Connection: keep-alive
    User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36
    Accept: */*
    Accept-Encoding: gzip, deflate, br
    Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
    Cookie: JSESSIONID=CF6027F2B2BDE3A3391CD78B86A8FCAE; OAUTH2_CLIENT_SESION=3B9CC8AC317BAB4BE1B2ED57A5E9BEF2

    ```
    
    这里因为已经认证过了, 所以跳过认证网站的登录步骤, 直接返回跳转到8080
    
    ```http response
    302
    Cache-Control : no-store
    X-Content-Type-Options : nosniff
    X-XSS-Protection : 1; mode=block
    X-Frame-Options : DENY
    Location : http://127.0.0.1:8080/login?code=tqZzOc&state=fcYJFP

    ```

0. 根据返回指示执行跳转(http://127.0.0.1:8080/login)

    ```http request
    GET /login?code=tqZzOc&state=fcYJFP HTTP/1.1
    Host: 127.0.0.1:8080
    Connection: keep-alive
    User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36
    Accept: */*
    Accept-Encoding: gzip, deflate, br
    Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
    Cookie: JSESSIONID=CF6027F2B2BDE3A3391CD78B86A8FCAE; OAUTH2_CLIENT_SESION=3B9CC8AC317BAB4BE1B2ED57A5E9BEF2

    ```
    
    之后8080 根据url 中传来的code, 从8888 获得access token, 并将access token 添加到返回的头部中.
    
    ```http response
    302
    Authorization : Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NDc0MjI1MzAsInVzZXJfbmFtZSI6ImFkbWluIiwiYXV0aG9yaXRpZXMiOlsiYWRtaW4iLCJ1c2VyIl0sImp0aSI6IjAxNmM3MmNiLWJlN2QtNDliNi1hOWE0LWFjN2M1ZGI5Yzg4NSIsImNsaWVudF9pZCI6InNzb2NsaWVudC0xIiwic2NvcGUiOlsib3BlbmlkIl19.wkLySV5QcMNZYSyCEDolh9-zZKaFFRKcuGNahXOO1lg
    Set-Cookie : OAUTH2_CLIENT_SESION=B414B31A19994BD4C051A3BB3482FA47; Path=/; HttpOnly
    X-Content-Type-Options : nosniff
    X-XSS-Protection : 1; mode=block
    Cache-Control : no-cache, no-store, max-age=0, must-revalidate
    Pragma : no-cache
    Expires : 0
    X-Frame-Options : DENY
    Location : http://127.0.0.1:8080/authenticated

    ```

0. 重启8080, 带上之前返回的jwt, 向8080 发送请求.
    ```http request
    GET /authenticated HTTP/1.1
    Host: 127.0.0.1:8080
    Connection: keep-alive
    Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NDc0MjI1MzAsInVzZXJfbmFtZSI6ImFkbWluIiwiYXV0aG9yaXRpZXMiOlsiYWRtaW4iLCJ1c2VyIl0sImp0aSI6IjAxNmM3MmNiLWJlN2QtNDliNi1hOWE0LWFjN2M1ZGI5Yzg4NSIsImNsaWVudF9pZCI6InNzb2NsaWVudC0xIiwic2NvcGUiOlsib3BlbmlkIl19.wkLySV5QcMNZYSyCEDolh9-zZKaFFRKcuGNahXOO1lg
    User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36
    Accept: */*
    Accept-Encoding: gzip, deflate, br
    Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
    Cookie: JSESSIONID=C182C070B3C612FD56A1C872A43F4A18; OAUTH2_CLIENT_SESION=8F60868C855E3C113B86A123EE1A1569
    
    ```

    ```http response
    200
    Set-Cookie : OAUTH2_CLIENT_SESION=0B10824C0ADE01993B69ACDAA1AB34EF; Path=/; HttpOnly
    X-Content-Type-Options : nosniff
    X-XSS-Protection : 1; mode=block
    Cache-Control : no-cache, no-store, max-age=0, must-revalidate
    Pragma : no-cache
    Expires : 0
    X-Frame-Options : DENY
    Content-Type : text/plain;charset=UTF-8
    Content-Length : 13
    Date : Sun, 13 Jan 2019 13:01:01 GMT
    
    authenticated
    ```
    
    可以看到使用这个jwt 认证成功了.



因为spring security 的oauth2 依赖于session 来实现两次访问的认证, (state 就保存在session 中), 所以在这里不能取消session 而单独使用jwt.

