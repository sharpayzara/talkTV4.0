package com.sumavision.talktv2.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.melot.meshow.room.T;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.presenter.FeedBackPresenter;
import com.sumavision.talktv2.ui.activity.base.CommonHeadPanelActivity;
import com.sumavision.talktv2.ui.iview.base.IBaseView;
import com.sumavision.talktv2.util.TipUtil;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by sharpay on 16-5-24.
 */
public class FeedBackActivity extends CommonHeadPanelActivity<FeedBackPresenter> implements IBaseView {
    FeedBackPresenter presenter;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.desc_tv)
    TextView descTv;
    @BindView(R.id.contact_et)
    TextView contactEt;
    List<String> problemList;
    String problemStr ="";
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_feedback;
    }

    @Override
    protected void initPresenter() {
        presenter = new FeedBackPresenter(this, this);
        presenter.init();
    }

    @Override
    public void initView() {
        initHeadPanel();
        setHeadTitle("有奖反馈");
        showBackBtn();
        problemList = new ArrayList<>();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @OnClick({R.id.submit})
    public void onClick(View view) {
        if(TextUtils.isEmpty(descTv.getText().toString())){
            TipUtil.showSnackTip(view,"请填写意见或感受，以帮助我们改进，谢谢");
            return;
        }
        if(problemList.size() != 0){
            for(String problem : problemList){
                problemStr += problem +",";
            }
            presenter.sendFeedBack(problemStr.substring(0,problemStr.length()-1),descTv.getText().toString(),contactEt.getText().toString());
        }else{
            presenter.sendFeedBack("",descTv.getText().toString(),contactEt.getText().toString());
        }

        Toast.makeText(this,"谢谢你的意见,我们将努力改进", Toast.LENGTH_SHORT).show();
        finish();
    }
    @OnCheckedChanged({R.id.label1, R.id.label2, R.id.label3, R.id.label4, R.id.label5, R.id.label6})
    public void onCheckChanged(CompoundButton button,boolean bool){
        if(bool){
            problemList.add(button.getText().toString());
        }else{
            problemList.remove(button.getText().toString());
        }
    }
}
