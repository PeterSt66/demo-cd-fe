package demo.cd.fe.controller;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import demo.cd.fe.model.DeployInfo;
import demo.cd.fe.model.Quotorama;
import demo.cd.fe.service.InfoService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/info")
@Slf4j
public class InfoController {
    // private static final Logger LOG =
    // LoggerFactory.getLogger(InfoController.class);
    private static final int SLEEP_BEFORE_KILL = 1500;
    // private static final int HTTP_OK = 200;
    private static final int HTTP_REDIRECT = 302;
    private static final String HTTP_HDR_LOC = "location";

    @Autowired
    private InfoService infoService;

    @Autowired
    private DeployInfo deployInfo;

    @Autowired
    private Quotorama quoter;

    @RequestMapping(method = RequestMethod.GET)
    public Map<String, Object> collectInfo() throws Exception {
        log.info("Frontend collectInfo start");

        CompletableFuture<JsonNode> beFuture = infoService.getData();

        CompletableFuture<Map<String, Object>> feFuture = deployInfo.getData();

        log.info("Returning the future");
        return feFuture.thenCombineAsync(beFuture, (feData, beData) -> {
            Map<String, Object> allInfo = new java.util.HashMap<>();
            allInfo.put("be", beData);
            allInfo.put("fe", feData);
            feData.put("quote", quoter.nextQuote("1"));
            Map<String, Object> backend = new java.util.HashMap<>();
            backend.put("host", infoService.getBackendHost());
            backend.put("port", infoService.getBackendPort());
            feData.put("infoBackend", backend);
            log.info("Returning the present");
            return allInfo;
        }).get();
    }

    @RequestMapping(value = "/html", method = RequestMethod.GET)
    public String collectInfoAsHtml() throws Exception {
        log.info("Frontend collectInfoAsHtml start");

        Map<String, Object> info = collectInfo();

        StringBuilder res = new StringBuilder();
        res.append("<html><body>");
        res.append("  <pre>");

        ObjectMapper mapper = new ObjectMapper();
        res.append(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(info));

        res.append("  </pre>");
        res.append("</body></html>");
        return res.toString();
    }

    @RequestMapping(value = "/stop", method = RequestMethod.GET)
    public void stopAlll(HttpServletResponse resp) throws Exception {
        log.error("Frontend stopAll start self-exploding in 1.5 secs");

        new WaitBeforeKill().start();

        redirectToIndex(resp);
    }

    private void redirectToIndex(HttpServletResponse resp) {
        resp.setHeader(HTTP_HDR_LOC, "/");
        resp.setStatus(HTTP_REDIRECT);
    }

    class WaitBeforeKill extends Thread {
        public void run() {
            try {
                Thread.sleep(SLEEP_BEFORE_KILL);
                log.info("Start self-explosion now");
                System.exit(0);
            } catch (Throwable e) {
                log.warn("Kill FE: Could not sleep: " + e, e);
            }
        }
    }

}
