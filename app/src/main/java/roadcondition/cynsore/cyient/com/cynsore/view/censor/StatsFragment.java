package roadcondition.cynsore.cyient.com.cynsore.view.censor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.tomtom.core.maps.OnMapDragListener;
import com.tomtom.online.sdk.map.MapFragment;
import com.tomtom.online.sdk.map.MarkerBuilder;
import com.tomtom.online.sdk.map.TomtomMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import roadcondition.cynsore.cyient.com.cynsore.BuildConfig;
import roadcondition.cynsore.cyient.com.cynsore.R;
import roadcondition.cynsore.cyient.com.cynsore.RHAApplication;
import roadcondition.cynsore.cyient.com.cynsore.SQLDataBase.FileDataRepository;
import roadcondition.cynsore.cyient.com.cynsore.algorithm.DeviceStateDetector;
import roadcondition.cynsore.cyient.com.cynsore.algorithm.IRITable;
import roadcondition.cynsore.cyient.com.cynsore.algorithm.RoadQuality;
import roadcondition.cynsore.cyient.com.cynsore.capturedata.CaptureDataService;
import roadcondition.cynsore.cyient.com.cynsore.capturedata.UploadFileTasks;
import roadcondition.cynsore.cyient.com.cynsore.capturedata.UploadVideoFile;
import roadcondition.cynsore.cyient.com.cynsore.direction.PathJSONParser;
import roadcondition.cynsore.cyient.com.cynsore.graph.Graph_1;
import roadcondition.cynsore.cyient.com.cynsore.model.BumpModel;
import roadcondition.cynsore.cyient.com.cynsore.model.FileData;
import roadcondition.cynsore.cyient.com.cynsore.model.ProcessedDataModel;
import roadcondition.cynsore.cyient.com.cynsore.model.RHATask;
import roadcondition.cynsore.cyient.com.cynsore.model.RecordDetailsModel;
import roadcondition.cynsore.cyient.com.cynsore.network.ServerAsyncTask;
import roadcondition.cynsore.cyient.com.cynsore.network.ServerAsyncTaskPost;
import roadcondition.cynsore.cyient.com.cynsore.network.ServerHelper;
import roadcondition.cynsore.cyient.com.cynsore.utility.Constants;
import roadcondition.cynsore.cyient.com.cynsore.utility.CyientSharePrefrence;
import roadcondition.cynsore.cyient.com.cynsore.utility.Helper;
import roadcondition.cynsore.cyient.com.cynsore.utility.MapUtility;
import roadcondition.cynsore.cyient.com.cynsore.utility.SharePrefrenceConstant;
import roadcondition.cynsore.cyient.com.cynsore.utility.Util;
import roadcondition.cynsore.cyient.com.cynsore.utility.ValidateUtil;
import roadcondition.cynsore.cyient.com.cynsore.view.main.ParentActivity;
import roadcondition.cynsore.cyient.com.cynsore.viewmodel.task.TaskMessage;
import roadcondition.cynsore.cyient.com.cynsore.viewmodel.task.TaskViewModel;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.DOWNLOAD_SERVICE;

public class StatsFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, com.tomtom.online.sdk.map.OnMapReadyCallback {

    protected static final int REQUEST_CHECK_SETTINGS = 1;
    private static final int REQUEST_SEARCH_CODE = 2;
    private static final int REQUEST_OVERLAY_PERMISSION_CODE = 3;
    private static final int REQUEST_VIDEO = 4;

    protected static final String TAG = StatsFragment.class.getSimpleName();

    private static final int TOM_TOM_ZOOM_LEVEL = 14;
    private static final int PERM_CAM_REQ_CODE = 100;
    private static final int PERM_REQ_CODE = 200;
    private static final int PERM_REQ_EXTERNAL_CODE = 300;

    private GoogleMap map;
    private TomtomMap mTMap;
    private SupportMapFragment mapGMapView;
    private TextView heading_tv, speed_tv;// bad_road_conditions_tv, bytes_contributed_tv;
    private ImageButton /*gMap, tMap,*/ mMapSelector, mImgGraph;
    private ImageView mImgMyLoc;
    private Chronometer chrono;
    private int mLastChronoSecs;
    private View mView;
    //    private Marker mPositionMarker;
    private LinearLayout mSearchLayout;
    private TextView home_tv, upload_tv;
    private Date triggereddate;
    private Dialog mAppUpdateDial, mModeChangeAlert, mFileProgDialog, mEndTripDialog;

    private MapFragment mTMapView;
    private ImageView logo;
    private ImageButton mImgMenu;
    private TextView mTxtNotify;
    private LinearLayout mBottomLayout;
    private BottomSheetBehavior mSheetBehavior;

    //    private ImageView cam;
    //    private String imagepath = null;
    public static String phone2 = null;

    private LatLng srcLatlng, destLatlng;
    private RHATask mRhaTask;

    // 08/08 IRI
    private static final float ANGLE_THRESHOLD = 15;

    private static final float IRI_QUALITY_1 = 2;
    private static final float IRI_QUALITY_2 = 4;
    private static final float IRI_QUALITY_3 = 6;

    private float quality1 = IRI_QUALITY_1;
    private float quality2 = IRI_QUALITY_2;
    private float quality3 = IRI_QUALITY_3;

    public static float IRI_DEFAULT_VALUE = 1f;

    private static StatsFragment instance;
    private IRITable iriTable;
    private DeviceStateDetector stateDetector;

    public static final float G = 9.80665f;

    private static final float SPEED_1 = 30.0f;
    private static final float SPEED_2 = 50.0f;

    private float bumpThreshold1 = 1.0f * G;
    private float bumpThreshold2 = 1.2f * G;

    private List<BumpModel> detectedBumps;

    private long timeInterval = 500;
    private long prevTime = 0;

    private float iri;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;
    private Marker currLocMarker;

//    private String upLoadServerUri = "https://iptools.cyient.com/IndoorMappingService/webapi/dbfile/upload1"; //Server URl need to be here

    //    double latitude = 0;
//    double longitude = 0;
//    double latitude_original = 0;
//    double longitude_original = 0;
    //https://iptools.cyient.com/IndoorMappingService/webapi/dbfile/upload
//    final String uploadFilePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "cyient/";

    private JSONObject mGraphResponse;
    private boolean setMyLoc = true;
    private boolean playAppInBack = false;

    private List<FileData> mFileDatas;
    private String status_uploading = "Uploading";
    private String status_start = "Start";
    private String status_noremain = "No Files";

    private Messenger messenger = new Messenger(new IncomingHandler());
    private static Messenger mServiceMessanger = null;
    private NotificationManagerCompat mNotManager;
    private NotificationCompat.Builder mBuilder;
    private DownloadManager mgr;
    private long lastDownload;

    private Snackbar mAccuracyAlert;

    public static final String VID_INTENT_FILTER = "vid_intent_filter";
    public static final String VID_STATUS_FLAG = "status_flag";
    public static final String VID_FILENAME = "vid_filename";

    //TaskMessage from CaptureDataService
    public static final int MSG_REQ_PERMISSIONS = 10;
    public static final int MSG_UPDATE_LOCONMAP = 11;
    public static final int MSG_UPDATE_UI_FIELDS = 12;
    public static final int MSG_FILE_UPLOADED = 13;
    public static final int MSG_FILE_UPLOAD_START = 14;
    public static final int MSG_SHOW_FILEPROGRESS = 15;
    public static final int MSG_CANCEL_FILE_PROGRESS = 16;
    public static final int MSG_SENSOR_NOT_AVL = 17;
    public static final int MSG_PROMPT_LOC_ENABLE = 18;
    public static final int MSG_VID_FILE_UPLOAD_END = 19;
    public static final int MSG_VID_FILE_UPLOAD_PROGRESS = 20;
    public static final int MSG_VID_FILE_UPLOAD_START = 21;
    public static final int MSG_SHOW_ACCURACY_ALERT = 22;
    public static final int MSG_CLOSE_ACCURACY_ALERT = 23;

//    private boolean quit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        if (((ParentActivity) getActivity()).getNavigationView().getMenu().getItem(0).isChecked()) {
            ((ParentActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu1);
            ((ParentActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name);
        } else if (((ParentActivity) getActivity()).getNavigationView().getMenu().getItem(7).isChecked()) {
//            ((ParentActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
//            ((ParentActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((ParentActivity) getActivity()).getSupportActionBar().setTitle(R.string.work_force_management);
        }

        mView = inflater.inflate(R.layout.fragment_capture, null);

        playAppInBack = getActivity().getIntent().getBooleanExtra("play_in_back", false);
        if (playAppInBack) {
            getActivity().moveTaskToBack(true);
            playAppInBack = false;
        }

        mNotManager = NotificationManagerCompat.from(getActivity());

        logo = (ImageView) mView.findViewById(R.id.icon);
        mImgMyLoc = (ImageView) mView.findViewById(R.id.ic_img_myloc);
        mImgMyLoc.setOnClickListener(this);

        if (getActivity() instanceof ParentActivity) {
            boolean tasksEnable = getActivity().getIntent().getBooleanExtra(Constants.TASKS_KEY.IS_TASK_AVL, false);
            if (tasksEnable) {
                MenuItem item = ((ParentActivity) getActivity()).getNavigationView().getMenu().findItem(R.id.opt_tasks);
                item.setVisible(true);
            }
        }

        mImgMenu = (ImageButton) mView.findViewById(R.id.menu);
        mImgMenu.setOnClickListener(this);
        mImgMenu.setTag("search");

        mTxtNotify = (TextView) mView.findViewById(R.id.ic_txt_notify);

        home_tv = (TextView) mView.findViewById(R.id.home_RR);
        upload_tv = (TextView) mView.findViewById(R.id.upload_RR);
        triggereddate = new Date();

        mTMapView = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map_tomtom);
        mTMapView.getAsyncMap(this);

        mapGMapView = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_google);
        mapGMapView.getMapAsync(this);

        mBottomLayout = mView.findViewById(R.id.lin_bottom);
        mSheetBehavior = BottomSheetBehavior.from(mBottomLayout);
        mSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        mImgGraph = (ImageButton) mView.findViewById(R.id.ic_img_graph);
        mImgGraph.setOnClickListener(this);
        mMapSelector = (ImageButton) mView.findViewById(R.id.ic_img_mapselector);
        mMapSelector.setSelected(true);
        mMapSelector.setOnClickListener(this);
        heading_tv = (Button) mView.findViewById(R.id.head_tv);
        speed_tv = (TextView) mView.findViewById(R.id.speed_tv);

        mSearchLayout = mView.findViewById(R.id.lay_searchwidget);
        EditText mSearchView = (EditText) mView.findViewById(R.id.searchwidget);
        mSearchView.setInputType(InputType.TYPE_NULL);
        mSearchView.setFocusable(false);
        mSearchView.setOnClickListener(this);

        chrono = (Chronometer) mView.findViewById(R.id.scan_tv);

//        cam.setOnClickListener(this);
        logo.setOnClickListener(this);

        heading_tv.setText("Heading\n" + 0.0);
        speed_tv.setText("Speed\n" + 0 + " kph");

//        mapGMapView = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
//        mapGMapView.getMapAsync(this);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(myBroadCastReceiver, new IntentFilter(VID_INTENT_FILTER));
        setActivityFeatures();
        return mView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.sensor_actionbar_menu, menu);
        if (((ParentActivity) getActivity()).getNavigationView().getMenu().getItem(0).isChecked()) {
            MenuItem menuItem = menu.findItem(R.id.action_end);
            menuItem.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (((ParentActivity) getActivity()).getNavigationView().getMenu().getItem(7).isChecked()) {
                    showEndTripAlert();
                } else {
//                if (((ParentActivity) getActivity()).getNavigationView().getMenu().getItem(0).isChecked()) {
                    ParentActivity activity = (ParentActivity) getActivity();
                    if (activity.getDrawer().isDrawerOpen(Gravity.START)) {
                        activity.getDrawer().closeDrawer(Gravity.START);
                    } else {
                        activity.getDrawer().openDrawer(Gravity.START);
                    }
                }
                break;
            case R.id.action_end:
                showEndTripAlert();
                break;
            case R.id.action_camera:
                openGetPhNumDial();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        mTMapView.onStart();
        if (!((ParentActivity) getActivity()).getNavigationView().getMenu().getItem(7).isChecked()) {
            ParentActivity activity = (ParentActivity) getActivity();
            activity.getNavigationView().setNavigationItemSelectedListener(activity);
            activity.getNavigationView().getMenu().getItem(0).setChecked(true);
        }
    }


    private void setActivityFeatures() {
//        ActionBar actionBar = ((ParentActivity) getActivity()).getSupportActionBar();
//        actionBar.setTitle("RHA");
//        actionBar.setLogo(R.drawable.ic_menu1);

        if (((ParentActivity) getActivity()).getNavigationView().getMenu().getItem(0).isChecked()) {
            //FetchVideoFiles
            FileDataRepository.getInstance(getContext()).fetchAllFiles(mVidFilesObserver);
            //Menu Button
            mImgMenu.setVisibility(View.VISIBLE);
            //search widget
            mSearchLayout.setVisibility(View.VISIBLE);

        } else if (((ParentActivity) getActivity()).getNavigationView().getMenu().getItem(7).isChecked()) {
            //Menu Button
            mImgMenu.setVisibility(View.GONE);
            //search widget
            mSearchLayout.setVisibility(View.GONE);

            if (mRhaTask != null) {
                String source = mRhaTask.getSource();
                String dest = mRhaTask.getDestination();

                String[] srcArr = source.split(",");
                String[] destArr = dest.split(",");

                if (srcArr.length == 2 && destArr.length == 2) {
                    srcLatlng = new LatLng(Double.parseDouble(srcArr[0]), Double.parseDouble(srcArr[1]));
                    destLatlng = new LatLng(Double.parseDouble(destArr[0]), Double.parseDouble(destArr[1]));
                    drawRoute();
                }
            }

            setCaptureMode();

//            set start time
            if (mRhaTask != null) {
                mRhaTask.setActualStartTime(Util.getDate_yyyy_MM_dd__HH_mm_ss(System.currentTimeMillis()));
            }

        }
    }

    public void setData(RHATask mRhaTask) {
        this.mRhaTask = mRhaTask;
        if (map != null && currLocMarker != null) {
            map.clear();
            MarkerOptions mo = new MarkerOptions().position(currLocMarker.getPosition());
            mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation_navigation));
            currLocMarker = map.addMarker(mo);
        }
    }

    private boolean checkSensorNGPS() {
        boolean isAvl = true;
        SensorManager sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        Sensor accl = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (accl == null) {
            isAvl = false;
        }
        Sensor gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (gyro == null) {
            isAvl = false;
        }
        Sensor orientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        if (orientation == null) {
            isAvl = false;
        }

        LocationManager mgr = (LocationManager) getActivity()
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            isAvl = false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            isAvl = false;
        if (!providers.contains(LocationManager.GPS_PROVIDER)) {
            isAvl = false;
        }

        return isAvl;
    }

    private void showUpdateAlert(final boolean mandatory, int remDays) {
        if (mAppUpdateDial == null) {
            remDays = Math.max(0, remDays);
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setTitle("Update Alert!")
                    .setMessage("Please update latest version. " + remDays + " days are remaining.")
                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startApkDownload();
                            mAppUpdateDial.dismiss();
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAppUpdateDial.dismiss();
                            if (mandatory) {
                                getActivity().finish();
                            } else {
                                Intent intent = new Intent(getContext(), CaptureDataService.class);
                                if (mRhaTask != null) {
                                    intent.putExtra("rha_task", mRhaTask);
                                }
                                getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                            }
                        }
                    })
                    .setCancelable(false);

            mAppUpdateDial = builder.create();
        }

        mAppUpdateDial.show();

        if (BuildConfig.FLAVOR.equals("dongle")) {
            try {
                JSONObject jsonObject = new JSONObject();

                String emailId = CyientSharePrefrence.getStringFromSharePef(getContext(), SharePrefrenceConstant.EMAIL_ID);
                String update_msg = "Dear " + emailId + ",\n" + "New version update of RHA application is available for the dongle. Please " +
                        "update the application.";

                jsonObject.put("emailId", emailId);
                jsonObject.put("message", update_msg);

                notifyUpdateHelper.setUrl(Constants.base_url + "notification/notifyEmail");
                ServerAsyncTaskPost asyncTaskPost = new ServerAsyncTaskPost(getContext(), notifyUpdateHelper);
                asyncTaskPost.showCallProgress(false);
                asyncTaskPost.execute(jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    ServerHelper notifyUpdateHelper = new ServerHelper() {

        @Override
        public void onFailure(Object obj) throws NullPointerException {
        }

        @Override
        public void onSuccess(Object obj) throws NullPointerException {
            if (obj != null && String.valueOf(obj).length() > 0) {
                Helper.printLogMsg(TAG, "Version update message: " + String.valueOf(obj));
            }
        }

        @Override
        public void onServerError(String message) {
        }

    };

    private void startApkDownload() {
        Uri uri = Uri.parse(Constants.getApkDownloadUri());

        Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .mkdirs();

        mgr = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
        getActivity().registerReceiver(onComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        getActivity().registerReceiver(onNotificationClick,
                new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));

        lastDownload =
                mgr.enqueue(new DownloadManager.Request(uri)
                        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                                DownloadManager.Request.NETWORK_MOBILE)
                        .setAllowedOverRoaming(false)
                        .setTitle("RHA")
                        .setDescription("RHA updating...")
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                "RHA.apk"));
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {

        public void onReceive(Context ctxt, Intent intent) {

            Cursor c = mgr.query(new DownloadManager.Query().setFilterById(lastDownload));
            if (c.moveToFirst()) {
                String colURI = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                File file = new File(Uri.parse(colURI).getPath());
                Helper.printLogMsg(TAG, Uri.parse(colURI).getPath());
                if (file.exists()) {
                    Helper.printLogMsg(TAG, "exist");
                }

                Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                        .setDataAndType(FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", file),
                                "application/vnd.android.package-archive")
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(promptInstall);
                getActivity().finish();
            }
        }
    };

    BroadcastReceiver onNotificationClick = new BroadcastReceiver() {

        public void onReceive(Context ctxt, Intent intent) {
            Toast.makeText(ctxt, "Ummmm...hi!", Toast.LENGTH_LONG).show();
        }

    };

    Observer mVidFilesObserver = new Observer() {

        @Override
        public void onChanged(@Nullable Object list) {
            ParentActivity activity = (ParentActivity) getActivity();
            Menu menu = activity.getNavigationView().getMenu();
            if (menu != null) {
                MenuItem menuItem = menu.findItem(R.id.upload_vid);
                mFileDatas = (List<FileData>) list;
                if (mFileDatas != null && mFileDatas.size() > 0) {
                    int count = 0;
                    for (int i = 0; i < mFileDatas.size(); i++) {
                        if (mFileDatas.get(i).getUpload_status() == Constants.FileUploadStatus.END_RECORD) {
                            count++;
                        }
                    }

                    if (count > 0) {
                        boolean isRecording = UploadVideoFile.getInstance(getContext()).isRecording();
                        if (isRecording) {
                            menuItem.setTitle(status_uploading + "(" + count + ")");
                        } else {
                            menuItem.setTitle(status_start + "(" + count + ")");
                        }
                        mTxtNotify.setVisibility(View.VISIBLE);
                        mTxtNotify.setText(String.valueOf(count));

                        NotificationCompat.Builder builder = buildVidNotifyBuilder(0, 0, false, "RHA", count + " video files are left to upload");
                        mNotManager.notify(Constants.NOTIFICATION_IDS.NOTIFICATION_VID_UPLOAD, builder.build());

                    } else {
                        mTxtNotify.setVisibility(View.INVISIBLE);
                        mTxtNotify.setText("0");
                        menuItem.setTitle(status_noremain);

                        mNotManager.cancel(Constants.NOTIFICATION_IDS.NOTIFICATION_VID_UPLOAD);
                    }
                } else {
                    mTxtNotify.setVisibility(View.INVISIBLE);
                    mTxtNotify.setText("0");
                    menuItem.setTitle(status_noremain);

                    mNotManager.cancel(Constants.NOTIFICATION_IDS.NOTIFICATION_VID_UPLOAD);
                }
            }
        }
    };

    private NotificationCompat.Builder buildVidNotifyBuilder(int maxProgress, int progress, boolean inderminate, String title, String text) {
        // Create notification default intent.
        Intent intent = new Intent(getActivity(), StatsFragment.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

        if (mBuilder == null) {
            mBuilder = new NotificationCompat.Builder(getActivity());
        }

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();

        bigTextStyle.setBigContentTitle(title);
        bigTextStyle.bigText(text);
//        bigTextStyle.bigText(vidCount + " video data left to upload.");

        mBuilder.setStyle(bigTextStyle);

        mBuilder.setWhen(System.currentTimeMillis());
        mBuilder.setSmallIcon(R.drawable.ic_rr_ticker);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setContentIntent(pendingIntent);//setFullScreenIntent(pendingIntent, true);
        mBuilder.setAutoCancel(true);
        mBuilder.setProgress(maxProgress, progress, inderminate);

        return mBuilder;
    }

    public boolean isApplicationSentToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    private void checkUpdateOrStartService() {
        boolean isUpdateReq = getActivity().getIntent().getBooleanExtra(Constants.UPDATE_KEYS.UPDATE_REQ_KEY, false);
        boolean isSensorAvl = checkSensorNGPS();
        if (isUpdateReq) {
            Helper.printLogMsg(TAG, "update is required");
            int remDays = (int) getActivity().getIntent().getLongExtra(Constants.UPDATE_KEYS.UPDATE_REM_DAYS, -1);
            boolean mandatory = remDays < 0 ? true : false;
            showUpdateAlert(mandatory, remDays);
        } else if (!isSensorAvl) {
            AlertDialog dialog = null;
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setTitle("Alert!")
                    .setMessage("Sensors for recording data is not available on this device, hence data cannot be recorded on this device.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("Finish", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().finish();
                        }
                    });
            dialog = builder.show();
        } else {
            Intent intent = new Intent(getActivity(), CaptureDataService.class);
            if (mRhaTask != null) {
                intent.putExtra("rha_task", mRhaTask);
            }
            getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        mTMapView.onPause();
        if (isApplicationSentToBackground(getContext())) {
            Helper.printLogMsg(TAG, "background ...\n\n\n...");
            // Do what you want to do on detecting Home Key being Pressed
            if (mServiceMessanger != null) {
                Message msg = Message.obtain(null, CaptureDataService.MSG_SHOW_LAYER);
                try {
                    mServiceMessanger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mTMapView.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTMapView.onDestroy();
        if (mServiceMessanger != null) {
            Message msg = Message.obtain(null, CaptureDataService.MSG_CLOSE_LAYER);
            try {
                mServiceMessanger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        try {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(myBroadCastReceiver);
            getContext().unregisterReceiver(onComplete);
            getContext().unregisterReceiver(onNotificationClick);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        setData(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mTMapView.onResume();
    }

    public void onBackPressedFrag() {
        if (mServiceMessanger != null) {
//            Message msg = Message.obtain(null, CaptureDataService.MSG_TRIGGER_FILE_UPLOAD);
//            try {
//                mServiceMessanger.send(msg);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
            setViewMode(true);
        } else {
            getActivity().finish();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        ViewGroup parent = (ViewGroup) mapGMapView.getView().findViewWithTag("GoogleMapMyLocationButton").getParent();
        parent.post(new Runnable() {

            @Override
            public void run() {
                try {
                    Resources r = getResources();
                    //convert our dp margin into pixels
                    int marginPixelsR = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());
                    int marginPixelsB = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, r.getDisplayMetrics());
                    // Get the map compass mView
                    View mapCompass = parent.getChildAt(4);

                    // create layoutParams, giving it our wanted width and height(important, by default the width is "match parent")
                    RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(mapCompass.getHeight(), mapCompass.getHeight());
                    // position on top right
                    rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                    rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                    rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    //give compass margin
                    rlp.setMargins(0, 0, marginPixelsR, marginPixelsB);
                    mapCompass.setLayoutParams(rlp);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {

            @Override
            public void onCameraMoveStarted(int reason) {
                Helper.printLogMsg("onCameraMoveStarted", "map camera started " +
                        "");
                if (reason == REASON_GESTURE) {
                    setMyLoc = false;
                    mImgMyLoc.setVisibility(View.VISIBLE);
                    Helper.printLogMsg("onCameraMoveStarted", "The user gestured on the map.");
                } else if (reason == REASON_API_ANIMATION) {
                    Helper.printLogMsg("onCameraMoveStarted", "The user tapped something on the map.");
                } else if (reason == REASON_DEVELOPER_ANIMATION) {
                    Helper.printLogMsg("onCameraMoveStarted", "The app moved the camera.");
                }
            }
        });

    }

    // IRI Module starts here
    private static boolean isValidRotationAngle(RecordDetailsModel r, float angleThreshold) {
        return Math.abs(r.getRotationAngle()) <= angleThreshold;
    }

    public static float getAv(float gx, float gy, float gz, float ax, float ay, float az) {
        float av = (ax * gx + ay * gy + az * gz) / G;
        return av;
    }

    public static float getAv(RecordDetailsModel record) {
        float gx = record.getAccelerometerGravityX();
        float gy = record.getAccelerometerGravityY();
        float gz = record.getAccelerometerGravityZ();
        float ax = record.getAccelerometerLinearX();
        float ay = record.getAccelerometerLinearY();
        float az = record.getAccelerometerLinearZ();
        return getAv(gx, gy, gz, ax, ay, az);
    }

    private boolean isNextWindow(long time) {
        if (time - prevTime >= timeInterval) {
            prevTime = time;
            return true;
        }
        return false;
    }

    public float getThreshold(float speed, boolean isFixed) {
        float threshold1 = bumpThreshold1;
        float threshold2 = bumpThreshold2;
        if (speed >= SPEED_1 && speed <= SPEED_2) {
            return threshold1;
        } else if (speed > SPEED_2) {
            return threshold2;
        }
        return threshold1;
    }

    public void addBumpEvent(RecordDetailsModel r, boolean isFixed) {
        if (detectedBumps == null) {
            detectedBumps = new ArrayList<>();
        }
        BumpModel bump = new BumpModel();
        bump.setRecordId(r.getRecordId());
        bump.setAccelerationX(r.getAccelerometerX());
        bump.setAccelerationY(r.getAccelerometerY());
        bump.setAccelerationZ(r.getAccelerometerZ());
        bump.setTime(r.getTime());
        bump.setSpeed(r.getAverageSpeed());
        bump.setLatitude(r.getLatitude());
        bump.setLongitude(r.getLongitude());
        bump.setAltitude(r.getAltitude());
        bump.setFixed(isFixed);
        detectedBumps.add(bump);
    }

    public boolean detectBump(float av, float speed, boolean isFixed) {
        float threshold = getThreshold(speed, isFixed);
        Log.i(TAG, "detectBump: av: " + av + ", speed: " + speed + ", threshold: " + threshold + ", isFixed: " + isFixed);
        if (av >= threshold) {
            return true;
        }
        return false;
    }

    public static float getMeanAv(List<RecordDetailsModel> records, float angleThreshold) {
        float avrAv = 0;
        int count = 0;
        for (RecordDetailsModel r : records) {
            if (!isValidRotationAngle(r, angleThreshold)) {
                continue;
            }
            avrAv += getAv(r);
            count++;
        }
        avrAv /= count;
        return avrAv;
    }

    public static float getSTD(List<RecordDetailsModel> records, float angleThreshold) {
        float sumAvs = 0;
        float subAvs = 0;
        int count = 0;
        float std = 0;
        float meanAv = getMeanAv(records, angleThreshold);
        for (RecordDetailsModel r : records) {
            //Feng Guo: how data value are put into records: based on time interval or distance
            //data values adding to records for interval based on calculated distance by GPS
            //e.g. if passed distance >= 100 meters then algorithm calculations started with collected
            // data from accelerometer)
            if (!isValidRotationAngle(r, angleThreshold)) {
                continue;
            }
            subAvs = getAv(r) - meanAv;
            sumAvs += subAvs * subAvs;
            count++;
        }
        std = (float) Math.sqrt(1.0f / (float) (count - 1) * sumAvs);
        return std;
    }

    public ProcessedDataModel calculate(List<RecordDetailsModel> records) {
        return calculate(records, -1);
    }

    public ProcessedDataModel calculate(List<RecordDetailsModel> records, double distance) {
        int bumpsCount = 0;
        float speedAvg = 0;
        float meanAv = 0;
        float sumAv = 0;
        int avgAvCount = 0;
        int count = 0;
        ProcessedDataModel processedData = new ProcessedDataModel();
        stateDetector.calcState(records);
        boolean isDeviceFixed = stateDetector.isDeviceFixedState();
        for (RecordDetailsModel r : records) {
            if (!isValidRotationAngle(r, ANGLE_THRESHOLD)) {
                Log.e(TAG, "[ Non valid RotationAngle detected ] Interval: "
                        + r.getTime() + ", angle: "
                        + r.getRotationAngle() + ", skip calculations for this value");
                continue;
            }
            final float av = getAv(r);
            sumAv += Math.abs(av);
            avgAvCount++;
            boolean isNextWindow = isNextWindow(r.getTime());
            boolean isEndOfList = count == records.size() - 1;
            if (isNextWindow || isEndOfList) {
                meanAv = sumAv / (float) avgAvCount;
                sumAv = 0;
                avgAvCount = 0;
                if (detectBump(meanAv, speedAvg / (float) count, isDeviceFixed)) {
                    bumpsCount++;
                    addBumpEvent(r, isDeviceFixed);
                    Log.i(TAG, "interval: " + processedData.getTime() + ", Mean Av: " + meanAv + ", bumpsCount: " + bumpsCount);
                }
            }
            speedAvg += r.getAverageSpeed();
            count++;
        }
        float stdDeviation = getSTD(records, ANGLE_THRESHOLD);
        if (!ValidateUtil.isValidNumber(stdDeviation) || stdDeviation <= 0) {
            Log.e(TAG, "[ Too low standard deviation value ] algorithm calculation stopped");
            return null;
        }
        float avgSpeed = speedAvg / (float) count;
        iri = calculateIRI(stdDeviation, avgSpeed, isDeviceFixed);
        RoadQuality roadQuality = getRoadQuality(iri);

        Helper.printLogMsg(TAG, "stddev: " + stdDeviation + ", IRI: " + iri + ", records count: " + records.size());
        Helper.printLogMsg(TAG, "isDeviceFixed: " + isDeviceFixed);
        Helper.printLogMsg(TAG, "road Quality: " + roadQuality.name());

        double latStart = 0;
        double lonStart = 0;
        double altStart = 0;
        double latEnd = 0;
        double lonEnd = 0;
        double altEnd = 0;
        if (records.get(0) != null) {
            latStart = records.get(0).getLatitude();
            lonStart = records.get(0).getLongitude();
            altStart = records.get(0).getAltitude();
            if (latStart == 0 || lonStart == 0) {
                latStart = records.get(0).getCurLatitude();
                lonStart = records.get(0).getCurLongitude();
            }
            if (altStart == 0) {
                altStart = records.get(0).getCurAltitude();
            }
        }
        int lastId = records.size() - 1;
        if (records.get(lastId) != null) {
            latEnd = records.get(lastId).getLatitude();
            lonEnd = records.get(lastId).getLongitude();
            altEnd = records.get(lastId).getAltitude();
            if (latEnd == 0 || lonEnd == 0) {
                latEnd = records.get(lastId).getCurLatitude();
                lonEnd = records.get(lastId).getCurLongitude();
            }
            if (altEnd == 0) {
                altEnd = records.get(lastId).getCurAltitude();
            }
        }
        if (distance >= 0) {
            processedData.setDistance(distance);
        }
        processedData.setMeasurementId(records.get(0).getMeasurementId());
        processedData.setIri(iri);
        processedData.setFixed(isDeviceFixed);
        processedData.setStdDeviation(stdDeviation);
        processedData.setBumps(bumpsCount);
        processedData.setCoordsStart(latStart, lonStart, altStart);
        processedData.setCoordsEnd(latEnd, lonEnd, altEnd);
        processedData.setSpeed(avgSpeed);
        processedData.setCategory(roadQuality);
        processedData.setItemsCount(records.size());
        return processedData;
    }

    private RoadQuality getRoadQuality(float iri) {
        if (iri < quality1) {
            return RoadQuality.EXCELLENT;
        } else if (iri > quality1 && iri <= quality2) {
            return RoadQuality.GOOD;
        } else if (iri > quality2 && iri <= quality3) {
            return RoadQuality.FAIR;
        } else {
            return RoadQuality.POOR;
        }
    }

    public float calculateIRI(float std, float speed, boolean isFixed) {
        float interceptConst = StatsFragment.getInstance().getIriTable().getIntercept(isFixed, std);
        float sdConst = StatsFragment.getInstance().getIriTable().getSd(isFixed, std);
        float speedConst = StatsFragment.getInstance().getIriTable().getSpeed(isFixed, std);
        float sdConstPow2 = StatsFragment.getInstance().getIriTable().getSdPow2(isFixed, std);
        float sdSpeedConst = StatsFragment.getInstance().getIriTable().getSdSpeed(isFixed, std);
        float iri = interceptConst + sdConst * std + speedConst * speed + sdConstPow2 * std * std + sdSpeedConst * std * speed;
        if (iri <= 1) {
            iri = IRI_DEFAULT_VALUE;
        }
        return iri;
    }

    public static StatsFragment getInstance() {
        if (instance == null) {
            instance = new StatsFragment();
//            throw new IllegalStateException("Application isn't initialized yet!");
        }
        return instance;
    }

    public IRITable getIriTable() {
        return iriTable;
    }
    // IRI module end

    boolean isTMapVisible = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchwidget:
                Intent intent = new Intent(getActivity(), SearchDirectionActivity.class);
//                Intent intent = new Intent(StatsFragment.this, GooglePlacesAutocompleteActivity.class);
                if (mCurrentLocation != null) {
                    intent.putExtra("curr_loc", mCurrentLocation);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                getActivity().startActivityForResult(intent, REQUEST_SEARCH_CODE);
                break;
            case R.id.ic_img_myloc:
                setMyLoc = true;
                mImgMyLoc.setVisibility(View.GONE);
                break;
            case R.id.menu:
                if (mImgMenu.getTag() == "search") {
                    ParentActivity activity = (ParentActivity) getActivity();
                    if (activity.getDrawer().isDrawerOpen(Gravity.START)) {
                        activity.getDrawer().closeDrawer(Gravity.START);
                    } else {
                        activity.getDrawer().openDrawer(Gravity.START);
                    }
                } else {
                    mImgMenu.setImageResource(R.drawable.ic_menu1);
                    mImgMenu.setTag("search");
                    mSearchLayout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.ic_img_mapselector:
//                if (mTMapView.getView().getVisibility() == View.VISIBLE) {
                if (isTMapVisible) {
                    isTMapVisible = false;
                    mapGMapView.getView().setVisibility(View.VISIBLE);
                    mMapSelector.setImageResource(R.drawable.tomtom_map_selector);
                } else if (mapGMapView.getView().getVisibility() == View.VISIBLE) {
                    isTMapVisible = true;
                    mapGMapView.getView().setVisibility(View.GONE);
                    mMapSelector.setImageResource(R.drawable.google_map_background_selector);
                }
                break;
//            case R.id.ic_btn_camera:
////                this.camera();
//                openGetPhNumDial();
//                break;
            case R.id.ic_img_graph:
                this.graph();
                break;
            default:
                break;
        }
    }

    public boolean onNavigationItemSelectedFrag(@NonNull MenuItem item) {
        int mode = ((RHAApplication) getActivity().getApplication()).getMODE();
        ParentActivity activity = (ParentActivity) getActivity();
        switch (item.getItemId()) {
            case R.id.home:
                Helper.printLogMsg(TAG, "Main Screen");
                activity.getDrawer().closeDrawer(Gravity.START);
                break;
            case R.id.upload_vid:
                if (mode == Constants.MODE.CAPTURE_DATA_MODE) {
                    Snackbar.make(mView, getString(R.string.feature_not_avl_capture_mode), Snackbar.LENGTH_LONG).show();
                    break;
                }
//                View view = item.getActionView();
//                ProgressBar pb = (ProgressBar) view.findViewById(R.id.ic_progress_view);
//                TextView tv = (TextView) view.findViewById(R.id.ic_txt_progress);
//                mDrawer.closeDrawer(Gravity.START);
                if (mFileDatas != null && mFileDatas.size() > 0) {
                    List<FileData> copiedList = new ArrayList<>();
                    for (int i = 0; i < mFileDatas.size(); i++) {
                        if (mFileDatas.get(i).getUpload_status() == Constants.FileUploadStatus.END_RECORD) {
                            copiedList.add(mFileDatas.get(i));
                        }
                    }

                    UploadVideoFile.getInstance(getContext()).startStopUpload(mFileDatas, messenger, mBuilder);

                    MenuItem menuItem = activity.getNavigationView().getMenu().findItem(R.id.upload_vid);
                    int count = copiedList.size();
                    if (count > 0) {
                        boolean isRecording = UploadVideoFile.getInstance(getContext()).isRecording();
                        if (isRecording) {
                            mTxtNotify.setVisibility(View.VISIBLE);
                            mTxtNotify.setText(String.valueOf(count));
                            menuItem.setTitle(status_uploading + "(" + count + ")");

                            NotificationCompat.Builder builder = buildVidNotifyBuilder(0, 0, false, "RHA", "Uploading(" + count + ")");
                            mNotManager.notify(Constants.NOTIFICATION_IDS.NOTIFICATION_VID_UPLOAD, builder.build());
                        } else {
                            mTxtNotify.setVisibility(View.VISIBLE);
                            mTxtNotify.setText(String.valueOf(count));
                            menuItem.setTitle(status_start + "(" + count + ")");

                            NotificationCompat.Builder builder = buildVidNotifyBuilder(0, 0, false, "RHA", count + " video files are left to upload");
                            mNotManager.notify(Constants.NOTIFICATION_IDS.NOTIFICATION_VID_UPLOAD, builder.build());
                        }
                    } else {
                        mTxtNotify.setVisibility(View.INVISIBLE);
                        mTxtNotify.setText("0");
                        menuItem.setTitle(status_noremain);

                        mNotManager.cancel(Constants.NOTIFICATION_IDS.NOTIFICATION_VID_UPLOAD);
                    }
                }
                break;
            case R.id.mode:
                Helper.printLogMsg(TAG, "mode change");
                showModeAlert();
                break;
        }
        return false;
    }

    public void showEndTripAlert() {
        final ParentActivity activity = (ParentActivity) getActivity();
        final int mode = ((RHAApplication) getActivity().getApplication()).getMODE();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle(getText(R.string.end_trip))
                .setMessage("Do you want to end the trip?")
                .setPositiveButton(getText(R.string.end), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mode == Constants.MODE.CAPTURE_DATA_MODE) {
                            setViewMode(false);
//                            set end time
                            if (mRhaTask != null) {
                                if (mRhaTask != null) {
                                    mRhaTask.setActualEndTime(Util.getDate_yyyy_MM_dd__HH_mm_ss(System.currentTimeMillis()));
                                }
                            }
                        }
                        mEndTripDialog.dismiss();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mEndTripDialog.dismiss();
                    }

                });
        mEndTripDialog = builder.create();
        mEndTripDialog.show();
    }

    private void showModeAlert() {
        final ParentActivity activity = (ParentActivity) getActivity();
        final int mode = ((RHAApplication) getActivity().getApplication()).getMODE();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle("Change Mode")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.getDrawer().closeDrawer(Gravity.START);
                        activity.getNavigationView().setCheckedItem(R.id.home);
                        if (mode == Constants.MODE.VIEW_MODE) {
                            setCaptureMode();
                        } else if (mode == Constants.MODE.CAPTURE_DATA_MODE) {
                            setViewMode(false);
                        }
                        mModeChangeAlert.dismiss();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.getDrawer().closeDrawer(Gravity.START);
                        activity.getNavigationView().setCheckedItem(R.id.home);
                        mModeChangeAlert.dismiss();
                    }

                });
        if (mode == Constants.MODE.VIEW_MODE) {
            builder.setMessage("Do you want to change the application mode to capture mode?");
        } else if (mode == Constants.MODE.CAPTURE_DATA_MODE) {
            builder.setMessage("Do you want to change the application mode to View mode?");
        }
        mModeChangeAlert = builder.create();
        mModeChangeAlert.show();
    }

    private void setCaptureMode() {

        if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Helper.printLogMsg(TAG, "External storage Permission is granted");
            checkUpdateOrStartService();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERM_REQ_EXTERNAL_CODE);
        }

        if (getActivity() instanceof ParentActivity) {
            MenuItem menuItem = ((ParentActivity) getActivity()).getNavigationView().getMenu().findItem(R.id.mode);
            menuItem.setTitle(getString(R.string.view_mode));
        }

        ((RHAApplication) getActivity().getApplication()).setMode(Constants.MODE.CAPTURE_DATA_MODE);
    }

    private void setViewMode(boolean exit) {
        if (mServiceMessanger != null) {
            Message msg = Message.obtain(null, CaptureDataService.MSG_TRIGGER_FILE_UPLOAD);
            Bundle bundle = new Bundle();
            bundle.putBoolean("close_app", exit);
            msg.setData(bundle);
            try {
                mServiceMessanger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        if (getActivity() instanceof ParentActivity) {
            MenuItem menuItem = ((ParentActivity) getActivity()).getNavigationView().getMenu().findItem(R.id.mode);
            menuItem.setTitle(getString(R.string.capture_data_mode));
        }
        ((RHAApplication) getActivity().getApplication()).setMode(Constants.MODE.VIEW_MODE);
    }

    private void drawRoute() {
        map.clear();
        mGraphResponse = null;
        mImgGraph.setVisibility(View.INVISIBLE);
        setMyLoc = false;
        mImgMyLoc.setVisibility(View.VISIBLE);

        final double slat = srcLatlng.latitude;
        final double slng = srcLatlng.longitude;

        LatLng barcelona = new LatLng(slat, slng);
        MarkerOptions mo = new MarkerOptions()
                .position(barcelona)
                .title("Source")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        map.addMarker(mo);

        final double dlat = destLatlng.latitude;
        final double dlng = destLatlng.longitude;

        LatLng madrid = new LatLng(dlat, dlng);
        map.addMarker(new MarkerOptions().position(madrid).title("Destination").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        //Define list to get all latlng for the route

        Handler handler = new Handler();
        handler.post(new Runnable() {

            @Override
            public void run() {
//                List<LatLng> path = new ArrayList<>();
                String origin = slat + "," + slng;
                String dest = dlat + "," + dlng;
//                String googleApiKey = "AIzaSyC7V9uWEfYabkwgSNF2zyDedJcHilCDIpM";
                String googleApiKey = "AIzaSyCFZyKKvqUSacq3gDLw5rCfgFohQMYWyKI";
                String tomtomApiKey = "93lGMTmQuN5JmgZqR0HGb3TwYvrDSz6i";

//                    URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin="
//                            + origin + "&destination=" + dest + "&travel_mode=DRIVING" +
//                            "&key=" + googleApiKey);
                String data = "";
                InputStream iStream = null;
                HttpURLConnection urlConnection = null;
                try {
                    /* for google
                    URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin="
                            + origin + "&destination=" + dest + "&travel_mode=DRIVING" +
                            "&key=" + googleApiKey);
                            */
                    /* for tomtom */
                    URL url = new URL("https://api.tomtom.com/routing/1/calculateRoute/" + origin +
                            ":" + dest +
                            "/json?&travelMode=car&key=" + tomtomApiKey);

                    ServerAsyncTask serverAsyncTask = new ServerAsyncTask(getContext(), serverHelper);
                    serverHelper.setUrl(url.toString());
                    serverAsyncTask.showCallProgress(false);
                    serverAsyncTask.execute();

                } catch (Exception e) {
                    Helper.printLogMsg("Exception while reading url", e.toString());
                }

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(slat, slng)).zoom(16).build();
                map.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));
            }
        });
    }

    ServerHelper serverHelper = new ServerHelper() {

        @Override
        public void onFailure(Object o) throws NullPointerException {
        }

        @Override
        public void onSuccess(Object o) throws NullPointerException {
//            Helper.printLogMsg(TAG, "url: " + url.toString());
            if (o != null && String.valueOf(o).length() > 0) {
                try {
                    String data = String.valueOf(o);
                    if (data != null && data.length() > 0) {
                        JSONObject object = new JSONObject(data);
                        String status = object.optString("status");
                        if (status.equals("OVER_QUERY_LIMIT")) {
                            //                                attempt += 1;
                            //                                if (attempt == 2) {
                            //                                    googleApiKey = Constants.GOOGLE_API_KEY_LIVE2;
                            //                                } else if (attempt == 3) {
                            //                                    googleApiKey = Constants.GOOGLE_API_KEY_LIVE3;
                            //                                }
                            //                                data = getDirections(origin, wayPts, destination, attempt, googleApiKey);
                        } else {
                            mImgGraph.setVisibility(View.VISIBLE);
                            drawRoute(data);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServerError(String message) {
        }
    };

    private void drawRoute(String jsonRoute) {
        try {
            JSONObject jObject = new JSONObject(jsonRoute);

            PathJSONParser parser = new PathJSONParser();
            List<List<HashMap<String, String>>> routes = parser.parse(jObject, MapUtility.TOM_TOM_API);
            drawRoute(routes);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
//                            for (AssosiateDetail assosiateDetailobj : assosiateDetail) {
//                                builder.include(assosiateDetailobj.getPosition());
//                            }
//                            LatLngBounds bounds = builder.build();
//
//                            int padding = 120; // offset from edges of the map in pixels
//                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//
            CameraUpdate cu = CameraUpdateFactory.newLatLng(srcLatlng);
            map.animateCamera(cu);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void drawRoute(List<List<HashMap<String, String>>> routes) {

        ArrayList<LatLng> points = null;
        PolylineOptions polyLineOptions = null;
        if (routes != null && routes.size() > 0) {
            points = new ArrayList<LatLng>();
//                for (int i = 0; i < routes.size(); i++) {
            polyLineOptions = new PolylineOptions();

            List<HashMap<String, String>> path = routes.get(0);

//                LatLng tmpPos = null;
            String lineStr = "'LINESTRING(";
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);
                lineStr += lng + " " + lat;
                if (j < path.size() - 1) {
                    lineStr += ", ";
                }
//                    if (tmpPos != null && j % 9 == 0) {
//                        double headRotation = SphericalUtil.computeHeading(tmpPos, position);
//                        googleMap.addMarker(new MarkerOptions().position(tmpPos).flat(true).rotation((float) headRotation).icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow_map)));
//                    }
//                    tmpPos = position;

                points.add(position);
            }
            lineStr += ")'";

            try {
                mGraphResponse = new JSONObject();
                mGraphResponse.put("Data", lineStr);
                Helper.printLogMsg(TAG, mGraphResponse.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            polyLineOptions.addAll(points);
            polyLineOptions.width(8);
//            polyLineOptions.color(Color.argb(255, 125, 144, 250));
            polyLineOptions.color(Color.GREEN);

//                }
            if (polyLineOptions != null) {
                Polyline polyline = map.addPolyline(polyLineOptions);
            }
        }
    }

    public String getAddressFromLocation(LatLng location) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses = null;
        String loc = null;
        try {
            addresses = geocoder.getFromLocation(
                    location.longitude,
                    location.latitude,
                    // In this sample, get just a single address.
                    1);
            Helper.printLogMsg("location: ", "" + location.latitude + "\n" + location.longitude);

        } catch (IOException ioException) {
            // Catch network or other I/O problems.
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
        }
        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            Log.e(TAG, "no_address_found");
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();
            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }

            loc = TextUtils.join(System.getProperty("line.separator"), addressFragments);
        }
        return loc;
    }

    private void graph() {
        if (mGraphResponse != null) {
            getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            Intent myIntent = new Intent(getActivity(), Graph_1.class);

            myIntent.putExtra("route_data", mGraphResponse.toString());
            startActivity(myIntent);
        } else {
            Snackbar.make(mView, getString(R.string.data_not_avl_for_route), Snackbar.LENGTH_LONG).show();
        }
    }

    private void openGetPhNumDial() {
        int mode = ((RHAApplication) getActivity().getApplication()).getMODE();
        // check weather mode is capture data then only open the camera.

        if (mode == Constants.MODE.VIEW_MODE) {
            Snackbar.make(mView, getString(R.string.feature_not_avl_view_mode), Snackbar.LENGTH_LONG).show();
            return;
        }

        if (getActivity().checkSelfPermission(CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{CAMERA, Manifest.permission.RECORD_AUDIO},
                    PERM_CAM_REQ_CODE);
        } else {
            final Dialog captureDial = new Dialog(getContext(), R.style.FilterDialogTheme);
            captureDial.setTitle("Capture!");

            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.dial_get_phone, null);
            captureDial.setContentView(view);
//            captureDial.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            final EditText editPhNum = (EditText) view.findViewById(R.id.ic_edit_phone);
            String phNum = CyientSharePrefrence.getStringFromSharePef(getContext(), SharePrefrenceConstant.MOB_NUM_CAM);

            if (phNum != null && phNum.length() > 0) {
                editPhNum.setText(phNum);
                editPhNum.setEnabled(false);
            } else {
                editPhNum.setEnabled(true);
                editPhNum.setFocusable(true);
            }
            ImageView imgPhNum = (ImageView) captureDial.findViewById(R.id.ic_img_edphnum);
            imgPhNum.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    editPhNum.setEnabled(true);
                    editPhNum.setSelection(0, editPhNum.getText().length());
                    editPhNum.setFocusable(true);
                    editPhNum.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editPhNum, InputMethodManager.SHOW_IMPLICIT);
                }
            });

            Button btnCapture = (Button) view.findViewById(R.id.ic_btn_capture);
            btnCapture.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    phone2 = editPhNum.getText().toString();

                    String regexStr = "^[+]?[0-9]{10,13}$";
                    if (phone2 != null && phone2.length() > 6 && phone2.length() <= 13 && phone2.matches(regexStr)) {
                        CyientSharePrefrence.setStringInSharePef(getContext(), SharePrefrenceConstant.MOB_NUM_CAM, phone2);
                        camera();
                        captureDial.cancel();
                    } else {
                        if (phone2.length() == 0) {
                            editPhNum.setError("Phone number should not be empty.");
                        } else if (phone2.length() < 6 || phone2.length() > 13 || !phone2.matches(regexStr)) {
                            editPhNum.setError("Please enter valid phone number.");
                        }
                    }
                }
            });
            captureDial.getWindow().getDecorView().setBackground(getActivity().getDrawable(R.drawable.round_corner));
            captureDial.show();
        }
    }

    //29/12
    public void camera() {
        if (getActivity().checkSelfPermission(CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{CAMERA, Manifest.permission.RECORD_AUDIO},
                    PERM_CAM_REQ_CODE);
        } else {
            Intent intent = new Intent(getActivity(), CamVideo.class);
            if (mRhaTask != null) {
                intent.putExtra("rha_task", mRhaTask);
            }
            startActivity(intent);
        }
    }

    /**
     * This method checks whether the user is connected to a network or not
     *
     * @param context
     * @return :true, if the user is connected to a network ,else returns false
     */
    public boolean isOnline(StatsFragment context) {
        if (context == null) {
            return false;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.isConnectedOrConnecting();
        } else {
            return false;
        }
    }

    @SuppressLint("MissingPermission")
    private void updateMaps() {

        if (setMyLoc && map != null && mCurrentLocation != null) {
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation
                    .getLatitude(), mCurrentLocation.getLongitude()), 15.0f);
            map.animateCamera(update);
//            changeMap(mCurrentLocation);
            map.getUiSettings().setZoomControlsEnabled(false);
            animateMarker(mCurrentLocation);

            if (mTMap != null) {
                com.tomtom.online.sdk.map.CameraPosition cp = com.tomtom.online.sdk.map.CameraPosition
                        .builder(new com.tomtom.online.sdk.common.location.LatLng(
                                mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()
                        ))
                        .zoom(15)
                        .build();
                mTMap.centerOn(cp);
                mTMap.clear();
                MarkerBuilder mb = new MarkerBuilder(new com.tomtom.online.sdk.common.location.LatLng(
                        mCurrentLocation.getLatitude(),
                        mCurrentLocation.getLongitude()
                ));
                com.tomtom.online.sdk.map.Marker marker = mTMap.addMarker(mb);
                marker.canShowBalloon(true);
            }
        }
    }

    public void animateMarker(final Location location) {

        if (map == null) {
            return;
        }

        if (currLocMarker == null) {
            MarkerOptions mo = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation_navigation));
            currLocMarker = map.addMarker(mo);
        }

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final LatLng startLatLng = currLocMarker.getPosition();
        final double startRotation = currLocMarker.getRotation();
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {

            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);

                double lng = t * location.getLongitude() + (1 - t)
                        * startLatLng.longitude;
                double lat = t * location.getLatitude() + (1 - t)
                        * startLatLng.latitude;

                float rotation = (float) (t * location.getBearing() + (1 - t)
                        * startRotation);

                currLocMarker.setPosition(new LatLng(lat, lng));
                currLocMarker.setRotation(rotation);

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    public void onActivityResultFrag(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SEARCH_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        srcLatlng = data.getParcelableExtra("srcloc");
                        destLatlng = data.getParcelableExtra("destloc");
                        if (mTMapView.getView().getVisibility() == View.VISIBLE) {
                            mTMapView.getView().setVisibility(View.GONE);
                            mapGMapView.getView().setVisibility(View.VISIBLE);
                            mMapSelector.setImageResource(R.drawable.tomtom_map_selector);
                        }
                        drawRoute();
                        mImgMenu.setImageResource(R.drawable.round_keyboard_backspace_24);
                        mImgMenu.setTag("route");
                        mSearchLayout.setVisibility(View.GONE);
//                        Animation animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(),
//                                R.anim.slide_down);
                        // Slide Down
//                        sas
                        break;
                }
                break;
            case REQUEST_CHECK_SETTINGS:
                if (resultCode == RESULT_OK) {
                    if (mServiceMessanger != null) {
                        Message msg = Message.obtain(null, CaptureDataService.MSG_LOC_SETTING_ENABLED);
                        try {
                            mServiceMessanger.send(msg);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    initLocUpdate();
                }
                break;
            case REQUEST_OVERLAY_PERMISSION_CODE:
                break;
            case REQUEST_VIDEO:

                break;
        }
    }

    //    @Override
    public void onRequestPermissionsResultFrag(int requestCode, String permissions[],
                                               int[] grantResults) {

        boolean permAccepted = grantResults != null && grantResults.length > 0 ? grantResults[0] == PackageManager.PERMISSION_GRANTED : false;

        switch (requestCode) {

            case PERM_REQ_CODE:
                if (permissions != null && permissions.length > 0) {
                    for (String permission : permissions) {
                        if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION) || permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                            if (grantResults.length > 0) {

                                if (permAccepted) {
                                    //
                                    if (mServiceMessanger != null) {
                                        Message msg = Message.obtain(null, CaptureDataService.MSG_PERM_REQ_LOC_GRANTED);
                                        try {
                                            mServiceMessanger.send(msg);
                                        } catch (RemoteException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

//                    boolean writeSdCardAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                                if (permAccepted /*&& writeSdCardAccepted*/) {
//                        Snackbar.make(mView, "Permission Granted, Now you can access LOCATION DATA, WRITE STORAGE and CAMERA.", Snackbar.LENGTH_LONG).show();
                                    //  resetRecordStats();
                                } else {

                                    Snackbar.make(mView, "Permission Denied, You cannot access LOCATION DATA. ", Snackbar.LENGTH_LONG).show();

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                            showMessageOKCancel("You need to allow access to both the permissions",

                                                    new DialogInterface.OnClickListener() {

                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            Log.d("dialog status --->", "" + which);
                                                            if (which == -1) {
                                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                                    reqPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                                                            Manifest.permission.ACCESS_FINE_LOCATION});
                                                                }
                                                            } else {
                                                                getActivity().finish();
                                                            }
                                                        }
                                                    });
                                            return;
                                        }
                                    }
                                }
                            }
                        }

                        if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            if (permAccepted) {
                                if (mServiceMessanger != null) {
                                    Message msg = Message.obtain(null, CaptureDataService.MSG_PERM_REQ_FILE_GRANTED);
                                    try {
                                        mServiceMessanger.send(msg);
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case PERM_CAM_REQ_CODE:
                if (permAccepted) {
                    openGetPhNumDial();
                }
                break;
            case PERM_REQ_EXTERNAL_CODE:
                if (permAccepted) {
                    Helper.printLogMsg(TAG, "external storage permission granted");
                    checkUpdateOrStartService();
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void clearRecordStats() {
        chrono.setTextColor(Color.BLACK);
        chrono.setBase(SystemClock.elapsedRealtime());
        chrono.stop();
        map.clear();
    }

    public void resetRecordStats() {
        chrono.setTextColor(Color.RED);
        chrono.setBase(SystemClock.elapsedRealtime());
        chrono.stop();
        chrono.start();
    }

    public void startRecordFrmLast(int secs) {
        chrono.setTextColor(Color.RED);
        chrono.stop();
        chrono.setBase(SystemClock.elapsedRealtime() - (secs * 1000));
        chrono.start();
    }

    BroadcastReceiver myBroadCastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(VID_INTENT_FILTER)) {
                int status_flag = intent.getIntExtra(VID_STATUS_FLAG, -1);
                if (status_flag == UploadFileTasks.VIDEO_STARTED) {
                    Helper.printLogMsg(TAG, "video started");
                    String fileName = intent.getStringExtra(VID_FILENAME);

                    if (mServiceMessanger != null) {
                        String lastChronoTime = chrono.getText().toString();
                        int mins = Integer.valueOf(lastChronoTime.substring(0, lastChronoTime.indexOf(':')));
                        mLastChronoSecs = mins * 60 + Integer.valueOf(lastChronoTime.substring(lastChronoTime.indexOf(':') + 1));

                        Message msg = Message.obtain(null, CaptureDataService.MSG_VIDEO_STARTED);
                        try {
                            Bundle bundle = new Bundle();
                            bundle.putString("filename", fileName);
                            msg.setData(bundle);
                            mServiceMessanger.send(msg);

                            chrono.stop();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                } else if (status_flag == UploadFileTasks.VIDEO_STOPPED) {
                    Helper.printLogMsg(TAG, "video stopped: chrono time: " + chrono.getText() + " " + SystemClock.elapsedRealtime());
                    if (mServiceMessanger != null) {
                        Message msg = Message.obtain(null, CaptureDataService.MSG_VIDEO_STOPPED);
                        try {
                            Bundle bundle = new Bundle();
                            bundle.putInt("time_elapsed", mLastChronoSecs);
                            msg.setData(bundle);
                            mServiceMessanger.send(msg);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                    startRecordFrmLast(mLastChronoSecs);
                }
            }
        }
    };

    @Override
    public void onMapReady(@NonNull TomtomMap tomtomMap) {
        mTMap = tomtomMap;
        mTMap.setMyLocationEnabled(true);

        mTMap.getGestureDetector().addOnMapDragListener(new OnMapDragListener() {
            @Override
            public void onMapDragStarted() {
                setMyLoc = false;
                mImgMyLoc.setVisibility(View.VISIBLE);
                Helper.printLogMsg(TAG, "onMapDragStarted");
            }

            @Override
            public void onMapDragEnded() {
            }

            @Override
            public void onMapDragging(PointF pointF) {
            }
        });

    }

    private void reqOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getActivity().getPackageName()));
                startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION_CODE);
            }
        }
    }

    private void reqPermissions(String[] permissions) {
        Helper.printLogMsg(TAG, "get msg from service");
        ActivityCompat.requestPermissions(getActivity(), permissions, PERM_REQ_CODE);
        reqOverlayPermission();
    }

    ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Helper.printLogMsg(TAG, "onServiceConnected");
            mServiceMessanger = new Messenger(service);
            Message msg = Message.obtain(null, CaptureDataService.MSG_TYPE_REGISTER);
            msg.replyTo = messenger;

            try {
                mServiceMessanger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Helper.printLogMsg(TAG, "onServiceDisconnected");
            mServiceMessanger = null;
        }
    };

    private void initLocUpdate() {
        @SuppressLint("RestrictedApi")
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(new LocationRequest());

        SettingsClient client = LocationServices.getSettingsClient(getContext());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {

            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                if (mServiceMessanger != null) {
                    Message msg = Message.obtain(null, CaptureDataService.MSG_LOC_SETTING_ENABLED);
                    try {
                        mServiceMessanger.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        task.addOnFailureListener(getActivity(), new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(getActivity(),
                            REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                }
            }
        });
    }

    private class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Menu menu = null;
            int state = msg.what;
            switch (state) {
                case MSG_VID_FILE_UPLOAD_START:
                    if (getActivity() instanceof ParentActivity) {
                        ParentActivity activity = (ParentActivity) getActivity();
                        menu = activity.getNavigationView().getMenu();
                        if (menu != null) {
                            MenuItem menuItem = menu.findItem(R.id.upload_vid);
                            ProgressBar progress = (ProgressBar) menuItem.getActionView().findViewById(R.id.ic_progress_view);
                            TextView tvprogress = (TextView) menuItem.getActionView().findViewById(R.id.ic_txt_progress);
                            progress.setProgress(0);
                            tvprogress.setText("0%");
                        }
                        Helper.printLogMsg(TAG, "Video File Upload start");
                    }
                    break;
                case MSG_VID_FILE_UPLOAD_PROGRESS:
                    if (getActivity() instanceof ParentActivity) {
                        ParentActivity activity = (ParentActivity) getActivity();
                        menu = activity.getNavigationView().getMenu();
                        if (menu != null) {
                            MenuItem menuItem = menu.findItem(R.id.upload_vid);
                            ProgressBar progress = (ProgressBar) menuItem.getActionView().findViewById(R.id.ic_progress_view);
                            TextView tvprogress = (TextView) menuItem.getActionView().findViewById(R.id.ic_txt_progress);

                            int perc = msg.arg1;
                            progress.setProgress(perc);
                            tvprogress.setText(perc + "%");
                        }
                    }
                    break;
                case MSG_VID_FILE_UPLOAD_END:
                    if (getActivity() instanceof ParentActivity) {
                        ParentActivity activity = (ParentActivity) getActivity();
                        menu = activity.getNavigationView().getMenu();
                        if (menu != null) {
                            MenuItem menuItem = menu.findItem(R.id.upload_vid);
                            ProgressBar progress = (ProgressBar) menuItem.getActionView().findViewById(R.id.ic_progress_view);
                            TextView tvprogress = (TextView) menuItem.getActionView().findViewById(R.id.ic_txt_progress);
                            progress.setProgress(0);
                            tvprogress.setText("0%");
                        }
                        Helper.printLogMsg(TAG, "Video File Upload end");
                    }
                    break;
                case MSG_PROMPT_LOC_ENABLE:
                    initLocUpdate();
                    break;
                case MSG_SENSOR_NOT_AVL:
                    Snackbar.make(mView, getString(R.string.err_sensor_notavl), Snackbar.LENGTH_LONG).show();
                    break;
                case MSG_UPDATE_LOCONMAP:
                    mCurrentLocation = msg.getData().getParcelable("curr_loc");
                    updateMaps();
                    break;
                case MSG_UPDATE_UI_FIELDS:
                    Bundle data = msg.getData();
                    double heading = data.getDouble("heading");
                    double speed = data.getDouble("speed");
                    heading_tv.setText("Heading\n" + heading);
                    speed_tv.setText("Speed\n" + speed);
                    break;
                case MSG_REQ_PERMISSIONS:
                    Bundle bundle = msg.getData();
                    String[] permission = bundle.getStringArray("permissions");
                    reqPermissions(permission);
                    break;
                case MSG_FILE_UPLOAD_START:
                    resetRecordStats();
                    break;
                case MSG_FILE_UPLOADED:
                    resetRecordStats();
                    Helper.printLogMsg(TAG, getString(R.string.file_uploaded));
                    break;
                case MSG_SHOW_FILEPROGRESS:
                    handleProgress(getString(R.string.file_uploading), true, false, false);
                    break;
                case MSG_CANCEL_FILE_PROGRESS:
                    String resmsg = msg.getData().getString("message");
                    boolean closeApp = msg.getData().getBoolean("close_app");
                    handleProgress(resmsg, false, true, closeApp);
                    break;
                case MSG_SHOW_ACCURACY_ALERT:
                    if (mAccuracyAlert == null) {
                        mAccuracyAlert = Snackbar.make(mView, getString(R.string.loc_not_accurate), Snackbar.LENGTH_INDEFINITE);
                    }
                    if (!mAccuracyAlert.isShown()) {
                        View view = mAccuracyAlert.getView();
                        mAccuracyAlert.setActionTextColor(Color.RED);
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                        params.gravity = Gravity.CENTER;
                        view.setLayoutParams(params);
                        mAccuracyAlert.show();
                    }
                    break;
                case MSG_CLOSE_ACCURACY_ALERT:
                    if (mAccuracyAlert != null && mAccuracyAlert.isShown()) {
                        mAccuracyAlert.dismiss();
                    }
                    break;
            }
        }
    }

    private void handleProgress(String msg, boolean show, boolean cancel, boolean closeApp) {
        if (show) {
            mFileProgDialog = new Dialog(getContext());

            View view = getLayoutInflater().inflate(R.layout.dial_fileupload_n_exit, null);

            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.ic_progressbar);
            progressBar.setVisibility(View.VISIBLE);

            TextView textView = (TextView) view.findViewById(R.id.ic_txt_msg);
            textView.setText(msg);

            mFileProgDialog.setContentView(view);
            mFileProgDialog.setCancelable(false);
            mFileProgDialog.show();
        } else if (mFileProgDialog != null && cancel) {
            ProgressBar progressBar = (ProgressBar) mFileProgDialog.findViewById(R.id.ic_progressbar);
            progressBar.setVisibility(View.GONE);

            TextView textView = (TextView) mFileProgDialog.findViewById(R.id.ic_txt_msg);
            textView.setText(msg);

            if (!msg.equalsIgnoreCase(getString(R.string.file_uploaded))) {
                Button btnRetry = (Button) mFileProgDialog.findViewById(R.id.ic_btn_retry);
                btnRetry.setVisibility(View.VISIBLE);
                btnRetry.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mFileProgDialog.dismiss();
                        if (mServiceMessanger != null) {
                            Message msg = Message.obtain(null, CaptureDataService.MSG_TRIGGER_FILE_UPLOAD);
                            try {
                                mServiceMessanger.send(msg);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }

            Button btnExit = (Button) mFileProgDialog.findViewById(R.id.ic_btn_exit);
            if (!closeApp) {
                btnExit.setText(getText(R.string.done));
            } else {
                btnExit.setText(getText(R.string.exit));
            }
            btnExit.setVisibility(View.VISIBLE);
            btnExit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mFileProgDialog.dismiss();
                    clearRecordStats();
                    if (mConnection != null) {
                        getActivity().unbindService(mConnection);
                    }
                    if (((ParentActivity) getActivity()).getNavigationView().getMenu().getItem(7).isChecked()) {
                        TaskMessage msg = new TaskMessage();
                        msg.setFromClass(StatsFragment.class.getName());
                        msg.setMsg(TaskMessage.POP_STACK);
                        msg.setTag(mRhaTask);
                        ViewModelProviders.of(getActivity()).get(TaskViewModel.class).sendMessage(msg);
                    } else if (closeApp) {
                        getActivity().finish();
                    }
                }
            });
        }
    }

}