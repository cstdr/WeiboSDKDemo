package cstdr.weibosdk.demo.share;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.net.RequestListener;
import com.weibo.sdk.android.sso.SsoHandler;

import cstdr.weibosdk.demo.MainActivity.WeiboListener;
import cstdr.weibosdk.demo.util.LOG;
import cstdr.weibosdk.demo.util.PreferenceUtil;
import cstdr.weibosdk.demo.util.Util;

/**
 * 新浪微博工具类
 * 
 * @author cstdingran@gmail.com
 * 
 */
public class SinaWeiboUtil {

	private static final String TAG = "SinaWeiboUtil";

	private static Context mContext;

	private static SinaWeiboUtil mInstantce;

	private static Weibo mWeibo;

	/** 保存token等参数 **/
	private static Oauth2AccessToken mAccessToken;

	/** 调用SSO授权 **/
	private static SsoHandler mSsoHandler;

	private WeiboListener listener;

	public SinaWeiboUtil() {
		mWeibo = Weibo.getInstance(Constants.SINA_APP_KEY,
				Constants.SINA_REDIRECT_URL);
	}

	public static SinaWeiboUtil getInstance(Context context) {
		mContext = context;
		if (mInstantce == null) {
			mInstantce = new SinaWeiboUtil();
		}
		return mInstantce;
	}

	/**
	 * 初始化新浪微博
	 * 
	 * @param l
	 *            授权是否过期回调函数
	 */
	public void initSinaWeibo(WeiboListener l) {
		String token = PreferenceUtil.getInstance(mContext).getString(
				Constants.PREF_SINA_ACCESS_TOKEN, "");
		long expiresTime = PreferenceUtil.getInstance(mContext).getLong(
				Constants.PREF_SINA_EXPIRES_TIME, 0);
		String uid = PreferenceUtil.getInstance(mContext).getString(
				Constants.PREF_SINA_UID, "");
		String userName = PreferenceUtil.getInstance(mContext).getString(
				Constants.PREF_SINA_USER_NAME, "");
		String remindIn = PreferenceUtil.getInstance(mContext).getString(
				Constants.PREF_SINA_REMIND_IN, "");
		mAccessToken = new Oauth2AccessToken();
		mAccessToken.setToken(token);
		mAccessToken.setExpiresTime(expiresTime);
		LOG.cstdr(TAG, "accessToken = " + mAccessToken);
		LOG.cstdr(TAG, "accessToken.getToken() = " + mAccessToken.getToken());
		LOG.cstdr(
				TAG,
				"accessToken.getExpiresTime() = "
						+ mAccessToken.getExpiresTime());
		LOG.cstdr(TAG, "uid = " + uid);
		LOG.cstdr(TAG, "userName = " + userName);
		LOG.cstdr(TAG, "remindIn = " + remindIn);

		if (mAccessToken.isSessionValid()) { // TODO 判断是否已授权
			String date = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss")
					.format(new java.util.Date(mAccessToken.getExpiresTime()));
			LOG.cstdr(TAG, "access_token 仍在有效期内,无需再次登录: \naccess_token:"
					+ mAccessToken.getToken() + "\n有效期：" + date + "\nuid:"
					+ uid + "\nuserName:" + userName + "\nremindIn:" + remindIn);
			l.init(true);
		} else {
			LOG.cstdr(TAG, "使用SSO登录前，请检查手机上是否已经安装新浪微博客户端，"
					+ "目前仅3.0.0及以上微博客户端版本支持SSO；如果未安装，将自动转为Oauth2.0进行认证");
			l.init(false);
		}
	}

	/**
	 * SSO授权
	 * 
	 * @param l
	 */
	public void auth(WeiboListener l) {
		// SSO授权
		mSsoHandler = new SsoHandler((Activity) mContext, mWeibo);
		mSsoHandler.authorize(new AuthDialogListener());

		listener = l;

		// test 网页授权
		// mWeibo.authorize(mContext, new AuthDialogListener());
	}

	/**
	 * 授权回调函数
	 * 
	 * @author ran.ding@downjoy.com
	 */
	class AuthDialogListener implements WeiboAuthListener {

		@Override
		public void onCancel() {
			LOG.cstdr(TAG,
					"===================AuthDialogListener=Auth cancel==========");
			Util.showToast(mContext, "取消授权操作。");
		}

		@Override
		public void onComplete(Bundle values) {
			LOG.cstdr(TAG,
					"===================AuthDialogListener=onComplete==========");
			for (String key : values.keySet()) {
				LOG.cstdr(
						TAG,
						"values:key = " + key + " value = "
								+ values.getString(key));
			}
			String token = values.getString(Constants.SINA_ACCESS_TOKEN);
			String uid = values.getString(Constants.SINA_UID);
			String userName = values.getString(Constants.SINA_USER_NAME);
			String expiresIn = values.getString(Constants.SINA_EXPIRES_IN); // expiresIn
																			// 是授权时长，因为要初始化，所以为String类型
			String remindIn = values.getString(Constants.SINA_REMIND_IN);

			mAccessToken = new Oauth2AccessToken(token, expiresIn);
			if (mAccessToken.isSessionValid()) {
				PreferenceUtil.getInstance(mContext).saveString(
						Constants.PREF_SINA_ACCESS_TOKEN, token);
				PreferenceUtil.getInstance(mContext).saveString(
						Constants.PREF_SINA_UID, uid);
				PreferenceUtil.getInstance(mContext).saveLong(
						Constants.PREF_SINA_EXPIRES_TIME,
						mAccessToken.getExpiresTime()); // 存入的是到期时间
				PreferenceUtil.getInstance(mContext).saveString(
						Constants.PREF_SINA_REMIND_IN, remindIn);
				if (TextUtils.isEmpty(userName)) {
					show(Long.parseLong(uid));
				} else {
					PreferenceUtil.getInstance(mContext).saveString(
							Constants.PREF_SINA_USER_NAME, userName);
					if (listener != null) {
						listener.onResult();
					}
				}
				LOG.cstdr(TAG, "isSessionValid~~~~~~~token = " + token
						+ " uid = " + uid + " userName = " + userName
						+ " expiresIn = " + expiresIn + " remindIn = "
						+ remindIn);

			}
		}

		@Override
		public void onError(WeiboDialogError e) {
			LOG.cstdr(TAG,
					"===================AuthDialogListener=onError=WeiboDialogError = "
							+ e.getMessage());
			Util.showToast(mContext, "分享失败，请检查网络连接。出错信息：" + e.getMessage());
		}

		@Override
		public void onWeiboException(WeiboException e) {
			LOG.cstdr(TAG,
					"===================AuthDialogListener=onWeiboException=WeiboException = "
							+ e.getMessage());
			Util.showToast(mContext, "分享失败，请检查网络连接。出错信息：" + e.getMessage());
		}
	}

	/**
	 * SSO授权回调函数
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void authCallBack(int requestCode, int resultCode, Intent data) {
		if (mSsoHandler != null) {
			LOG.cstdr(TAG, "=====onActivityResult=mSsoHandler resultCode = "
					+ resultCode + " requestCode = " + requestCode);
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	/**
	 * 网页授权时，需要单独获取UserName
	 * 
	 * @param uid
	 */
	public void show(long uid) {
		SinaWeiboAPI api = new SinaWeiboAPI(mAccessToken);
		api.show(uid, new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				LOG.cstdr(TAG, "onIOException---e = " + e.getMessage());
				Util.showToast(mContext, "分享失败，请检查网络连接。出错信息：" + e.getMessage());
			}

			@Override
			public void onError(WeiboException e) {
				LOG.cstdr(TAG, "WeiboException---e = " + e.getMessage());
				Util.showToast(mContext, "分享失败，请检查网络连接。出错信息：" + e.getMessage());
			}

			@Override
			public void onComplete(String json) {
				JSONObject object;
				try {
					object = new JSONObject(json);
					String userName = object.optString(Constants.SINA_NAME);
					LOG.cstdr(TAG, "show---onComplete---userName = " + userName);
					PreferenceUtil.getInstance(mContext).saveString(
							Constants.PREF_SINA_USER_NAME, userName);
					if (listener != null) {
						listener.onResult();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
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
	public void update(String content, String lat, String lon) {
		SinaWeiboAPI api = new SinaWeiboAPI(mAccessToken);
		api.update(content, lat, lon, new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				LOG.cstdr(TAG, "onIOException---e = " + e.getMessage());
				Util.showToast(mContext, "分享失败，请检查网络连接。出错信息：" + e.getMessage());
			}

			@Override
			public void onError(WeiboException e) {
				LOG.cstdr(TAG, "onError---e = " + e.getMessage()
						+ " e.getStatusCode() = " + e.getStatusCode());
				if (e.getStatusCode() == 400) { // 相同内容短时间内不能分享
					Util.showToast(mContext, "分享失败，相同内容短时间内不能分享，请稍候再试吧。出错信息："
							+ e.getMessage());
				} else {
					Util.showToast(mContext,
							"分享失败，请检查网络连接。出错信息：" + e.getMessage());
				}
			}

			@Override
			public void onComplete(String str) {
				LOG.cstdr(TAG, "onComplete---str = " + str);
				Util.showToast(mContext, "分享成功，去你绑定的新浪微博看看吧！");
			}
		});
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
	 */
	public void upload(String content, String file, String lat, String lon) {
		SinaWeiboAPI api = new SinaWeiboAPI(mAccessToken);
		api.upload(content, file, lat, lon, new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				LOG.cstdr(TAG, "onIOException---e = " + e.getMessage());
				Util.showToast(mContext, "分享失败，请检查网络连接。出错信息：" + e.getMessage());
			}

			@Override
			public void onError(WeiboException e) {
				LOG.cstdr(TAG, "onError---e = " + e.getMessage()
						+ " e.getStatusCode() = " + e.getStatusCode());
				if (e.getStatusCode() == 400) { // 相同内容短时间内不能分享，判断还以促出错信息为准
					Util.showToast(mContext, "分享失败，相同内容短时间内不能分享，请稍候再试吧。出错信息："
							+ e.getMessage());
				} else {
					Util.showToast(mContext,
							"分享失败，请检查网络连接。出错信息：" + e.getMessage());
				}
			}

			@Override
			public void onComplete(String str) {
				LOG.cstdr(TAG, "onComplete---str = " + str);
				Util.showToast(mContext, "分享成功，去你绑定的新浪微博看看吧！");
			}
		});
	}

	/**
	 * 注销授权
	 * 
	 * @param l
	 */
	public void logout(WeiboListener l) {
		PreferenceUtil.getInstance(mContext).remove(
				Constants.PREF_SINA_ACCESS_TOKEN);
		l.onResult();
	}

	/**
	 * 检查是否已授权
	 * 
	 * @return true 已授权，false 未授权
	 */
	public boolean isAuth() {
		String token = PreferenceUtil.getInstance(mContext).getString(
				Constants.PREF_SINA_ACCESS_TOKEN, "");
		if (TextUtils.isEmpty(token)) {
			return false;
		}
		return true;
	}

}
