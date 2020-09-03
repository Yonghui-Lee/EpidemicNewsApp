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
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.tabs.TabLayout;
import com.java.liyonghui.InvertedIndex;
import com.java.liyonghui.MainActivity;
import com.java.liyonghui.News;
import com.java.liyonghui.NewsContentActivity;
import com.java.liyonghui.R;
import com.java.liyonghui.RecyclerOnScrollerListener;
import com.java.liyonghui.channel.ChannelActivity;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class NewsFragment extends Fragment{
    private List<News> mNewsList;
    private int mCurrentPage = 1;
    private static final int PER_PAGE = 30;
    private NewsAdapter adapter;
    private NewsViewModel newsViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String myNewsType;
    private TabLayout mTabLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        newsViewModel =
                ViewModelProviders.of(this).get(NewsViewModel.class);
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
                    mNewsList = new ArrayList<News>();
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
                    myNewsType = "news";
                    mNewsList = new ArrayList<>();
                    getNews();
                }
                if (tab.getText().equals("paper")) {
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
        switch (requestCode){
            case 1:
                boolean newsSelected = data.getBooleanExtra("news",true);
                boolean paperSelected = data.getBooleanExtra("paper",true);
                if(newsSelected || paperSelected)
                {
                    mTabLayout.removeAllTabs();
                    if(newsSelected)
                        mTabLayout.addTab(mTabLayout.newTab().setText("news"));
                    if(paperSelected)
                        mTabLayout.addTab(mTabLayout.newTab().setText("paper"));
                    mTabLayout.addTab(mTabLayout.newTab().setText(""));
                    LinearLayout tabStrip = (LinearLayout) mTabLayout.getChildAt(0);
                    View tabView = tabStrip.getChildAt(mTabLayout.getTabCount()-1);
                    if (tabView != null) {
                        tabView.setClickable(false);
                    }
                    myNewsType = mTabLayout.getTabAt(0).getText().toString();
                    mNewsList = new ArrayList<>();
                    getNews();
                }else{
                    Toast.makeText(getActivity(), "操作失败，必须保留一个频道", Toast.LENGTH_SHORT).show();
                }
            default:
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.history:
                Objects.requireNonNull(mTabLayout.getTabAt(mTabLayout.getTabCount() - 1)).select();
                mNewsList = News.listAll(News.class);
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
                        News news = mNewsList.get(position);
                        NewsContentActivity.actionStart(getActivity(), news.getTitle(), news.getTime(), news.getSource(), news.getContent());
                    }
                });

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
//                                    OkHttpClient client= new OkHttpClient();
//                                    Request.Builder reqBuild = new Request.Builder();
//                                    HttpUrl.Builder urlBuilder =HttpUrl.parse("https://covid-dashboard-api.aminer.cn/event/"+news.getNewsID())
//                                            .newBuilder();
//                                    reqBuild.url(urlBuilder.build());
//                                    Request request = reqBuild.build();
//                                    Response response = client.newCall(request).execute();
//                                    String responseData = response.body().string();
//                                    JSONObject outerJSON = new JSONObject(responseData);
//                                    JSONObject innerJSON = (JSONObject)outerJSON.get("data");
//                                    String content = innerJSON.getString("content");
//                                    Log.d("text",request.toString());
//                                    Log.d("text",responseData);
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
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                initLoad();
                                new Handler(Looper.getMainLooper()).post(new Runnable(){
                                    @Override
                                    public void run() {
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
//                if(content.equals("")) continue;
                News news = new News(id, title, content, time, source);
                newsList.add(news);
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        mNewsList.addAll(newsList);
//        if(adapter!=null){
//            if (mNewsList.size() == mCurrentPage * PER_PAGE) {
//                adapter.setCanLoadMore(true);
//            } else {
//                adapter.setCanLoadMore(false);
//            }
//        }

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
                Toast.makeText(getActivity(), "onQueryTextSubmit = " + queryText, Toast.LENGTH_SHORT).show();
                searchView.setIconified(true);
                Objects.requireNonNull(mTabLayout.getTabAt(mTabLayout.getTabCount() - 1)).select();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<News> newsList = new ArrayList<>();
                        List<InvertedIndex> li = Select.from(InvertedIndex.class)
                                .where(Condition.prop("myword").eq(queryText)).list();
                        Log.d("this",String.valueOf(li.size()));

                        HashSet<String> set = new HashSet<>();
                        for(InvertedIndex in :li){
                            String s = in.getIndex();
                            set.add(s);
                        }
                        int i = 0;
                        for (String str : set) {
                            try{
                                if(i++ > 15) break;
                                OkHttpClient client= new OkHttpClient();
                                Request.Builder reqBuild = new Request.Builder();
                                HttpUrl.Builder urlBuilder =HttpUrl.parse("https://covid-dashboard-api.aminer.cn/event/"+str)
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
                                String source = innerJSON.getString("source");
                                News news = new News(id, title, content, time, source);
                                newsList.add(news);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }


                        mNewsList = newsList;
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
                                News news = mNewsList.get(position);
                                NewsContentActivity.actionStart(getActivity(), news.getTitle(), news.getTime(), news.getSource(), news.getContent());
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

    //    private RecyclerView.AdapterDataObserver mAdapterDataObserver;

    private static final int VIEW_TYPE_CONTENT = 0;
    private static final int VIEW_TYPE_FOOTER = 1;
    private boolean isCanLoadMore = true;
    private Animation rotateAnimation;

    private static final int PER_PAGE = 10;

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView newsTitleText;
        TextView newsTimeText;
        public ViewHolder(View view) {
            super(view);
            newsTitleText = (TextView) view.findViewById(R.id.news_title);
            newsTimeText = (TextView) view.findViewById(R.id.news_time);

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
            String title,time;
            if(news.getIsRead()){
                title = "<font color = \"#dcdcdc\">" + news.getTitle() + "</font>";
                time = "<font color = \"#dcdcdc\">" + news.getTime() + "</font>";
            }else{
                title = "<font color = \"#000000\">" + news.getTitle() + "</font>";
                time = "<font color = \"#000000\">" + news.getTime() + "</font>";
            }

            holder.newsTitleText.setText(Html.fromHtml(title,Html.FROM_HTML_MODE_LEGACY));
            holder.newsTimeText.setText(Html.fromHtml(time,Html.FROM_HTML_MODE_LEGACY));
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

    //ContentView，水果们
    class ContentViewHolder extends ViewHolder {
        TextView news_title = itemView.findViewById(R.id.news_title);
        TextView news_time = itemView.findViewById(R.id.news_time);
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
//        mAdapterDataObserver = new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onChanged() {
//                super.onChanged();
//            }
//        };
        //初始化的时候如果未填满一页，那么肯定就没有更多数据了
//        if (mNewsList.size() < PER_PAGE) {
//            setCanLoadMore(false);
//        } else {
//            setCanLoadMore(true);
//        }
    }


    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (mOnScrollListener != null) {
            recyclerView.removeOnScrollListener(mOnScrollListener);
        }
//        if (mAdapterDataObserver != null) {
//            unregisterAdapterDataObserver(mAdapterDataObserver);
//        }
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


    /*
     * 数据加载完毕时执行setCanLoadMore()，此时isLoading都置为false
     * */
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