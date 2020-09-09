package com.java.liyonghui.ui.data;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bin.david.form.core.SmartTable;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.tabs.TabLayout;
import com.java.liyonghui.R;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DataFragment extends Fragment {
    private String[] xLabel;
    private BarChart mBarChart;
    private TabLayout mTabLayout;
    private String mDataType;
    private List<EpidemicData> mDataList;
    private SmartTable<EpidemicData> table;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_data, container, false);
        //setHasOptionsMenu(true);




        mTabLayout = root.findViewById(R.id.tabDataLayout);
        // 添加 tab item

        mTabLayout.addTab(mTabLayout.newTab().setText("国内统计"));
        mTabLayout.addTab(mTabLayout.newTab().setText("国外统计"));

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals("国内统计")) {
                    mDataType = "province";
                    getData();
                }
                if (tab.getText().equals("国外统计")) {
                    mDataType = "country";
                    getData();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        mDataType = "province";
        table = root.findViewById(R.id.province_table);
        table.getConfig().setShowXSequence(false);
        table.getConfig().setShowYSequence(false);
        table.getConfig().setShowTableTitle(false);
        mBarChart = root.findViewById(R.id.barChart);

        xLabel = new String[]{"","","","",""};
        getData();

        mBarChart.setExtraTopOffset(25);
        mBarChart.setExtraLeftOffset(30);
        mBarChart.setExtraRightOffset(100);
        mBarChart.setExtraBottomOffset(50);
        mBarChart.getDescription().setText("");
        mBarChart.getLegend().setEnabled(false);
        loadChartData();
        setXAxis();
        return root;
    }
    private void loadChartData() {




    }

    private void setXAxis() {
        //得到xAxis对象
        XAxis xAxis = mBarChart.getXAxis();
        //设置x轴是显示在顶部还是底部，柱状图内还是外
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //设置文字旋转的角度
        xAxis.setLabelRotationAngle(60);
        //设置最小值，可通过该属性设置与左边的距离
        xAxis.setAxisMinimum(-0.5f);
        //设置最大值
        xAxis.setAxisMaximum(4.5f);
        xAxis.setLabelCount(5);
        //设置靠近x轴第一条线的颜色
//        xAxis.setAxisLineColor(Color.RED);
        //设置绘制靠近x轴的第一条线的宽度
//        xAxis.setAxisLineWidth(5f);
        //是否绘制横向的网格
        xAxis.setDrawGridLines(false);

        //自定义格式
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int tep = (int) (value + 1);
                return xLabel[tep-1];
            }
        });
        //设置x轴在y方向上的偏移量
        xAxis.setYOffset(10f);
        //设置x轴字体大小
        xAxis.setTextSize(10f);
        //设置x轴字体颜色
//        xAxis.setTextColor(Color.GREEN);
        xAxis.setGranularity(1);
    }


    void getData(){
        mDataList = Select.from(EpidemicData.class)
                .where(Condition.prop("category").eq(mDataType)).list();
        Collections.sort(mDataList,new Comparator<EpidemicData>(){
            @Override
            public int compare(EpidemicData o1, EpidemicData o2) {
                return Integer.compare(o2.getConfirmed(), o1.getConfirmed());
            }
        });
        table.setData(mDataList);

        if(mDataList.size()>5){
            List<BarEntry> barEntries = new ArrayList<BarEntry>();
            String[] labels = new String[5];
            for(int i=0;i<5;i++){
                barEntries.add(new BarEntry(i,mDataList.get(i).getConfirmed()));
                labels[i] = mDataList.get(i).getLocation();
            }
            BarDataSet barDataSet = new BarDataSet(barEntries,"title");
            BarData ba = new BarData(barDataSet);
            xLabel = labels;
            mBarChart.setData(ba);
        }
    }

}

