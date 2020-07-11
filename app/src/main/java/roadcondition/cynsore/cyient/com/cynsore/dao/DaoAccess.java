package roadcondition.cynsore.cyient.com.cynsore.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import roadcondition.cynsore.cyient.com.cynsore.model.FileData;
import roadcondition.cynsore.cyient.com.cynsore.model.SensorFileData;

@Dao
public interface DaoAccess {

    @Insert
    Long insertVidFileData(FileData fileData);

    @Insert
    Long insertSensorFileData(SensorFileData sensorFileData);

    @Query("SELECT * FROM SensorFileData")
    List<SensorFileData> fetchAllSensorData();

    @Query("SELECT * FROM SensorFileData where filetype = :fileType")
    List<SensorFileData> fetchSensorData(int fileType);

    @Query("SELECT * FROM FileData")
    LiveData<List<FileData>> fetchAllFiles();

    @Query("SELECT * FROM FileData WHERE id =:filePath")
    LiveData<FileData> getFile(String filePath);

    @Update
    void updateTask(FileData data);

    @Query("UPDATE FileData SET status =:uploadStatus where name=:filename")
    void updateFile(int uploadStatus, String filename);

    @Query("UPDATE SensorFileData SET status =:uploadStatus where name=:filename")
    void updateSensorFile(int uploadStatus, String filename);

    @Delete
    void deleteTask(FileData data);

    @Delete
    void deleteSensorFD(SensorFileData fd);

}
