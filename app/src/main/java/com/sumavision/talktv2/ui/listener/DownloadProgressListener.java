package com.sumavision.talktv2.ui.listener;

/**
 * Created by sharpay on 16-7-6.
 */
public interface DownloadProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}