package roadcondition.cynsore.cyient.com.cynsore.view.censor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import roadcondition.cynsore.cyient.com.cynsore.R;
import roadcondition.cynsore.cyient.com.cynsore.SQLDataBase.FileDataRepository;
import roadcondition.cynsore.cyient.com.cynsore.capturedata.UploadFileTasks;
import roadcondition.cynsore.cyient.com.cynsore.model.FileData;
import roadcondition.cynsore.cyient.com.cynsore.model.RHATask;
import roadcondition.cynsore.cyient.com.cynsore.utility.Constants;
import roadcondition.cynsore.cyient.com.cynsore.utility.CyientSharePrefrence;

public class CamVideo extends AppCompatActivity implements OnClickListener/*SensorEventListener */ {

    private static final String TAG = "CamVideo";
    private Camera mCamera;
    private CameraPreview mPreview;
    private MediaRecorder mediaRecorder;
    private ImageButton capture,/*, vid*/
            mExitFullScr;
    private Context myContext;
    private FrameLayout cameraPreview;
    private Chronometer chrono;
    private TextView txt;

    //    int quality = 0;
//    int rate = 100;
//    private String timeStampFile;

    //    private int VideoFrameRate = 30;
    private FileData mCurrFile = null;
    private FileDataRepository fileDataRepository;
    private AudioManager mAudioManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.camvideo);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        myContext = this;
        fileDataRepository = FileDataRepository.getInstance(this);

        cameraPreview = (FrameLayout) findViewById(R.id.camera_preview);

        mPreview = new CameraPreview(myContext, mCamera);
        cameraPreview.addView(mPreview);

        capture = (ImageButton) findViewById(R.id.button_capture);
        capture.setOnClickListener(captureListener);

        mExitFullScr = findViewById(R.id.ic_img_minimize);
        mExitFullScr.setOnClickListener(this);
//        mExitFullScr.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && getPackageManager().hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
            mExitFullScr.setVisibility(View.VISIBLE);
        } else {
            mExitFullScr.setVisibility(View.GONE);
        }

        chrono = (Chronometer) findViewById(R.id.chronometer);
        chrono.setTextColor(Color.GREEN);
        txt = (TextView) findViewById(R.id.ic_txt_captureblink);
        txt.setTextColor(-16711936);

        mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

//        vid = (ImageButton) findViewById(R.id.ic_img_framerate);
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        // Search for the back facing camera
        // get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        // for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    @SuppressLint("MissingPermission")
    public void onResume() {
        super.onResume();
        if (!checkCameraHardware(myContext)) {
            Toast toast = Toast.makeText(myContext, "Phone doesn't have a camera!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        if (mCamera == null) {
            mCamera = Camera.open(findBackFacingCamera());
            mPreview.refreshCamera(mCamera);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // when on Pause, release camera in order to be used from other
        // applications
        stopRecording();
        releaseCamera();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopRecording();
        releaseCamera();
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        if (recording) {
            stopRecording();
        } else {
            super.onBackPressed();
        }
//        releaseCamera();
    }

    boolean recording = false;

    OnClickListener captureListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (recording) {

                stopRecording();

            } else {
//                timeStampFile = String.valueOf((new Date()).getTime());
//                File wallpaperDirectory = new File(Environment.getExternalStorageDirectory().getPath() + "/elab/");
//                wallpaperDirectory.mkdirs();

                Camera.Parameters params = null;
                try {
                    params = mCamera.getParameters();

//                    List<int[]> fpsRange = params.getSupportedPreviewFpsRange();
//                    if (fpsRange.size()>0)
//                    params.setPreviewFpsRange(fpsRange.get(0)[0], fpsRange.get(0)[1]); // 30 fps

                    if (params.isAutoExposureLockSupported()) {
                        params.setAutoExposureLock(true);
                    }

//                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    mCamera.setParameters(params);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mCamera.unlock();

                if (!prepareMediaRecorder()) {
                    Toast.makeText(CamVideo.this, "Fail in prepareMediaRecorder()!\n - Ended -", Toast.LENGTH_LONG).show();
                    finish();
                }

                // work on UiThread for better performance
                runOnUiThread(new Runnable() {

                    public void run() {
                        try {
                            mediaRecorder.start();
//                            vid.setVisibility(View.GONE);
                            txt.setTextColor(Color.RED);

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                Toast.makeText(CamVideo.this, "Recording...", Toast.LENGTH_LONG).show();

                //d.beginData();
//                storeData();
                chrono.setBase(SystemClock.elapsedRealtime());

                chrono.start();
                //chrono.setBackgroundColor(-65536);
                recording = true;

            }
        }
    };

    private void stopRecording() {
        if (recording) {

            if (mAudioManager.isMicrophoneMute()) {
                mAudioManager.setMicrophoneMute(false);
            }

            // stop recording and release camera
            try {
                mediaRecorder.stop(); // stop the recording
//                    vid.setVisibility(View.VISIBLE);
                if (mCurrFile != null) {
                    mCurrFile.setUpload_status(Constants.FileUploadStatus.END_RECORD);
                    fileDataRepository.updateFile(mCurrFile.getName(), Constants.FileUploadStatus.END_RECORD);
                }

                Intent intent = new Intent(StatsFragment.VID_INTENT_FILTER);
                intent.putExtra(StatsFragment.VID_STATUS_FLAG, UploadFileTasks.VIDEO_STOPPED);
                LocalBroadcastManager.getInstance(CamVideo.this).sendBroadcast(intent);

//                    StatsActivity.mTasks.onPostProgress(UploadFileTasks.VIDEO_STOPPED, "");
                showAlert();
            } catch (RuntimeException re) {
                re.printStackTrace();
            }
            releaseMediaRecorder(); // release the MediaRecorder object
            Toast.makeText(CamVideo.this, "Video captured!", Toast.LENGTH_LONG).show();
            recording = false;
            //d.exportData();
            chrono.stop();

//                chrono.setBase(SystemClock.elapsedRealtime());
//
            txt.setTextColor(-16711936);
        }
    }

    AlertDialog alertDialog = null;

    private void showAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog)
                .setIcon(R.drawable.roadrunner)
                .setMessage("Recording is completed.")
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("Start New?", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (alertDialog != null) {
                            alertDialog.dismiss();
                            chrono.setBase(SystemClock.elapsedRealtime());
                            chrono.start();
                            chrono.stop();
                        }
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset(); // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            mCamera.lock(); // lock camera for later use
        }
    }

    private boolean prepareMediaRecorder() {

        if (!mAudioManager.isMicrophoneMute()) {
            mAudioManager.setMicrophoneMute(true);
        }

        mediaRecorder = new MediaRecorder();

        mediaRecorder.setCamera(mCamera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));

        String dir = Environment.getExternalStorageDirectory().getPath() + File.separator + "cyient" + File.separator;

        File folder = new File(dir); //folder name
        if (!folder.exists()) {
            folder.mkdir();
        }
        dir = dir + "Video" + File.separator;

        folder = new File(dir); //folder name
        if (!folder.exists()) {
            folder.mkdir();
        }

        String timeStampFile = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = timeStampFile + ".mp4";

        try {
            File file = new File(dir, fileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            mediaRecorder.setOutputFile(file.getPath());
            mediaRecorder.setOrientationHint(90);

            mediaRecorder.prepare();

            String jobId = Constants.DEFAULT_JOB_ID;
            Object obj = getIntent().getParcelableExtra("rha_task");
            if (obj != null && obj instanceof RHATask) {
                RHATask rhaTask = (RHATask) obj;
                jobId = rhaTask.getJobId();
                CyientSharePrefrence.setJobDataInSharePef(this, jobId, fileName);
            }
            mCurrFile = fileDataRepository.insertVidFileData(fileName, file.getAbsolutePath(), Constants.FileUploadStatus.START_RECORD, jobId);

            Intent intent = new Intent(StatsFragment.VID_INTENT_FILTER);
            intent.putExtra(StatsFragment.VID_STATUS_FLAG, UploadFileTasks.VIDEO_STARTED);
            intent.putExtra(StatsFragment.VID_FILENAME, timeStampFile);
            LocalBroadcastManager.getInstance(CamVideo.this).sendBroadcast(intent);

//            StatsActivity.mTasks.onPreProgress(UploadFileTasks.VIDEO_STARTED, timeStampFile);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ic_img_minimize:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    enterPictureInPictureMode();
                }
                break;
        }
    }
}