package com.sumavision.talktv2.model.entity.decor;

import java.io.Serializable;
import java.util.List;

/**
 * 直播频道列表和频道分类的数据bean
 * Created by zjx on 2016/6/2.
 */
public class LiveData extends BaseData implements Serializable{

    /**
     * code : 0
     * content : {"type":[{"channel":[{"cpEndTime":"","cpId":"","cpName":"没有节目信息","cpStartTime":"","haveProgram":false,"id":"103","name":"重庆卫视","skipWeb":"0"}],"channelCount":1,"id":52,"name":"重庆"}],"typeCount":33}
     * msg : success
     */

    private int code;
    /**
     * type : [{"channel":[{"cpEndTime":"","cpId":"","cpName":"没有节目信息","cpStartTime":"","haveProgram":false,"id":"103","name":"重庆卫视","skipWeb":"0"}],"channelCount":1,"id":52,"name":"重庆"}]
     * typeCount : 33
     */
    private ContentBean content;
    private String msg;

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

    public static class ContentBean {
        private int typeCount;
        /**
         * channel : [{"cpEndTime":"","cpId":"","cpName":"没有节目信息","cpStartTime":"","haveProgram":false,"id":"103","name":"重庆卫视","skipWeb":"0"}]
         * channelCount : 1
         * id : 52
         * name : 重庆
         */

        private List<TypeBean> type;

        public int getTypeCount() {
            return typeCount;
        }

        public void setTypeCount(int typeCount) {
            this.typeCount = typeCount;
        }

        public List<TypeBean> getType() {
            return type;
        }

        public void setType(List<TypeBean> type) {
            this.type = type;
        }

        public static class TypeBean {
            private int channelCount;
            private int id;
            private String name;
            /**
             * cpEndTime :
             * cpId :
             * cpName : 没有节目信息
             * cpStartTime :
             * haveProgram : false
             * id : 103
             * name : 重庆卫视
             * skipWeb : 0
             */

            private List<ChannelBean> channel;

            public int getChannelCount() {
                return channelCount;
            }

            public void setChannelCount(int channelCount) {
                this.channelCount = channelCount;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<ChannelBean> getChannel() {
                return channel;
            }

            public void setChannel(List<ChannelBean> channel) {
                this.channel = channel;
            }

            public static class ChannelBean implements Cloneable{
                private String cpEndTime;
                private String cpId;
                private String cpName;
                private String cpStartTime;
                private boolean haveProgram;
                private String id;
                private String name;
                private String skipWeb;
                private String channelType;

                @Override
                public Object clone() throws CloneNotSupportedException {
                    return super.clone();
                }

                public String getChannelType(){
                    return channelType;
                }

                public void setChannelType (String channelType) {
                    this.channelType = channelType;
                }

                public String getCpEndTime() {
                    return cpEndTime;
                }

                public void setCpEndTime(String cpEndTime) {
                    this.cpEndTime = cpEndTime;
                }

                public String getCpId() {
                    return cpId;
                }

                public void setCpId(String cpId) {
                    this.cpId = cpId;
                }

                public String getCpName() {
                    return cpName;
                }

                public void setCpName(String cpName) {
                    this.cpName = cpName;
                }

                public String getCpStartTime() {
                    return cpStartTime;
                }

                public void setCpStartTime(String cpStartTime) {
                    this.cpStartTime = cpStartTime;
                }

                public boolean isHaveProgram() {
                    return haveProgram;
                }

                public void setHaveProgram(boolean haveProgram) {
                    this.haveProgram = haveProgram;
                }

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getSkipWeb() {
                    return skipWeb;
                }

                public void setSkipWeb(String skipWeb) {
                    this.skipWeb = skipWeb;
                }
            }
        }
    }
}
