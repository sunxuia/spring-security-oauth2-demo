# spring cloud 的授权方式

一个sso 站点的认证和资源服务器端.

自定义了/me 为用户信息的获取点. 客户端首先通过验证, 然后通过/me 访问用户信息. 客户端调用/me 时候的principal 是oauth2 的principal, 和保存在用户session 中的内容不一样.
资源站点的定义是通过 net.sunxu.study.c7.provider.ResourceServerConfiguration 来设置的.

另一个/user 没有定义为资源站点, 所以用户登录之后可以访问, 但是客户端访问则会被重定向到  /login 页面.

## authorization_code

最常用的授权码. 事前启动c7-oauth2-client 作为client, 然后用户访问client, client 重定向到provider, provider 认证通过后将code 作为查询参数重定向到client, client 根据code 向provider 请求用户access token.

通信方式可以查看c7-oauth2-client 中的readme.

## client_credentials

客户端模式. 和用户没关系, client 根据自己注册的 id 和密码向provider 请求access token.

(直接把client 认证信息 post 到 http://127.0.0.1:8888/oauth/token?grant_type=client_credentials&client_id=ssoclient-1&client_secret=ssosecret)

```http request
POST /oauth/token?grant_type=client_credentials&client_id=ssoclient-1&client_secret=ssosecret HTTP/1.1
Host: 127.0.0.1:8888
Connection: keep-alive
Content-Length: 0
Origin: chrome-extension://aejoelaoggembcahagimdiliamlcdmfm
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36
Accept: */*
Accept-Encoding: gzip, deflate, br
Accept-Language: zh-CN,zh;q=0.9,en;q=0.8

```

```http response
200
Cache-Control : no-store
Pragma : no-cache
X-Content-Type-Options : nosniff
X-XSS-Protection : 1; mode=block
X-Frame-Options : DENY
Content-Type : application/json;charset=UTF-8
Transfer-Encoding : chunked
Date : Wed, 16 Jan 2019 01:58:37 GMT

{"access_token":"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJvcGVuaWQiXSwiZXhwIjoxNTQ3NjQ3MTE3LCJqdGkiOiJhMzRhN2U3ZC01MDVhLTRkMmMtODBhMC03OTM3ZDM1YTAwNWIiLCJjbGllbnRfaWQiOiJzc29jbGllbnQtMSIsImxvY2FsLXVzZXItbmFtZSI6InNzb2NsaWVudC0xIn0.31veaE-4qUDD0HDwZCQ_x8C_lnvQFywMmz-FB7i5jgCJ0iOA_t9fixUX2xzG3Jb6x4sH9HgQLExu1lvc0JRxSpZAg2PA461JdkeShA48N-ZfPeN-bI2zV1SWYaaaYqRiyeXAlrNcmeJp6K3CgG9YIv9lshWRnrBXFVZUdJXXPtA","token_type":"bearer","expires_in":43199,"scope":"openid","local-user-name":"ssoclient-1","jti":"a34a7e7d-505a-4d2c-80a0-7937d35a005b"}

```

access token 的内容是 ```{"alg":"RS256","typ":"JWT"}.{"scope":["openid"],"exp":1547647117,"jti":"a34a7e7d-505a-4d2c-80a0-7937d35a005b","client_id":"ssoclient-1","local-user-name":"ssoclient-1"}```

## password

密码模式. 用户把用户名和密码告诉client, client 拿着它去请求access token.

直接把用户认证信息和client 认证信息 post 到http://127.0.0.1:8888/oauth/token?grant_type=password&client_id=ssoclient-1&client_secret=ssosecret&username=admin&password=123456 (scopeid 可有可无)

```http request
POST /oauth/token?grant_type=password&client_id=ssoclient-1&client_secret=ssosecret&username=admin&password=123456 HTTP/1.1
Host: 127.0.0.1:8888
Connection: keep-alive
Content-Length: 0
Origin: chrome-extension://aejoelaoggembcahagimdiliamlcdmfm
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36
Accept: */*
Accept-Encoding: gzip, deflate, br
Accept-Language: zh-CN,zh;q=0.9,en;q=0.8

```

```http response
200
Cache-Control : no-store
Pragma : no-cache
X-Content-Type-Options : nosniff
X-XSS-Protection : 1; mode=block
X-Frame-Options : DENY
Content-Type : application/json;charset=UTF-8
Transfer-Encoding : chunked
Date : Wed, 16 Jan 2019 03:13:03 GMT

{"access_token":"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsInNjb3BlIjpbIm9wZW5pZCJdLCJleHAiOjE1NDc2NTE1NzIsImF1dGhvcml0aWVzIjpbImFkbWluIiwidXNlciJdLCJqdGkiOiJkZGEzOTdlNC04OTAxLTQ2ZjAtODllNy01MTFjOGE2MzgzNzgiLCJjbGllbnRfaWQiOiJzc29jbGllbnQtMSIsImxvY2FsLXVzZXItbmFtZSI6ImFkbWluIn0.Kqoum9elfemI9VHGOsE55lch471qbQh6CnOuRqiBiw9GSD9gfFoyjCLcwK_qRWL0YCBlT-4z3WABOgwWF-bYIew-hXNx5-yfAv5wIzYUY6z25nOC02UFaF0c3leVcSLaNH_AYxKrVFT7h-hSwmhe37nx5HhJZ75IfW2U5eto69g","token_type":"bearer","refresh_token":"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsInNjb3BlIjpbIm9wZW5pZCJdLCJhdGkiOiJkZGEzOTdlNC04OTAxLTQ2ZjAtODllNy01MTFjOGE2MzgzNzgiLCJleHAiOjE1NTAyMDAzNzIsImF1dGhvcml0aWVzIjpbImFkbWluIiwidXNlciJdLCJqdGkiOiI4M2YwNmViNS1mMjE3LTQ1OTUtYTM4ZC0xMmMyZmI3NWU3ZTUiLCJjbGllbnRfaWQiOiJzc29jbGllbnQtMSIsImxvY2FsLXVzZXItbmFtZSI6ImFkbWluIn0.WugWtGVGFIp755pfUJ7__jE8Tbeq3HI4nIITdKS5iuA95Gfv9iWdISrXQQbp83ucyalREnujv5I6ibBecqECL9wvA764G973OU6l-DPfcm2t2vHy0DuwFCwFXiHoJrrp97OEM2H9Yz9ORUNNeyHoh-lt3ccH6cubVHnElj_xOis","expires_in":43188,"scope":"openid","local-user-name":"admin","jti":"dda397e4-8901-46f0-89e7-511c8a638378"}
```

access token 的内容是```{"alg":"RS256","typ":"JWT"}.{"user_name":"admin","scope":["openid"],"exp":1547651572,"authorities":["admin","user"],"jti":"dda397e4-8901-46f0-89e7-511c8a638378","client_id":"ssoclient-1","local-user-name":"admin"}```

## refresh token

通过refresh token 来刷新获得新的access token.

把client 认证信息和客户的refresh token post 到 ```http://127.0.0.1:8888/oauth/token?grant_type=refresh_token&client_id=ssoclient-1&client_secret=ssosecret&refresh_token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsInNjb3BlIjpbIm9wZW5pZCJdLCJhdGkiOiJkZGEzOTdlNC04OTAxLTQ2ZjAtODllNy01MTFjOGE2MzgzNzgiLCJleHAiOjE1NTAyMDAzNzIsImF1dGhvcml0aWVzIjpbImFkbWluIiwidXNlciJdLCJqdGkiOiI4M2YwNmViNS1mMjE3LTQ1OTUtYTM4ZC0xMmMyZmI3NWU3ZTUiLCJjbGllbnRfaWQiOiJzc29jbGllbnQtMSIsImxvY2FsLXVzZXItbmFtZSI6ImFkbWluIn0.WugWtGVGFIp755pfUJ7__jE8Tbeq3HI4nIITdKS5iuA95Gfv9iWdISrXQQbp83ucyalREnujv5I6ibBecqECL9wvA764G973OU6l-DPfcm2t2vHy0DuwFCwFXiHoJrrp97OEM2H9Yz9ORUNNeyHoh-lt3ccH6cubVHnElj_xOis```

```http request
POST /oauth/token?grant_type=refresh_token&client_id=ssoclient-1&client_secret=ssosecret&refresh_token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsInNjb3BlIjpbIm9wZW5pZCJdLCJhdGkiOiJkZGEzOTdlNC04OTAxLTQ2ZjAtODllNy01MTFjOGE2MzgzNzgiLCJleHAiOjE1NTAyMDAzNzIsImF1dGhvcml0aWVzIjpbImFkbWluIiwidXNlciJdLCJqdGkiOiI4M2YwNmViNS1mMjE3LTQ1OTUtYTM4ZC0xMmMyZmI3NWU3ZTUiLCJjbGllbnRfaWQiOiJzc29jbGllbnQtMSIsImxvY2FsLXVzZXItbmFtZSI6ImFkbWluIn0.WugWtGVGFIp755pfUJ7__jE8Tbeq3HI4nIITdKS5iuA95Gfv9iWdISrXQQbp83ucyalREnujv5I6ibBecqECL9wvA764G973OU6l-DPfcm2t2vHy0DuwFCwFXiHoJrrp97OEM2H9Yz9ORUNNeyHoh-lt3ccH6cubVHnElj_xOis HTTP/1.1
Host: 127.0.0.1:8888
Connection: keep-alive
Content-Length: 0
Origin: chrome-extension://aejoelaoggembcahagimdiliamlcdmfm
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36
Accept: */*
Accept-Encoding: gzip, deflate, br
Accept-Language: zh-CN,zh;q=0.9,en;q=0.8

```

```http response
200
Cache-Control : no-store
Pragma : no-cache
X-Content-Type-Options : nosniff
X-XSS-Protection : 1; mode=block
X-Frame-Options : DENY
Content-Type : application/json;charset=UTF-8
Transfer-Encoding : chunked
Date : Wed, 16 Jan 2019 03:26:35 GMT

{"access_token":"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsInNjb3BlIjpbIm9wZW5pZCJdLCJleHAiOjE1NDc2NTIzOTUsImF1dGhvcml0aWVzIjpbImFkbWluIiwidXNlciJdLCJqdGkiOiIzYTY5ZDQ5NC1kNjYxLTQwNzYtODk1Ny1hN2I4Mjg4MWRmMjkiLCJjbGllbnRfaWQiOiJzc29jbGllbnQtMSIsImxvY2FsLXVzZXItbmFtZSI6ImFkbWluIn0.Zp8m3b5klmePa56RUDNGgS1UxQKsGcCF7r87LsK0fHVCJYUPmaortI3LWaoKOtQzm-Ym_r8zp7nYd2e46A8RQxRotaByiPjhOAov-gKskKg3VOdgDHwLDuiYS0CqXTUSFTxYr1Kyk8CUOB39cy3uuNleg0vdV0ulmsh-et5q1j4","token_type":"bearer","refresh_token":"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsInNjb3BlIjpbIm9wZW5pZCJdLCJhdGkiOiIzYTY5ZDQ5NC1kNjYxLTQwNzYtODk1Ny1hN2I4Mjg4MWRmMjkiLCJleHAiOjE1NTAyMDAzNzIsImF1dGhvcml0aWVzIjpbImFkbWluIiwidXNlciJdLCJqdGkiOiI4M2YwNmViNS1mMjE3LTQ1OTUtYTM4ZC0xMmMyZmI3NWU3ZTUiLCJjbGllbnRfaWQiOiJzc29jbGllbnQtMSIsImxvY2FsLXVzZXItbmFtZSI6ImFkbWluIn0.z2FmuZTug5q7fHAuXXJxHvBaeymxaSQHh1ngCVvL4hnGIrWYRfMww5cnpFo7NeYE5Kg_2YOifsIG0ZPHv51G72G4nX7aFljzMmtqQv-DMvtJDuY2WcXXW1ucBJw8GlDZdxE4ilyDkwLqonzVnGUMTqmz9Aoesljx0ff_jnUSuRg","expires_in":43199,"scope":"openid","local-user-name":"admin","jti":"3a69d494-d661-4076-8957-a7b82881df29"}

```

返回的access token 内容是```{"alg":"RS256","typ":"JWT"}.{"user_name":"admin","scope":["openid"],"exp":1547652395,"authorities":["admin","user"],"jti":"3a69d494-d661-4076-8957-a7b82881df29","client_id":"ssoclient-1","local-user-name":"admin"}``

## implicit

简化模式. 在provider 认证之后就将access token 给浏览器, 重定向到client.


(认证服务器登录之后)向认证服务器发送 http://127.0.0.1:8888/oauth/authorize?response_type=token&client_id=ssoclient-1&redirect_uri=http://127.0.0.1:8080/login, 会的得到重定向的response, 重定向的url 里面就带有access token.

```http request
GET /oauth/authorize?response_type=token&client_id=ssoclient-1&redirect_uri=http%3A%2F%2F127.0.0.1%3A8080%2Flogin HTTP/1.1
Host: 127.0.0.1:8888
Connection: keep-alive
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36
Accept: */*
Accept-Encoding: gzip, deflate, br
Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
Cookie: JSESSIONID=3E46340450802EB77AE126A1DAD6A386

```

```http response
302
Cache-Control : no-store
X-Content-Type-Options : nosniff
X-XSS-Protection : 1; mode=block
X-Frame-Options : DENY
Location : http://127.0.0.1:8080/login#access_token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsInNjb3BlIjpbIm9wZW5pZCJdLCJleHAiOjE1NDc2NTk3NDcsImF1dGhvcml0aWVzIjpbImFkbWluIiwidXNlciJdLCJqdGkiOiI4ODU5N2E3YS05NzcwLTQyZGEtYTg4YS03MWM3ZTZjZTIwNTAiLCJjbGllbnRfaWQiOiJzc29jbGllbnQtMSIsImxvY2FsLXVzZXItbmFtZSI6ImFkbWluIn0.jadHcmJZ-dmKoJR7vedCGpYOSksjp3gfShJOeDOfVhtg2A4pZRxiZbLOU2KULSWdAySt82bgiGQCD1ooJH_1PyuaE5BmYO--v3v9MbVU5FIFZXsCzG2xkSl7WPclt6vRqaoDB8o5S9fEtg6SWM54X_FR7apRMZKfGfa_1D9copU&token_type=bearer&expires_in=43199&scope=openid&local-user-name=admin&jti=88597a7a-9770-42da-a88a-71c7e6ce2050

```

得到的access token 的内容是```{"alg":"RS256","typ":"JWT"}.{"user_name":"admin","scope":["openid"],"exp":1547659747,"authorities":["admin","user"],"jti":"88597a7a-9770-42da-a88a-71c7e6ce2050","client_id":"ssoclient-1","local-user-name":"admin"}```

注意到返回的login 后面是#, 这个是让跳转到对应页面上才能通过js 获得# 后面的内容(这个在AuthorizationEndpoint 中写死了不能改). 在spring mvc 中接收不到# 后面的内容.

这种模式的client 无法使用spring client oauth2 来处理, 因为EnableOAuth2Sso 默认使用 AuthorizationCodeResourceDetails (一个被@Primary 标注的bean), 无法注入ImplicitResourceDetails.

如果使用 @EnableOAuth2Client 则默认的OAuth2ClientAuthenticationProcessingFilter 的认证处理流程不符合隐式模式的流程(直接处理出错), 所以只能在js 中直接获取access token(从# 后面传递而来), 具体方式可以查看 cb-jwt-client-implicit. 