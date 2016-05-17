package com.jcbsh.mygreyscalecamera;

import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.IOException;

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


        try {
            ExifInterface exifInterface = new ExifInterface(mPath);
            Log.d("ImageSaver", "" +  exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL));

            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                b = PictureUtils.rotateDrawable(this, b, 180);
            } else {
                switch (orientation) {
                    case 0:
                        b = PictureUtils.rotateDrawable(this, b, 90);
                        break;
                    case 1:
                        break;
                    case 2:
                        b = PictureUtils.rotateDrawable(this, b, 270);
                        break;
                    case 3:
                        b = PictureUtils.rotateDrawable(this, b, 180);
                        break;
                }
            }




        } catch (IOException e) {
            e.printStackTrace();
            Log.d("ImageSaver", "exifInterface IOException");
        }


        mImageView.setImageDrawable(b);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mImageView.setImageBitmap(null);

    }
}
