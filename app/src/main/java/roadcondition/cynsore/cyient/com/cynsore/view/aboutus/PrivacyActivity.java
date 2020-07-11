package roadcondition.cynsore.cyient.com.cynsore.view.aboutus;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import roadcondition.cynsore.cyient.com.cynsore.R;
import roadcondition.cynsore.cyient.com.cynsore.utility.Constants;
import roadcondition.cynsore.cyient.com.cynsore.utility.Util;

public class PrivacyActivity extends Activity {
    TextView toWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_abt_us);
        toWeb = (TextView) findViewById(R.id.toWeb);
        toWeb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Util.isOnline(PrivacyActivity.this)) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(Constants.WEB_URL_CYIENT));
                    startActivity(i);
                } else {
                    Toast.makeText(PrivacyActivity.this, "It seems to be, there is no Network. \n" +
                            " Please try later", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}