package cstdr.weibosdk.demo.util;

import android.util.Log;

public abstract class LOG {

    public static final boolean DEBUG=true;

    public static void cstdr(String TAG, String msg) {
        if(DEBUG) {
            Log.i("cstdr", TAG + "~~~" + msg);
        }
    }
}
