package com.sumavision.talktv2.model.entity;

import java.io.Serializable;

/**
 * Created by sharpay on 16-8-23.
 */

public class UserInfo  implements Serializable,Cloneable {
    private String nickName;
    private String sex;//性别
    private String openid;
    private String userId;
    private String imageUrl;
    private String origin;
    private String city;
    private String province;
    private String birthday;
    private String expiresIn;
    private String accessToken;
    private String tag;

    public UserInfo(){}

    public UserInfo(UMUserInfo userInfo) {
        this.nickName = userInfo.getScreen_name();
        this.sex = userInfo.getGender();
        this.imageUrl = userInfo.getProfile_image_url();
        this.origin = userInfo.getOrigin();
        this.province = userInfo.getProvince();
        this.city = userInfo.getCity();
        this.accessToken = userInfo.getAccess_token();
        this.expiresIn = userInfo.getExpires_in();
        if(userInfo.getOrigin().equals("SINA")){
            this.openid = userInfo.getUid();
            this.imageUrl = userInfo.getCover_image_phone();
        }else{
            this.openid = userInfo.getOpenid();
        }
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
