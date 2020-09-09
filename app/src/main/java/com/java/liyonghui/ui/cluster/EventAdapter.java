package com.java.liyonghui.ui.cluster;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.java.liyonghui.R;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>{

    private List<Event> mEventList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventTitleText;
        TextView eventTimeText;

        public ViewHolder(View view) {
            super(view);
            eventTitleText = (TextView) view.findViewById(R.id.event_title);
            eventTimeText = (TextView) view.findViewById(R.id.event_time);
        }
    }

    public EventAdapter(List<Event> eventList) {
        mEventList = eventList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cluster_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event event = mEventList.get(position);
        holder.eventTitleText.setText(event.getName());
        holder.eventTimeText.setText(event.getTime());
    }

    @Override
    public int getItemCount() {
        if(mEventList!=null){
            return mEventList.size();
        }
        else return 0;
    }
}
