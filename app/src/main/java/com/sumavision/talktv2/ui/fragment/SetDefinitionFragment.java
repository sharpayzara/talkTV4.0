package com.sumavision.talktv2.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.ACache;
import com.sumavision.talktv2.presenter.SetDefinitionFragmentPresenter;
import com.sumavision.talktv2.ui.fragment.Base.BaseFragment;
import com.sumavision.talktv2.ui.iview.base.IBaseView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by zhoutao on 2016/6/17.
 */
public class SetDefinitionFragment extends BaseFragment<SetDefinitionFragmentPresenter> implements IBaseView {
    @BindView(R.id.rl_setdefi_fluency)
    RelativeLayout rl_setdefi_fluency;
    @BindView(R.id.rl_setdefi_standard)
    RelativeLayout rl_setdefi_standard;
    @BindView(R.id.rl_setdefi_super)
    RelativeLayout rl_setdefi_super;
    @BindView(R.id.iv_setdefi_fluency)
    ImageView iv_setdefi_fluency;
    @BindView(R.id.iv_setdefi_standard)
    ImageView iv_setdefi_standard;
    @BindView(R.id.iv_setdefi_super)
    ImageView iv_setdefi_super;

    private int currentSelect = 0;

    public SetDefinitionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_setdefi;
    }

    @Override
    protected void initPresenter() {
        presenter = new SetDefinitionFragmentPresenter(getContext(), this);
        presenter.init();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void initView() {
        if(BaseApp.getACache().getAsObject("currentSelect") == null){
            currentSelect = 0;
        }else{
            currentSelect = (int) BaseApp.getACache().getAsObject("currentSelect");
        }
       refreshViewByNum(currentSelect);
    }

    @OnClick({R.id.rl_setdefi_fluency, R.id.rl_setdefi_standard, R.id.rl_setdefi_super})
    public void refreshShow(View view) {
        if (view == rl_setdefi_fluency) {
            currentSelect = 0;
        } else if (view == rl_setdefi_standard) {
            currentSelect = 1;
        } else {
            currentSelect = 2;
        }
        refreshViewByNum(currentSelect);
        ACache.get(getContext()).put("currentSelect",currentSelect);
    }
        public void refreshViewByNum(int currentSelect){
            switch (currentSelect){
                case 0:
                    iv_setdefi_fluency.setVisibility(View.VISIBLE);
                    iv_setdefi_standard.setVisibility(View.INVISIBLE);
                    iv_setdefi_super.setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    iv_setdefi_fluency.setVisibility(View.INVISIBLE);
                    iv_setdefi_standard.setVisibility(View.VISIBLE);
                    iv_setdefi_super.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    iv_setdefi_fluency.setVisibility(View.INVISIBLE);
                    iv_setdefi_standard.setVisibility(View.INVISIBLE);
                    iv_setdefi_super.setVisibility(View.VISIBLE);
                    break;

            }
        }
}