package com.liu.fixcamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by StormGuoson on 2017/2/15.
 */

public class AtyCamera extends AppCompatActivity implements SurfaceHolder.Callback, Camera.AutoFocusCallback, View.OnClickListener {
    String TAG = "tag";
    SurfaceView surfaceView;
    SurfaceHolder holder;
    Camera camera;
    Button btnOk, btnRemake;
    View ctrl;
    File image;
    Bitmap bitmap;
    float ratio, w, h;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_camera);
        ImageView imageView = (ImageView) findViewById(R.id.iv_water);
        imageView.setImageResource(MyAdapter.shapeLists.get(MainActivity.position));
        init();
    }

    void init() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        w = metrics.widthPixels;
        h = metrics.heightPixels;
        ratio = w / h;
        surfaceView = (SurfaceView) findViewById(R.id.surface);
        holder = surfaceView.getHolder();
        holder.addCallback(this);

        btnOk = (Button) findViewById(R.id.btn_ok);
        btnRemake = (Button) findViewById(R.id.btn_cancel);
        ctrl = findViewById(R.id.layout_ok);
        surfaceView.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        btnRemake.setOnClickListener(this);
    }

    void initParameter() {
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> list = parameters.getSupportedPictureSizes();
        for (Camera.Size size : list) {
            float r =(float) size.height /(float) size.width;
            if (size.width <= h)
                if (r == ratio) {
                    parameters.setPictureSize(size.width, size.height);
                    break;
                }
        }
        camera.setParameters(parameters);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            camera = Camera.open(1);
            initParameter();
            camera.setDisplayOrientation(90);
            camera.cancelAutoFocus();
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        camera.stopPreview();
        camera.release();
        camera = null;
        holder = null;
    }

    @Override
    public void onAutoFocus(boolean b, Camera camera) {

    }

    void takePic(byte[] bytes) {
        try {
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            image = File.createTempFile("image", null);
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(image));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.surface:
                surfaceView.setClickable(false);
                camera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] bytes, Camera camera) {
                        takePic(bytes);
                        camera.stopPreview();
                    }
                });
                new Thread() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessageDelayed(new Message().what = 1, 300);
                    }
                }.start();
                break;
            case R.id.btn_ok:
                try {
                    Intent intent = new Intent();
                    intent.putExtra("data", image.getAbsolutePath());
                    setResult(RESULT_OK, intent);
                    finish();
                } catch (NullPointerException ignored) {
                }
                break;
            case R.id.btn_cancel:
                surfaceView.setClickable(true);
                ctrl.setVisibility(View.GONE);
                camera.startPreview();
                break;
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    ctrl.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };
}
