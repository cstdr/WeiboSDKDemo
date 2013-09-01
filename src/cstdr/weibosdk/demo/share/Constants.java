package cstdr.weibosdk.demo.share;

/**
 * 开放平台常量，注意修改相关字段
 * 
 * @author cstdingran@gmail.com
 * 
 */
public interface Constants {

	// 新浪微博======================================================

	/** 申请的appkey,修改成你自己的才能使用 **/
	String SINA_APP_KEY = "230409856";

	/** 申请的App Secret,修改成你自己的才能使用 **/
	String SINA_APP_SECRET = "c4bb2921f622f256d3e2766c8aeb72a1";

	// 回调地址，移动APP使用官方默认地址
	String SINA_REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";

	// 新支持scope 支持传入多个scope权限，用逗号分隔，暂时用不上
	String SINA_SCOPE = "email,direct_messages_read,direct_messages_write,"
			+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read," + "follow_app_official_microblog";

	/** 认证Code **/
	String SINA_CODE = "code";

	String SINA_ACCESS_TOKEN = "access_token";

	String SINA_EXPIRES_IN = "expires_in";

	String SINA_UID = "uid";

	String SINA_USER_NAME = "userName";

	String SINA_NAME = "name";

	String SINA_REMIND_IN = "remind_in";

	String SINA_DATE_PATTERN = "yyyy/MM/dd HH:mm:ss";

	String SINA_CLIENT_ID = "client_id";

	String SINA_CLIENT_SECRET = "client_secret";

	String SINA_GRANT_TYPE = "grant_type";

	String SINA_GRANT_TYPE_VALUE = "authorization_code";

	String SINA_REDIRECT_URI = "redirect_uri";

	// 新浪微博首选项

	String PREF_SINA_ACCESS_TOKEN = "SINA_ACCESS_TOKEN";

	String PREF_SINA_EXPIRES_TIME = "SINA_EXPIRES_TIME";

	String PREF_SINA_UID = "SINA_UID";

	String PREF_SINA_USER_NAME = "SINA_USER_NAME";

	String PREF_SINA_REMIND_IN = "SINA_REMIND_IN";

	// 腾讯微博======================================================

	// 1. 修改配置文件，目标文件config.properties在Android_SDK.jar包config文件夹下(我已经改名为
	// TencentWeiboSDK.jar)，把appkey、appsecret修改成自己应用对应的appkey和appsecret.

	/** 腾讯微博的Appkey请参考文档修改 **/
	String TX_APP_KEY = "APP_KEY";

	String TX_APP_KEY_SEC = "APP_KEY_SEC";

	// 腾讯微博首选项

	String PREF_TX_ACCESS_TOKEN = "TX_ACCESS_TOKEN";

	String PREF_TX_EXPIRES_IN = "TX_EXPIRES_IN";

	String PREF_TX_OPEN_ID = "TX_OPEN_ID";

	String PREF_TX_OPEN_KEY = "TX_OPEN_KEY";

	String PREF_TX_REFRESH_TOKEN = "TX_REFRESH_TOKEN";

	String PREF_TX_NAME = "TX_NAME";

	String PREF_TX_CLIENT_ID = "TX_CLIENT_ID";

	String PREF_TX_EXPIRES_TIME = "TX_EXPIRES_TIME";

	String PREF_TX_CLIENT_IP = "TX_CLIENT_IP";

	String PREF_TX_UID = "TX_UID";

	// TX API请求字段

	String TX_API_APP_KEY = "oauth_consumer_key";

	String TX_API_ACCESS_TOKEN = "access_token";

	String TX_API_OPEN_ID = "openid";

	String TX_API_CLIENT_IP = "clientip";

	String TX_API_OAUTH_VERSION = "oauth_version";

	String TX_API_SCOPE = "scope";

	String TX_API_CONTENT = "content";

	String TX_API_FORMAT = "format";

	String TX_API_LONGITUDE = "longitude";

	String TX_API_LATITUDE = "latitude";

	String TX_API_SYNCFLAG = "syncflag";

	String TX_API_COMPATIBLEFLAG = "compatibleflag";
}
