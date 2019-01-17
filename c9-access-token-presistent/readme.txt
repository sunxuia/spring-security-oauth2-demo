保存access_token的示例
这样就不用每次应用重启之后, 都要用户去重新申请授权码了, spring security 有一个jdbc 的实现版本可以参考一下.
这个里面的示例写的比较简单, 持久化到一个文件 d:\temp.txt 中去了.

1. client 模式
程序会进行如下判断, 判断是否是client 模式, 如果满足才会持久化(从repo 中获取/ 保存).
(因为这里用的都是code 模式, 所以示例并不会被执行, 可以在调试的时候修改一些值让它运行).
		if (clientTokenServices != null
				&& (resource.isClientOnly() || auth != null && auth.isAuthenticated())) {

		}
2. code 模式
code 模式不需要保存access token
