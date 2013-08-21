package cstdr.weibosdk.demo.share;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.weibo.sdk.android.component.sso.AuthHelper;
import com.tencent.weibo.sdk.android.component.sso.OnAuthListener;
import com.tencent.weibo.sdk.android.component.sso.WeiboToken;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.net.RequestListener;

import cstdr.weibosdk.demo.MainActivity.WeiboListener;
import cstdr.weibosdk.demo.util.LOG;
import cstdr.weibosdk.demo.util.PreferenceUtil;
import cstdr.weibosdk.demo.util.Util;

/**
 * 腾讯微博工具类
 * 
 * @author cstdingran@gmail.com
 * 
 */
public class TencentWeiboUtil {

	private static final String TAG = "TencentWeiboUtil";

	private static Context mContext;

	private static TencentWeiboUtil tencentWeiboUtil;

	/** 微博相关API **/
	private static TencentWeiboAPI weiboAPI;

	/** 保存token等参数 **/
	private static TencentTO tencentTO;

	private WeiboListener listener;

	public TencentWeiboUtil() {
		tencentTO = new TencentTO();
	}

	public static TencentWeiboUtil getInstance(Context context) {
		mContext = context;
		if (tencentWeiboUtil == null) {
			tencentWeiboUtil = new TencentWeiboUtil();
		}
		return tencentWeiboUtil;
	}

	/**
	 * 网页授权回调函数
	 */
	public void webAuthOnResult() {
		LOG.cstdr(TAG, "initTencentWeibo=======listener = " + listener);
		if (listener != null) {
			listener.onResult();
		}
	}

	/**
	 * 初始化腾讯微博
	 * 
	 * @param l
	 *            授权是否过期回调函数
	 */
	public void initTencentWeibo(WeiboListener l) {
		String accessToken = PreferenceUtil.getInstance(mContext).getString(Constants.PREF_TX_ACCESS_TOKEN, "");
		if (TextUtils.isEmpty(accessToken)) { // 未授权
			l.init(false);
		} else {
			long expiresTime = Long.parseLong(PreferenceUtil.getInstance(mContext).getString(
					Constants.PREF_TX_EXPIRES_TIME, ""));
			LOG.cstdr(TAG, "expiresTime = " + expiresTime);
			LOG.cstdr(TAG, "expiresTime - System.currentTimeMillis() = " + (expiresTime - System.currentTimeMillis()));
			if (expiresTime - System.currentTimeMillis() > 0) { // 已授权未过期
				String openId = PreferenceUtil.getInstance(mContext).getString(Constants.PREF_TX_OPEN_ID, "");
				String clientId = PreferenceUtil.getInstance(mContext).getString(Constants.PREF_TX_CLIENT_ID, "");
				String clientIp = PreferenceUtil.getInstance(mContext).getString(Constants.PREF_TX_CLIENT_IP, "");
				tencentTO.setAccessToken(accessToken);
				tencentTO.setOpenId(openId);
				tencentTO.setAppkey(clientId);
				tencentTO.setClientIp(clientIp);
				l.init(true);
			} else { // 已过期
				l.init(false);
			}
		}
	}

	/**
	 * SSO授权
	 * 
	 * @param appId
	 * @param appSecket
	 * @param l
	 */
	public void auth(final WeiboListener l) {
		long appId = Long.valueOf(com.tencent.weibo.sdk.android.api.util.Util.getConfig().getProperty(
				Constants.TX_APP_KEY));
		String appSecket = com.tencent.weibo.sdk.android.api.util.Util.getConfig()
				.getProperty(Constants.TX_APP_KEY_SEC);
		listener = l;
		LOG.cstdr(TAG, "appId = " + appId + " appSecket = " + appSecket);
		// test 网页授权
		// Intent intent=new Intent(mContext, TencentWebAuthActivity.class);
		// ((Activity)mContext).startActivityForResult(intent, 1);

		AuthHelper.register(mContext, appId, appSecket, new OnAuthListener() {

			@Override
			public void onWeiboVersionMisMatch() {
				Util.showToast(mContext, "腾讯微博版本不符合");
				Intent intent = new Intent(mContext, TencentWebAuthActivity.class);
				((Activity) mContext).startActivityForResult(intent, 1);
			}

			@Override
			public void onWeiBoNotInstalled() {
				Util.showToast(mContext, "腾讯微博客户端没有安装");
				Intent intent = new Intent(mContext, TencentWebAuthActivity.class);
				((Activity) mContext).startActivityForResult(intent, 1);
			}

			@Override
			public void onAuthPassed(String name, WeiboToken token) {
				StringBuffer sb = new StringBuffer();
				sb.append("token.accessToken = " + token.accessToken).append("\ntoken.expiresIn = " + token.expiresIn)
						.append("\ntoken.omasKey = " + token.omasKey).append("\ntoken.omasToken = " + token.omasToken)
						.append("\ntoken.openID = " + token.openID)
						.append("\ntoken.refreshToken = " + token.refreshToken);
				LOG.cstdr(TAG, "onAuthPassed---name = " + name + " token = " + token);
				LOG.cstdr(TAG, "onAuthPassed = " + sb.toString());
				String clientId = com.tencent.weibo.sdk.android.api.util.Util.getConfig().getProperty(
						Constants.TX_APP_KEY);
				String clientIp = getClientIp();
				PreferenceUtil.getInstance(mContext).saveString(Constants.PREF_TX_ACCESS_TOKEN, token.accessToken);
				PreferenceUtil.getInstance(mContext).saveString(Constants.PREF_TX_EXPIRES_IN,
						String.valueOf(token.expiresIn));
				PreferenceUtil.getInstance(mContext).saveString(Constants.PREF_TX_OPEN_ID, token.openID);
				PreferenceUtil.getInstance(mContext).saveString(Constants.PREF_TX_OPEN_KEY, token.omasKey);
				PreferenceUtil.getInstance(mContext).saveString(Constants.PREF_TX_REFRESH_TOKEN, token.refreshToken); // 总是为null
				PreferenceUtil.getInstance(mContext).saveString(Constants.PREF_TX_CLIENT_ID, clientId);
				PreferenceUtil.getInstance(mContext).saveString(Constants.PREF_TX_EXPIRES_TIME,
						String.valueOf(System.currentTimeMillis() + token.expiresIn * 1000));
				PreferenceUtil.getInstance(mContext).saveString(Constants.PREF_TX_CLIENT_IP, clientIp);

				tencentTO.setAccessToken(token.accessToken);
				tencentTO.setAppkey(clientId);
				tencentTO.setClientIp(clientIp);
				tencentTO.setOpenId(token.openID);

				LOG.cstdr(TAG, "clientIp = " + clientIp);
				getUserInfo(listener);
			}

			@Override
			public void onAuthFail(int result, String error) {
				LOG.cstdr(TAG, "onAuthFail---result = " + result + " error = " + error);
				Util.showToast(mContext, "授权失败。出错信息：" + error);
			}
		});
		AuthHelper.auth(mContext, "");
	}

	/**
	 * 获得客户端IP
	 * 
	 * @return
	 */
	public static String getClientIp() {
		try {
			for (Enumeration<NetworkInterface> mEnumeration = NetworkInterface.getNetworkInterfaces(); mEnumeration
					.hasMoreElements();) {
				NetworkInterface intf = mEnumeration.nextElement();
				for (Enumeration<InetAddress> enumIPAddr = intf.getInetAddresses(); enumIPAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIPAddr.nextElement();
					// 如果不是回环地址
					if (!inetAddress.isLoopbackAddress()) {
						// 直接返回本地IP地址
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("Error", ex.toString());
		}
		return null;
	}

	/**
	 * 获得用户信息
	 * 
	 * @param l
	 */
	private void getUserInfo(final WeiboListener l) {
		weiboAPI = new TencentWeiboAPI(tencentTO);
		weiboAPI.getUserInfo(new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				LOG.cstdr(TAG, "onIOException");
				Util.showToast(mContext, "获取用户信息失败。出错信息：" + e.getMessage());
			}

			@Override
			public void onError(WeiboException e) {
				LOG.cstdr(TAG, "onError = " + e.getMessage());
				Util.showToast(mContext, "获取用户信息失败。出错信息：" + e.getMessage());
			}

			@Override
			public void onComplete(String json) {
				LOG.cstdr(TAG, "onComplete---json = " + json);
				try {
					JSONObject object = new JSONObject(json);
					JSONObject data = object.getJSONObject("data");
					String name = data.optString("name"); // name : 用户帐户名
					// String nick=data.optString("nick"); // nick : 用户昵称
					LOG.cstdr(TAG, "name = " + name);
					// LOG.cstdr(TAG, "nick = " + nick);
					PreferenceUtil.getInstance(mContext).saveString(Constants.PREF_TX_NAME, name);
					if (l != null) {
						l.onResult();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});
	}

	/**
	 * 发送一条微博
	 * 
	 * @param content
	 *            微博内容（若在此处@好友，需正确填写好友的微博账号，而非昵称），不超过140字
	 * @param longitude
	 *            经度，为实数，如113.421234（最多支持10位有效数字，可以填空）不是必填
	 * @param latitude
	 *            纬度，为实数，如22.354231（最多支持10位有效数字，可以填空） 不是必填
	 * @param syncflag
	 *            微博同步到空间分享标记（可选，0-同步，1-不同步，默认为0），目前仅支持oauth1.0鉴权方式 不是必填
	 * @param compatibleflag
	 *            容错标志，支持按位操作，默认为0。 0x20-微博内容长度超过140字则报错 0-以上错误做容错处理，即发表普通微博
	 *            不是必填
	 * @param listener
	 *            回调函数
	 */
	public void addWeibo(String content, long longitude, long latitude, int syncflag, int compatibleflag) {
		TencentWeiboAPI weiboAPI = new TencentWeiboAPI(tencentTO);
		weiboAPI.addWeibo(content, longitude, latitude, syncflag, compatibleflag, new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				LOG.cstdr(TAG, "onIOException---e = " + e.getMessage());
				Util.showToast(mContext, "分享失败，请检查网络连接。出错信息：" + e.getMessage());
			}

			@Override
			public void onError(WeiboException e) {
				LOG.cstdr(TAG, "onError---e = " + e.getMessage());
				Util.showToast(mContext, "分享失败，请检查网络连接。出错信息：" + e.getMessage());
			}

			@Override
			public void onComplete(String str) {
				LOG.cstdr(TAG, "onComplete---str = " + str);
				Util.showToast(mContext, "分享成功，去你绑定的腾讯微博看看吧！");
			}
		});
	}

	/**
	 * 注销授权
	 * 
	 * @param l
	 */
	public void logout(WeiboListener l) {
		PreferenceUtil.getInstance(mContext).remove(Constants.PREF_TX_ACCESS_TOKEN);
		l.onResult();
	}

	/**
	 * 检查是否已授权
	 * 
	 * @return true 已授权，false 未授权
	 */
	public boolean isAuth() {
		String token = PreferenceUtil.getInstance(mContext).getString(Constants.PREF_TX_ACCESS_TOKEN, "");
		if (TextUtils.isEmpty(token)) {
			return false;
		}
		return true;
	}

}
