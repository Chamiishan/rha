package roadcondition.cynsore.cyient.com.cynsore.view.tasks;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import roadcondition.cynsore.cyient.com.cynsore.R;
import roadcondition.cynsore.cyient.com.cynsore.customview.ExpandableLayout;
import roadcondition.cynsore.cyient.com.cynsore.model.RHATask;
import roadcondition.cynsore.cyient.com.cynsore.utility.Constants;
import roadcondition.cynsore.cyient.com.cynsore.utility.MapUtility;
import roadcondition.cynsore.cyient.com.cynsore.view.censor.StatsFragment;
import roadcondition.cynsore.cyient.com.cynsore.viewmodel.task.TaskMessage;
import roadcondition.cynsore.cyient.com.cynsore.viewmodel.task.TaskViewModel;

public class TaskDetail extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private SupportMapFragment mMapFragment;
    private RHATask mRhaTask;
    private GoogleMap map;
    private View mViewTitle;
    private Spinner mSpinner;
    private EditText mEditComment;
    private View mParentView;
    private ImageView mBtnExpand;
    private ScrollView mScrollView;

    private MapUtility mMapUtility;

    String[] allStatus = new String[]{
            Constants.WorkStatus.COMP,
            Constants.WorkStatus.HOLD,
            Constants.WorkStatus.REJECTED,
            Constants.WorkStatus.WIP,
            Constants.WorkStatus.YTS
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if (mParentView != null) {
            container.removeView(mParentView);
        }

        mParentView = inflater.inflate(R.layout.fragment_task_detail, container, false);
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        mEditComment = mParentView.findViewById(R.id.ic_edit_comment);
        mEditComment.addTextChangedListener(mWatcher);
//        mEditComment.setFocusable(false);

        mBtnExpand = mParentView.findViewById(R.id.ic_btn_expand);
        mBtnExpand.setOnClickListener(this);

        mScrollView = mParentView.findViewById(R.id.scrollview);

        return mParentView;
    }

    TextWatcher mWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            mRhaTask.setComments(editable.toString());
        }

    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewModelProviders.of(getActivity()).get(TaskViewModel.class).getMessage().observe(this,
                item -> {
                    switch (item.getMsg()) {
                        case TaskMessage.SHOW_TASK_DETAIL:
                            Object obj1 = item.getTag();
                            if (obj1 instanceof RHATask && item.getFromClass().equals(TaskListFragment.class.getName())) {
                                mRhaTask = (RHATask) obj1;
//                                ParentActivity activity = (ParentActivity) getActivity();

//                                ActionBar actionBar = activity.getSupportActionBar();
//                                actionBar.setTitle("Job ID : " + mRhaTask.getJobId());
//                                actionBar.setDisplayHomeAsUpEnabled(true);
//                                actionBar.show();

                                setData();
                            }
                            break;
                        case TaskMessage.POP_STACK:
                            if (item.getFromClass() != null && item.getFromClass().equals(StatsFragment.class.getName())) {
                                Object obj2 = item.getTag();
                                if (obj2 != null && obj2 instanceof RHATask) {
                                    mRhaTask = (RHATask) obj2;
                                    setData();
                                }
                            }
                            break;
                    }
                });
    }

    private void setData() {
        mViewTitle = mParentView.findViewById(R.id.lay_jobid);
        mViewTitle.setOnClickListener(this);

        mSpinner = mParentView.findViewById(R.id.ic_spin_status);

        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, allStatus);
        //set the spinners adapter to the previously created one.
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TaskMessage msg = new TaskMessage();
                msg.setFromClass(TaskDetail.class.getName());
                msg.setMsg(TaskMessage.CHANGE_STATUS);
                Object obj = adapterView.getItemAtPosition(i);
                mRhaTask.setStatus(String.valueOf(obj));
                msg.setTag(mRhaTask);
                ViewModelProviders.of(getActivity()).get(TaskViewModel.class).sendMessage(msg);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

        });

        for (int i = 0; i < allStatus.length; i++) {
            if (allStatus[i].equals(mRhaTask.getStatus())) {
                mSpinner.setSelection(i);
            }
        }

        View kmtocover = mParentView.findViewById(R.id.lay_kmtocover);
        View srcndest = mParentView.findViewById(R.id.lay_src_dest);
        View starttime = mParentView.findViewById(R.id.lay_start_time);
        View endtime = mParentView.findViewById(R.id.lay_end_time);

        setKeyNVal(mViewTitle, "Job ID:", mRhaTask.getJobId());
        setKeyNVal(kmtocover, "Km to Cover", String.valueOf(mRhaTask.getKmstobecovered()) + "km");
        setKeyNVal(srcndest, "Source / Destination", mRhaTask.getSourceAddress() + " /\n\n" + mRhaTask.getDestinationAddress());
        setKeyNVal(starttime, "Start time", mRhaTask.getActualStartTime() == null ? "--" : mRhaTask.getActualStartTime());
        setKeyNVal(endtime, "End time", mRhaTask.getActualEndTime() == null ? "--" : mRhaTask.getActualEndTime());

    }

    private void setKeyNVal(View parentView, String key, String value) {
        TextView txtKey = parentView.findViewById(R.id.ic_txt_key);
        TextView txtValue = parentView.findViewById(R.id.ic_txt_value);
        txtKey.setText(key);
        txtValue.setText(value);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mRhaTask != null) {
            map = googleMap;
            drawRoute();
        }
    }

    private void drawRoute() {
        if (map != null) {
            map.clear();
            String source = mRhaTask.getSource();
            String dest = mRhaTask.getDestination();
            String wayPts = mRhaTask.getWaypoints();
            LatLng srcLoc, destLoc;
            if (source != null && dest != null) {
                srcLoc = getLocation(source);
                destLoc = getLocation(dest);
                mMapUtility = new MapUtility();
                mMapUtility.drawRoute(map, srcLoc, destLoc, wayPts, MapUtility.TOM_TOM_API, getContext());
            }
        }
    }

    private LatLng getLocation(String location) {
        String[] locArr = location.split(",");
        if (locArr != null && locArr.length == 2) {
            return new LatLng(Double.parseDouble(locArr[0].trim()), Double.parseDouble(locArr[1].trim()));
        }
        return null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ic_btn_expand:
            case R.id.lay_jobid:
                ExpandableLayout layout = mParentView.findViewById(R.id.expandable_layout);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mMapFragment.getView().getLayoutParams();

                if (layout.isExpanded()) {
                    mBtnExpand.setImageResource(android.R.drawable.arrow_down_float);
                    params.weight = 1.8f;
                    layout.collapse(true);
                } else {
                    mBtnExpand.setImageResource(android.R.drawable.arrow_up_float);
                    params.weight = 0.7f;
                    layout.expand(true);

                    mScrollView.post(new Runnable() {

                        @Override
                        public void run() {
                            mScrollView.scrollTo(0, mScrollView.getBottom());
                        }
                    });
                }

                mMapFragment.getView().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mMapFragment.getView().setLayoutParams(params);
                        if (mMapUtility != null && map != null) {
                            Polyline polyline = mMapUtility.getRoute();
                            if (polyline != null) {
                                mMapUtility.zoom(map, polyline);
                            }
                        }
                    }
                }, 100);

                break;
        }
    }

}