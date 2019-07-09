一个sso 站点的客户端, 采用常用的code 方式.

访问主页可以获得用户的相关信息, 其中就有UserModel 的内容, 这个是从服务端传递过来的.

### 流程

0. 用户使用浏览器访问client server ( http://127.0.0.1:8080/)
    
    ```http request
    GET / HTTP/1.1
    Host: 127.0.0.1:8080
    Connection: keep-alive
    Upgrade-Insecure-Requests: 1
    User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.67 Safari/537.36
    Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8
    Accept-Encoding: gzip, deflate, br
    Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
    ```

0. client server 发现没有认证, 重定向到 http://127.0.0.1:8080/login.

   这一步会在cookie 中设置session, session 的名称在client 中指定为OAUTH2_CLIENT_SESION. (只要client 和server 的session id 名不一样就好, 具体原因在下面会讲到)
   
   ```http request
    GET / HTTP/1.1
    Host: 127.0.0.1:8080
    Connection: keep-alive
    Upgrade-Insecure-Requests: 1
    User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.67 Safari/537.36
    Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8
    Accept-Encoding: gzip, deflate, br
    Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
    ```
   
0. /login 根据策略重定向到authorization server (http://127.0.0.1:8888)
   
   OAUTH2SESSION 的cookie 也会带上.
   
   ```http request
   GET /oauth/authorize?client_id=ssoclient-1&redirect_uri=http://127.0.0.1:8080/login&response_type=code&state=27XURx HTTP/1.1
   Host: 127.0.0.1:8888
   Connection: keep-alive
   Upgrade-Insecure-Requests: 1
   User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.67 Safari/537.36
   Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8
   Accept-Encoding: gzip, deflate, br
   Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
   Cookie: OAUTH2_CLIENT_SESION=6BBBB2BDBD374992CA581425AA04A591
   ```
   
0. authorization server (provider) 看这个用户有没有认证, 没有认证则重定向到 http://127.0.0.1:8888/login.
    
    ```http request
    GET /login HTTP/1.1
    Host: 127.0.0.1:8888
    Connection: keep-alive
    Upgrade-Insecure-Requests: 1
    User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.67 Safari/537.36
    Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8
    Accept-Encoding: gzip, deflate, br
    Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
    Cookie: OAUTH2_CLIENT_SESION=6BBBB2BDBD374992CA581425AA04A591; JSESSIONID=856F4B3DA1894D6133CC609B4956B48C
    ```

   这一步authorization server 会为这个请求加上JSESSIONID, 在服务器中识别这个SESSION. 所以client 和server中表示sessionid 的cookie 键值不能一样, 否则就会互相覆盖, 导致在调用client 的回调URL 时候找不到这个session 中保存的state 信息.

0. 用户输入用户名/ 密码, 点击确认.

    ```http request
    POST /login HTTP/1.1
    Host: 127.0.0.1:8888
    Connection: keep-alive
    Content-Length: 43
    Cache-Control: max-age=0
    Origin: http://127.0.0.1:8888
    Upgrade-Insecure-Requests: 1
    Content-Type: application/x-www-form-urlencoded
    User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.67 Safari/537.36
    Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8
    Referer: http://127.0.0.1:8888/login
    Accept-Encoding: gzip, deflate, br
    Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
    Cookie: OAUTH2_CLIENT_SESION=6BBBB2BDBD374992CA581425AA04A591; JSESSIONID=856F4B3DA1894D6133CC609B4956B48C
    
    username=admin&password=123456&submit=Login
    ```

0. authorization server 认证通过后, 重定向到之前的oauth2 认证页面.

    ```http request
    GET /oauth/authorize?client_id=ssoclient-1&redirect_uri=http://127.0.0.1:8080/login&response_type=code&state=27XURx HTTP/1.1
    Host: 127.0.0.1:8888
    Connection: keep-alive
    Cache-Control: max-age=0
    Upgrade-Insecure-Requests: 1
    User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.67 Safari/537.36
    Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8
    Referer: http://127.0.0.1:8888/login
    Accept-Encoding: gzip, deflate, br
    Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
    Cookie: OAUTH2_CLIENT_SESION=6BBBB2BDBD374992CA581425AA04A591; JSESSIONID=B394ECC4EFAF3682945B81F26B1AC7A9
    ```

0. 这个链接会将浏览器重定向到client server (http://127.0.0.1:8080) 的回调URL.

    ```http request
    GET /login?code=MG965u&state=27XURx HTTP/1.1
    Host: 127.0.0.1:8080
    Connection: keep-alive
    Cache-Control: max-age=0
    Upgrade-Insecure-Requests: 1
    User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.67 Safari/537.36
    Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8
    Referer: http://127.0.0.1:8888/login
    Accept-Encoding: gzip, deflate, br
    Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
    Cookie: OAUTH2_CLIENT_SESION=6BBBB2BDBD374992CA581425AA04A591; JSESSIONID=B394ECC4EFAF3682945B81F26B1AC7A9
    ```

0. client server 根据传入的code, 从authorization server (http://127.0.0.1:8888) 获得access_token 等信息.
    
    ```http request
    POST /oauth/token HTTP/1.1
    Authorization: Basic c3NvY2xpZW50LTE6c3Nvc2VjcmV0
    Accept: application/json, application/x-www-form-urlencoded
    Content-Type: application/x-www-form-urlencoded;charset=UTF-8
    Cache-Control: no-cache
    Pragma: no-cache
    User-Agent: Java/11.0.1
    Host: 127.0.0.1:8888
    Connection: keep-alive
    Content-Length: 92

    code=MG965u
    ```
        
    Authorization 中的base64 内容是 ```ssoclient-1:ssosecret```, 用于client server 的认证. 如果密码不正确就会得到302 的结果.
    
    返回的内容是一个json. (这个是后来整理的, 所以数据内容与下面的不一致)
    
    ```json
    {
       "access_token":"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NDY1MjIwNDIsInVzZXJfbmFtZSI6ImFkbWluIiwiYXV0aG9yaXRpZXMiOlsiYWRtaW4iLCJ1c2VyIl0sImp0aSI6IjMwYmZjMmNlLWQxZTAtNGNmNi1hYjM2LThhYTQyMWFmODIxMyIsImNsaWVudF9pZCI6InNzb2NsaWVudC0xIiwic2NvcGUiOlsib3BlbmlkIl19.qH-Y8pvGMHdQ4Zl_XfUnhfYu9Xktjrj17OgTyFFOFsElhaMaodfLvsyU9xbMq5hB2qonYCBWkgmbA5GS3rBQk1Aw6stflf1hvwtgpbuPHKBuPeTPUnjP7-BMZewJlcRJajAs6FWgWN04vzf1O86rD10gxiNZp4D_2dmanFoiw-OvMk9PfMkZPGZ75Mj4Pz4JrP2AEduY4xpwgbpwOq8r4Cy8QEbdqveiU2j6WulX3KBZDejWZYtNcyDmu2SYVY77jAXwTJmC27eKJwf1gZEqfB0z51_gebcPDc-MkJQ6leyTs6GRVeAD6xWiY5U6K2g_Lh3suQtnG-XjmvshdfbArA",
       "token_type":"bearer",
       "refresh_token":"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsInNjb3BlIjpbIm9wZW5pZCJdLCJhdGkiOiIzMGJmYzJjZS1kMWUwLTRjZjYtYWIzNi04YWE0MjFhZjgyMTMiLCJleHAiOjE1NDkwNzA4NDIsImF1dGhvcml0aWVzIjpbImFkbWluIiwidXNlciJdLCJqdGkiOiI4MWU5ZjExNy0zMTg4LTQ1ZDQtYTA4Zi1iNzI5MTYwOTBhNmQiLCJjbGllbnRfaWQiOiJzc29jbGllbnQtMSJ9.BGJ9CZUx0fap_WylCi43LgxsxcoYiOvjkI5ODb8FIgUCEy1E45VWv4sS0RSd6g06KF5aalYUtWKXR7Cpj5kTR7g4BIv5AMrHOD5qj-OylDgZr1fjKdWAj0JTKlhFbeQmzcB7u-qH2Ol8D6bdgBjWMPGv6fhI5YAteDMeY_jmKJoTkrBEnwqITeq9StJjX5By720hfaLoJ5i8z17t6CPXk-KWKsLjsn1v60QvkQkmyPLJEFBvWw8qySwqg8zxyxtHFdaa8mG3VbZ7bTgosde91Wo56_L03W-z1PxZoHE0W_cHinZQcuVTSYiXcmBNRQ0X20O5Wuew_Z98d13LNL4Low",
       "expires_in":43199,
       "scope":"openid",
       "jti":"30bfc2ce-d1e0-4cf6-ab36-8aa421af8213"
    }
    ```
   
 0. client 获得access token 后从resource server (http://127.0.0.1:8888) 获得用户的相关资源信息.
 
    ```http request
    GET /me HTTP/1.1
    Authorization: bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NDY0NTAzNDUsInVzZXJfbmFtZSI6ImFkbWluIiwiYXV0aG9yaXRpZXMiOlsiYWRtaW4iLCJ1c2VyIl0sImp0aSI6ImYzZGYzZjZlLWJiNjgtNDA1OS04NTkxLTJjMDNmODBhMDM0ZSIsImNsaWVudF9pZCI6InNzb2NsaWVudC0xIiwic2NvcGUiOlsib3BlbmlkIl19.W4IXbNON81bp76qHpj4JKYAz5pyLEf7nMkvZcJe7dUqdHVCMzfW0Du3fdIHBupSmpiaQbzyuvQHibX_40mM_-0cnGsCXR3fAmu2OzJzMmciuSWq74DgSPyDz8xKYksFmGUfocecA8aN9mm8-l9-eSwLUX10v794XPAM-FA_mzpDfpZjj2cRYKnOaaWLy_PbTp7WerhJ68sDxm9xl0mO0s7byBo-Cap0nwKFZoI_0qNNgPnMrGekwXOW1B1sEO6eU-OYVU17tZHiYUG9hnESWt04BA035vD56kXJfRu4IPc7l-RI1Ib7KIWIJwaMsc2CmD1h_4VMU56JeTZH5faRmyA
    Accept: application/json
    User-Agent: Java/11.0.1
    Host: 127.0.0.1:8888
    Connection: keep-alive
    ```
    
    Authorization 的认证信息是access token, 中jwt 的内容是```{"alg":"RS256","typ":"JWT"}.{"exp":1546450345,"user_name":"admin","authorities":["admin","user"],"jti":"f3df3f6e-bb68-4059-8591-2c03f80a034e","client_id":"ssoclient-1","scope":["openid"]}.签名``` 
 0. 认证完成. 之后给浏览器重定向到最开始访问的页面.
 
    ```http request
    GET / HTTP/1.1
    Host: 127.0.0.1:8080
    Connection: keep-alive
    Cache-Control: max-age=0
    Upgrade-Insecure-Requests: 1
    User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.67 Safari/537.36
    Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8
    Referer: http://127.0.0.1:8888/login
    Accept-Encoding: gzip, deflate, br
    Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
    Cookie: JSESSIONID=B394ECC4EFAF3682945B81F26B1AC7A9; OAUTH2_CLIENT_SESION=F6A55E6D7DBDF79496CFE876980D5D03
    ```
    得到的结果就是http://127.0.0.1:8080/me 的内容.
    
    ```json
    {
        "name":"admin",
        "password":"{bcrypt}$2a$10$B2qpTAJagc9YWGtuGoQuBuTdDgP4Q0zguJ.VEukpcfsqTOQhG/rUm",
        "mailAddress":"test@test.com",
        "portrait":null,
        "createTime":"2019-01-03T01:15:35.971+0000",
        "roleNames":["admin","user"],
        "state":"NORMAL",
        "githubId":null,
        "weiboId":null
    }
    ```

0. 在认证完成之后, client 的principal 中也会多出一个tokenValue 字段, 这个保存了access token 的jwt 的字符串值.