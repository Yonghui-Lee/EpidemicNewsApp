package com.java.liyonghui.ui.cluster;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.java.liyonghui.R;
import com.java.liyonghui.channel.Channel;
import com.java.liyonghui.channel.ChannelAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


public class ClusterFragment extends Fragment {
    private LinkedHashMap<String, ArrayList<String>> invertedIndex;
    private GridView eventGridView;
    ChannelAdapter eventAdapter;
    ArrayList<Channel> eventList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cluster, container, false);
        final TextView textView = root.findViewById(R.id.text_cluster);

        eventGridView = (GridView) root.findViewById(R.id.clustGridView);
        eventGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 跳转到另一个界面查看商品详细信息
                TextView newTextView = (TextView) view.findViewById(R.id.text_item);

                final Channel channel = ((ChannelAdapter) parent.getAdapter()).getItem(position);
//                Toast.makeText(getActivity(), channel.getName(), Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(getActivity(),
//                        ProductDetailActivity.class);
//                intent.putExtra("productId", position);//携带参数
//                startActivity(intent);'
                EventActivity.actionStart(getActivity(),invertedIndex.get(channel.getName()),channel.getName());
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                initHashMap();
                eventList = new ArrayList<>();
                for(Map.Entry<String,ArrayList<String>> entry : invertedIndex.entrySet()) {
                    eventList.add(new Channel(entry.getKey()));
                }
                eventAdapter = new ChannelAdapter(getContext(),eventList);

                new Handler(Looper.getMainLooper()).post(new Runnable(){
                    @Override
                    public void run() {
                        eventGridView.setAdapter(eventAdapter);
                    }
                });
            }
        }).start();



        return root;
    }

    void initHashMap(){
        Gson gson = new Gson();
        invertedIndex = gson.fromJson(ReadDayDayString(),new TypeToken<LinkedHashMap<String,ArrayList<String>>>(){}.getType());
//        for(Map.Entry<String,ArrayList<String>> entry : invertedIndex.entrySet()) {
//            System.out.println("key:" + entry.getKey() + "   value:" + entry.getValue().get(0));
//        }
    }

    //读取本地JSON字符
    public String ReadDayDayString() {
        InputStream is = null;
        String msg = null;
        try {
            is = getActivity().getResources().getAssets().open("InvertedIndex.json");
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            msg = new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return msg;
    }

}
