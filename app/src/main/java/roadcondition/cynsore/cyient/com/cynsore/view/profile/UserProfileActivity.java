package roadcondition.cynsore.cyient.com.cynsore.view.profile;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import roadcondition.cynsore.cyient.com.cynsore.R;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView user_img;
    private TextView name_tv, id_tv, email_tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);

        Toolbar toolbar = findViewById(R.id.ic_toolbar);
        toolbar.setTitle(getString(R.string.user_info));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name_tv = (TextView) findViewById(R.id.user_name);
        email_tv = (TextView) findViewById(R.id.user_email);
        id_tv = (TextView) findViewById(R.id.user_id);


        user_img = (ImageView) findViewById(R.id.user_img);

        SharedPreferences prefs = getSharedPreferences("Cyient", MODE_PRIVATE);
        final String value = prefs.getString("email_id", "0");
        final String value1 = prefs.getString("name", "0");
        final String value2 = prefs.getString("date_of_expiry", "0");

        name_tv.setText(value);
        id_tv.setText(value1);
        email_tv.setText(value2);
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
    }

}