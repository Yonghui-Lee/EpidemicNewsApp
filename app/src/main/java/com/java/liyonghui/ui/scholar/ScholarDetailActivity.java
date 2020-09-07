package com.java.liyonghui.ui.scholar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.java.liyonghui.NewsContentActivity;
import com.java.liyonghui.R;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.openapi.WBAPIFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ScholarDetailActivity extends AppCompatActivity {
    public static void actionStart(Context context, Scholar scholar) {
        Intent intent = new Intent(context, ScholarDetailActivity.class);
        intent.putExtra("imageUrl",scholar.getImageUrl());
        intent.putExtra("name", scholar.getName());
        intent.putExtra("position", scholar.getPosition());
        intent.putExtra("affiliation", scholar.getAffiliation());
        intent.putExtra("work", scholar.getWork());
        intent.putExtra("edu", scholar.getEdu());
        intent.putExtra("bio", scholar.getBio());
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scholar_detail);

        final String imageUrl = getIntent().getStringExtra("imageUrl");
        String scholarName = getIntent().getStringExtra("name");
        String scholarPosition = getIntent().getStringExtra("position");
        String scholarAffiliation = getIntent().getStringExtra("affiliation");
        String scholarWork = getIntent().getStringExtra("work");
        String scholarEdu = getIntent().getStringExtra("edu");
        String scholarBio = getIntent().getStringExtra("bio");

        final ImageView scholarImageView = findViewById(R.id.scholar_detail_image);
        TextView scholarNameText = findViewById(R.id.scholar_detail_name);
        TextView scholarPositionText = findViewById(R.id.scholar_detail_position);
        TextView scholarAffiliationText = findViewById(R.id.scholar_detail_affiliation);
        TextView scholarWorkText = findViewById(R.id.scholar_detail_work);
        TextView scholarEduText = findViewById(R.id.scholar_detail_edu);
        TextView scholarBioText = findViewById(R.id.scholar_detail_bio);

        scholarImageView.setImageResource(R.drawable.lack_photo);
        scholarNameText.setText(scholarName);
        scholarPositionText.setText(scholarPosition);
        scholarAffiliationText.setText(scholarAffiliation);
        scholarWorkText.setText(scholarWork);
        scholarEduText.setText(scholarEdu);
        scholarBioText.setText(scholarBio);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Bitmap image = null;
                    if (!imageUrl.equals("null")) {
                        Request imgRequest = new Request.Builder().url(imageUrl).build();
                        ResponseBody body = client.newCall(imgRequest).execute().body();
                        InputStream in = body.byteStream();
                        image = BitmapFactory.decodeStream(in);
                    }
                    final Bitmap finalImage = image;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            scholarImageView.setImageBitmap(finalImage);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
