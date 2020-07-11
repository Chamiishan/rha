package roadcondition.cynsore.cyient.com.cynsore.utility;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import roadcondition.cynsore.cyient.com.cynsore.view.aboutus.FeedbackActivity;
import roadcondition.cynsore.cyient.com.cynsore.R;

/**
 * Created by sn34434 on 03-02-2017.
 */

public class Util {


    private static String TAG = Util.class.getSimpleName();

    public static void setPhoneNumber(Context context, String number) {
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences("road_runner_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("mobile", number);
            editor.commit();
        } else {
            Log.d("Util", "context returns null");
        }
    }

    public static String getPhoneNumber(Context context) {
        String number = null;
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences("road_runner_prefs", Context.MODE_PRIVATE);
            number = prefs.getString("mobile", null);
        } else {
            Log.d("Util", "context returns null");
        }
        return number;
    }

    /**
     * Launches the GPS dialog to prompt user to enable gps
     *
     * @param activity
     * @return
     */
    public static Dialog showFeedbackAlertDialog(final Activity activity) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gpsDialog = inflater.inflate(R.layout.show_alert_dailog, null);
        final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
        TextView dialogTitle = (TextView) gpsDialog.findViewById(R.id.txt_dia);
        //TextView dialogMessage = (TextView) gpsDialog.findViewById(R.id.gps_dialog_msg);
//        Typeface myTypeface = Typeface.createFromAsset(activity.getAssets(), "Roboto-Regular.ttf");
        //       Typeface myTypefaceBold = Typeface.createFromAsset(activity.getAssets(), "Roboto-Bold.ttf");
        //       dialogTitle.setTypeface(myTypefaceBold);
        //dialogMessage.setTypeface(myTypeface);
        Button yesButton = (Button) gpsDialog.findViewById(R.id.ok_btn);
//        yesButton.setTypeface(myTypeface);

        yesButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(activity, FeedbackActivity.class);
                activity.startActivity(intent);
            }
        });
        Button noButton = (Button) gpsDialog.findViewById(R.id.cancel_btn);
        //       noButton.setTypeface(myTypeface);
        noButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(gpsDialog);
        return dialog;
    }

    public static void setFeedbackFlag(Context context, boolean flag) {
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences(Constants.MY_PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(Constants.SENT_FEEDBACK, flag);
            editor.commit();
        } else {
            Log.d(TAG, "context returns null");
        }
    }

    public static boolean getFeedbackFlag(Context context) {
        boolean isSent = false;
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences(Constants.MY_PREFS_NAME, Context.MODE_PRIVATE);
            isSent = prefs.getBoolean(Constants.SENT_FEEDBACK, false);
        } else {
            Log.d(TAG, "context returns null");
        }
        return isSent;
    }

    /**
     * This method checks whether the user is connected to a network or not
     *
     * @param context
     * @return :true, if the user is connected to a network ,else returns false
     */
    public static boolean isOnline(Context context) {
        if (context == null) {
            return false;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.isConnectedOrConnecting();
        } else {
            return false;
        }
    }

    public static Bitmap customBitMap70(Context context, Drawable d) {
        Bitmap smallMarker = null;
        if (context != null) {
            BitmapDrawable bitmapdraw = (BitmapDrawable) (d);
            Bitmap b = bitmapdraw.getBitmap();
            smallMarker = Bitmap.createScaledBitmap(b, 80, 80, false);
        }
        return smallMarker;
    }

    public static String getDate_yyyy_MM_dd__HH_mm_ss(long timeinmillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(timeinmillis));
    }
}