package com.andyrn.rubikfix;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Author: Andy
 * Date: 12/10/2015
 */
@SuppressLint("ViewConstructor")
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "CameraPreview";

    private final Camera camera;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.camera = camera;

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated()");

        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();

        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged()");

        if (holder.getSurface() == null) {
            // Preview surface does not exist.
            return;
        }

        try {
            camera.stopPreview();
        } catch (Exception e) {
            // Ignore: Tried to stop a non-existent preview.
        }

        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = parameters.getSupportedPictureSizes().get(4);
        parameters.setPictureSize(size.width, size.height);
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.set("jpeg-quality", 100);
        camera.setParameters(parameters);
        camera.setDisplayOrientation(90);

        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();

        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed()");
        // No-op
    }
}