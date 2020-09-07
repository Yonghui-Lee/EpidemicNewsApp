package com.java.liyonghui.ui.scholar;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.java.liyonghui.News;
import com.java.liyonghui.R;
import com.java.liyonghui.channel.Channel;
import com.java.liyonghui.ui.cluster.Event;
import com.java.liyonghui.ui.cluster.EventAdapter;


import java.util.List;


public class ScholarAdapter extends RecyclerView.Adapter<ScholarAdapter.ViewHolder> {

    private Context context;
    private List<Scholar> mScholarList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView scholarImage;
        TextView scholarNameText;
        TextView scholarHindexText;
        TextView scholarActivityText;
        TextView scholarSociabilityText;
        TextView scholarCititationsText;
        TextView scholarPubsText;
        TextView scholarPositionText;
        TextView scholarAffiliationText;

        public ViewHolder(View view) {
            super(view);
            scholarImage = view.findViewById(R.id.scholar_image);
            scholarNameText = view.findViewById(R.id.scholar_name);
            scholarHindexText = view.findViewById(R.id.scholar_hindex);
            scholarActivityText = view.findViewById(R.id.scholar_activity);
            scholarSociabilityText = view.findViewById(R.id.scholar_sociability);
            scholarCititationsText = view.findViewById(R.id.scholar_citations);
            scholarPubsText = view.findViewById(R.id.scholar_pubs);
            scholarPositionText = view.findViewById(R.id.scholar_position);
            scholarAffiliationText = view.findViewById(R.id.scholar_affiliation);
        }
    }

    public ScholarAdapter(Context context, List<Scholar> _scholarList) {
        this.context = context;
        this.mScholarList = _scholarList;
    }

    @Override
    public ScholarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scholar_item, parent, false);
        return new ScholarAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ScholarAdapter.ViewHolder holder, final int position) {
        Scholar scholar = mScholarList.get(position);
        if(holder.scholarImage==null)
            Log.e("this","NULLLLLL");
        if(scholar.getAvatar()==null){
            holder.scholarImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.lack_photo));
        }else{
            holder.scholarImage.setImageBitmap(scholar.getAvatar());
        }

        holder.scholarNameText.setText(scholar.getName());
        holder.scholarHindexText.setText("H:"+String.valueOf(scholar.getHindex()));
        holder.scholarActivityText.setText("A:"+String.valueOf(scholar.getActivity()));
        holder.scholarSociabilityText.setText("S:"+String.valueOf(scholar.getSociability()));
        holder.scholarCititationsText.setText("C:"+String.valueOf(scholar.getCitations()));
        holder.scholarPubsText.setText("P:"+String.valueOf(scholar.getPubs()));
        holder.scholarPositionText.setText(scholar.getPosition());
        holder.scholarAffiliationText.setText(scholar.getAffiliation());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mScholarList.size();
    }

    public void setData(List<Scholar> list) {
        mScholarList = list;
        notifyDataSetChanged();

    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    private ScholarAdapter.OnItemClickListener listener;

    public void setOnItemClickListener(ScholarAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

}