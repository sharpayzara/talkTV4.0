package com.sumavision.talktv2.ui.fragment;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jiongbull.jlog.JLog;
import com.sumavision.offlinelibrary.entity.InternalExternalPathInfo;
import com.sumavision.offlinelibrary.util.CommonUtils;
import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.ACache;
import com.sumavision.talktv2.model.entity.VersionData;
import com.sumavision.talktv2.presenter.SettingFragmentPresenter;
import com.sumavision.talktv2.ui.activity.DragSettingActivity;
import com.sumavision.talktv2.ui.fragment.Base.BaseFragment;
import com.sumavision.talktv2.ui.iview.ISettingView;
import com.sumavision.talktv2.ui.services.AppUpdateService;
import com.sumavision.talktv2.ui.widget.ChangeCachePathDialog;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.PreferencesUtils;
import com.sumavision.talktv2.util.UpdateUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by zhoutao on 2016/6/16.
 */
public class SettingFragment extends BaseFragment<SettingFragmentPresenter> implements ISettingView{
    @BindView(R.id.tv_setting_cache)
    TextView tv_setting_cache;
    @BindView(R.id.tv_setting_define)
    TextView tv_setting_define;
    @BindView(R.id.rl_setting_cache)
    RelativeLayout rl_setting_cache;
    @BindView(R.id.rl_setting_clearcache)
    RelativeLayout rl_setting_clearcache;
    @BindView(R.id.rl_setting_allowcache)
    RelativeLayout rl_setting_allowcache;
    @BindView(R.id.rl_setting_messeage)
    RelativeLayout rl_setting_messeage;
    @BindView(R.id.rl_setting_version)
    RelativeLayout rl_setting_version;
    @BindView(R.id.rl_setting_grade)
    RelativeLayout rl_setting_grade;
    @BindView(R.id.rl_setting_copyrightnotice)
    RelativeLayout rl_setting_copyrightnotice;
    @BindView(R.id.rl_setting_aboutus)
    RelativeLayout rl_setting_aboutus;
    @BindView(R.id.img_setting_messeage)
    ImageView img_setting_messeage;
    @BindView(R.id.fl_settingcontainer)
    FrameLayout fl_settingcontainer;
    @BindView(R.id.img_setting_allowcache)
    ImageView img_setting_allowcache;
    @BindView(R.id.rl_cache_path)
    RelativeLayout rl_cache_path;
    @BindView(R.id.cache_path)
    TextView cache_path;
    //作为标记点击是否允许网络运营商缓存的常量
    private  boolean CLICKNUM_ALLOWCACHE;
    //作为标记点击是否允许推送消息的常量
    private  boolean CLICKNUM_INFO;
    private  int currentSelect;
    private final int MSG_CLEAN_OVER = 1;//清理成功
    private final int MSG_CLEAN_ERROR = 2;//清理失败
    private VersionData versionData;//更新信息
    public SettingFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void initPresenter() {
        presenter = new SettingFragmentPresenter(getContext(),this);
        presenter.init();
    }
    @Override
    public void initView() {
        BusProvider.getInstance().register(this);
        tv_setting_cache.setText("播放&缓存清晰度选择");
        //获取本地存储的CLICKNUM_ALLOWCACHE和CLICKNUM_INFO值
        if(BaseApp.getACache().getAsObject("CLICKNUM_ALLOWCACHE")==null){
            CLICKNUM_ALLOWCACHE = false;
        }else{

            CLICKNUM_ALLOWCACHE = (boolean) ACache.get(getContext()).getAsObject("CLICKNUM_ALLOWCACHE");
        }
        if(BaseApp.getACache().getAsObject("CLICKNUM_INFO")==null){
            CLICKNUM_INFO = true;
        }else{
            CLICKNUM_INFO = (boolean) ACache.get(getContext()).getAsObject("CLICKNUM_INFO");
        }
        cache_path.setText(PreferencesUtils.getInt(getContext(), null, "cache_path_type") == 0 ? "手机存储" : "SD卡");
        refreshCheckAllowcacheBtn();
        refreshCheckInfoBtn();
        //获取服务器数据如果没有数据则设置空页面显示

    }


    /**
     * 这是根据本地数据刷新是否允许运营商缓存按钮的方法
     */
    private void refreshCheckAllowcacheBtn() {
        if(CLICKNUM_ALLOWCACHE){
            img_setting_allowcache.setBackgroundResource(R.mipmap.checkbox_selected_btn);
        }else{
            img_setting_allowcache.setBackgroundResource(R.mipmap.checkbox_nor_btn);
        }
        //把选中的是否允许运营商缓存的状态保存到本地中
        BaseApp.getACache().put("CLICKNUM_ALLOWCACHE",CLICKNUM_ALLOWCACHE);
    }
    /**
     * 这是根据本地数据刷新是否允许推送按钮的方法
     */
    private void refreshCheckInfoBtn() {
        if(CLICKNUM_INFO){
            img_setting_messeage.setBackgroundResource(R.mipmap.checkbox_selected_btn);
        }else{
            img_setting_messeage.setBackgroundResource(R.mipmap.checkbox_nor_btn);
        }
        //把选中的是否允许推送的状态保存到本地中
        ACache.get(getContext()).put("CLICKNUM_INFO",CLICKNUM_INFO);
    }

    @Override
    public void onDestroy() {
//        BusProvider.getInstance().unregister(this);
        BaseApp.getACache().put("CLICKNUM_ALLOWCACHE",CLICKNUM_ALLOWCACHE);
        BaseApp.getACache().put("CLICKNUM_INFO",CLICKNUM_INFO);
        super.onDestroy();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    //这是点击进入选择默认清晰度的方法
    @OnClick(R.id.rl_setting_cache)
    public void setDefinition(){
        enterNext("007");
    }
    //这是点击清除缓存的方法
    @OnClick(R.id.rl_setting_clearcache)
    public void clearCache(){
        Glide.get(getContext()).clearMemory();
        deleteMemoryRoot();
    }
    //这是点击是否允许运营上缓存的方法
    @OnClick(R.id.rl_setting_allowcache)
    public void allowCache(){
        JLog.d("是否允许运营上缓存被点击了");
        CLICKNUM_ALLOWCACHE=!CLICKNUM_ALLOWCACHE;
        refreshCheckAllowcacheBtn();

    }

    //这是点击是否允许运营上缓存的方法
    @OnClick(R.id.rl_cache_path)
    public void changCachePath(){
        JLog.d("修改缓存路径");
        showChangeCachePathDialog();
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(AppGlobalConsts.EventType.CHANGE_PATH)})
    public void cachePathSelected(ChangeCachePathDialog.PathData data) {
        cache_path.setText(data.pathType == 0 ? "手机存储" : "SD卡");
    }


    ChangeCachePathDialog changeCachePathDialog;
    private void showChangeCachePathDialog() {
        InternalExternalPathInfo internalExternalPathInfo = CommonUtils
                .getInternalExternalPath(getContext());
        if (internalExternalPathInfo.emulatedSDcard == null && internalExternalPathInfo.removableSDcard == null) {
            return;
        }
        if (changeCachePathDialog == null)
            changeCachePathDialog = new ChangeCachePathDialog(getActivity(), R.style.ExitDialog);
        changeCachePathDialog.show();
    }

    //这是点击是否允许接收推送消息的方法
    @OnClick(R.id.rl_setting_messeage)
    public void allowInfo(){
        JLog.d("是否允许接收推送消息被点击了");
        CLICKNUM_INFO =  !CLICKNUM_INFO;
        refreshCheckInfoBtn();

    }
    //这是点击检测新版本的方法
    @OnClick(R.id.rl_setting_version)
    public void detectionVersion(){
          //  getAppNewVersion();
        new UpdateUtil().checkUpdate(getContext(),true);
    }
    //这是点击给我评分的方法
    @OnClick(R.id.rl_setting_grade)
    public void clickGrade(){
        openScoreActivity();
    }
    //这是点击版权申明的方法
    @OnClick(R.id.rl_setting_copyrightnotice)
    public void copyrightNotice(){
       enterNext("005");
    }

    @OnClick(R.id.rl_setting_aboutus)
    public void aboutUs(){
        enterNext("006");
    }


    private void enterNext(String id) {
        BusProvider.getInstance().post("setting",id);
    }

    @Override
    public void onResume() {
        AppGlobalConsts.TITLE_TXT=4;
        ((DragSettingActivity)getActivity()).refreshToolbar();
        if(ACache.get(getContext()).getAsObject("currentSelect") == null){
            currentSelect = 0;
        }else{
            currentSelect = (int) BaseApp.getACache().getAsObject("currentSelect");
        }
        refreshdefi(currentSelect);
        super.onResume();
    }

    private void deleteMemoryRoot() {
        Glide.get(getContext()).clearMemory();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                deleteFileExceptCache(new File(
                        AppGlobalConsts.USER_ALL_SDCARD_FOLDER));
//                deleteFileExceptCache();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                handler.sendEmptyMessage(MSG_CLEAN_OVER);
                super.onPostExecute(result);
            }
        }.execute();
    }

  /**
     * 清除缓存的方法
     * @param file
     */
    public void deleteFileExceptCache(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory() && !file.getName().equals("cache")) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    this.deleteFileExceptCache(files[i]);
                }
            }
            file.delete();
        }
        Glide.get(getContext()).clearDiskCache();
    }
  /* *//**
     * 清除缓存的方法
     *//*
    public void deleteFileExceptCache() {
        Glide.get(getContext()).clearDiskCache();
    }*/

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_CLEAN_OVER:
                    Toast.makeText(getContext(), "清理成功！", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case MSG_CLEAN_ERROR:
                    Toast.makeText(getContext(), "清理出错！", Toast.LENGTH_SHORT)
                            .show();
                    break;
                default:
                    break;
            }
        };
    };

    /**
     * 打开应用市场
     */
    private void openScoreActivity() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + this.getActivity().getPackageName()));
            startActivity(intent);
        } catch (ActivityNotFoundException E) {
            Toast.makeText(this.getContext(), "亲，您还没有安装应用市场哦！",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取新版本信息的方法
     * @return
     */
    public void getAppNewVersion() {
            presenter.getVersionData();
    }

    public void refreshdefi(int currentSelect){
        switch (currentSelect){
            case 0:
                tv_setting_define.setText("标清");
                break;
            case 1:
                tv_setting_define.setText("高清");
                break;
            case 2:
                tv_setting_define.setText("超清");
                break;
        }
    }
    public void starAppDownloadService() {
        Intent intent = new Intent(getActivity(), AppUpdateService.class);
        intent.putExtra("url", versionData.downLoadUrl);
        intent.putExtra("name", "电视粉");
        getActivity().startService(intent);
    }


    public void showNewVersionDialog(Context context, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("现在更新",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        starAppDownloadService();
                        Toast.makeText(getContext(),"新版本已经开始下载，您可在通知栏观看下载进度",Toast.LENGTH_SHORT).show();
                    }

                });
        builder.setNegativeButton("稍后再说",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }


    @Override
    public void showUpdateView(int errCode, String msg, VersionData version) {
        if (errCode == AppGlobalConsts.SERVER_CODE_OK
                && !TextUtils.isEmpty(version.versionId)) {
            versionData = version;
            showNewVersionDialog(getContext(), versionData.versionId, versionData.info);
        } else {
            Toast.makeText(getContext(),"当前已经是最新版本了",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getInstance().unregister(this);
    }
}

