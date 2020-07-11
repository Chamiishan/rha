package roadcondition.cynsore.cyient.com.cynsore.model;

/**
 * Created by vv42523 on 09-08-2018.
 */

public interface MeasurementItem {
    long getId();
    long getTime();
    double getLatitude();
    double getLongitude();
    String getName();
    String getDescription();
    float getIri();
    MeasurementItemType getType();
}

