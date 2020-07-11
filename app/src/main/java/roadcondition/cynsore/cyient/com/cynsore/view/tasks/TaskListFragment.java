package roadcondition.cynsore.cyient.com.cynsore.view.tasks;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import roadcondition.cynsore.cyient.com.cynsore.R;
import roadcondition.cynsore.cyient.com.cynsore.adapter.task.TaskListAdapter;
import roadcondition.cynsore.cyient.com.cynsore.model.RHATask;
import roadcondition.cynsore.cyient.com.cynsore.network.ServerAsyncTask;
import roadcondition.cynsore.cyient.com.cynsore.network.ServerHelper;
import roadcondition.cynsore.cyient.com.cynsore.utility.Constants;
import roadcondition.cynsore.cyient.com.cynsore.utility.CyientSharePrefrence;
import roadcondition.cynsore.cyient.com.cynsore.utility.Helper;
import roadcondition.cynsore.cyient.com.cynsore.utility.SharePrefrenceConstant;
import roadcondition.cynsore.cyient.com.cynsore.view.main.ParentActivity;
import roadcondition.cynsore.cyient.com.cynsore.viewmodel.task.TaskMessage;
import roadcondition.cynsore.cyient.com.cynsore.viewmodel.task.TaskViewModel;

public class TaskListFragment extends Fragment implements
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, TaskListAdapter.listItemInteractionListner {

    private static final String TAG = TaskListFragment.class.getName();

    private View mParentView;
    private TabLayout mTabLayout;
    private ImageButton mImgFilter;
    private RecyclerView mRecyclerView;
    private AlertDialog mStatusDial;

    private final String tab1Txt = "Todays", tab2Txt = "Upcoming", tab3Txt = "Past";
    private HashMap<Integer, Boolean> mFilterMap;
    //    private boolean isFetched;
    private ArrayList<RHATask> mTaskList;
    private TaskListAdapter mAdapter;
    private TabLayout.Tab tab1, tab2, tab3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ParentActivity activity = (ParentActivity) getActivity();
        activity.getSupportActionBar().setHomeAsUpIndicator(null);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((ParentActivity) getActivity()).getSupportActionBar().setTitle(R.string.work_force_management);

//        ActionBar actionBar = activity.getSupportActionBar();
//        actionBar.setTitle(getString(R.string.work_force_management));
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.show();

        // Inflate the layout for this fragment
        mParentView = inflater.inflate(R.layout.fragment_task_list, container, false);
        mTabLayout = mParentView.findViewById(R.id.ic_tab_layout);

        initFilterMap();
        filterStatus(false);

        tab1 = mTabLayout.newTab().setText(tab1Txt).setTag(Constants.FilterKeys.today);
        tab2 = mTabLayout.newTab().setText(tab2Txt).setTag(Constants.FilterKeys.upcoming);
        tab3 = mTabLayout.newTab().setText(tab3Txt).setTag(Constants.FilterKeys.past);

        mTabLayout.addTab(tab1);
        mTabLayout.addTab(tab2);
        mTabLayout.addTab(tab3);
        mTabLayout.removeOnTabSelectedListener(mSelectedListener);
        mTabLayout.addOnTabSelectedListener(mSelectedListener);

        mImgFilter = mParentView.findViewById(R.id.ic_img_filter);
        mImgFilter.setOnClickListener(this);

        mRecyclerView = mParentView.findViewById(R.id.ic_task_list);

        return mParentView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavigationView
                ((ParentActivity) getActivity()).getNavigationView().setCheckedItem(R.id.home);

                TaskMessage msg = new TaskMessage();
                msg.setFromClass(TaskListFragment.class.getName());
                msg.setMsg(TaskMessage.POP_STACK);
                ViewModelProviders.of(getActivity()).get(TaskViewModel.class).sendMessage(msg);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String email = CyientSharePrefrence.getStringFromSharePef(getContext(), SharePrefrenceConstant.EMAIL_ID);
        mTaskDetailHelper.setUrl(Constants.fetch_task_details + email);
        ServerAsyncTask asyncTask = new ServerAsyncTask(getContext(), mTaskDetailHelper);
        asyncTask.showCallProgress(true);
        asyncTask.execute();
    }

    ServerHelper mTaskDetailHelper = new ServerHelper() {

        @Override
        public void onFailure(Object o) throws NullPointerException {
        }

        @Override
        public void onSuccess(Object o) throws NullPointerException {
            if (o != null) {

                String jsonStr = String.valueOf(o);
                if (jsonStr.length() > 0) {
//                    jsonStr = jsonStr.replaceAll("(\\r\\n|\\n|\\r)", "");
                    Helper.printLogMsg(TAG, "json str: " + jsonStr);
                    Gson gson = new Gson();

                    Type type = new TypeToken<List<RHATask>>() {
                    }.getType();
                    mTaskList = gson.fromJson(jsonStr, type);

                    mAdapter = new TaskListAdapter(getContext(), mTaskList, TaskListFragment.this::onItemClick);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    mRecyclerView.setLayoutManager(layoutManager);
                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    mRecyclerView.setAdapter(mAdapter);
                    tab1.select();

                    Helper.printLogMsg(TAG, mTaskList.toString());
                }
            }
        }

        @Override
        public void onServerError(String message) {
        }
    };

    TabLayout.OnTabSelectedListener mSelectedListener = new TabLayout.OnTabSelectedListener() {

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            switch ((Integer) tab.getTag()) {
                case Constants.FilterKeys.today:
                    mFilterMap.put(Constants.FilterKeys.today, true);
                    mFilterMap.put(Constants.FilterKeys.upcoming, false);
                    mFilterMap.put(Constants.FilterKeys.past, false);
                    break;
                case Constants.FilterKeys.upcoming:
                    mFilterMap.put(Constants.FilterKeys.today, false);
                    mFilterMap.put(Constants.FilterKeys.upcoming, true);
                    mFilterMap.put(Constants.FilterKeys.past, false);
                    break;
                case Constants.FilterKeys.past:
                    mFilterMap.put(Constants.FilterKeys.today, false);
                    mFilterMap.put(Constants.FilterKeys.upcoming, false);
                    mFilterMap.put(Constants.FilterKeys.past, true);
                    break;
            }
            if (mAdapter != null) {
                mAdapter.filterList(mFilterMap);
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            switch ((Integer) tab.getTag()) {
                case Constants.FilterKeys.today:
                    mFilterMap.put(Constants.FilterKeys.today, false);
                    break;
                case Constants.FilterKeys.upcoming:
                    mFilterMap.put(Constants.FilterKeys.upcoming, false);
                    break;
                case Constants.FilterKeys.past:
                    mFilterMap.put(Constants.FilterKeys.past, false);
                    break;
            }
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            onTabSelected(tab);
        }

    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ic_img_filter:
                filterStatus(true);
                break;
        }
    }

    private void filterStatus(boolean show) {
        if (mStatusDial == null) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.dial_status_box, null);

            CheckBox cbAll = createCheckBox(view.findViewById(R.id.lay_all), R.id.ic_cbox_status, Constants.FilterKeys.cboxAll, "All");
            cbAll.setChecked(true);
            mFilterMap.put(Constants.FilterKeys.cboxAll, true);
            createCheckBox(view.findViewById(R.id.lay_completed), R.id.ic_cbox_status, Constants.FilterKeys.cboxComp, "Completed");
            createCheckBox(view.findViewById(R.id.lay_wip), R.id.ic_cbox_status, Constants.FilterKeys.cboxWIP, "WIP (Work In Progress)");
            createCheckBox(view.findViewById(R.id.lay_yts), R.id.ic_cbox_status, Constants.FilterKeys.cboxYTS, "YTS (Yet To Start)");
            createCheckBox(view.findViewById(R.id.lay_hold), R.id.ic_cbox_status, Constants.FilterKeys.cboxHold, "Hold");
            createCheckBox(view.findViewById(R.id.lay_reject), R.id.ic_cbox_status, Constants.FilterKeys.cboxReject, "Rejected");

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setView(view)
                    .setNegativeButton("Close", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mAdapter != null) {
                                mAdapter.filterList(mFilterMap);
                            }
                            mStatusDial.dismiss();
                        }
                    });
            mStatusDial = builder.create();
        }
        if (!mStatusDial.isShowing() && show) {
            mStatusDial.show();
        }
    }

    private CheckBox createCheckBox(View parentView, int xmlid, int newid, String text) {
        CheckBox cb = parentView.findViewById(xmlid);
        cb.setText(text);
        cb.setId(newid);
        cb.setOnCheckedChangeListener(this);
        return cb;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        checkChangeCommonOp(buttonView, isChecked);
        switch (buttonView.getId()) {
            case Constants.FilterKeys.cboxAll:
                break;
            case Constants.FilterKeys.cboxComp:
                break;
            case Constants.FilterKeys.cboxWIP:
                break;
            case Constants.FilterKeys.cboxYTS:
                break;
            case Constants.FilterKeys.cboxHold:
                break;
            case Constants.FilterKeys.cboxReject:
                break;
        }
    }

    private void checkChangeCommonOp(CompoundButton cBox, boolean isChecked) {
        if (isChecked) {
            cBox.setTextColor(Color.BLACK);
            mFilterMap.put(cBox.getId(), true);
        } else {
            mFilterMap.put(cBox.getId(), false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cBox.setTextColor(getContext().getColor(R.color.grey));
            } else {
                cBox.setTextColor(Color.parseColor("#BFBEBB"));
            }
        }
    }

    private HashMap<Integer, Boolean> initFilterMap() {
        mFilterMap = new HashMap<>();
        mFilterMap.put(Constants.FilterKeys.cboxAll, true);
        mFilterMap.put(Constants.FilterKeys.cboxComp, false);
        mFilterMap.put(Constants.FilterKeys.cboxWIP, false);
        mFilterMap.put(Constants.FilterKeys.cboxYTS, false);
        mFilterMap.put(Constants.FilterKeys.cboxHold, false);
        mFilterMap.put(Constants.FilterKeys.cboxReject, false);
        mFilterMap.put(Constants.FilterKeys.upcoming, false);
        mFilterMap.put(Constants.FilterKeys.today, false);
        mFilterMap.put(Constants.FilterKeys.past, false);
        return mFilterMap;
    }

    @Override
    public void onItemClick(Object obj) {
        if (obj instanceof RHATask) {
            TaskMessage taskMessage = new TaskMessage();
            taskMessage.setFromClass(TaskListFragment.class.getName());
            taskMessage.setTag(obj);
            taskMessage.setMsg(TaskMessage.SHOW_TASK_DETAIL);
            ((TaskViewModel) ViewModelProviders.of(getActivity()).get(TaskViewModel.class)).sendMessage(taskMessage);
        }
    }

}