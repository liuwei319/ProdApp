package com.kevin.prodapp.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * 网络请求工具类
 *
 * https 单项认证 参数带有context
 * <p/>
 *
 */
public class HttpsUrlConnection {
	private static final String DEFAULT_CHARSET = "UTF-8"; // 默认字符集
	private static final String _GET = "GET"; // GET
	private static final String _POST = "POST";// POST
	private static HostnameVerifier TRUSTED_VERIFIER;

	// static String mcookie=null;

	/**
	 * 初始化http请求参数
	 */
	private static HttpURLConnection initHttp(String url, String method,
											  Map<String, String> headers, Context cxt) throws IOException {
		URL _url = new URL(url);

		// String
		// host=android.net.Proxy.getDefaultHost();//通过andorid.net.Proxy可以获取默认的代理地址
		// int port
		// =android.net.Proxy.getDefaultPort();//通过andorid.net.Proxy可以获取默认的代理端口

		// SocketAddress sa= new InetSocketAddress(host,port);
		// Proxy pro = new Proxy(Proxy.Type.HTTP, sa);
		HttpURLConnection http = (HttpURLConnection) _url.openConnection();

		// 连接超时
		http.setConnectTimeout(25000);
		// 读取超时 --服务器响应比较慢，增大时间
		http.setReadTimeout(25000);
		http.setRequestMethod(method);
		http.setRequestProperty("Content-Type", "application/json");
//		http.setRequestProperty("Content-Type",
//				"application/x-www-form-urlencoded");
		http.setRequestProperty(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
		if (null != headers && !headers.isEmpty()) {
			for (Entry<String, String> entry : headers.entrySet()) {
				http.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		// http.setDoOutput(false);
		// http.setDoInput(true);
		http.connect();
		return http;
	}

	/**
	 * 验证服务器端证书
	 *
	 * @param url
	 * @param method
	 * @param headers
	 *            请求头
	 * @param cxt
	 *            上下文对象，用于获取证书输入流
	 * @return
	 */
	private static HttpsURLConnection initHttps(String url, String method,
												Map<String, String> headers, Context cxt) {
		HttpsURLConnection http = null;
		try {
			URL _url = new URL(url);
			http = (HttpsURLConnection) _url.openConnection();
//			CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
//			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//			keyStore.load(null);
//
//			// 从文件中获取
//			InputStream is = cxt.getAssets().open("root.crt");
//			// 从代码中获取
////			InputStream is = new Buffer().writeUtf8(ROOT_CA_CERT).inputStream();
//			// 签名文件设置证书
//			keyStore.setCertificateEntry("0", certificateFactory.generateCertificate(is));
//			is.close();
//			String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//			TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//			tmf.init(keyStore);
//			SSLContext context = SSLContext.getInstance("TLSv1");
//			context.init(null,tmf.getTrustManagers(),new SecureRandom());
//			http.setSSLSocketFactory(context.getSocketFactory());
//			http.setHostnameVerifier(getTrustedVerifier());
			http.setSSLSocketFactory(setCertificates(cxt.getAssets().open(
					"weblogic.cer")));
			// 连接超时
			http.setConnectTimeout(25000);
			// 读取超时 --服务器响应比较慢，增大时间
			http.setReadTimeout(25000);
			http.setRequestMethod(method);
			http.setRequestProperty("Content-Type",
					"application/json");
//			http.setRequestProperty("Content-Type",
//					"application/x-www-form-urlencoded");
			http.setRequestProperty(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
			if (null != headers && !headers.isEmpty()) {
				for (Entry<String, String> entry : headers.entrySet()) {
					Log.e("http-head", "key:" + entry.getKey() + " --value:"
							+ entry.getValue());
					http.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
			http.connect();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return http;
	}

	public static HttpsURLConnection Httpsdownload(String url, String method,
												   Map<String, String> headers, Context cxt) {

		HttpsURLConnection http = null;
		try {
			URL _url = new URL(url);
			http = (HttpsURLConnection) _url.openConnection();
			http.setSSLSocketFactory(setCertificates(cxt.getAssets().open(
					"weblogic.cer")));
			// 连接超时
			http.setConnectTimeout(25000);
			// 读取超时 --服务器响应比较慢，增大时间
			http.setReadTimeout(25000);
			http.setRequestMethod(method);
			http.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			http.setRequestProperty(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
			if (null != headers && !headers.isEmpty()) {
				for (Entry<String, String> entry : headers.entrySet()) {
					http.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
			// http.setDoOutput(true);
			// http.setDoInput(true);
			http.connect();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return http;
	}

	public static String get(String url, Map<String, String> params,
							 Map<String, String> headers, Context cxt, String action) {
		StringBuffer bufferRes = null;
		try {
			HttpURLConnection http = null;
			if (isHttps(url)) {
				http = initHttps(url, _GET, headers, cxt);
			} else {
				http = initHttp(initParams(url, params), _GET, headers, cxt);
			}
			InputStream in = http.getInputStream();
			String cookie = http.getHeaderField("Set-Cookie");
			String jksessionid = null;
			Log.e("cookie", cookie + "");
			if (cookie != null) {
				String[] s = cookie.split(";");
				jksessionid = s[0];
			}
//			SharePManager spm = new SharePManager(cxt,SharePManager.USER_FILE_NAME);
//			spm.putString("cookie", cookie);
//			spm.putString("jksessionid", jksessionid);
			BufferedReader read = new BufferedReader(new InputStreamReader(in,DEFAULT_CHARSET));
			String valueString = null;
			bufferRes = new StringBuffer();
			while ((valueString = read.readLine()) != null) {
				bufferRes.append(valueString);
			}
			in.close();
			if (http != null) {
				http.disconnect();// 关闭连接
			}
			return bufferRes.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * get请求
	 */
	public static String get(String url, Map<String, String> params,
							 Map<String, String> headers, Context cxt) {
		StringBuffer bufferRes = null;
		try {
			HttpURLConnection http = null;
			if (isHttps(url)) {
				http = initHttps(url, _GET, headers, cxt);
			} else {
				http = initHttp(initParams(url, params), _GET, headers, cxt);
			}

			int responsecode = http.getResponseCode();
			Log.e("responsecode", responsecode + "");
			if (responsecode == 200) {
				InputStream in = http.getInputStream();
				BufferedReader read = new BufferedReader(new InputStreamReader(
						in, DEFAULT_CHARSET));
				String valueString = null;
				bufferRes = new StringBuffer();
				while ((valueString = read.readLine()) != null) {
					bufferRes.append(valueString);
				}
				in.close();
				if (http != null) {
					http.disconnect();// 关闭连接
				}
				return bufferRes.toString();
			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * get请求
	 */
	public static String get(String url, Context cxt) {
		return get(url, null, cxt);
	}

	/**
	 * get请求
	 */
	public static String get(String url, Map<String, String> params, Context cxt) {
		return get(url, params, null, cxt);
	}

	/**
	 *
	 * post请求
	 */
	public static String post(String url, String params,
							  Map<String, String> headers, Context cxt, String action) {
		StringBuffer bufferRes = null;
		try {
			HttpURLConnection http = null;
			if (isHttps(url)) {
				http = initHttps(url, _POST, headers, cxt);
			} else {
				http = initHttp(url, _POST, headers, cxt);
			}
			if (params != null && params != "") {
//				OutputStream out = http.getOutputStream();
//				out.write(params.getBytes(DEFAULT_CHARSET));
//				out.flush();
//				out.close();
				DataOutputStream os = new DataOutputStream(http.getOutputStream());
				os.writeBytes(params);
				os.flush();
				os.close();
			}

			InputStream in = null;
			if (http.getResponseCode() == http.HTTP_OK
					|| http.getResponseCode() == http.HTTP_CREATED
					|| http.getResponseCode() == http.HTTP_ACCEPTED) {
				in = http.getInputStream();
			} else {
				in = http.getErrorStream();
				in.close();
				return null;
			}

//			String cookie = http.getHeaderField("Set-Cookie");
//			String jksessionid = null;
//			Log.e("cookie", cookie + "");
//			if (cookie != null) {
//				String[] s = cookie.split(";");
//				jksessionid = s[0];
//			}
//			Log.e("jksessionid", jksessionid + "");
//
//			SharePManager spm = new SharePManager(cxt,
//					SharePManager.USER_FILE_NAME);
//			spm.putString("cookie", cookie);
//			spm.putString("jksessionid", jksessionid);
			String cookie = "";
			String jksessionid = "";
			Map<String, List<String>> map=http.getHeaderFields();
			if (map.containsKey("Set-Cookie")) {
				List<String> cookies = map.get("Set-Cookie");
				StringBuilder builder = new StringBuilder();
				for (int i = 0; i < cookies.size(); i ++){
					builder.append(cookies.get(i));
					if (cookies.get(i).indexOf("JSESSIONID") != -1){
						jksessionid = cookies.get(i);
					}
				}
				cookie = builder.toString();
			}
			if (!cookie.equals("")) {
				SharePManager spm = new SharePManager(cxt,
						SharePManager.USER_FILE_NAME);
				spm.putString("cookie", cookie);
				spm.putString("jksessionid", jksessionid);
			}

			BufferedReader read = new BufferedReader(new InputStreamReader(in,
					DEFAULT_CHARSET));
			String valueString = null;
			bufferRes = new StringBuffer();
			while ((valueString = read.readLine()) != null) {
				bufferRes.append(valueString);
			}
			in.close();
			if (http != null) {
				http.disconnect();// 关闭连接
			}
			return bufferRes.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * post请求
	 */
	public static String post(String url, String params,
							  Map<String, String> headers, Context cxt) {
//		StringBuffer bufferRes = null;
		String result = "";
		try {
			HttpURLConnection http = null;
			if (isHttps(url)) {
				http = initHttps(url, _POST, headers, cxt);
			} else {
				http = initHttp(url, _POST, headers, cxt);
			}
			if (params != null && params != "") {
				OutputStream out = http.getOutputStream();
				out.write(params.getBytes(DEFAULT_CHARSET));
				out.flush();
				out.close();
			}

			int responsecode = http.getResponseCode();
			Log.e("responsecode", responsecode + "");
			if (responsecode == 200) {
				InputStream in = http.getInputStream();
				result = getTextFromStream(in);
//				BufferedReader read = new BufferedReader(new InputStreamReader(
//						in, DEFAULT_CHARSET));
//				String valueString = null;
//				bufferRes = new StringBuffer();
//				while ((valueString = read.readLine()) != null) {
//					bufferRes.append(valueString);
//				}
				String cookie = "";
				String jksessionid = "";
				Map<String, List<String>> map=http.getHeaderFields();
				if (map.containsKey("Set-Cookie")) {
					List<String> cookies = map.get("Set-Cookie");
					StringBuilder builder = new StringBuilder();
					for (int i = 0; i < cookies.size(); i ++){
						builder.append(cookies.get(i));
						if (cookies.get(i).indexOf("JSESSIONID") != -1){
							jksessionid = cookies.get(i);
						}
					}
					cookie = builder.toString();
				}
				if (!cookie.equals("")) {
					SharePManager spm = new SharePManager(cxt,
							SharePManager.USER_FILE_NAME);
					spm.putString("cookie", cookie);
					spm.putString("jksessionid", jksessionid);
				}
				in.close();
				if (http != null) {
					http.disconnect();// 关闭连接
				}
//				return bufferRes.toString();
				return result;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * post请求
	 */
	public static String post(String url, Map<String, String> params,
							  Context cxt) {
		return post(url, map2Url(params), null, cxt);
	}

	/**
	 * post请求
	 */
	public static String post(String url, Map<String, String> params,
							  Map<String, String> headers, Context cxt) {
		return post(url, map2Url(params), headers, cxt);
	}

	/**
	 * 方法名请求 post请求
	 */
	public static String post(String url, Map<String, String> params,
							  Map<String, String> headers, Context cxt, String action) {
		return post(url, map2Url(params), headers, cxt, action);
	}

	/**
	 * 初始化参数
	 */
	public static String initParams(String url, Map<String, String> params) {
		if (null == params || params.isEmpty()) {
			return url;
		}
		StringBuilder sb = new StringBuilder(url);
		if (url.indexOf("?") == -1) {
			sb.append("?");
		}
		sb.append(map2Url(params));
		return url + sb.toString();
	}

	/**
	 * map转url参数
	 */
	public static String map2Url(Map<String, String> paramToMap) {
		if (null == paramToMap || paramToMap.isEmpty()) {
			return null;
		}
		StringBuffer url = new StringBuffer();
		boolean isfist = true;
		for (Entry<String, String> entry : paramToMap.entrySet()) {
			if (isfist) {
				isfist = false;
			} else {
				url.append("&");
			}
			url.append(entry.getKey()).append("=");
			String value = entry.getValue();
			if (null != value && !"".equals(value.trim())) {
				try {
					url.append(URLEncoder.encode(value, DEFAULT_CHARSET));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return url.toString();
	}

	/**
	 * 检测是否https
	 */
	private static boolean isHttps(String url) {
		return url.startsWith("https");
	}

	/**
	 * 获取自定义证书流
	 *
	 * @param certificates
	 * @return
	 */
	public static SSLSocketFactory setCertificates(InputStream... certificates) {
		try {
			CertificateFactory certificateFactory = CertificateFactory
					.getInstance("X.509");
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			// 去掉系统默认证书
			keyStore.load(null);
			int index = 0;
			for (InputStream certificate : certificates) {
				String certificateAlias = Integer.toString(index++);
				// 设置自己的证书
				keyStore.setCertificateEntry(certificateAlias,
						certificateFactory.generateCertificate(certificate));

				try {
					if (certificate != null)
						certificate.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// 取得SSL的SSLContext实例
			SSLContext sslContext = SSLContext.getInstance("TLS");
			TrustManagerFactory trustManagerFactory = TrustManagerFactory
					.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(keyStore);

			// 初始化keystore
			// KeyStore clientKeyStore =
			// KeyStore.getInstance(KeyStore.getDefaultType());
			// clientKeyStore.load(SSLtestActivity.this.getAssets().open("itest_client.bks"),
			// "123456".toCharArray());
			//
			// KeyManagerFactory keyManagerFactory =
			// KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			// keyManagerFactory.init(clientKeyStore, "123456".toCharArray());
			//
			// sslContext.init(keyManagerFactory.getKeyManagers(),
			// trustManagerFactory.getTrustManagers(), new SecureRandom());
			sslContext.init(null, trustManagerFactory.getTrustManagers(),
					new SecureRandom());
			return sslContext.getSocketFactory();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	//读取放回信息
	public static String getTextFromStream(InputStream is) {

		try {
			int len = 0;
			byte[] b = new byte[1024];
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			while ((len = is.read(b)) != -1) {
				bos.write(b, 0, len);
			}

			String text = new String(bos.toByteArray());
			bos.close();
			return text;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static HostnameVerifier getTrustedVerifier() {
		if (TRUSTED_VERIFIER == null)
			TRUSTED_VERIFIER = new HostnameVerifier() {

				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

		return TRUSTED_VERIFIER;
	}
}
