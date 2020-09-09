package com.java.liyonghui.ui.scholar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.java.liyonghui.R;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
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
        intent.putExtra("ispassedaway",scholar.getIs_passedaway());
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
        final boolean ispassedaway = getIntent().getBooleanExtra("ispassedaway",false);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(scholarName);

        final ImageView scholarImageView = findViewById(R.id.scholar_detail_image);
        TextView scholarNameText = findViewById(R.id.scholar_detail_name);
        TextView scholarPositionText = findViewById(R.id.scholar_detail_position);
        TextView scholarAffiliationText = findViewById(R.id.scholar_detail_affiliation);
        TextView scholarWorkText = findViewById(R.id.scholar_detail_work);
        TextView scholarEduText = findViewById(R.id.scholar_detail_edu);
        TextView scholarBioText = findViewById(R.id.scholar_detail_bio);


        if(scholarPosition.equals(""))
            scholarPosition = "Unknown";
        if(scholarAffiliation.equals(""))
            scholarAffiliation = "Unknown";
        if(scholarWork.equals(""))
            scholarWork = "Unknown";
        if(scholarEdu.equals(""))
            scholarEdu = "Unknown";
        if(scholarBio.equals(""))
            scholarBio = "Unknown";

        scholarImageView.setImageResource(R.drawable.lack_photo);
        scholarNameText.setText(Html.fromHtml(("<font color = \"#000000\"><b>Name:</b></font>" + scholarName),Html.FROM_HTML_MODE_LEGACY));
        scholarPositionText.setText(Html.fromHtml(("<font color = \"#000000\"><b>Position:</b></font>" + scholarPosition),Html.FROM_HTML_MODE_LEGACY));
        scholarAffiliationText.setText(Html.fromHtml(("<font color = \"#000000\"><b>Affiliation:</b></font>" + scholarAffiliation),Html.FROM_HTML_MODE_LEGACY));
        scholarWorkText.setText(Html.fromHtml(("<font color = \"#000000\"><b>Work:</b></font>" + scholarWork),Html.FROM_HTML_MODE_LEGACY));
        scholarEduText.setText(Html.fromHtml(("<font color = \"#000000\"><b>Edu:</b></font>" + scholarEdu),Html.FROM_HTML_MODE_LEGACY));
        scholarBioText.setText(Html.fromHtml(("<font color = \"#000000\"><b>Bio:</b></font>"+scholarBio),Html.FROM_HTML_MODE_LEGACY));

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
                    final Bitmap finalImage = ispassedaway?greyBitmap(image):image;
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
    public static Bitmap greyBitmap(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap greyBitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(greyBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0.1f);
        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorMatrixFilter);
        canvas.drawBitmap(bitmap,0,0,paint);
        return greyBitmap;
    }
}
