package com.sumavision.talktv2.ui.services;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.sumavision.talktv2.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by zhoutao on 2016/6/20.
 */
public class AppUpdateService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private NotificationManager notificationManager;
    public static final int NOTIFICATION_ID_NEW_VERSION = 3001;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        final String url = intent.getStringExtra("url");
        new Thread() {
            @Override
            public void run() {
                downLoadFile(url);
            }
        }.start();
        handler.sendEmptyMessage(MESSAGE_NEW_PROGRESS);
        return super.onStartCommand(intent, flags, startId);
    }

    private static final int MESSAGE_OVER = 1;
    private static final int MESSAGE_NEW_PROGRESS = 2;
    private static final int MESSAGE_DOWN_OVER = 3;
    private static final int MESSAGE_DOWN_ERR = 4;
    private static final int MESSAGE_NO_SDCARD = 5;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_OVER:
                    close();
                    break;
                case MESSAGE_DOWN_OVER:
                    // TODO 用不上了
                    break;
                case MESSAGE_DOWN_ERR:
                    handler.sendEmptyMessage(MESSAGE_OVER);
                    break;
                case MESSAGE_NEW_PROGRESS:
                    if (downSize != lastDownSize) {
                        lastDownSize = downSize;
                        int percent = lastDownSize * 100 / fileSize;
                        notifyProgress(lastDownSize, String.valueOf(percent) + "%");
                    }
                    handler.sendEmptyMessageDelayed(MESSAGE_NEW_PROGRESS, 1000);
                    break;
                case MESSAGE_NO_SDCARD:
                    Toast.makeText(AppUpdateService.this,
                            "SD卡不存在，无法更新程序，请插入SD卡后重试", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        };
    };

    private void close() {
        handler.removeMessages(MESSAGE_NEW_PROGRESS);
        stopSelf();
        Log.e(TAG, "appUpdateService finish");
    }

    private final static String TAG = "AppUpdateService";

    private HttpURLConnection conn;
    private int fileSize;
    private InputStream is;
    private FileOutputStream fos;
    private boolean isCancelDownload;
    private int downSize = 0;
    private int lastDownSize;

    private String folderPath;
    private String fileName = "TalkTV-update.apk";

    protected void downLoadFile(String httpUrl) {
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
        if (!sdCardExist) {
            handler.sendEmptyMessage(MESSAGE_NO_SDCARD);
            handler.sendEmptyMessage(MESSAGE_OVER);
        } else {

            folderPath = Environment.getExternalStorageDirectory()
                    + File.separator + "TVFan" + File.separator;

            File tmpFile = new File(folderPath);
            if (!tmpFile.exists()) {
                tmpFile.mkdir();
            }

            final File file = new File(folderPath + fileName);

            try {
                URL url = new URL(httpUrl);
                try {
                    conn = (HttpURLConnection) url.openConnection();
                    fileSize = conn.getContentLength();
                    byte[] buf = new byte[1024];
                    conn.setReadTimeout(5000);
                    conn.setConnectTimeout(5000);
                    is = conn.getInputStream();
                    fos = new FileOutputStream(file);
                    int status = conn.getResponseCode();
                    if (status != 200) {
                        handler.sendEmptyMessage(MESSAGE_DOWN_ERR);
                    } else {
                        while (true) {
                            if (!isCancelDownload) {
                                int numRead = is.read(buf);
                                if (numRead <= 0) {
                                    break;
                                } else {
                                    downSize = downSize + numRead;
                                    fos.write(buf, 0, numRead);
                                }
                            }

                        }
                    }
                    conn.disconnect();
                    fos.close();
                    is.close();

                } catch (IOException e) {
                    handler.sendEmptyMessage(MESSAGE_DOWN_ERR);
                } catch (NullPointerException e) {
                    handler.sendEmptyMessage(MESSAGE_DOWN_ERR);
                }
            } catch (MalformedURLException e) {
                handler.sendEmptyMessage(MESSAGE_DOWN_ERR);
            }
        }
    }

    private void notifyProgress(int progressValue, String percent) {
        boolean isNeedOver = false;
        int icon;
        long when = System.currentTimeMillis();
        String title;
        RemoteViews contentView = new RemoteViews(getPackageName(),
                R.layout.notification_app_view);
        Intent intent = new Intent();
        if (progressValue != fileSize) {
            icon = android.R.drawable.stat_sys_download;
            title = "下载中";
            contentView.setProgressBar(R.id.progressBar, fileSize,
                    progressValue, false);
            contentView.setTextViewText(R.id.err_text, percent);
        } else {
            icon = android.R.drawable.stat_sys_download_done;
            title = "电视粉新版本下载完成";
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(android.content.Intent.ACTION_VIEW);
            File file = new File(folderPath, fileName);
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
            // startActivity(intent);
            contentView.setTextViewText(R.id.err_text, "下载已完成，点击进行安装");
            contentView.setViewVisibility(R.id.progressBar, View.GONE);
            isNeedOver = true;
        }
        Notification notification = new Notification(icon, title, when);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, 0);
        if (isNeedOver) {
            notification.defaults = Notification.DEFAULT_VIBRATE;
        }
        notification.contentIntent = contentIntent;
        notification.contentView = contentView;
        notificationManager.notify(NOTIFICATION_ID_NEW_VERSION, notification);
        if (isNeedOver) {
            handler.sendEmptyMessage(MESSAGE_OVER);
        }
    }
}
