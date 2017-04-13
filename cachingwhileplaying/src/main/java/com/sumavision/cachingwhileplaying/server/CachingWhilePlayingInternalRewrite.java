package com.sumavision.cachingwhileplaying.server;

import java.util.Map;

import com.sumavision.cachingwhileplaying.server.CachingWhilePlayingNanoHTTPD.Response;







/**
 * @author Paul S. Hawke (paul.hawke@gmail.com)
 *         On: 9/15/13 at 2:52 PM
 */
public class CachingWhilePlayingInternalRewrite extends Response {
    private final String uri;
    private final Map<String, String> headers;

    public CachingWhilePlayingInternalRewrite(Map<String, String> headers, String uri) {
        super(null);
        this.headers = headers;
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
