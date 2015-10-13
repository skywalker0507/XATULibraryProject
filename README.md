# XATULibraryProject
##项目说明
###（项目尚在开发中，以下是已完成部分）
+ 先用Firebug抓包,得到http请求参数用HttpClient模拟登陆,将用户名,密码和验证码发送
至服务器,利用Jsoup和正则表达式对获取的相应的html内容进行解析得到所需内容,客户
端所需的数据均是通过解析html获得
+  完成按关键字,书名,作者,ISBN号进行检索功能,通过解析JSON获取调取豆瓣API查询
图书的详细内容
+ 为改善程序运行效率,采用LruCache进行内存缓存,采用DiskLruCache对获取的图片进行
磁盘缓存
+ 实现扫描图书条码查询功能,采用zxing解码
+ 整个APP的UI基本遵循material design,整体结构为NavigationDrawer+viewpager ,使用
Android support design库使程序更符合Android设计标准
