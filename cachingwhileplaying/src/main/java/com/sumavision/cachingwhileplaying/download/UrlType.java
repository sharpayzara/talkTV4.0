package com.sumavision.cachingwhileplaying.download;

public enum UrlType {
	TYPE_M3U8(0x0), TYPE_FLV(0x1), TYPE_MP4(0x2), TYPE_UNKNOW(0x3), TYPE_WEB(
			0x4);

	UrlType(int intValue) {
		this.intValue = intValue;
	}

	private int intValue;

	// use when save instateState
	public static UrlType mapIntToValue(int intValue) {
		for (UrlType type : UrlType.values()) {
			if (type.intValue == intValue) {
				return type;
			}
		}
		return UrlType.TYPE_UNKNOW;
	}
}
