package com.sumavision.talktv2.model.entity;

import com.google.gson.annotations.SerializedName;
import com.sumavision.talktv2.model.entity.decor.BaseData;

/**
 * Created by sharpay on 16-6-30.
 */
public class WXOrder extends BaseData{

    /**
     * appid : wxb4ba3c02aa476ea1
     * partnerid : 1305176001
     * package : Sign=WXPay
     * noncestr : 9a99265421b945e61529df2826d7d078
     * timestamp : 1472781373
     * prepayid : wx20160902095613af685ead850442071398
     * sign : 94CB179069B406FA1DFED6D98634C0F3
     */

    private String appid;
    private String partnerid;
    @SerializedName("package")
    private String packageX;
    private String noncestr;
    private String timestamp;
    private String prepayid;
    private String sign;
    private String retmsg;
    private String retcode;

    public String getRetcode() {
        return retcode;
    }

    public void setRetcode(String retcode) {
        this.retcode = retcode;
    }

    public String getRetmsg() {
        return retmsg;
    }

    public void setRetmsg(String retmsg) {
        this.retmsg = retmsg;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getPackageX() {
        return packageX;
    }

    public void setPackageX(String packageX) {
        this.packageX = packageX;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
