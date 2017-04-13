package com.sumavision.talktv2.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.presenter.DefintionChangePresenter;
import com.sumavision.talktv2.ui.adapter.DefintionAdapter;
import com.sumavision.talktv2.ui.iview.IDefintionChangeDialogView;
import com.sumavision.talktv2.ui.widget.base.BaseDialog;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by zjx on 2016/6/28.
 */
public class DefintionChangeDialog extends BaseDialog<DefintionChangePresenter> implements IDefintionChangeDialogView{

    @BindView(R.id.defintion_list)
    ListView defintionList;

    private Context context;
    private int lastPostion;
    private ArrayList<String> defintionDatas = new ArrayList<>();

    public DefintionChangeDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        initLayout();
        setCanceledOnTouchOutside(true);
    }

    public void setDatas (int defaultDefintion, ArrayList<String> datas) {
        defintionDatas.clear();
        defintionDatas.addAll(datas);
        initList(defaultDefintion);
    }

    /**
     * 给dialog定位和设置大小
     */
    private void initLayout() {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);
    }

    private void initList(int defaultDefintion) {
        final DefintionAdapter defintionAdapter = new DefintionAdapter(context, defintionDatas);
        defintionList.setAdapter(defintionAdapter);
        defintionList.setSelector(new ColorDrawable(Color.TRANSPARENT));
        if(defaultDefintion != -1) {
            defintionAdapter.setSelector(defaultDefintion);
            lastPostion = defaultDefintion;
        }
        defintionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(lastPostion == position)
                    return;
                defintionAdapter.setSelector(position);
                defintionAdapter.notifyDataSetChanged();
                lastPostion = position;
                if(onDefintionChangeListener != null)
                    onDefintionChangeListener.onDefintionChange(position);
            }
        });
    }

    @Override
    public void initView() {
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.layout_defintion_change;
    }

    @Override
    protected void initPresenter() {
        presenter = new DefintionChangePresenter(context, this);
    }

    public interface OnDefintionChangeListener {
        void onDefintionChange(int postion);
    }
    private OnDefintionChangeListener onDefintionChangeListener;
    public void setOnDefintionChangeListener (OnDefintionChangeListener onDefintionChangeListener) {
        this.onDefintionChangeListener = onDefintionChangeListener;
    }
}
