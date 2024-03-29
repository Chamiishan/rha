package roadcondition.cynsore.cyient.com.cynsore.graph;

import android.annotation.SuppressLint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import roadcondition.cynsore.cyient.com.cynsore.R;
import roadcondition.cynsore.cyient.com.cynsore.network.ServerAsyncTaskPost;
import roadcondition.cynsore.cyient.com.cynsore.network.ServerHelper;

public class Graph_1 extends DemoBase implements
        OnChartValueSelectedListener {

    protected BarChart mChart;
    //    private SeekBar mSeekBarX, mSeekBarY;
//    private TextView tvX, tvY;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawer;
    private ImageButton mImgMenu;
    private View mView;

    private static final String TAG = "Graph_1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_barchart);
        mView = findViewById(R.id.activity_bar_view);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(onNavItemSelListener);
        mDrawer = (DrawerLayout) findViewById(R.id.bar_drawer);
        mImgMenu = (ImageButton) findViewById(R.id.menu);
        mImgMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mDrawer.isDrawerOpen(Gravity.START)) {
                    mDrawer.closeDrawer(Gravity.START);
                } else {
                    mDrawer.openDrawer(Gravity.START);
                }
            }
        });

//        tvX = (TextView) findViewById(R.id.tvXMax);
//        tvY = (TextView) findViewById(R.id.tvYMax);

//        mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
//        mSeekBarY = (SeekBar) findViewById(R.id.seekBar2);

        mChart = (BarChart) findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);

        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);

        mChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);
        // mChart.setDrawYLabels(false);

//        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(mChart);
//
//        XAxis xAxis = mChart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setTypeface(mTfLight);
//        xAxis.setDrawGridLines(false);
//        xAxis.setGranularity(1f); // only intervals of 1 day
//        xAxis.setLabelCount(7);
//        xAxis.setValueFormatter(xAxisFormatter);

        IAxisValueFormatter custom = new MyAxisValueFormatter();

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(mTfLight);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        // l.setExtra(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });
        // l.setCustom(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });

//        XYMarkerView mv = new XYMarkerView(this, xAxisFormatter);
//        mv.setChartView(mChart); // For bounds control
//        mChart.setMarker(mv); // Set the marker to the chart

//        setData(5, 5);

        // setting data
//        mSeekBarY.setProgress(5);
//        mSeekBarX.setProgress(5);
//
//        mSeekBarY.setOnSeekBarChangeListener(this);
//        mSeekBarX.setOnSeekBarChangeListener(this);

        // mChart.setDrawLegend(false);

        fetchData();
    }

    private void fetchData() {
        String routeData = getIntent().getStringExtra("route_data");
        if (routeData != null && routeData.length() > 0) {
            ServerAsyncTaskPost asyncTask = new ServerAsyncTaskPost(this, mGraphServerHelper);
            mGraphServerHelper.setUrl("https://iptools.cyient.com/runnerAtRoad/webresource/myresource/routeBasedCondition");
            asyncTask.execute(routeData);
        }
    }


    ServerHelper mGraphServerHelper = new ServerHelper() {

        @Override
        public void onFailure(Object o) throws NullPointerException {

        }

        @Override
        public void onSuccess(Object o) throws NullPointerException {
            String res = o.toString();
            Log.d(TAG, res);
            if (res != null && res.length() > 0) {
                try {
                    JSONArray jsonArray = new JSONArray(res);
                    if (jsonArray.length() > 0) {
                        setData(jsonArray);
                    } else {
                        Snackbar.make(mView, getString(R.string.route_not_avl), Snackbar.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mChart.animateXY(3000, 3000);
            }

        }

        @Override
        public void onServerError(String message) {

        }
    };


    private void setData(JSONArray arr) {

        float start = 1f;

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        try {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String roadCond = obj.getString("Roadcondition");
                int sum = obj.getInt("Sum");

                yVals1.add(new BarEntry(i, sum));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        BarDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "Road Conditions");

            set1.setDrawIcons(false);
            int color1 = ContextCompat.getColor(this, R.color.tITANIUM_YELLOW);
            int color2 = ContextCompat.getColor(this, R.color.cONGO_PINK);
            int color3 = ContextCompat.getColor(this, R.color.mEDIUM_AQUAMARINE);
            int color4 = ContextCompat.getColor(this, android.R.color.holo_red_dark);
            int color5 = ContextCompat.getColor(this, android.R.color.holo_orange_dark);

            int[] colors = new int[5];
            colors[0] = color1;
            colors[1] = color2;
            colors[2] = color3;
            colors[3] = color4;
            colors[4] = color5;

            set1.setColors(colors, 120);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setValueTypeface(mTfLight);
            data.setBarWidth(0.9f);

            mChart.setData(data);
        }

        IAxisValueFormatter xAxisFormatter = new RoadConditionValueFormatter(mChart, arr);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

    }

    NavigationView.OnNavigationItemSelectedListener onNavItemSelListener = new NavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            mDrawer.closeDrawer(Gravity.START);
            if (mChart.getData() == null){
                return false;
            }
            switch (item.getItemId()) {
                case R.id.actionToggleValues: {
                    for (IDataSet set : mChart.getData().getDataSets())
                        set.setDrawValues(!set.isDrawValuesEnabled());

                    mChart.invalidate();
                    break;
                }
                case R.id.actionToggleIcons: {
                    for (IDataSet set : mChart.getData().getDataSets())
                        set.setDrawIcons(!set.isDrawIconsEnabled());

                    mChart.invalidate();
                    break;
                }
                case R.id.actionToggleHighlight: {
                    if (mChart.getData() != null) {
                        mChart.getData().setHighlightEnabled(!mChart.getData().isHighlightEnabled());
                        mChart.invalidate();
                    }
                    break;
                }
                case R.id.actionTogglePinch: {
                    if (mChart.isPinchZoomEnabled())
                        mChart.setPinchZoom(false);
                    else
                        mChart.setPinchZoom(true);

                    mChart.invalidate();
                    break;
                }
                case R.id.actionToggleAutoScaleMinMax: {
                    mChart.setAutoScaleMinMaxEnabled(!mChart.isAutoScaleMinMaxEnabled());
                    mChart.notifyDataSetChanged();
                    break;
                }
                case R.id.actionToggleBarBorders: {
                    for (IBarDataSet set : mChart.getData().getDataSets())
                        ((BarDataSet) set).setBarBorderWidth(set.getBarBorderWidth() == 1.f ? 0.f : 1.f);

                    mChart.invalidate();
                    break;
                }
                case R.id.animateX: {
                    mChart.animateX(3000);
                    break;
                }
                case R.id.animateY: {
                    mChart.animateY(3000);
                    break;
                }
                case R.id.animateXY: {

                    mChart.animateXY(3000, 3000);
                    break;
                }
                case R.id.actionSave: {
                    if (mChart.saveToGallery("title" + System.currentTimeMillis(), 50)) {
                        Toast.makeText(getApplicationContext(), "Saving SUCCESSFUL!",
                                Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(getApplicationContext(), "Saving FAILED!", Toast.LENGTH_SHORT)
                                .show();
                    break;
                }
            }
            return true;
        }
    };
//
//    @Override
//    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//        tvX.setText("" + (mSeekBarX.getProgress() + 2));
//        tvY.setText("" + (mSeekBarY.getProgress()));
//
////        setData(mSeekBarX.getProgress() + 1, mSeekBarY.getProgress());
//        mChart.invalidate();
//    }
//
//    @Override
//    public void onStartTrackingTouch(SeekBar seekBar) {
//        // TODO Auto-generated method stub
//    }
//
//    @Override
//    public void onStopTrackingTouch(SeekBar seekBar) {
//        // TODO Auto-generated method stub
//    }

//    private void setData(int count, float range) {
//
//        float start = 1f;
//
//        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
//
//        for (int i = (int) start; i < start + count + 1; i++) {
//            float mult = (range + 1);
//            float val = (float) (Math.random() * mult);
//
//            if (Math.random() * 100 < 25) {
//                yVals1.add(new BarEntry(i, val, getResources().getDrawable(R.drawable.star)));
//            } else {
//                yVals1.add(new BarEntry(i, 10));
//            }
//        }
//
//        BarDataSet set1;
//
//        if (mChart.getData() != null &&
//                mChart.getData().getDataSetCount() > 0) {
//            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
//            set1.setValues(yVals1);
//            mChart.getData().notifyDataChanged();
//            mChart.notifyDataSetChanged();
//        } else {
//            set1 = new BarDataSet(yVals1, "The year 2017");
//
//            set1.setDrawIcons(false);
//
////            set1.setColors(ColorTemplate.MATERIAL_COLORS);
//
//            /*int startColor = ContextCompat.getColor(this, android.R.color.holo_blue_dark);
//            int endColor = ContextCompat.getColor(this, android.R.color.holo_blue_bright);
//            set1.setGradientColor(startColor, endColor);*/
//
////            int startColor1 = ContextCompat.getColor(this, android.R.color.holo_orange_light);
////            int startColor2 = ContextCompat.getColor(this, android.R.color.holo_blue_light);
////            int startColor3 = ContextCompat.getColor(this, android.R.color.holo_orange_light);
////            int startColor4 = ContextCompat.getColor(this, android.R.color.holo_green_light);
////            int startColor5 = ContextCompat.getColor(this, android.R.color.holo_red_light);
//            int endColor1 = ContextCompat.getColor(this, android.R.color.holo_blue_dark);
//            int endColor2 = ContextCompat.getColor(this, android.R.color.holo_purple);
//            int endColor3 = ContextCompat.getColor(this, android.R.color.holo_green_dark);
//            int endColor4 = ContextCompat.getColor(this, android.R.color.holo_red_dark);
//            int endColor5 = ContextCompat.getColor(this, android.R.color.holo_orange_dark);
//
////            List<GradientColor> gradientColors = new ArrayList<>();
////            gradientColors.add(new GradientColor(startColor1, endColor1));
////            gradientColors.add(new GradientColor(startColor2, endColor2));
////            gradientColors.add(new GradientColor(startColor3, endColor3));
////            gradientColors.add(new GradientColor(startColor4, endColor4));
////            gradientColors.add(new GradientColor(startColor5, endColor5));
//
////            set1.setGradientColors(gradientColors);
//
//
//            int[] colors = new int[5];
//            colors[0] = endColor1;
//            colors[1] = endColor2;
//            colors[2] = endColor3;
//            colors[3] = endColor4;
//            colors[4] = endColor5;
//
//            set1.setColors(colors, 120);
//
//            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
//            dataSets.add(set1);
//
//            BarData data = new BarData(dataSets);
//            data.setValueTextSize(10f);
//            data.setValueTypeface(mTfLight);
//            data.setBarWidth(0.9f);
//
//            mChart.setData(data);
//        }
//    }

    protected RectF mOnValueSelectedRectF = new RectF();

    @SuppressLint("NewApi")
    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;

        RectF bounds = mOnValueSelectedRectF;
        mChart.getBarBounds((BarEntry) e, bounds);
        MPPointF position = mChart.getPosition(e, YAxis.AxisDependency.LEFT);

        Log.i("bounds", bounds.toString());
        Log.i("position", position.toString());

        Log.i("x-index",
                "low: " + mChart.getLowestVisibleX() + ", high: "
                        + mChart.getHighestVisibleX());

        MPPointF.recycleInstance(position);
    }

    @Override
    public void onNothingSelected() {
    }
}