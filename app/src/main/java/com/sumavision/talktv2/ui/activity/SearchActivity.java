package com.sumavision.talktv2.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.entity.HintData;
import com.sumavision.talktv2.presenter.SearchPresenter;
import com.sumavision.talktv2.ui.activity.base.ToolBarActivity;
import com.sumavision.talktv2.ui.fragment.SearchListInfoFragment;
import com.sumavision.talktv2.ui.fragment.SearchMainFragment;
import com.sumavision.talktv2.ui.iview.ISearchView;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class SearchActivity extends ToolBarActivity<SearchPresenter> implements ISearchView {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_search)
    Button tv_search;
    @BindView(R.id.ib_editdelete)
    ImageButton ib_editdelete;
    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.searchcontent)
    FrameLayout searchcontent;
    @BindView(R.id.hint_lv)
    ListView hintLv;
    private ArrayAdapter<String> mAdapter;
    private Fragment fragment;
    private List<String> searchHisList;
    private boolean hintFlag = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (fragment == null) {

            fragment = new SearchMainFragment();
        }
        getSupportFragmentManager().beginTransaction()
                .add(R.id.searchcontent, fragment)
                .commit();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_searches;
    }

    @Override
    protected void initPresenter() {
        BusProvider.getInstance().register(this);
        presenter = new SearchPresenter(this, this);
        presenter.init();
    }

    @Override
    protected void onDestroy() {
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("scrolling")})
    public void hideWindow(String scrolling) {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchcontent.getWindowToken(), 0);
        AppGlobalConsts.EDITSTATE = false;
    }

    @Override
    public void initView() {
        AppGlobalConsts.ISSEARCHBACK = false;
        searchHisList = new ArrayList<>();
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.nav_right_icon);
        RxTextView.textChangeEvents(et_search)
                .observeOn(AndroidSchedulers.mainThread())  //触发后回到Android主线程调度器
                .subscribe(new Action1<TextViewTextChangeEvent>() {
                    @Override
                    public void call(TextViewTextChangeEvent textViewTextChangeEvent) {
                        if(!hintFlag){
                            hintFlag = true;
                            return;
                        }
                        String key = textViewTextChangeEvent.text().toString().trim();
                        if (TextUtils.isEmpty(key)) {
                            if (mAdapter != null) {
                                mAdapter.clear();
                                mAdapter.notifyDataSetChanged();
                            }
                            hintLv.setVisibility(View.GONE);
                        } else {
                            hintLv.setVisibility(View.VISIBLE);
                            presenter.getHintData(key);
                        }
                    }
                });

    }

    private void fillList(List<String> keyWords) {
        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<>(this, R.layout.item_search, R.id.tv_text, keyWords);
            hintLv.setAdapter(mAdapter);
            hintLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    hintFlag = false;
                    hintLv.setVisibility(View.GONE);
                    et_search.setText(mAdapter.getItem(position));
                    et_search.setSelection(mAdapter.getItem(position).length());
                    tv_search.performClick();
                }
            });
        } else {
            mAdapter.clear();
            mAdapter.addAll(keyWords);
            mAdapter.notifyDataSetChanged();
            if(keyWords.size() == 0){
                hintLv.setVisibility(View.GONE);
            }
        }
    }

    @OnClick(R.id.tv_search)
    public void searchData() {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(tv_search.getWindowToken(), 0);
        //获取当前edittext的输入内容
        hintFlag = false;
        hintLv.setVisibility(View.GONE);
        String currentStr = "Andrea";
        if (et_search.getText().toString() != null) {
            currentStr = et_search.getText().toString();
        } else {
            currentStr = "";
        }
        //切换到展示搜索结果的Fragment
        fragment = new SearchListInfoFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.searchcontent, fragment)
                .commit();
        getIntent().putExtra("currentStr", currentStr);
        searchHisList = CommonUtil.getSearch(this);
        if(!TextUtils.isEmpty(currentStr)&& !(currentStr.trim().equals(""))){
            CommonUtil.addSearchListInfo(currentStr.trim(), searchHisList, this);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick(R.id.ib_editdelete)
    public void deleteEditText() {
        et_search.setText("");
    }

    @OnClick(R.id.et_search)
    public void showEditText() {
        AppGlobalConsts.EDITSTATE = true;
    }

    @Override
    public void fillHintData(HintData data) {
        List<String> tempList = new ArrayList<>();
        for(HintData.DataBean bean : data.getData()){
            tempList.add(bean.getName());
        }
        fillList(tempList);
    }
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(AppGlobalConsts.EventType.TAG_A)})
    public void refreshShow(String msg){
        hintFlag = false;
        et_search.setText(msg);
        et_search.setSelection(msg.length());
        tv_search.performClick();
    }

}
