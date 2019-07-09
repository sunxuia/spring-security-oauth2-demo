# 使用implicit 模式的授权

针对cb-jwt-client-jwt-authorize 授权码模式仍然需要session 的情况进行了改进, 在客户端(js) 中获得access token 即可.

浏览器自己访问认证服务器, 认证服务器返回1个带有access token 的重定向到8080 指定页的响应, 这样浏览器就获得了token, 之后浏览器可以将这个token 发送给8080, 作为自己的身份认证.

这个客户端其实已经和spring security oauth2 没有关系了, 仅仅是spring security + jwt 认证. 相比cb-jwt-client-jwt-authorize 主要的更改在js 客户端和去掉了客户端的session.

现在已经不推荐使用这个模式了, 在使用implicit 模式的时候会将token 传递给客户端, 造成token 泄露. (https://medium.com/oauth-2/why-you-should-stop-using-the-oauth-implicit-grant-2436ced1c926)