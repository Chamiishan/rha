package roadcondition.cynsore.cyient.com.cynsore.graph;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by ij39559 on 8/31/2018.
 */

public class RoadConditionValueFormatter implements IAxisValueFormatter {

    private BarLineChartBase<?> chart;
    private JSONArray roadCondArr;

    public RoadConditionValueFormatter(BarLineChartBase<?> chart, JSONArray roadCondArr) {
        this.chart = chart;
        this.roadCondArr = roadCondArr;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        try {
            return roadCondArr.getJSONObject((int) value).getString("Roadcondition");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

}