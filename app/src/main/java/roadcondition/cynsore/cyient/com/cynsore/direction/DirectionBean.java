package roadcondition.cynsore.cyient.com.cynsore.direction;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by vv42523 on 18-07-2018.
 */

public class DirectionBean implements Parcelable {

    private LatLng startPoint;

    protected DirectionBean(Parcel in) {
        startPoint = in.readParcelable(LatLng.class.getClassLoader());
        endPoint = in.readParcelable(LatLng.class.getClassLoader());
        directionFlag = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(startPoint, flags);
        dest.writeParcelable(endPoint, flags);
        dest.writeString(directionFlag);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DirectionBean> CREATOR = new Creator<DirectionBean>() {

        @Override
        public DirectionBean createFromParcel(Parcel in) {
            return new DirectionBean(in);
        }

        @Override
        public DirectionBean[] newArray(int size) {
            return new DirectionBean[size];
        }
    };

    public LatLng getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(LatLng startPoint) {
        this.startPoint = startPoint;
    }

    private LatLng endPoint;
    private String directionFlag;

    public LatLng getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(LatLng endPoint) {
        this.endPoint = endPoint;
    }

    public String getDirectionFlag() {
        return directionFlag;
    }

    public void setDirectionFlag(String directionFlag) {
        this.directionFlag = directionFlag;
    }

    public DirectionBean(LatLng start, LatLng end, String direction) {
        this.startPoint = start;
        this.endPoint = end;
        this.directionFlag = direction;
    }


}