package com.java.liyonghui.ui.data;

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
import com.google.android.material.tabs.TabLayout;
import com.java.liyonghui.InvertedIndex;
import com.java.liyonghui.R;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

public class DataFragment extends Fragment {
    private TabLayout mTabLayout;
    private DataViewModel dataViewModel;
    private String mDataType;
    private List<EpidemicData> mDataList;
    private SmartTable<EpidemicData> table;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dataViewModel =
                ViewModelProviders.of(this).get(DataViewModel.class);
        View root = inflater.inflate(R.layout.fragment_data, container, false);
        setHasOptionsMenu(true);
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

//        List<EpidemicData> epidemicDataList= new ArrayList<>();
//        epidemicDataList.add(new EpidemicData("China","country",999,999,99));
//        epidemicDataList.add(new EpidemicData("China1","country",99,999,99));
//        epidemicDataList.add(new EpidemicData("China2","country",999,99,99));
//        epidemicDataList.add(new EpidemicData("China3","country",989,9979,99));
//        epidemicDataList.add(new EpidemicData("China4","country",999,9299,99));
//        epidemicDataList.add(new EpidemicData("China5","country",9399,999,99));
//        table.setData(epidemicDataList);
        mDataType = "province";
        table = root.findViewById(R.id.province_table);
        table.getConfig().setShowXSequence(false);
        table.getConfig().setShowYSequence(false);
        table.getConfig().setShowTableTitle(false);
        getData();
        return root;
    }
    void getData(){
        mDataList = Select.from(EpidemicData.class)
                .where(Condition.prop("category").eq(mDataType)).list();
        table.setData(mDataList);
    }

}