package com.sumavision.cachingwhileplaying.server;

import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.StringTokenizer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class CachingWhilePlayingServer extends CachingWhilePlayingNanoHTTPD {

	private Context context;

	private static final String TAG = "CachingWhilePlayingServer";
	/**
	 * Common mime type for dynamic content: binary
	 */
	public static final String MIME_DEFAULT_BINARY = "application/octet-stream";
	/**
	 * Default Index file names.
	 */

	public static final ArrayList<String> INDEX_FILE_NAMES = new ArrayList<String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			add("index.html");
			add("index.htm");
		}
	};
	/**
	 * Hashtable mapping (String)FILENAME_EXTENSION -> (String)MIME_TYPE
	 */
	private static final Map<String, String> MIME_TYPES = new HashMap<String, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put("css", "text/css");
			put("htm", "text/html");
			put("html", "text/html");
			put("xml", "text/xml");
			put("java", "text/x-java-source, text/java");
			put("md", "text/plain");
			put("txt", "text/plain");
			put("asc", "text/plain");
			put("gif", "image/gif");
			put("jpg", "image/jpeg");
			put("jpeg", "image/jpeg");
			put("png", "image/png");
			put("mp3", "audio/mpeg");
			put("m3u", "audio/mpeg-url");
			put("mp4", "video/mp4");
			put("ogv", "video/ogg");
			put("flv", "video/x-flv");
			put("mov", "video/quicktime");
			put("swf", "application/x-shockwave-flash");
			put("js", "application/javascript");
			put("pdf", "application/pdf");
			put("doc", "application/msword");
			put("ogg", "application/x-ogg");
			put("zip", "application/octet-stream");
			put("exe", "application/octet-stream");
			put("class", "application/octet-stream");
		}
	};
	/**
	 * The distribution licence
	 */
	private static final String LICENCE = "Copyright (c) 2012-2013 by Paul S. Hawke, 2001,2005-2013 by Jarno Elonen, 2010 by Konstantinos Togias\n"
			+ "\n"
			+ "Redistribution and use in source and binary forms, with or without\n"
			+ "modification, are permitted provided that the following conditions\n"
			+ "are met:\n"
			+ "\n"
			+ "Redistributions of source code must retain the above copyright notice,\n"
			+ "this list of conditions and the following disclaimer. Redistributions in\n"
			+ "binary form must reproduce the above copyright notice, this list of\n"
			+ "conditions and the following disclaimer in the documentation and/or other\n"
			+ "materials provided with the distribution. The name of the author may not\n"
			+ "be used to endorse or promote products derived from this software without\n"
			+ "specific prior written permission. \n"
			+ " \n"
			+ "THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR\n"
			+ "IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES\n"
			+ "OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.\n"
			+ "IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,\n"
			+ "INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT\n"
			+ "NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,\n"
			+ "DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY\n"
			+ "THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT\n"
			+ "(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE\n"
			+ "OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.";
	private static Map<String, CachingWhilePlayingWebServerPlugin> mimeTypeHandlers = new HashMap<String, CachingWhilePlayingWebServerPlugin>();
	private final List<File> rootDirs;
	private final boolean quiet;

	public CachingWhilePlayingServer(String host, int port, File wwwroot,
			boolean quiet) {
		super(host, port);
		this.quiet = quiet;
		this.rootDirs = new ArrayList<File>();
		this.rootDirs.add(wwwroot);
	}

	public CachingWhilePlayingServer(String host, int port, File wwwroot,
			boolean quiet, Context context) {
		super(host, port);
		this.quiet = quiet;
		this.context = context;
		this.rootDirs = new ArrayList<File>();
		this.rootDirs.add(wwwroot);
	}

	public CachingWhilePlayingServer(String host, int port,
			List<File> wwwroots, boolean quiet) {
		super(host, port);
		this.quiet = quiet;
		this.rootDirs = new ArrayList<File>(wwwroots);
	}

	/**
	 * Starts as a standalone file server and waits for Enter.
	 */
	@SuppressLint("NewApi")
	public static void main(String[] args) {
		// Defaults
		int port = 8080;

		String host = "127.0.0.1";
		List<File> rootDirs = new ArrayList<File>();
		boolean quiet = false;
		Map<String, String> options = new HashMap<String, String>();

		// Parse command-line, with short and long versions of the options.
		for (int i = 0; i < args.length; ++i) {
			if (args[i].equalsIgnoreCase("-h")
					|| args[i].equalsIgnoreCase("--host")) {
				host = args[i + 1];
			} else if (args[i].equalsIgnoreCase("-p")
					|| args[i].equalsIgnoreCase("--port")) {
				port = Integer.parseInt(args[i + 1]);
			} else if (args[i].equalsIgnoreCase("-q")
					|| args[i].equalsIgnoreCase("--quiet")) {
				quiet = true;
			} else if (args[i].equalsIgnoreCase("-d")
					|| args[i].equalsIgnoreCase("--dir")) {
				// rootDirs.add(new File(args[i + 1]).getAbsoluteFile());
			} else if (args[i].equalsIgnoreCase("--licence")) {
				System.out.println(LICENCE + "\n");
			} else if (args[i].startsWith("-X:")) {
				int dot = args[i].indexOf('=');
				if (dot > 0) {
					String name = args[i].substring(0, dot);
					String value = args[i].substring(dot + 1, args[i].length());
					options.put(name, value);
				}
			}
		}

		if (rootDirs.isEmpty()) {
			rootDirs.add(new File(".").getAbsoluteFile());
		}

		options.put("host", host);
		options.put("port", "" + port);
		options.put("quiet", String.valueOf(quiet));
		StringBuilder sb = new StringBuilder();
		for (File dir : rootDirs) {
			if (sb.length() > 0) {
				sb.append(":");
			}
			try {
				sb.append(dir.getCanonicalPath());
			} catch (IOException ignored) {
			}
		}
		options.put("home", sb.toString());

		ServiceLoader<CachingWhilePlayingWebServerPluginInfo> serviceLoader = ServiceLoader
				.load(CachingWhilePlayingWebServerPluginInfo.class);
		for (CachingWhilePlayingWebServerPluginInfo info : serviceLoader) {
			String[] mimeTypes = info.getMimeTypes();
			for (String mime : mimeTypes) {
				String[] indexFiles = info.getIndexFilesForMimeType(mime);
				if (!quiet) {
					System.out.print("# Found plugin for Mime type: \"" + mime
							+ "\"");
					if (indexFiles != null) {
						System.out.print(" (serving index files: ");
						for (String indexFile : indexFiles) {
							System.out.print(indexFile + " ");
						}
					}
					System.out.println(").");
				}
				registerPluginForMimeType(indexFiles, mime,
						info.getWebServerPlugin(mime), options);
			}
		}

		CachingWhilePlayingServerRunner
				.executeInstance(new CachingWhilePlayingServer(host, port,
						rootDirs, quiet));
	}

	private static void registerPluginForMimeType(String[] indexFiles,
			String mimeType, CachingWhilePlayingWebServerPlugin plugin,
			Map<String, String> commandLineOptions) {
		if (mimeType == null || plugin == null) {
			return;
		}

		if (indexFiles != null) {
			for (String filename : indexFiles) {
				int dot = filename.lastIndexOf('.');
				if (dot >= 0) {
					String extension = filename.substring(dot + 1)
							.toLowerCase();
					MIME_TYPES.put(extension, mimeType);
				}
			}
			INDEX_FILE_NAMES.addAll(Arrays.asList(indexFiles));
		}
		mimeTypeHandlers.put(mimeType, plugin);
		plugin.initialize(commandLineOptions);
	}

	private List<File> getRootDirs() {
		return rootDirs;
	}

	/**
	 * URL-encodes everything between "/"-characters. Encodes spaces as '%20'
	 * instead of '+'.
	 */
	private String encodeUri(String uri) {
		String newUri = "";
		StringTokenizer st = new StringTokenizer(uri, "/ ", true);
		while (st.hasMoreTokens()) {
			String tok = st.nextToken();
			if (tok.equals("/"))
				newUri += "/";
			else if (tok.equals(" "))
				newUri += "%20";
			else {
				try {
					newUri += URLEncoder.encode(tok, "UTF-8");
				} catch (UnsupportedEncodingException ignored) {
				}
			}
		}
		return newUri;
	}

	@SuppressLint("NewApi")
	public Response serve(int socketCount, IHTTPSession session) {
		Map<String, String> header = session.getHeaders();
		Map<String, String> parms = session.getParms();
		String uri = session.getUri();

		if (!quiet) {
			// Log.i(TAG, "current socketCount" + socketCount);
			System.out.println(session.getMethod() + " '" + uri + "' ");

			Iterator<String> e = header.keySet().iterator();
			while (e.hasNext()) {
				String value = e.next();
				// System.out.println("  HDR: '" + value + "' = '"
				// + header.get(value) + "'");
			}
			e = parms.keySet().iterator();
			while (e.hasNext()) {
				String value = e.next();
				// System.out.println("  PRM: '" + value + "' = '"
				// + parms.get(value) + "'");
			}
		}

		for (File homeDir : getRootDirs()) {
			// Make sure we won't die of an exception later
			if (!homeDir.isDirectory()) {
				return createResponse(Response.Status.INTERNAL_ERROR,
						CachingWhilePlayingNanoHTTPD.MIME_PLAINTEXT,
						"INTERNAL ERRROR: given path is not a directory ("
								+ homeDir + ").");
			}
		}
		return respond(socketCount, Collections.unmodifiableMap(header), uri);
	}

	private Response respond(int socketCount, Map<String, String> headers,
			String uri) {
		// Remove URL arguments
		uri = uri.trim().replace(File.separatorChar, '/');
		if (uri.indexOf('?') >= 0) {
			uri = uri.substring(0, uri.indexOf('?'));
		}

		// Prohibit getting out of current directory
		if (uri.startsWith("src/main") || uri.endsWith("src/main")
				|| uri.contains("../")) {
			return createResponse(Response.Status.FORBIDDEN,
					CachingWhilePlayingNanoHTTPD.MIME_PLAINTEXT,
					"FORBIDDEN: Won't serve ../ for security reasons.");
		}

		boolean canServeUri = false;
		File homeDir = null;
		List<File> roots = getRootDirs();
		for (int i = 0; !canServeUri && i < roots.size(); i++) {
			homeDir = roots.get(i);
			canServeUri = canServeUri(uri, homeDir);
		}
		// TODO add 2014 3yue 1
		canServeUri = true;
		if (!canServeUri) {
			return createResponse(Response.Status.NOT_FOUND,
					CachingWhilePlayingNanoHTTPD.MIME_PLAINTEXT,
					"Error 404, file not found.");
		}

		// Browsers get confused without '/' after the directory, send a
		// redirect.
		File f = new File(homeDir, uri);
		if (f.isDirectory() && !uri.endsWith("/")) {
			uri += "/";
			Response res = createResponse(Response.Status.REDIRECT,
					CachingWhilePlayingNanoHTTPD.MIME_HTML,
					"<html><body>Redirected: <a href=\"" + uri + "\">" + uri
							+ "</a></body></html>");
			res.addHeader("Location", uri);
			return res;
		}

		if (f.isDirectory()) {
			// First look for index files (index.html, index.htm, etc) and if
			// none found, list the directory if readable.
			String indexFile = findIndexFileInDirectory(f);
			if (indexFile == null) {
				if (f.canRead()) {
					// No index file, list the directory if it is readable
					return createResponse(Response.Status.OK,
							CachingWhilePlayingNanoHTTPD.MIME_HTML,
							listDirectory(uri, f));
				} else {
					return createResponse(Response.Status.FORBIDDEN,
							CachingWhilePlayingNanoHTTPD.MIME_PLAINTEXT,
							"FORBIDDEN: No directory listing.");
				}
			} else {
				return respond(socketCount, headers, uri + indexFile);
			}
		}

		String mimeTypeForFile = getMimeTypeForFile(uri);
		CachingWhilePlayingWebServerPlugin plugin = mimeTypeHandlers
				.get(mimeTypeForFile);
		Response response = null;
		if (plugin != null) {
			response = plugin.serveFile(uri, headers, f, mimeTypeForFile);
			if (response != null
					&& response instanceof CachingWhilePlayingInternalRewrite) {
				CachingWhilePlayingInternalRewrite rewrite = (CachingWhilePlayingInternalRewrite) response;
				return respond(socketCount, rewrite.getHeaders(),
						rewrite.getUri());
			}
		} else {
			response = serveFile(socketCount, uri, headers, f, mimeTypeForFile);
		}
		return response != null ? response : createResponse(
				Response.Status.NOT_FOUND,
				CachingWhilePlayingNanoHTTPD.MIME_PLAINTEXT,
				"Error 404, file not found.");
	}

	private boolean canServeUri(String uri, File homeDir) {
		boolean canServeUri;
		File f = new File(homeDir, uri);
		canServeUri = f.exists();
		if (!canServeUri) {
			String mimeTypeForFile = getMimeTypeForFile(uri);
			CachingWhilePlayingWebServerPlugin plugin = mimeTypeHandlers
					.get(mimeTypeForFile);
			if (plugin != null) {
				canServeUri = plugin.canServeUri(uri, homeDir);
			}
		}
		return canServeUri;
	}

	/**
	 * Serves file from homeDir and its' subdirectories (only). Uses only URI,
	 * ignores all headers and HTTP parameters.
	 */

	public static boolean seeked = false;

	private String lastUrl;
	private static WebHandler segHandler;

	public static boolean checkExist(String uri) {
		if (uri == null || uri.equals(""))
			return true;
		boolean isM3u8 = WebUtil.isM3u8(uri);
		if (isM3u8) {
			return segHandler.getM3u8Exist();
		}
		boolean isSegUrl = WebUtil.isSegUrl(uri);
		if (isSegUrl) {
			return segHandler.getSegExists(uri);
		}
		return false;
	}

	Response serveFile(int socketCount, String uri, Map<String, String> header,
			File file, String mime) {
		Response res;
		try {
			// TODO static handler or instance handler
			Log.w(TAG, socketCount + " " + "serveFile uri=" + uri);

			boolean isM3u8 = WebUtil.isM3u8(uri);
			if (isM3u8) {
				WebHandler handler = new WebHandler(context, uri,
						WebHandler.TYPE_M3U8, segsInfo);
				segHandler = handler;
				boolean m3u8Exists = handler.getM3u8Exist();
				// boolean m3u8Exists = segsDownloadInfo.get(uri);
				if (m3u8Exists) {
					Log.e(TAG, socketCount + " " + "serve file m3u8Exists");
				} else {
					Log.e(TAG, socketCount + " " + "serve file m3u8 not Exists");
					int m3u8CheckCount = 0;
					while (!handler.getM3u8Exist() && m3u8CheckCount < 300) {
						// while (!file.exists() && m3u8CheckCount < 10) {
						try {
							Thread.sleep(100);
							m3u8CheckCount++;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if (m3u8CheckCount >= 300) {
						Log.i(TAG, "m3u8CheckCount >= 300");
						res = createResponse(Response.Status.INTERNAL_ERROR,
								CachingWhilePlayingNanoHTTPD.MIME_PLAINTEXT,
								"FORBIDDEN: Reading file failed.");
						return res;

					}
				}
			}
			boolean isSegUrl = WebUtil.isSegUrl(uri);
			if (isSegUrl) {

				if (segHandler == null) {
					segHandler = new WebHandler(context, uri,
							WebHandler.TYPE_SEG, segsInfo);
				} else {
					segHandler.resetUrl(uri);
				}

				String[] a = uri.split("/");
				String filename = a[a.length - 1];
				curReqSegIndex = Integer.valueOf(filename.substring(0,
						filename.indexOf(".")));

				// if (curReqSegIndex == -1) {
				// InputStream fis = context.getAssets().open("my.ts");
				// res = createResponse(Response.Status.PARTIAL_CONTENT, mime,
				// fis);
				// res.setChunkedTransfer(true);
				// Log.i(TAG, socketCount + " " + "OK, file: /assets/0002.ts");
				// return res;
				// }

				boolean segExists = segHandler.getSegExists(uri);

//				if (segsInfo.get(Integer.valueOf(filename.substring(0,
//						filename.indexOf(".")))).isBroken) {
//					// 更换账号20次后，仍无法下载成功，则通知播放器，跳过此段
//					res = createResponse(Response.Status.FORBIDDEN,
//							CachingWhilePlayingNanoHTTPD.MIME_PLAINTEXT,
//							"FORBIDDEN");
//					Log.e(TAG, socketCount + " " + "FORBIDDEN, file:" + file
//							+ " broken");
//					return res;
//				}

				if (segExists) {
					Log.e(TAG, socketCount + " " + "serve file segExists");
				} else {
					Log.e(TAG, socketCount + " " + "serve file not segExists");
					HttpURLConnection conn;
					BufferedInputStream bis;
					InputStream is;
					String httpUrl;
					try {
						httpUrl = segsInfo.get(curReqSegIndex).downloadUrl;
						URL url = new URL(httpUrl);
						conn = (HttpURLConnection) url.openConnection();
						byte[] buf = new byte[1024 * 8];
						conn.setReadTimeout(5000);
						conn.setConnectTimeout(5000);
						conn.setInstanceFollowRedirects(true);
						conn.setRequestProperty("Charset", "UTF-8");
						conn.setRequestProperty("User-Agent",
								"AppleCoreMedia/1.0.0.9B206 (iPad; U; CPU OS 5_1_1 like Mac OS X; zh_cn)");
						int status = conn.getResponseCode();
						if (status == 200) {
							is = conn.getInputStream();
							bis = new BufferedInputStream(is);
							String etag = Integer.toHexString((file
									.getAbsolutePath()
									+ file.lastModified()
									+ "" + file.length()).hashCode());

							res = createResponse(
									Response.Status.PARTIAL_CONTENT, mime, bis);
							res.setChunkedTransfer(true);
							res.addHeader("ETag", etag);
							Log.i(TAG, socketCount + " " + "OK, file:" + file);

						} else {
//							if (Integer.valueOf(filename.substring(0,
//									filename.indexOf("."))) == 0
//									&& m3u8Info.m3u8.contains("pcs.baidu")) {
//								Thread.sleep(10000);
//							}
							res = createResponse(
									Response.Status.INTERNAL_ERROR,
									CachingWhilePlayingNanoHTTPD.MIME_PLAINTEXT,
									"INTERNAL_ERROR: Reading file failed.");
							Log.i(TAG, socketCount + " "
									+ "INTERNAL_ERROR, file:" + file
									+ ", status code:" + status + "\t");
						}
						return res;
					} catch (SocketTimeoutException e) {
						e.printStackTrace();
						res = createResponse(Response.Status.INTERNAL_ERROR,
								CachingWhilePlayingNanoHTTPD.MIME_PLAINTEXT,
								"INTERNAL_ERROR: Reading file failed.");
						Log.i(TAG, socketCount + " " + "SocketTimeout, file:"
								+ file);
						return res;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			// Calculate etag
			String etag = Integer.toHexString((file.getAbsolutePath()
					+ file.lastModified() + "" + file.length()).hashCode());

			long fileLen = file.length();

			FileInputStream fis = new FileInputStream(file);
			res = createResponse(Response.Status.PARTIAL_CONTENT, mime, fis);
			res.setChunkedTransfer(true);
			res.addHeader("ETag", etag);
			Log.i(TAG, socketCount + " " + "OK, file:" + file + " filelen:"
					+ fileLen);
		} catch (IOException ioe) {
			// ioe.printStackTrace();
			res = createResponse(Response.Status.INTERNAL_ERROR,
					CachingWhilePlayingNanoHTTPD.MIME_PLAINTEXT,
					"INTERNAL_ERROR: Reading file failed.");
			Log.i(TAG, socketCount + " " + "INTERNAL_ERROR, file:" + file);
		}

		return res;
	}

	// Get MIME type from file name extension, if possible
	private String getMimeTypeForFile(String uri) {
		int dot = uri.lastIndexOf('.');
		String mime = null;
		if (dot >= 0) {
			mime = MIME_TYPES.get(uri.substring(dot + 1).toLowerCase());
		}
		return mime == null ? MIME_DEFAULT_BINARY : mime;
	}

	// Announce that the file server accepts partial content requests
	private Response createResponse(Response.Status status, String mimeType,
			InputStream message) {
		Response res = new Response(status, mimeType, message);
		res.addHeader("Accept-Ranges", "bytes");
		return res;
	}

	// Announce that the file server accepts partial content requests
	private Response createResponse(Response.Status status, String mimeType,
			String message) {
		Response res = new Response(status, mimeType, message);
		res.addHeader("Accept-Ranges", "bytes");
		return res;
	}

	private String findIndexFileInDirectory(File directory) {
		for (String fileName : INDEX_FILE_NAMES) {
			File indexFile = new File(directory, fileName);
			if (indexFile.exists()) {
				return fileName;
			}
		}
		return null;
	}

	private String listDirectory(String uri, File f) {
		String heading = "Directory " + uri;
		StringBuilder msg = new StringBuilder("<html><head><title>" + heading
				+ "</title><style><!--\n"
				+ "span.dirname { font-weight: bold; }\n"
				+ "span.filesize { font-size: 75%; }\n" + "// -->\n"
				+ "</style>" + "</head><body><h1>" + heading + "</h1>");

		String up = null;
		if (uri.length() > 1) {
			String u = uri.substring(0, uri.length() - 1);
			int slash = u.lastIndexOf('/');
			if (slash >= 0 && slash < u.length()) {
				up = uri.substring(0, slash + 1);
			}
		}

		List<String> files = Arrays.asList(f.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return new File(dir, name).isFile();
			}
		}));
		Collections.sort(files);
		List<String> directories = Arrays.asList(f.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return new File(dir, name).isDirectory();
			}
		}));
		Collections.sort(directories);
		if (up != null || directories.size() + files.size() > 0) {
			msg.append("<ul>");
			if (up != null || directories.size() > 0) {
				msg.append("<section class=\"directories\">");
				if (up != null) {
					msg.append("<li><a rel=\"directory\" href=\"")
							.append(up)
							.append("\"><span class=\"dirname\">..</span></a></b></li>");
				}
				for (String directory : directories) {
					String dir = directory + "/";
					msg.append("<li><a rel=\"directory\" href=\"")
							.append(encodeUri(uri + dir))
							.append("\"><span class=\"dirname\">").append(dir)
							.append("</span></a></b></li>");
				}
				msg.append("</section>");
			}
			if (files.size() > 0) {
				msg.append("<section class=\"files\">");
				for (String file : files) {
					msg.append("<li><a href=\"").append(encodeUri(uri + file))
							.append("\"><span class=\"filename\">")
							.append(file).append("</span></a>");
					File curFile = new File(f, file);
					long len = curFile.length();
					msg.append("&nbsp;<span class=\"filesize\">(");
					if (len < 1024) {
						msg.append(len).append(" bytes");
					} else if (len < 1024 * 1024) {
						msg.append(len / 1024).append(".")
								.append(len % 1024 / 10 % 100).append(" KB");
					} else {
						msg.append(len / (1024 * 1024)).append(".")
								.append(len % (1024 * 1024) / 10 % 100)
								.append(" MB");
					}
					msg.append(")</span></li>");
				}
				msg.append("</section>");
			}
			msg.append("</ul>");
		}
		msg.append("</body></html>");
		return msg.toString();
	}
}
