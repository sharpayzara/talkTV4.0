package com.sumavision.talktv2.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sumavision.talktv2.R;

/**
 * 自媒体首页界面的Fragment
 * Created by sharpay on 2016/6/31.
 */
public class PicFragment extends Fragment{
    View view;
    ImageView iv;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pic,container,false);
        return view;
    }

    @Override
    public void onResume() {
        iv= (ImageView) view.findViewById(R.id.pic_iv);
        Bundle bundle = getArguments();
        String num = bundle.getString("pic");
        if(num.equals("0")){
            iv.setImageResource(R.mipmap.guide_a);
        }else if(num.equals("1")){
            iv.setImageResource(R.mipmap.guide_b);
        }else{
            iv.setImageResource(R.mipmap.guide_c);
        }
        super.onResume();
    }
}
