package roadcondition.cynsore.cyient.com.cynsore.adapter.task;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import roadcondition.cynsore.cyient.com.cynsore.R;
import roadcondition.cynsore.cyient.com.cynsore.model.RHATask;
import roadcondition.cynsore.cyient.com.cynsore.utility.Constants;
import roadcondition.cynsore.cyient.com.cynsore.view.tasks.TaskListFragment;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {

    private ArrayList<RHATask> mTaskList;
    private ArrayList<RHATask> mOriginalList;
    private Context mContext;
    private TaskListAdapter.listItemInteractionListner mInteractionListner;

    public TaskListAdapter(Context context, ArrayList<RHATask> taskList, TaskListAdapter.listItemInteractionListner interactionListner) {
        mTaskList = taskList;
        mOriginalList = new ArrayList<>();
        mOriginalList.addAll(mTaskList);
        mTaskList = taskList;
        mContext = context;
        mInteractionListner = interactionListner;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View listItem = inflater.inflate(R.layout.task_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RHATask task = mTaskList.get(position);
        holder.jobId.setText(task.getJobId());
        holder.src_n_dest.setText(task.getSourceAddress() + "/\n\n" + task.getDestinationAddress());
        holder.km_to_cover.setText(String.valueOf(task.getKmstobecovered()) + " km");
        holder.planned_time.setText(task.getPlannedstarttime());
        holder.status.setText(task.getStatus());

        holder.parentView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mInteractionListner.onItemClick(task);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mTaskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView jobId, src_n_dest, km_to_cover, planned_time, status;
        public View parentView;

        public ViewHolder(View itemView) {
            super(itemView);
            parentView = itemView;

            View view1 = itemView.findViewById(R.id.lay_jobid);
            ((TextView) view1.findViewById(R.id.ic_txt_key)).setText("Job Id");
            jobId = view1.findViewById(R.id.ic_txt_value);

            View view2 = itemView.findViewById(R.id.lay_src_dest);
            ((TextView) view2.findViewById(R.id.ic_txt_key)).setText("Km To Cover");
            km_to_cover = view2.findViewById(R.id.ic_txt_value);

            View view3 = itemView.findViewById(R.id.lay_kmtocover);
            ((TextView) view3.findViewById(R.id.ic_txt_key)).setText("Source / Destination");
            src_n_dest = view3.findViewById(R.id.ic_txt_value);

            View view4 = itemView.findViewById(R.id.lay_assigned_date);
            ((TextView) view4.findViewById(R.id.ic_txt_key)).setText("Planned Start Time");
            planned_time = view4.findViewById(R.id.ic_txt_value);

            View view5 = itemView.findViewById(R.id.lay_status);
            ((TextView) view5.findViewById(R.id.ic_txt_key)).setText("Status");
            status = view5.findViewById(R.id.ic_txt_value);

        }
    }

    public void filterList(HashMap<Integer, Boolean> filterMap) {
        /////Time Filter//////////
        ArrayList<RHATask> timeList = new ArrayList<>();
        try {
            Calendar todayCal = Calendar.getInstance();
            todayCal.set(Calendar.HOUR_OF_DAY, 0);
            todayCal.set(Calendar.MINUTE, 0);
            todayCal.set(Calendar.SECOND, 0);
            todayCal.set(Calendar.MILLISECOND, 0);
            Date todayDate = todayCal.getTime();

            for (RHATask rhaTask : mOriginalList) {
                String pStartTime = rhaTask.getPlannedstarttime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                Date pdate = sdf.parse(pStartTime);
                if (filterMap.get(Constants.FilterKeys.past)) {
                    if (pdate.before(todayDate)) {
                        timeList.add(rhaTask);
                    }
                } else if (filterMap.get(Constants.FilterKeys.upcoming)) {
                    if (pdate.after(todayDate)) {
                        timeList.add(rhaTask);
                    }
                } else if (filterMap.get(Constants.FilterKeys.today)) {
                    if (pdate.equals(todayDate)) {
                        timeList.add(rhaTask);
                    }
                }
            }
            mTaskList = timeList;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //////////////////////////

        //////////Status Filter///
        ArrayList<RHATask> statusList = new ArrayList<>();
        for (RHATask rhaTask : mTaskList) {
            if (filterMap.get(Constants.FilterKeys.cboxAll)) {
                //same list will be used
                notifyDataSetChanged();
                return;
            }
            if (filterMap.get(Constants.FilterKeys.cboxYTS)) {
                if (rhaTask.getStatus().equals("YTS")) {
                    statusList.add(rhaTask);
                }
            }
            if (filterMap.get(Constants.FilterKeys.cboxWIP)) {
                if (rhaTask.getStatus().equals("WTP")) {
                    statusList.add(rhaTask);
                }
            }
            if (filterMap.get(Constants.FilterKeys.cboxHold)) {
                if (rhaTask.getStatus().equals("HOLD")) {
                    statusList.add(rhaTask);
                }
            }
            if (filterMap.get(Constants.FilterKeys.cboxComp)) {
                if (rhaTask.getStatus().equals("COMP")) {
                    statusList.add(rhaTask);
                }
            }
            if (filterMap.get(Constants.FilterKeys.cboxReject)) {
                if (rhaTask.getStatus().equals("REJECTED")) {
                    statusList.add(rhaTask);
                }
            }
        }
        mTaskList = statusList;
        notifyDataSetChanged();
        //////////////

    }

    public interface listItemInteractionListner {
        public void onItemClick(Object obj);
    }

}