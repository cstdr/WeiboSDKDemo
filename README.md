## 新浪微博、腾讯微博开放平台DEMO（WeiboSDKDemo）

* 最近学习开放平台，官方文档和Demo有点坑爹，经过几天的努力，写了一个DEMO，整合了新浪微博和腾讯微博，均能够SSO授权、网页授权和发微博，如果需要可以去看看源码，里面有注释说明
* 代码使用自己感觉舒服的方式进行封装和整合，使用前需要修改的地方在下面有详细说明，如果有问题请随时联系我，或者咱们一起完善！～
* 代码并没有整合所有API方法（其实我觉得这也没必要），当你需要哪个API接口，可以马上去官网查看API文档（文档也会变的），然后添加上去就可以啦～

* 官方新浪微博SDK地址：https://github.com/mobileresearch/weibo_android_sdk 
* 新浪API文档：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI

* 官方腾讯微博SDK地址：http://wiki.open.t.qq.com/index.php/%E7%A7%BB%E5%8A%A8%E5%BA%94%E7%94%A8%E6%8E%A5%E5%85%A5
* 腾讯API文档：http://wiki.open.t.qq.com/index.php/API%E6%96%87%E6%A1%A3

*** 

## WeiboSDKDemo使用方法

* 请先熟悉官方文档，虽然其中有一些问题:)
* 根据文档申请开放平台帐号，特别注意回调地址，官网和代码中要保持一致，这个经常出现错误；
* 运行Demo前，先确定下载完Demo的所有文件，有关jar包的问题可以参考官方文档，都是必须的依赖包；
* 然后修改开放平台的AppKey和AppSecret：

1. 新浪微博需要在Constants类中把appkey、appsecret修改成自己应用对应的appkey和appsecret，回调地址不需要修改；
2. 腾讯微博需要修改配置文件，目标文件config.properties在Android_SDK.jar包config文件夹下(我已经改名为TencentWeiboSDK.jar)，把appkey、appsecret修改成自己应用对应的appkey和appsecret，这里再次提醒注意回调地址要和网上你填写的一致，别管文档怎么说.

* 修改完后，clean工程，运行测试吧！
*

下面是我自己的一点总结，如果你有好的建议，请分享下吧～

***

### 新浪微博平台:

* SSO一直没有测试成功，运行官方DEMO也如此，没有找到解决方法，已经确认是新浪的问题，没有开放大量注册，官方建议先使用老版本SDK ；今天发现新浪更新了文档和jar包，SSO可以正常使用，返回Token、uid、userName等信息； 
* 网页授权得到的是授权Code，不是Token，所以还需要根据Code去获取Token，调用自定义的getTokenByCode 方法；最新jar包可以直接获取到Token； 
* 目前网页授权、微博分享都操作已经整合到工程中，测试正常； 
* 新浪另一个问题，token过期后需要用户登陆重新授权。

### 新浪SSO授权返回值：（所需全有，不用再获取）

1. key = uid value = 1111111111 ； 
1. key = userName value = cstdr ；
1. key = expires_in value = 157186399 ；
1. key = remind_in value = 157186399 ；
1. key = access_token value = 2.00KNDUIDyonFnC99e5598a960gYir2 。

### 新浪网页授权返回值：（缺少UserName，所以授权后再去获取了一次）

1. key = uid value = 1111111111 ；
1. key = expires_in value = 157185966 ；
1. key = remind_in value = 157185966 ；
1. key = access_token value = 2.00KNDUIDyonFnC99e5598a960gYir2 。

*** 

### 腾讯微博平台：

* SSO可以正常获取； 
* 因为获取授权时Refreshtoken 总为null，所以不能使用官方SDK自带方法，于是把新浪微博的方法修改，使之适用于腾讯微博； 
* 回调地址文档说可以为空，但是实际上为空不能网页授权，必须与网站上填写的一致，否则分享微博的时候总是说回调地址错误，所以重新修改了网页授权的Activity； 
* 网页授权的Activity不仅仅是回调地址为空问题，还因为没有回调方法，不能得到授权正确与否的消息，也就不能修改UI和数据，所以修改后添加了回调方法。

### 腾讯SSO授权返回值：（返回WeiboToken ，但是缺少UserName，还需要再获取一次）

1. token.accessToken = 8e509ffc2cbad04e1678d9e2b1822970 ；
1. token.expiresIn = 604800 ；
1. token.omasKey = 9a23c968893edb64fc35fa33f7b44c05 ；
1. token.omasToken = 010000008790cc668e6ed810b2cffc374f9a3dd9ed126be4a279eff1c0c1a6852ea048019c4c6746cb40ce72 ；
1. token.openID = 5789a9d103704e1f63102b1dba348690 ；（即uid）
1. token.refreshToken =  。（总为空）

### 腾讯网页授权返回值：（所需全有，不用再获取）

1. accessToken = 8e509ffc2cbad04e1678d9e2b1822970 ；
1. expiresIn = 8035200 ；
1. omasKey = 090FF697B85D63703AAA4AB6642650EB ；
1. openID = 5789A9D103704E1F63102B1DBA348690 ；
1. refreshToken = 359107cbc9f7a43b69ccaaafadf14ff1 ；
1. state = 111 ；
1. name = name ；
1. nick = nick 。

*** 

## 联系或反馈

如果你有任何问题、建议或者想交个朋友，请联系我吧～

我的邮箱: cstdingran(at)gmail.com

我的QQ: 280114396

我的新浪微博：[http://weibo.com/cstdr][WEIBO]




[WEIBO]: http://weibo.com/cstdr "新浪微博"
