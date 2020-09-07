package com.java.liyonghui.ui.wiki;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.java.liyonghui.R;
import com.java.liyonghui.channel.Channel;
import com.java.liyonghui.ui.cluster.Event;
import com.java.liyonghui.ui.cluster.EventAdapter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class WiKiAdapter extends RecyclerView.Adapter<WiKiAdapter.ViewHolder> {
    private List<WiKi> mWiKiList;
    private Context mContext;


    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView wikiImage;
        TextView wikiLabel;
        TextView wikiInfo;
        RelationGridView relationGridView;
        RelationGridView propertiesGridView;

        public ViewHolder(View view) {
            super(view);
            wikiImage = view.findViewById(R.id.wiki_image);
            wikiLabel = view.findViewById(R.id.wiki_label);
            wikiInfo = view.findViewById(R.id.wiki_info);
            relationGridView = view.findViewById(R.id.relation_gridview);
            propertiesGridView = view.findViewById(R.id.propertiesView);
        }
    }

    public WiKiAdapter(Context context,List<WiKi> WiKiList) {
        this.mContext= context;
        this.mWiKiList = WiKiList;
    }

    @Override
    public WiKiAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wiki_item, parent, false);
        return new WiKiAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WiKiAdapter.ViewHolder holder, int position) {
        WiKi wiki = mWiKiList.get(position);

        holder.wikiLabel.setText(wiki.return_value("label"));
        holder.wikiInfo.setText(wiki.return_value("enwiki")+wiki.return_value("baidu")+wiki.return_value("zhwiki"));

        if(wiki.getImage()==null){
            holder.wikiImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.lack_photo));
        }else{
            holder.wikiImage.setImageBitmap(wiki.getImage());
        }


        holder.relationGridView.setClickable(false);
        holder.relationGridView.setPressed(false);
        holder.relationGridView.setEnabled(false);

        holder.propertiesGridView.setClickable(false);
        holder.propertiesGridView.setPressed(false);
        holder.propertiesGridView.setEnabled(false);


        ArrayList<HashMap<String, Object>> arraylist = new ArrayList<HashMap<String, Object>>();
        ArrayList<Relation> arr = wiki.getRelations();
        int size = arr.size();
        for(int i=0;i<size;i++){
            HashMap<String, Object> hm = new HashMap<String, Object>();
            Relation r = arr.get(i);
            hm.put("relation",r.getRelation() + " "+ (r.getForward().equals("true")?"->":"<-") + " ");
            hm.put("label",r.getLabel());
            arraylist.add(hm);
        }

        SimpleAdapter sa = new SimpleAdapter(mContext,arraylist,R.layout.relation_item,new String[] { "relation", "label"},
                // ImageItem的XML文件里面的一个ImageView,两个TextView ID
                new int[] { R.id.relation_text, R.id.relation_label});
        holder.relationGridView.setAdapter(sa);

        ArrayList<HashMap<String, Object>> propertiesList = new ArrayList<HashMap<String, Object>>();
        ArrayList<String> propertiesArr = wiki.getProperties();
        int propertiesSize = propertiesArr.size();
        for(int i=0;i<propertiesSize;i++){
            HashMap<String, Object> hm = new HashMap<String, Object>();
            String[] sArr = propertiesArr.get(i).split("::");
            hm.put("name",sArr[0]+": ");
            hm.put("properties",sArr[1]);
            propertiesList.add(hm);
        }

        SimpleAdapter sb = new SimpleAdapter(mContext,propertiesList,R.layout.relation_item,new String[] { "name", "properties"},
                // ImageItem的XML文件里面的一个ImageView,两个TextView ID
                new int[] { R.id.relation_text, R.id.relation_label});
        holder.propertiesGridView.setAdapter(sb);
    }

    @Override
    public int getItemCount() {
        return mWiKiList.size();
    }
}

