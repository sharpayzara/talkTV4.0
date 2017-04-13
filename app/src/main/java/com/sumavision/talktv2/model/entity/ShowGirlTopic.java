package com.sumavision.talktv2.model.entity;

import com.sumavision.talktv2.model.entity.decor.BaseData;

import java.util.List;

/**
 * Created by sharpay on 16-8-3.
 */
public class ShowGirlTopic extends BaseData{
    private String pathPrefix;
    private String TagCode;

    private List<PartListBean> partList;

    public String getPathPrefix() {
        return pathPrefix;
    }

    public void setPathPrefix(String pathPrefix) {
        this.pathPrefix = pathPrefix;
    }

    public String getTagCode() {
        return TagCode;
    }

    public void setTagCode(String TagCode) {
        this.TagCode = TagCode;
    }

    public List<PartListBean> getPartList() {
        return partList;
    }

    public void setPartList(List<PartListBean> partList) {
        this.partList = partList;
    }

    public static class PartListBean {
        private String partName;
        private int onlineCount;
        private int partId;

        private List<RcdRoomsBean> rcdRooms;

        public String getPartName() {
            return partName;
        }

        public void setPartName(String partName) {
            this.partName = partName;
        }

        public int getOnlineCount() {
            return onlineCount;
        }

        public void setOnlineCount(int onlineCount) {
            this.onlineCount = onlineCount;
        }

        public int getPartId() {
            return partId;
        }

        public void setPartId(int partId) {
            this.partId = partId;
        }

        public List<RcdRoomsBean> getRcdRooms() {
            return rcdRooms;
        }

        public void setRcdRooms(List<RcdRoomsBean> rcdRooms) {
            this.rcdRooms = rcdRooms;
        }

        public static class RcdRoomsBean {
            private int roomId;
            private int userId;
            private int screenType;
            private String nickname;
            private int gender;
            private int onlineCount;
            private int max;
            private int liveType;
            private long livestarttime;
            private int actorLevel;
            private int richLevel;
            private int icon;
            private int roomMode;
            private String roomTheme;
            private int roomType;
            private int roomSource;
            private String portrait_path_128;
            private String portrait_path_256;
            private String portrait_path_400;
            private String portrait_path_756;
            private String poster_path_1280;
            private String poster_path_272;
            private String poster_path_128;
            private String poster_path_400;
            private String poster_path_756;
            private String poster_path_300;
            private String city;

            public int getRoomId() {
                return roomId;
            }

            public void setRoomId(int roomId) {
                this.roomId = roomId;
            }

            public int getUserId() {
                return userId;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }

            public int getScreenType() {
                return screenType;
            }

            public void setScreenType(int screenType) {
                this.screenType = screenType;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public int getGender() {
                return gender;
            }

            public void setGender(int gender) {
                this.gender = gender;
            }

            public int getOnlineCount() {
                return onlineCount;
            }

            public void setOnlineCount(int onlineCount) {
                this.onlineCount = onlineCount;
            }

            public int getMax() {
                return max;
            }

            public void setMax(int max) {
                this.max = max;
            }

            public int getLiveType() {
                return liveType;
            }

            public void setLiveType(int liveType) {
                this.liveType = liveType;
            }

            public long getLivestarttime() {
                return livestarttime;
            }

            public void setLivestarttime(long livestarttime) {
                this.livestarttime = livestarttime;
            }

            public int getActorLevel() {
                return actorLevel;
            }

            public void setActorLevel(int actorLevel) {
                this.actorLevel = actorLevel;
            }

            public int getRichLevel() {
                return richLevel;
            }

            public void setRichLevel(int richLevel) {
                this.richLevel = richLevel;
            }

            public int getIcon() {
                return icon;
            }

            public void setIcon(int icon) {
                this.icon = icon;
            }

            public int getRoomMode() {
                return roomMode;
            }

            public void setRoomMode(int roomMode) {
                this.roomMode = roomMode;
            }

            public String getRoomTheme() {
                return roomTheme;
            }

            public void setRoomTheme(String roomTheme) {
                this.roomTheme = roomTheme;
            }

            public int getRoomType() {
                return roomType;
            }

            public void setRoomType(int roomType) {
                this.roomType = roomType;
            }

            public int getRoomSource() {
                return roomSource;
            }

            public void setRoomSource(int roomSource) {
                this.roomSource = roomSource;
            }

            public String getPortrait_path_128() {
                return portrait_path_128;
            }

            public void setPortrait_path_128(String portrait_path_128) {
                this.portrait_path_128 = portrait_path_128;
            }

            public String getPortrait_path_256() {
                return portrait_path_256;
            }

            public void setPortrait_path_256(String portrait_path_256) {
                this.portrait_path_256 = portrait_path_256;
            }

            public String getPortrait_path_400() {
                return portrait_path_400;
            }

            public void setPortrait_path_400(String portrait_path_400) {
                this.portrait_path_400 = portrait_path_400;
            }

            public String getPortrait_path_756() {
                return portrait_path_756;
            }

            public void setPortrait_path_756(String portrait_path_756) {
                this.portrait_path_756 = portrait_path_756;
            }

            public String getPoster_path_1280() {
                return poster_path_1280;
            }

            public void setPoster_path_1280(String poster_path_1280) {
                this.poster_path_1280 = poster_path_1280;
            }

            public String getPoster_path_272() {
                return poster_path_272;
            }

            public void setPoster_path_272(String poster_path_272) {
                this.poster_path_272 = poster_path_272;
            }

            public String getPoster_path_128() {
                return poster_path_128;
            }

            public void setPoster_path_128(String poster_path_128) {
                this.poster_path_128 = poster_path_128;
            }

            public String getPoster_path_400() {
                return poster_path_400;
            }

            public void setPoster_path_400(String poster_path_400) {
                this.poster_path_400 = poster_path_400;
            }

            public String getPoster_path_756() {
                return poster_path_756;
            }

            public void setPoster_path_756(String poster_path_756) {
                this.poster_path_756 = poster_path_756;
            }

            public String getPoster_path_300() {
                return poster_path_300;
            }

            public void setPoster_path_300(String poster_path_300) {
                this.poster_path_300 = poster_path_300;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }
        }
    }
}
