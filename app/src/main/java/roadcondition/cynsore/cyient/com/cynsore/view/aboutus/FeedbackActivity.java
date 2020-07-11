package roadcondition.cynsore.cyient.com.cynsore.view.aboutus;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import roadcondition.cynsore.cyient.com.cynsore.R;
import roadcondition.cynsore.cyient.com.cynsore.utility.Constants;
import roadcondition.cynsore.cyient.com.cynsore.network.AsyncServiceDataTask;
import roadcondition.cynsore.cyient.com.cynsore.utility.Util;

public class FeedbackActivity extends Activity implements View.OnClickListener, TextWatcher {
    private ImageView back_feedback;
    //    private RatingBar ratingBar;
//    private TextView ratingText;
    private EditText feedbackText;
    private int ratingInt = 0;
    private Button submit;
    private String urlString;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private String userID;
    private TextView bad, fine, good, great, textlength;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_layout);
        back_feedback = (ImageView) findViewById(R.id.back_feedback);
//        ratingText = (TextView) findViewById(R.id.rating);
//        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        feedbackText = (EditText) findViewById(R.id.feedback_et);
        feedbackText.addTextChangedListener(this);
        submit = (Button) findViewById(R.id.submit_fb);
//        terrible = (TextView) findViewById(R.id.rate1);
        bad = (TextView) findViewById(R.id.rate2);
        fine = (TextView) findViewById(R.id.rate3);
        good = (TextView) findViewById(R.id.rate4);
        great = (TextView) findViewById(R.id.rate5);
        textlength = (TextView) findViewById(R.id.textlength);
        bad.setOnClickListener(this);
        fine.setOnClickListener(this);
        good.setOnClickListener(this);
        great.setOnClickListener(this);
        back_feedback.setOnClickListener(this);
        submit.setOnClickListener(this);
        userID = Util.getPhoneNumber(FeedbackActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_feedback:
                finish();
                break;
            case R.id.submit_fb:
                String feedback = feedbackText.getText().toString();
                if (ratingInt != 0) {
                    if (!feedback.isEmpty() && feedback != null) {
                        sendData(feedback);
                    } else {
                        Toast.makeText(this, "Please enter the Feedback", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "Please select your rating", Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.rate2:
                ratingInt = 1;
//                terrible.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.terrible, 0, 0);
                bad.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.bad_color,
                        0, 0);
                fine.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.fine,
                        0, 0);
                good.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.good,
                        0, 0);
                great.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.great,
                        0, 0);
//                terrible.setTextColor(R.color.color_picker_border_color);
                bad.setTextColor(getResources().getColor(R.color.black));
                fine.setTextColor(getResources().getColor(R.color.color_picker_border_color));
                good.setTextColor(getResources().getColor(R.color.color_picker_border_color));
                great.setTextColor(getResources().getColor(R.color.color_picker_border_color));
                break;
            case R.id.rate3:
                ratingInt = 2;
//                terrible.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.terrible, 0, 0);
                bad.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.bad,
                        0, 0);
                fine.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.fine_color,
                        0, 0);
                good.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.good,
                        0, 0);
                great.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.great,
                        0, 0);
//                terrible.setTextColor(getResources().getColor(R.color.color_picker_border_color));
                bad.setTextColor(getResources().getColor(R.color.color_picker_border_color));
                fine.setTextColor(getResources().getColor(R.color.black));
                good.setTextColor(getResources().getColor(R.color.color_picker_border_color));
                great.setTextColor(getResources().getColor(R.color.color_picker_border_color));
                break;
            case R.id.rate4:
                ratingInt = 3;
//                terrible.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.terrible, 0, 0);
                bad.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.bad,
                        0, 0);
                fine.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.fine,
                        0, 0);
                good.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.good_color,
                        0, 0);
                great.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.great,
                        0, 0);
//                terrible.setTextColor(getResources().getColor(R.color.color_picker_border_color));
                bad.setTextColor(getResources().getColor(R.color.color_picker_border_color));
                fine.setTextColor(getResources().getColor(R.color.color_picker_border_color));
                good.setTextColor(getResources().getColor(R.color.black));
                great.setTextColor(getResources().getColor(R.color.color_picker_border_color));
                break;
            case R.id.rate5:
                ratingInt = 4;
//                terrible.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.terrible, 0, 0);
                bad.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.bad,
                        0, 0);
                fine.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.fine,
                        0, 0);
                good.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.good,
                        0, 0);
                great.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.great_color,
                        0, 0);
//                terrible.setTextColor(getResources().getColor(R.color.color_picker_border_color));
                bad.setTextColor(getResources().getColor(R.color.color_picker_border_color));
                fine.setTextColor(getResources().getColor(R.color.color_picker_border_color));
                good.setTextColor(getResources().getColor(R.color.color_picker_border_color));
                great.setTextColor(getResources().getColor(R.color.black));
                break;
            default:
                break;
        }

    }

    void sendData(String feedback) {
//        // updated from &aMessage= -- fot global ip

 //       urlString = "http://localhost:8020/runnerAtRoad/webresource/myresource/Feedback?MobileNumber=9985362484&Message=fff&Rating=5";
        urlString = "https://iptools.cyient.com/runnerAtRoad/webresource/myresource/Feedback?MobileNumber=" + userID + "&TaskMessage=" +
                feedback + "&Rating=" + ratingInt;

        StringBuilder builder = new StringBuilder();
        builder.append(urlString);
        if (Util.isOnline(FeedbackActivity.this)) {
            AsyncServiceDataTask asyncServiceDataTask = new AsyncServiceDataTask(this, Constants.FEEDBACK_RESPONSE);
            asyncServiceDataTask.execute(builder.toString());
        } else {
            Toast.makeText(FeedbackActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    public void reslut(String s) {
        if (!TextUtils.isEmpty(s)) {
            ratingInt = 0;
            bad.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.bad,
                    0, 0);
            fine.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.fine,
                    0, 0);
            good.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.good,
                    0, 0);
            great.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.great,
                    0, 0);
            bad.setTextColor(getResources().getColor(R.color.color_picker_border_color));
            fine.setTextColor(getResources().getColor(R.color.color_picker_border_color));
            good.setTextColor(getResources().getColor(R.color.color_picker_border_color));
            great.setTextColor(getResources().getColor(R.color.color_picker_border_color));

            feedbackText.setText("");

            Toast.makeText(FeedbackActivity.this, "Thanks, your feedback has sent", Toast
                    .LENGTH_LONG).show();
            Util.setFeedbackFlag(FeedbackActivity.this, true);
            finish();
//            }
        }else{
            Toast.makeText(FeedbackActivity.this, "Something went wrong, please try again", Toast
                    .LENGTH_LONG).show();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int length = s.length();
        String convert = String.valueOf(length);
        textlength.setText(convert + "/500");
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
