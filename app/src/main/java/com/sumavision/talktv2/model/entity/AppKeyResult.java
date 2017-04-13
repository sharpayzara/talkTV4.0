package com.sumavision.talktv2.model.entity;

import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.model.entity.decor.BaseData;

import java.util.List;

/**
 * Created by sharpay on 16-9-19.
 */
public class AppKeyResult extends BaseData{

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private String appid;
        private String secret;

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }
    }
}
