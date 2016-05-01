package com.andyrn.rubikfix;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileOutputStream;

/**
 * Author: Andy
 * Date: 12/10/2015
 */
public class CameraActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "CameraActivity";
    private final Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            Log.i(TAG, "onAutoFocus()");

            if (success) {
                camera.takePicture(null, null, pictureCallback);
            }
        }
    };
    private Camera camera;
    private boolean firstImageTaken;
    private final Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            Log.i(TAG, "onPictureTaken()");

            final boolean secondImage = firstImageTaken;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String filename = "FUR.jpg";
                    if (secondImage) {
                        filename = "BDL.jpg";
                    }

                    saveImage(data, filename);
                }
            }).start();

            if (!secondImage) {
                firstImageTaken = true;

                camera.startPreview();

                ImageView overlay = (ImageView) findViewById(R.id.camera_overlay);
                overlay.setImageResource(R.drawable.overlay_2);

                Toast.makeText(getApplicationContext(), "Scan BDL", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Processing...", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        }
    };

    private static Camera getCameraInstance() {
        Camera c = null;

        try {
            c = Camera.open();

        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }

        return c;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
        setContentView(R.layout.activity_camera);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        camera = getCameraInstance();

        CameraPreview cameraPreview = new CameraPreview(this, camera);
        cameraPreview.setOnClickListener(this);

        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(cameraPreview);

        ImageView overlay = (ImageView) findViewById(R.id.camera_overlay);
        overlay.setImageResource(R.drawable.overlay_1);

        Toast.makeText(getApplicationContext(), "Scan FUR", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()");

        releaseCamera();
    }

    @Override
    public void onClick(View v) {
        camera.autoFocus(autoFocusCallback);
    }

    private void saveImage(byte[] data, String filename) {
        try {
            String dir = getCacheDir().getAbsolutePath();
            FileOutputStream fos = new FileOutputStream(dir + "/" + filename);

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap mutableBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, null).copy(Bitmap.Config.RGB_565, true);
            Bitmap bmp = Bitmap.createBitmap(mutableBitmap, 0, 0, mutableBitmap.getWidth(), mutableBitmap.getHeight(), matrix, true);

            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.flush();
            fos.close();

            Log.d(TAG, "Image saved!");

        } catch (java.io.IOException e) {
            Log.d(TAG, "Error writing file: " + e.getMessage());
        }
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;

            Log.i(TAG, "Camera released!");
        }
    }
}