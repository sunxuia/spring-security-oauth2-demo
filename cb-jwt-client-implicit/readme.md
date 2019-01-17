# 使用implicit 模式的授权

针对cb-jwt-client-jwt-authorize 授权码模式仍然需要session 的情况进行了改进, 在客户端(js) 中获得access token 即可.

这个客户端其实已经和spring security oauth2 没有关系了, 仅仅是spring security + jwt 认证. 相比cb-jwt-client-jwt-authorize 主要的更改在js 客户端和去掉了客户端的session.