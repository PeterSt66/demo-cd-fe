package demo.cd.fe.service;

import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.AsyncClientHttpRequestExecution;
import org.springframework.http.client.AsyncClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.concurrent.ListenableFuture;

import lombok.SneakyThrows;

public class CorrelationIdInterceptor implements ClientHttpRequestInterceptor, AsyncClientHttpRequestInterceptor {
    private static final String CID = "cid";

    @Override
    @SneakyThrows
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
        return execution.execute(enrichWithCid(request), body);
    }

    @Override
    @SneakyThrows
    public ListenableFuture<ClientHttpResponse> intercept(HttpRequest request, byte[] body,AsyncClientHttpRequestExecution execution)  {
        return execution.executeAsync(enrichWithCid(request), body);
    }

    private HttpRequest enrichWithCid(HttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        headers.add(CID, MDC.get(CID));
        return request;
    }
}
