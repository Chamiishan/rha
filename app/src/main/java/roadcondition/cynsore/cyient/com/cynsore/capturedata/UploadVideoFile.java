package roadcondition.cynsore.cyient.com.cynsore.capturedata;

import android.content.Context;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import roadcondition.cynsore.cyient.com.cynsore.R;
import roadcondition.cynsore.cyient.com.cynsore.SQLDataBase.FileDataRepository;
import roadcondition.cynsore.cyient.com.cynsore.view.censor.StatsFragment;
import roadcondition.cynsore.cyient.com.cynsore.utility.Constants;
import roadcondition.cynsore.cyient.com.cynsore.model.FileData;
import roadcondition.cynsore.cyient.com.cynsore.model.SensorFileData;
import roadcondition.cynsore.cyient.com.cynsore.utility.Helper;

/**
 * Created by ij39559 on 10/15/2018.
 */

public class UploadVideoFile {

    private static UploadVideoFile VIDEO_FILE;
    private static List<FileData> mFileData;
    private ScheduledExecutorService service;
    private static final String TAG = "UploadVideoFile";
    private static Context mContext;
    private boolean mrecording;
    private Messenger mMessenger;
    private NotificationCompat.Builder mBuilder;

    private UploadVideoFile() {
    }

    public static final UploadVideoFile getInstance(Context context) {
        mContext = context;
        if (VIDEO_FILE == null) {
            VIDEO_FILE = new UploadVideoFile();
        }
        return VIDEO_FILE;
    }

    public void startStopUpload(List<FileData> fileData, Messenger messenger, NotificationCompat.Builder builder) {
        mMessenger = messenger;
        mBuilder = builder;
        if (!mrecording) {
            if (service == null) {
                service = Executors.newSingleThreadScheduledExecutor();
            }
            mFileData = fileData;
            service.schedule(new UploadVidFileRun(), 1, TimeUnit.SECONDS);
            mrecording = true;
        } else {
            stopUpload();
        }
    }

    public void stopUpload() {
        if (service != null) {
            service.shutdownNow();
            service = null;
            mrecording = false;
        }
    }

    public boolean isRecording() {
        return mrecording;
    }

    public class UploadVidFileRun implements Runnable {

        public UploadVidFileRun() {
        }

        @Override
        public void run() {
            String resMsg = "";
            mrecording = true;

            List<SensorFileData> sensorDataList = FileDataRepository.getInstance(mContext).fetchSensorFiles(Constants.FileType.VID_SENSOR_DATA);

            Helper.printLogMsg(TAG, "size: " + sensorDataList.size());

            for (int i = 0; i < mFileData.size(); i++) {
                FileData fileData = mFileData.get(i);

                Message msg = Message.obtain(null, StatsFragment.MSG_VID_FILE_UPLOAD_START);
                try {
                    mMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                try {
                    if (fileData.getUpload_status() == Constants.FileUploadStatus.END_RECORD) {
                        Helper.printLogMsg(TAG, "start uploading" + mContext);

                        notifyProgress(0, 0, true, "RHA", "Uploading progress(" + (mFileData.size() - 1) + ")");

                        int serverResponseCode = 0;
                        HttpURLConnection conn = null;
                        DataOutputStream dos = null;
                        String lineEnd = "\r\n";
                        String twoHyphens = "--";
                        String boundary = "*****";
                        int bytesRead, bytesAvailable, bufferSize;
                        byte[] buffer;
                        int maxBufferSize = 1 * 1024 * 1024;

                        // open a URL connection to the Servlet
                        FileInputStream fileInputStream = new FileInputStream(fileData.getFilePath());
                        URL url = new URL(Constants.fileUploadUri);

                        // Open a HTTP  connection to  the URL
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true); // Allow Inputs
                        conn.setDoOutput(true); // Allow Outputs
                        conn.setChunkedStreamingMode(1024);
                        conn.setUseCaches(false); // Don't use a Cached Copy
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                        conn.setRequestProperty("file", fileData.getName());
                        dos = new DataOutputStream(conn.getOutputStream());

                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
                                + fileData.getName() + "\"" + lineEnd);

                        dos.writeBytes(lineEnd);

                        // create a buffer of  maximum size
                        bytesAvailable = fileInputStream.available();

                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];

                        // read file and write it into form...
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        int totalSize = bytesAvailable;
                        int j = 0;
                        while (bytesRead > 0) {

                            dos.write(buffer, 0, bufferSize);

                            bytesAvailable = fileInputStream.available();

                            j++;
                            if (j == 3) {

                                BigDecimal bdTotalSize = new BigDecimal(totalSize);
                                BigDecimal bdBytesUplaoded = bdTotalSize.subtract(new BigDecimal(bytesAvailable));
                                BigDecimal bdPercVal = (bdBytesUplaoded.divide(bdTotalSize, 2, RoundingMode.HALF_EVEN)).multiply(new BigDecimal(100));

                                int perc = bdPercVal.intValue();
                                int count = mFileData.size() - (i);

                                notifyProgress(100, perc, false, "RHA", "Uploading progress(" + count + ")");

                                msg = Message.obtain(null, StatsFragment.MSG_VID_FILE_UPLOAD_PROGRESS);
                                msg.arg1 = perc;

                                try {
                                    mMessenger.send(msg);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                                j = 0;
                            }

                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                        }

                        // send multipart form data necesssary after file data...
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                        // Responses from the server (code and message)
                        serverResponseCode = conn.getResponseCode();
                        String serverResponseMessage = conn.getResponseMessage();

                        if (serverResponseCode == 204) {
                            Helper.printLogMsg("uploadFile", "HTTP Response is : "
                                    + serverResponseMessage + ": " + serverResponseCode);

                            //close the streams //
                            fileInputStream.close();
                            dos.flush();
                            dos.close();

                            resMsg = "File Uploaded";
                            msg = Message.obtain(null, StatsFragment.MSG_VID_FILE_UPLOAD_END);
                            try {
                                mMessenger.send(msg);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                            FileDataRepository.getInstance(mContext).deleteTask(fileData);
                            File file = new File(fileData.getFilePath());
                            file.delete();

                            for (SensorFileData fd : sensorDataList) {
                                String vidFnameWE = fileData.getName().substring(0, fileData.getName().lastIndexOf('.'));
                                String dataFnameWE = fd.getName().substring(0, fileData.getName().lastIndexOf('.'));

                                if (vidFnameWE.equals(dataFnameWE)) {
                                    uploadDataFile(fd);
                                }
                            }
                        } else {
                            resMsg = "File uploaded error";
                        }
                    }
                } catch (FileNotFoundException fe) {
                    fe.printStackTrace();
                    FileDataRepository.getInstance(mContext).deleteTask(fileData);
                    resMsg = "File not found";
                    Helper.printErrorMsg("Upload file to server", "error: " + resMsg + "\n" + fe.getMessage(), fe);
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                    resMsg = "File uploaded error";
                    Helper.printErrorMsg("Upload file to server", "error: " + resMsg + "\n" + ex.getMessage(), ex);
                } catch (Exception e) {
                    e.printStackTrace();
                    resMsg = "File uploaded error";
                    Helper.printErrorMsg("Upload Exception", "Exception : "
                            + resMsg + "\n" + e.getMessage(), e);
                }
            }
            if (mContext != null) {
                NotificationManagerCompat.from(mContext).cancel(Constants.NOTIFICATION_IDS.NOTIFICATION_VID_UPLOAD);
            }
            mrecording = false;
        }

        public void uploadDataFile(SensorFileData fd) {

            String resMsg = "";

            try {
                Helper.printLogMsg(TAG, "start uploading");

                File file = null;
                String uploadFileName = fd.getName();
                if (uploadFileName != null && uploadFileName.length() > 0) {

//                                String dir = Environment.getExternalStorageDirectory().getPath() + File.separator + "cyient/";
                    file = new File(fd.getFilePath());

                    if (file.exists()) {

                        double fileSizeInKB = file.length();
                        fileSizeInKB = fileSizeInKB / 1024;
                        if (fileSizeInKB < 1) {
                            resMsg = mContext.getString(R.string.small_file_size);
                            FileDataRepository.getInstance(mContext).deleteSensorFD(fd);
                            return;
                        }

                    } else {
                        resMsg = mContext.getString(R.string.file_not_exists);
                        FileDataRepository.getInstance(mContext).deleteSensorFD(fd);
                        return;
                    }

                } else {
                    resMsg = mContext.getString(R.string.file_not_exists);
                    return;
                }

                if (file == null) {
                    return;
                }

                int serverResponseCode = 0;
                HttpURLConnection conn = null;
                DataOutputStream dos = null;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1 * 1024 * 1024;

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(file);
                URL url = new URL(Constants.fileUploadUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("file", uploadFileName);
                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
                        + file.getName() + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                Message msg = Message.obtain(null, StatsFragment.MSG_VID_FILE_UPLOAD_START);
                try {
                    mMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                int totalSize = bytesAvailable;
                while (bytesRead > 0) {

                    BigDecimal bdTotalSize = new BigDecimal(totalSize);
                    BigDecimal bdBytesUplaoded = bdTotalSize.subtract(new BigDecimal(bufferSize));
                    BigDecimal bdPercVal = (bdBytesUplaoded.divide(bdTotalSize, 2, RoundingMode.HALF_EVEN)).multiply(new BigDecimal(100));

                    int perc = bdPercVal.intValue();
                    notifyProgress(100, perc, false, "RHA", "Upload data file progress");

                    msg = Message.obtain(null, StatsFragment.MSG_VID_FILE_UPLOAD_PROGRESS);
                    msg.arg1 = perc;

                    try {
                        mMessenger.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                if (serverResponseCode == 204) {
                    String serverResponseMessage = conn.getResponseMessage();

                    Log.i("uploadFile", "HTTP Response is : "
                            + serverResponseMessage + ": " + serverResponseCode);

                    //close the streams //
                    fileInputStream.close();
                    dos.flush();
                    dos.close();

                    resMsg = mContext.getString(R.string.file_uploaded);
                    msg = Message.obtain(null, StatsFragment.MSG_VID_FILE_UPLOAD_END);
                    try {
                        mMessenger.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    FileDataRepository.getInstance(mContext).deleteSensorFD(fd);
                    file.delete();
                } else {
                    resMsg = mContext.getString(R.string.file_upload_err);
                }

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                resMsg = mContext.getString(R.string.file_upload_err);
                Helper.printErrorMsg("Upload file to server", resMsg, ex);
            } catch (Exception e) {
                e.printStackTrace();

                resMsg = mContext.getString(R.string.file_upload_err);
                Helper.printErrorMsg("Upload Exception", resMsg, e);
            }
        }

        private void notifyProgress(int maxProgress, int progress, boolean inderminate, String title, String msg) {
            if (mContext != null) {
                mBuilder.setProgress(maxProgress, progress, inderminate);

                NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
                bigTextStyle.setBigContentTitle(title);
                bigTextStyle.bigText(msg);

                mBuilder.setStyle(bigTextStyle);
                NotificationManagerCompat.from(mContext).notify(Constants.NOTIFICATION_IDS.NOTIFICATION_VID_UPLOAD, mBuilder.build());
            }
        }

    }

}