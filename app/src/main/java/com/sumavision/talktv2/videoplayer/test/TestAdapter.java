package com.sumavision.talktv2.videoplayer.test;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.talktv2.R;

import java.util.List;

public class TestAdapter extends BaseAdapter {

    private Context context;
    List<DownloadInfo> list;

    public  TestAdapter(Context context, List<DownloadInfo> results) {
        this.list = results;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.test_listview_layout, null);
            TextView textView = (TextView) convertView.findViewById(R.id.textview);
            viewHolder = new ViewHolder();
            viewHolder.textView = textView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText("hahah");
        return convertView;
    }

    public class ViewHolder {
        public TextView textView;
    }
}
