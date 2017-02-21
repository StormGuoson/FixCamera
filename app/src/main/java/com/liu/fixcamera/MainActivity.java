package com.liu.fixcamera;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity implements View.OnClickListener {
    MyAdapter adapter;
    GridView gridView;
    List<ImageView> lists;                          //照片容器
    ImageView currentView;                          //当前照片
    LinearLayout layout;                            //改变网格布局界面
    Button btnChange;                               //改变布局按钮
    SimpleDateFormat format;                        //格式化文件名
    static int position = 0;                        //点击的图片位置
    public static int screenWidth;
    public static int screenHeight;
    public static int viewWidth;
    public static int viewHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        changeGridNum(4);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                layout.setVisibility(View.GONE);
                MainActivity.position = position;
                currentView = (ImageView) view.findViewById(R.id.iv);
                viewWidth = currentView.getWidth();
                viewHeight = currentView.getHeight();
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 21);
                else {
                    Intent intent = new Intent(MainActivity.this, AtyCamera.class);
                    startActivityForResult(intent, 2);
                }
            }
        });
    }

    void init() {
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        screenHeight = getWindowManager().getDefaultDisplay().getHeight();

        layout = (LinearLayout) findViewById(R.id.changeLayout);
        btnChange = (Button) findViewById(R.id.btnChange);

        btnChange.setOnClickListener(MainActivity.this);
        findViewById(R.id.btnShape).setOnClickListener(MainActivity.this);
        findViewById(R.id.btnSave).setOnClickListener(MainActivity.this);

        layout.setOnClickListener(MainActivity.this);
        findViewById(R.id.btn4).setOnClickListener(MainActivity.this);
        findViewById(R.id.btn5).setOnClickListener(MainActivity.this);
        findViewById(R.id.btn6).setOnClickListener(MainActivity.this);
    }

    void changeGridNum(int n) {
        MyAdapter.change = true;
        lists = new ArrayList<>();
        gridView = (GridView) findViewById(R.id.gvMain);
        for (int i = 0; i < n * n; i++) lists.add(new ImageView(MainActivity.this));
        gridView.setNumColumns(n);
        adapter = new MyAdapter(MainActivity.this, lists, n);
        gridView.setAdapter(adapter);
        MyAdapter.shapeLists.clear();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2:
                if (resultCode == RESULT_OK) {
                    MyAdapter.change = false;
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    matrix.postScale(1, -1);
                    String file = data.getStringExtra("data");
                    Bitmap bitmap1 = BitmapFactory.decodeFile(file);
                    Bitmap bitmap2 = Bitmap.createBitmap(bitmap1, 0, 0, bitmap1.getWidth(), bitmap1.getHeight(), matrix, true);
                    Bitmap resizeBmp = ThumbnailUtils.extractThumbnail(bitmap2, currentView.getWidth(), currentView.getHeight());
                    currentView.setImageBitmap(resizeBmp);
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        layout.setVisibility(View.GONE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("欸呦喂");
        builder.setMessage("走了啊？");
        builder.setPositiveButton("是啊", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("再拍会儿", null);
        builder.show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnChange:
                if (layout.getVisibility() == View.GONE)
                    layout.setVisibility(View.VISIBLE);
                else layout.setVisibility(View.GONE);
                break;
            case R.id.btn4:
                changeGridNum(4);
                layout.setVisibility(View.GONE);
                break;
            case R.id.btn5:
                changeGridNum(5);
                layout.setVisibility(View.GONE);
                break;
            case R.id.btn6:
                changeGridNum(6);
                layout.setVisibility(View.GONE);
                break;
            case R.id.btnShape:
                layout.setVisibility(View.GONE);
                break;
            case R.id.btnSave:
                layout.setVisibility(View.GONE);
                if (format == null)
                    format = new SimpleDateFormat("yy-MM-dd HH:mm:ss", Locale.CHINA);
                String date = format.format(System.currentTimeMillis());
                gridView.setDrawingCacheEnabled(true);
                gridView.invalidate();
                gridView.buildDrawingCache();
                Bitmap bitmap = gridView.getDrawingCache();
                try {
                    File dir = new File(Environment.getExternalStorageDirectory(), "DCIM/pic");
                    if (!dir.exists()) dir.mkdirs();
                    File mFile = new File(dir, date + ".jpeg");
                    mFile.createNewFile();
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(mFile));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    bos.flush();
                    bos.close();
                    Toast.makeText(MainActivity.this, "已保存至/DCIM/pic目录！", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }

                break;
        }
    }

}