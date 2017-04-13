package com.sumavision.talktv2.ui.activity;

import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.entity.PreferenceBean;
import com.sumavision.talktv2.presenter.PreferencePresenter;
import com.sumavision.talktv2.ui.activity.base.CommonHeadPanelActivity;
import com.sumavision.talktv2.ui.iview.IPreferenceView;
import com.sumavision.talktv2.util.AppUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by sharpay on 16-5-24.
 */
public class PreferenceEditActivity extends CommonHeadPanelActivity<PreferencePresenter> implements IPreferenceView {
    PreferencePresenter presenter;

    List<CheckBox> list;
    @BindView(R.id.man_cb)
    CheckBox manCb;
    @BindView(R.id.woman_cb)
    CheckBox womanCb;
    @BindView(R.id.boy_cb)
    CheckBox boyCb;
    @BindView(R.id.old_man_cb)
    CheckBox oldManCb;
    @BindView(R.id.label1)
    CheckBox label1;
    @BindView(R.id.label2)
    CheckBox label2;
    @BindView(R.id.label3)
    CheckBox label3;
    @BindView(R.id.label4)
    CheckBox label4;
    @BindView(R.id.label5)
    CheckBox label5;
    @BindView(R.id.label6)
    CheckBox label6;
    @BindView(R.id.label7)
    CheckBox label7;
    @BindView(R.id.label8)
    CheckBox label8;
    @BindView(R.id.label9)
    CheckBox label9;
    @BindView(R.id.label10)
    CheckBox label10;
    @BindView(R.id.label11)
    CheckBox label11;
    @BindView(R.id.label12)
    CheckBox label12;
    @BindView(R.id.label13)
    CheckBox label13;
    @BindView(R.id.label14)
    CheckBox label14;
    @BindView(R.id.label15)
    CheckBox label15;
    List<CheckBox> lableList;
    String roleStr;
    String preferenceStr;
    PreferenceBean bean;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_preference2;
    }

    @Override
    protected void initPresenter() {
        presenter = new PreferencePresenter(this, this);
        presenter.init();
    }

    @Override
    public void initView() {
        initHeadPanel();
        showBackBtn();
        setHeadTitle("兴趣管理");
        list = new ArrayList<>();
        list.add(manCb);
        list.add(womanCb);
        list.add(boyCb);
        list.add(oldManCb);
        lableList = new ArrayList<>();
        lableList.add(label1);
        lableList.add(label2);
        lableList.add(label3);
        lableList.add(label4);
        lableList.add(label5);
        lableList.add(label6);
        lableList.add(label7);
        lableList.add(label8);
        lableList.add(label9);
        lableList.add(label10);
        lableList.add(label11);
        lableList.add(label12);
        lableList.add(label13);
        lableList.add(label14);
        lableList.add(label15);
        presenter.loadPreferenceStr();
    }

    @OnClick(R.id.submit)
    public void onClick() {
        preferenceStr = "";
        for(CheckBox cb : lableList){
            if(cb.isChecked()){
                preferenceStr += cb.getText().toString()+",";
            }
        }
        if(preferenceStr.length() > 0 ){
            preferenceStr = preferenceStr.substring(0,preferenceStr.length()-1);
        }
        for(CheckBox cb :list){
            if(cb.isChecked()){
                roleStr = cb.getText().toString();
            }
        }
        if(bean == null){
            bean = new PreferenceBean();
        }
        bean.setPreference(preferenceStr);
        bean.setRole(roleStr);
        presenter.savePreferenceStr(bean);
        bean.setDid(AppUtil.getDeviceInfo(this));
        presenter.sendPreferenceStr(bean);
        finish();
    }

    @OnClick({R.id.man_llt, R.id.woman_llt, R.id.boy_llt, R.id.old_man_llt, R.id.label1, R.id.label2, R.id.label3, R.id.label4, R.id.label5, R.id.label6, R.id.label7, R.id.label8, R.id.label9, R.id.label10, R.id.label11, R.id.label12, R.id.label13, R.id.label14, R.id.label15})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.man_llt:
                if (manCb.isChecked()) {
                    return;
                } else {
                    for (CheckBox cbox : list) {
                        cbox.setChecked(false);
                    }
                    manCb.setChecked(true);
                }
                break;
            case R.id.woman_llt:
                if (womanCb.isChecked()) {
                    return;
                } else {
                    for (CheckBox cbox : list) {
                        cbox.setChecked(false);
                    }
                    womanCb.setChecked(true);
                }
                break;
            case R.id.boy_llt:
                if (boyCb.isChecked()) {
                    return;
                } else {
                    for (CheckBox cbox : list) {
                        cbox.setChecked(false);
                    }
                    boyCb.setChecked(true);
                }
                break;
            case R.id.old_man_llt:
                if (oldManCb.isChecked()) {
                    return;
                }else {
                    for (CheckBox cbox : list) {
                        cbox.setChecked(false);
                    }
                    oldManCb.setChecked(true);
                }
                break;
        }
    }

    @Override
    public void fillData(PreferenceBean bean) {
        if(bean == null){
            return;
        }
        this.bean = bean;
        if(bean.getRole() != null){
            if (bean.getRole().equals(manCb.getText().toString())) {
                manCb.setChecked(true);
            } else if (bean.getRole().equals(womanCb.getText().toString())) {
                womanCb.setChecked(true);
            } else if (bean.getRole().equals(boyCb.getText().toString())) {
                boyCb.setChecked(true);
            } else if (bean.getRole().equals(oldManCb.getText().toString())) {
                oldManCb.setChecked(true);
            }
        }

        for(CheckBox cb : lableList){
            if(bean.getPreference().contains(cb.getText().toString())){
                cb.setChecked(true);
            }
        }
    }
}
