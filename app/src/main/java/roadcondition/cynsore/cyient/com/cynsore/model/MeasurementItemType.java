package roadcondition.cynsore.cyient.com.cynsore.model;

import roadcondition.cynsore.cyient.com.cynsore.R;
import roadcondition.cynsore.cyient.com.cynsore.utility.Constants;

/**
 * Created by vv42523 on 09-08-2018.
 */

public enum MeasurementItemType {

    INTERVAl(R.drawable.interval_icon, -1, Constants.ROAD_INTERVALS),
    BUMP(R.drawable.bump_icon, R.drawable.ic_map_bump_red, Constants.BUMPS);

    private int appIcon;
    private int mapIcon;
    private String name;

    private MeasurementItemType(int appIcon, int mapIcon, String name) {
        this.appIcon = appIcon;
        this.name = name;
        this.mapIcon = mapIcon;
    }

    public int getAppIcon() {
        return appIcon;
    }

    public int getMapIcon() {
        return mapIcon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}