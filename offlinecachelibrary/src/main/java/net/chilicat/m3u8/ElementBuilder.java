package net.chilicat.m3u8;

import java.net.URI;

/**
 * @author dkuffner
 */
class ElementBuilder {
	private Float duration = 0f;
	private URI uri;
	private PlayListInfo playlistInfo;
	private EncryptionInfo encryptionInfo;
	private String title;
	private long programDate = -1;

	public boolean getIsDiscontinuity() {
		return discontinuity;
	}

	public void discontinuity(boolean discontinuity) {
		this.discontinuity = discontinuity;
	}

	private boolean discontinuity;

	public ElementBuilder() {

	}

	public long programDate() {
		return programDate;
	}

	public ElementBuilder programDate(long programDate) {
		this.programDate = programDate;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public ElementBuilder title(String title) {
		this.title = title;
		return this;
	}

	public Float getDuration() {
		return duration;
	}

	public ElementBuilder duration(Float duration) {
		this.duration = duration;
		return this;
	}

	public URI getUri() {
		return uri;
	}

	public ElementBuilder uri(URI uri) {
		this.uri = uri;
		return this;
	}

	public ElementBuilder playList(final int programId, final int bandWidth,
			final String codec) {
		this.playlistInfo = new ElementImpl.PlayListInfoImpl(programId,
				bandWidth, codec);
		return this;
	}

	public ElementBuilder resetPlatListInfo() {
		playlistInfo = null;
		return this;
	}

	public ElementBuilder resetEncryptedInfo() {
		encryptionInfo = null;
		return this;
	}

	public ElementBuilder reset() {
		duration = 0f;
		uri = null;
		title = null;
		programDate = -1;
		resetEncryptedInfo();
		resetPlatListInfo();
		discontinuity = false;
		return this;
	}

	public ElementBuilder encrypted(EncryptionInfo info) {
		this.encryptionInfo = info;
		return this;
	}

	public ElementBuilder encrypted(final URI uri, final String method,
			final String iv) {
		encryptionInfo = new ElementImpl.EncryptionInfoImpl(uri, method, iv);
		return this;
	}

	public Element create() {
		return new ElementImpl(playlistInfo, encryptionInfo, duration, uri,
				title, programDate, discontinuity);
	}

}
