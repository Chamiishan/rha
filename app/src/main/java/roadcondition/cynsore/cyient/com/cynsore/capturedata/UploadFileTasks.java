package roadcondition.cynsore.cyient.com.cynsore.capturedata;

import android.os.Parcelable;

/**
 * Created by ij39559 on 10/5/2018.
 */

public interface UploadFileTasks {

    int SHOW_PROGRESS = 1;
    int FILE_EXISTS = 2;
    int FILE_NOT_EXISTS = 3;
    int FILE_UPLOADED = 4;
    int SMALL_FILE_SIZE = 5;
    int CANCEL_PROGRESS_WITH_EXIT = 6;
    int CANCEL_PROGRESS_WITHOUT_EXIT = 7;
    int FILE_UPLOAD_ERROR = 8;
    int CLOSE_WRITE = 9;
    int RESTART_WRITE_EXT = 10;
    int VIDEO_STARTED = 11;
    int VIDEO_STOPPED = 12;


    void onPreProgress(int flag, String message);

    void onProgress(int flag);

    void onPostProgress(int flag, String message);

}
