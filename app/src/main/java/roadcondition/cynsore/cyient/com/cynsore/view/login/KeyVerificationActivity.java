//package roadcondition.cynsore.cyient.com.cynsore.view.login;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.google.gson.Gson;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import roadcondition.cynsore.cyient.com.cynsore.view.main.ParentActivity;
//import roadcondition.cynsore.cyient.com.cynsore.R;
//import roadcondition.cynsore.cyient.com.cynsore.utility.Constants;
//import roadcondition.cynsore.cyient.com.cynsore.model.UserDetails;
//import roadcondition.cynsore.cyient.com.cynsore.network.ServerAsyncTaskPost;
//import roadcondition.cynsore.cyient.com.cynsore.network.ServerHelper;
//import roadcondition.cynsore.cyient.com.cynsore.utility.CyientSharePrefrence;
//import roadcondition.cynsore.cyient.com.cynsore.utility.Helper;
//import roadcondition.cynsore.cyient.com.cynsore.utility.SharePrefrenceConstant;
//
//public class KeyVerificationActivity extends AppCompatActivity {
//
//    private EditText mEditActivation;
//    private Button mBtnActivation;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_key_verification);
//
//        mEditActivation = (EditText) findViewById(R.id.ic_edit_act_key);
//        mBtnActivation = (Button) findViewById(R.id.ic_btn_veract_key);
//
//        mBtnActivation.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                try {
//                    String actKey = mEditActivation.getText().toString();
//                    String emailId = CyientSharePrefrence.getStringFromSharePef(KeyVerificationActivity.this, SharePrefrenceConstant.EMAIL_ID);
//
//                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put("emailId", emailId);
//                    jsonObject.put("activationKey", actKey);
//                    jsonObject.put("version", Helper.getVersionName(KeyVerificationActivity.this));
//
//                    String url = Constants.base_url + "user/validateactivationkey";
//                    mVerificationHelper.setUrl(url);
//                    ServerAsyncTaskPost serverAsyncTask = new ServerAsyncTaskPost(KeyVerificationActivity.this, mVerificationHelper);
//                    serverAsyncTask.showCallProgress(true);
//                    serverAsyncTask.execute(jsonObject.toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//    ServerHelper mVerificationHelper = new ServerHelper() {
//
//        @Override
//        public void onFailure(Object o) throws NullPointerException {
//            Toast.makeText(KeyVerificationActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
//        }
//
//        @Override
//        public void onSuccess(Object obj) throws NullPointerException {
//            if (obj != null && String.valueOf(obj).length() > 0) {
//                String response = String.valueOf(obj);
//
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//
//                    if (jsonObject.getString("result").equalsIgnoreCase("success")) {
//                        JSONArray jsonArray = jsonObject.getJSONArray("data");
//
//                        if (jsonArray.length() > 0) {
//                            Gson gson = new Gson();
//                            UserDetails userDetails = gson.fromJson(jsonArray.get(0).toString(), UserDetails.class);
//
//                            if (!userDetails.isAccExpires()) {
//                                if (userDetails.getMsg().equalsIgnoreCase("Login")) {
//                                    Toast.makeText(KeyVerificationActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
//                                }
//
//                                CyientSharePrefrence.setStringInSharePef(KeyVerificationActivity.this, SharePrefrenceConstant.EMAIL_ID, userDetails.getEmail_id());
//                                CyientSharePrefrence.setIntegerValInSharePef(KeyVerificationActivity.this, SharePrefrenceConstant.LOGIN_STATUS, Constants.LoginStatus.ACTIVE);
//                                CyientSharePrefrence.setStringInSharePef(KeyVerificationActivity.this, SharePrefrenceConstant.PASSW_HASH, userDetails.getPassw_hash());
//                                CyientSharePrefrence.setStringInSharePef(KeyVerificationActivity.this, SharePrefrenceConstant.NAME, userDetails.getName());
//                                CyientSharePrefrence.setStringInSharePef(KeyVerificationActivity.this, SharePrefrenceConstant.MOB_NUM, userDetails.getMob_num());
//                                String login_domain = getIntent().getStringExtra("login_domain");
//                                CyientSharePrefrence.setStringInSharePef(KeyVerificationActivity.this, SharePrefrenceConstant.LOGIN_DOMAIN, login_domain);
//
//
//                                Intent intent = new Intent(KeyVerificationActivity.this, ParentActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);
//
//                            } else {
//                                Toast.makeText(KeyVerificationActivity.this, getString(R.string.acc_expires), Toast.LENGTH_LONG).show();
//                            }
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//
//        @Override
//        public void onServerError(String message) {
//            Toast.makeText(KeyVerificationActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
//        }
//    };
//
//}