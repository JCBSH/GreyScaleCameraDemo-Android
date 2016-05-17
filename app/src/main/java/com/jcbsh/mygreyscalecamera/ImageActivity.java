package com.jcbsh.mygreyscalecamera;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

/**
 * Created by JCBSH on 17/05/2016.
 */
public class ImageActivity extends AppCompatActivity {
    public static final String EXTRA_VIDEO_FILE_PATH = "com.jcbsh.mygreyscalecamera.ImageActivity_video_file_path";

    private ImageView mImageView;
    private String mPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mImageView = (ImageView) findViewById(R.id.grey_scale_imageView);
        mPath = getIntent().getStringExtra(EXTRA_VIDEO_FILE_PATH);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        BitmapDrawable b = PictureUtils.getScaledDrawable(this, mPath);
        mImageView.setImageDrawable(b);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mImageView.setImageBitmap(null);

    }
}
