package net.chilicat.m3u8;

import java.net.URI;

/**
 * @author dkuffner
 */
final class ElementImpl implements Element {
	private final PlayListInfo playlistInfo;
	private final EncryptionInfo encryptionInfo;
	private final Float duration;
	private final URI uri;
	private final String title;
	private final long programDate;
	private final boolean discontinuity;

	public ElementImpl(PlayListInfo playlistInfo,
			EncryptionInfo encryptionInfo, Float duration, URI uri,
			String title, long programDate, boolean discontinuity) {
		if (uri == null) {
			throw new NullPointerException("uri");
		}

		if (duration < -1) {
			throw new IllegalArgumentException();
		}
		if (playlistInfo != null && encryptionInfo != null) {
			throw new IllegalArgumentException(
					"Element cannot be a encrypted playlist.");
		}
		this.playlistInfo = playlistInfo;
		this.encryptionInfo = encryptionInfo;
		this.duration = duration;
		this.uri = uri;
		this.title = title;
		this.programDate = programDate;
		this.discontinuity = discontinuity;
	}

	public String getTitle() {
		return title;
	}

	public Float getDuration() {
		return duration;
	}

	public URI getURI() {
		return uri;
	}

	public boolean isEncrypted() {
		return encryptionInfo != null;
	}

	public boolean isPlayList() {
		return playlistInfo != null;
	}

	public boolean isMedia() {
		return playlistInfo == null;
	}

	public EncryptionInfo getEncryptionInfo() {
		return encryptionInfo;
	}

	public PlayListInfo getPlayListInfo() {
		return playlistInfo;
	}

	public long getProgramDate() {
		return programDate;
	}

	@Override
	public boolean isDiscontinuity() {
		return discontinuity;
	}

	@Override
	public String toString() {
		return "ElementImpl{" + "playlistInfo=" + playlistInfo
				+ ", encryptionInfo=" + encryptionInfo + ", duration="
				+ duration + ", uri=" + uri + ", title='" + title + '\'' + '}';
	}

	static final class PlayListInfoImpl implements PlayListInfo {
		private final int programId;
		private final int bandWidth;
		private final String codec;

		public PlayListInfoImpl(int programId, int bandWidth, String codec) {
			this.programId = programId;
			this.bandWidth = bandWidth;
			this.codec = codec;
		}

		public int getProgramId() {
			return programId;
		}

		public int getBandWitdh() {
			return bandWidth;
		}

		public String getCodecs() {
			return codec;
		}

		@Override
		public String toString() {
			return "PlayListInfoImpl{" + "programId=" + programId
					+ ", bandWidth=" + bandWidth + ", codec='" + codec + '\''
					+ '}';
		}
	}

	static final class EncryptionInfoImpl implements EncryptionInfo {
		private final URI uri;
		private final String method;
		private final String iv;

		public EncryptionInfoImpl(URI uri, String method, String iv) {
			this.uri = uri;
			this.method = method;
			this.iv = iv;
		}

		public URI getURI() {
			return uri;
		}

		public String getMethod() {
			return method;
		}

		public String getIv() {
			return iv;
		}

		@Override
		public String toString() {
			return "EncryptionInfoImpl{" + "uri=" + uri + ", method='" + method
					+ "', iv='" + iv + '\'' + '}';
		}
	}
}
