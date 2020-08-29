package com.java.liyonghui.ui.news;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.java.liyonghui.News;
import com.java.liyonghui.R;
import com.java.liyonghui.RecyclerOnScrollerListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class NewsFragment extends Fragment{
    private List<News> mNewsList;
    private int mCurrentPage = 1;
    private NewsAdapter adapter;
    private NewsViewModel newsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        newsViewModel =
                ViewModelProviders.of(this).get(NewsViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_news, container, false);


        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //这里获取数据的逻辑
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });
        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.news_title_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        new Thread(new Runnable() {
            @Override
            public void run() {
                adapter = new NewsAdapter(initLoad());
                new Handler(Looper.getMainLooper()).post(new Runnable(){
                    @Override
                    public void run() {
                        recyclerView.setAdapter(adapter);
                        adapter.setOnLoadMoreListener(new NewsAdapter.OnLoadMoreListener() {
                            @Override
                            public void onLoadMore(int currentPage) {
                                mCurrentPage = currentPage;
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new Handler(Looper.getMainLooper()).post(new Runnable(){
                                            @Override
                                            public void run() {
                                                loadMoreTest();
                                            }
                                        });
                                    }
                                }).start();
                            }
                        });
                    }
                });
            }
        }).start();

        return root;
    }
    private List<News> initLoad(){
        List<News> newsList = new ArrayList<>();
        try{
            OkHttpClient client= new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://covid-dashboard.aminer.cn/api/dist/events.json")
                    .build();
            Response response = client.newCall(request).execute();
            String responseData = response.body().string();
            JSONObject outerJSON = new JSONObject(responseData);
            JSONArray jsonArray = outerJSON.getJSONArray("datas");
            for(int i = 0; i < 10; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("_id");
                String title = jsonObject.getString("title");
                String time = jsonObject.getString("time");
                News news = new News();
                news.setTitle(title);
                news.setTime(time);
                newsList.add(news);
                Log.d("this","id is "+id);
                Log.d("this","time is "+time);
                Log.d("this","title is "+title);
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        mNewsList = newsList;
        return newsList;

    }


    private void loadMoreTest() {
        List<News> newsList = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            News news = new News();
            news.setTitle("This is news title " + i);
            news.setContent(getRandomLengthContent("This is news content " + i + ". "));
            newsList.add(news);
        }
        mNewsList.addAll(newsList);
//        if (mNewsList.size() == mCurrentPage * PER_PAGE) {
//            adapter.setCanLoadMore(true);
//        } else {
//            adapter.setCanLoadMore(false);
//        }
        adapter.setCanLoadMore(true);
        adapter.setData(mNewsList);
    }

    private List<News> getNews() {
        List<News> newsList = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            News news = new News();
            news.setTitle("This is news title " + i);
            news.setContent(getRandomLengthContent("This is news content " + i + ". "));
            newsList.add(news);
        }
        mNewsList = newsList;
        return newsList;
    }

    private String getRandomLengthContent(String content) {
        Random random = new Random();
        int length = random.nextInt(20) + 1;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(content);
        }
        return builder.toString();
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


//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
//            final ViewHolder holder = new ViewHolder(view);
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    News news = mNewsList.get(holder.getAdapterPosition());
////                    NewsContentActivity.actionStart(getActivity(), news.getTitle(), news.getContent());
//                }
//            });
//            return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_CONTENT) {
            News news = mNewsList.get(position);
            //这里必须强制转换
            //如果外层的判断条件改为if(holder instance ContentViewHolder)，这里输入holder后会自动转换
            holder.newsTitleText.setText(news.getTitle());
            holder.newsTimeText.setText(news.getTime());
        } else {
            Log.d("mytest", "isCanLoadMore: " + isCanLoadMore);
            if (isCanLoadMore) {
                ((FooterViewHolder) holder).showLoading();
            } else {
                ((FooterViewHolder) holder).showTextOnly("无更多数据");
            }
        }


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
        if (mNewsList.size() < PER_PAGE) {
            setCanLoadMore(false);
        } else {
            setCanLoadMore(true);
        }
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

}