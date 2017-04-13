package com.sumavision.cachingwhileplaying.entity;

import java.util.ArrayList;

public class CachingWhilePlayingInfo {
	// ��ĿID
	public String programId;
	// �ӽ�ĿID
	public String subId;
	// ��Ŀ����
	public String programName;
	// ��ĿͼƬ
	public String programPic;
	// ��ʼ��ַ
	public String initUrl;
	// ����?ص�M3U8�ļ�
	public String localUrl;
	// ���ص�ַ ԭʼ��ַ����ֱ����M3U8 Ҳ�����ƽ����M3U8
	public String m3u8;
	// 0 false 1 true;
	public int ism3u8Downloaded;
	
	// the m3u8 file is downloading or not
	public boolean isDownloading;
	// the m3u8 file is downloaded or not
	public boolean isDownloaded;
	// ���ص�״̬
	public int state;
	// ���صĽ��
	public int progress;
	// m3u8�Ӷ�����
	public int segCount;
	// m3u8�Ӷ����ص���λ��
	public int segStep;

	// M3U8�ļ���targetDuration;
	public int targetDuration;

	// ����
	public ArrayList<SegInfo> segInfos;

	// M3U8������ʱ�� ʱ�䳤��Ӧ���滻��Ŀ
	public long initUrlDownloadTime;

	// �����ֶ� �����ò���
	public int breakPoint;

	@Override
	public String toString() {
		return "localUrl=" + (this.localUrl == null ? "" : this.localUrl)
				+ ",m3u8=" + (this.m3u8 == null ? "" : this.m3u8)
				+ ",ism3u8Downloaded=" + this.ism3u8Downloaded + ",progress="
				+ this.progress + ",segCount=" + this.segCount + ",segStep="
				+ this.segStep + ",programId=" + this.programId + ",subId="
				+ this.subId;
	}
}
