package cn.edcheung.springskills.gateway.resttemplate.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

/**
 * Description RedirectService
 *
 * @author Edward Cheung
 * @date 2021/11/8
 * @since JDK 1.8
 */
@Service
public class RedirectService {

    public ResponseEntity<String> redirect(HttpServletRequest request, HttpServletResponse response, String routeUrl, String prefix) {
        try {
            String directUrl = this.createDirectUrl(request, routeUrl, prefix);
            RequestEntity<?> requestEntity = this.createRequestEntity(request, directUrl);
            return this.direct(requestEntity);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("redirect error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String createDirectUrl(HttpServletRequest request, String routeUrl, String prefix) {
        String queryString = request.getQueryString();
        return routeUrl + request.getRequestURI().replace(prefix, "")
                + (queryString == null ? "" : "?" + queryString);
    }

    private RequestEntity<?> createRequestEntity(HttpServletRequest request, String url) throws URISyntaxException, IOException {
        String requestMethod = request.getMethod();
        HttpMethod httpMethod = HttpMethod.resolve(requestMethod);
        MultiValueMap<String, String> requestHeader = this.parseRequestHeader(request);
        byte[] requestBody = this.parseRequestBody(request);
        return new RequestEntity<>(requestBody, requestHeader, httpMethod, new URI(url));
    }

    private MultiValueMap<String, String> parseRequestHeader(HttpServletRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        List<String> requestHeaderNames = Collections.list(request.getHeaderNames());
        for (String requestHeaderName : requestHeaderNames) {
            List<String> requestHeaderValues = Collections.list(request.getHeaders(requestHeaderName));
            httpHeaders.addAll(requestHeaderName, requestHeaderValues);
        }
        return httpHeaders;
    }

    private byte[] parseRequestBody(HttpServletRequest request) throws IOException {
        InputStream inputStream = request.getInputStream();
        return StreamUtils.copyToByteArray(inputStream);
    }

    private ResponseEntity<String> direct(RequestEntity<?> requestEntity) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(requestEntity, String.class);
    }
}
