package com.sumavision.talktv2.ui.widget;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.presenter.LiveSourcePresenter;
import com.sumavision.talktv2.ui.adapter.LiveSourceAdapter;
import com.sumavision.talktv2.ui.iview.ILiveSourceView;
import com.sumavision.talktv2.ui.widget.base.BaseDialog;

import butterknife.BindView;

/**
 * Created by zjx on 2016/6/22.
 */
public class LiveSourceDialog extends BaseDialog<LiveSourcePresenter> implements ILiveSourceView{

    @BindView(R.id.sourcelist)
    ListView sourceList;

    LiveSourceAdapter adapter;

    private int currSource;

    private Context mContetxt;
    public LiveSourceDialog(Context context, int themeResId, int windowHeight) {
        super(context, themeResId);
        this.mContetxt = context;
        initLayout(windowHeight);
    }



    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_live_source;
    }

    @Override
    protected void initPresenter() {
        presenter = new LiveSourcePresenter(mContetxt, this);
    }

    public void setSourceDatas(int count) {
        initListView(count);
    }

    public void initListView(int sourceNum){
        currSource = 0;
        adapter = new LiveSourceAdapter(mContetxt, sourceNum);
        sourceList.setAdapter(adapter);
        sourceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currSource != position) {
                    currSource = position;
                    if(onSourceChangeListener != null)
                        onSourceChangeListener.onSourceChange(position);

                    adapter.setSelector(position);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void changeSourceAuto (int pos) {
        adapter.setSelector(pos);
        adapter.notifyDataSetChanged();
    }

    public void setPos(int pos) {
        currSource = pos;
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

    @Override
    public void initView() {

    }

    public interface OnSourceChangeListener {
        void onSourceChange(int pos);
    }
    private OnSourceChangeListener onSourceChangeListener;
    public void setOnSourceChangeListener (OnSourceChangeListener onSourceChangeListener) {
        this.onSourceChangeListener = onSourceChangeListener;
    }
}
