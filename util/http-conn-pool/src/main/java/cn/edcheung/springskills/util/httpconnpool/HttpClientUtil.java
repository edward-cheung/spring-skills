package cn.edcheung.springskills.util.httpconnpool;


import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description HttpClientUtil
 *
 * @author zhangyi
 * @date 2020/12/8
 * @since JDK 1.8
 */
public class HttpClientUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private static CloseableHttpClient httpClient = null;

    static {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        // 总连接池数量
        connectionManager.setMaxTotal(150);
        // 可为每个域名设置单独的连接池数量
        connectionManager.setMaxPerRoute(new HttpRoute(new HttpHost("xx.xx.xx.xx")), 80);
        // setConnectTimeout：设置建立连接的超时时间
        // setConnectionRequestTimeout：从连接池中拿连接的等待超时时间
        // setSocketTimeout：发出请求后等待对端应答的超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(1000)
                .setConnectionRequestTimeout(2000)
                .setSocketTimeout(3000)
                .build();
        // 重试处理器，StandardHttpRequestRetryHandler
        HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                if (logger.isDebugEnabled()) {
                    logger.debug("开始第" + executionCount + "次重试！");
                }
                if (executionCount > 3) {
                    logger.info("重试次数大于3次，不再重试");
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {
                    logger.info("连接超时，准备进行重新请求...");
                    return true;
                }
                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
                return idempotent;
            }
        };

        httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler(retryHandler)
                .build();
    }

    public static String doHttpGet(String uri, Map<String, String> getParams) {
        HttpEntity entity = null;
        CloseableHttpResponse response = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(uri);
            if (null != getParams && !getParams.isEmpty()) {
                List<NameValuePair> list = new ArrayList<>();
                for (Map.Entry<String, String> param : getParams.entrySet()) {
                    list.add(new BasicNameValuePair(param.getKey(), param.getValue()));
                }
                uriBuilder.setParameters(list);
            }
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK == statusCode) {
                entity = response.getEntity();
                if (null != entity) {
                    return EntityUtils.toString(entity, Consts.UTF_8.name());
                }
            }
        } catch (Exception e) {
            logger.error("CloseableHttpClient-get-请求异常", e);
        } finally {
            try {
                // 关闭HttpEntity的流
                // 如果手动关闭了InputStream in = entity.getContent();这个流，也可以不调用这个方法
                EntityUtils.consume(entity);
                if (null != response) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error("关闭输入输出流异常", e);
            }
        }
        return null;
    }

    public static String doHttpPost(String uri, Map<String, String> getParams) {
        HttpEntity entity = null;
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(uri);
            if (null != getParams && !getParams.isEmpty()) {
                List<NameValuePair> list = new ArrayList<>();
                for (Map.Entry<String, String> param : getParams.entrySet()) {
                    list.add(new BasicNameValuePair(param.getKey(), param.getValue()));
                }
                HttpEntity httpEntity = new UrlEncodedFormEntity(list, Consts.UTF_8.name());
                httpPost.setEntity(httpEntity);
            }
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK == statusCode) {
                entity = response.getEntity();
                if (null != entity) {
                    return EntityUtils.toString(entity, Consts.UTF_8.name());
                }
            }
        } catch (Exception e) {
            logger.error("CloseableHttpClient-post-请求异常", e);
        } finally {
            try {
                // 关闭HttpEntity的流
                // 如果手动关闭了InputStream in = entity.getContent();这个流，也可以不调用这个方法
                EntityUtils.consume(entity);
                if (null != response) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error("关闭输入输出流异常", e);
            }
        }
        return null;
    }
}
