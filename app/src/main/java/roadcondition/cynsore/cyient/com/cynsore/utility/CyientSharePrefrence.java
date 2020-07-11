package roadcondition.cynsore.cyient.com.cynsore.utility;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;

/**
 * Created by as38911 on 15/Oct/2016.
 */

public class CyientSharePrefrence {

    private static SharedPreferences sharedpreferences;

    public static SharedPreferences GetSharedPrefrence(Context context) {
        if (sharedpreferences == null) {
            sharedpreferences = context.getSharedPreferences("Cyient", Context.MODE_PRIVATE);
        }
        return sharedpreferences;
    }

    public static void setStringInSharePef(Context context, String key, String value) {

        SharedPreferences.Editor editor = GetSharedPrefrence(context).edit();
        editor.putString(key, value).apply();
    }

    public static void setJobDataInSharePef(Context context, String key, String value) {

        String fileNames = getJobDataFromSharePef(context, key);
        StringBuilder builder = new StringBuilder(fileNames);
        if (fileNames.length() == 0) {
            builder.append(value);
        } else {
            builder.append("," + value);
        }
        SharedPreferences.Editor editor = GetSharedPrefrence(context).edit();
        editor.putString(key, builder.toString()).apply();
    }

    public static void setIntegerValInSharePef(Context context, String key, Integer value) {
        SharedPreferences.Editor editor = GetSharedPrefrence(context).edit();
        editor.putInt(key, value).apply();
    }

    public static void setBooleanInSharePef(Context context, String key, Boolean value) {

        SharedPreferences.Editor editor = GetSharedPrefrence(context).edit();
        editor.putBoolean(key, value).apply();
    }


    public static void setLongInSharePef(Context context, String key, Long value) {

        SharedPreferences.Editor editor = GetSharedPrefrence(context).edit();
        editor.putLong(key, value).apply();
    }


    public static void setFloatInSharePef(Context context, String key, Float value) {

        SharedPreferences.Editor editor = GetSharedPrefrence(context).edit();
        editor.putFloat(key, value).apply();
    }

    public static void setArrayListInSharePef(Context context, String key, Set value) {

        SharedPreferences.Editor editor = GetSharedPrefrence(context).edit();
        editor.putStringSet(key, value).apply();
    }

    public static Set getArrayListFromSharePef(Context context, String key) {

        SharedPreferences sharedPreferences = GetSharedPrefrence(context);
        return sharedPreferences.getStringSet(key, null);
    }


    public static String getStringFromSharePef(Context context, String key) {

        SharedPreferences sharedPreferences = GetSharedPrefrence(context);
        return sharedPreferences.getString(key, null);
    }

    public static String getJobDataFromSharePef(Context context, String key) {

        SharedPreferences sharedPreferences = GetSharedPrefrence(context);
        return sharedPreferences.getString(key, "");
    }

    public static int getIntegerFromSharePef(Context context, String key, int defValue) {

        SharedPreferences sharedPreferences = GetSharedPrefrence(context);
        return sharedPreferences.getInt(key, defValue);
    }


    public static Boolean getBoolenValSharePef(Context context, String key) {

        SharedPreferences sharedPreferences = GetSharedPrefrence(context);
        return sharedPreferences.getBoolean(key, false);
    }

    public static Boolean getBoolenValSharePef(Context context, String key, boolean defValue) {

        SharedPreferences sharedPreferences = GetSharedPrefrence(context);
        return sharedPreferences.getBoolean(key, defValue);
    }


    public static long getLongSharePef(Context context, String key) {

        SharedPreferences sharedPreferences = GetSharedPrefrence(context);
        return sharedPreferences.getLong(key, 0);
    }

    public static float getFloatSharePef(Context context, String key) {

        SharedPreferences sharedPreferences = GetSharedPrefrence(context);
        return sharedPreferences.getFloat(key, 0);
    }

    public static void clearSharePrefrence(Context context) {
        SharedPreferences.Editor editor = GetSharedPrefrence(context).edit();
        /*editor.clear();
        editor.apply();*/

        Map<String, ?> prefs = GetSharedPrefrence(context).getAll();
        for (Map.Entry<String, ?> prefToReset : prefs.entrySet()) {
            editor.remove(prefToReset.getKey()).apply();
        }

    }

    public static void clearJobDataFrmPreference(Context context, String key) {
        SharedPreferences.Editor editor = GetSharedPrefrence(context).edit();
        editor.remove(key).apply();
    }


}
