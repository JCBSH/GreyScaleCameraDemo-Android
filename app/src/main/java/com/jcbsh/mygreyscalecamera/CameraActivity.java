package com.jcbsh.mygreyscalecamera;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by JCBSH on 23/02/2016.
 */
public class CameraActivity extends SingleFragmentActivity implements AbstractCameraFragment.FragmentCallback{

    protected static final String LIFE_TAG = "life_CameraActivity";
    protected static final String TAG = CameraActivity.class.getSimpleName();

    @Override
    protected Fragment createFragment() {
        return CameraFragment.getInstance();
    }

    private View mProgressContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressContainer = findViewById(R.id.progressContainer);
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


    @Override
    public void setPBarVisibility(boolean b) {

        if (b) {
            mProgressContainer.setVisibility(View.VISIBLE);
        } else {
            mProgressContainer.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public boolean isPBarVisibility() {
        if (mProgressContainer.getVisibility() == View.VISIBLE) return true;
        return false;
    }
}
