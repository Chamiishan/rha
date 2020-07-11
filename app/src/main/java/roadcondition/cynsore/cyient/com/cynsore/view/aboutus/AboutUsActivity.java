package roadcondition.cynsore.cyient.com.cynsore.view.aboutus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import roadcondition.cynsore.cyient.com.cynsore.R;
import roadcondition.cynsore.cyient.com.cynsore.utility.Helper;

public class AboutUsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView feedback, privacyPolicy, termsCond;
    private static final String TAG = "AboutUsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutus);

        Toolbar toolbar = findViewById(R.id.ic_toolbar);
        toolbar.setTitle(getString(R.string.abt_us));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        feedback = findViewById(R.id.feedback);
        privacyPolicy = findViewById(R.id.privacy_policy);
        termsCond = findViewById(R.id.terms_cond);
        feedback.setOnClickListener(this);
        privacyPolicy.setOnClickListener(this);
        termsCond.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.feedback:
                Intent feedbackIntent = new Intent(this, FeedbackActivity.class);
                startActivity(feedbackIntent);
                break;
            case R.id.privacy_policy:
                Intent privacyIntent = new Intent(this, PrivacyActivity.class);
                startActivity(privacyIntent);
                break;
            case R.id.terms_cond:
                Intent termIntent = new Intent(this, TermsActivity.class);
                startActivity(termIntent);
                break;
            default:
                break;
        }
    }
}