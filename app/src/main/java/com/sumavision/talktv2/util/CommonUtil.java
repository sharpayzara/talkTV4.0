package com.sumavision.talktv2.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 *  desc  通用的工具类
 *  @author  yangjh
 *  created at  16-5-18 下午4:15
 */
public class CommonUtil {

    /**
     * 获取屏幕的高度
     */
    public static int screenHeight(Context context){
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return  dm.heightPixels;
    }

    /**
     * 获取屏幕的宽度
     */
    public static int screenWidth(Context context){
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return  dm.widthPixels;
    }

    /**
     * 获取顶部状态栏的高度
     */
    public static int getStatueHeight(Activity activity){
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        // 状态栏高度
        return frame.top;
    }
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float countale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * countale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float countale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / countale + 0.5f);
    }


    /**
     * 读取当前网速
     */
    public static long getNetSpeed() {
        ProcessBuilder cmd;
        long readBytes = 0;
        BufferedReader rd = null;
        try {
            String[] args = { "/system/bin/cat", "/proc/net/dev" };
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            rd = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                if (line.contains("lan0") || line.contains("eth0")) {
                    String[] delim = line.split(":");
                    if (delim.length >= 2) {
                        readBytes = parserNumber(delim[1].trim());
                        break;
                    }
                }
            }
            rd.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rd != null) {
                try {
                    rd.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return readBytes;
    }


    private static long parserNumber(String line) throws Exception {
        long ret = 0;
        if (!TextUtils.isEmpty(line)) {
            String[] delim = line.split(" ");
            if (delim.length >= 1) {
                ret = Long.parseLong(delim[0]);
            }
        }
        return ret;
    }

    @SuppressWarnings("deprecation")
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory(); // 获取数据目录
        StatFs stat = new StatFs(path.getPath());

        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    @SuppressWarnings("deprecation")
    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    @SuppressWarnings("deprecation")
    public static long getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            return -1;
        }
    }

    @SuppressWarnings("deprecation")
    public static long getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        } else {
            return -1;
        }
    }


    public static boolean hasAvailableSpace() {
        // 内部空间小于30M时，无法安装电视粉
        if (CommonUtil.getAvailableInternalMemorySize() / (1024 * 1024) < 350)
            return false;
        else
            return true;
    }

    // 解析字符串为时间
    public static int parserString2TimeStamp(String str) {
        int totalSec = 0;
        if (str.contains(":")) {
            String[] my = str.split(":");
            int hour = Integer.parseInt(my[0]);
            int min = Integer.parseInt(my[1]);
            int sec = Integer.parseInt(my[2]);
            totalSec = hour * 3600 + min * 60 + sec;
            totalSec = totalSec * 1000;
        }
        return totalSec;
    }

    public static final String spic = "s.jpg";
    public static final String mpic = "m.jpg";
    public static final String xpic = "b.jpg";

    public static String getUrl(Context context, String path) {
        final float countale = context.getResources().getDisplayMetrics().density;
        if (countale >= 2.0) {
            return path += xpic;
        } else if (countale >= 1.5) {
            return path += mpic;
        } else {
            return path += spic;
        }
    }

    public static void showMethodLog(String name){
        Log.e("msg_method", name);
    }
    public static String getImagCacheDir(Context context) {
        if (CommonUtil.externalMemoryAvailable()) {
            String root = context.getExternalFilesDir(null) + "/";
            return root + "/TVFan/imageCache";
        }
        return "";
    }
    public static String getDateString(){
        String result = "20150513";
        result = new SimpleDateFormat("yyyyMMdd").format(new Date());
        return result;
    }

    /**
     * 秒转时分秒
     * @param time
     * @return
     */
    public static String getNewDataString(int time){
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }
    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }
    //List深度复制
    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }
    //获取当前进程名
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
    public static String getAppVersionName(Context context){
        String result = "";
        PackageManager pm = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = pm.getPackageInfo(context.getPackageName(),0);
            result = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }
    public static int getAppVersionCode(Context context){
        int result = -1;
        PackageManager pm = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = pm.getPackageInfo(context.getPackageName(),0);
            result = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 集合中是否存在指定元素
     *
     * @param value 指定字符
     * @param list  集合
     * @return
     */
    public static boolean existValue(String value, List<String> list) {
        if (list == null || value == null) {
            return false;
        }
        for (String str : list) {
            if (value.equals(str)) {
                return true;
            }
        }
        return false;
    }

    public static void copyToClipBoard(Context context, String text, String success) {
        ClipData clipData = ClipData.newPlainText("girl_copy", text);
        ClipboardManager manager = (ClipboardManager) context.getSystemService(
                Context.CLIPBOARD_SERVICE);
        manager.setPrimaryClip(clipData);
        Toast.makeText(context, success, Toast.LENGTH_SHORT).show();
    }
    /**
     *  desc  判断字符的长度 汉字为2个字符，数字为一个
     */
    public static int stringLength(String value) {
        int valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (temp.matches(chinese)) {
                valueLength += 2;
            } else {
                valueLength += 1;
            }
        }
        return valueLength;
    }
    public static void addSearchListInfo(String currentStr, List<String> searchHisList, Context context) {

            if (searchHisList == null){
                searchHisList = new ArrayList<>();
            }
            //searchList里没有数据就直接存
            if (searchHisList.size() == 0) {
                searchHisList.add(currentStr);
            } else {
                if (!searchHisList.contains(currentStr)) {
                    searchHisList.add(currentStr);
                }
            }
            if (searchHisList.size()>6){
                searchHisList.remove(0);
            }
        if (currentStr.equals("delete")){
            for (int i= 0 ;i<searchHisList.size();i++){
                searchHisList.remove(i);
            }
        }
            SharedPreferences.Editor  editor =context.getSharedPreferences(
                    "searchHisList", Context.MODE_PRIVATE).edit();
            editor.putInt("searchNums", searchHisList.size());
            for (int i = 0; i < searchHisList.size(); i++) {
                editor.putString("item_" + i, searchHisList.get(i));
            }
            editor.commit();
    }

    public static ArrayList<String> getSearch(Context context) {
        SharedPreferences preferDataList =context.getSharedPreferences(
                "searchHisList", Context.MODE_PRIVATE);
        ArrayList<String> list = new ArrayList<>();

        int searchNums = preferDataList.getInt("searchNums", 0);
        for (int i = 0; i < searchNums; i++) {
            String searchItem = preferDataList.getString("item_" + i, null);
            list.add(searchItem);
        }
        return list;
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param fontScale（DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(float pxValue, float fontScale) {
        return (int) (pxValue / fontScale + 0.5f);
    }


    public static float getFontScale(Context context){
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        return dm.density;
    }

    /**
     * 检测网络是否可用
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }
    /**
     * 获取当前网络是否是wifi状态
     */
    public static boolean getNetWorkType(Context context) {
        boolean isWifi = false;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if (type.equalsIgnoreCase("WIFI")) {
                isWifi = true;
            } else {
                isWifi = false;
            }
        }
        return isWifi;
    }
    /**
     * 设置小米手机状态栏颜色自动改变
     * @param activity
     * @param darkmode
     * @return
     */
    public static boolean setMiuiStatusBarDarkMode(Activity activity, boolean darkmode) {
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置魅族手机状态栏颜色自动改变
     * @param activity
     * @param dark
     * @return
     */
    public static boolean setMeizuStatusBarDarkIcon(Activity activity, boolean dark) {
        boolean result = false;
        if (activity != null) {
            try {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                activity.getWindow().setAttributes(lp);
                result = true;
            } catch (Exception e) {
            }
        }
        return result;
    }

    /**
     * 状态栏高度算法
     * @param activity
     * @return
     */
    public static int getStatusHeight(Activity activity){
        int statusHeight = 0;
        Rect localRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight){
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = activity.getResources().getDimensionPixelSize(i5);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }
}
