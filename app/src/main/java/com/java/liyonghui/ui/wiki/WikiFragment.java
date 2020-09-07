package com.java.liyonghui.ui.wiki;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.java.liyonghui.News;
import com.java.liyonghui.R;
import com.java.liyonghui.ui.news.NewsFragment;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class WikiFragment extends Fragment {

    private WikiViewModel wikiViewModel;

    private EditText search_bar;
    private Button search_button;
    private RecyclerView recyclerView;

    private ArrayList<WiKi> wikiList;
    private WiKiAdapter adapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        wikiViewModel =
                ViewModelProviders.of(this).get(WikiViewModel.class);
        View root = inflater.inflate(R.layout.fragment_wiki, container, false);

        recyclerView = root.findViewById(R.id.wiki_view);
        search_bar = root.findViewById(R.id.search_bar);
        search_button = root.findViewById(R.id.search_button);
        search_button.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v)
            {
                final String keyword = search_bar.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            OkHttpClient client= new OkHttpClient();
                            Request.Builder reqBuild = new Request.Builder();
                            HttpUrl.Builder urlBuilder =HttpUrl.parse("https://innovaapi.aminer.cn/covid/api/v1/pneumonia/entityquery")
                                    .newBuilder();
                            urlBuilder.addQueryParameter("entity", keyword);
                            reqBuild.url(urlBuilder.build());
                            Request request = reqBuild.build();
                            Log.d("this",request.toString());
                            Response response = client.newCall(request).execute();
                            String responseData = response.body().string();
                            JSONObject outerJSON = new JSONObject(responseData);
                            JSONArray jsonArray = outerJSON.getJSONArray("data");

                            wikiList = new ArrayList<WiKi>();


                            for(int i = 0; i < jsonArray.length(); i++) {
                                //HashMap<String,Object> wikiMap = new HashMap<String,Object>();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String hot = jsonObject.getString("hot");
                                String label = jsonObject.getString("label");
                                String url = jsonObject.getString("url");
                                String imageURL = jsonObject.getString("img");
                                //Log.e("this",imageURL);
                                Bitmap image = null;
                                if(!imageURL.equals("null")){

                                    Request imgRequest = new Request.Builder().url(imageURL).build();
                                    ResponseBody body = client.newCall(imgRequest).execute().body();
                                    InputStream in = body.byteStream();
                                    image = BitmapFactory.decodeStream(in);
                                }

                                JSONObject wiki_json = jsonObject.getJSONObject("abstractInfo");
                                String enwiki = wiki_json.getString("enwiki");
                                String baidu = wiki_json.getString("baidu");
                                String zhwiki = wiki_json.getString("zhwiki");
                                JSONObject covid_json = wiki_json.getJSONObject("COVID");
                                String properties_string = covid_json.getString("properties");
                                Gson gson = new Gson();
                                HashMap<String,String> pro_map = gson.fromJson(properties_string,new TypeToken<HashMap<String,String>>(){}.getType());
                                ArrayList<String> properties = new ArrayList<>();
                                for(Map.Entry<String,String> entry3 : pro_map.entrySet()) {
                                    String key_ = entry3.getKey();
                                    String value_ = entry3.getValue();
                                    properties.add(key_ + "::" + value_);
                                }

                                WiKi newdata = new WiKi(hot,label,url,enwiki,baidu,zhwiki,properties,image);
                                ArrayList<Relation> relationlist = new ArrayList<Relation>();
                                JSONArray relationsArray = covid_json.getJSONArray("relations");
                                for(int j = 0; j < relationsArray.length(); j++) {
                                    JSONObject relation_json = relationsArray.getJSONObject(j);
                                    String relations = relation_json.getString("relation");
                                    String relation_url = relation_json.getString("url");
                                    String relation_label = relation_json.getString("label");
                                    String relation_forward = relation_json.getString("forward");
                                    Relation new_relation = new Relation(relations,relation_url,relation_label ,relation_forward);
                                    newdata.add_relation(new_relation);
                                }
                                wikiList.add(newdata);
                            }

                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }

                        new Handler(Looper.getMainLooper()).post(new Runnable(){
                            @Override
                            public void run() {
                                adapter= new WiKiAdapter(getActivity(), wikiList);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                recyclerView.setAdapter(adapter);
                            }
                        });
                    }
                }).start();
            }
        });
        return root;
    }

}