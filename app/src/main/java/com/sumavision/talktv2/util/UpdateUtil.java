package com.sumavision.talktv2.util;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.jiongbull.jlog.JLog;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.DownloadModel;
import com.sumavision.talktv2.model.entity.UpdateBean;
import com.sumavision.talktv2.model.impl.DownloadModelImpl;
import com.sumavision.talktv2.ui.listener.DownloadProgressListener;

import java.io.File;

/**
 * Created by sharpay on 16-7-5.
 */
public class UpdateUtil {
    DownloadModel model;
    int currentCode;
    UpdateBean updateInfo;
    PackageInfo packageInfo;
    Context mContext;
    public UpdateUtil() {
        model = new DownloadModelImpl();
    }
    private AlertDialog myDialog = null;
    public void checkUpdate(final Context mContext, final boolean isTip) {
        this.mContext = mContext;
        model.updteData(new CallBackListener<UpdateBean>() {
            @Override
            public void onSuccess(UpdateBean bean) {
                //处理一下升级版本首页错乱的问题
//                BaseApp.getACache().remove("recommendList");
                updateInfo = bean;
                try {
                    packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), PackageManager.GET_CONFIGURATIONS);
                    int versionInfo = packageInfo.versionCode;
                    currentCode = updateInfo.getVersionCode();
                    if (versionInfo < currentCode) {
                        JLog.e("获取更新信息成功");
                        if (updateInfo.getForceupdate().equals("1")) {  //强制更新
                            myDialog = new AlertDialog.Builder(mContext, R.style.Dialog).create();
                            myDialog.show();
                            myDialog.setCanceledOnTouchOutside(false);
                            myDialog.getWindow().setContentView(R.layout.update_dialog);
                            for (String note : updateInfo.getRelease()) {
                                TextView tv = new TextView(mContext);
                                tv.setText(note);
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                layoutParams.setMargins(CommonUtil.dip2px(mContext, 20), 0, CommonUtil.dip2px(mContext, 20), CommonUtil.dip2px(mContext, 3));
                                tv.setTextColor(mContext.getResources().getColor(R.color.update_txt));
                                tv.setTextSize(15);
                                tv.setLayoutParams(layoutParams);
                                ((LinearLayout) myDialog.getWindow().findViewById(R.id.content)).addView(tv);
                            }
                            (myDialog.getWindow().findViewById(R.id.update_force)).setVisibility(View.VISIBLE);
                            ((TextView) myDialog.getWindow().findViewById(R.id.version)).setText(updateInfo.getVersionName() + "版本上线");
                            myDialog.getWindow().findViewById(R.id.updateButton).setOnClickListener(
                                    new NoDoubleClickListener() {

                                        @Override
                                        public void onNoDoubleClick(View view) {
                                            myDialog.dismiss();
                                            immediateDownloadApk(mContext);
                                        }
                                    });
                            myDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

                                @Override
                                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                                        dialog.dismiss();
                                        if (updateInfo.getForceupdate().equals("1")) {
                                            ((Activity) mContext).finish();
                                        }
                                    }
                                    return true;
                                }
                            });
                        } else if(isTip == true){
                            final File outputAPK = new File(Environment.getExternalStoragePublicDirectory
                                    (Environment.DIRECTORY_DOWNLOADS), "update" + currentCode + ".apk");
                            if (outputAPK.exists()) {
                                myDialog = new AlertDialog.Builder(mContext, R.style.Dialog).create();
                                myDialog.show();
                                myDialog.setCanceledOnTouchOutside(false);
                                myDialog.getWindow().setContentView(R.layout.update_dialog);
                                for (String note : updateInfo.getRelease()) {
                                    TextView tv = new TextView(mContext);
                                    tv.setText(note);
                                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    layoutParams.setMargins(CommonUtil.dip2px(mContext, 20), 0, CommonUtil.dip2px(mContext, 20), CommonUtil.dip2px(mContext, 3));
                                    tv.setTextColor(mContext.getResources().getColor(R.color.update_txt));
                                    tv.setTextSize(15);
                                    tv.setLayoutParams(layoutParams);
                                    ((LinearLayout) myDialog.getWindow().findViewById(R.id.content)).addView(tv);
                                }
                                (myDialog.getWindow().findViewById(R.id.update_no_force)).setVisibility(View.VISIBLE);
                                ((TextView) myDialog.getWindow().findViewById(R.id.version)).setText(updateInfo.getVersionName() + "版本上线");
                                myDialog.getWindow().findViewById(R.id.negativeButton).setOnClickListener(
                                        new NoDoubleClickListener() {
                                            @Override
                                            public void onNoDoubleClick(View v) {
                                                myDialog.dismiss();
                                            }
                                        }
                                );
                                myDialog.getWindow().findViewById(R.id.positiveButton).setOnClickListener(
                                        new NoDoubleClickListener() {
                                            @Override
                                            public void onNoDoubleClick(View v) {
                                                myDialog.dismiss();
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                                intent.setDataAndType(Uri.fromFile(outputAPK),
                                                        "application/vnd.android.package-archive");
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                mContext.startActivity(intent);
                                                // 会返回结果,回调方法onActivityResult
                                                android.os.Process.killProcess(android.os.Process.myPid());
                                            }
                                        }
                                );
                                myDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

                                    @Override
                                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                                            dialog.dismiss();
                                            if (updateInfo.getForceupdate().equals("1")) {
                                                ((Activity) mContext).finish();
                                            }
                                        }
                                        return true;
                                    }
                                });
                            } else {
                                Toast.makeText(mContext,"正在下载，请稍后。。。",Toast.LENGTH_LONG).show();
                            }
                        }else {//静默更新
                            final File outputAPK = new File(Environment.getExternalStoragePublicDirectory
                                    (Environment.DIRECTORY_DOWNLOADS), "update" + currentCode + ".apk");
                            if (outputAPK.exists()) {
                                myDialog = new AlertDialog.Builder(mContext, R.style.Dialog).create();
                                myDialog.show();
                                myDialog.setCanceledOnTouchOutside(false);
                                myDialog.getWindow().setContentView(R.layout.update_dialog);
                                for (String note : updateInfo.getRelease()) {
                                    TextView tv = new TextView(mContext);
                                    tv.setText(note);
                                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    layoutParams.setMargins(CommonUtil.dip2px(mContext, 20), 0, CommonUtil.dip2px(mContext, 20), CommonUtil.dip2px(mContext, 3));
                                    tv.setTextColor(mContext.getResources().getColor(R.color.update_txt));
                                    tv.setTextSize(15);
                                    tv.setLayoutParams(layoutParams);
                                    ((LinearLayout) myDialog.getWindow().findViewById(R.id.content)).addView(tv);
                                }
                                (myDialog.getWindow().findViewById(R.id.update_no_force)).setVisibility(View.VISIBLE);
                                ((TextView) myDialog.getWindow().findViewById(R.id.version)).setText(updateInfo.getVersionName() + "版本上线");
                                myDialog.getWindow().findViewById(R.id.negativeButton).setOnClickListener(
                                        new NoDoubleClickListener() {
                                            @Override
                                            public void onNoDoubleClick(View v) {
                                                myDialog.dismiss();
                                            }
                                        }
                                );
                                myDialog.getWindow().findViewById(R.id.positiveButton).setOnClickListener(
                                        new NoDoubleClickListener() {
                                            @Override
                                            public void onNoDoubleClick(View v) {
                                                myDialog.dismiss();
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                                intent.setDataAndType(Uri.fromFile(outputAPK),
                                                        "application/vnd.android.package-archive");
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                mContext.startActivity(intent);
                                                // 会返回结果,回调方法onActivityResult
                                                android.os.Process.killProcess(android.os.Process.myPid());
                                            }
                                        }
                                );
                                myDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

                                    @Override
                                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                                            dialog.dismiss();
                                            if (updateInfo.getForceupdate().equals("1")) {
                                                ((Activity) mContext).finish();
                                            }
                                        }
                                        return true;
                                    }
                                });
                            } else {
                                silenceDownloadApk();
                            }


                        }
                    } else {
                        if(isTip){
                            Toast.makeText(mContext, "当前已是最新版本！V "+packageInfo.versionName, Toast.LENGTH_SHORT).show();
                            JLog.e("当前版本不需要更新");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                JLog.e(throwable.toString());
            }
        });
    }

    public void silenceDownloadApk(){
        model.release();
        final boolean[] isComplete = {false};
        final File outputAPK = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS), "update.apk");
        model.download(new CallBackListener() {
            @Override
            public void onSuccess(Object o) {
                if(isComplete[0] == true) {
                    final File newFile = new File(Environment.getExternalStoragePublicDirectory
                            (Environment.DIRECTORY_DOWNLOADS), "update" + currentCode + ".apk");
                    outputAPK.renameTo(newFile);
                }else{
                    outputAPK.delete();
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
            }
        }, updateInfo.getDownloadUrl(), outputAPK, new DownloadProgressListener() {
            @Override
            public void update(long bytesRead, long contentLength, boolean done) {
                isComplete[0] = done;
            }
        });
    }
    public void immediateDownloadApk(final Context mContext){
        final boolean[] isComplete = {false};
        final File tmpAPK = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS), "update"+currentCode+".apk");

        if(tmpAPK.exists()){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setDataAndType(Uri.fromFile(tmpAPK),
                    "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            // 会返回结果,回调方法onActivityResult
            android.os.Process.killProcess(android.os.Process.myPid());
            return;
        }

        final File outputAPK = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS), "update.apk");
        model.release();
        initNotification();
        model.download(new CallBackListener() {
            @Override
            public void onSuccess(Object o) {
                if(isComplete[0]){
                    final File newFile= new File(Environment.getExternalStoragePublicDirectory
                            (Environment.DIRECTORY_DOWNLOADS), "update"+currentCode+".apk");
                    outputAPK.renameTo(newFile);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setDataAndType(Uri.fromFile(newFile),
                            "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    // 会返回结果,回调方法onActivityResult
                    android.os.Process.killProcess(android.os.Process.myPid());
                }else{
                    outputAPK.delete();
                }

            }

            @Override
            public void onFailure(Throwable throwable) {
                outputAPK.delete();
                Toast.makeText(mContext, "文件下载失败", Toast.LENGTH_SHORT).show();
            }
        }, updateInfo.getDownloadUrl(), outputAPK, new DownloadProgressListener() {
            @Override
            public void update(long current, long total, boolean done) {
                isComplete[0] = done;
               /* Notification notification = null;
                NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                notification = new Notification(R.mipmap.logo, "下载", System.currentTimeMillis());
                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                RemoteViews view = new RemoteViews(mContext.getPackageName(), R.layout.load_process);
                notification.contentView = view;
                notification.contentView.setImageViewResource(R.id.noti_img, R.mipmap.logo);
                PendingIntent contentIntent = PendingIntent.getActivity(mContext,
                        R.string.app_name, new Intent(),PendingIntent.FLAG_UPDATE_CURRENT);
                notification.contentIntent = contentIntent;*/

                if(total != current){
                  /*  notification.contentView.setTextViewText(R.id.noti_tv, "正在下载  "+current*100/total + "%");
                    notification.contentView.setProgressBar(R.id.noti_pd, 100, (int) (current*100/total), false);
                    notificationManager.notify(0, notification);*/
                    Message msg = handler.obtainMessage();
                    msg.what = 1;
                    msg.arg1 = (int) (current*100/total);
                    handler.sendMessage(msg);
                }
                else{
                    Message msg = handler.obtainMessage();
                    msg.what = 0;
                    handler.sendMessage(msg);
                }
            }
        });
    }

    private NotificationManager mNotificationManager;
    private Notification mNotification;
    private static final int NOTIFY_ID = 0;
    public void initNotification(){
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        int icon = R.mipmap.logo;
        CharSequence tickerText = "正在下载";
        long when = System.currentTimeMillis();
        mNotification = new Notification(icon, tickerText, when);

        // 放置在"正在运行"栏目中
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;

        RemoteViews contentView = new RemoteViews(mContext.getPackageName(), R.layout.load_process);
        contentView.setTextViewText(R.id.noti_tv, "电视粉.apk");
        // 指定个性化视图
        mNotification.contentView = contentView;
        mNotification.contentView.setImageViewResource(R.id.noti_img, R.mipmap.logo);
        // intent为null,表示点击通知时不跳转
        PendingIntent contentIntent = PendingIntent.getActivity(mContext,
                R.string.app_name, new Intent(),PendingIntent.FLAG_UPDATE_CURRENT);
        mNotification.contentIntent = contentIntent;

        mNotificationManager.notify(NOTIFY_ID, mNotification);
    }
    int lastNum;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if(lastNum == msg.arg1){
                return;
            }
            lastNum= msg.arg1;
            switch (msg.what) {
                case 1:
                    int rate = msg.arg1;
                    if (rate < 100) {
                        // 更新进度
                        RemoteViews contentView = mNotification.contentView;
                        contentView.setTextViewText(R.id.noti_tv, rate + "%");
                        contentView.setProgressBar(R.id.noti_pd, 100, rate, false);
                    } else {
                        // 下载完毕后变换通知形式
                        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
                    }

                    // 最后别忘了通知一下,否则不会更新
                    mNotificationManager.notify(NOTIFY_ID, mNotification);
                    break;
                case 0:
                    // 取消通知
                    mNotificationManager.cancel(NOTIFY_ID);
                    break;
            }
        };
    };
}
