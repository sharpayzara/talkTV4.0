package com.sumavision.talktv2.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jiongbull.jlog.JLog;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.entity.ShowEditButtonEvent;
import com.sumavision.talktv2.presenter.DragSettingFragmentPresenter;
import com.sumavision.talktv2.ui.activity.base.BaseActivity;
import com.sumavision.talktv2.ui.fragment.AboutUsFragment;
import com.sumavision.talktv2.ui.fragment.CopyRightNoticeFragment;
import com.sumavision.talktv2.ui.fragment.DragSettingFragment;
import com.sumavision.talktv2.ui.fragment.MesseageFragment;
import com.sumavision.talktv2.ui.fragment.MyGridFragment;
import com.sumavision.talktv2.ui.fragment.MyListFragment;
import com.sumavision.talktv2.ui.fragment.NoticeFragment;
import com.sumavision.talktv2.ui.fragment.OwnCacheFragment;
import com.sumavision.talktv2.ui.fragment.SetDefinitionFragment;
import com.sumavision.talktv2.ui.fragment.SettingFragment;
import com.sumavision.talktv2.ui.iview.base.IBaseView;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;

import butterknife.BindView;
import butterknife.OnClick;

public class DragSettingActivity extends BaseActivity<DragSettingFragmentPresenter> implements IBaseView,View.OnClickListener{

    //记录当前正在使用的fragment
    private Fragment isFragment;
    private Fragment fragment=null;
    //设置页面使用的fragment
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.view_toolbar_title)
    TextView view_toolbar_title;
    @BindView(R.id.view_toolbar_subtitle)
    TextView view_toolbar_subtitle;
    @BindView(R.id.btn_drag_edit)
    Button btn_drag_edit;
    @BindView(R.id.fm_fragment)
    FrameLayout fm_fragment;
    private String id;
    public DragSettingActivity(){
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            id = getIntent().getStringExtra("enternext");
            if (getIntent().getDataString() != null && getIntent().getDataString().contains("cache")) {
               id = AppGlobalConsts.EnterType.ENTERHUANCUN;
            }
            switch (id) {
                case AppGlobalConsts.EnterType.ENTERDINGZHI:
                    if (fragment == null) {

                        fragment = new DragSettingFragment();
                        AppGlobalConsts.TITLE_TXT = 0;
                    }
                    break;

                case AppGlobalConsts.EnterType.ENTERGRID:
                    if (fragment == null) {

                        fragment = new MyGridFragment();
                    }
                    AppGlobalConsts.TITLE_TXT = 2;
                    break;
                //说明是跳转消息界面
                case AppGlobalConsts.EnterType.ENTERXIAOXI:
                    if (fragment == null) {

                        fragment = new MesseageFragment();
                    }
                    AppGlobalConsts.TITLE_TXT = 3;
                    break;
                //说明是跳转设置界面
                case AppGlobalConsts.EnterType.ENTERSHEZHI:
                    if (fragment == null) {
                        fragment = new SettingFragment();
                    }
                    AppGlobalConsts.TITLE_TXT = 4;
                    break;
                case AppGlobalConsts.EnterType.ENTERHUANCUN://跳转到缓存界面
                    if (fragment == null) {

                        fragment = new OwnCacheFragment();
                    }
                    AppGlobalConsts.TITLE_TXT = 8;
                    break;
                case AppGlobalConsts.EnterType.ENTERTONGGAO://跳转到通告界面
                    if (fragment == null) {

                        fragment = new NoticeFragment();
                    }
                    AppGlobalConsts.TITLE_TXT = 9;
                    break;
                case AppGlobalConsts.EnterType.ENTERList://跳转到首页定制界面
                    if (fragment == null) {

                        fragment = new MyListFragment();
                    }
                    AppGlobalConsts.TITLE_TXT = 1;
                    break;
            }
            isFragment = fragment;
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fm_fragment, fragment)
                    .commit();
            refreshToolbar();
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_dragsetting;
    }

    @Override
    protected void initPresenter() {
        presenter = new DragSettingFragmentPresenter(this,this);
        presenter.init();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.dragsetting_btn_list:
                fragment = new MyListFragment();
                switchContent(isFragment,fragment);
                AppGlobalConsts.TITLE_TXT = 1;
                break;
            case R.id.dragsetting_btn_grid:
                fragment = new MyGridFragment();
                switchContent(isFragment,fragment);
                AppGlobalConsts.TITLE_TXT = 2;
                break;
        }
        refreshToolbar();

    }

    /**
     * 当fragment进行切换时，采用隐藏与显示的方法加载fragment以防止数据的重复加载
     * @param from
     * @param to
     */
    private void switchContent(Fragment from, Fragment to) {
        /* getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fm_fragment, fragment)
                        .addToBackStack(null)
                        .commit();*/
        if (isFragment != to) {
            isFragment = to;
            FragmentManager fm = getSupportFragmentManager();
            //添加渐隐渐现的动画
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fm_fragment,fragment).addToBackStack("11").commit();
           /* if (!to.isAdded()) {    // 先判断是否被add过
                ft.hide(from).add(R.id.fm_fragment, to).addToBackStack("11").commit(); // 隐藏当前的fragment，add下一个到Activity中

            } else {
                ft.hide(from).show(to).addToBackStack("11").commit(); // 隐藏当前的fragment，显示下一个
            }*/
        }
    }


    @Override
    protected void onDestroy() {
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }

    @Override
    public void initView() {
        BusProvider.getInstance().register(this);
        initToolBar();

    }

    private void initToolBar() {
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        view_toolbar_title.setText("个人定制");
        refreshToolbar();
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.nav_right_icon);
//        toolbar.setOnMenuItemClickListener(this);
    }

   /* *//**
     *
     * 当客户点击菜单当中的某一个选项时，会调用该方法
     * @param item
     * @return
     *//*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }*/
/*
    *//* 菜单的监听可以在toolbar里设置，也可以像ActionBar那样，通过Activity的onOptionsItemSelected回调方法来处理 *//*
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ab_search:
                Toast.makeText(DragSettingActivity.this, "ab_search", Toast.LENGTH_LONG).show();
                break;
            case R.id.action_settings:
                Toast.makeText(DragSettingActivity.this, "action_settings", Toast.LENGTH_LONG).show();
                break;
            case R.id.action_share:
                Toast.makeText(DragSettingActivity.this, "action_share", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
        return true;
    }*/
/*
    *//**
     * 当客户点击MENU按钮的时候，调用该方法
     * @param menu
     * @return
     *//*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //添加menu按钮的布局
        getMenuInflater().inflate(R.menu.menu_main, menu);
		*//* ShareActionProvider配置 *//*
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menu
                .findItem(R.id.action_share));
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text*//*");
        mShareActionProvider.setShareIntent(intent);
        return super.onCreateOptionsMenu(menu);
    }*/

   /* *//**
     * 一般情况下，每次按menu键Framewrok都会先调用onPrepareOptionsMenu()，准备需要显示的菜单。
     * 但是当按menu键之前调用了Activity.invalidateOptionsMenu()之后，情况变的不一样，调用完Activity.invalidateOptionsMenu()之后，
     * Framework会立即调用onPrepareOptionsMenu()准备好菜单项数据，之后当用户按menu键时，Framework不会再次调用onPrepareOptionsMenu()，
     * 而是将之前准备好的菜单显示出来。这点一定要注意。
     * @param menu
     * @return
     *//*
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //一般是需要更新toolbar显示之前调用clear();
//        每次调用onPrepareOptionsMenu()的时候需要首先调用menu.clear()清除一下之前的menu数据，
// 如果不清除的话，之前的menu数据不会被释放，造成内存泄漏。
//        menu.clear();
        return super.onPrepareOptionsMenu(menu);
    }*/

    /**
     * 刷新toolbartitle的方法
     */
    public void refreshToolbar(){
        switch (AppGlobalConsts.TITLE_TXT){
            case 0:
                view_toolbar_title.setText("个人定制");
                view_toolbar_subtitle.setText("");
                break;
            case 1:
                view_toolbar_title.setText("推荐卡片");
                view_toolbar_subtitle.setText("");
                break;
            case 2:
                view_toolbar_title.setText("频道");
                view_toolbar_subtitle.setText("长按拖拽排序");
                break;
            case 3:
                view_toolbar_title.setText("消息中心");
                view_toolbar_subtitle.setText("");
                break;
            case 4:
                view_toolbar_title.setText("设置");
                view_toolbar_subtitle.setText("");
                break;
            case 5:
                view_toolbar_title.setText("版权声明");
                view_toolbar_subtitle.setText("");
                break;
            case 6:
                view_toolbar_title.setText("关于我们");
                view_toolbar_subtitle.setText("");
                break;
            case 7:
                view_toolbar_title.setText("播放&缓存清晰度选择");
                view_toolbar_subtitle.setText("");
                break;
            case 8:
                view_toolbar_title.setText("我的缓存");
                view_toolbar_subtitle.setText("");
                break;
            case 9:
                view_toolbar_title.setText("关于积分.活动暂停的通告");
                view_toolbar_subtitle.setText("");
                break;
        }
        toolbar.invalidate();
    }


    @Override
    public void onBackPressed() {
        //对于toolbar的回退键而言，它也是要走这个方法的
        refreshToolbar();
        super.onBackPressed();
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("setting")})
    public void enterNext(String id){
        switch (id){
            //说明跳转到版权声明页面
            case "005":
                    fragment = new CopyRightNoticeFragment();
                AppGlobalConsts.TITLE_TXT = 5;
                break;
            case "006"://说明跳转到关于我们页面
                    fragment = new AboutUsFragment();
                AppGlobalConsts.TITLE_TXT = 6;
                break;
            case "007"://说明跳转到选择清晰度页面
                    fragment = new SetDefinitionFragment();
                AppGlobalConsts.TITLE_TXT = 7;
                break;
        }
        switchContent(isFragment,fragment);
        refreshToolbar();
    }


    @OnClick(R.id.btn_drag_edit)
    public void editCache(){
        AppGlobalConsts.ISEDITSTATE = !AppGlobalConsts.ISEDITSTATE;
        refreshOwnCacheFragment(AppGlobalConsts.ISEDITSTATE);
        JLog.e("编辑按钮被点击了");
    }

    /**
     * 这是刷新缓存界面显示的方法
     */
    private void refreshOwnCacheFragment(boolean b) {
        if (b){
            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_C,"refreshEdit");
        }else{
            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_D,"backEdit");
        }
    }

    // 根据当前显示的fragment，决定是否显示编辑按钮
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(AppGlobalConsts.EventType.TAG_CACHE_SHOW_EDIT)})
    public void showEditButton(ShowEditButtonEvent event) {
        if (fragment instanceof OwnCacheFragment) {
            int tmp = ((OwnCacheFragment) fragment).getCurrentItem();
            if (event.item == tmp) {
                if (event.show) {
                    btn_drag_edit.setVisibility(View.VISIBLE);
                } else {
                    btn_drag_edit.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
}
