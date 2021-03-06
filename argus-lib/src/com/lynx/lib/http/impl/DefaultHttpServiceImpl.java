package com.lynx.lib.http.impl;

import com.lynx.lib.http.HttpService;
import com.lynx.lib.http.core.HttpParam;
import com.lynx.lib.http.HttpCallback;
import com.lynx.lib.http.handler.HttpHandler;
import com.lynx.lib.http.handler.RetryHandler;
import com.lynx.lib.http.handler.SyncRequestHandler;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;

/**
 * 
 * @author zhufeng.liu
 * 
 * @addtime 2013-4-17 下午11:26:48
 */
public class DefaultHttpServiceImpl implements HttpService {
	private static final int DEFAULT_SOCKET_BUFFER_SIZE = 8 * 1024; // 8KB
	private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	private static final String ENCODING_GZIP = "gzip";

	private static int maxConnections = 10; // http请求最大并发连接数
	private static int socketTimeout = 10 * 1000; // 超时时间，默认10秒
	private static int maxRetries = 5;// 错误尝试次数，错误异常表请在RetryHandler添加
	private static int httpThreadCount = 3;// http线程池数量

	private final DefaultHttpClient httpClient;
	private final HttpContext httpContext;
	private String charset = "utf-8";

	private final Map<String, String> clientHeaderMap;

	private static final ThreadFactory sThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		public Thread newThread(Runnable r) {
			Thread tread = new Thread(r, "FinalHttp #"
					+ mCount.getAndIncrement());
			tread.setPriority(Thread.NORM_PRIORITY - 1);
			return tread;
		}
	};

	private static final Executor executor = Executors.newFixedThreadPool(
			httpThreadCount, sThreadFactory);

	public DefaultHttpServiceImpl() {
		BasicHttpParams httpParams = new BasicHttpParams();

		ConnManagerParams.setTimeout(httpParams, socketTimeout);
		ConnManagerParams.setMaxConnectionsPerRoute(httpParams,
				new ConnPerRouteBean(maxConnections));
		ConnManagerParams.setMaxTotalConnections(httpParams, 10);

		HttpConnectionParams.setSoTimeout(httpParams, socketTimeout);
		HttpConnectionParams.setConnectionTimeout(httpParams, socketTimeout);
		HttpConnectionParams.setTcpNoDelay(httpParams, true);
		HttpConnectionParams.setSocketBufferSize(httpParams,
				DEFAULT_SOCKET_BUFFER_SIZE);

		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory
				.getSocketFactory(), 443));
		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(
				httpParams, schemeRegistry);

		httpContext = new SyncBasicHttpContext(new BasicHttpContext());
		httpClient = new DefaultHttpClient(cm, httpParams);
		httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
			public void process(HttpRequest request, HttpContext context) {
				if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
					request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
				}
				for (String header : clientHeaderMap.keySet()) {
					request.addHeader(header, clientHeaderMap.get(header));
				}
			}
		});

		httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
			public void process(HttpResponse response, HttpContext context) {
				final HttpEntity entity = response.getEntity();
				if (entity == null) {
					return;
				}
				final Header encoding = entity.getContentEncoding();
				if (encoding != null) {
					for (HeaderElement element : encoding.getElements()) {
						if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
							response.setEntity(new InflatingEntity(response
									.getEntity()));
							break;
						}
					}
				}
			}
		});

		httpClient.setHttpRequestRetryHandler(new RetryHandler(maxRetries));

		clientHeaderMap = new HashMap<String, String>();

	}

	public HttpClient httpClient() {
		return this.httpClient;
	}

	public HttpContext httpContext() {
		return this.httpContext;
	}

	public void setCharset(String charSet) {
		if (charSet != null && charSet.trim().length() != 0)
			this.charset = charSet;
	}

	public void setCookieStore(CookieStore cookieStore) {
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	}

	public void setUserAgent(String userAgent) {
		HttpProtocolParams.setUserAgent(this.httpClient.getParams(), userAgent);
	}

	/**
	 * 设置网络连接超时时间，默认为10秒钟
	 * 
	 * @param timeout
	 */
	public void setTimeout(int timeout) {
		final HttpParams httpParams = this.httpClient.getParams();
		ConnManagerParams.setTimeout(httpParams, timeout);
		HttpConnectionParams.setSoTimeout(httpParams, timeout);
		HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
	}

	/**
	 * 设置https请求时 的 SSLSocketFactory
	 * 
	 * @param sslSocketFactory
	 */
	public void setSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
		Scheme scheme = new Scheme("https", sslSocketFactory, 443);
		this.httpClient.getConnectionManager().getSchemeRegistry()
				.register(scheme);
	}

	/**
	 * 配置错误重试次数
	 * 
	 * @param count
	 */
	public void setRequestExecutionRetryCount(int count) {
		this.httpClient.setHttpRequestRetryHandler(new RetryHandler(count));
	}

	/**
	 * 添加http请求头
	 * 
	 * @param header
	 * @param value
	 */
	public void addHeader(String header, String value) {
		clientHeaderMap.put(header, value);
	}

	// ------------------get 请求-----------------------
	@Override
	public void get(String url, HttpCallback<? extends Object> callBack) {
		get(url, null, callBack);
	}

	@Override
	public void get(String url, HttpParam params,
			HttpCallback<? extends Object> callBack) {
		sendRequest(httpClient, httpContext,
				new HttpGet(getUrlWithQueryString(url, params)), null, callBack);
	}

	@Override
	public void get(String url, Header[] headers, HttpParam params,
			HttpCallback<? extends Object> callBack) {
		HttpUriRequest request = new HttpGet(getUrlWithQueryString(url, params));
		if (headers != null)
			request.setHeaders(headers);
		sendRequest(httpClient, httpContext, request, null, callBack);
	}

	@Override
	public Object getSync(String url) {
		return getSync(url, null);
	}

	@Override
	public Object getSync(String url, HttpParam params) {
		HttpUriRequest request = new HttpGet(getUrlWithQueryString(url, params));
		return sendSyncRequest(httpClient, httpContext, request, null);
	}

	@Override
	public Object getSync(String url, Header[] headers, HttpParam params) {
		HttpUriRequest request = new HttpGet(getUrlWithQueryString(url, params));
		if (headers != null)
			request.setHeaders(headers);
		return sendSyncRequest(httpClient, httpContext, request, null);
	}

	// ------------------post 请求-----------------------
	@Override
	public void post(String url, HttpCallback<? extends Object> callBack) {
		post(url, null, callBack);
	}

	@Override
	public void post(String url, HttpParam params,
			HttpCallback<? extends Object> callBack) {
		post(url, paramsToEntity(params), null, callBack);
	}

	@Override
	public void post(String url, HttpEntity entity, String contentType,
			HttpCallback<? extends Object> callBack) {
		sendRequest(httpClient, httpContext,
				addEntityToRequestBase(new HttpPost(url), entity), contentType,
				callBack);
	}

	@Override
	public <T> void post(String url, Header[] headers, HttpParam params,
			String contentType, HttpCallback<T> callBack) {
		HttpEntityEnclosingRequestBase request = new HttpPost(url);
		if (params != null)
			request.setEntity(paramsToEntity(params));
		if (headers != null)
			request.setHeaders(headers);
		sendRequest(httpClient, httpContext, request, contentType, callBack);
	}

	@Override
	public void post(String url, Header[] headers, HttpEntity entity,
			String contentType, HttpCallback<? extends Object> callBack) {
		HttpEntityEnclosingRequestBase request = addEntityToRequestBase(
				new HttpPost(url), entity);
		if (headers != null)
			request.setHeaders(headers);
		sendRequest(httpClient, httpContext, request, contentType, callBack);
	}

	@Override
	public Object postSync(String url) {
		return postSync(url, null);
	}

	@Override
	public Object postSync(String url, HttpParam params) {
		return postSync(url, paramsToEntity(params), null);
	}

	@Override
	public Object postSync(String url, HttpEntity entity, String contentType) {
		return sendSyncRequest(httpClient, httpContext,
				addEntityToRequestBase(new HttpPost(url), entity), contentType);
	}

	@Override
	public Object postSync(String url, Header[] headers, HttpParam params,
			String contentType) {
		HttpEntityEnclosingRequestBase request = new HttpPost(url);
		if (params != null)
			request.setEntity(paramsToEntity(params));
		if (headers != null)
			request.setHeaders(headers);
		return sendSyncRequest(httpClient, httpContext, request, contentType);
	}

	@Override
	public Object postSync(String url, Header[] headers, HttpEntity entity,
			String contentType) {
		HttpEntityEnclosingRequestBase request = addEntityToRequestBase(
				new HttpPost(url), entity);
		if (headers != null)
			request.setHeaders(headers);
		return sendSyncRequest(httpClient, httpContext, request, contentType);
	}

	// ------------------put 请求-----------------------
	@Override
	public void put(String url, HttpCallback<? extends Object> callBack) {
		put(url, null, callBack);
	}

	@Override
	public void put(String url, HttpParam params,
			HttpCallback<? extends Object> callBack) {
		put(url, paramsToEntity(params), null, callBack);
	}

	@Override
	public void put(String url, HttpEntity entity, String contentType,
			HttpCallback<? extends Object> callBack) {
		sendRequest(httpClient, httpContext,
				addEntityToRequestBase(new HttpPut(url), entity), contentType,
				callBack);
	}

	@Override
	public void put(String url, Header[] headers, HttpEntity entity,
			String contentType, HttpCallback<? extends Object> callBack) {
		HttpEntityEnclosingRequestBase request = addEntityToRequestBase(
				new HttpPut(url), entity);
		if (headers != null)
			request.setHeaders(headers);
		sendRequest(httpClient, httpContext, request, contentType, callBack);
	}

	@Override
	public Object putSync(String url) {
		return putSync(url, null);
	}

	@Override
	public Object putSync(String url, HttpParam params) {
		return putSync(url, paramsToEntity(params), null);
	}

	@Override
	public Object putSync(String url, HttpEntity entity, String contentType) {
		return putSync(url, null, entity, contentType);
	}

	@Override
	public Object putSync(String url, Header[] headers, HttpEntity entity,
			String contentType) {
		HttpEntityEnclosingRequestBase request = addEntityToRequestBase(
				new HttpPut(url), entity);
		if (headers != null)
			request.setHeaders(headers);
		return sendSyncRequest(httpClient, httpContext, request, contentType);
	}

	// ------------------delete 请求-----------------------
	@Override
	public void delete(String url, HttpCallback<? extends Object> callBack) {
		final HttpDelete delete = new HttpDelete(url);
		sendRequest(httpClient, httpContext, delete, null, callBack);
	}

	@Override
	public void delete(String url, Header[] headers,
			HttpCallback<? extends Object> callBack) {
		final HttpDelete delete = new HttpDelete(url);
		if (headers != null)
			delete.setHeaders(headers);
		sendRequest(httpClient, httpContext, delete, null, callBack);
	}

	@Override
	public Object deleteSync(String url) {
		return deleteSync(url, null);
	}

	@Override
	public Object deleteSync(String url, Header[] headers) {
		final HttpDelete delete = new HttpDelete(url);
		if (headers != null)
			delete.setHeaders(headers);
		return sendSyncRequest(httpClient, httpContext, delete, null);
	}

	// ---------------------下载---------------------------------------
	@Override
	public HttpHandler<File> download(String url, String target,
			HttpCallback<File> callback) {
		return download(url, null, target, false, callback);
	}

	@Override
	public HttpHandler<File> download(String url, String target,
			boolean isResume, HttpCallback<File> callback) {
		return download(url, null, target, isResume, callback);
	}

	@Override
	public HttpHandler<File> download(String url, HttpParam params,
			String target, HttpCallback<File> callback) {
		return download(url, params, target, false, callback);
	}

	@Override
	public HttpHandler<File> download(String url, HttpParam params,
			String target, boolean isResume, HttpCallback<File> callback) {
		final HttpGet get = new HttpGet(getUrlWithQueryString(url, params));
		HttpHandler<File> handler = new HttpHandler<File>(httpClient,
				httpContext, callback, charset);
		handler.executeOnExecutor(executor, get, target, isResume);
		return handler;
	}

	protected <T> void sendRequest(DefaultHttpClient client,
			HttpContext httpContext, HttpUriRequest uriRequest,
			String contentType, HttpCallback<T> httpCallBack) {
		if (contentType != null) {
			uriRequest.addHeader("Content-Type", contentType);
		}

		new HttpHandler<T>(client, httpContext, httpCallBack, charset)
				.executeOnExecutor(executor, uriRequest);

	}

	protected Object sendSyncRequest(DefaultHttpClient client,
			HttpContext httpContext, HttpUriRequest uriRequest,
			String contentType) {
		if (contentType != null) {
			uriRequest.addHeader("Content-Type", contentType);
		}
		return new SyncRequestHandler(client, httpContext, charset)
				.sendRequest(uriRequest);
	}

	public static String getUrlWithQueryString(String url, HttpParam params) {
		if (params != null) {
			String paramString = params.getParamString();
			url += "?" + paramString;
		}
		return url;
	}

	private HttpEntity paramsToEntity(HttpParam params) {
		HttpEntity entity = null;

		if (params != null) {
			entity = params.getEntity();
		}

		return entity;
	}

	private HttpEntityEnclosingRequestBase addEntityToRequestBase(
			HttpEntityEnclosingRequestBase requestBase, HttpEntity entity) {
		if (entity != null) {
			requestBase.setEntity(entity);
		}

		return requestBase;
	}

	private static class InflatingEntity extends HttpEntityWrapper {
		public InflatingEntity(HttpEntity wrapped) {
			super(wrapped);
		}

		@Override
		public InputStream getContent() throws IOException {
			return new GZIPInputStream(wrappedEntity.getContent());
		}

		@Override
		public long getContentLength() {
			return -1;
		}
	}
}
