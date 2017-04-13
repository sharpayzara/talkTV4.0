package com.sumavision.crack;

public interface OnCrackCompleteListener {

	/**
	 * 破解url成功
	 * 
	 * @param parseUrl
	 */
	public void OnCrackComplete(String parseUrl, int videoFormat);
}
