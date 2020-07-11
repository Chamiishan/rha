package roadcondition.cynsore.cyient.com.cynsore.view.tasks;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.gson.Gson;

import roadcondition.cynsore.cyient.com.cynsore.R;
import roadcondition.cynsore.cyient.com.cynsore.model.RHATask;
import roadcondition.cynsore.cyient.com.cynsore.network.ServerAsyncTaskPost;
import roadcondition.cynsore.cyient.com.cynsore.network.ServerHelper;
import roadcondition.cynsore.cyient.com.cynsore.utility.Constants;
import roadcondition.cynsore.cyient.com.cynsore.utility.CyientSharePrefrence;
import roadcondition.cynsore.cyient.com.cynsore.utility.Helper;
import roadcondition.cynsore.cyient.com.cynsore.view.censor.StatsFragment;
import roadcondition.cynsore.cyient.com.cynsore.view.main.ParentActivity;
import roadcondition.cynsore.cyient.com.cynsore.viewmodel.task.TaskMessage;
import roadcondition.cynsore.cyient.com.cynsore.viewmodel.task.TaskViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskDetailParent extends Fragment implements View.OnClickListener {

    private static final String TAG = "TaskDetailParent";

    private Button mBtnStart, /*mBtnEnd, */
            mBtnDone;
    private FrameLayout mFrame;

    private TaskDetail mTaskDetail;
    private RHATask mRhaTask;

    public TaskDetailParent() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ((ParentActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((ParentActivity) getActivity()).getSupportActionBar().setTitle(R.string.work_force_management);

        View view = inflater.inflate(R.layout.fragment_temp, container, false);
        mFrame = view.findViewById(R.id.ic_frame_taskdetail);

        mBtnStart = view.findViewById(R.id.ic_btn_start);
        mBtnDone = view.findViewById(R.id.ic_btn_done);

        mBtnDone.setOnClickListener(this);
        mBtnStart.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        if (mRhaTask != null) {
//            TaskMessage taskMessage = new TaskMessage();
//            taskMessage.setFromClass(TaskListFragment.class.getName());
//            taskMessage.setTag(mRhaTask);
//            taskMessage.setMsg(TaskMessage.SHOW_TASK_DETAIL);
//            ((TaskViewModel) ViewModelProviders.of(getActivity()).get(TaskViewModel.class)).sendMessage(taskMessage);
//        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewModelProviders.of(getActivity()).get(TaskViewModel.class).getMessage().observe(this,
                item -> {
                    switch (item.getMsg()) {
                        case TaskMessage.SHOW_TASK_DETAIL:
                            Object o = item.getTag();
                            if (o instanceof RHATask && item.getFromClass().equals(TaskListFragment.class.getName())) {
                                mRhaTask = (RHATask) o;
//                                ParentActivity activity = (ParentActivity) getActivity();

//                                ActionBar actionBar = activity.getSupportActionBar();
//                                actionBar.setTitle("Job ID : " + mRhaTask.getJobId());
//                                actionBar.setDisplayHomeAsUpEnabled(true);
//                                actionBar.show();

                                String status = mRhaTask.getStatus();
                                if (!status.equals(Constants.WorkStatus.WIP)) {
                                    mBtnDone.setVisibility(View.VISIBLE);
                                    mBtnStart.setVisibility(View.GONE);
                                } else {
                                    mBtnDone.setVisibility(View.VISIBLE);
                                    mBtnStart.setVisibility(View.VISIBLE);
                                }

                                //show Task Detail
//                                TempFragment tempFragment = new TempFragment();
                                if (mTaskDetail == null) {
                                    mTaskDetail = new TaskDetail();
                                }

                                if (!mTaskDetail.isVisible()) {
//                                if (!tempFragment.isVisible()) {
                                    getChildFragmentManager().beginTransaction()
                                            .replace(mFrame.getId(), mTaskDetail)
//                                            .addToBackStack(null)
                                            .commit();
                                }
                            }
                            break;
                        case TaskMessage.CHANGE_STATUS:
                            mRhaTask = (RHATask) item.getTag();
                            String status = mRhaTask.getStatus();
                            if (!status.equals(Constants.WorkStatus.WIP)) {
                                mBtnDone.setVisibility(View.VISIBLE);
                                mBtnStart.setVisibility(View.GONE);
                            } else {
                                mBtnDone.setVisibility(View.VISIBLE);
                                mBtnStart.setVisibility(View.VISIBLE);
                            }
                            break;
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                TaskMessage msg = new TaskMessage();
                msg.setMsg(TaskMessage.POP_STACK);
                msg.setFromClass(TaskDetailParent.class.getName());
                ViewModelProviders.of(getActivity()).get(TaskViewModel.class).sendMessage(msg);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ic_btn_done:
                String dataFiles = CyientSharePrefrence.getJobDataFromSharePef(getContext(), mRhaTask.getJobId());
                mRhaTask.setSensorFileName(dataFiles);
                mUpdateHelper.setUrl(Constants.update_task_details);
                Gson gson = new Gson();
                String response = gson.toJson(mRhaTask);
                ServerAsyncTaskPost asyncTaskPost = new ServerAsyncTaskPost(getContext(), mUpdateHelper);
                asyncTaskPost.execute(response);
                break;
            case R.id.ic_btn_start:
                TaskMessage msg = new TaskMessage();
                msg.setMsg(TaskMessage.CAPTURE_SENSOR);
                msg.setTag(mRhaTask);
                msg.setFromClass(TaskDetailParent.class.getName());
                ViewModelProviders.of(getActivity()).get(TaskViewModel.class).sendMessage(msg);
                break;
        }
    }

    ServerHelper mUpdateHelper = new ServerHelper() {

        @Override
        public void onFailure(Object o) throws NullPointerException {
        }

        @Override
        public void onSuccess(Object o) throws NullPointerException {
            if (o != null) {
                if (Integer.valueOf(o.toString().replaceAll("\n", "")) > 0)
                    Helper.printLogMsg(TAG, "success");
                TaskMessage msg = new TaskMessage();
                msg.setFromClass(StatsFragment.class.getName());
                msg.setMsg(TaskMessage.POP_STACK);
                ViewModelProviders.of(getActivity()).get(TaskViewModel.class).sendMessage(msg);
            }
        }

        @Override
        public void onServerError(String message) {
        }
    };

}