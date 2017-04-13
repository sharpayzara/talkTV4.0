package com.sumavision.talktv2.util;

import android.content.Context;

import com.sumavision.talktv2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sharpay on 16-6-23.
 */
public class EdgeAcuity{
    private static List<String> instance;
    private EdgeAcuity(){};
    public static List<String> getInstance(Context context){
        if(instance == null){
            instance = new ArrayList<>();
            instance.add(context.getString(R.string.standardDef));
            instance.add(context.getString(R.string.hightDef));
            instance.add(context.getString(R.string.superDef));
        }
        return instance;
    }
}