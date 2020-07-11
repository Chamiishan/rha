package roadcondition.cynsore.cyient.com.cynsore.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RHATask implements Parcelable {

    private String Status;
    private String ActualStartTime;
    private String ActualEndTime;
    private float Kmsactualcovered;
    private String SensorFileName;
    private float ActualHours;
    private String JobType;
    private String JobId;
    private int JobDetailsId;
    private String Source;
    private String Destination;
    private String Waypoints;
    private float Kmstobecovered;
    private String Plannedstarttime;
    private String Plannedendtime;
    private String Comments;
    private String SourceAddress;
    private String DestinationAddress;


    protected RHATask(Parcel in) {
        Status = in.readString();
        ActualStartTime = in.readString();
        ActualEndTime = in.readString();
        Kmsactualcovered = in.readFloat();
        SensorFileName = in.readString();
        ActualHours = in.readFloat();
        JobType = in.readString();
        JobId = in.readString();
        JobDetailsId = in.readInt();
        Source = in.readString();
        Destination = in.readString();
        Waypoints = in.readString();
        Kmstobecovered = in.readFloat();
        Plannedstarttime = in.readString();
        Plannedendtime = in.readString();
        Comments = in.readString();
        SourceAddress = in.readString();
        DestinationAddress = in.readString();
    }

    public static final Creator<RHATask> CREATOR = new Creator<RHATask>() {

        @Override
        public RHATask createFromParcel(Parcel in) {
            return new RHATask(in);
        }

        @Override
        public RHATask[] newArray(int size) {
            return new RHATask[size];
        }
    };

    public String getJobId() {
        return JobId;
    }

    public void setJobId(String jobId) {
        JobId = jobId;
    }

    public int getJobDetailsId() {
        return JobDetailsId;
    }

    public void setJobDetailsId(int jobDetailsId) {
        JobDetailsId = jobDetailsId;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String source) {
        Source = source;
    }

    public String getDestination() {
        return Destination;
    }

    public void setDestination(String destination) {
        Destination = destination;
    }

    public String getWaypoints() {
        return Waypoints;
    }

    public void setWaypoints(String waypoints) {
        Waypoints = waypoints;
    }

    public float getKmstobecovered() {
        return Kmstobecovered;
    }

    public void setKmstobecovered(float kmstobecovered) {
        Kmstobecovered = kmstobecovered;
    }

    public String getPlannedstarttime() {
        return Plannedstarttime;
    }

    public void setPlannedstarttime(String plannedstarttime) {
        Plannedstarttime = plannedstarttime;
    }

    public String getPlannedendtime() {
        return Plannedendtime;
    }

    public void setPlannedendtime(String plannedendtime) {
        Plannedendtime = plannedendtime;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getActualStartTime() {
        return ActualStartTime;
    }

    public void setActualStartTime(String actualStartTime) {
        ActualStartTime = actualStartTime;
    }

    public String getActualEndTime() {
        return ActualEndTime;
    }

    public void setActualEndTime(String actualEndTime) {
        ActualEndTime = actualEndTime;
    }

    public float getKmsactualcovered() {
        return Kmsactualcovered;
    }

    public void setKmsactualcovered(float kmsactualcovered) {
        Kmsactualcovered = kmsactualcovered;
    }

    public String getSensorFileName() {
        return SensorFileName;
    }

    public void setSensorFileName(String sensorFileName) {
        SensorFileName = sensorFileName;
    }

    public float getActualHours() {
        return ActualHours;
    }

    public void setActualHours(float actualHours) {
        ActualHours = actualHours;
    }

    public String getJobType() {
        return JobType;
    }

    public void setJobType(String jobType) {
        JobType = jobType;
    }

    public void setDestinationAddress(String destinationAddress) {
        DestinationAddress = destinationAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        SourceAddress = sourceAddress;
    }

    public String getDestinationAddress() {
        return DestinationAddress;
    }

    public String getSourceAddress() {
        return SourceAddress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Status);
        dest.writeString(ActualStartTime);
        dest.writeString(ActualEndTime);
        dest.writeFloat(Kmsactualcovered);
        dest.writeString(SensorFileName);
        dest.writeFloat(ActualHours);
        dest.writeString(JobType);
        dest.writeString(JobId);
        dest.writeInt(JobDetailsId);
        dest.writeString(Source);
        dest.writeString(Destination);
        dest.writeString(Waypoints);
        dest.writeFloat(Kmstobecovered);
        dest.writeString(Plannedstarttime);
        dest.writeString(Plannedendtime);
        dest.writeString(Comments);
        dest.writeString(SourceAddress);
        dest.writeString(DestinationAddress);
    }
}