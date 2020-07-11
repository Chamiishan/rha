package roadcondition.cynsore.cyient.com.cynsore.algorithm;

import android.content.Context;

import roadcondition.cynsore.cyient.com.cynsore.R;
import roadcondition.cynsore.cyient.com.cynsore.view.censor.StatsFragment;

public enum RoadQuality {

    EXCELLENT(0), GOOD(1), FAIR(2), POOR(3), NONE(4);

    private int id;

    RoadQuality(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static RoadQuality getRoadQuality(int id) {
        for (RoadQuality r : values()) {
            if (id == r.getId()) {
                return r;
            }
        }
        return RoadQuality.POOR;
    }

    public static RoadQuality getRoadQualityById(int id) {
        for (RoadQuality r : values()) {
            if (r.getId() == id) {
                return r;
            }
        }
        return RoadQuality.NONE;
    }

    public int getRoadConditionColor(Context context) {
        int color = context.getResources().getColor(R.color.chart_none_roads_color);
        switch (this) {
            case EXCELLENT:
                color = context.getResources().getColor(R.color.chart_perfect_roads_color);
                break;
            case GOOD:
                color = context.getResources().getColor(R.color.chart_good_roads_color);
                break;
            case FAIR:
                color = context.getResources().getColor(R.color.chart_normal_roads_color);
                break;
            case POOR:
                color = context.getResources().getColor(R.color.chart_bad_roads_color);
                break;
        }
        return color;
    }

    @Override
    public String toString() {
        return id == 0 ? StatsFragment.getInstance().getActivity().getApplicationContext().getString(R.string.summary_perfect_text)
                : id == 1 ? StatsFragment.getInstance().getActivity().getApplicationContext().getString(R.string.summary_good_text)
                : id == 2 ? StatsFragment.getInstance().getActivity().getApplicationContext().getString(R.string.summary_normal_text)
                : id == 3 ? StatsFragment.getInstance().getActivity().getApplicationContext().getString(R.string.summary_bad_text)
                : StatsFragment.getInstance().getActivity().getApplicationContext().getString(R.string.summary_none_text);
    }
}
