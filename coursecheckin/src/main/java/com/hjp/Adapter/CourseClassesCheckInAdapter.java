package com.hjp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.hjp.NetWork.MainBmobQuery;
import  com.hjp.coursecheckin.R;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by HJP on 2016/5/17 0017.
 */
public class CourseClassesCheckInAdapter extends RecyclerView.Adapter<CourseClassesCheckInViewHolder> {

    private final String TAG = "test";
    private Context mContext;
    private LayoutInflater layoutInflater;
    private final List<String> mClasses;
    private final Map<String, List<Double>> mClassesCheckInInfo;
    private String mJobNum;
    private String mCourseName;

    public CourseClassesCheckInAdapter(Context context, String jobNum, String courseName, List<String> classes, Map<String, List<Double>> classesCheckInInfo) {
        super();
        Log.i(TAG, "CourseClassesCheckInAdapter: ");
        mContext = context;
        mClasses = classes;
        mClassesCheckInInfo = classesCheckInInfo;
        mJobNum = jobNum;
        mCourseName = courseName;
        layoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public CourseClassesCheckInViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder: ");
        View itemLayout = layoutInflater.inflate(R.layout.cell_classcheckin, parent, false);
        return new CourseClassesCheckInViewHolder(itemLayout);
    }

    @Override
    public void onBindViewHolder(CourseClassesCheckInViewHolder holder, final int position) {

        String className = mClasses.get(position);

        List<Double> classCheckInInfo = mClassesCheckInInfo.get(className);

        //classCheckInInfo的size有可能为0
        int size_checkIn = classCheckInInfo.size();
        if (size_checkIn <= 0) {
            classCheckInInfo.add(0D);
            classCheckInInfo.add(0D);
        } else if (size_checkIn == 1) {
            classCheckInInfo.add(0D);
        }
        double checkInSum = classCheckInInfo.get(0);
        double notCheckInSum = classCheckInInfo.get(1);
        double sum = checkInSum + notCheckInSum;
        float proportion_chedkIn = (float) (checkInSum / sum);
        float proportion_notChedkIn = (float) (notCheckInSum / sum);
        double checkIndata = proportion_chedkIn * 100;
        double notCheckIndata = proportion_notChedkIn * 100;
        double[] checkInData = new double[]{checkIndata};
        double[] notCheckInData = new double[]{notCheckIndata};

        Log.i(TAG, "chart: " + checkIndata + "|" + notCheckIndata);
        showBarChart(holder.cardV_wrapBarChart, "  总人数" + (int) sum, className, checkInData, notCheckInData);

        //查看当前班级的具体签到情况(哪些是签到、迟到等,显示头像和学号)
        holder.cardV_wrapBarChart.setId(position);
        holder.cardV_wrapBarChart.setTag(className);
        holder.cardV_wrapBarChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickId = v.getId();
                if (clickId == position) {
                    String currClass = (String) v.getTag();
                    Bundle bundle = new Bundle();
                    bundle.putString("jobNum", mJobNum);
                    bundle.putString("courseName", mCourseName);
                    bundle.putString("className", currClass);

                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);

                    Intent intent = new Intent();
                    intent.setAction("com.hjp.jumpActivity");
                    intent.putExtras(bundle);
                    localBroadcastManager.sendBroadcast(intent);
                    Log.i(TAG, "onClick: " + position);

                }
            }
        });
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        Log.i(TAG, "getItemCount: ");
        return mClasses.size();
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            String tag = (String) v.getTag();
            String currClass = null;
            MainBmobQuery bmobQuery = new MainBmobQuery(mContext);

            switch (tag) {
                case "classInfo":
                    currClass = mClasses.get(id);
                    Log.i(TAG, "onClick: classInfo");
                    bmobQuery.querySpecialCourseClass(mJobNum, mCourseName, currClass, new MainBmobQuery.SpecialClassQueryListener() {
                        @Override
                        public void Success(List<String> list) {
                            Log.i(TAG, "Success: querySpecialCourseClass");
                            LinearLayout contentView = (LinearLayout) layoutInflater.inflate(R.layout.exact_classcheck_ininfo, null);
                            PopupWindow ppWindow = new PopupWindow(contentView
                                    , LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            ppWindow.setFocusable(false);
                            ppWindow.setOutsideTouchable(false);
                            ppWindow.setBackgroundDrawable(new BitmapDrawable());

                            RecyclerView recyclerGridV = (RecyclerView) contentView.findViewById(R.id.gridV_showNotCheckInPeople);
                            recyclerGridV.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.HORIZONTAL, false));
                            CourseClassNotCheckInPeopleAdapter notCheckInPeopleAdapter = new CourseClassNotCheckInPeopleAdapter(
                                    mContext, list);
                            recyclerGridV.setAdapter(notCheckInPeopleAdapter);

                        }

                        @Override
                        public void Error(String msg) {

                        }
                    });
                    break;
            }
        }
    };

    protected void setChartSettings(XYMultipleSeriesRenderer renderer,
                                    String title, String xTitle, String yTitle, double xMin,
                                    double xMax, double yMin, double yMax, int axesColor,
                                    int labelsColor) {
        renderer.setChartTitle(title);
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
        //控制图表标题、xy轴的标签、每一栋柱形的标题的文本的大小
        renderer.setAxisTitleTextSize(30);
        renderer.setChartTitleTextSize(30);
        renderer.setLabelsTextSize(30);
        renderer.setLegendTextSize(30);
        //调整y轴、x轴的标签文本与GraphicalView的距离
        renderer.setMargins(new int[]{50, 70, 0, 50});
        renderer.setXAxisMin(-4.5f);//负数设置第一条柱形跟y轴的距离因子
        renderer.setXAxisMax(xMax);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(yMax);
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
    }

    protected XYMultipleSeriesRenderer buildBarRenderer(int[] colors) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(16);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(15);
        int length = colors.length;
        for (int i = 0; i < length; i++) {
            SimpleSeriesRenderer r = new XYSeriesRenderer();
            r.setColor(colors[i]);
            renderer.addSeriesRenderer(r);
        }
        return renderer;
    }

    protected XYMultipleSeriesDataset buildBarDataset(String[] titles,
                                                      List<double[]> values) {//柱形图的数据源和饼图差不多，也是由一些键值对组成
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        int length = titles.length;
        for (int i = 0; i < length; i++) {
            CategorySeries series = new CategorySeries(titles[i]);
            double[] v = values.get(i);
            int seriesLength = v.length;
            for (int k = 0; k < seriesLength; k++) {
                series.add(v[k]);
            }
            dataset.addSeries(series.toXYSeries());
        }
        return dataset;
    }

    private void showBarChart(CardView cardV_wrapBarChart, String sum, String legendTitle, double[] checkInData, double[] notCheckInData) {

        String[] titles = new String[]{"未签到", "签到" + sum};
        List<double[]> values = new ArrayList<>();
        values.add(notCheckInData);
        values.add(checkInData);
        int[] colors = new int[]{Color.RED, Color.BLUE};
        XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);// 柱形图颜色设置
        setChartSettings(renderer, legendTitle, "", "", 0, 7, 0, 100,
                Color.GRAY, Color.LTGRAY);// 设置柱形图标题，横轴（X轴）、纵轴（Y轴）、最小的伸所刻度、最大的伸所刻度
        renderer.getSeriesRendererAt(0).setDisplayBoundingPoints(true);// 在第0条柱形图上显示数据
        renderer.getSeriesRendererAt(1).setDisplayBoundingPoints(true);// 在第1条柱形图上显示数据
        renderer.setXLabels(0);
        renderer.setYLabels(10);
        renderer.setXLabelsAlign(Paint.Align.LEFT);// 数据从左到右显示
        renderer.setYLabelsAlign(Paint.Align.RIGHT);
        renderer.setPanEnabled(true, false);
        renderer.setBarWidth(100);

        GraphicalView mChartView = ChartFactory.getBarChartView(mContext,
                buildBarDataset(titles, values), renderer, BarChart.Type.DEFAULT);
        mChartView.setBackgroundColor(Color.WHITE);
        renderer.setClickEnabled(true);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT);
        cardV_wrapBarChart.addView(mChartView, layoutParams);
    }
}
