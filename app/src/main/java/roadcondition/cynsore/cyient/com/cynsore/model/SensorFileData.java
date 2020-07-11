package roadcondition.cynsore.cyient.com.cynsore.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class SensorFileData {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "filepath")
    private String filePath;

    @ColumnInfo(name = "status")
    private int upload_status;

    @ColumnInfo(name = "filetype")
    private int fileType;

    @ColumnInfo(name = "jobid")
    private String JobId;

    public int getId() {
        return id;
    }

    public String getJobId() {
        return JobId;
    }

    public int getUpload_status() {
        return upload_status;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getName() {
        return name;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int file_type) {
        this.fileType = file_type;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUpload_status(int upload_status) {
        this.upload_status = upload_status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setJobId(String jobId) {
        JobId = jobId;
    }
}