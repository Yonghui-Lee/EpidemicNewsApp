package com.java.liyonghui.ui.news;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.tabs.TabLayout;

import com.java.liyonghui.R;
import com.java.liyonghui.channel.ChannelActivity;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class NewsFragment extends Fragment{
    private List<News> mNewsList;
    private int mCurrentPage = 1;
    private static final int PER_PAGE = 20;
    private NewsAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String myNewsType;
    private TabLayout mTabLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_news, container, false);
        setHasOptionsMenu(true);

        myNewsType = "news";
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(mTabLayout.getSelectedTabPosition()==(mTabLayout.getTabCount()-1))
                    new Handler(Looper.getMainLooper()).post(new Runnable(){
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                else{
                    //这里获取数据的逻辑
                    mNewsList = new ArrayList<>();
                    getNews();
                }
            }
        });
        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.news_title_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        mNewsList = new ArrayList<>();
        getNews();


        mTabLayout = root.findViewById(R.id.tabLayout);
        // 添加 tab item

        mTabLayout.addTab(mTabLayout.newTab().setText("news"));
        mTabLayout.addTab(mTabLayout.newTab().setText("paper"));
        mTabLayout.addTab(mTabLayout.newTab().setText(""));

        LinearLayout tabStrip = (LinearLayout) mTabLayout.getChildAt(0);
        View tabView = tabStrip.getChildAt(mTabLayout.getTabCount()-1);
        if (tabView != null) {
            tabView.setClickable(false);
        }

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals("news")) {
                    mCurrentPage = 1;
                    myNewsType = "news";
                    mNewsList = new ArrayList<>();
                    getNews();
                }
                if (tab.getText().equals("paper")) {
                    mCurrentPage = 1;
                    myNewsType = "paper";
                    mNewsList = new ArrayList<>();
                    getNews();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Button btn = (Button) root.findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(), ChannelActivity.class);
                int num = mTabLayout.getTabCount()-1;
                boolean newsSelected = false;
                boolean paperSelected = false;
                for(int i=0; i<num; i++){
                    if(Objects.equals(Objects.requireNonNull(mTabLayout.getTabAt(i)).getText(), "news"))
                        newsSelected = true;
                    if(Objects.equals(Objects.requireNonNull(mTabLayout.getTabAt(i)).getText(), "paper"))
                        paperSelected = true;
                }
                intent.putExtra("news",newsSelected);
                intent.putExtra("paper",paperSelected);
                startActivityForResult(intent,1);
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 1) {
            boolean newsSelected = data.getBooleanExtra("news", true);
            boolean paperSelected = data.getBooleanExtra("paper", true);
            if (newsSelected || paperSelected) {
                mTabLayout.removeAllTabs();
                if (newsSelected)
                    mTabLayout.addTab(mTabLayout.newTab().setText("news"));
                if (paperSelected)
                    mTabLayout.addTab(mTabLayout.newTab().setText("paper"));
                mTabLayout.addTab(mTabLayout.newTab().setText(""));
                LinearLayout tabStrip = (LinearLayout) mTabLayout.getChildAt(0);
                View tabView = tabStrip.getChildAt(mTabLayout.getTabCount() - 1);
                if (tabView != null) {
                    tabView.setClickable(false);
                }
                myNewsType = mTabLayout.getTabAt(0).getText().toString();
                mNewsList = new ArrayList<>();
                getNews();
            } else {
                Toast.makeText(getActivity(), "操作失败，必须保留一个频道", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.history) {
            Objects.requireNonNull(mTabLayout.getTabAt(mTabLayout.getTabCount() - 1)).select();
            mNewsList = Select.from(News.class)
                    .where(Condition.prop("content").notEq("")).list();
            adapter = new NewsAdapter(mNewsList);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    final RecyclerView recyclerView = (RecyclerView) swipeRefreshLayout.findViewById(R.id.news_title_view);
                    recyclerView.setAdapter(adapter);
                    adapter.setCanLoadMore(false);
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

            adapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
                @Override
                public void onClick(final int position) {
                    News news = mNewsList.get(position);
                    NewsContentActivity.actionStart(getActivity(), news.getTitle(), news.getTime(), news.getSource(), news.getContent());
                }
            });

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void getNews(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                adapter = new NewsAdapter(initLoad());
                new Handler(Looper.getMainLooper()).post(new Runnable(){
                    @Override
                    public void run() {
                        final RecyclerView recyclerView = (RecyclerView) swipeRefreshLayout.findViewById(R.id.news_title_view);
                        recyclerView.setAdapter(adapter);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

                adapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(final int position) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                News news = mNewsList.get(position);
                                if(!news.getIsRead()){
                                    news.setIsRead(true);
                                    news.save();
                                    new Handler(Looper.getMainLooper()).post(new Runnable(){
                                        @Override
                                        public void run() {
                                            adapter.setGrey(position);
                                        }
                                    });
                                }


                                NewsContentActivity.actionStart(getActivity(), news.getTitle(), news.getTime(), news.getSource(), news.getContent());

                            }
                        }).start();
                    }
                });

                adapter.setOnLoadMoreListener(new NewsAdapter.OnLoadMoreListener() {
                    @Override
                    public void onLoadMore(int currentPage) {
                        mCurrentPage = currentPage;
                        Log.e("this","current page:"+ mCurrentPage);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                initLoad();
                                new Handler(Looper.getMainLooper()).post(new Runnable(){
                                    @Override
                                    public void run() {
                                        adapter.setCanLoadMore(true);
                                        adapter.setData(mNewsList);
                                    }
                                });
                            }
                        }).start();
                    }
                });
            }
        }).start();
    }
    private List<News> initLoad(){
        List<News> newsList = new ArrayList<>();
        try{
            OkHttpClient client= new OkHttpClient();
            Request.Builder reqBuild = new Request.Builder();
            HttpUrl.Builder urlBuilder =HttpUrl.parse("https://covid-dashboard.aminer.cn/api/events/list")
                    .newBuilder();
            urlBuilder.addQueryParameter("page", String.valueOf(mCurrentPage));
            urlBuilder.addQueryParameter("size", String.valueOf(PER_PAGE));
            urlBuilder.addQueryParameter("type", myNewsType);
            reqBuild.url(urlBuilder.build());
            Request request = reqBuild.build();
            Log.d("this",request.toString());
            Response response = client.newCall(request).execute();
            String responseData = response.body().string();
            JSONObject outerJSON = new JSONObject(responseData);
            JSONArray jsonArray = outerJSON.getJSONArray("data");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("_id");
                String title = jsonObject.getString("title");
                String time = jsonObject.getString("time");
                String content = jsonObject.getString("content");
                String source = jsonObject.getString("source");
                if(content.equals("")) continue;
                List<News> oldNews = News.find(News.class,"news_id = ?",id);
                if(oldNews.size()!=0 && oldNews.get(0).getIsRead()){
                    newsList.add(oldNews.get(0));
                    Log.e("this","oldNews");
                }
                else{
                    News news = new News(id, title, content, time, source);
                    newsList.add(news);
                }
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        mNewsList.addAll(newsList);

        return mNewsList;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.actionbar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);

        final SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //文字输入完成，提交的回调
            @Override
            public boolean onQueryTextSubmit(final String queryText) {
                searchView.setIconified(true);
                Objects.requireNonNull(mTabLayout.getTabAt(mTabLayout.getTabCount() - 1)).select();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mNewsList = Select.from(News.class)
                                .where(Condition.prop("title").like("%" + queryText +"%")).list();
                        adapter = new NewsAdapter(mNewsList);
                        new Handler(Looper.getMainLooper()).post(new Runnable(){
                            @Override
                            public void run() {
                                final RecyclerView recyclerView = (RecyclerView) swipeRefreshLayout.findViewById(R.id.news_title_view);
                                recyclerView.setAdapter(adapter);
                                adapter.setCanLoadMore(false);
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });

                        adapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
                            @Override
                            public void onClick(final int position) {
                                final News news = mNewsList.get(position);

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        try{
                                            OkHttpClient client= new OkHttpClient();
                                            Request.Builder reqBuild = new Request.Builder();
                                            HttpUrl.Builder urlBuilder =HttpUrl.parse("https://covid-dashboard-api.aminer.cn/event/"+news.getNewsID())
                                                    .newBuilder();
                                            reqBuild.url(urlBuilder.build());
                                            Request request = reqBuild.build();
                                            Response response = client.newCall(request).execute();
                                            String responseData = response.body().string();
                                            JSONObject outerJSON = new JSONObject(responseData);
                                            JSONObject innerJSON = (JSONObject)outerJSON.get("data");
                                            String id = innerJSON.getString("_id");
                                            String title = innerJSON.getString("title");
                                            String time = innerJSON.getString("time");
                                            String content = innerJSON.getString("content");
                                            String source = "";
                                            if(innerJSON.has("source"))
                                                source = innerJSON.getString("source");
                                            News fullNews = new News(id, title, content, time, source);
                                            fullNews.setIsRead(true);
                                            fullNews.save();
                                            new Handler(Looper.getMainLooper()).post(new Runnable(){
                                                @Override
                                                public void run() {
                                                    adapter.setGrey(position);
                                                }
                                            });
                                            NewsContentActivity.actionStart(getActivity(), fullNews.getTitle(), fullNews.getTime(), fullNews.getSource(), fullNews.getContent());
                                        } catch (JSONException | IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();



                            }
                        });
                    }
                }).start();


                return true;
            }

            //输入文字发生改变
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        //点击搜索图标，搜索框展开时的回调
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }



}

class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private List<News> mNewsList;
    private Context mContext;
    private RecyclerOnScrollerListener mOnScrollListener;

    private static final int VIEW_TYPE_CONTENT = 0;
    private static final int VIEW_TYPE_FOOTER = 1;
    private boolean isCanLoadMore = true;
    private Animation rotateAnimation;

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView newsTitleText;
        TextView newsTimeText;
        TextView newsSourceText;
        public ViewHolder(View view) {
            super(view);
            newsTitleText = (TextView) view.findViewById(R.id.news_title);
            newsTimeText = (TextView) view.findViewById(R.id.news_time);
            newsSourceText = (TextView) view.findViewById(R.id.news_source);
        }

    }

    public NewsAdapter(List<News> newsList) {
        mNewsList = newsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        if (rotateAnimation == null) {
            rotateAnimation = AnimationUtils.loadAnimation(mContext, R.anim.loading);
            rotateAnimation.setInterpolator(new LinearInterpolator());
        }

        if (viewType == VIEW_TYPE_CONTENT) {
            return new ContentViewHolder(LayoutInflater.from(mContext).inflate(R.layout.news_item, parent, false));
        } else {
            return new FooterViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_footer, parent, false));
        }

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (holder.getItemViewType() == VIEW_TYPE_CONTENT) {
            News news = mNewsList.get(position);
            //这里必须强制转换
            //如果外层的判断条件改为if(holder instance ContentViewHolder)，这里输入holder后会自动转换
            String title,time,source;
            if(news.getSource().equals(""))
                source = "来源：未知";
            else source = "来源："+ news.getSource();
            if(news.getIsRead()){
                title = "<font color = \"#dcdcdc\">" + news.getTitle() + "</font>";
                time = "<font color = \"#dcdcdc\">" + "时间：" + news.getTime() + "</font>";
                source = "<font color = \"#dcdcdc\">" + source + "</font>";
            }else{
                title = "<font color = \"#000000\">" + news.getTitle() + "</font>";
                time = "<font color = \"#000000\">" + news.getTime() + "</font>";
                source = "<font color = \"#000000\">" + source + "</font>";
            }

            holder.newsTitleText.setText(Html.fromHtml(title,Html.FROM_HTML_MODE_LEGACY));
            holder.newsTimeText.setText(Html.fromHtml(time,Html.FROM_HTML_MODE_LEGACY));
            holder.newsSourceText.setText(Html.fromHtml(source,Html.FROM_HTML_MODE_LEGACY));
        } else {
            Log.d("mytest", "isCanLoadMore: " + isCanLoadMore);
            if (isCanLoadMore) {
                ((FooterViewHolder) holder).showLoading();
            } else {
                ((FooterViewHolder) holder).showTextOnly("无更多数据");
            }
        }
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
        return mNewsList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return VIEW_TYPE_FOOTER;
        }
        return VIEW_TYPE_CONTENT;
    }

    //ContentView
    class ContentViewHolder extends ViewHolder {
        TextView news_title = itemView.findViewById(R.id.news_title);
        TextView news_time = itemView.findViewById(R.id.news_time);
        TextView news_source = itemView.findViewById(R.id.news_source);
        public ContentViewHolder(View itemView) {
            super(itemView);
        }
    }

    //底部的FooterView
    class FooterViewHolder extends ViewHolder {

        ImageView ivLoading = itemView.findViewById(R.id.iv_loading);
        TextView tvLoading = itemView.findViewById(R.id.tv_loading);

        public FooterViewHolder(View itemView) {
            super(itemView);
        }

        void showTextOnly(String s) {
            Log.d("mytest", "showTextOnly: " + s);
            ivLoading.setVisibility(View.INVISIBLE);
            tvLoading.setText(s);
        }

        void showLoading() {
            Log.i("mytest", "show loading");
            ivLoading.setImageResource(R.mipmap.ic_launcher);
            tvLoading.setText("正在加载...");
            if (ivLoading != null) {
                ivLoading.startAnimation(rotateAnimation);
            }
        }

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mOnScrollListener = new RecyclerOnScrollerListener(recyclerView) {
            @Override
            public void onLoadMore(int currentPage) {
                Log.i("loadingtest", "currentPage: " + currentPage);
                mOnLoadMoreListener.onLoadMore(currentPage);
            }
        };
        recyclerView.addOnScrollListener(mOnScrollListener);
    }


    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (mOnScrollListener != null) {
            recyclerView.removeOnScrollListener(mOnScrollListener);
        }
        mOnScrollListener = null;
    }

    public void setData(List<News> list) {
        mNewsList = list;
        notifyDataSetChanged();

    }

    public void setGrey(int index){
        mNewsList.get(index).setIsRead(true);
        notifyDataSetChanged();
    }


    public void setCanLoadMore(boolean isCanLoadMore) {
        this.isCanLoadMore = isCanLoadMore;
        mOnScrollListener.setCanLoadMore(isCanLoadMore);
        mOnScrollListener.setLoading(false);
    }


    public interface OnLoadMoreListener {
        void onLoadMore(int currentPage);
    }

    private OnLoadMoreListener mOnLoadMoreListener;

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.mOnLoadMoreListener = listener;
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}