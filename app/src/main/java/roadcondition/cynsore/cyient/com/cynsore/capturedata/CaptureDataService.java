package roadcondition.cynsore.cyient.com.cynsore.capturedata;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.LifecycleService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import androidx.annotation.RequiresApi;
import roadcondition.cynsore.cyient.com.cynsore.R;
import roadcondition.cynsore.cyient.com.cynsore.SQLDataBase.FileDataRepository;
import roadcondition.cynsore.cyient.com.cynsore.model.RHATask;
import roadcondition.cynsore.cyient.com.cynsore.model.SensorFileData;
import roadcondition.cynsore.cyient.com.cynsore.utility.Constants;
import roadcondition.cynsore.cyient.com.cynsore.utility.CyientSharePrefrence;
import roadcondition.cynsore.cyient.com.cynsore.utility.Helper;
import roadcondition.cynsore.cyient.com.cynsore.utility.SharePrefrenceConstant;
import roadcondition.cynsore.cyient.com.cynsore.view.censor.StatsFragment;
import roadcondition.cynsore.cyient.com.cynsore.view.main.ParentActivity;

public class CaptureDataService extends LifecycleService implements SensorEventListener, UploadFileTasks {

    private final String TAG = "CaptureDataService";
    /* ---------------------- Sensor data ------------------- */
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor head;
    private Sensor gyro;
    private float linear_acc_x = 0;
    private float linear_acc_y = 0;
    private float linear_acc_z = 0;
    private float heading = 0;

    private float gyro_x = 0;
    private float gyro_y = 0;
    private float gyro_z = 0;

    private double latitude = 0.0;
    private double longitude = 0.0;
    private float speed = 0;
    private float mAccuracy = 0f;

    private Location mCurrlocation;

    private long UPDATE_INTERVAL = 2000;
    private long FASTEST_INTERVAL = UPDATE_INTERVAL / 2;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;

    public static final int MSG_TYPE_REGISTER = 10;
    public static final int MSG_PERM_REQ_LOC_GRANTED = 11;
    public static final int MSG_PERM_REQ_FILE_GRANTED = 12;
    public static final int MSG_TRIGGER_FILE_UPLOAD = 13;
    public static final int MSG_LOC_SETTING_ENABLED = 14;
    public static final int MSG_SHOW_LAYER = 15;
    public static final int MSG_CLOSE_LAYER = 16;
    public static final int MSG_VIDEO_STARTED = 17;
    public static final int MSG_VIDEO_STOPPED = 18;

    private Messenger mResMessanger;
    private Messenger messenger = new Messenger(new IncomingHandler());
    private WriteInFile mWriteInFile;

    private FileUploadService mUploadService;

    private static final int NOTIFICATION_ID = 100;

    private WindowManager mWindowManager;
    private RelativeLayout mRootContainer;
    private RHATask mRhaTask;

    public CaptureDataService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        Helper.printLogMsg(TAG, "onBind");

        Object obj = intent.getParcelableExtra("rha_task");
        if (obj != null && obj instanceof RHATask) {
            mRhaTask = (RHATask) obj;
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        head = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, head, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_NORMAL);

        mWriteInFile = new WriteInFile();

        startForegroundService();
        return messenger.getBinder();
    }

    /* Used to build and start foreground service. */
    private void startForegroundService() {
        Helper.printLogMsg(TAG, "Start foreground service.");

        // Create notification default intent.
        Intent intent = new Intent(this, ParentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Create notification builder.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Make notification show big text.
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("RHA");
        bigTextStyle.bigText("Road Runner is capturing data.");
        // Set big text style.
        builder.setStyle(bigTextStyle);

        builder.setWhen(System.currentTimeMillis());
//        Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//        builder.setLargeIcon(largeIconBitmap);
        builder.setSmallIcon(R.drawable.ic_rr_ticker);
        // Make the notification max priority.
        builder.setPriority(Notification.PRIORITY_MAX);
        // Make head-up notification.
        builder.setContentIntent(pendingIntent);//setFullScreenIntent(pendingIntent, true);
        builder.setAutoCancel(true);

        // Build the notification.
        Notification notification = builder.build();

        // Start foreground service.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyOwnForeground();
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_rr_ticker)
                .setContentTitle("Road Runner is capturing data.")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(NOTIFICATION_ID, notification);
    }

    private void stopForegroundService() {
        Helper.printLogMsg(TAG, "Stop foreground service.");

        // Stop foreground service and remove the notification.
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();
    }

    LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Helper.printLogMsg(TAG, "onLocationResult");
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last mCurrlocation in the list is the newest
                mCurrlocation = locationList.get(locationList.size() - 1);
                Helper.printLogMsg(TAG, "Accuracy: " + mCurrlocation.getAccuracy());
//                if (mCurrlocation.hasAccuracy() && mCurrlocation.getAccuracy() < 20) {
                latitude = mCurrlocation.getLatitude();
                longitude = mCurrlocation.getLongitude();
                mAccuracy = mCurrlocation.getAccuracy();
//                    sendMsgToActivity(StatsActivity.MSG_CLOSE_ACCURACY_ALERT);
//                } else {
//                    sendMsgToActivity(StatsActivity.MSG_SHOW_ACCURACY_ALERT);
//                }
            }

            if (mCurrlocation.hasSpeed()) {
                //speed = mCurrentLocation.getSpeed();
                speed = (int) ((mCurrlocation.getSpeed() * 3600) / 1000);
            }

            if (mResMessanger != null) {
                Message msg = Message.obtain(null, StatsFragment.MSG_UPDATE_LOCONMAP);
                try {
                    msg.getData().putParcelable("curr_loc", mCurrlocation);
                    mResMessanger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void sendMsgToActivity(int msgIndex) {
        if (mResMessanger != null) {
            Message msg = Message.obtain(null, msgIndex);
            try {
                mResMessanger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
//        jobDispatcher.cancelAll();
        if (mWriteInFile != null) {
            mWriteInFile.enddata();
        }
        mUploadService.cancel();
        Helper.printLogMsg(TAG, "onUnbind");
        sensorManager.unregisterListener(this);
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        stopForegroundService();
        closeHeadLayer();
        stopSelf();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Helper.printLogMsg(TAG, "onDestory");
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            linear_acc_x = event.values[0];
            linear_acc_y = event.values[1];
            linear_acc_z = event.values[2];
        } else if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            heading = Math.round(event.values[0]);
            if (heading >= 270) {
                heading = heading + 90;
                heading = heading - 360;
            } else {
                heading = heading + 90;
            }
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyro_x = event.values[0];
            gyro_y = event.values[1];
            gyro_z = event.values[2];
        }
//        Helper.printLogMsg(TAG, linear_acc_x + linear_acc_y + linear_acc_z + heading + gyro_x + gyro_y + gyro_z + "");
//        bad_road_conditions_tv.setText("Bad Road Conditions: " + Count);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

//    public String getCurrentFile() {
//        if (mWriteInFile != null) {
//            return mWriteInFile.mFileName;
//        }
//        return "";
//    }

    private void initHeadLayer() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                return;
            }
        }

        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mRootContainer = (RelativeLayout) inflater.inflate(R.layout.service_head_view, null, false);
        mRootContainer.setVisibility(View.VISIBLE);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE | WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL | Gravity.END;

        mWindowManager.addView(mRootContainer, layoutParams);

        AppCompatImageButton imageButton = (AppCompatImageButton) mRootContainer.findViewById(R.id.btn_head);

        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Create notification default intent.
                Intent intent = new Intent(CaptureDataService.this, ParentActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                closeHeadLayer();
            }
        });
    }

    private void closeHeadLayer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                return;
            }
        }

        try {
            if (mWindowManager != null && mRootContainer != null) {
                mRootContainer.setVisibility(View.GONE);
                mWindowManager.removeView(mRootContainer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class IncomingHandler extends Handler {

        @SuppressLint({"RestrictedApi", "MissingPermission"})
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_LOC_SETTING_ENABLED:
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    break;
                case MSG_PERM_REQ_LOC_GRANTED:
                    Helper.printLogMsg(TAG, "Location Permission Granted");
                    if (mResMessanger != null) {
                        Message msgprompt = Message.obtain(null, StatsFragment.MSG_PROMPT_LOC_ENABLE);
                        try {
                            mResMessanger.send(msgprompt);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case MSG_TYPE_REGISTER:
                    mResMessanger = msg.replyTo;

                    /*if (!isSensorsAvl) {
                        if (mResMessanger != null) {
                            TaskMessage msgsensor = TaskMessage.obtain(null, StatsActivity.MSG_SENSOR_NOT_AVL);
                            try {
                                mResMessanger.send(msgsensor);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }*/

                    Helper.printLogMsg(TAG, "onStartCommand");
                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(CaptureDataService.this);
                    mLocationRequest = new LocationRequest();
                    mLocationRequest.setInterval(UPDATE_INTERVAL);
                    mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        initialize();
                    } else {
                        if (mResMessanger != null) {
                            Message msgprompt = Message.obtain(null, StatsFragment.MSG_PROMPT_LOC_ENABLE);
                            try {
                                mResMessanger.send(msgprompt);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                        mWriteInFile.storeData(Constants.FileType.NONVID_SENSOR_DATA, "");
                    }
                    mUploadService = new FileUploadService(CaptureDataService.this);
                    mUploadService.startUpload();
                    if (mResMessanger != null) {
                        Message msgtoact = Message.obtain(null, StatsFragment.MSG_FILE_UPLOAD_START);
                        try {
                            mResMessanger.send(msgtoact);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case MSG_PERM_REQ_FILE_GRANTED:
                    if (mWriteInFile != null) {
                        mWriteInFile.storeData(Constants.FileType.NONVID_SENSOR_DATA, "");
                    }
                    break;
                case MSG_TRIGGER_FILE_UPLOAD:
                    if (mUploadService != null) {
                        mUploadService.cancelNow();
                        boolean closeApp = msg.getData().getBoolean("close_app");
                        mUploadService.triggerFileUpload(Constants.fileUploadUri, CaptureDataService.this, true, closeApp);
                    }
                    break;
                case MSG_SHOW_LAYER:
                    initHeadLayer();
                    break;
                case MSG_CLOSE_LAYER:
                    closeHeadLayer();
                    break;
                case MSG_VIDEO_STARTED:
                    if (mWriteInFile != null) {
                        mWriteInFile.enddata();
                        Bundle bundle = msg.getData();
                        String filename = bundle.getString("filename");
                        mWriteInFile.storeData(Constants.FileType.VID_SENSOR_DATA, filename);
                        if (mUploadService != null) {
                            mUploadService.cancelNow();
                        }
                    }
                    break;
                case MSG_VIDEO_STOPPED:
                    if (mWriteInFile != null) {
                        mWriteInFile.enddata();
                        mWriteInFile.storeData(Constants.FileType.NONVID_SENSOR_DATA, "");
                        if (mUploadService != null) {
                            int timeLeft = FileUploadService.FILE_UPLOAD_INTERVAL_SECS - msg.getData().getInt("time_elapsed");
                            mUploadService.startUpload(timeLeft);
                        }
                    }
                    break;
            }
        }
    }

    private void initialize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            String permission = "";

            if (ContextCompat.checkSelfPermission(CaptureDataService.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(CaptureDataService.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                permission = Manifest.permission.ACCESS_FINE_LOCATION + "," + Manifest.permission.ACCESS_COARSE_LOCATION;
            } else {
                if (mResMessanger != null) {
                    Message msgprompt = Message.obtain(null, StatsFragment.MSG_PROMPT_LOC_ENABLE);
                    try {
                        mResMessanger.send(msgprompt);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (ContextCompat.checkSelfPermission(CaptureDataService.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (permission.length() > 0) {
                    permission += "," + Manifest.permission.WRITE_EXTERNAL_STORAGE + "," + Manifest.permission.READ_EXTERNAL_STORAGE;
                } else {
                    permission = Manifest.permission.WRITE_EXTERNAL_STORAGE + "," + Manifest.permission.READ_EXTERNAL_STORAGE;
                }
            } else {
                if (mWriteInFile != null) {
                    mWriteInFile.storeData(Constants.FileType.NONVID_SENSOR_DATA, "");
                }
            }

            if (permission.length() > 0) {
                String[] permissions = permission.split(",");
                if (mResMessanger != null) {
                    Message msg = Message.obtain(null, StatsFragment.MSG_REQ_PERMISSIONS);
                    Bundle bundle = new Bundle();
                    bundle.putStringArray("permissions", permissions);
                    msg.setData(bundle);
                    try {
                        mResMessanger.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private class WriteInFile implements Runnable {

        private PrintWriter writer;
        private SimpleDateFormat sdftime;
        private ScheduledExecutorService mservice;
        private String mFileName;
        private File file;

        //        int a = 0;
//        ArrayList<Block> blockchain = new ArrayList<Block>();

//        public Boolean isChainValid() {
//            Block currentBlock;
//            Block previousBlock;
//
//            //loop through blockchain to check hashes:
//            for (int i = 1; i < blockchain.size(); i++) {
//                currentBlock = blockchain.get(i);
//                previousBlock = blockchain.get(i - 1);
//                //compare registered hash and calculated hash:
//                if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
//                    return false;
//                }
//                //compare previous hash and registered previous hash
//                if (!previousBlock.hash.equals(currentBlock.previousHash)) {
//                    return false;
//                }
//            }
//            return true;
//        }

        WriteInFile() {
            sdftime = new SimpleDateFormat("HH-mm-ss");
        }

        @Override
        public void run() {

            if (latitude != 0.0 && longitude != 0.0) {
                //&& gyro_x != 0 && gyro_y != 0 && gyro_z != 0
                Helper.printLogMsg(TAG, "Write in file");

                if (mResMessanger != null) {
                    Message msg = Message.obtain(null, StatsFragment.MSG_UPDATE_UI_FIELDS);
                    Bundle bundle = new Bundle();
                    bundle.putDouble("heading", heading);
                    bundle.putDouble("speed", speed);
                    msg.setData(bundle);
                    try {
                        mResMessanger.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                String timeStamp = sdftime.format(Calendar.getInstance().getTime());

                // UUID
                UUID uuid = UUID.randomUUID();

                //to get android ID
//                String androidId = Settings.Secure.getString(getContentResolver(),
//                        Settings.Secure.ANDROID_ID);
                Helper.printLogMsg(TAG, "latitude: " + latitude + " longitude: " + longitude);
                writer.println(uuid + " , " + longitude + "," + latitude + "," + speed + "," + 0 + "," + timeStamp + "," + linear_acc_x + "," + linear_acc_y + "," + linear_acc_z + "," +
                        heading + "," + gyro_x + "," + gyro_y + "," + gyro_z + ',' + 0);

            }
        }

        public void enddata() {
            Helper.printLogMsg(TAG, "Dataaaa e");
            if (writer != null) {
                writer.close();
                Helper.printLogMsg(TAG, "file size: " + file.length());
            }
            if (mservice != null && !mservice.isShutdown()) {
                mservice.shutdownNow();
            }

            FileDataRepository.getInstance(CaptureDataService.this).updateSensorFile(mFileName, Constants.FileUploadStatus.END_RECORD);
        }

        public void storeData(int fileType, String fileName) {
//            Helper.printLogMsg(TAG, "Dataaaa s");
            String dir = Environment.getExternalStorageDirectory().getPath() + File.separator + "cyient" + File.separator;

            File folder = new File(dir); //folder name
            if (!folder.exists()) {
                folder.mkdir();
            }

            if (fileType == Constants.FileType.NONVID_SENSOR_DATA) {
                fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                dir = dir + "NonVideoData" + File.separator;
            } else {
                dir = dir + "VideoData" + File.separator;
            }

            //String dir = Environment.getExternalStorageDirectory()+File.separator+"myDirectory";
            //create folder
            folder = new File(dir); //folder name
            if (!folder.exists()) {
                folder.mkdir();
            }
            try {
//                String timeStampFile = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                //create file
                file = new File(dir, fileName + ".csv");
                mFileName = file.getName();
                if (file != null && file.exists()) {
                    Helper.printLogMsg(TAG, "File Exists");
                } else if (file != null) {
                    file.createNewFile();
                    Helper.printLogMsg(TAG, "File created: " + file.exists());
                }

                SensorFileData sfd = new SensorFileData();
                sfd.setName(file.getName());
                sfd.setFilePath(file.getAbsolutePath());
                sfd.setUpload_status(Constants.FileUploadStatus.START_RECORD);
                sfd.setFileType(fileType);
                if (mRhaTask != null) {
                    sfd.setJobId(mRhaTask.getJobId());
                    CyientSharePrefrence.setJobDataInSharePef(CaptureDataService.this, mRhaTask.getJobId(), file.getName());
                } else {
                    sfd.setJobId(Constants.DEFAULT_JOB_ID);
                }
                FileDataRepository.getInstance(CaptureDataService.this).insertSensorFileData(sfd);

                writer = new PrintWriter(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //to get android ID
            String androidId = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            //To get list of sensors
            List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

            String regEmailId = CyientSharePrefrence.getStringFromSharePef(CaptureDataService.this, SharePrefrenceConstant.REG_EMAIL_ID);
            writer.println("AndroidId, Sensors, User email: " + regEmailId);

            writer.println(androidId + " , " + deviceSensors);

            writer.println("UUID" + " , " + "Longitude" + "," + "Latitude" + "," + "Speed" + "," + "Distance" + "," + "Time" + "," + "Acc X" + "," + "Acc Y" + "," + "Acc Z" + "," + "Heading"
                    + "," + "gyro_x" + "," + "gyro_y" + "," + "gyro_z" + "," + "IRI");

//            heading_tv.setText("Heading\n" + heading);
//            speed_tv.setText("Speed\n" + speed + " kph");
//
            if (mservice != null && !mservice.isShutdown()) {
                mservice.shutdownNow();
            }

            mservice = Executors.newSingleThreadScheduledExecutor();
            mservice.scheduleAtFixedRate(mWriteInFile, 1, 1, TimeUnit.SECONDS);
        }
    }

    @Override
    public void onPreProgress(int flag, String message) {
        if (flag == CLOSE_WRITE) {
            if (mWriteInFile != null) {
                mWriteInFile.enddata();
            }
        }
        if (flag == RESTART_WRITE_EXT) {
            mWriteInFile.storeData(Constants.FileType.NONVID_SENSOR_DATA, "");
        }
    }

    @Override
    public void onProgress(int flag) {
        if (flag == SHOW_PROGRESS) {
            Helper.printLogMsg(TAG, "on show progress");
            if (mResMessanger != null) {
                Message msg = Message.obtain(null, StatsFragment.MSG_SHOW_FILEPROGRESS);
                try {
                    mResMessanger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        if (flag == FILE_EXISTS) {
            Helper.printLogMsg(TAG, "on file exists");
        }
        if (flag == FILE_NOT_EXISTS) {
            Helper.printLogMsg(TAG, "on file not exists");
        }
        if (flag == SMALL_FILE_SIZE) {
            Helper.printLogMsg(TAG, "on small file size");
        }
    }

    @Override
    public void onPostProgress(int flag, String message) {
        if (flag == FILE_UPLOADED) {
            Helper.printLogMsg(TAG, "on file uploaded");
            if (mResMessanger != null) {
                Message msg = Message.obtain(null, StatsFragment.MSG_FILE_UPLOADED);
                try {
                    mResMessanger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        if (flag == CANCEL_PROGRESS_WITHOUT_EXIT) {
            Helper.printLogMsg(TAG, "on cancel progress");

            if (mResMessanger != null) {
                Message msg = Message.obtain(null, StatsFragment.MSG_CANCEL_FILE_PROGRESS);
                try {
                    msg.getData().putString("message", message);
                    msg.getData().putBoolean("close_app", false);
                    mResMessanger.send(msg);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        } else if (flag == CANCEL_PROGRESS_WITH_EXIT) {
            Helper.printLogMsg(TAG, "on cancel progress");

            if (mResMessanger != null) {
                Message msg = Message.obtain(null, StatsFragment.MSG_CANCEL_FILE_PROGRESS);
                try {
                    msg.getData().putString("message", message);
                    msg.getData().putBoolean("close_app", true);
                    mResMessanger.send(msg);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        }

        if (flag == FILE_UPLOAD_ERROR) {
            Helper.printLogMsg(TAG, "on file uploaded file upload error");
        }
    }

}