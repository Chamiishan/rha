package roadcondition.cynsore.cyient.com.cynsore.capturedata;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import roadcondition.cynsore.cyient.com.cynsore.R;
import roadcondition.cynsore.cyient.com.cynsore.SQLDataBase.FileDataRepository;
import roadcondition.cynsore.cyient.com.cynsore.utility.Constants;
import roadcondition.cynsore.cyient.com.cynsore.model.SensorFileData;
import roadcondition.cynsore.cyient.com.cynsore.utility.Helper;

/**
 * Created by ij39559 on 10/1/2018.
 */
public class FileUploadService {

    private static final String TAG = "FileUploadService";
    //    private AlarmManager am;
//    private PendingIntent pi;
    private CaptureDataService mContext;
    private ScheduledExecutorService service;
    public static final int FILE_UPLOAD_INTERVAL_SECS = 10 * 60;

    public FileUploadService() {
    }

    public FileUploadService(CaptureDataService context) {
        mContext = context;
    }

    public void startUpload() {

        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(new UploadFileThread(Constants.fileUploadUri, mContext, false, false),
                2, FILE_UPLOAD_INTERVAL_SECS, TimeUnit.SECONDS);
    }

    public void startUpload(int upInterval) {
        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(new UploadFileThread(Constants.fileUploadUri, mContext, false, false),
                upInterval, FILE_UPLOAD_INTERVAL_SECS, TimeUnit.SECONDS);
    }

    public void cancel() {
        if (!service.isShutdown()) {
            service.shutdown();
        }
    }

    public void cancelNow() {
        if (!service.isShutdown()) {
            service.shutdownNow();
        }
    }

    public void triggerFileUpload(String url, CaptureDataService context, boolean showProgress, boolean exit) {
        if (service.isShutdown()) {
            UploadFileThread fileThread = new UploadFileThread(url, context, showProgress, exit);
            Thread thread = new Thread(fileThread);
            thread.start();
        }
    }

    public class UploadFileThread implements Runnable {

        private CaptureDataService mCaptureService;
        private boolean mShowProgress;
        private String mUrl;
        private boolean mExit;

        public UploadFileThread(String url, CaptureDataService context, boolean showProgress, boolean exit) {
            mShowProgress = showProgress;
            mCaptureService = context;
            mUrl = url;
            mExit = exit;
        }

        public void uploadFiles(Object list) {

            if (list instanceof List) {
                List<SensorFileData> fileDataList = (List<SensorFileData>) list;
                Helper.printLogMsg(TAG, "list size: " + fileDataList.size());

                if (fileDataList != null && fileDataList.size() > 0) {
                    mCaptureService.onPreProgress(UploadFileTasks.CLOSE_WRITE, "");
                    if (mShowProgress) {
                        mCaptureService.onProgress(UploadFileTasks.SHOW_PROGRESS);
                    } else {
                        mCaptureService.onPreProgress(UploadFileTasks.RESTART_WRITE_EXT, "");
                    }


                    String resMsg = "";
                    for (SensorFileData fd : fileDataList) {

                        try {
                            Helper.printLogMsg(TAG, "start uploading");

                            File file = null;
                            String uploadFileName = fd.getName();
                            if (uploadFileName != null && uploadFileName.length() > 0) {

//                                String dir = Environment.getExternalStorageDirectory().getPath() + File.separator + "cyient/";
                                file = new File(fd.getFilePath());

                                if (file.exists()) {
                                    mCaptureService.onPreProgress(UploadFileTasks.FILE_EXISTS, "");

                                    double fileSizeInKB = file.length();
                                    fileSizeInKB = fileSizeInKB / 1024;
                                    if (fileSizeInKB < 1) {
                                        resMsg = mCaptureService.getString(R.string.small_file_size);
                                        mCaptureService.onProgress(UploadFileTasks.SMALL_FILE_SIZE);
                                        FileDataRepository.getInstance(mContext).deleteSensorFD(fd);
                                        continue;
                                    }

                                } else {
                                    resMsg = mCaptureService.getString(R.string.file_not_exists);
                                    mCaptureService.onProgress(UploadFileTasks.FILE_NOT_EXISTS);
                                    FileDataRepository.getInstance(mContext).deleteSensorFD(fd);
                                    continue;
                                }

                            } else {
                                resMsg = mCaptureService.getString(R.string.file_not_exists);
                                mCaptureService.onProgress(UploadFileTasks.FILE_NOT_EXISTS);
                                continue;
                            }

                            if (file == null) {
                                continue;
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
                            URL url = new URL(mUrl);

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

                            while (bytesRead > 0) {

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

                                resMsg = mCaptureService.getString(R.string.file_uploaded);
                                mCaptureService.onPostProgress(UploadFileTasks.FILE_UPLOADED, resMsg);
                                FileDataRepository.getInstance(mContext).deleteSensorFD(fd);
                                file.delete();
                            } else {
                                resMsg = mCaptureService.getString(R.string.file_upload_err);
                                mCaptureService.onPostProgress(UploadFileTasks.FILE_UPLOAD_ERROR, resMsg);
                            }
                        } catch (MalformedURLException ex) {
                            ex.printStackTrace();
                            Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
                            resMsg = mCaptureService.getString(R.string.file_upload_err);
                            mCaptureService.onPostProgress(UploadFileTasks.FILE_UPLOAD_ERROR, resMsg);
//                            mCaptureService.onPostProgress(UploadFileTasks.RESTART_WRITE_EXT, resMsg);
                        } catch (Exception e) {
                            e.printStackTrace();

                            Log.e("Upload Exception", "Exception : "
                                    + e.getMessage(), e);
                            resMsg = mCaptureService.getString(R.string.file_upload_err);
                            mCaptureService.onPostProgress(UploadFileTasks.FILE_UPLOAD_ERROR, resMsg);
//                            mCaptureService.onPostProgress(UploadFileTasks.RESTART_WRITE_EXT, resMsg);
                        }
                    }
                    if (mShowProgress) {
                        if (mExit) {
                            mCaptureService.onPostProgress(UploadFileTasks.CANCEL_PROGRESS_WITH_EXIT, resMsg);
                        } else {
                            mCaptureService.onPostProgress(UploadFileTasks.CANCEL_PROGRESS_WITHOUT_EXIT, resMsg);
                        }
                    }
                }
            }
        }

        @Override
        public void run() {
            List<SensorFileData> fds = FileDataRepository.getInstance(mCaptureService).fetchSensorFiles(Constants.FileType.NONVID_SENSOR_DATA);
            uploadFiles(fds);
        }

    }

}