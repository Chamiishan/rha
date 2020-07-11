//package roadcondition.cynsore.cyient.com.cynsore.view.tasks;
//
//import android.arch.lifecycle.ViewModelProviders;
//import android.content.Intent;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.widget.FrameLayout;
//
//import roadcondition.cynsore.cyient.com.cynsore.R;
//import roadcondition.cynsore.cyient.com.cynsore.model.RHATask;
//import roadcondition.cynsore.cyient.com.cynsore.utility.Constants;
//import roadcondition.cynsore.cyient.com.cynsore.view.censor.StatsFragment;
//import roadcondition.cynsore.cyient.com.cynsore.viewmodel.task.TaskMessage;
//import roadcondition.cynsore.cyient.com.cynsore.viewmodel.task.TaskViewModel;
//
//public class TaskActivity extends AppCompatActivity {
//
//    private TaskListFragment mTaskListFragment;
//    private TaskDetailParent mDetailFragment;
//    private StatsFragment mStatsFragment;
//    private FrameLayout mFrame;
//
//    private TaskViewModel mTaskViewModel;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_task);
//
//        mFrame = findViewById(R.id.main_frag);
//        mTaskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
//        mTaskViewModel.init();
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        TaskMessage taskMessage = new TaskMessage();
//        taskMessage.setFromClass(TaskActivity.class.getName());
//        taskMessage.setMsg(TaskMessage.SHOW_TASK_LIST);
//        mTaskViewModel.sendMessage(taskMessage);
//
//        mTaskViewModel.getMessage().observe(this, item -> {
//            switch (item.getMsg()) {
//                case TaskMessage.SHOW_TASK_DETAIL:
//                    Object tag = item.getTag();
//                    if (tag instanceof RHATask && item.getFromClass().equals(TaskListFragment.class.getName())) {
//                        if (mDetailFragment == null) {
//                            mDetailFragment = new TaskDetailParent();
//                        }
//                        int bsNum = getSupportFragmentManager().beginTransaction()
//                                .replace(mFrame.getId(), mDetailFragment)
//                                .addToBackStack(null)
//                                .commit();
//                    }
//                    break;
//                case TaskMessage.SHOW_TASK_LIST:
//                    if (mTaskListFragment == null) {
//                        mTaskListFragment = new TaskListFragment();
//                    }
//                    int bsNum1 = getSupportFragmentManager().beginTransaction()
//                            .replace(mFrame.getId(), mTaskListFragment)
//                            .addToBackStack(null)
//                            .commit();
//                    break;
//                case TaskMessage.CAPTURE_SENSOR:
//                    if (mStatsFragment == null) {
//                        mStatsFragment = new StatsFragment();
//                    }
//
//                    int bsNum2 = getSupportFragmentManager().beginTransaction()
//                            .replace(mFrame.getId(), mStatsFragment)
//                            .addToBackStack(null)
//                            .commit();
//                    break;
//                case TaskMessage.POP_STACK:
//                    getSupportFragmentManager().popBackStack();
//                    break;
//            }
//        });
//    }
//}