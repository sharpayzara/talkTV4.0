package com.sumavision.talktv2.util;

import android.os.Environment;

/**
 *  desc  全局常量
 *  @author  yangjh
 *  created at  16-5-23 上午10:21 
 */
public class AppGlobalConsts {
    public static boolean ISSEARCHBACK  = false;
    public static boolean ISFROMGRID  = false;
    public static boolean ISLOGINDUIBA= false;
    public static boolean ISFROMDUIBATOLOGIN= false;
    public static String newDuibaUrl= null;
    public static class EnterType{

        public static final String ENTERSHEZHI = "entershezhi";
        public static final String ENTERXIAOXI = "enterxiaoxi";
        public static final String ENTERHUANCUN = "enterhuancun";
        public static final String ENTERTONGGAO = "entertonggao";
        public static final String ENTERDINGZHI= "enterdingzhi";
        public static final String ENTERGRID= "entergrid";
        public static final String ENTERList= "enterlist";
    }
    public static final int MEIZI_SIZE = 20;
    public static final int ITEMPOSITION = 20160718;
    public static  boolean EDITSTATE = true;
    public static  boolean NEEDEXITAPP = false;
    public static  boolean ISFROMHOME = false;
    public final class EventType {
        public final static String TAG_A = "tag_a";
        public final static String TAG_B = "tag_b";
        public final static String TAG_C = "tag_c";
        public final static String TAG_D = "tag_d";
        public final static String TAG_E = "tag_e";
        public final static String TAG_F = "tag_f";
        public final static String TAG_PAUSE = "tag_pause";
        public final static String TAG_START = "tag_start";
        public final static String TAG_SEARCH = "tag_search";
        public final static String TAG_ENTER = "tag_enter" ;
        public final static String TAG_ENTERDUIBA = "tag_enter_duiba" ;
        public final static String TAG_CARDID = "tag_cardid" ;
        public final static String TAG_CACHE = "tag_cache";
        public final static String TAG_CACHE_CANCEL = "tag_cache_cancel";
        public final static String TAG_CACHE_SHOW_EDIT = "tag_cache_show_edit";
        public static final String CHANGE_SELECT_ALL_STATE = "change_select_all_state";
        public static final String CHANGE_PATH = "change_path";
    }

    public final class ResultCode{
        public final static int USER_CODE = 100;
        public final static int PREFERENCE_CODE = 101;
        public final static int PIC_FROM_CAMERA = 102;
        public final static int PIC_FROM_LOCALPHOTO = 103;
        public final static int PIC_FROM_CROP = 104;
    }
    // 主目录
    public static String USER_ALL_SDCARD_FOLDER = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/TVFan/";
    //作为标记当前toolbar显示的常量
    public static  int TITLE_TXT = 0;

    public static final int SERVER_CODE_OK = 0;
    public static final int SERVER_CODE_ERROR = 1;
    public static boolean ISEDIT=false;
    public static boolean ISEDITSTATE=false;


    public static final String APP_KEY      = "2064721383";
    public static final String REDIRECT_URL = "http://www.tvfan.cn";
    public static final String SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";
    public final class PlatCode{
        public static final String WEIXIN_APPID = "wxcfaa020ee248a2f2";
       // public static final String WEIXIN_APPID = "wxb4ba3c02aa476ea1";  //测试
    }
}

