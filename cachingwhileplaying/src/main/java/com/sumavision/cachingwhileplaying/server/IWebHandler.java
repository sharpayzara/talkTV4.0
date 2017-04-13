package com.sumavision.cachingwhileplaying.server;

public interface IWebHandler {
	public boolean getM3u8Exist();

	public boolean getSegExists(String url);
}
