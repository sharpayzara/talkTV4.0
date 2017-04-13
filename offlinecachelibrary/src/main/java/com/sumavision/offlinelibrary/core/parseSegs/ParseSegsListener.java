package com.sumavision.offlinelibrary.core.parseSegs;

public interface ParseSegsListener {

	public void onPareseSegsStart(boolean isUpdateSeginfos);

	public void onPareseSegsResume();

	public void onPareseSegsFail();

	public void onDownloadError();

	public void onParseSegIsPlaylist();

}
