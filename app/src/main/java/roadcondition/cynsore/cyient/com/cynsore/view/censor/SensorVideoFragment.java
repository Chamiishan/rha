package roadcondition.cynsore.cyient.com.cynsore.view.censor;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import roadcondition.cynsore.cyient.com.cynsore.R;

public class SensorVideoFragment extends Fragment {

    private static final String TAG = "CamVideo";
    private Camera mCamera;
    private CameraPreview mPreview;
    private MediaRecorder mMediaRecorder;
    private ImageButton mCapture/*, vid*/;
    private Context mContext;
    private FrameLayout mCameraPreview;
    private Chronometer mChrono;
    private TextView mTxt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        View view = inflater.inflate(R.layout.camvideo, container, false);

        return view;
    }


}