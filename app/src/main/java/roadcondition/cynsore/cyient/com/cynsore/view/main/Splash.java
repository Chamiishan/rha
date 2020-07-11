package roadcondition.cynsore.cyient.com.cynsore.view.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import roadcondition.cynsore.cyient.com.cynsore.R;
import roadcondition.cynsore.cyient.com.cynsore.utility.Constants;
import roadcondition.cynsore.cyient.com.cynsore.view.login.LoginActivity;
import roadcondition.cynsore.cyient.com.cynsore.model.UserDetails;
import roadcondition.cynsore.cyient.com.cynsore.network.ServerAsyncTaskPost;
import roadcondition.cynsore.cyient.com.cynsore.network.ServerHelper;
import roadcondition.cynsore.cyient.com.cynsore.utility.CyientSharePrefrence;
import roadcondition.cynsore.cyient.com.cynsore.utility.Helper;
import roadcondition.cynsore.cyient.com.cynsore.utility.SharePrefrenceConstant;

public class Splash extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private static int TIME_DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Helper.isOnline(Splash.this)) {

            if (Helper.isGooglePlayServicesAvailable(Splash.this)) {

                Helper.registerGCM(Splash.this);

                TIME_DELAY = getIntent().getIntExtra("time_delay", TIME_DELAY);

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        checkLogin();
                    }
                }, TIME_DELAY);

            } else {
                Toast.makeText(Splash.this, R.string.play_services_na, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(Splash.this, R.string.internet_not_avl, Toast.LENGTH_LONG).show();
        }
    }

    private void checkLogin() {
        int loginstatus = CyientSharePrefrence.getIntegerFromSharePef(Splash.this, SharePrefrenceConstant.LOGIN_STATUS, Constants.LoginStatus.INACTIVE);

        if (loginstatus == Constants.LoginStatus.ACTIVE) {
            String email = CyientSharePrefrence.getStringFromSharePef(Splash.this, SharePrefrenceConstant.EMAIL_ID);
            String passwh = CyientSharePrefrence.getStringFromSharePef(Splash.this, SharePrefrenceConstant.PASSW_HASH);
            String name = CyientSharePrefrence.getStringFromSharePef(Splash.this, SharePrefrenceConstant.NAME);
            String mobNum = CyientSharePrefrence.getStringFromSharePef(Splash.this, SharePrefrenceConstant.MOB_NUM);
            String login_domain = CyientSharePrefrence.getStringFromSharePef(Splash.this, SharePrefrenceConstant.LOGIN_DOMAIN);

            loginOnServer(email, passwh, name, mobNum, login_domain);
        } else if (loginstatus == Constants.LoginStatus.INACTIVE) {

            Intent intent = new Intent(Splash.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
    }

    private void loginOnServer(String emailId, String passwh, String name, String mobNum, String login_domain) {
        try {
            String version = Helper.getVersionName(this);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email_id", emailId);
            jsonObject.put("passw_hash", passwh);
            jsonObject.put("name", name);
            jsonObject.put("mob_num", mobNum);
            jsonObject.put("login_domain", login_domain);
            String regId = CyientSharePrefrence.getStringFromSharePef(Splash.this, SharePrefrenceConstant.REG_ID);
            jsonObject.put("gcm_id", regId);
            jsonObject.put("version", version);
            String url = Constants.base_url + "user/doregisterorlogin";
            mLoginRegistrationHelper.setUrl(url);
            ServerAsyncTaskPost serverAsyncTask = new ServerAsyncTaskPost(Splash.this, mLoginRegistrationHelper);
            serverAsyncTask.showCallProgress(true);
            serverAsyncTask.execute(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    ServerHelper mLoginRegistrationHelper = new ServerHelper() {

        @Override
        public void onFailure(Object o) throws NullPointerException {
            Toast.makeText(Splash.this, R.string.server_error, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onSuccess(Object obj) throws NullPointerException {
            Log.d(TAG, "response: ");
            if (obj != null && String.valueOf(obj).length() > 0) {
                String response = String.valueOf(obj);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("result").equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        if (jsonArray.length() > 0) {
                            Gson gson = new Gson();
                            UserDetails userDetails = gson.fromJson(jsonArray.get(0).toString(), UserDetails.class);

                            if (!userDetails.isAccExpires()) {
                                if (userDetails.getMsg().equalsIgnoreCase("Login") && userDetails.isAcc_verified()) {
                                    Toast.makeText(Splash.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();

                                    Intent intent = null;

                                    if (userDetails.getReg_email() == null || !userDetails.isReg_email_verified()) {
                                        intent = new Intent(Splash.this, LoginActivity.class);
//                                        intent.putExtra(LoginVerificationActivity.OPEN_PAGE_FLAG, LoginVerificationActivity.OPEN_EMAIL_VERIFICATION);
                                    } else {
                                        intent = new Intent(Splash.this, ParentActivity.class);
                                    }

                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                    boolean isUpdateReq = Helper.isUpdateReq(Splash.this, userDetails.getCurr_version());
                                    if (isUpdateReq) {
                                        intent.putExtra(Constants.UPDATE_KEYS.UPDATE_REQ_KEY, isUpdateReq);
                                        intent.putExtra(Constants.UPDATE_KEYS.UPDATE_CHANGE_LOG, userDetails.getChangelog());
                                        intent.putExtra(Constants.UPDATE_KEYS.UPDATE_REM_DAYS, userDetails.getRemain_days());
                                    }
                                        intent.putExtra(Constants.TASKS_KEY.IS_TASK_AVL, userDetails.isTasks_avl());

                                    CyientSharePrefrence.setStringInSharePef(Splash.this, SharePrefrenceConstant.REG_EMAIL_ID, userDetails.getReg_email());
                                    CyientSharePrefrence.setBooleanInSharePef(Splash.this, SharePrefrenceConstant.REG_EMAIL_VERIFIED, userDetails.isReg_email_verified());

                                    startActivity(intent);
                                } else {
                                    CyientSharePrefrence.setIntegerValInSharePef(Splash.this, SharePrefrenceConstant.LOGIN_STATUS, Constants.LoginStatus.INACTIVE);
                                    Toast.makeText(Splash.this, getString(R.string.verification_failed), Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(Splash.this, LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }

                            } else {
                                CyientSharePrefrence.setIntegerValInSharePef(Splash.this, SharePrefrenceConstant.LOGIN_STATUS, Constants.LoginStatus.INACTIVE);
                                Toast.makeText(Splash.this, getString(R.string.acc_expires), Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(Splash.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServerError(String message) {
        }
    };
}