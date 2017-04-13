package com.sumavision.talktv2.ui.widget;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.ui.widget.base.MyItemBaseView;

/**
 * Created by zhoutao on 2016/6/29.
 */
public  class MyItemHView extends MyItemBaseView {
    private Context mContext;
    RelativeLayout rl_myhitem_item;
    TextView tv_rankrec_classify;
    Button btn_rankrec_more;
    Button btn_myhitem_change;
    MyChildItemHView mychilditemview1;
    MyChildItemHView mychilditemview2;
    MyChildItemHView mychilditemview3;
    MyChildItemHView mychilditemview4;

    public MyItemHView(Context context) {
        super(context);
        mContext = context;
        View view = View.inflate(context, R.layout.item_rec_myhitem, this);
        rl_myhitem_item = (RelativeLayout)view.findViewById(R.id.rl_myhitem_item);
        tv_rankrec_classify = (TextView)view.findViewById(R.id.tv_rankrec_classify);
        btn_rankrec_more = (Button)view.findViewById(R.id.btn_rankrec_more);
        btn_myhitem_change = (Button)view.findViewById(R.id.btn_myhitem_change);
        mychilditemview1 = (MyChildItemHView)view.findViewById(R.id.mychilditemview1);

        mychilditemview2 = (MyChildItemHView)view.findViewById(R.id.mychilditemview2);

        mychilditemview3 = (MyChildItemHView)view.findViewById(R.id.mychilditemview3);

        mychilditemview4 = (MyChildItemHView)view.findViewById(R.id.mychilditemview4);

    }

    //下面两个是关于换一批按钮的方法
    public void setChangeBtnShow(boolean b){
        if (b){
            btn_myhitem_change.setVisibility(View.VISIBLE);
        }else{
            btn_myhitem_change.setVisibility(View.GONE);
        }
    }

    public Button getChangeBtn(){
        return btn_myhitem_change;
    }
    //
    public MyChildItemHView getMyChildHView1(){
        return mychilditemview1;
    }
    public MyChildItemHView getMyChildHView2(){
        return mychilditemview2;
    }
    public MyChildItemHView getMyChildHView3(){
        return mychilditemview3;
    }
    public MyChildItemHView getMyChildHView4(){
        return mychilditemview4;
    }

    public Button getMoreBtn(){
        return btn_rankrec_more;
    }

    public void setClassifyname(String name){
        tv_rankrec_classify.setText(name);
    }

    public void setTopShow(boolean b){
        if (b){
            rl_myhitem_item.setVisibility(View.VISIBLE);
        }else{
            rl_myhitem_item.setVisibility(View.GONE);
        }
    }

}
