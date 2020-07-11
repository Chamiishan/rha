package roadcondition.cynsore.cyient.com.cynsore.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import roadcondition.cynsore.cyient.com.cynsore.BuildConfig;
import roadcondition.cynsore.cyient.com.cynsore.view.main.Splash;
import roadcondition.cynsore.cyient.com.cynsore.utility.Helper;


public class RRReceiver extends BroadcastReceiver {

    private static final String TAG = "RRReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) && BuildConfig.FLAVOR.equals("dongle")) {
            Helper.printLogMsg(TAG, "device started");
            Intent i = new Intent(context, Splash.class);
            i.putExtra("time_delay", 2000);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(i);
        }
    }
}