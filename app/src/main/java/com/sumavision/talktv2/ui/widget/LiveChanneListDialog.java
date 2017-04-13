package com.sumavision.talktv2.ui.widget;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.ShareElement;
import com.sumavision.talktv2.model.entity.decor.LiveData;
import com.sumavision.talktv2.presenter.LiveChannelListDialogPresenter;
import com.sumavision.talktv2.ui.adapter.ChannelAdapter;
import com.sumavision.talktv2.ui.adapter.ChannelTypeAdapter;
import com.sumavision.talktv2.ui.iview.ILiveChannelListDialogView;
import com.sumavision.talktv2.ui.widget.base.BaseDialog;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 直播播放器内频道列表
 * Created by zjx on 2016/6/19.
 */
public class LiveChanneListDialog extends BaseDialog<LiveChannelListDialogPresenter> implements ILiveChannelListDialogView{

    @BindView(R.id.fav_bg)
    ImageView favBg;
    @BindView(R.id.lv_channel_type_list)
    ListView typeList;
    @BindView(R.id.lv_channel_list)
    ListView channelList;
    @BindView(R.id.tv_channel_name)
    TextView typeName;
    @BindView(R.id.tv_no_channel)
    TextView noChannel;

    private Context mContext;
    private ChannelTypeAdapter channelTypeAdapter;
    private ChannelAdapter channelAdapter;

    private String channelId;
    private int typePos;//当前的typeposition

    private ArrayList<String> channelTypeDatas = new ArrayList<>();//频道类型数据
    private HashMap<Integer, ArrayList<LiveData.ContentBean.TypeBean.ChannelBean>> channelDatas = new HashMap<>();
    private ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> collectDatas;

    private int windowHeight;

    public LiveChanneListDialog(Context context, int themeResId, int windowHeight) {
        super(context, themeResId);
        this.windowHeight = windowHeight;
        this.mContext = context;
        initLayout(windowHeight);
        setCanceledOnTouchOutside(true);
    }

    /**
     * 给dialog定位和设置大小
     */
    private void initLayout(int windowHeight) {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        setFlag();
        lp.x = 0;
        lp.y = 0;
        lp.height = windowHeight;
        dialogWindow.setAttributes(lp);
    }

    private void setFlag() {
        if (Build.VERSION.SDK_INT < 19) {
            return;
        }
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    public void setChannelI (String channelId) {
        this.channelId = channelId;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_live_channel_list;
    }

    @Override
    protected void initPresenter() {
        presenter = new LiveChannelListDialogPresenter(mContext, this);
    }

    /**
     * 给dialog定位和设置大小
     */
    private void initLayout() {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        lp.x = 0;
        lp.y = 0;
        lp.height = windowHeight;
        dialogWindow.setAttributes(lp);
    }

    public void setTypeName(String type) {
        typeName.setText(type);
    }

    /**
     * 获取类型数据
     * @param channelTypeDatas
     */
    public void setTypeDatas (ArrayList<String> channelTypeDatas) {
        this.channelTypeDatas.addAll(channelTypeDatas);
    }

    /**
     * 获取直播频道数据
     * @param channelDatas
     */
    public void setChannelDatas(HashMap<Integer, ArrayList<LiveData.ContentBean.TypeBean.ChannelBean>> channelDatas) {
        this.channelDatas = channelDatas;
        presenter.getSelector(channelId, channelDatas);
    }

    /**
     * 获取收藏数据
     * @param collectDatas
     */
    public void setCollectDatas(ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> collectDatas) {
        this.collectDatas = collectDatas;
    }

    @Override
    public void initView() {}

    public void select(int typePos, String channelId) {
        this.channelId = channelId;
        channelAdapter.setChannelId(channelId);
        if(typePos==-1) {
            favDataShow();
            return;
        }
        this.typePos = typePos;
        channelTypeAdapter.setSelector(typePos);
        channelTypeAdapter.notifyDataSetChanged();
        favBg.setVisibility(View.GONE);
        channelAdapter.setChannelList(channelDatas.get(typePos));
        channelAdapter.notifyDataSetChanged();
        channelList.setSelection(channelAdapter.getSelector());
        typeName.setText(channelTypeDatas.get(typePos));
    }

    /**
     * 创建频道类型列表
     */
    private void initTypeList (int selector) {
        setTypeName(channelTypeDatas.get(selector));
        channelTypeAdapter = new ChannelTypeAdapter(mContext, channelTypeDatas);
        channelTypeAdapter.setSelector(selector);
        typeList.setAdapter(channelTypeAdapter);
        typeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                noChannel.setVisibility(View.GONE);
                channelList.setVisibility(View.VISIBLE);
                typePos = position;
                channelTypeAdapter.setSelector(position);
                channelTypeAdapter.notifyDataSetChanged();
                favBg.setVisibility(View.GONE);
                channelAdapter.setChannelList(channelDatas.get(position));
                channelAdapter.notifyDataSetChanged();
                channelList.setSelection(channelAdapter.getSelector());
                typeName.setText(channelTypeDatas.get(position));
            }
        });
    }

    /**
     * 创建频道列表
     */
    private void initChanelList (int selector) {
        channelAdapter = new ChannelAdapter(mContext);
        channelAdapter.setChannelList(channelDatas.get(selector));
        channelAdapter.setChannelId(channelId);
        channelList.setAdapter(channelAdapter);
        channelList.setSelection(channelAdapter.getSelector());
        channelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if(ShareElement.isIgnoreNetChange == 1)
//                    return;
                LiveData.ContentBean.TypeBean.ChannelBean channelBean = null;
                if(typePos>=0) {
                    channelBean = channelDatas.get(typePos).get(position);
                }
                else {
                    channelBean = collectDatas.get(position);
                }
                channelAdapter.setChannelId(channelBean.getId());
                channelAdapter.notifyDataSetChanged();
                if(onChangeChannelListener != null)
                    onChangeChannelListener.onChangeChannel(channelBean);
            }
        });
    }

    /**
     * 首次全屏获取播放频道的相关position
     * @param poss
     */
    @Override
    public void getSelector(int poss) {
        typePos = poss;
        initTypeList(poss);
        initChanelList(poss);
    }

    /**
     * 点击事件
     * @param v
     */
    @OnClick(R.id.ll_favorite)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_favorite:
               favDataShow();
                break;
        }
    }

    /**
     * 点击收藏
     */
    private void favDataShow() {
        favBg.setVisibility(View.VISIBLE);
        channelTypeAdapter.setSelector(-1);
        typePos = -1;
        channelTypeAdapter.notifyDataSetChanged();
        if(collectDatas.size()>0) {
            channelAdapter.setChannelList(collectDatas);
            channelAdapter.notifyDataSetChanged();
            noChannel.setVisibility(View.GONE);
            channelList.setVisibility(View.VISIBLE);
            channelList.setSelection(channelAdapter.getSelector());
        }
        else {
            noChannel.setVisibility(View.VISIBLE);
            channelList.setVisibility(View.INVISIBLE);
        }

        typeName.setText("收藏");
    }

    public interface OnChangeChannelListener {
        void onChangeChannel(LiveData.ContentBean.TypeBean.ChannelBean channelBean);
    }
    private OnChangeChannelListener onChangeChannelListener;
    public void setOnChangeChannelListener (OnChangeChannelListener onChangeChannelListener) {
        this.onChangeChannelListener = onChangeChannelListener;
    }
}
