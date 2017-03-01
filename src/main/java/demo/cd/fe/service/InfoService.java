package demo.cd.fe.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class InfoService {
    private static final String PROTO = "http://";
    private static final String PORTSEP = ":";

    private ObjectMapper mapper = new ObjectMapper();

    private String host = "UNK1";
    private String port = "UNK2";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AsyncRestTemplate asyncRestTemplate;

    public InfoService() {
        Map<String, String> env = System.getenv();
        host = env.get("DEMO_BE_SERVICE_MASTER_SERVICE_HOST");
        port = env.get("DEMO_BE_SERVICE_MASTER_SERVICE_PORT");

        // use direct dns name of service, removes linkage to IP which can change
        host = "demo-be-svc-master";
        port = port == null ? "8081" : port;
    }

    private URI infoResourceUrl() {
        try {
            return new URI(PROTO + host + PORTSEP + port + "/info");
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    private String killUrl() {
        return PROTO + host + PORTSEP + port + "/lc/stop";
    }

    public CompletableFuture<JsonNode> getData() throws Exception {
        log.info("Fetching backend info from " + infoResourceUrl());

        CompletableFuture<JsonNode> cfResp = new CompletableFuture<>();

        asyncRestTemplate.getForEntity(infoResourceUrl(), String.class)
                .addCallback(new ListenableFutureCallback<ResponseEntity<String>>() {
                    @Override
                    public void onSuccess(ResponseEntity<String> response) {
                        if (response.getStatusCode() != HttpStatus.OK) {
                            cfResp.completeExceptionally(
                                    new IllegalStateException("Wrong status from remote info: got " + response.getStatusCode()));
                        }
                        try {
                            cfResp.complete(mapper.readTree(response.getBody()));
                        } catch (Exception e) {
                            log.error("Could not fetch Info backend data: " + e);

                            cfResp.complete(exceptionAsJson(e));
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        log.error("Could not fetch Info backend data: " + t);
                        cfResp.complete(exceptionAsJson(t));
                    }
                });
        return cfResp;
    }

    private JsonNode exceptionAsJson(Throwable t) {
        String exStr = t.toString().replace("\"", "\\\"");
        try {
            return mapper.readTree("{\"exception\": \"" + exStr + "\"}");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void killBackend() throws Exception {
        log.info("Killing backend " + killUrl());
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(killUrl(), String.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new IllegalStateException("Could not kill backend: got " + response.getStatusCode());
            }
        } catch (Throwable e) {
            log.error("Could not fetch Kill backend data: " + e, e);
        }
    }

    public void changeBackend(String newName) {
        log.info("Changing backend to {}", newName);
        if (newName == null || newName.length() == 0) {
            throw new IllegalArgumentException("New backend hostname cannot be empty");
        }
        String newPort = port;
        String newHost = newName;

        String[] newStuff = newName.split(PORTSEP);
        if (newStuff.length > 1) {
            newHost = newStuff[0];
            newPort = newStuff[1];
        }

        host = newHost;
        port = newPort;
        log.warn("Changed backend hostname to {}:{}", host, port);
    }

    public String getBackendHost() {
        return host;
    }

    public String getBackendPort() {
        return port;
    }
}
