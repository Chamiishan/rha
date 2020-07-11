package roadcondition.cynsore.cyient.com.cynsore.SQLDataBase;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.util.List;

import roadcondition.cynsore.cyient.com.cynsore.dao.DaoAccess;
import roadcondition.cynsore.cyient.com.cynsore.model.FileData;
import roadcondition.cynsore.cyient.com.cynsore.model.SensorFileData;
import roadcondition.cynsore.cyient.com.cynsore.utility.Helper;

public class FileDataRepository {

    private String DB_NAME = "db_rr";
    private RRDatabase mRRDatabase;
    private static Context mContext;
    private static final String TAG = "FileDataRepository";

    private static FileDataRepository mRepository;

    public static FileDataRepository getInstance(Context context) {
        mContext = context;
        if (mRepository == null) {
            mRepository = new FileDataRepository();
        }
        return mRepository;
    }

    private FileDataRepository() {
        mRRDatabase = Room.databaseBuilder(mContext, RRDatabase.class, DB_NAME)
                .fallbackToDestructiveMigration().build();
    }

    public FileData insertVidFileData(String name, String filePath, int upload_status, String jobId) {
        FileData fileData = new FileData();
        fileData.setName(name);
        fileData.setFilePath(filePath);
        fileData.setUpload_status(upload_status);
        fileData.setJobId(jobId);

        insertVidFileData(fileData);

        return fileData;
    }

    public void insertVidFileData(final FileData fileData) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                DaoAccess daoAccess = mRRDatabase.daoAccess();
                long row = daoAccess.insertVidFileData(fileData);
                return null;
            }

        }.execute();
    }

    public void insertSensorFileData(final SensorFileData fileData) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                DaoAccess daoAccess = mRRDatabase.daoAccess();
                long row = daoAccess.insertSensorFileData(fileData);
                return null;
            }

        }.execute();
    }

    public List<SensorFileData> fetchAllSensorFiles() {
        List<SensorFileData> listData = mRRDatabase.daoAccess().fetchAllSensorData();
        return listData;
    }

    public List<SensorFileData> fetchSensorFiles(int fileType) {
        List<SensorFileData> listData = mRRDatabase.daoAccess().fetchSensorData(fileType);
        return listData;
    }

    public void fetchAllFiles(final Observer observer) {
        new AsyncTask<Void, Void, LiveData<List<FileData>>>() {

            @Override
            protected LiveData<List<FileData>> doInBackground(Void... voids) {
                LiveData<List<FileData>> listData = mRRDatabase.daoAccess().fetchAllFiles();
                return listData;
            }

            @Override
            protected void onPostExecute(LiveData<List<FileData>> listLiveData) {
                super.onPostExecute(listLiveData);
                if (!listLiveData.hasActiveObservers()) {
                    listLiveData.observe((LifecycleOwner) mContext, observer);
                }
            }
        }.execute();
    }

    public LiveData<FileData> getFile(final Context context, final String filePath) {
        new AsyncTask<Void, Void, LiveData<FileData>>() {

            @Override
            protected LiveData<FileData> doInBackground(Void... voids) {
                LiveData<FileData> fileData = mRRDatabase.daoAccess().getFile(filePath);
                fileData.observe((LifecycleOwner) context, new Observer<FileData>() {

                    @Override
                    public void onChanged(@Nullable FileData data) {
                        Helper.printLogMsg(TAG, "");
                    }
                });
                return fileData;
            }

//            @Override
//            protected void onPostExecute(LiveData<List<FileData>> listLiveData) {
//                super.onPostExecute(listLiveData);
//                return;
//            }
        }.execute();
        return null;
    }

    public void updateFile(final String filename, final int uploadStatus) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                mRRDatabase.daoAccess().updateFile(uploadStatus, filename);
                return null;
            }

        }.execute();
    }

    public void updateSensorFile(final String filename, final int uploadStatus) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                mRRDatabase.daoAccess().updateSensorFile(uploadStatus, filename);
                return null;
            }

        }.execute();
    }

    public void updateTask(final FileData fileData) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                mRRDatabase.daoAccess().updateTask(fileData);
                return null;
            }

        }.execute();
    }

    public void deleteTask(final FileData fileData) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                mRRDatabase.daoAccess().deleteTask(fileData);
                return null;
            }

        }.execute();
    }

    public void deleteSensorFD(final SensorFileData fileData) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                mRRDatabase.daoAccess().deleteSensorFD(fileData);
                return null;
            }

        }.execute();
    }

}