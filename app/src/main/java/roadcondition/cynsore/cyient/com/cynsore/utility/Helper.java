package roadcondition.cynsore.cyient.com.cynsore.utility;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * Created by ij39559 on 7/25/2018.
 */

public class Helper {

    public static boolean isOnline(Context context) {
        boolean connected = false;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.d("connectivity", e.toString());
        }
        return connected;
    }

    public static void registerGCM(final Context context) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                GoogleCloudMessaging gcm = null;
                String msg = "";
                try {
                    gcm = GoogleCloudMessaging.getInstance(context);
                    String googleMapRegistrationId = gcm.register(Constants.GOOGLE_PROJECT_ID);
                    Helper.printLogMsg("RegisterActivity", "registerInBackground - googleMapRegistrationId: "
                            + googleMapRegistrationId);
                    msg = "Device registered, registration ID = " + googleMapRegistrationId;
                    //save GCM regid in shared pref
                    CyientSharePrefrence.setStringInSharePef(context, SharePrefrenceConstant.REG_ID, googleMapRegistrationId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Helper.printLogMsg("RegisterActivity", "Error: " + msg);
                }
                Helper.printLogMsg("RegisterActivity", "AsyncTask completed: " + msg);
            }
        }).start();
    }

    public static boolean isGooglePlayServicesAvailable(Context context) {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            return false;
        }
    }

    public static void printLogMsg(String tag, String msg) {
        if (true) {
            Log.d(tag, msg);
        }
    }

    public static void printErrorMsg(String tag, String msg, Throwable t) {
        if (true) {
            Log.e(tag, msg, t);
        }
    }

    public static void printErrorMsg(String tag, String msg) {
        if (true) {
            Log.e(tag, msg);
        }
    }

    public static String getVersionName(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isUpdateReq(Context context, String newVerStr) {
        float oldVersion = Float.valueOf(getVersionName(context));
        float newVersion = Float.valueOf(newVerStr);

        if (newVersion > oldVersion) {
            return true;
        }
        return false;
    }

}
