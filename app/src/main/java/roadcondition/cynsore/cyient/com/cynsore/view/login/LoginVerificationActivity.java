package roadcondition.cynsore.cyient.com.cynsore.view.login;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import roadcondition.cynsore.cyient.com.cynsore.view.main.ParentActivity;
import roadcondition.cynsore.cyient.com.cynsore.R;
import roadcondition.cynsore.cyient.com.cynsore.adapter.VerificationPagerAdapter;
import roadcondition.cynsore.cyient.com.cynsore.utility.Constants;

public class LoginVerificationActivity extends AppCompatActivity implements KeyVerificationFragment.OnFragmentInteraction,
        EmailVerificationFragment.OnFragmentInteraction {

    private ViewPager mViewPager;

    public static final int OPEN_EMAIL_VERIFICATION = 1;
    public static final int OPEN_ACTKEY_VERIFICATION = 2;

    public static String OPEN_PAGE_FLAG = "open_page_flag";

    public static final int TASK_OPEN_EMAIL_VERIFICATION = 1;
    public static final int TASK_OPEN_STATSACTIVITY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_parent);

        mViewPager = (ViewPager) findViewById(R.id.ic_view_pager);

        List<Fragment> fragments = new ArrayList<>();

        if (getIntent().getIntExtra(OPEN_PAGE_FLAG, OPEN_ACTKEY_VERIFICATION) == OPEN_EMAIL_VERIFICATION) {
            fragments.add(EmailVerificationFragment.newInstance());
        } else if (getIntent().getIntExtra(OPEN_PAGE_FLAG, OPEN_ACTKEY_VERIFICATION) == OPEN_ACTKEY_VERIFICATION) {
            fragments.add(KeyVerificationFragment.newInstance());
            fragments.add(EmailVerificationFragment.newInstance());
        }

        FragmentManager fm = getSupportFragmentManager();
        VerificationPagerAdapter pagerAdapter = new VerificationPagerAdapter(fm, fragments);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.beginFakeDrag();

    }

    @Override
    public void onFragmentInteract(int task) {
        if (task == TASK_OPEN_EMAIL_VERIFICATION) {
            mViewPager.setCurrentItem(2);
        } else if (task == TASK_OPEN_STATSACTIVITY) {
            Intent intent = new Intent(LoginVerificationActivity.this, ParentActivity.class);

            boolean isUpdateReq = getIntent().getBooleanExtra(Constants.UPDATE_KEYS.UPDATE_REQ_KEY, false);
            if (isUpdateReq) {
                intent.putExtra(Constants.UPDATE_KEYS.UPDATE_REQ_KEY, isUpdateReq);
                intent.putExtra(Constants.UPDATE_KEYS.UPDATE_CHANGE_LOG, getIntent().getStringExtra(Constants.UPDATE_KEYS.UPDATE_CHANGE_LOG));
                intent.putExtra(Constants.UPDATE_KEYS.UPDATE_REM_DAYS, getIntent().getLongExtra(Constants.UPDATE_KEYS.UPDATE_REM_DAYS, 0));
            }
            intent.putExtra(Constants.TASKS_KEY.IS_TASK_AVL, getIntent().getBooleanExtra(Constants.TASKS_KEY.IS_TASK_AVL, false));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

}