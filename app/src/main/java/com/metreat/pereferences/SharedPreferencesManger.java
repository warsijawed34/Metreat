package com.metreat.pereferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesManger {

    public static final String PREF_NAME = "meTreat_pref";
    public static final String LOGIN_TYPE_PREF_NAME = "meTreat_pref_token";

    public enum PREF_DATA_TYPE {
        BOOLEAN, STRING, INT, FLOAT, LONG
    }

    public static SharedPreferences getSharedPref(Context mContext) {
        return mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferences getTokenSharedPref(Context mContext) {
        return mContext.getSharedPreferences(LOGIN_TYPE_PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void setTokenPrefValue(Context mContext, String key, Object value, PREF_DATA_TYPE type) {
        Editor edit = getTokenSharedPref(mContext).edit();
        switch (type) {
            case BOOLEAN:
                edit.putBoolean(key, (Boolean) value);
                break;
            case STRING:
                edit.putString(key, (String) value);
                break;
            case INT:
                edit.putInt(key, (Integer) value);
                break;
            case FLOAT:
                edit.putFloat(key, (Float) value);
                break;
            case LONG:
                edit.putLong(key, (Long) value);
                break;
            default:
                break;
        }
        edit.commit();
    }

    public static void setPrefValue(Context mContext, String key, Object value, PREF_DATA_TYPE type) {
        Editor edit = getSharedPref(mContext).edit();
        switch (type) {
            case BOOLEAN:
                edit.putBoolean(key, (Boolean) value);
                break;
            case STRING:
                edit.putString(key, (String) value);
                break;
            case INT:
                edit.putInt(key, (Integer) value);
                break;
            case FLOAT:
                edit.putFloat(key, (Float) value);
                break;
            case LONG:
                edit.putLong(key, (Long) value);
                break;
            default:
                break;
        }
        edit.commit();
    }


    public static Object getTokenPrefValue(Context mContext, String key, PREF_DATA_TYPE type) {
        Object resultant = null;
        SharedPreferences pref = getTokenSharedPref(mContext);
        switch (type) {
            case BOOLEAN:
                resultant = pref.getBoolean(key, false);
                break;
            case STRING:
                resultant = pref.getString(key, "");
                break;
            case INT:
                resultant = pref.getInt(key, 0);
                break;
            case FLOAT:
                resultant = pref.getFloat(key, 0.0f);
                break;
            case LONG:
                resultant = pref.getLong(key, 0L);
                break;
            default:
                break;
        }
        return resultant;
    }

    public static Object getPrefValue(Context mContext, String key, PREF_DATA_TYPE type) {
        Object resultant = null;
        SharedPreferences pref = getSharedPref(mContext);
        switch (type) {
            case BOOLEAN:
                resultant = pref.getBoolean(key, false);
                break;
            case STRING:
                resultant = pref.getString(key, "");
                break;
            case INT:
                resultant = pref.getInt(key, 0);
                break;
            case FLOAT:
                resultant = pref.getFloat(key, 0.0f);
                break;
            case LONG:
                resultant = pref.getLong(key, 0L);
                break;
            default:
                break;
        }
        return resultant;
    }

    public static void removeTokenPrefValue(Context mContext, String key) {
        Editor edit = getTokenSharedPref(mContext).edit();
        edit.clear();
        edit.commit();
    }

    public static void removePrefValue(Context mContext, String key) {
        Editor edit = getSharedPref(mContext).edit();
        edit.remove(key);
        edit.commit();
    }

    public static void removeAllPrefValue(Context mContext) {
        Editor edit = getSharedPref(mContext).edit();
        edit.clear();
        edit.commit();
    }
}