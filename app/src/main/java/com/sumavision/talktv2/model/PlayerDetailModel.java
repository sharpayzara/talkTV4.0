package com.sumavision.talktv2.model;

import android.content.Context;

import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import com.sumavision.talktv2.model.entity.SeriesDetail;
import com.sumavision.talktv2.ui.listener.DownloadProgressListener;

import java.io.File;
import java.util.List;

/**
 * Created by sharpay on 16-6-20.
 */
public interface PlayerDetailModel extends BaseModel {
    void getDetailData(final String id, final String mid, String cpId, final CallBackListener listener);
    void getSeriesGridData(final String id,final String cpid,final String tab,final CallBackListener listener);
    void getSeriesListData(final String id,final String cpid,final int page,final int size,final CallBackListener listener);
    PlayerHistoryBean getPlayHistory (String programId);
    void insertPlayHistory (PlayerHistoryBean playerHistoryBean);
    void delPlayHistory (String programId);
    void getRecommendData(String id, String cname, final CallBackListener listener);
    void getCpData(final CallBackListener listener);
    void download(CallBackListener listener, String url, File file, DownloadProgressListener progressListener);

    void enterDedailLog(String id,Context context);
    void pvLog(String id,Context context);
    int getEpiPos (String epi, List<SeriesDetail.SourceBean> sourceBeens);
}
