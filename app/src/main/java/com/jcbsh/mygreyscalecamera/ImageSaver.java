package com.jcbsh.mygreyscalecamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.util.Log;

import com.bignerdranch.android.eora3d.ScriptC_saturation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ImageSaver implements Runnable {

    public static final int CAPTURE_REFERENCE = 0;
    public static final int CAPTURE_BITMAP = 1;
    public static final int CAPTURE_FILE = 2;
    public static final int CAPTURE_GREY_SCALE_FILE = 3;


    private static final String TAG = ImageSaver.class.getSimpleName();
    private final Image mImage;
    private final Handler mHandler;
    private final CaptureRequest mCaptureRequest;
    private final int tag;
    private final File mImageFile;
    private ScriptC_saturation mScript;
    private RenderScript mRS;

    private Allocation mInAllocation;
    private Allocation mOutAllocations;
    private Bitmap mBitmap;


    public ImageSaver(Image image, Handler handler, CaptureRequest captureRequest, File imageFile,
                      ScriptC_saturation script, RenderScript RS) {

        mImage = image;
        mHandler = handler;
        mCaptureRequest = captureRequest;
        mImageFile = imageFile;
        tag = (int) captureRequest.getTag();
        mScript = script;
        mRS = RS;
    }

    @Override
    public void run() {


        ByteBuffer byteBuffer = mImage.getPlanes()[0].getBuffer();
        Log.d(TAG, "format: " + mImage.getFormat());


        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        mImage.close();
        if (tag == CAPTURE_FILE) {
            Log.d(TAG, " CAPTURE_FILE");
            FileOutputStream fileOutputStream = null;

            try {
                fileOutputStream = new FileOutputStream(mImageFile);
                fileOutputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                //mImage.close();
                Message bitmapMessage = mHandler.obtainMessage(AbstractCameraFragment.WHAT_FILE_SAVED);
                bitmapMessage.sendToTarget();

                if(fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }



        } else {
            //Log.d(TAG, "bytes length: " + bytes.length);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            mBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);


            long start = System.currentTimeMillis();
            mInAllocation = Allocation.createFromBitmap(mRS, mBitmap);

            mOutAllocations = Allocation.createFromBitmap(mRS, mBitmap);

            RenderScriptTask renderScriptTask = new RenderScriptTask();
            renderScriptTask.setTag(tag);
            renderScriptTask.execute(mInAllocation, mOutAllocations);
        }

        //mImage.close();


    }


    private static final String detail3DScanTag = "detail3DScan";
    private class RenderScriptTask extends AsyncTask<Allocation, Void, Void> {
        int tag;

        public void setTag(int tag) {
            this.tag = tag;
        }

        @Override
        protected Void doInBackground(Allocation... values) {
            long start = System.currentTimeMillis();


            Allocation inAllocation = values[0];
            Allocation outAllocations = values[1];
            mScript.forEach_saturation(inAllocation, outAllocations);

            /*
             * Copy to bitmap and invalidate image view
             */
            outAllocations.copyTo(mBitmap);
            long timeTaken =  System.currentTimeMillis() -  start;
            Log.d(detail3DScanTag, "time taken to progess one image: " + timeTaken + "ms");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (tag == CAPTURE_GREY_SCALE_FILE) {
                Log.d(TAG, " CAPTURE_GREY_SCALE_FILE");
                FileOutputStream fileOutputStream = null;

                try {
                    fileOutputStream = new FileOutputStream(mImageFile);
                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    //mImage.close();
                    Message bitmapMessage = mHandler.obtainMessage(AbstractCameraFragment.WHAT_FILE_SAVED);
                    bitmapMessage.sendToTarget();

                    if(fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }

        protected void onCancelled() {
        }
    }
}
