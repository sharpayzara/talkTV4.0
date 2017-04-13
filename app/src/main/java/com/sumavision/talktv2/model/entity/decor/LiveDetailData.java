package com.sumavision.talktv2.model.entity.decor;

import java.io.Serializable;
import java.util.List;

/**
 * 直播详情的数据bean
 * Created by zjx on 2016/6/14.
 */
public class LiveDetailData extends BaseData implements Serializable {


    /**
     * code : 0
     * content : {"day":[{"count":20,"date":"2016-06-16","program":[{"cpId":39381275,"cpTime":"09:15","date":"2016-06-16","endTime":"10:10","programName":"精品剧场","week":"星期四"},{"cpId":39381276,"cpTime":"10:10","date":"2016-06-16","endTime":"11:05","programName":"精品剧场","week":"星期四"},{"cpId":39381277,"cpTime":"11:05","date":"2016-06-16","endTime":"12:00","programName":"精品剧场","week":"星期四"},{"cpId":39381278,"cpTime":"12:00","date":"2016-06-16","endTime":"12:12","programName":"都市快报","week":"星期四"},{"cpId":39381279,"cpTime":"12:12","date":"2016-06-16","endTime":"12:49","programName":"打鱼晒网","week":"星期四"},{"cpId":39381280,"cpTime":"12:49","date":"2016-06-16","endTime":"14:20","programName":"都市报道","week":"星期四"},{"cpId":39381281,"cpTime":"14:20","date":"2016-06-16","endTime":"15:09","programName":"精选剧场","week":"星期四"},{"cpId":39381282,"cpTime":"15:09","date":"2016-06-16","endTime":"15:58","programName":"精选剧场","week":"星期四"},{"cpId":39381283,"cpTime":"15:58","date":"2016-06-16","endTime":"16:48","programName":"精选剧场","week":"星期四"},{"cpId":39381284,"cpTime":"16:48","date":"2016-06-16","endTime":"17:00","programName":"都市快报","week":"星期四"},{"cpId":39381285,"cpTime":"17:00","date":"2016-06-16","endTime":"17:30","programName":"都市007","week":"星期四"},{"cpId":39381286,"cpTime":"17:30","date":"2016-06-16","endTime":"18:00","programName":"食客准备着","week":"星期四"},{"cpId":39381287,"cpTime":"18:00","date":"2016-06-16","endTime":"18:40","programName":"打鱼晒网","week":"星期四"},{"cpId":39381288,"cpTime":"18:40","date":"2016-06-16","endTime":"20:15","programName":"都市报道","week":"星期四"},{"cpId":39381289,"cpTime":"20:15","date":"2016-06-16","endTime":"20:56","programName":"都市剧场","week":"星期四"},{"cpId":39381290,"cpTime":"20:56","date":"2016-06-16","endTime":"21:38","programName":"都市剧场","week":"星期四"},{"cpId":39381291,"cpTime":"21:38","date":"2016-06-16","endTime":"22:20","programName":"都市剧场","week":"星期四"},{"cpId":39381292,"cpTime":"22:20","date":"2016-06-16","endTime":"22:51","programName":"都市007","week":"星期四"},{"cpId":39381293,"cpTime":"22:51","date":"2016-06-16","endTime":"23:22","programName":"食客准备着","week":"星期四"},{"cpId":39381294,"cpTime":"23:22","date":"2016-06-16","endTime":"23:59","programName":"双喜临门","week":"星期四"}],"week":"星期四"},{"count":23,"date":"2016-06-17","program":[{"cpId":39393599,"cpTime":"00:00","date":"2016-06-17","endTime":"07:30","programName":"双喜临门","week":"星期五"},{"cpId":39393600,"cpTime":"07:30","date":"2016-06-17","endTime":"08:21","programName":"都市报道","week":"星期五"},{"cpId":39393601,"cpTime":"08:21","date":"2016-06-17","endTime":"09:15","programName":"精品剧场","week":"星期五"},{"cpId":39393602,"cpTime":"09:15","date":"2016-06-17","endTime":"10:10","programName":"精品剧场","week":"星期五"},{"cpId":39393603,"cpTime":"10:10","date":"2016-06-17","endTime":"11:05","programName":"精品剧场","week":"星期五"},{"cpId":39393604,"cpTime":"11:05","date":"2016-06-17","endTime":"12:00","programName":"精品剧场","week":"星期五"},{"cpId":39393605,"cpTime":"12:00","date":"2016-06-17","endTime":"12:12","programName":"都市快报","week":"星期五"},{"cpId":39393606,"cpTime":"12:12","date":"2016-06-17","endTime":"12:49","programName":"打鱼晒网","week":"星期五"},{"cpId":39393607,"cpTime":"12:49","date":"2016-06-17","endTime":"14:20","programName":"都市报道","week":"星期五"},{"cpId":39393608,"cpTime":"14:20","date":"2016-06-17","endTime":"15:09","programName":"精选剧场","week":"星期五"},{"cpId":39393609,"cpTime":"15:09","date":"2016-06-17","endTime":"15:58","programName":"精选剧场","week":"星期五"},{"cpId":39393610,"cpTime":"15:58","date":"2016-06-17","endTime":"16:48","programName":"精选剧场","week":"星期五"},{"cpId":39393611,"cpTime":"16:48","date":"2016-06-17","endTime":"17:00","programName":"都市快报","week":"星期五"},{"cpId":39393612,"cpTime":"17:00","date":"2016-06-17","endTime":"17:30","programName":"都市007","week":"星期五"},{"cpId":39393613,"cpTime":"17:30","date":"2016-06-17","endTime":"18:00","programName":"食客准备着","week":"星期五"},{"cpId":39393614,"cpTime":"18:00","date":"2016-06-17","endTime":"18:40","programName":"打鱼晒网","week":"星期五"},{"cpId":39393615,"cpTime":"18:40","date":"2016-06-17","endTime":"20:15","programName":"都市报道","week":"星期五"},{"cpId":39393616,"cpTime":"20:15","date":"2016-06-17","endTime":"20:56","programName":"都市剧场","week":"星期五"},{"cpId":39393617,"cpTime":"20:56","date":"2016-06-17","endTime":"21:38","programName":"都市剧场","week":"星期五"},{"cpId":39393618,"cpTime":"21:38","date":"2016-06-17","endTime":"22:20","programName":"都市剧场","week":"星期五"},{"cpId":39393619,"cpTime":"22:20","date":"2016-06-17","endTime":"22:51","programName":"都市007","week":"星期五"},{"cpId":39393620,"cpTime":"22:51","date":"2016-06-17","endTime":"23:22","programName":"食客准备着","week":"星期五"},{"cpId":39393621,"cpTime":"23:22","date":"2016-06-17","endTime":"23:59","programName":"双喜临门","week":"星期五"}],"week":"星期五"},{"count":23,"date":"2016-06-18","program":[{"cpId":39405770,"cpTime":"00:00","date":"2016-06-18","endTime":"07:30","programName":"双喜临门","week":"星期六"},{"cpId":39405771,"cpTime":"07:30","date":"2016-06-18","endTime":"08:21","programName":"都市报道","week":"星期六"},{"cpId":39405772,"cpTime":"08:21","date":"2016-06-18","endTime":"09:15","programName":"精品剧场","week":"星期六"},{"cpId":39405773,"cpTime":"09:15","date":"2016-06-18","endTime":"10:10","programName":"精品剧场","week":"星期六"},{"cpId":39405774,"cpTime":"10:10","date":"2016-06-18","endTime":"11:05","programName":"精品剧场","week":"星期六"},{"cpId":39405775,"cpTime":"11:05","date":"2016-06-18","endTime":"12:00","programName":"精品剧场","week":"星期六"},{"cpId":39405776,"cpTime":"12:00","date":"2016-06-18","endTime":"12:12","programName":"都市快报","week":"星期六"},{"cpId":39405777,"cpTime":"12:12","date":"2016-06-18","endTime":"12:49","programName":"打鱼晒网","week":"星期六"},{"cpId":39405778,"cpTime":"12:49","date":"2016-06-18","endTime":"14:20","programName":"都市报道","week":"星期六"},{"cpId":39405779,"cpTime":"14:20","date":"2016-06-18","endTime":"15:09","programName":"精选剧场","week":"星期六"},{"cpId":39405780,"cpTime":"15:09","date":"2016-06-18","endTime":"15:58","programName":"精选剧场","week":"星期六"},{"cpId":39405781,"cpTime":"15:58","date":"2016-06-18","endTime":"16:48","programName":"精选剧场","week":"星期六"},{"cpId":39405782,"cpTime":"16:48","date":"2016-06-18","endTime":"17:00","programName":"都市快报","week":"星期六"},{"cpId":39405783,"cpTime":"17:00","date":"2016-06-18","endTime":"17:30","programName":"都市007","week":"星期六"},{"cpId":39405784,"cpTime":"17:30","date":"2016-06-18","endTime":"18:00","programName":"食客准备着","week":"星期六"},{"cpId":39405785,"cpTime":"18:00","date":"2016-06-18","endTime":"18:40","programName":"打鱼晒网","week":"星期六"},{"cpId":39405786,"cpTime":"18:40","date":"2016-06-18","endTime":"20:15","programName":"都市报道","week":"星期六"},{"cpId":39405787,"cpTime":"20:15","date":"2016-06-18","endTime":"20:56","programName":"都市剧场","week":"星期六"},{"cpId":39405788,"cpTime":"20:56","date":"2016-06-18","endTime":"21:38","programName":"都市剧场","week":"星期六"},{"cpId":39405789,"cpTime":"21:38","date":"2016-06-18","endTime":"22:20","programName":"都市剧场","week":"星期六"},{"cpId":39405790,"cpTime":"22:20","date":"2016-06-18","endTime":"22:51","programName":"都市007","week":"星期六"},{"cpId":39405791,"cpTime":"22:51","date":"2016-06-18","endTime":"23:22","programName":"食客准备着","week":"星期六"},{"cpId":39405792,"cpTime":"23:22","date":"2016-06-18","endTime":"23:59","programName":"双喜临门","week":"星期六"}],"week":"星期六"},{"count":20,"date":"2016-06-19","program":[{"cpId":39417913,"cpTime":"00:00","date":"2016-06-19","endTime":"07:30","programName":"双喜临门","week":"星期日"},{"cpId":39417914,"cpTime":"07:30","date":"2016-06-19","endTime":"08:21","programName":"都市报道","week":"星期日"},{"cpId":39417915,"cpTime":"08:21","date":"2016-06-19","endTime":"09:15","programName":"精品剧场","week":"星期日"},{"cpId":39417916,"cpTime":"09:15","date":"2016-06-19","endTime":"10:10","programName":"精品剧场","week":"星期日"},{"cpId":39417917,"cpTime":"10:10","date":"2016-06-19","endTime":"11:05","programName":"精品剧场","week":"星期日"},{"cpId":39417918,"cpTime":"11:05","date":"2016-06-19","endTime":"12:00","programName":"精品剧场","week":"星期日"},{"cpId":39417919,"cpTime":"12:00","date":"2016-06-19","endTime":"12:12","programName":"都市快报","week":"星期日"},{"cpId":39417920,"cpTime":"12:12","date":"2016-06-19","endTime":"12:49","programName":"打鱼晒网","week":"星期日"},{"cpId":39417921,"cpTime":"12:49","date":"2016-06-19","endTime":"14:20","programName":"都市报道","week":"星期日"},{"cpId":39417922,"cpTime":"14:20","date":"2016-06-19","endTime":"15:13","programName":"精选剧场","week":"星期日"},{"cpId":39417923,"cpTime":"15:13","date":"2016-06-19","endTime":"16:06","programName":"精选剧场","week":"星期日"},{"cpId":39417924,"cpTime":"16:06","date":"2016-06-19","endTime":"17:00","programName":"精选剧场","week":"星期日"},{"cpId":39417925,"cpTime":"17:00","date":"2016-06-19","endTime":"17:30","programName":"都市007","week":"星期日"},{"cpId":39417926,"cpTime":"17:30","date":"2016-06-19","endTime":"18:00","programName":"食客准备着","week":"星期日"},{"cpId":39417927,"cpTime":"18:00","date":"2016-06-19","endTime":"18:40","programName":"打鱼晒网","week":"星期日"},{"cpId":39417928,"cpTime":"18:40","date":"2016-06-19","endTime":"20:15","programName":"都市报道","week":"星期日"},{"cpId":39417929,"cpTime":"20:15","date":"2016-06-19","endTime":"22:20","programName":"你最有才","week":"星期日"},{"cpId":39417930,"cpTime":"22:20","date":"2016-06-19","endTime":"22:51","programName":"都市007","week":"星期日"},{"cpId":39417931,"cpTime":"22:51","date":"2016-06-19","endTime":"23:22","programName":"食客准备着","week":"星期日"},{"cpId":39417932,"cpTime":"23:22","date":"2016-06-19","endTime":"23:59","programName":"双喜临门","week":"星期日"}],"week":"星期日"},{"count":0,"date":"2016-06-20","program":[],"week":"星期一"},{"count":0,"date":"2016-06-21","program":[],"week":"星期二"},{"count":0,"date":"2016-06-22","program":[],"week":"星期三"}],"play":[{"channelIdStr":"","id":55124,"platformId":27,"url":"http10://ws_hnds_1300","videoPath":""},{"channelIdStr":"","id":53813,"platformId":28,"url":"http://live.togic.com/live/letv_ipad10/ws_hnds_1300","videoPath":""},{"channelIdStr":"","id":47245,"platformId":7,"url":"","videoPath":"http://live.hntv.tv/channels/tvie/video_channel_02/m3u8:hntv2_apple_200k/live"},{"channelIdStr":"","id":53817,"platformId":28,"url":"","videoPath":"http://live.hntv.tv/channels/tvie/video_channel_02/m3u8:hntv2_apple_200k/live"},{"channelIdStr":"","id":55125,"platformId":27,"url":"","videoPath":"http://rtmplive1.hnr.cn/hnlive/HN2-3.stream/playlist.m3u8"}]}
     * msg : success
     * p2plock : 1
     */

    private int code;
    private ContentBean content;
    private String msg;
    private String p2plock;
    private String jumpUrl;
    private String adUrl;

    public String getAdUrl() {
        return adUrl;
    }

    public void setAdUrl(String adUrl) {
        this.adUrl = adUrl;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getP2plock() {
        return p2plock;
    }

    public void setP2plock(String p2plock) {
        this.p2plock = p2plock;
    }

    public static class ContentBean {
        /**
         * count : 20
         * date : 2016-06-16
         * program : [{"cpId":39381275,"cpTime":"09:15","date":"2016-06-16","endTime":"10:10","programName":"精品剧场","week":"星期四"},{"cpId":39381276,"cpTime":"10:10","date":"2016-06-16","endTime":"11:05","programName":"精品剧场","week":"星期四"},{"cpId":39381277,"cpTime":"11:05","date":"2016-06-16","endTime":"12:00","programName":"精品剧场","week":"星期四"},{"cpId":39381278,"cpTime":"12:00","date":"2016-06-16","endTime":"12:12","programName":"都市快报","week":"星期四"},{"cpId":39381279,"cpTime":"12:12","date":"2016-06-16","endTime":"12:49","programName":"打鱼晒网","week":"星期四"},{"cpId":39381280,"cpTime":"12:49","date":"2016-06-16","endTime":"14:20","programName":"都市报道","week":"星期四"},{"cpId":39381281,"cpTime":"14:20","date":"2016-06-16","endTime":"15:09","programName":"精选剧场","week":"星期四"},{"cpId":39381282,"cpTime":"15:09","date":"2016-06-16","endTime":"15:58","programName":"精选剧场","week":"星期四"},{"cpId":39381283,"cpTime":"15:58","date":"2016-06-16","endTime":"16:48","programName":"精选剧场","week":"星期四"},{"cpId":39381284,"cpTime":"16:48","date":"2016-06-16","endTime":"17:00","programName":"都市快报","week":"星期四"},{"cpId":39381285,"cpTime":"17:00","date":"2016-06-16","endTime":"17:30","programName":"都市007","week":"星期四"},{"cpId":39381286,"cpTime":"17:30","date":"2016-06-16","endTime":"18:00","programName":"食客准备着","week":"星期四"},{"cpId":39381287,"cpTime":"18:00","date":"2016-06-16","endTime":"18:40","programName":"打鱼晒网","week":"星期四"},{"cpId":39381288,"cpTime":"18:40","date":"2016-06-16","endTime":"20:15","programName":"都市报道","week":"星期四"},{"cpId":39381289,"cpTime":"20:15","date":"2016-06-16","endTime":"20:56","programName":"都市剧场","week":"星期四"},{"cpId":39381290,"cpTime":"20:56","date":"2016-06-16","endTime":"21:38","programName":"都市剧场","week":"星期四"},{"cpId":39381291,"cpTime":"21:38","date":"2016-06-16","endTime":"22:20","programName":"都市剧场","week":"星期四"},{"cpId":39381292,"cpTime":"22:20","date":"2016-06-16","endTime":"22:51","programName":"都市007","week":"星期四"},{"cpId":39381293,"cpTime":"22:51","date":"2016-06-16","endTime":"23:22","programName":"食客准备着","week":"星期四"},{"cpId":39381294,"cpTime":"23:22","date":"2016-06-16","endTime":"23:59","programName":"双喜临门","week":"星期四"}]
         * week : 星期四
         */

        private List<DayBean> day;
        /**
         * channelIdStr :
         * id : 55124
         * platformId : 27
         * url : http10://ws_hnds_1300
         * videoPath :
         */

        private List<PlayBean> play;

        public List<DayBean> getDay() {
            return day;
        }

        public void setDay(List<DayBean> day) {
            this.day = day;
        }

        public List<PlayBean> getPlay() {
            return play;
        }

        public void setPlay(List<PlayBean> play) {
            this.play = play;
        }

        public static class DayBean {
            private int count;
            private String date;
            private String week;
            /**
             * cpId : 39381275
             * cpTime : 09:15
             * date : 2016-06-16
             * endTime : 10:10
             * programName : 精品剧场
             * week : 星期四
             */

            private List<ProgramBean> program;

            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
            }

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getWeek() {
                return week;
            }

            public void setWeek(String week) {
                this.week = week;
            }

            public List<ProgramBean> getProgram() {
                return program;
            }

            public void setProgram(List<ProgramBean> program) {
                this.program = program;
            }

            public static class ProgramBean {

                private int cpId;
                private String cpTime;
                private String date;
                private String endTime;
                private String programName;
                private String week;

                public int getCpId() {
                    return cpId;
                }

                public void setCpId(int cpId) {
                    this.cpId = cpId;
                }

                public String getCpTime() {
                    return cpTime;
                }

                public void setCpTime(String cpTime) {
                    this.cpTime = cpTime;
                }

                public String getDate() {
                    return date;
                }

                public void setDate(String date) {
                    this.date = date;
                }

                public String getEndTime() {
                    return endTime;
                }

                public void setEndTime(String endTime) {
                    this.endTime = endTime;
                }

                public String getProgramName() {
                    return programName;
                }

                public void setProgramName(String programName) {
                    this.programName = programName;
                }

                public String getWeek() {
                    return week;
                }

                public void setWeek(String week) {
                    this.week = week;
                }
            }
        }

        public static class PlayBean {
            private String channelIdStr;
            private int id;
            private int platformId;
            private String url;
            private String videoPath;

            public String getChannelIdStr() {
                return channelIdStr;
            }

            public void setChannelIdStr(String channelIdStr) {
                this.channelIdStr = channelIdStr;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getPlatformId() {
                return platformId;
            }

            public void setPlatformId(int platformId) {
                this.platformId = platformId;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getVideoPath() {
                return videoPath;
            }

            public void setVideoPath(String videoPath) {
                this.videoPath = videoPath;
            }
        }
    }
}
