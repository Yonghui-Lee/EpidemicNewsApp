package com.java.liyonghui.ui.news;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.java.liyonghui.R;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.common.UiError;
import com.sina.weibo.sdk.openapi.IWBAPI;
import com.sina.weibo.sdk.openapi.WBAPIFactory;
import com.sina.weibo.sdk.share.WbShareCallback;
//import com.sina.weibo.sdk.openapi.IWBAPI;
//import com.sina.weibo.sdk.openapi.WBAPIFactory;


public class NewsContentActivity extends AppCompatActivity implements WbShareCallback{
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
    private String mNewsTitle;
    private String mNewsContent;
    public static void actionStart(Context context, String newsTitle, String newsTime, String newsSource, String newsContent) {
        Intent intent = new Intent(context, NewsContentActivity.class);
        intent.putExtra("news_title", newsTitle);
        intent.putExtra("news_time", newsTime);
        intent.putExtra("news_source", newsSource);
        intent.putExtra("news_content", newsContent);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_content);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        String newsTitle = getIntent().getStringExtra("news_title"); // 获取传入的新闻标题
        String newsTime = "时间：" + getIntent().getStringExtra("news_time"); // 获取传入的新闻标题
        String newsSource = getIntent().getStringExtra("news_source"); // 获取传入的新闻内容
        mNewsContent = getIntent().getStringExtra("news_content"); // 获取传入的新闻内容
        if(newsSource.equals(""))
            newsSource = "来源：未知";
        else newsSource = "来源："+ newsSource;
        mNewsTitle = newsTitle;

        TextView newsTitleText = (TextView) findViewById(R.id.news_title);
        TextView newsTimeText = (TextView) findViewById(R.id.news_time);
        TextView newsSourceText = (TextView) findViewById(R.id.news_source);
        TextView newsContentText = (TextView) findViewById(R.id.news_content);
        newsTitleText.setText(newsTitle); // 刷新新闻的标题
        newsTimeText.setText(newsTime);
        newsSourceText.setText(newsSource);
        newsContentText.setText(mNewsContent); // 刷新新闻的内容

        AuthInfo authInfo = new AuthInfo(this, APP_KY, REDIRECT_URL, SCOPE);
        mWBAPI = WBAPIFactory.createWBAPI(this);
        mWBAPI.registerApp(this, authInfo);
        mWBAPI.setLoggerEnable(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.weibo:
                doWeiboShare();
                return true;
//            case R.id.weixin:
//                Toast.makeText(this, "Weixin", Toast.LENGTH_SHORT).show();
//                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.news_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mWBAPI.doResultIntent(data, this);
    }

    private void doWeiboShare() {
        WeiboMultiMessage message = new WeiboMultiMessage();
        Log.e("this",mNewsContent);
        String[] sArr1 = mNewsContent.split(".");
        String[] sArr2 = mNewsContent.split("。");
        TextObject textObject = new TextObject();
        if(sArr1.length>0){
            textObject.text = mNewsTitle + "." + sArr1[0];
        }else if(sArr2.length>0){
            textObject.text = mNewsTitle + "。" + sArr2[0];
        }else{
            textObject.text = mNewsTitle;
        }




        message.textObject = textObject;

        mWBAPI.shareMessage(message, true);
    }

    @Override
    public void onComplete() {
        Toast.makeText(NewsContentActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(UiError error) {
        Toast.makeText(NewsContentActivity.this, "分享失败:" + error.errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancel() {
        Toast.makeText(NewsContentActivity.this, "分享取消", Toast.LENGTH_SHORT).show();
    }

}
