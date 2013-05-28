package cstdr.weibosdk.demo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 首选项工具类
 */
public class PreferenceUtil {

    private static final String PREFERENCE_NAME="PREFERENCE_XWIFI";

    private static PreferenceUtil preferenceUtil;

    private SharedPreferences sp;

    private Editor ed;

    private PreferenceUtil(Context context) {
        init(context);
    }

    public void init(Context context) {
        if(sp == null || ed == null) {
            try {
                sp=context.getSharedPreferences(PREFERENCE_NAME, 0);
                ed=sp.edit();
            } catch(Exception e) {
            }
        }
    }

    public static PreferenceUtil getInstance(Context context) {
        if(preferenceUtil == null) {
            preferenceUtil=new PreferenceUtil(context);
        }
        preferenceUtil.init(context);
        return preferenceUtil;
    }

    public void saveLong(String key, long l) {
        ed.putLong(key, l);
        ed.commit();
    }

    public long getLong(String key, long defaultlong) {
        return sp.getLong(key, defaultlong);
    }

    public void saveBoolean(String key, boolean value) {
        ed.putBoolean(key, value);
        ed.commit();
    }

    public boolean getBoolean(String key, boolean defaultboolean) {
        return sp.getBoolean(key, defaultboolean);
    }

    public void saveInt(String key, int value) {
        if(ed != null) {
            ed.putInt(key, value);
            ed.commit();
        }
    }

    public int getInt(String key, int defaultInt) {
        return sp.getInt(key, defaultInt);
    }

    public String getString(String key, String defaultInt) {
        return sp.getString(key, defaultInt);
    }

    public String getString(Context context, String key, String defaultValue) {
        if(sp == null || ed == null) {
            sp=context.getSharedPreferences(PREFERENCE_NAME, 0);
            ed=sp.edit();
        }
        if(sp != null) {
            return sp.getString(key, defaultValue);
        }
        return defaultValue;
    }

    public void saveString(String key, String value) {
        ed.putString(key, value);
        ed.commit();
    }

    public void remove(String key) {
        ed.remove(key);
        ed.commit();
    }

    public void destroy() {
        sp=null;
        ed=null;
        preferenceUtil=null;
    }
}
