package com.java.liyonghui.ui.cluster;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.java.liyonghui.R;

import java.util.ArrayList;

public class EventActivity extends AppCompatActivity {
    private ArrayList<Event> eventList;
    public static void actionStart(Context context, ArrayList<String> events, String cluster) {
        Intent intent = new Intent(context, EventActivity.class);
        intent.putStringArrayListExtra("events",events);
        intent.putExtra("cluster",cluster);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.cluster_content);
        String cluster = getIntent().getStringExtra("cluster");
        Log.e("this",cluster);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(cluster);
        eventList = new ArrayList<>();
        ArrayList<String> arr = getIntent().getStringArrayListExtra("events");// 获取传入的新闻标题
        for(String s:arr){
            String[] str = s.split("\\$");
            eventList.add(new Event(str[0],str[1]));
        }
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.event_title_view);
        StaggeredGridLayoutManager layoutManager = new
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        EventAdapter adapter = new EventAdapter(eventList);
        recyclerView.setAdapter(adapter);

    }


}
