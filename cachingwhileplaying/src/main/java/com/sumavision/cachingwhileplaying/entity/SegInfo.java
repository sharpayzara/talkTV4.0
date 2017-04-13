package com.sumavision.cachingwhileplaying.entity;

import java.io.Serializable;

public class SegInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	public int index;// �ļ����
	public String downloadUrl;// �ļ���ַ
	// �ж��� ���ڱ���m3u8
	public String timeLength;// �ļ�ʱ��
	public long dataLength;// �ļ�����
	public boolean isDownloaded = false; // �Ƿ��������
	public boolean isDownloading = false;// �Ƿ���������

	public long breakPoint; // �ϵ�
	public String fileDir;

	public int programId;

	public int subId;

	public String locationFile;

	public float totalDuration;
	// �����ε�ʱ�� ��,
	public String singleTimeLength;

	// 该段对应的m3u8文件url
	public String m3u8Url;

	// 用于百度网盘边看边缓，标明该段是否更换账号20次后仍无法下载，若是则置为true
	public boolean isBroken = false;

	// 处理#EXT-X-DISCONTINUITY的情况
	public boolean isDiscontinuity = false;

}
