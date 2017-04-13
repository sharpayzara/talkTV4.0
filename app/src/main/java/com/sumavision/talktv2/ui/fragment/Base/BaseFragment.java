package com.sumavision.talktv2.ui.fragment.Base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.sumavision.talktv2.presenter.base.BasePresenter;

import butterknife.ButterKnife;
/**
 *  desc  fragment基类
 *  @author  yangjh
 *  created at  16-5-24 下午8:57
 */
public abstract class BaseFragment<T extends BasePresenter> extends Fragment {

    protected BackHandledInterface mBackHandledInterface;
    protected View view;
    protected T presenter;
    public String tid,currentType;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(getLayoutResId(), container, false);
        ButterKnife.bind(this, view);
        initPresenter();
        return view;
    }
    public void set(String currentType,String tid){
        this.tid = tid;
        this.currentType = currentType;
    }

    protected abstract int getLayoutResId();

    protected abstract void initPresenter();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
      //  ButterKnife.unbind(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!(getActivity() instanceof BackHandledInterface)){
            throw new ClassCastException("Hosting Activity must implement BackHandledInterface");
        }else{
            this.mBackHandledInterface = (BackHandledInterface)getActivity();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //告诉FragmentActivity，当前Fragment在栈顶
        mBackHandledInterface.setSelectedFragment(this);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        view = null;
       // App.getRefWatcher(getContext()).watch(this);
    }
    public abstract boolean onBackPressed();
}
