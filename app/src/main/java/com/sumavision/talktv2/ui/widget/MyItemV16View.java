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
public class MyItemV16View extends MyItemBaseView {
    Context mContext;
    RelativeLayout rl_myhitem_item;
    TextView tv_rankrec_classify;
    Button btn_rankrec_more;
    Button btn_myhitem_change;
    MyChildItemVEView mychilditemview1;
    MyChildItemVEView mychilditemview2;
    MyChildItemVEView mychilditemview3;
    MyChildItemVEView mychilditemview4;
    MyChildItemVEView mychilditemview5;
    MyChildItemVEView mychilditemview6;
    MyChildItemBaseView mychilditemviewbase;

    public MyItemV16View(Context context) {
        super(context);
        mContext = context;
        View view = View.inflate(mContext, R.layout.item_rec_myv16item, this);
        rl_myhitem_item = (RelativeLayout)view.findViewById(R.id.rl_myhitem_item);
        tv_rankrec_classify = (TextView)view.findViewById(R.id.tv_rankrec_classify);
        btn_rankrec_more = (Button)view.findViewById(R.id.btn_rankrec_more);
        btn_myhitem_change = (Button)view.findViewById(R.id.btn_myhitem_change);
        mychilditemview1 = (MyChildItemVEView)view.findViewById(R.id.mychilditemview1);

        mychilditemview2 = (MyChildItemVEView)view.findViewById(R.id.mychilditemview2);


        mychilditemview3 = (MyChildItemVEView)view.findViewById(R.id.mychilditemview3);

        mychilditemview4= (MyChildItemVEView)view.findViewById(R.id.mychilditemview4);

        mychilditemview5 = (MyChildItemVEView)view.findViewById(R.id.mychilditemview5);

        mychilditemview6 = (MyChildItemVEView)view.findViewById(R.id.mychilditemview6);
        mychilditemviewbase = (MyChildItemBaseView)view.findViewById(R.id.mychilditemviewbase);

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
    public MyChildItemVEView getMyChildVView1(){
        return mychilditemview1;
    }
    public MyChildItemVEView getMyChildVView2(){
        return mychilditemview2;
    }
    public MyChildItemVEView getMyChildVView3(){
        return mychilditemview3;
    }
    public MyChildItemVEView getMyChildVView4(){
        return mychilditemview4;
    }
    public MyChildItemVEView getMyChildVView5(){
        return mychilditemview5;
    }
    public MyChildItemVEView getMyChildVView6(){
        return mychilditemview6;
    }
    public MyChildItemBaseView getMyChildViewBaseView(){
        return mychilditemviewbase;
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
