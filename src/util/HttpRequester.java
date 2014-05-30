package util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * HTTP请求对象
 * 
 * @author King 用法： HttpRequester request = new HttpRequester(); HttpRespons hr
 *         = request.sendGet("http://www.csdn.net");
 * 
 *         System.out.println(hr.getUrlString());
 *         System.out.println(hr.getProtocol());
 *         System.out.println(hr.getHost()); System.out.println(hr.getPort());
 *         System.out.println(hr.getContentEncoding());
 *         System.out.println(hr.getMethod());
 *         System.out.println(hr.getContent());
 */
public class HttpRequester {
	private String defaultContentEncoding;

	public HttpRequester() {
		this.defaultContentEncoding = Charset.defaultCharset().name();
	}

	/**
	 * 发送GET请求
	 * 
	 * @param urlString
	 *            URL地址
	 * @param timeout
	 * @return 响应对象
	 * @throws IOException
	 */
	public HttpRespons sendGet(String urlString, int timeout)
			throws IOException {
		return this.send(urlString, "GET", null, null, timeout);
	}

	/**
	 * 发送GET请求
	 * 
	 * @param urlString
	 *            URL地址
	 * @param params
	 *            参数集合
	 * @param timeout
	 * @return 响应对象
	 * @throws IOException
	 */
	public HttpRespons sendGet(String urlString,
			HashMap<String, Object> params, int timeout) throws IOException {
		return this.send(urlString, "GET", params, null, timeout);
	}

	/**
	 * 发送GET请求
	 * 
	 * @param urlString
	 *            URL地址
	 * @param params
	 *            参数集合
	 * @param propertys
	 *            请求属性
	 * @param timeout
	 * @return 响应对象
	 * @throws IOException
	 */
	public HttpRespons sendGet(String urlString,
			HashMap<String, Object> params, Map<String, String> propertys,
			int timeout) throws IOException {
		return this.send(urlString, "GET", params, propertys, timeout);
	}

	/**
	 * 发送POST请求
	 * 
	 * @param urlString
	 *            URL地址
	 * @param timeout
	 * @return 响应对象
	 * @throws IOException
	 */
	public HttpRespons sendPost(String urlString, int timeout)
			throws IOException {
		return this.send(urlString, "POST", null, null, timeout);
	}

	/**
	 * 发送POST请求
	 * 
	 * @param urlString
	 *            URL地址
	 * @param params
	 *            参数集合
	 * @param timeout
	 * @return 响应对象
	 * @throws IOException
	 */
	public HttpRespons sendPost(String urlString,
			HashMap<String, Object> params, int timeout) throws IOException {
		return this.send(urlString, "POST", params, null, timeout);
	}

	/**
	 * 发送POST请求
	 * 
	 * @param urlString
	 *            URL地址
	 * @param params
	 *            参数集合
	 * @param propertys
	 *            请求属性
	 * @param timeout
	 * @return 响应对象
	 * @throws IOException
	 */
	public HttpRespons sendPost(String urlString,
			HashMap<String, Object> params, Map<String, String> propertys,
			int timeout) throws IOException {
		return this.send(urlString, "POST", params, propertys, timeout);
	}

	/**
	 * 发送POST 为了请求ios充值接口的POST请求
	 * 
	 * @param urlStr
	 * @param params
	 * @throws IOException
	 */
	public static String sendNewPost(String urlStr,
			HashMap<String, Object> params) throws IOException {
		StringBuffer sb = new StringBuffer("");
		try {
			// 创建连接
			URL url = new URL(urlStr);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			connection.connect();

			// POST请求
			DataOutputStream out = new DataOutputStream(
					connection.getOutputStream());
			GSONObject obj = new GSONObject();
			for (String key : params.keySet()) {
				obj.put(key, params.get(key));
			}

			out.writeBytes(obj.toString());
			out.flush();
			out.close();

			// 读取响应
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String lines;

			while ((lines = reader.readLine()) != null) {
				lines = new String(lines.getBytes(), "utf-8");
				sb.append(lines);
			}
			System.out.println(sb);
			reader.close();
			// 断开连接
			connection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw e;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		return sb.toString();

	}

	/**
	 * 发送HTTP请求
	 * 
	 * @param urlString
	 * @param timeout
	 * @return 响映对象
	 * @throws IOException
	 */
	private HttpRespons send(String urlString, String method,
			HashMap<String, Object> parameters, Map<String, String> propertys,
			int timeout) throws IOException {
		HttpURLConnection urlConnection = null;
		if (method.equalsIgnoreCase("GET") && parameters != null) {
			StringBuffer param = new StringBuffer();
			int i = 0;
			for (String key : parameters.keySet()) {
				if (i == 0)
					param.append("?");
				else
					param.append("&");
				param.append(key).append("=").append(parameters.get(key));
				i++;
			}
			urlString += param;
		}
		URL url = new URL(urlString);
		urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setRequestMethod(method);
		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);
		urlConnection.setUseCaches(false);
		urlConnection.setReadTimeout(timeout);

		if (propertys != null)
			for (String key : propertys.keySet()) {
				urlConnection.addRequestProperty(key, propertys.get(key));
			}

		if (method.equalsIgnoreCase("POST") && parameters != null) {
			StringBuffer param = new StringBuffer();
			for (String key : parameters.keySet()) {
				param.append("&");
				param.append(key).append("=").append(parameters.get(key));
			}
			urlConnection.getOutputStream().write(param.toString().getBytes());
			urlConnection.getOutputStream().flush();
			urlConnection.getOutputStream().close();
		}

		return this.makeContent(urlString, urlConnection);
	}

	/**
	 * 得到响应对象
	 * 
	 * @param urlConnection
	 * @return 响应对象
	 * @throws IOException
	 */
	private HttpRespons makeContent(String urlString,
			HttpURLConnection urlConnection) throws IOException {
		HttpRespons httpResponser = new HttpRespons();
		try {
			InputStream in = urlConnection.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(in));
			httpResponser.contentCollection = new Vector<String>();
			StringBuffer temp = new StringBuffer();
			String line = bufferedReader.readLine();
			while (line != null) {
				httpResponser.contentCollection.add(line);
				temp.append(line);
				// temp.append(line).append("\r\n");
				line = bufferedReader.readLine();
			}
			bufferedReader.close();

			String ecod = urlConnection.getContentEncoding();
			if (ecod == null)
				ecod = this.defaultContentEncoding;

			httpResponser.urlString = urlString;

			httpResponser.defaultPort = urlConnection.getURL().getDefaultPort();
			httpResponser.file = urlConnection.getURL().getFile();
			httpResponser.host = urlConnection.getURL().getHost();
			httpResponser.path = urlConnection.getURL().getPath();
			httpResponser.port = urlConnection.getURL().getPort();
			httpResponser.protocol = urlConnection.getURL().getProtocol();
			httpResponser.query = urlConnection.getURL().getQuery();
			httpResponser.ref = urlConnection.getURL().getRef();
			httpResponser.userInfo = urlConnection.getURL().getUserInfo();

			httpResponser.content = new String(temp.toString().getBytes(), ecod);
			httpResponser.contentEncoding = ecod;
			httpResponser.code = urlConnection.getResponseCode();
			httpResponser.message = urlConnection.getResponseMessage();
			httpResponser.contentType = urlConnection.getContentType();
			httpResponser.method = urlConnection.getRequestMethod();
			httpResponser.connectTimeout = urlConnection.getConnectTimeout();
			httpResponser.readTimeout = urlConnection.getReadTimeout();

			return httpResponser;
		} catch (IOException e) {
			throw e;
		} finally {
			if (urlConnection != null)
				urlConnection.disconnect();
		}
	}

	/**
	 * 默认的响应字符集
	 */
	public String getDefaultContentEncoding() {
		return this.defaultContentEncoding;
	}

	/**
	 * 设置默认的响应字符集
	 */
	public void setDefaultContentEncoding(String defaultContentEncoding) {
		this.defaultContentEncoding = defaultContentEncoding;
	}
}