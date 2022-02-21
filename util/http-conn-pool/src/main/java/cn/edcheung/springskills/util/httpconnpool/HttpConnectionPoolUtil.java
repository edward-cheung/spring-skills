package cn.edcheung.springskills.util.httpconnpool;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.pool.PoolStats;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Description HttpConnectionPoolUtil
 *
 * @author zhangyi
 * @date 2020/12/8
 * @since JDK 1.8
 */
public class HttpConnectionPoolUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpConnectionPoolUtil.class);

    private static final int CONNECT_REQUEST_TIMEOUT = 3000; // 设定从连接池获取可用连接的时间
    private static final int CONNECT_TIMEOUT = 3000; // 建立连接超时时间
    private static final int SOCKET_TIMEOUT = 5000; // 设置等待数据超时时间5秒钟 根据业务调整
    private static final int MAX_CONN = 100; // 连接池最大连接数
    private static final int MAX_PRE_ROUTE = 100; // 每个主机的并发
    private static final int MAX_ROUTE = 100; // 目标主机的最大连接数
    private final static Object SYNC_LOCK = new Object(); // 相当于线程锁,用于线程安全
    private static CloseableHttpClient httpClient; // 发送请求的客户端单例
    private static PoolingHttpClientConnectionManager manager; //连接池管理类
    private static ScheduledExecutorService monitorExecutor;

    public static CloseableHttpClient getHttpClient(String url) {
        String hostName = url.split("/")[2];
        int port = 80;
        if (hostName.contains(":")) {
            String[] args = hostName.split(":");
            hostName = args[0];
            port = Integer.parseInt(args[1]);
        }

        if (httpClient == null) {
            //多线程下多个线程同时调用getHttpClient容易导致重复创建httpClient对象的问题,所以加上了同步锁
            synchronized (SYNC_LOCK) {
                if (httpClient == null) {
                    httpClient = createHttpClient(hostName, port);
                    //开启监控线程,对异常和空闲线程进行关闭
                    monitorExecutor = Executors.newScheduledThreadPool(1);
                    monitorExecutor.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            //关闭异常连接
                            manager.closeExpiredConnections();
                            //关闭5s空闲的连接
                            manager.closeIdleConnections(5000, TimeUnit.MILLISECONDS);
                            logger.info("close expired and idle for over 5s connection");
                            //打印连接池使用情况
                            if (logger.isDebugEnabled()) {
                                final PoolStats poolStats = manager.getTotalStats();
                                logger.debug("***********》关闭异常+空闲连接！ 空闲连接:"
                                        + poolStats.getAvailable() + " 持久连接:" + poolStats.getLeased() + " 最大连接数:"
                                        + poolStats.getMax() + " 阻塞连接数:" + poolStats.getPending());
                            }
                        }
                    }, 3000, 3000, TimeUnit.MILLISECONDS);
                }
            }
        }
        return httpClient;
    }

    /**
     * 根据host和port构建httpclient实例
     *
     * @param host 要访问的域名
     * @param port 要访问的端口
     * @return
     */
    public static CloseableHttpClient createHttpClient(String host, int port) {
        ConnectionSocketFactory plainSocketFactory = PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactory.getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", plainSocketFactory)
                .register("https", sslSocketFactory)
                .build();

        manager = new PoolingHttpClientConnectionManager(registry);
        //设置连接参数
        manager.setMaxTotal(MAX_CONN); // 最大连接数
        manager.setDefaultMaxPerRoute(MAX_PRE_ROUTE); // 路由最大连接数

        HttpHost httpHost = new HttpHost(host, port);
        manager.setMaxPerRoute(new HttpRoute(httpHost), MAX_ROUTE); // 目标主机的最大连接数

        //请求失败时,进行请求重试
        HttpRequestRetryHandler handler = new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException e, int i, HttpContext httpContext) {
                if (i > 3) {
                    //重试超过3次,放弃请求
                    logger.error("retry has more than 3 time, give up request");
                    return false;
                }
                if (e instanceof NoHttpResponseException) {
                    //服务器没有响应,可能是服务器断开了连接,应该重试
                    logger.error("receive no response from server, retry");
                    return true;
                }
                if (e instanceof SSLHandshakeException) {
                    //SSL握手异常
                    logger.error("SSL hand shake exception");
                    return false;
                }
                if (e instanceof InterruptedIOException) {
                    //超时
                    logger.error("InterruptedIOException");
                    return false;
                }
                if (e instanceof UnknownHostException) {
                    //目标服务器不可达
                    logger.error("server host unknown");
                    return false;
                }
                if (e instanceof SSLException) {
                    //SSL握手异常
                    logger.error("SSLException");
                    return false;
                }

                HttpClientContext context = HttpClientContext.adapt(httpContext);
                HttpRequest request = context.getRequest();
                //如果请求不是关闭连接的请求
                return !(request instanceof HttpEntityEnclosingRequest);
            }
        };

        CloseableHttpClient client = HttpClients.custom()
                .setConnectionManager(manager)
                .setRetryHandler(handler)
                .build();
        return client;
    }

    /**
     * 对http请求进行基本设置
     *
     * @param httpRequestBase http请求
     */
    private static void setRequestConfig(HttpRequestBase httpRequestBase) {
        // setConnectionRequestTimeout：从连接池中拿连接的等待超时时间
        // setConnectTimeout：设置建立连接的超时时间
        // setSocketTimeout：发出请求后等待对端应答的超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(CONNECT_REQUEST_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT)
                .build();
        httpRequestBase.setConfig(requestConfig);
    }

    /**
     * 设置post请求的参数
     *
     * @param httpPost
     * @param params
     */
    private static void setPostParams(HttpPost httpPost, Map<String, String> params) {
        List<NameValuePair> nvpList = new ArrayList<>();
        Set<String> keys = params.keySet();
        for (String key : keys) {
            nvpList.add(new BasicNameValuePair(key, params.get(key)));
        }
        httpPost.setEntity(new UrlEncodedFormEntity(nvpList, StandardCharsets.UTF_8));
    }

    public static JsonObject post(String url, Map<String, String> params) {
        HttpPost httpPost = new HttpPost(url);
        setRequestConfig(httpPost);
        setPostParams(httpPost, params);
        CloseableHttpResponse response = null;
        InputStream in = null;
        JsonObject object = null;
        try {
            response = getHttpClient(url).execute(httpPost, HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                in = entity.getContent();
                String result = IOUtils.toString(in, StandardCharsets.UTF_8);
                Gson gson = new Gson();
                object = gson.fromJson(result, JsonObject.class);
            }
        } catch (IOException e) {
            logger.error("连接池发起post请求异常", e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error("关闭输入输出流异常", e);
            }
        }
        return object;
    }

    public static JsonObject get(String url) {
        HttpGet httpGet = new HttpGet(url);
        setRequestConfig(httpGet);
        CloseableHttpResponse response = null;
        InputStream in = null;
        JsonObject object = null;
        try {
            response = getHttpClient(url).execute(httpGet, HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                in = entity.getContent();
                String result = IOUtils.toString(in, StandardCharsets.UTF_8);
                Gson gson = new Gson();
                object = gson.fromJson(result, JsonObject.class);
            }
        } catch (IOException e) {
            logger.error("连接池发起get请求异常", e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error("关闭输入输出流异常", e);
            }
        }
        return object;
    }

    /**
     * 关闭连接池
     */
    public static void closeConnectionPool() {
        try {
            httpClient.close();
            manager.close();
            monitorExecutor.shutdown();
        } catch (IOException e) {
            logger.error("关闭连接池异常", e);
        }
    }
}
