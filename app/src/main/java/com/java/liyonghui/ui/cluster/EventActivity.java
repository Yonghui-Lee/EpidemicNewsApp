package com.java.liyonghui.ui.cluster;

import android.content.Context;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.java.liyonghui.NewsContentActivity;
import com.java.liyonghui.R;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.openapi.WBAPIFactory;

import java.util.ArrayList;

public class EventActivity extends AppCompatActivity {
    private ArrayList<Event> eventList;
    public static void actionStart(Context context, ArrayList<String> events) {
        Intent intent = new Intent(context, EventActivity.class);
        intent.putStringArrayListExtra("events",events);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cluster_content);
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
