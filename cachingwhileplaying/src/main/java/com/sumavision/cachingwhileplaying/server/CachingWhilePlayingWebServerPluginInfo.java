package com.sumavision.cachingwhileplaying.server;

/**
* @author Paul S. Hawke (paul.hawke@gmail.com)
*         On: 9/14/13 at 8:09 AM
*/
public interface CachingWhilePlayingWebServerPluginInfo {
    String[] getMimeTypes();

    String[] getIndexFilesForMimeType(String mime);

    CachingWhilePlayingWebServerPlugin getWebServerPlugin(String mimeType);
}
