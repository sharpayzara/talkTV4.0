package com.sumavision.talktv2.util;

import android.text.TextUtils;

import com.sumavision.talktv2.model.entity.decor.LiveDetailData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by zjx on 2016/6/23.
 */
public class DateUtils {

    private static String mWay;

    public static final String DATE_STYLE = "yyyy/MM/dd-HH:mm:ss";

    public static final String DATE_STYLE2 = "yyyy/MM/dd-HH:mm ";

    public static final String DATE_DB_STYLE = "yyyyMMddHHmmssSSS";

    public static final String DATE_UI_STYLE = "yyyy-MM-dd HH:mm";

    public static final String DATE_STYLE3 = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_PORTAL_STYLE = "yyyyMMddHHmmss";

    public static final String DATE_EPG_STYLE = "HH：mm";

    public static final String DATE_PHOTO_STYLE = "yyyyMMdd_HHmmssSSS";

    public static final String DATE_SEARCH_STYLE = "HH:mm MM/dd ";

    public static final String DATE_SEARCH_DF_STYLE = "yyyy/MM/dd/ HH:mm";

    public static final String DATE_NOTIFY_STYLE = " MM/dd HH:mm ";

    public static final String DATE_GATE_EPG_STYLE = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_GATE_EPG_STYLE2 = "yyyy-MM-dd";

    public static final String DATE_GATE_EPG_STYLE3 = "HH:mm:ss";

    public static long timeOffset;

    public static long getServerCurrentTime() {
        return System.currentTimeMillis() + timeOffset;
    }

    public static long getServerTime() {
        return System.currentTimeMillis() + timeOffset;
    }

    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public static long getCurrentTimeByUTC() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        // 取得时间偏移量
        int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
        // 取得夏令时差
        int dstOffset = cal.get(Calendar.DST_OFFSET);
        cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        return cal.getTimeInMillis();
    }

    public static Date getDate(String time, String style) {
        if (style == null) {
            style = DATE_DB_STYLE;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(style);
        try {
            return formatter.parse(time);
        } catch (Exception e) {
            return null;
        }

    }

    public static String parseTime(long time, String style) {
        Date date = new Date(time);
        if (style == null) {
            style = DATE_DB_STYLE;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(style);
        String day = formatter.format(date);
        return day;
    }

    public static String getNowTime(String style) {
        if (style == null) {
            style = DATE_DB_STYLE;
        }
        Date date = new Date(getServerTime());
        SimpleDateFormat formatter = new SimpleDateFormat(style);
        String day = formatter.format(date);
        return day;
    }

    public static String getFormatTime(Date date, String style) {
        if (style == null) {
            style = DATE_DB_STYLE;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(style);
        String day = formatter.format(date);
        return day;
    }

    public static boolean isOutOfDate(long myTime, long start, long end) {
        if (start < myTime && myTime < end) {
            return true;
        } else {
            return false;
        }
    }

    public static String parseTime(String time, String style) {
        if (!TextUtils.isEmpty(time)) {
            if (DATE_EPG_STYLE.equals(style)) {
                if (time.length() == 14) {
                    return (time.substring(8, 10) + ":" + time.substring(10, 12));
                }
            } else if (DATE_STYLE2.equals(style)) {
                if (time.length() == 14) {
                    return (time.substring(0, 4) + "/"
                            + time.substring(4, 6)) + "/"
                            + time.substring(6, 8) + "/"
                            + " "
                            + time.substring(8, 10) + ":" + time.substring(10, 12);
                }
            }
        }
        return null;
    }

    public static String stringForTime(long timeMs) {
        int totalSeconds = (int) (timeMs / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        Formatter mFormatter = new Formatter();
        StringBuffer mFormatBuilder = new StringBuffer();
        mFormatBuilder.setLength(0);
        return mFormatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString();
    }

    public static int getSqeu(String start, String end) {
//		String start = "20140714120000";
//		String end = "20140714130000";
        Date startDate = getDate(start, DATE_PORTAL_STYLE);
        Date endDate = getDate(end, DATE_PORTAL_STYLE);
        return (int) (endDate.getTime() - startDate.getTime());
    }

    public static int getLongTime(String time) {
        Date timeDate = getDate(time, DATE_PORTAL_STYLE);
        return (int) timeDate.getTime();
    }

    public static String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        return  year+"-"+month+"-"+day+" ";
    }

    public static String StringData(){
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
//        mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
//        mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
//        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if("1".equals(mWay)){
//            mWay ="天";
            mWay ="6";
        }else if("2".equals(mWay)){
//            mWay ="一";
            mWay ="0";
        }else if("3".equals(mWay)){
//            mWay ="二";
            mWay ="1";
        }else if("4".equals(mWay)){
//            mWay ="三";
            mWay ="2";
        }else if("5".equals(mWay)){
//            mWay ="四";
            mWay ="3";
        }else if("6".equals(mWay)){
//            mWay ="五";
            mWay ="4";
        }else if("7".equals(mWay)){
//            mWay ="六";
            mWay ="5";
        }
//        return mYear + "年" + mMonth + "月" + mDay+"日"+"/星期"+mWay;
        return mWay;
    }

    /**
     * 这是获取当前播放节目对应位置的方法
     * @param
     * @return
     */
    public static int getmCurrentIndex(List<LiveDetailData.ContentBean.DayBean.ProgramBean> programBeens) {

        int mCurrentIndex = 0;
        for (int i=0; i<programBeens.size(); i++) {
            LiveDetailData.ContentBean.DayBean.ProgramBean programBeen = programBeens.get(i);
            long serverTime = DateUtils.getServerTime();
            String cpTime = programBeen.getCpTime();
            String endime = programBeen.getEndTime();
            long startTime = DateUtils.getDate(getCurrentDate()+cpTime, DateUtils.DATE_UI_STYLE).getTime();
            long endTime = DateUtils.getDate(getCurrentDate()+endime, DateUtils.DATE_UI_STYLE).getTime();
            if (startTime < serverTime && endTime > serverTime) {
               /* if (DateUtils.getDate(programBeen.getEndTime(), DateUtils.DATE_EPG_STYLE).getTime() > DateUtils .getServerTime()) {
                } else {
                    String mCurrentProgram = programBeen.getProgramName();
                    String  mCurrentInterval = DateUtils.parseTime(
                            programBeen.getCpTime(),
                            DateUtils.DATE_EPG_STYLE) + " - "  + DateUtils.parseTime(programBeen.getEndTime(), DateUtils.DATE_EPG_STYLE);

                }*/
                mCurrentIndex = i;
                break;
            }
        }
        return mCurrentIndex;
    }


    public static String formatTime(int second) {
        if (second < 0) {
            second = 0;
        }
        int hh = second / 3600;
        int mm = second % 3600 / 60;
        int ss = second % 60;
        String strTemp = null;
        if (0 != hh) {
            strTemp = String.format("%02d:%02d:%02d", hh, mm, ss);
        } else {
            strTemp = String.format("%02d:%02d", mm, ss);
        }
        return strTemp;
    }
}
