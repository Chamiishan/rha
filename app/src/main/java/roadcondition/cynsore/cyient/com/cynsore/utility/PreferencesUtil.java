package roadcondition.cynsore.cyient.com.cynsore.utility;

import android.content.Context;
import android.content.SharedPreferences;

import roadcondition.cynsore.cyient.com.cynsore.R;

public final class PreferencesUtil {

    private static final String SUSPENSION_TYPE = "SUSPENSION_TYPE";
    private static volatile PreferencesUtil instance;
    private final Context context;

    private PreferencesUtil(Context context) {
        this.context = context;
    }

    public enum SUSPENSION_TYPES {
        SOFT,  // 0
        MEDIUM, // 1
        HARD,  // 2
    }

    public static PreferencesUtil getInstance(Context context) {
        PreferencesUtil localInstance = instance;
        if (localInstance == null) {
            synchronized (PreferencesUtil.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new PreferencesUtil(context);
                }
            }
        }
        return localInstance;
    }

    public SUSPENSION_TYPES getSuspensionType() {
        return SUSPENSION_TYPES.values()[getIntValue(SUSPENSION_TYPE, 1)];
    }

    public int getIntValue(String key, int defaultValue) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getInt(key, defaultValue);
    }

    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
    }
}
