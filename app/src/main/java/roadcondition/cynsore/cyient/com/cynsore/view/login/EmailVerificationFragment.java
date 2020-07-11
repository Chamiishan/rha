package roadcondition.cynsore.cyient.com.cynsore.view.login;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import roadcondition.cynsore.cyient.com.cynsore.R;
import roadcondition.cynsore.cyient.com.cynsore.utility.Constants;
import roadcondition.cynsore.cyient.com.cynsore.network.ServerAsyncTaskPost;
import roadcondition.cynsore.cyient.com.cynsore.network.ServerHelper;
import roadcondition.cynsore.cyient.com.cynsore.utility.CyientSharePrefrence;
import roadcondition.cynsore.cyient.com.cynsore.utility.Helper;
import roadcondition.cynsore.cyient.com.cynsore.utility.SharePrefrenceConstant;

public class EmailVerificationFragment extends Fragment implements View.OnClickListener {

    private LinearLayout mlinVerifyEmail, mlinVerifyCode, mlinMsg;
    private EditText mEditRegEmail, mEditVerCode;
    private Button mBtnSubmit, mBtnVerify;
    private TextView mTxtMsgs;

    private static final String TAG = "EmailVerification";

    private KeyVerificationFragment.OnFragmentInteraction mInteraction;

    public EmailVerificationFragment() {
        // Required empty public constructor
    }

    public static EmailVerificationFragment newInstance() {
        EmailVerificationFragment fragment = new EmailVerificationFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_verify_email, container, false);

        mlinVerifyEmail = (LinearLayout) view.findViewById(R.id.lin_verify_email);
        mlinVerifyCode = (LinearLayout) view.findViewById(R.id.lin_verify_code);
        mlinMsg = (LinearLayout) view.findViewById(R.id.lin_msg);
        mTxtMsgs = (TextView) view.findViewById(R.id.ic_txt_msg);
        mEditRegEmail = (EditText) view.findViewById(R.id.ic_edit_regemail);
        mEditVerCode = (EditText) view.findViewById(R.id.ic_edit_vercode);
        mBtnSubmit = (Button) view.findViewById(R.id.ic_btn_submit);
        mBtnVerify = (Button) view.findViewById(R.id.ic_btn_verify);

        mBtnVerify.setOnClickListener(this);
        mBtnSubmit.setOnClickListener(this);

        String regEmail = CyientSharePrefrence.getStringFromSharePef(getContext(), SharePrefrenceConstant.REG_EMAIL_ID);
        if (regEmail == null) {
            mlinVerifyCode.setVisibility(View.GONE);
        } else {
            Boolean regEmailVerified = CyientSharePrefrence.getBoolenValSharePef(getContext(), SharePrefrenceConstant.REG_EMAIL_VERIFIED);

            if (!regEmailVerified) {
                mlinVerifyEmail.setVisibility(View.GONE);
                mTxtMsgs.setText(Html.fromHtml("<h2>check your mail</h2></br>" +
                        "<p>Please verify your email using code which is sent to your email address: " +
                        "<b>" + regEmail + "</b></p>"));
                mlinMsg.setVisibility(View.VISIBLE);
                mlinVerifyCode.setVisibility(View.VISIBLE);
            }
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ic_btn_submit:
                String regEmail = mEditRegEmail.getText().toString();

                if (!TextUtils.isEmpty(regEmail) && Patterns.EMAIL_ADDRESS.matcher(regEmail).matches()) {
                    storeRegEmail(regEmail);
                } else {
                    mEditRegEmail.setError(getString(R.string.err_invalid_email));
                }
                break;
            case R.id.ic_btn_verify:
                String verCode = mEditVerCode.getText().toString();
                if (verCode != null && !TextUtils.isEmpty(verCode)) {
                    doEmailVerification(verCode);
                } else {
                    mEditVerCode.setError(getString(R.string.err_empty_code));
                }
                break;
        }
    }


    private void doEmailVerification(String verificationCode) {
        ServerAsyncTaskPost taskPost = new ServerAsyncTaskPost(getContext(), mVerifyCode);
        mVerifyCode.setUrl(Constants.base_url + "user/verifyemail");
        taskPost.showCallProgress(true);
        String email = CyientSharePrefrence.getStringFromSharePef(getContext(), SharePrefrenceConstant.REG_EMAIL_ID);
        JSONObject json = new JSONObject();
        try {
            json.put("verify_code", verificationCode);
            json.put("reg_email", email);

            taskPost.execute(json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void storeRegEmail(String emailAdd) {
        ServerAsyncTaskPost taskPost = new ServerAsyncTaskPost(getContext(), mEmailVerification);
        mEmailVerification.setUrl(Constants.base_url + "user/doemailregister");
        taskPost.showCallProgress(true);
        String email = CyientSharePrefrence.getStringFromSharePef(getContext(), SharePrefrenceConstant.EMAIL_ID);
        JSONObject json = new JSONObject();
        try {
            json.put("email_id", email);
            json.put("reg_email", emailAdd);

            taskPost.execute(json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    ServerHelper mEmailVerification = new ServerHelper() {

        @Override
        public void onFailure(Object o) throws NullPointerException {
        }

        @Override
        public void onSuccess(Object obj) throws NullPointerException {
            if (obj != null) {
                String response = String.valueOf(obj);
                if (response.length() > 0) {
                    try {
                        JSONObject json = new JSONObject(response);
                        String result = json.optString("result");
                        if (result.equalsIgnoreCase("success")) {
                            String regEmail = mEditRegEmail.getText().toString();
                            CyientSharePrefrence.setStringInSharePef(getContext(), SharePrefrenceConstant.REG_EMAIL_ID, regEmail);
                            CyientSharePrefrence.setBooleanInSharePef(getContext(), SharePrefrenceConstant.REG_EMAIL_VERIFIED, false);

                            mlinVerifyEmail.setVisibility(View.GONE);

                            mTxtMsgs.setText(Html.fromHtml("<h2>check your mail</h2></br>" +
                                    "<p>Please verify your email using code which is sent to your email address: " +
                                    "<b>" + regEmail + "</b></p>"));

                            mlinMsg.setVisibility(View.VISIBLE);
                            mlinVerifyCode.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Helper.printLogMsg(TAG, response);
            }
        }

        @Override
        public void onServerError(String message) {
        }

    };

    ServerHelper mVerifyCode = new ServerHelper() {

        @Override
        public void onFailure(Object o) throws NullPointerException {
        }

        @Override
        public void onSuccess(Object obj) throws NullPointerException {
            if (obj != null) {
                String response = String.valueOf(obj);
                if (response.length() > 0) {
                    try {
                        JSONObject json = new JSONObject(response);
                        String result = json.getString("result");
                        if (result.equalsIgnoreCase("success")) {
                            Toast.makeText(getContext(), getString(R.string.email_verified), Toast.LENGTH_LONG).show();
                            mInteraction.onFragmentInteract(LoginVerificationActivity.TASK_OPEN_STATSACTIVITY);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void onServerError(String message) {
        }

    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mInteraction = (KeyVerificationFragment.OnFragmentInteraction) context;
    }

    interface OnFragmentInteraction {
        public void onFragmentInteract(int task);
    }

}