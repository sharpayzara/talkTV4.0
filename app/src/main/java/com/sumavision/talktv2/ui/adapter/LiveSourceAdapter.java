package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;

/**
 * Created by zjx on 2016/6/22.
 */
public class LiveSourceAdapter extends BaseAdapter {

    private Context context;
    private int count;
    private String[] nums = new String[]{"源1", "源2", "源3", "源4", "源5"};
    private int[] picIds = new int[]{R.mipmap.yuan1, R.mipmap.yuan2, R.mipmap.yuan3, R.mipmap.yuan4, R.mipmap.yuan5};
    public LiveSourceAdapter (Context context, int count) {
        this.count = count;
        this.context = context;
    }
    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int position) {
        return picIds[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = View.inflate(context, R.layout.item_live_source, null);
        TextView num = (TextView) convertView.findViewById(R.id.sourcenum);
        ImageView back = (ImageView) convertView.findViewById(R.id.background);
        ImageView pic = (ImageView) convertView.findViewById(R.id.sign);

        num.setText(nums[position]);
        pic.setImageResource(picIds[position]);

        if(selector == position) {
            back.setVisibility(View.VISIBLE);
        }
        else {
            back.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    private int selector;
    public void setSelector (int selector) {
        this.selector = selector;
    }
}
