package roadcondition.cynsore.cyient.com.cynsore;

import android.app.Application;

import roadcondition.cynsore.cyient.com.cynsore.utility.Constants;

import static roadcondition.cynsore.cyient.com.cynsore.utility.Constants.MODE.VIEW_MODE;

public class RHAApplication extends Application {

    private int MODE = VIEW_MODE;

    @Override
    public void onCreate() {
        super.onCreate();
        setMode(Constants.MODE.VIEW_MODE);
    }

    public void setMode(int mode) {
        this.MODE = mode;
    }

    public int getMODE() {
        return MODE;
    }
}