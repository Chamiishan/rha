package roadcondition.cynsore.cyient.com.cynsore.view.main;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import roadcondition.cynsore.cyient.com.cynsore.R;
import roadcondition.cynsore.cyient.com.cynsore.RHAApplication;
import roadcondition.cynsore.cyient.com.cynsore.model.RHATask;
import roadcondition.cynsore.cyient.com.cynsore.utility.Constants;
import roadcondition.cynsore.cyient.com.cynsore.utility.CyientSharePrefrence;
import roadcondition.cynsore.cyient.com.cynsore.utility.Helper;
import roadcondition.cynsore.cyient.com.cynsore.view.aboutus.AboutUsActivity;
import roadcondition.cynsore.cyient.com.cynsore.view.censor.StatsFragment;
import roadcondition.cynsore.cyient.com.cynsore.view.dashboard.DashboardActivity;
import roadcondition.cynsore.cyient.com.cynsore.view.login.LoginActivity;
import roadcondition.cynsore.cyient.com.cynsore.view.profile.UserProfileActivity;
import roadcondition.cynsore.cyient.com.cynsore.view.tasks.TaskDetailParent;
import roadcondition.cynsore.cyient.com.cynsore.view.tasks.TaskListFragment;
import roadcondition.cynsore.cyient.com.cynsore.viewmodel.task.TaskMessage;
import roadcondition.cynsore.cyient.com.cynsore.viewmodel.task.TaskViewModel;

public class ParentActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private FrameLayout mFrame;
    private StatsFragment mStatsFragment;
    private TaskListFragment mTaskListFragment;
    private TaskDetailParent mDetailFragment;

    private NavigationView mNavigationView;
    private DrawerLayout mDrawer;
    private TaskViewModel mTaskViewModel;

    private View mActivityView;

    private Toolbar mToolbar;

    private static final String TAG = "ParentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();
        setContentView(R.layout.activity_parent);

        mToolbar = findViewById(R.id.ic_toolbar);
        mToolbar.setTitle("Road Health");

        setSupportActionBar(mToolbar);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActivityView = findViewById(R.id.activity_parent);

        mFrame = findViewById(R.id.main_frag);
//        mFrame = getSupportFragmentManager().findFragmentById(R.id.main_frag);

        mTaskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        mTaskViewModel.init();
        addObserver();

        mStatsFragment = new StatsFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(mFrame.getId(), mStatsFragment);
        ft.commit();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.getMenu().getItem(0).setChecked(true);

        mDrawer = (DrawerLayout) findViewById(R.id.activity_parent);
        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Helper.printLogMsg(TAG, "home");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addObserver() {

        mTaskViewModel.getMessage().observe(this, item -> {

            switch (item.getMsg()) {
                case TaskMessage.SHOW_TASK_DETAIL:
                    Object tag = item.getTag();
                    if (tag instanceof RHATask && item.getFromClass().equals(TaskListFragment.class.getName())) {
                        if (mDetailFragment == null) {
                            mDetailFragment = new TaskDetailParent();
                        }

//                        TempFragment tempFragment = new TempFragment();
                        if (!mDetailFragment.isVisible()) {
//                        if (!tempFragment.isVisible()) {
                            int bsNum = getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                                            R.anim.slide_in_left, R.anim.slide_out_right)
                                    .replace(mFrame.getId(), mDetailFragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }
                    break;
                case TaskMessage.SHOW_TASK_LIST:
                    if (mTaskListFragment == null) {
                        mTaskListFragment = new TaskListFragment();
                    }

                    if (!mTaskListFragment.isVisible()) {
                        int bsNum1 = getSupportFragmentManager().beginTransaction()
//                                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right,
//                                        R.anim.slide_in_right, R.anim.slide_out_left)
                                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                                .replace(mFrame.getId(), mTaskListFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                    break;
                case TaskMessage.CAPTURE_SENSOR:
                    if (mStatsFragment == null) {
                        mStatsFragment = new StatsFragment();
                    }

                    if (!mStatsFragment.isVisible()) {
                        mStatsFragment.setData((RHATask) item.getTag());
                        int bsNum2 = getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                                        R.anim.slide_in_left, R.anim.slide_out_right)
                                .replace(mFrame.getId(), mStatsFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                    break;
                case TaskMessage.POP_STACK:
                    getSupportFragmentManager().popBackStack();
                    break;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mNavigationView.getMenu().getItem(7).isChecked()) {
            if (mTaskListFragment.isVisible()) {
                mNavigationView.getMenu().getItem(0).setChecked(true);
            }
            if (mStatsFragment.isVisible()) {
                mStatsFragment.showEndTripAlert();
            } else {
                super.onBackPressed();
            }
        } else {
            int mode = ((RHAApplication) getApplication()).getMODE();
            if (mode == Constants.MODE.VIEW_MODE) {
                super.onBackPressed();
            } else {
                mStatsFragment.onBackPressedFrag();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mStatsFragment.onActivityResultFrag(requestCode, resultCode, data);
    }

    public NavigationView getNavigationView() {
        return mNavigationView;
    }

    public DrawerLayout getDrawer() {
        return mDrawer;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mStatsFragment.onRequestPermissionsResultFrag(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        int mode = ((RHAApplication) getApplication()).getMODE();
        switch (item.getItemId()) {
            case R.id.opt_tasks:
                if (mode == Constants.MODE.CAPTURE_DATA_MODE) {
                    Snackbar.make(mActivityView, getString(R.string.feature_not_avl_capture_mode), Snackbar.LENGTH_LONG).show();
                    break;
                } else {
                    mDrawer.closeDrawer(Gravity.START);
                    TaskMessage msg = new TaskMessage();
                    msg.setFromClass(ParentActivity.class.getName());
                    msg.setMsg(TaskMessage.SHOW_TASK_LIST);
                    ViewModelProviders.of(ParentActivity.this).get(TaskViewModel.class).sendMessage(msg);
                }
                break;
            case R.id.opt_logout:
                mDrawer.closeDrawer(Gravity.START);
                CyientSharePrefrence.clearSharePrefrence(ParentActivity.this);
                Intent intent = new Intent(ParentActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.my_profile:
                mDrawer.closeDrawer(Gravity.START);
                Intent myProfileIntent = new Intent(ParentActivity.this, UserProfileActivity.class);
                startActivity(myProfileIntent);
                break;
            case R.id.abt_us:
                mDrawer.closeDrawer(Gravity.START);
                Intent aboutIntent = new Intent(ParentActivity.this, AboutUsActivity.class);
                startActivity(aboutIntent);
                break;
            case R.id.dashboard:
                Intent dbIntent = new Intent(ParentActivity.this, DashboardActivity.class);
                startActivity(dbIntent);
                break;
            default:
                mStatsFragment.onNavigationItemSelectedFrag(item);
                break;
        }
        return false;
    }

}