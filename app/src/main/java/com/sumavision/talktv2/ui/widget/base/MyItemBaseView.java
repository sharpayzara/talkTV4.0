package com.sumavision.talktv2.ui.widget.base;

import android.content.Context;
import android.widget.RelativeLayout;

import com.sumavision.talktv2.ui.widget.MyChildItemBaseView;

/**
 * Created by zhoutao on 2016/8/23.
 *
 */
public  class MyItemBaseView extends RelativeLayout {
    private Context mContext;
    private MyChildItemBaseView myChildItemBaseView;
    public MyItemBaseView(Context context) {
        super(context);
        mContext = context;
    }

    //下面两个是关于换一批按钮的方法
    public void setChangeBtnShow(boolean b){
    }

   /* public  Button getChangeBtn(){
        return ;
    };

    public  Button getMoreBtn(){

    };
    */
    public MyChildItemBaseView getMyChildViewBaseView(){
        if (myChildItemBaseView==null){
            myChildItemBaseView = new MyChildItemBaseView(mContext);
        }
        return myChildItemBaseView;
    };

    public void setClassifyname(String name){
    }

    public void setTopShow(boolean b){
    }

}
