package com.java.liyonghui;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.java.liyonghui.ui.data.EpidemicData;
import com.java.liyonghui.ui.news.News;
import com.orm.SugarContext;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.openapi.IWBAPI;
import com.sina.weibo.sdk.openapi.WBAPIFactory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    //在微博开发平台为应用申请的App Key
    private static final String APP_KY = "990645216";
    //在微博开放平台设置的授权回调页
    private static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    //在微博开放平台为应用申请的高级权限
    private static final String SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";
    private IWBAPI mWBAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_news, R.id.navigation_cluster, R.id.navigation_wiki, R.id.navigation_data, R.id.navigation_scholar)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        initSdk();

        SugarContext.init(this);
        //InvertedIndex.deleteAll(InvertedIndex.class);
        //News.deleteAll(News.class);
        //EpidemicData.deleteAll(EpidemicData.class);



        //initEpidemicData();
        //initNewsData();
    }

    void initNewsData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request.Builder reqBuild = new Request.Builder();
                    HttpUrl.Builder urlBuilder = HttpUrl.parse("https://covid-dashboard.aminer.cn/api/dist/events.json")
                            .newBuilder();
                    reqBuild.url(urlBuilder.build());
                    Request request = reqBuild.build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    JSONObject outerJSON = new JSONObject(responseData);
                    JSONArray jsonArray = outerJSON.getJSONArray("datas");
                    for(int i=0; i<jsonArray.length(); i++){
                        if(i>4000) break;
                        JSONObject innerJSON = jsonArray.getJSONObject(i);
                        String id = innerJSON.getString("_id");
                        String time = innerJSON.getString("time");
                        String title = innerJSON.getString("title");
                        News news = new News(id, title, "", time, "");
                        //Log.d("this",id+" "+ time+" "+ title);
                        news.save();
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }


    void initEpidemicData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    OkHttpClient client = new OkHttpClient();
                    Request.Builder reqBuild = new Request.Builder();
                    HttpUrl.Builder urlBuilder = HttpUrl.parse("https://covid-dashboard.aminer.cn/api/dist/epidemic.json")
                            .newBuilder();
                    reqBuild.url(urlBuilder.build());
                    Request request = reqBuild.build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    JSONObject outerJSON = new JSONObject(responseData);
                    //通过迭代器获得json当中所有的key值
                    Iterator keys = outerJSON.keys();
                    //然后通过循环遍历出的key值
                    while (keys.hasNext()) {
                        String key = String.valueOf(keys.next());
                        boolean isChinaprovinces = isChinaProvince(key);
                        boolean isCountries = isCountry(key);
                        if(isChinaprovinces||isCountries){
                            JSONArray jsonArray = outerJSON.getJSONObject(key).getJSONArray("data");
                            JSONArray jsonAr = jsonArray.getJSONArray(jsonArray.length()-1);
                            int confirmed = jsonAr.getInt(0);
                            int cured = jsonAr.getInt(2);
                            int dead = jsonAr.getInt(3);
                            EpidemicData epidemicData;
                            if(isChinaprovinces){
                                epidemicData = new EpidemicData(key.substring(6),"province",confirmed,cured,dead);
                            }else{
                                epidemicData = new EpidemicData(key,"country",confirmed,cured,dead);
                            }
                            epidemicData.save();
                        }
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static boolean isChinaProvince(String str) {
        String regex = "^China\\|(\\w|\\s)*$";
        return str.matches(regex);
    }

    public static boolean isCountry(String str) {
        String regex = "^(\\w|\\s)*$";
        return str.matches(regex) && !str.equals("World");
    }


    private void initSdk() {
        AuthInfo authInfo = new AuthInfo(this, APP_KY, REDIRECT_URL, SCOPE);
        mWBAPI = WBAPIFactory.createWBAPI(this);
        mWBAPI.registerApp(this, authInfo);
    }
}