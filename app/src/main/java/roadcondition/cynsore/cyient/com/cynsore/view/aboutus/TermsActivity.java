package roadcondition.cynsore.cyient.com.cynsore.view.aboutus;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import roadcondition.cynsore.cyient.com.cynsore.R;


public class TermsActivity extends Activity {

    private Button okBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms_abt_us);
        okBtn = (Button) findViewById(R.id.terms_btn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
