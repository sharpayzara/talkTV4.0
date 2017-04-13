package com.sumavision.talktv2.model;

import com.sumavision.talktv2.model.entity.ADInfoItem;
import com.sumavision.talktv2.model.entity.SpecialListData;
import com.sumavision.talktv2.model.entity.decor.BaseData;

import java.io.File;

/**
 * Created by sharpay on 16-5-26.
 */
public interface HomeModel extends BaseModel{
    void loadClassifys(CallBackListener listener);
    void loadClassifyUpdatas(CallBackListener listener);
    void getUpdateInfo(CallBackListener listener);
    void loadHomeRecommendData(CallBackListener listener,String nid);
    void loadHomeRecommendUpdateData(CallBackListener listener,String nid,String v);
    void download(String url, File file);
    void loadScreenData(CallBackListener listener);
    void getPlayHistory(CollectCallBackListener listener);
    void loadSpecialData(int page, int size,CallBackListener<SpecialListData> listener);
    void loadADInfo(String type,CallBackListener<ADInfoItem> listener);
    void sendScanData(String urlStr,CallBackListener<BaseData> listener);
}
