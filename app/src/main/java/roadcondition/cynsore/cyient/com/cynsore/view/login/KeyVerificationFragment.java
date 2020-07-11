package roadcondition.cynsore.cyient.com.cynsore.view.login;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import roadcondition.cynsore.cyient.com.cynsore.R;
import roadcondition.cynsore.cyient.com.cynsore.utility.Constants;
import roadcondition.cynsore.cyient.com.cynsore.model.UserDetails;
import roadcondition.cynsore.cyient.com.cynsore.network.ServerAsyncTaskPost;
import roadcondition.cynsore.cyient.com.cynsore.network.ServerHelper;
import roadcondition.cynsore.cyient.com.cynsore.utility.CyientSharePrefrence;
import roadcondition.cynsore.cyient.com.cynsore.utility.Helper;
import roadcondition.cynsore.cyient.com.cynsore.utility.SharePrefrenceConstant;

public class KeyVerificationFragment extends Fragment {

    private EditText mEditActivation;
    private Button mBtnActivation;

    private OnFragmentInteraction mInteraction;

    public KeyVerificationFragment() {
        // Required empty public constructor
    }

    public static KeyVerificationFragment newInstance() {
        KeyVerificationFragment fragment = new KeyVerificationFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_key_verification, container, false);

        mEditActivation = (EditText) view.findViewById(R.id.ic_edit_act_key);
        mBtnActivation = (Button) view.findViewById(R.id.ic_btn_veract_key);

        mBtnActivation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    String actKey = mEditActivation.getText().toString();
                    String emailId = CyientSharePrefrence.getStringFromSharePef(getContext(), SharePrefrenceConstant.EMAIL_ID);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("emailId", emailId);
                    jsonObject.put("activationKey", actKey);
                    jsonObject.put("version", Helper.getVersionName(getContext()));

                    String url = Constants.base_url + "user/validateactivationkey";
                    mVerificationHelper.setUrl(url);
                    ServerAsyncTaskPost serverAsyncTask = new ServerAsyncTaskPost(getContext(), mVerificationHelper);
                    serverAsyncTask.showCallProgress(true);
                    serverAsyncTask.execute(jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    ServerHelper mVerificationHelper = new ServerHelper() {

        @Override
        public void onFailure(Object o) throws NullPointerException {
            Toast.makeText(getContext(), R.string.server_error, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onSuccess(Object obj) throws NullPointerException {
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
                                    Toast.makeText(getContext(), getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                                }

                                CyientSharePrefrence.setStringInSharePef(getContext(), SharePrefrenceConstant.EMAIL_ID, userDetails.getEmail_id());
                                CyientSharePrefrence.setIntegerValInSharePef(getContext(), SharePrefrenceConstant.LOGIN_STATUS, Constants.LoginStatus.ACTIVE);
                                CyientSharePrefrence.setStringInSharePef(getContext(), SharePrefrenceConstant.PASSW_HASH, userDetails.getPassw_hash());
                                CyientSharePrefrence.setStringInSharePef(getContext(), SharePrefrenceConstant.NAME, userDetails.getName());
                                CyientSharePrefrence.setStringInSharePef(getContext(), SharePrefrenceConstant.MOB_NUM, userDetails.getMob_num());
                                String login_domain = getActivity().getIntent().getStringExtra("login_domain");
                                CyientSharePrefrence.setStringInSharePef(getContext(), SharePrefrenceConstant.LOGIN_DOMAIN, login_domain);

                                mInteraction.onFragmentInteract(LoginVerificationActivity.TASK_OPEN_EMAIL_VERIFICATION);

                                boolean isUpdateReq = Helper.isUpdateReq(getActivity(), userDetails.getCurr_version());
                                if (isUpdateReq) {
                                    getActivity().getIntent().putExtra(Constants.UPDATE_KEYS.UPDATE_REQ_KEY, isUpdateReq);
                                    getActivity().getIntent().putExtra(Constants.UPDATE_KEYS.UPDATE_CHANGE_LOG, userDetails.getChangelog());
                                    getActivity().getIntent().putExtra(Constants.UPDATE_KEYS.UPDATE_REM_DAYS, userDetails.getRemain_days());
                                }
                                getActivity().getIntent().putExtra(Constants.TASKS_KEY.IS_TASK_AVL, userDetails.isTasks_avl());
                            } else {
                                Toast.makeText(getContext(), getString(R.string.acc_expires), Toast.LENGTH_LONG).show();
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
            Toast.makeText(getContext(), R.string.server_error, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mInteraction = (OnFragmentInteraction) context;
    }

    interface OnFragmentInteraction {
        public void onFragmentInteract(int task);
    }

}