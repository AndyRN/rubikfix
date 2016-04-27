package com.andyrn.rubikfix;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;

/**
 * Created by Andy on 12/10/2015.
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Button capture = (Button) findViewById(R.id.button_capture);
        capture.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(v.getContext(), CameraActivity.class));
                    }
                }
        );

        Button loadFur = (Button) findViewById(R.id.button_load_fur);
        loadFur.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadImage("FUR.jpg");
                    }
                }
        );

        Button loadBdl = (Button) findViewById(R.id.button_load_bdl);
        loadBdl.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadImage("BDL.jpg");
                    }
                }
        );
    }

    private void loadImage(String filename) {
        try {
            String dir = getCacheDir().getAbsolutePath();
            FileInputStream fis = new FileInputStream(dir + "/" + filename);

            Bitmap bmp = BitmapFactory.decodeStream(fis);

            ImageView image = (ImageView) findViewById(R.id.loaded_image);
            image.setImageBitmap(bmp);

            fis.close();

            Log.d(TAG, "Image loaded!");

            image.setOnTouchListener(touchListener);

        } catch (java.io.IOException e) {
            Log.d(TAG, "Error reading file: " + e.getMessage());
        }
    }

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ImageView imageView = ((ImageView) v);
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            TextView touchCoordinates = (TextView) findViewById(R.id.touch_coordinates);
            TextView pixelColour = (TextView) findViewById(R.id.pixel_colour);
            View pixelColourPreview = findViewById(R.id.pixel_colour_preview);

            float scaleFactor = (float) bitmap.getHeight() / imageView.getHeight();

            float x = event.getX() * scaleFactor;
            float y = event.getY() * scaleFactor;

            if (x < 0 || y < 0 || x > bitmap.getWidth() || y > bitmap.getHeight()) {
                return false;
            }

            touchCoordinates.setText(String.format("Touch coordinates : x(%s), y(%s)", String.valueOf(x), String.valueOf(y)));

            int pixel = bitmap.getPixel((int) x, (int) y);

            int redValue = Color.red(pixel);
            int greenValue = Color.green(pixel);
            int blueValue = Color.blue(pixel);

            pixelColour.setText(String.format("Pixel's colour : r(%d), g(%d), b(%d)", redValue, greenValue, blueValue));

            pixelColourPreview.setBackgroundColor(pixel);

            return true;
        }
    };

//    private View.OnTouchListener touchListener = new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            ImageView imageView = ((ImageView) v);
//            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
//            TextView touchCoordinates = (TextView) findViewById(R.id.touch_coordinates);
//            TextView pixelColour = (TextView) findViewById(R.id.pixel_colour);
//            View pixelColourPreview = findViewById(R.id.pixel_colour_preview);
//
//            float scaleFactor = (float) bitmap.getHeight() / imageView.getHeight();
//
//            float x = event.getX() * scaleFactor;
//            float y = event.getY() * scaleFactor;
//
//            if (x < 0 || y < 0 || x > bitmap.getWidth() || y > bitmap.getHeight()) {
//                return false;
//            }
//
//            touchCoordinates.setText(String.format("Touch coordinates : x(%s), y(%s)", String.valueOf(x), String.valueOf(y)));
//
//            int[] pixels = new int[400];
//            int index = 0;
//            for (int row = (int) y - 10; row < (int) y + 10; row++) {
//                for (int column = (int) x - 10; column < (int) x + 10; column++) {
//                    pixels[index] = bitmap.getPixel(column, row);
//                    index++;
//                }
//            }
//
//            long totalPixel = 0;
//            for (int pixel : pixels) {
//                totalPixel += pixel;
//            }
//            int averagePixel = (int) (totalPixel / pixels.length);
//
//            int redValue = Color.red(averagePixel);
//            int greenValue = Color.green(averagePixel);
//            int blueValue = Color.blue(averagePixel);
//
//            pixelColour.setText(String.format("Pixel's colour : r(%d), g(%d), b(%d)", redValue, greenValue, blueValue));
//
//            pixelColourPreview.setBackgroundColor(averagePixel);
//
//            return true;
//        }
//    };
}