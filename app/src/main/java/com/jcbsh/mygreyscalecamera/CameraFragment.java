package com.jcbsh.mygreyscalecamera;

import android.app.ActivityManager;
import android.app.Fragment;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Looper;
import android.renderscript.RenderScript;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by JCBSH on 23/02/2016.
 */
public class CameraFragment extends AbstractCameraFragment {

    public static Fragment getInstance() {
        Fragment fragment = new CameraFragment();
        return fragment;
    }

    private static final String LIFE_TAG = "life_CameraFragment";
    private static final String TAG = CameraFragment.class.getSimpleName();

    private static final int TARGET_RESOLUTION = 8000000;

    private Long mStartTime;
    private CaptureRequest mCaptureRequest;
    private int mCaptureType = ImageSaver.CAPTURE_GREY_SCALE_FILE;
    private RenderScript mRS;
    private ScriptC_saturation mScript;
    private final UiHandler mUiHandler = new UiHandler(Looper.getMainLooper());


////---------------------------------------------////
////---------------------------------------------////
////                LIFE CYCLE CODE              ////
////---------------------------------------------////
////---------------------------------------------////


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LIFE_TAG, "onCreate() ");
        super.onCreate(savedInstanceState);

        mRS = RenderScript.create(getActivity());
        mScript = new ScriptC_saturation(mRS);
        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(getActivity().ACTIVITY_SERVICE);
        Log.d(TAG, "memory: " +activityManager.getMemoryClass());
    }

    private TextureView mScanPreViewTextureView;
    private ImageButton mCaptureButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LIFE_TAG, "onCreateView() ");

        View v = inflater.inflate(R.layout.fragment_camera, container, false);
        mScanPreViewTextureView = (TextureView) v.findViewById(R.id.texture);
        mCaptureButton = (ImageButton) v.findViewById(R.id.image_capture);
        mCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentCallback.setPBarVisibility(true);
                takePhoto();
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mImageReader != null) {
            mImageReader.close();
            mImageReader = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void openBackgroundThread() {
        super.openBackgroundThread();
    }


////////////////////////////////////////////////////////////////////////////////
//////////////////////////////MEDIA RELATED CODES///////////////////////////////
//////////////////////////////MEDIA RELATED CODES///////////////////////////////
//////////////////////////////MEDIA RELATED CODES///////////////////////////////
////////////////////////////////////////////////////////////////////////////////
    private File mImageFile;
    private ImageReader mImageReader;
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener =
            new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    mImageSaverHandler.post(new ImageSaver(reader.acquireNextImage(), mUiHandler,
                            mCaptureRequest, mImageFile, mScript, mRS));
                }
            };


    @Override
    protected void captureStillImage() {

        if (mCameraDevice == null || mImageReader == null || mCameraCaptureSession == null) return;
        try {
            CaptureRequest.Builder captureStillBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureStillBuilder.addTarget(mImageReader.getSurface());

            captureStillBuilder.setTag(mCaptureType);

            int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
            captureStillBuilder.set(CaptureRequest.JPEG_ORIENTATION,
                    ORIENTATIONS_IMAGE.get(rotation));

            CameraCaptureSession.CaptureCallback captureCallback =
                    new CameraCaptureSession.CaptureCallback() {
                        @Override
                        public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
                            super.onCaptureStarted(session, request, timestamp, frameNumber);
                            mCaptureRequest = request;
                            if (mCaptureType == ImageSaver.CAPTURE_FILE || mCaptureType == ImageSaver.CAPTURE_GREY_SCALE_FILE) {
                                mImageFile = getImageFile();
                            }

                        }

                        @Override
                        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                            super.onCaptureCompleted(session, request, result);


                            /*
                            Toast.makeText(getApplicationContext(),
                                    "Image Captured!", Toast.LENGTH_SHORT).show();
                            */
                            unLockFocus();
                        }
                    };

            mCameraCaptureSession.stopRepeating();
            mCameraCaptureSession.capture(captureStillBuilder.build(), captureCallback, mHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Size CaptureSizeSetup(StreamConfigurationMap map) {
        Size largestImageSize = get8MegaPixel(map.getOutputSizes(ImageFormat.JPEG));
        mImageReader = ImageReader.newInstance(largestImageSize.getWidth(),
                largestImageSize.getHeight(),
                ImageFormat.JPEG,
                1);
        mImageReader.setOnImageAvailableListener(mOnImageAvailableListener,
                mImageSaverHandler);
        return largestImageSize;
    }

    private Size get8MegaPixel(Size[] outputSizes) {
        int highest = 0;
        Size result =  new Size(0,0);
        for (Size s:outputSizes) {
            int numOfPixels = s.getWidth()*s.getHeight();
            if (numOfPixels > highest) {
                if (numOfPixels <  TARGET_RESOLUTION) {
                    highest = numOfPixels;
                    result = s;
                }
            }
        }
        return result;
    }


    public void takePhoto() {
        lockFocus();
    }

////////////////////////////////////////////////////////////////////////////////
////////////////////////SIMPLE ABSTRACT IMPLEMENTATION//////////////////////////
////////////////////////SIMPLE ABSTRACT IMPLEMENTATION//////////////////////////
////////////////////////SIMPLE ABSTRACT IMPLEMENTATION//////////////////////////
////////////////////////////////////////////////////////////////////////////////


    @Override
    protected  Size[] getAllCaptureTypeSizes(StreamConfigurationMap map) {
        return map.getOutputSizes(ImageFormat.JPEG);
    }

    @Override
    protected List<Surface> getCameraPreviewSurfaces() {
        return Arrays.asList(mImageReader.getSurface());
    }

    @Override
    protected String getFragmentTag() {
        return TAG;
    }
    @Override
    protected String getFragmentLifeTag() {
        return LIFE_TAG;
    }

    @Override
    protected TextureView getPreViewTextureView() {
        return mScanPreViewTextureView;
    }

}
