package com.liu.fixcamera;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import static com.liu.fixcamera.MainActivity.screenHeight;
import static com.liu.fixcamera.MainActivity.screenWidth;


/**
 * Created by StormGuoson on 2016/11/18.
 */

class MyAdapter extends BaseAdapter {
    private Context c;
    private List<ImageView> list;
    private int a;
    static boolean change = true;
    private int _4_id = R.drawable.heart_01;
    private int _5_id = R.drawable.heart_5_01;
    private int _6_id = R.drawable.heart_6_01;

    static List<Integer> shapeLists = new ArrayList<>();

    private void setHeart_5() {
        for (int i = 0; i < 25; i++)
            shapeLists.add(_5_id++);
    }

    private void setHeart_6() {
        for (int i = 0; i < 36; i++) {
            shapeLists.add(_6_id++);
        }
    }

    private void setHeart_4() {
        for (int i = 0; i < 16; i++)
        shapeLists.add(_4_id++);
    }

    MyAdapter(Context context, List<ImageView> lists, int a) {
        c = context;
        list = lists;
        this.a = a;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ImageView getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        View view;
        if (convertView == null)
            view = LayoutInflater.from(c).inflate(R.layout.cell_photo, null);
        else
            view = convertView;
        ImageView imageView = (ImageView) view.findViewById(R.id.iv);
        float f = ((float) screenHeight / (float) screenWidth);
        float height = f * screenWidth / a;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        params.width = (screenWidth / a);
        params.height = (int) height;

        switch (a){
            case 5:
                setHeart_5();
                break;
            case 4:
                setHeart_4();
                break;
            case 6:
                setHeart_6();
                break;
        }
        if (change)
            imageView.setImageResource(shapeLists.get(position));
        return view;
    }


}
