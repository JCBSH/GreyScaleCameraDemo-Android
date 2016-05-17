package com.jcbsh.mygreyscalecamera;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * Created by JCBSH on 23/02/2016.
 */
public class CameraActivity extends SingleFragmentActivity {

    protected static final String LIFE_TAG = "life_CameraActivity";
    protected static final String TAG = CameraActivity.class.getSimpleName();

    @Override
    protected Fragment createFragment() {
        return CameraFragment.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
    protected int getLayoutResId() {
        return R.layout.activity_camera;
    }
}
