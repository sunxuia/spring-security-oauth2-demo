一个本地账号绑定多个社交用户账号, 是c8 和 c5 的集合. 首先使用github 登录, 然后绑定微博.
我原先是想类似c8 一样通过在github 和微博 extractPrincipal 的时候检查是否登录用户, 从而进行绑定.
结果这个在一个登录之后登录另一个账号后会出现问题,
    发送给认证服务器的Authorization 信息是错的 ( 因为之前登录过了, access_token 还存在, 会跳过认证 (org.springframework.security.oauth2.client.OAuth2RestTemplate.getAccessToken) , 直接使用这个token 向另一个不同的服务器直接请求用户数据 ) , 结果肯定是认证失败, 需要首先logout 然后再login 才行.
    登录失败后org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter.unsuccessfulAuthentication 会执行SecurityContextHolder.clearContext(); 清空认证信息, 需要重写这段内容.
以上只针对使用github 登录后再使用weibo 登录的情况修改了代码, 所以要首先登录github, 然后再点登录微博.
流程是 : 1. 登录github, SecurityContextHolder.getContext() 得到的结果是关于github 的OAuth2 的认证信息, principal 是UserModel, 此时关于社交绑定的store 中会有1 条github 的信息,
        2. 之后登录weibo, SecurityContextHolder.getContext() 得到的结果就变成了微博的OAuth2 的认证信息, principal 还是原来的信息, 此时关于社交绑定的store 中会有1 条github 1 条weibo 的信息.

写起来有些麻烦, 也无法实现其他的分享相关的功能, 最好还是看下用spring social 来实现这个功能.