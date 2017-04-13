package com.sumavision.talktv2.model;

import com.sumavision.talktv2.ui.listener.DownloadProgressListener;

import java.io.File;

/**
 * Created by sharpay on 16-7-6.
 */
public interface DownloadModel extends BaseModel{
    void download( CallBackListener listener,  String url,  File file, DownloadProgressListener progressListene);
    void updteData( CallBackListener listener);
}
