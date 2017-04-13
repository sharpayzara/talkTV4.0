package com.sumavision.talktv2.common;

import android.graphics.drawable.Drawable;

import com.sumavision.talktv2.model.entity.UserInfo;
import com.sumavision.talktv2.ui.adapter.ArtsAdapter;
import com.sumavision.talktv2.ui.adapter.ArtsSelectionsAdapter;
import com.sumavision.talktv2.ui.adapter.MediaItemCommonAdapter;
import com.sumavision.talktv2.ui.adapter.SelectionsRecyclerAdapter;
import com.sumavision.talktv2.ui.adapter.MediaSelectionsRecyclerAdapter;

public class ShareElement {
    public static Drawable userImgDrawable;
    public static Drawable shareDrawable;
    public static SelectionsRecyclerAdapter.MyViewHolder lastHolder;
    public static MediaSelectionsRecyclerAdapter.MyViewHolder mediaLastHolder;
    public static ArtsAdapter.ViewHolder artsAdapterHolder;
    public static ArtsSelectionsAdapter.SelectionsViewHolder artsPopuHolder;
    public static MediaItemCommonAdapter.CommonItemHolder mediaRecommendLastHolder;
    public static String channelId;
    public static boolean isFirst;

    public static int isIgnoreNetChange = -1;//3、4G网络情况下是否观看视频   1---不观看   2---看
    public static boolean isMobileNet;//连接的是wifi还是移动网络

    public static final int PORTRAIT = 146545;//竖屏
    public static final int LANDSCAPE = 4564687;//正向横屏
    public static final int REVERSILANDSCAPE = 456485;//反向横屏
}
