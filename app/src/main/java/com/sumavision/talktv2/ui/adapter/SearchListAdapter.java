package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sumavision.talktv2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhoutao on 2016/6/24.
 */
public class SearchListAdapter extends BaseAdapter {
    private Context context;
    private List<String> results;
    public TextView tv_searchlist_font;
    public TextView tv_searchlist_title;
    public SearchListAdapter(Context context){
        this.context = context;
        this.results = new ArrayList<>();
    }
    @Override
    public int getCount() {
        return results.size();
    }

    public void setSearchDataList(List<String> results) {
        this.results.addAll(results);
    }

    @Override
    public Object getItem(int position) {
        return results.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            convertView= View.inflate(context, R.layout.item_search_list, null);
            tv_searchlist_font = (TextView) convertView.findViewById(R.id.tv_searchlist_font);
            tv_searchlist_title = (TextView) convertView.findViewById(R.id.tv_searchlist_title);
        tv_searchlist_font.setText(position+1+"");
        tv_searchlist_title.setText(results.get(position));
        if (position == 0){
            tv_searchlist_font.setBackgroundResource(R.mipmap.search_top1);
            tv_searchlist_font.setTextColor(Color.WHITE);
        }else if(position == 1){
            tv_searchlist_font.setTextColor(Color.WHITE);
            tv_searchlist_font.setBackgroundResource(R.mipmap.search_top2);
        }else if(position == 2){
            tv_searchlist_font.setTextColor(Color.WHITE);
            tv_searchlist_font.setBackgroundResource(R.mipmap.search_top3);
        }
        return convertView;
    }

}
