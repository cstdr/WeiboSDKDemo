package com.example.testweibo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Enumeration;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.weibo.sdk.android.api.util.Util;
import com.tencent.weibo.sdk.android.component.GeneralInterfaceActivity;
import com.tencent.weibo.sdk.android.component.sso.AuthHelper;
import com.tencent.weibo.sdk.android.component.sso.OnAuthListener;
import com.tencent.weibo.sdk.android.component.sso.WeiboToken;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.net.RequestListener;
import com.weibo.sdk.android.sso.SsoHandler;

public class MainActivity extends Activity {

    private static final String TAG="MainActivity";

    private Context context=null;

    private TextView mTvAuth;

    private TextView mTvToken;

    private Button mBtnAuth;

    private Button mBtnAdd;

    private Button mBtnReadd;

    private Button mBtnDelAuth;

    private Button mBtnComInterface;

    private TencentWeiboAPI weiboAPI;// 微博相关API

    private TencentTO tencentTO;

    public static final String ACTION_AUTH="com.downjoy.xwifi.ACTION_AUTH";

    private BroadcastReceiver authReceiver=new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            Log.i(TAG, "action = " + action);
            if(action.equals(ACTION_AUTH)) {
                initTencentWeibo();
            }
        }
    };

    // 新浪微博===================================

    private TextView mTvAuthSina;

    private Button mBtnAuthSina;

    private Button mBtnAddSina;

    private Button mBtnLogoutSina;

    private Weibo mWeibo;

    public static Oauth2AccessToken accessToken;

    /**
     * SsoHandler 仅当sdk支持sso时有效，
     */
    private SsoHandler mSsoHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.i(TAG, "============onCreate==================");
        context=this;
        initReceiver();
        initView();

        // TODO
        initSinaWeibo();

        initTencentWeibo();

    }

    private void initReceiver() {
        IntentFilter authFilter=new IntentFilter(ACTION_AUTH);
        registerReceiver(authReceiver, authFilter);
    }

    private void unInitReceiver() {
        unregisterReceiver(authReceiver);
    }

    private void initSinaWeibo() {
        mWeibo=Weibo.getInstance(Constants.SINA_APP_KEY, Constants.SINA_REDIRECT_URL, Constants.SINA_SCOPE);
        String token=PreferenceUtil.getInstance(context).getString(Constants.PREF_SINA_ACCESS_TOKEN, "");
        long expiresTime=PreferenceUtil.getInstance(context).getLong(Constants.PREF_SINA_EXPIRES_IN, 0);
        accessToken=new Oauth2AccessToken();
        accessToken.setToken(token);
        accessToken.setExpiresTime(expiresTime);
        Log.i(TAG, "accessToken = " + accessToken);
        Log.i(TAG, "accessToken.getToken() = " + accessToken.getToken());
        Log.i(TAG, "accessToken.getExpiresTime() = " + accessToken.getExpiresTime());
        if(MainActivity.accessToken.isSessionValid()) { // TODO 判断是否已授权
            String date=
                new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").format(new java.util.Date(MainActivity.accessToken.getExpiresTime()));
            mTvAuthSina.setText("access_token 仍在有效期内,无需再次登录: \naccess_token:" + MainActivity.accessToken.getToken() + "\n有效期："
                + date);
        } else {
            mTvAuthSina.setText("使用SSO登录前，请检查手机上是否已经安装新浪微博客户端，" + "目前仅3.0.0及以上微博客户端版本支持SSO；如果未安装，将自动转为Oauth2.0进行认证");
        }
    }

    private void initTencentWeibo() {
        // TODO
        tencentTO=new TencentTO();

        String accessToken=PreferenceUtil.getInstance(context).getString(Constants.PREF_TX_ACCESS_TOKEN, "");
        if(TextUtils.isEmpty(accessToken)) {
            // 未授权
            mTvAuth.setText("未授权");
        } else {
            long expiresTime=Long.parseLong(PreferenceUtil.getInstance(context).getString(Constants.PREF_TX_EXPIRES_TIME, ""));
            Log.i(TAG, "expiresTime = " + expiresTime);
            Log.i(TAG, "expiresTime - System.currentTimeMillis() = " + (expiresTime - System.currentTimeMillis()));
            if(expiresTime - System.currentTimeMillis() > 0) { // 已授权未过期
                mTvAuth.setText("已授权未过期");
                String openId=PreferenceUtil.getInstance(context).getString(Constants.PREF_TX_OPEN_ID, "");
                String clientId=PreferenceUtil.getInstance(context).getString(Constants.PREF_TX_CLIENT_ID, "");
                String clientIp=PreferenceUtil.getInstance(context).getString(Constants.PREF_TX_CLIENT_IP, "");
                tencentTO.setAccessToken(accessToken);
                tencentTO.setOpenId(openId);
                tencentTO.setAppkey(clientId);
                tencentTO.setClientIp(clientIp);
            } else { // 已过期
                mTvAuth.setText("已授权已过期，请重新点击授权");
            }
        }
    }

    private void initView() {
        initTvAuth();
        initTvToken();
        initBtnAuth();
        initBtnAdd();
        initBtnReadd();
        initBtnDelAuth();
        initBtnComInterface();
        // sina
        initTvAuthSina();
        initBtnAuthSina();
        initBtnAddSina();
        initBtnLogoutSina();
    }

    private void initBtnLogoutSina() {
        mBtnLogoutSina=(Button)findViewById(R.id.btn_logout_sina);
        mBtnLogoutSina.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                long uid=PreferenceUtil.getInstance(context).getLong(Constants.PREF_SINA_UID, 0);
                SinaWeiboAPI api=new SinaWeiboAPI(accessToken);
                api.show(uid, new RequestListener() {

                    @Override
                    public void onIOException(IOException arg0) {
                        Log.i(TAG, "onIOException");
                    }

                    @Override
                    public void onError(WeiboException arg0) {
                        Log.i(TAG, "onError = " + arg0.getMessage());

                    }

                    @Override
                    public void onComplete4binary(ByteArrayOutputStream arg0) {
                        Log.i(TAG, "onComplete4binary");

                    }

                    @Override
                    public void onComplete(String uid) {
                        Log.i(TAG, "onComplete---uid = " + uid);
                    }
                });

            }
        });
    }

    private void initBtnAddSina() {
        mBtnAddSina=(Button)findViewById(R.id.btn_add_sina);
        mBtnAddSina.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                SinaWeiboAPI api=new SinaWeiboAPI(accessToken);
                api.update("尼玛的新浪微博开放平台，和腾讯一样烂！～", null, null, new RequestListener() {

                    @Override
                    public void onIOException(IOException arg0) {
                        Log.i(TAG, "onIOException");
                    }

                    @Override
                    public void onError(WeiboException arg0) {
                        Log.i(TAG, "onError = " + arg0.getMessage());

                    }

                    @Override
                    public void onComplete4binary(ByteArrayOutputStream arg0) {
                        Log.i(TAG, "onComplete4binary");

                    }

                    @Override
                    public void onComplete(String uid) {
                        Log.i(TAG, "onComplete---uid = " + uid);
                    }
                });
            }
        });
    }

    private void initBtnAuthSina() {
        mBtnAuthSina=(Button)findViewById(R.id.btn_auth_sina);
        mBtnAuthSina.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO
                mSsoHandler=new SsoHandler(MainActivity.this, mWeibo);
                mSsoHandler.authorize(new AuthDialogListener(), null);

                // mWeibo.anthorize(MainActivity.this, new AuthDialogListener()); // 网页授权
            }
        });
    }

    private void initTvAuthSina() {
        mTvAuthSina=(TextView)findViewById(R.id.tv_auth_sina);
    }

    // TX ================================================================
    private void initBtnComInterface() {
        mBtnComInterface=(Button)findViewById(R.id.btn_comInterface);
        mBtnComInterface.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, GeneralInterfaceActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initBtnDelAuth() {
        mBtnDelAuth=(Button)findViewById(R.id.btn_delAuth);
        mBtnDelAuth.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Util.clearSharePersistent(context);
                mTvAuth.setText("注销成功");
            }
        });
    }

    private void initBtnReadd() {
        mBtnReadd=(Button)findViewById(R.id.btn_readd);
        mBtnReadd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // Intent intent=new Intent(MainActivity.this, ReAddActivity.class);
                // Bundle bundle=new Bundle();
                // bundle.putString("content", "Make U happy～～");
                // bundle.putString("video_url", "http://www.tudou.com/programs/view/b-4VQLxwoX4/");
                // bundle.putString("pic_url", "http://t2.qpic.cn/mblogpic/9c7e34358608bb61a696/2000");
                // intent.putExtras(bundle);
                // startActivity(intent);

                weiboAPI=new TencentWeiboAPI(tencentTO);
                weiboAPI.getUserInfo(new RequestListener() {

                    @Override
                    public void onIOException(IOException arg0) {
                        Log.i(TAG, "onIOException");
                    }

                    @Override
                    public void onError(WeiboException arg0) {
                        Log.i(TAG, "onError = " + arg0.getMessage());

                    }

                    @Override
                    public void onComplete4binary(ByteArrayOutputStream arg0) {
                        Log.i(TAG, "onComplete4binary");

                    }

                    @Override
                    public void onComplete(String json) {
                        Log.i(TAG, "onComplete---json = " + json);
                        try {
                            JSONObject object=new JSONObject(json);
                            JSONObject data=object.getJSONObject("data");
                            String name=data.optString("name"); // name : 用户帐户名
                            String nick=data.optString("nick"); // nick : 用户昵称
                            Log.i(TAG, "name = " + name);
                            Log.i(TAG, "nick = " + nick);
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }

    private void initBtnAdd() {
        mBtnAdd=(Button)findViewById(R.id.btn_add);
        mBtnAdd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                weiboAPI=new TencentWeiboAPI(tencentTO);
                weiboAPI.addWeibo("尼玛的腾讯微博开放平台，和新浪一样烂！～", 0, 0, 0, 0, new RequestListener() {

                    @Override
                    public void onIOException(IOException arg0) {
                        Log.i(TAG, "onIOException");
                    }

                    @Override
                    public void onError(WeiboException arg0) {
                        Log.i(TAG, "onError = " + arg0.getMessage());

                    }

                    @Override
                    public void onComplete4binary(ByteArrayOutputStream arg0) {
                        Log.i(TAG, "onComplete4binary");

                    }

                    @Override
                    public void onComplete(String uid) {
                        Log.i(TAG, "onComplete---uid = " + uid);
                    }
                });

                // Bitmap
                // bm=((BitmapDrawable)getApplicationContext().getResources().getDrawable(R.drawable.ic_launcher)).getBitmap();
                // api.addPic(getApplicationContext(), "2131231231231", "json", 0, 0, bm, 0, 0, null, null, 4);

            }
        });
    }

    private void initBtnAuth() {
        mBtnAuth=(Button)findViewById(R.id.btn_auth);
        mBtnAuth.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                long appId=Long.valueOf(Util.getConfig().getProperty(Constants.TX_APP_KEY));
                String appSecket=Util.getConfig().getProperty(Constants.TX_APP_KEY_SEC);
                Log.i(TAG, "appId = " + appId + " appSecket = " + appSecket);
                authTX(appId, appSecket);
            }
        });
    }

    private void initTvToken() {
        mTvToken=(TextView)findViewById(R.id.tv_token);
    }

    private void initTvAuth() {
        mTvAuth=(TextView)findViewById(R.id.tv_auth);
    }

    private void authTX(long appId, String appSecket) {
        AuthHelper.register(context, appId, appSecket, new OnAuthListener() {

            @Override
            public void onWeiboVersionMisMatch() {
                mTvAuth.setText("onWeiboVersionMisMatch");
                Intent intent=new Intent(MainActivity.this, TencentWebAuthActivity.class);
                startActivity(intent);
            }

            @Override
            public void onWeiBoNotInstalled() {
                mTvAuth.setText("onWeiBoNotInstalled");
                Intent intent=new Intent(MainActivity.this, TencentWebAuthActivity.class);
                startActivity(intent);
            }

            @Override
            public void onAuthPassed(String name, WeiboToken token) {
                mTvAuth.setText("onAuthPassed---name = " + name + " token = " + token);
                StringBuffer sb=new StringBuffer();
                sb.append("token.accessToken = " + token.accessToken).append("\ntoken.expiresIn = " + token.expiresIn)
                    .append("\ntoken.omasKey = " + token.omasKey).append("\ntoken.omasToken = " + token.omasToken)
                    .append("\ntoken.openID = " + token.openID).append("\ntoken.refreshToken = " + token.refreshToken);
                mTvToken.setText(sb.toString());
                Log.i(TAG, "onAuthPassed---name = " + name + " token = " + token);
                Log.i(TAG, "onAuthPassed = " + sb.toString());
                //
                String clientId=Util.getConfig().getProperty(Constants.TX_APP_KEY);
                String clientIp=getClientIp();
                PreferenceUtil.getInstance(context).saveString(Constants.PREF_TX_ACCESS_TOKEN, token.accessToken);
                PreferenceUtil.getInstance(context).saveString(Constants.PREF_TX_EXPIRES_IN, String.valueOf(token.expiresIn));
                PreferenceUtil.getInstance(context).saveString(Constants.PREF_TX_OPEN_ID, token.openID);
                PreferenceUtil.getInstance(context).saveString(Constants.PREF_TX_OPEN_KEY, token.omasKey);
                PreferenceUtil.getInstance(context).saveString(Constants.PREF_TX_REFRESH_TOKEN, token.refreshToken); // 总是为null
                PreferenceUtil.getInstance(context).saveString(Constants.PREF_TX_NAME, name);
                PreferenceUtil.getInstance(context).saveString(Constants.PREF_TX_CLIENT_ID, clientId);
                PreferenceUtil.getInstance(context).saveString(Constants.PREF_TX_EXPIRES_TIME,
                    String.valueOf(System.currentTimeMillis() + token.expiresIn * 1000));
                PreferenceUtil.getInstance(context).saveString(Constants.PREF_TX_CLIENT_IP, clientIp);

                // TODO
                tencentTO.setAccessToken(token.accessToken);
                tencentTO.setAppkey(clientId);
                tencentTO.setClientIp(clientIp);
                tencentTO.setOpenId(token.openID);

                Log.i(TAG, "clientIp = " + clientIp);

            }

            @Override
            public void onAuthFail(int result, String error) {
                mTvAuth.setText("onAuthFail---result = " + result + " error = " + error);
                // Intent intent=new Intent(MainActivity.this, TencentWebAuthActivity.class);
                // startActivity(intent);
            }
        });
        AuthHelper.auth(this, "");
    }

    /**
     * 获得客户端IP
     * @return
     */
    public static String getClientIp() {
        try {
            for(Enumeration<NetworkInterface> mEnumeration=NetworkInterface.getNetworkInterfaces(); mEnumeration.hasMoreElements();) {
                NetworkInterface intf=mEnumeration.nextElement();
                for(Enumeration<InetAddress> enumIPAddr=intf.getInetAddresses(); enumIPAddr.hasMoreElements();) {
                    InetAddress inetAddress=enumIPAddr.nextElement();
                    // 如果不是回环地址
                    if(!inetAddress.isLoopbackAddress()) {
                        // 直接返回本地IP地址
                        // int i=Integer.parseInt(inetAddress.getHostAddress());
                        // String ipStr=(i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
                        // return ipStr;
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch(SocketException ex) {
            Log.e("Error", ex.toString());
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    // sina ==========================================================================

    class AuthDialogListener implements WeiboAuthListener {

        @Override
        public void onCancel() {
            Log.i(TAG, "===================AuthDialogListener=onCancel==========");
            mTvAuthSina.setText("Auth cancel~~~~~~~");
        }

        @Override
        public void onComplete(Bundle values) {
            Log.i(TAG, "===================AuthDialogListener=onComplete==========");
            mTvAuthSina.setText("onComplete~~~~~~~");
            final String code=values.getString(Constants.SINA_CODE);
            if(code != null) {
                mTvAuthSina.setText("取得认证code = " + code);
                // TODO
                // new Thread() {
                //
                // @Override
                // public void run() {
                // getTokenByCode(code);
                // }
                //
                // }.start();

                SinaWeiboAPI api=new SinaWeiboAPI(accessToken);
                api.getTokenByCode(code, new RequestListener() {

                    @Override
                    public void onIOException(IOException arg0) {
                    }

                    @Override
                    public void onError(WeiboException arg0) {
                        Log.i(TAG, "onError = " + arg0);
                    }

                    @Override
                    public void onComplete4binary(ByteArrayOutputStream arg0) {
                    }

                    @Override
                    public void onComplete(String json) {
                        JSONObject object;
                        try {
                            object=new JSONObject(json);
                            String token=object.optString(Constants.SINA_ACCESS_TOKEN);
                            String expiresIn=object.optString(Constants.SINA_EXPIRES_IN);
                            String uid=object.optString(Constants.SINA_UID);
                            Log.i(TAG, "token = " + token);
                            Log.i(TAG, "expiresIn = " + expiresIn);
                            Log.i(TAG, "uid = " + uid);
                            MainActivity.accessToken=new Oauth2AccessToken(token, expiresIn);
                            if(MainActivity.accessToken.isSessionValid()) {
                                PreferenceUtil.getInstance(context).saveString(Constants.PREF_SINA_ACCESS_TOKEN, token);
                                PreferenceUtil.getInstance(context).saveLong(Constants.PREF_SINA_EXPIRES_IN,
                                    accessToken.getExpiresTime());
                                PreferenceUtil.getInstance(context).saveString(Constants.PREF_SINA_UID, uid);
                            }
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Toast.makeText(MainActivity.this, "认证code成功", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(WeiboDialogError e) {
            Log.i(TAG, "===================AuthDialogListener=onError==========");
            mTvAuthSina.setText("WeiboDialogError = " + e.getMessage());
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Log.i(TAG, "===================AuthDialogListener=onWeiboException==========");
            mTvAuthSina.setText("WeiboException = " + e.getMessage());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "===================onActivityResult===========");
        // sso 授权回调
        if(mSsoHandler != null) {
            Log.i(TAG, "=====onActivityResult=mSsoHandler resultCode = " + resultCode + " requestCode = " + requestCode);
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

    }

    private void getTokenByCode(String code) {
        // TODO Auto-generated method stub
        String s=
            Constants.SINA_CLIENT_ID + "=" + Constants.SINA_APP_KEY + "&" + Constants.SINA_CLIENT_SECRET + "="
                + Constants.SINA_APP_SECRET + "&" + Constants.SINA_GRANT_TYPE + "=" + Constants.SINA_GRANT_TYPE_VALUE + "&"
                + Constants.SINA_CODE + "=" + code + "&" + Constants.SINA_REDIRECT_URI + "=";
        String url;
        try {
            url=
                Constants.SINA_BASEURL + Constants.SINA_ACCESS_TOKEN + "?" + s
                    + URLEncoder.encode(Constants.SINA_REDIRECT_URL, "UTF-8");
            Log.i(TAG, "url = " + url);
            HttpPost httpPost=new HttpPost(url);
            DefaultHttpClient client=new DefaultHttpClient();
            HttpResponse response=client.execute(httpPost);
            int statusCode=response.getStatusLine().getStatusCode();
            Log.i(TAG, "statusCode = " + statusCode);
            if(statusCode == 200) {
                HttpEntity entity=response.getEntity();
                InputStream in=entity.getContent();
                ByteArrayOutputStream out=new ByteArrayOutputStream();
                byte[] buffer=new byte[4096];
                int line;
                while((line=in.read(buffer)) != -1) {
                    out.write(buffer, 0, line);
                }
                String responseStr=new String(out.toByteArray(), "UTF-8");
                Log.i(TAG, "responseStr = " + responseStr);
                JSONObject object=new JSONObject(responseStr);
                String token=object.optString(Constants.SINA_ACCESS_TOKEN);
                String expiresIn=object.optString(Constants.SINA_EXPIRES_IN);
                long uid=object.optLong(Constants.SINA_UID);
                Log.i(TAG, "token = " + token);
                Log.i(TAG, "expiresTime = " + expiresIn);
                Log.i(TAG, "uid = " + uid);

                accessToken=new Oauth2AccessToken(token, expiresIn);
                if(accessToken.isSessionValid()) {
                    PreferenceUtil.getInstance(context).saveString(Constants.PREF_SINA_ACCESS_TOKEN, token);
                    PreferenceUtil.getInstance(context).saveLong(Constants.PREF_SINA_EXPIRES_IN, accessToken.getExpiresTime());
                    PreferenceUtil.getInstance(context).saveLong(Constants.PREF_SINA_UID, uid);

                    // mTvAuthSina.setText("成功!  access_token = " + token + "expires_in = " + expiresTime + "有效期 = " + date);
                    // Toast.makeText(MainActivity.this,
                    // "成功!  access_token = " + token + "expires_in = " + expiresTime + "有效期 = " + date, Toast.LENGTH_SHORT)
                    // .show();
                }
            }
        } catch(UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch(ClientProtocolException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        } catch(JSONException e) {
            e.printStackTrace();
        }
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
        unInitReceiver();
    }

}
