package com.sumavision.talktv2.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.entity.WXOrder;
import com.sumavision.talktv2.presenter.PayPresenter;
import com.sumavision.talktv2.ui.activity.base.CommonHeadPanelActivity;
import com.sumavision.talktv2.ui.iview.IPayView;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import butterknife.OnClick;

/**
 * Created by sharpay on 16-9-2.
 */
public class PayActivity extends CommonHeadPanelActivity<PayPresenter> implements IPayView {
    private IWXAPI api;
    PayPresenter presenter;
    @Override
    public void initView() {
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_pay;
    }

    @Override
    protected void initPresenter() {
        api = WXAPIFactory.createWXAPI(this, null);
        api.registerApp(AppGlobalConsts.PlatCode.WEIXIN_APPID);
        presenter = new PayPresenter(this,this);
        presenter.init();
    }

    @OnClick(R.id.pay_btn)
    public void onClick() {
     /*   String url = "http://wxpay.weixin.qq.com/pub_v2/app/app_pay.php?plat=android";
        Toast.makeText(PayActivity.this, "获取订单中...", Toast.LENGTH_SHORT).show();
        try{
            byte[] buf = Util.httpGet(url);
            if (true) {
                String content = new String("{\"appid\":\"wxb4ba3c02aa476ea1\",\"partnerid\":\"1305176001\",\"package\":\"Sign=WXPay\",\"noncestr\":\"9a99265421b945e61529df2826d7d078\",\"timestamp\":1472781373,\"prepayid\":\"wx20160902095613af685ead850442071398\",\"sign\":\"94CB179069B406FA1DFED6D98634C0F3\"}");
                Log.e("get server pay params:",content);
                JSONObject json = new JSONObject(content);
                if(null != json && !json.has("retcode") ){
                    PayReq req = new PayReq();
                    //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
                    req.appId			= json.getString("appid");
                    req.partnerId		= json.getString("partnerid");
                    req.prepayId		= json.getString("prepayid");
                    req.nonceStr		= json.getString("noncestr");
                    req.timeStamp		= json.getString("timestamp");
                    req.packageValue	= json.getString("package");
                    req.sign			= json.getString("sign");
                    req.extData			= "app data"; // optional
                    Toast.makeText(PayActivity.this, "正常调起支付", Toast.LENGTH_SHORT).show();
                    // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                    api.sendReq(req);
                }else{
                    Log.d("PAY_GET", "返回错误"+json.getString("retmsg"));
                    Toast.makeText(PayActivity.this, "返回错误"+json.getString("retmsg"), Toast.LENGTH_SHORT).show();
                }
            }else{
                Log.d("PAY_GET", "服务器请求错误");
                Toast.makeText(PayActivity.this, "服务器请求错误", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            Log.e("PAY_GET", "异常："+e.getMessage());
            Toast.makeText(PayActivity.this, "异常："+e.getMessage(), Toast.LENGTH_SHORT).show();
        }*/
        presenter.getWXOrder();
    }

    @Override
    public void respWXOrder(WXOrder order) {
        if(null != order && TextUtils.isEmpty(order.getRetcode()) ) {
            PayReq req = new PayReq();
            //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
            req.appId = order.getAppid();
            req.partnerId = order.getPartnerid();
            req.prepayId = order.getPrepayid();
            req.nonceStr = order.getNoncestr();
            req.timeStamp = order.getTimestamp();
            req.packageValue = order.getPackageX();
            req.sign = order.getSign();
            req.extData = "app data"; // optional
            Toast.makeText(PayActivity.this, "正常调起支付", Toast.LENGTH_SHORT).show();
            api.sendReq(req);
        }else{
            Log.d("PAY_GET", "返回错误"+order.getRetmsg());
            Toast.makeText(PayActivity.this, "返回错误"+order.getRetmsg(), Toast.LENGTH_SHORT).show();
        }
    }
}
