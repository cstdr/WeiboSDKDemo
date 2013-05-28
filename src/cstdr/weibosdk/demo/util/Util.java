package cstdr.weibosdk.demo.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

/**
 * 工具类
 */
public class Util {

    /**
     * 判断网络
     * @return
     */
    public static boolean checkNet(Context context) {
        if(hasConnectedNetwork(context)) {
            return true;
        }
        showToast(context, "无网络连接...");
        return false;
    }

    /**
     * 是否网络连接
     * @param context
     * @return
     */
    public static boolean hasConnectedNetwork(Context context) {
        ConnectivityManager connectivity=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivity == null) {
            return false;
        }
        return connectivity.getActiveNetworkInfo() != null;
    }

    /**
     * 显示toast
     * @param context
     * @param s
     */
    public static void showToast(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示toast（长时间）
     * @param context
     * @param s
     */
    public static void showToastLong(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

}
