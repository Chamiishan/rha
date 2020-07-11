package roadcondition.cynsore.cyient.com.cynsore.view.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import roadcondition.cynsore.cyient.com.cynsore.view.main.ParentActivity;
import roadcondition.cynsore.cyient.com.cynsore.R;
import roadcondition.cynsore.cyient.com.cynsore.utility.Constants;
import roadcondition.cynsore.cyient.com.cynsore.model.UserDetails;
import roadcondition.cynsore.cyient.com.cynsore.network.ServerAsyncTaskPost;
import roadcondition.cynsore.cyient.com.cynsore.network.ServerHelper;
import roadcondition.cynsore.cyient.com.cynsore.utility.CyientSharePrefrence;
import roadcondition.cynsore.cyient.com.cynsore.utility.Helper;
import roadcondition.cynsore.cyient.com.cynsore.utility.SharePrefrenceConstant;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private GoogleSignInClient mGoogleSign;
    private int mSignInReqCode = 90;
    //    private TextView mTxtSignin, mTxtSignUp;
    private EditText mEditUname, mEditPassw;
    private Button mBtnSubmit;
    private String login_domain = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        Helper.registerGCM(LoginActivity.this);

        mEditUname = (EditText) findViewById(R.id.ic_edit_uname);
        mEditPassw = (EditText) findViewById(R.id.ic_edit_passw);

        mBtnSubmit = (Button) findViewById(R.id.ic_btn_signin);
        mBtnSubmit.setOnClickListener(this);

//        int loginstatus = CyientSharePrefrence.getIntegerFromSharePef(LoginActivity.this, SharePrefrenceConstant.LOGIN_STATUS, Constants.LoginStatus.INACTIVE);

//        if (loginstatus == Constants.LoginStatus.INACTIVE) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestServerAuthCode("845802393489-s55ulm6rkaf0ga0lkn5rrcumtmksju2v.apps.googleusercontent.com")
                .build();

        mGoogleSign = GoogleSignIn.getClient(this, gso);
        mGoogleSign.signOut();

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login_domain = Constants.LoginDomain.gmail;
                signIn();
            }
        });
//        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSign.getSignInIntent();
        startActivityForResult(signInIntent, mSignInReqCode);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == mSignInReqCode) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String id = account.getId();
            String name = account.getDisplayName();
            String email = account.getEmail();
            String mobNum = "";

            loginOnServer(email, id, name, mobNum);

            // Signed in successfully, show authenticated UI.
            // updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code = " + e.getStatusCode());
//            updateUI(null);
        }
    }

    private void loginOnServer(String emailId, String passwh, String name, String mobNum) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email_id", emailId);
            jsonObject.put("passw_hash", passwh);
            if (name != null) {
                jsonObject.put("name", name);
            }
            if (mobNum != null) {
                jsonObject.put("mob_num", mobNum);
            }
            String regId = CyientSharePrefrence.getStringFromSharePef(LoginActivity.this, SharePrefrenceConstant.REG_ID);
            jsonObject.put("gcm_id", regId);
            jsonObject.put("login_domain", login_domain);
            jsonObject.put("version", Helper.getVersionName(this));
            String url = Constants.base_url + "user/doregisterorlogin";
            mLoginRegistrationHelper.setUrl(url);
            ServerAsyncTaskPost serverAsyncTask = new ServerAsyncTaskPost(LoginActivity.this, mLoginRegistrationHelper);
            serverAsyncTask.showCallProgress(true);
            serverAsyncTask.execute(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    ServerHelper mLoginRegistrationHelper = new ServerHelper() {

        @Override
        public void onFailure(Object o) throws NullPointerException {
            Toast.makeText(LoginActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
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
                                if (userDetails.getMsg().equalsIgnoreCase("Login")) {
                                    Toast.makeText(LoginActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();

                                    CyientSharePrefrence.setStringInSharePef(LoginActivity.this, SharePrefrenceConstant.EMAIL_ID, userDetails.getEmail_id());
                                    if (userDetails.isAcc_verified()) {
                                        CyientSharePrefrence.setIntegerValInSharePef(LoginActivity.this, SharePrefrenceConstant.LOGIN_STATUS, Constants.LoginStatus.ACTIVE);
                                        CyientSharePrefrence.setStringInSharePef(LoginActivity.this, SharePrefrenceConstant.EMAIL_ID, userDetails.getEmail_id());
                                        CyientSharePrefrence.setStringInSharePef(LoginActivity.this, SharePrefrenceConstant.PASSW_HASH, userDetails.getPassw_hash());
                                        CyientSharePrefrence.setStringInSharePef(LoginActivity.this, SharePrefrenceConstant.NAME, userDetails.getName());
                                        CyientSharePrefrence.setStringInSharePef(LoginActivity.this, SharePrefrenceConstant.MOB_NUM, userDetails.getMob_num());
                                        CyientSharePrefrence.setStringInSharePef(LoginActivity.this, SharePrefrenceConstant.Date_Of_Expiry, userDetails.getDate_of_expiry());
                                        CyientSharePrefrence.setStringInSharePef(LoginActivity.this, SharePrefrenceConstant.LOGIN_DOMAIN, login_domain);

//                                        Intent intent = new Intent(LoginActivity.this, StatsActivity.class);
                                        Intent intent = null;

                                        if (userDetails.getReg_email() == null || !userDetails.isReg_email_verified()) {
                                            intent = new Intent(LoginActivity.this, LoginVerificationActivity.class);
                                            intent.putExtra(LoginVerificationActivity.OPEN_PAGE_FLAG, LoginVerificationActivity.OPEN_EMAIL_VERIFICATION);
                                        } else {
                                            intent = new Intent(LoginActivity.this, ParentActivity.class);
                                        }

                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                        boolean isUpdateReq = Helper.isUpdateReq(LoginActivity.this, userDetails.getCurr_version());
                                        if (isUpdateReq) {
                                            intent.putExtra(Constants.UPDATE_KEYS.UPDATE_REQ_KEY, isUpdateReq);
                                            intent.putExtra(Constants.UPDATE_KEYS.UPDATE_CHANGE_LOG, userDetails.getChangelog());
                                            intent.putExtra(Constants.UPDATE_KEYS.UPDATE_REM_DAYS, userDetails.getRemain_days());
                                        }
                                        intent.putExtra(Constants.TASKS_KEY.IS_TASK_AVL, userDetails.isTasks_avl());

                                        CyientSharePrefrence.setStringInSharePef(LoginActivity.this, SharePrefrenceConstant.REG_EMAIL_ID, userDetails.getReg_email());
                                        CyientSharePrefrence.setBooleanInSharePef(LoginActivity.this, SharePrefrenceConstant.REG_EMAIL_VERIFIED, userDetails.isReg_email_verified());

                                        startActivity(intent);
                                    } else {
                                        CyientSharePrefrence.setIntegerValInSharePef(LoginActivity.this, SharePrefrenceConstant.LOGIN_STATUS, Constants.LoginStatus.INACTIVE);
                                        CyientSharePrefrence.setStringInSharePef(LoginActivity.this, SharePrefrenceConstant.EMAIL_ID, userDetails.getEmail_id());

                                        Intent intent = new Intent(LoginActivity.this, LoginVerificationActivity.class);
                                        intent.putExtra(LoginVerificationActivity.OPEN_PAGE_FLAG, LoginVerificationActivity.OPEN_ACTKEY_VERIFICATION);
                                        intent.putExtra("login_domain", login_domain);
                                        startActivity(intent);
                                    }
                                } else if (userDetails.getMsg().equalsIgnoreCase("Register")) {
                                    CyientSharePrefrence.setIntegerValInSharePef(LoginActivity.this, SharePrefrenceConstant.LOGIN_STATUS, Constants.LoginStatus.INACTIVE);
                                    CyientSharePrefrence.setStringInSharePef(LoginActivity.this, SharePrefrenceConstant.EMAIL_ID, userDetails.getEmail_id());

                                    Toast.makeText(LoginActivity.this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();
                                    if (userDetails.isAcc_verified()) {
                                        CyientSharePrefrence.setIntegerValInSharePef(LoginActivity.this, SharePrefrenceConstant.LOGIN_STATUS, Constants.LoginStatus.ACTIVE);
                                        CyientSharePrefrence.setStringInSharePef(LoginActivity.this, SharePrefrenceConstant.EMAIL_ID, userDetails.getEmail_id());
                                        CyientSharePrefrence.setStringInSharePef(LoginActivity.this, SharePrefrenceConstant.PASSW_HASH, userDetails.getPassw_hash());
                                        CyientSharePrefrence.setStringInSharePef(LoginActivity.this, SharePrefrenceConstant.NAME, userDetails.getName());
                                        CyientSharePrefrence.setStringInSharePef(LoginActivity.this, SharePrefrenceConstant.MOB_NUM, userDetails.getMob_num());
                                        CyientSharePrefrence.setStringInSharePef(LoginActivity.this, SharePrefrenceConstant.LOGIN_DOMAIN, login_domain);

//                                        Intent intent = new Intent(LoginActivity.this, StatsActivity.class);
                                        Intent intent = new Intent(LoginActivity.this, LoginVerificationActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra(LoginVerificationActivity.OPEN_PAGE_FLAG, LoginVerificationActivity.OPEN_EMAIL_VERIFICATION);
                                        boolean isUpdateReq = Helper.isUpdateReq(LoginActivity.this, userDetails.getCurr_version());
                                        if (isUpdateReq) {
                                            intent.putExtra(Constants.UPDATE_KEYS.UPDATE_REQ_KEY, isUpdateReq);
                                            intent.putExtra(Constants.UPDATE_KEYS.UPDATE_CHANGE_LOG, userDetails.getChangelog());
                                            intent.putExtra(Constants.UPDATE_KEYS.UPDATE_REM_DAYS, userDetails.getRemain_days());
                                        }
                                        intent.putExtra(Constants.TASKS_KEY.IS_TASK_AVL, userDetails.isTasks_avl());
                                        startActivity(intent);
                                    } else {
                                        showRegAlert("Registration Successful", jsonObject.getString("message"));
                                    }
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.acc_expires), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else if (jsonObject.getString("result").equalsIgnoreCase("failure")) {
                        mGoogleSign.signOut();
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        if (jsonArray.length() > 0) {
                            Gson gson = new Gson();
                            UserDetails userDetails = gson.fromJson(jsonArray.get(0).toString(), UserDetails.class);
                            showErrorAlert("Failure", userDetails.getMsg());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServerError(String message) {
            Toast.makeText(LoginActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
        }
    };

    private void showErrorAlert(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this).setTitle(title)
                .setMessage(msg).setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }

    private void showRegAlert(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this).setTitle(title)
                .setMessage(msg).setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(LoginActivity.this, LoginVerificationActivity.class);
                        intent.putExtra(LoginVerificationActivity.OPEN_PAGE_FLAG, LoginVerificationActivity.OPEN_ACTKEY_VERIFICATION);
                        intent.putExtra("login_domain", login_domain);
                        startActivity(intent);
                    }
                });
        builder.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ic_btn_signin:
                login_domain = Constants.LoginDomain.cyient;
                String txtUname = mEditUname.getText().toString();
                String txtPassw = mEditPassw.getText().toString();

                boolean isValidated = validateLoginCred(txtUname, txtPassw);

                if (isValidated) {
                    try {
                        ServerAsyncTaskPost taskPost = new ServerAsyncTaskPost(LoginActivity.this, mCheckCyientCred);

                        mCheckCyientCred.setUrl(Constants.test_cyient_login);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("UserName", txtUname);
                        jsonObject.put("Password", txtPassw);

                        mCheckCyientCred.setTag(jsonObject);
                        taskPost.execute(jsonObject.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
//                boolean isValidated = false;
//                if (login_type == Constants.Logintype.SIGNUP) {
//                    txtConfPassw = mEditConfPassw.getText().toString();
//                    txtName = mEditName.getText().toString();
//                    txtPhNum = mEditPhNum.getText().toString();
//                    isValidated = validateRegCred(txtEmail, txtPassw, txtConfPassw, txtName, txtPhNum);
//                } else {
//                    isValidated = validateLoginCred(txtEmail, txtPassw);
//                }
//
//                if (isValidated) {
//                    loginOnServer(txtEmail, txtPassw, txtName, txtConfPassw, login_type);
//                }

                break;
        }
    }

//    private boolean validateRegCred(String txtEmail, String txtPassw, String txtConfPassw, String txtName, String txtPhNum) {
//        boolean validation = true;
//
//        boolean validEmail = validCyientMailAdd(txtEmail, mEditUname);
//        if (!validEmail) {
//            validation = false;
//        }
//        boolean validPassw = validatePassw(txtPassw, mEditPassw);
//        if (!validPassw) {
//            validation = false;
//        }
//
//
//        return validation;
//    }

    private boolean validateLoginCred(String txtEmail, String txtPassw) {

        boolean validation = true;

        boolean validEmail = validateUname(txtEmail, mEditUname);
        if (!validEmail) {
            validation = false;
        }
        boolean validPassw = validatePassw(txtPassw, mEditPassw);
        if (!validPassw) {
            validation = false;
        }

        return validation;
    }

//    private boolean validCyientMailAdd(String email, EditText editEmail) {
//        Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
//                "\\@" +
//                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
//                "(" +
//                "\\." +
//                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
//                ")+");
//        boolean validAdd = EMAIL_ADDRESS_PATTERN.matcher(email).matches();
//
//        editEmail.setError(null);
//        if (validAdd) {
//            String domain = email.substring(email.indexOf('@') + 1);
//            if (domain.equalsIgnoreCase("cyient.com")) {
//                return true;
//            } else {
//                editEmail.setError(getString(R.string.err_non_cyient_email));
//            }
//        } else {
//            editEmail.setError(getString(R.string.err_invalid_email));
//        }
//        return false;
//    }

    private boolean validateUname(String uname, EditText editConfPassw) {
        if (uname != null && uname.length() > 0) {
            return true;
        }
        editConfPassw.setError(null);
        editConfPassw.setError(getString(R.string.err_uname_empty));
        return false;
    }

    private boolean validatePassw(String password, EditText editConfPassw) {
        if (password != null && password.length() > 0) {
            if (password.length() < 4) {
                editConfPassw.setError(null);
                editConfPassw.setError(getString(R.string.err_passw_short));
                return false;
            } else {
                return true;
            }
        }
        editConfPassw.setError(null);
        editConfPassw.setError(getString(R.string.err_password));
        return false;
    }

//    private boolean isValidMobNum(String phNum, EditText editPhNum) {
//        String regexStr = "^[+]?[0-9]{10,13}$";
//        if (phNum != null && phNum.length() > 6 && phNum.length() <= 13 && phNum.matches(regexStr)) {
//        } else {
//            if (phNum.length() == 0) {
//                editPhNum.setError(getString(R.string.err_phnum_empty));
//            } else if (phNum.length() < 6 || phNum.length() > 13 || !phNum.matches(regexStr)) {
//                editPhNum.setError(getString(R.string.err_invalid_phnum));
//            }
//            return false;
//        }
//        return true;
//    }

    ServerHelper mCheckCyientCred = new ServerHelper() {

        @Override
        public void onFailure(Object o) throws NullPointerException {
        }

        @Override
        public void onSuccess(Object o) throws NullPointerException {
            if (o != null && o.toString().length() > 0 && o.toString().equalsIgnoreCase("true\n")) {
                Helper.printLogMsg(TAG, "credentials match");
                try {
                    JSONObject jsonObject = (JSONObject) mCheckCyientCred.getTag();
                    String uname = jsonObject.getString("UserName");
                    String passw = jsonObject.getString("Password");
                    loginOnServer(uname, passw, "", "");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Snackbar.make(mEditUname, getString(R.string.err_cred_notmatch), Snackbar.LENGTH_LONG).show();
            }
        }

        @Override
        public void onServerError(String message) {
        }
    };

}