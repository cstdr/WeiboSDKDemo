package cstdr.weibosdk.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cstdr.weibosdk.demo.share.Constants;
import cstdr.weibosdk.demo.share.SinaWeiboUtil;
import cstdr.weibosdk.demo.share.TencentWeiboUtil;
import cstdr.weibosdk.demo.util.LOG;
import cstdr.weibosdk.demo.util.PreferenceUtil;
import cstdr.weibosdk.demo.util.Util;

/**
 * 主界面
 * 
 * @author cstdingran@gmail.com
 * 
 */
public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

	private Context mContext = null;

	private static Handler mHandler;

	// 腾讯微博===================================

	private TextView mTvAuthTX;

	private TextView mTvTokenTX;

	private Button mBtnAuthTX;

	private Button mBtnAddTX;

	private Button mBtnLogoutTX;

	// 新浪微博===================================

	private TextView mTvAuthSina;

	private TextView mTvTokenSina;

	private Button mBtnAuthSina;

	private Button mBtnAddSina;

	private Button mBtnLogoutSina;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Log.i(TAG, "============onCreate==================");
		mContext = this;
		initView();
		refreshView();
	}

	private void initView() {
		// TX
		initTvAuthTX();
		initTvTokenTX();
		initBtnAuthTX();
		initBtnAddTX();
		initBtnLogoutTX();
		// sina
		initTvAuthSina();
		initTvTokenSina();
		initBtnAuthSina();
		initBtnAddSina();
		initBtnLogoutSina();
	}

	// TX ================================================================

	private void initBtnLogoutTX() {
		mBtnLogoutTX = (Button) findViewById(R.id.btn_logout_tx);
		mBtnLogoutTX.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TencentWeiboUtil.getInstance(mContext).logout(new WeiboListener() {

					@Override
					public void onResult() {
						refreshView();
					}

				});
			}
		});
	}

	private void initBtnAddTX() {
		mBtnAddTX = (Button) findViewById(R.id.btn_add_tx);
		mBtnAddTX.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TencentWeiboUtil.getInstance(mContext).addWeibo("腾讯微博开放平台，Hello world！～", 0, 0, 0, 0);
			}
		});
	}

	private void initBtnAuthTX() {
		mBtnAuthTX = (Button) findViewById(R.id.btn_auth_tx);
		mBtnAuthTX.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Util.checkNet(mContext)) {
					TencentWeiboUtil.getInstance(mContext).auth(new WeiboListener() {

						@Override
						public void onResult() {
							MainActivity.getHandler().post(new Runnable() {

								@Override
								public void run() {
									refreshView();
								}
							});
						}
					});
				}
			}
		});
	}

	private void initTvTokenTX() {
		mTvTokenTX = (TextView) findViewById(R.id.tv_token_tx);
	}

	private void initTvAuthTX() {
		mTvAuthTX = (TextView) findViewById(R.id.tv_auth_tx);
	}

	// sina
	// ==========================================================================

	private void initBtnLogoutSina() {
		mBtnLogoutSina = (Button) findViewById(R.id.btn_logout_sina);
		mBtnLogoutSina.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SinaWeiboUtil.getInstance(mContext).logout(new WeiboListener() {

					@Override
					public void onResult() {
						refreshView();
					}

				});
			}
		});
	}

	private void initBtnAddSina() {
		mBtnAddSina = (Button) findViewById(R.id.btn_add_sina);
		mBtnAddSina.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SinaWeiboUtil.getInstance(mContext).update("新浪微博开放平台，Hello world！～", null, null);

				// TODO 发送本地图片微博
				// String dir =
				// Environment.getExternalStorageDirectory().getAbsolutePath();
				// String picPath = dir + "/cstdrpic.jpg"; // 需要改成你SD卡下的图片地址
				// SinaWeiboUtil.getInstance(mContext).upload("测试，新浪微博开放平台图片微博～～",
				// picPath, null, null);
				// LOG.cstdr(TAG, "picPath = " + picPath);

				// TODO 指定一个图片URL地址抓取后上传并同时发布一条新微博
				// // 图片的URL地址，必须以http开头。
				// String picUrl =
				// "https://www.google.com.hk/images/srpr/logo4w.png";
				// SinaWeiboUtil.getInstance(mContext).uploadUrlText("测试，新浪微博开放平台图片微博～～",
				// picUrl, null, null);
				// LOG.cstdr(TAG, "picUrl = " + picUrl);
			}
		});
	}

	private void initBtnAuthSina() {
		mBtnAuthSina = (Button) findViewById(R.id.btn_auth_sina);
		mBtnAuthSina.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Util.checkNet(mContext)) {
					SinaWeiboUtil.getInstance(mContext).auth(new WeiboListener() {

						@Override
						public void onResult() {
							MainActivity.getHandler().post(new Runnable() {

								@Override
								public void run() {
									refreshView();
								}
							});
						}
					});
				}
			}
		});
	}

	private void initTvTokenSina() {
		mTvTokenSina = (TextView) findViewById(R.id.tv_token_sina);
	}

	private void initTvAuthSina() {
		mTvAuthSina = (TextView) findViewById(R.id.tv_auth_sina);
	}

	//

	/**
	 * 刷新View
	 */
	private void refreshView() {
		String sinaToken = PreferenceUtil.getInstance(mContext).getString(Constants.PREF_SINA_ACCESS_TOKEN, "");
		String tencentToken = PreferenceUtil.getInstance(mContext).getString(Constants.PREF_TX_ACCESS_TOKEN, "");
		LOG.cstdr("refreshUserView", "sinaToken = " + sinaToken + "   tencentToken = " + tencentToken);

		if (TextUtils.isEmpty(sinaToken) && TextUtils.isEmpty(tencentToken)) { // 未授权
			MainActivity.getHandler().post(new Runnable() {

				@Override
				public void run() {
					mTvAuthTX.setText("未授权");
					mTvAuthSina.setText("未授权");
					Util.showToast(mContext, "未授权");
				}
			});
		} else {
			if (!TextUtils.isEmpty(tencentToken)) { // 初始化腾讯微博，判断是否授权过期
				TencentWeiboUtil.getInstance(mContext).initTencentWeibo(new WeiboListener() {

					@Override
					public void init(boolean isValid) {
						LOG.cstdr("txtxt~~~~~~~~~~~~isValid = ", isValid + "");
						if (isValid) {
							mTvAuthTX.setText("腾讯微博已授权");
							String token = PreferenceUtil.getInstance(mContext).getString(
									Constants.PREF_TX_ACCESS_TOKEN, "");
							String openId = PreferenceUtil.getInstance(mContext).getString(Constants.PREF_TX_OPEN_ID,
									"");
							String name = PreferenceUtil.getInstance(mContext).getString(Constants.PREF_TX_NAME, "");
							String expiresTime = PreferenceUtil.getInstance(mContext).getString(
									Constants.PREF_TX_EXPIRES_TIME, "");
							String clientIp = PreferenceUtil.getInstance(mContext).getString(
									Constants.PREF_TX_CLIENT_IP, "");
							mTvTokenTX.setText("token = " + token + "\nopenId = " + openId + "\nname = " + name
									+ "\nexpiresTime = " + expiresTime + "\nclientIp = " + clientIp);

							MainActivity.getHandler().post(new Runnable() {

								@Override
								public void run() {
									Util.showToast(mContext, "腾讯微博已授权");
								}
							});
						} else {
							mTvAuthTX.setText("授权已过期");
							MainActivity.getHandler().post(new Runnable() {

								@Override
								public void run() {
									Util.showToast(mContext, "腾讯微博授权已过期，请重新绑定。");
								}
							});
						}
					}
				});
			}
			if (!TextUtils.isEmpty(sinaToken)) { // 初始化新浪微博，判断是否授权过期
				SinaWeiboUtil.getInstance(mContext).initSinaWeibo(new WeiboListener() {

					@Override
					public void init(boolean isValid) {
						LOG.cstdr("sina~~~~~~~~~~~~isValid = ", isValid + "");
						if (isValid) {
							mTvAuthSina.setText("新浪微博已授权");
							String token = PreferenceUtil.getInstance(mContext).getString(
									Constants.PREF_SINA_ACCESS_TOKEN, "");
							long expiresTime = PreferenceUtil.getInstance(mContext).getLong(
									Constants.PREF_SINA_EXPIRES_TIME, 0);
							String uid = PreferenceUtil.getInstance(mContext).getString(Constants.PREF_SINA_UID, "");
							String userName = PreferenceUtil.getInstance(mContext).getString(
									Constants.PREF_SINA_USER_NAME, "");
							String remindIn = PreferenceUtil.getInstance(mContext).getString(
									Constants.PREF_SINA_REMIND_IN, "");
							mTvTokenSina.setText("access_token 仍在有效期内,无需再次登录: \naccess_token:" + token
									+ "\nexpiresTime：" + expiresTime + "\nuid:" + uid + "\nuserName:" + userName
									+ "\nremindIn:" + remindIn);

							MainActivity.getHandler().post(new Runnable() {

								@Override
								public void run() {
									Util.showToast(mContext, "新浪微博已授权");
								}
							});
						} else {
							mTvAuthSina.setText("授权已过期");
							MainActivity.getHandler().post(new Runnable() {

								@Override
								public void run() {
									Util.showToast(mContext, "新浪微博授权已过期，请重新绑定。");
								}
							});
						}
					}
				});
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LOG.cstdr(TAG, "===================onActivityResult===========requestCode = " + requestCode);
		// sso 授权回调
		if (requestCode == 32973) {
			SinaWeiboUtil.getInstance(mContext).authCallBack(requestCode, resultCode, data);
		} else if (requestCode == 1) {
			TencentWeiboUtil.getInstance(mContext).webAuthOnResult();
		}
	}

	/**
	 * 授权回调类
	 */
	public class WeiboListener {

		public void init(boolean isValid) {
		}

		public void onResult() {
		}
	}

	public static Handler getHandler() {
		if (mHandler == null) {
			mHandler = new Handler();
		}
		return mHandler;
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "============onResume==================");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.i(TAG, "============onRestart==================");

	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "============onPause==================");

	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i(TAG, "============onStop==================");

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "============onDestroy==================");
	}

}
