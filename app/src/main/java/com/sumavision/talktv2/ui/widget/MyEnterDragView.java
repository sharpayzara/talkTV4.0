package com.sumavision.talktv2.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.ui.activity.DragSettingActivity;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.NoDoubleClickListener;

/**
 * Created by zhoutao on 2016/6/29.
 */
public class MyEnterDragView extends RelativeLayout {
    private Context mContext;
    private RelativeLayout rl;
    public MyEnterDragView(Context context) {
        super(context);
        mContext = context;
        View view = View.inflate(mContext,R.layout.layout_myenterdrag,this);
        rl = (RelativeLayout) view.findViewById(R.id.rl);
        rl.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, DragSettingActivity.class);
                intent.putExtra("enternext", AppGlobalConsts.EnterType.ENTERList);
                mContext.startActivity(intent);
            }
        });
    }
}
