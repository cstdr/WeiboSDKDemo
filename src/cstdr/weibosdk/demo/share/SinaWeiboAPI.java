package cstdr.weibosdk.demo.share;

import android.text.TextUtils;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboParameters;
import com.weibo.sdk.android.net.AsyncWeiboRunner;
import com.weibo.sdk.android.net.RequestListener;

/**
 * 新浪微博API，可以根据自己的需要添加API
 * 
 * @author cstdingran@gmail.com
 * 
 */
public class SinaWeiboAPI {

	/**
	 * 访问微博服务接口的地址
	 */
	public static final String API_SERVER = "https://api.weibo.com/2";

	private static final String URL_USERS = API_SERVER + "/users";

	private static final String URL_STATUSES = API_SERVER + "/statuses";

	private static final String URL_ACCOUNT = API_SERVER + "/account";

	/**
	 * post请求方式
	 */
	public static final String HTTPMETHOD_POST = "POST";

	/**
	 * get请求方式
	 */
	public static final String HTTPMETHOD_GET = "GET";

	private Oauth2AccessToken oAuth2accessToken;

	private String accessToken;

	/**
	 * 构造函数，使用各个API接口提供的服务前必须先获取Oauth2AccessToken
	 * 
	 * @param accesssToken
	 *            Oauth2AccessToken
	 */
	public SinaWeiboAPI(Oauth2AccessToken oauth2AccessToken) {
		this.oAuth2accessToken = oauth2AccessToken;
		if (oAuth2accessToken != null) {
			accessToken = oAuth2accessToken.getToken();
		}
	}

	/**
	 * 执行请求
	 * 
	 * @param url
	 * @param params
	 * @param httpMethod
	 * @param listener
	 */
	private void request(final String url, final WeiboParameters params, final String httpMethod,
			RequestListener listener) {
		params.add("access_token", accessToken);
		AsyncWeiboRunner.request(url, params, httpMethod, listener);
	}

	/**
	 * 根据用户ID获取用户信息
	 * 
	 * @param uid
	 *            需要查询的用户ID。
	 * @param listener
	 */
	public void show(long uid, RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		params.add("uid", uid);
		request(URL_USERS + "/show.json", params, HTTPMETHOD_GET, listener);
	}

	/**
	 * 发布一条新微博(连续两次发布的微博不可以重复)
	 * 
	 * @param content
	 *            要发布的微博文本内容，内容不超过140个汉字。
	 * @param lat
	 *            纬度，有效范围：-90.0到+90.0，+表示北纬，默认为0.0。
	 * @param lon
	 *            经度，有效范围：-180.0到+180.0，+表示东经，默认为0.0。
	 * @param listener
	 */
	public void update(String content, String lat, String lon, RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		params.add("status", content);
		if (!TextUtils.isEmpty(lon)) {
			params.add("long", lon);
		}
		if (!TextUtils.isEmpty(lat)) {
			params.add("lat", lat);
		}
		request(URL_STATUSES + "/update.json", params, HTTPMETHOD_POST, listener);
	}

	/**
	 * 上传图片并发布一条新微博，此方法会处理urlencode
	 * 
	 * @param content
	 *            要发布的微博文本内容，内容不超过140个汉字
	 * @param file
	 *            要上传的图片，仅支持JPEG、GIF、PNG格式，图片大小小于5M。
	 * @param lat
	 *            纬度，有效范围：-90.0到+90.0，+表示北纬，默认为0.0。
	 * @param lon
	 *            经度，有效范围：-180.0到+180.0，+表示东经，默认为0.0。
	 * @param listener
	 */
	public void upload(String content, String file, String lat, String lon, RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		params.add("status", content);
		params.add("pic", file);
		if (!TextUtils.isEmpty(lon)) {
			params.add("long", lon);
		}
		if (!TextUtils.isEmpty(lat)) {
			params.add("lat", lat);
		}
		request(URL_STATUSES + "/upload.json", params, HTTPMETHOD_POST, listener);
	}

	/**
	 * 退出登录
	 * 
	 * @param listener
	 */
	public void endSession(RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		request(URL_ACCOUNT + "/end_session.json", params, HTTPMETHOD_POST, listener);
	}
}
