package demo.cd.fe.controller;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import demo.cd.fe.service.InfoService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/lc")
@Slf4j
public class LifecycleController {
    private static final int SLEEP_BEFORE_KILL = 1500;
    private static final int HTTP_OK = 200;
    private static final int HTTP_REDIRECT = 302;
    private static final int HTTP_NOTFOUND = 404;
    private static final String HTTP_HDR_LOC = "location";

    @Autowired
    private InfoService infoService;

    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public String ping(HttpServletResponse resp) throws Exception {
        log.trace("ping!");
        resp.setStatus(HTTP_OK);
        return "OK";
    }

    @RequestMapping(value = "/stop", method = RequestMethod.GET)
    public void stopThis(HttpServletResponse resp) throws Exception {

        log.error("Received stop signal - exploding in 1.5 secs");
        new WaitBeforeKill().start();

        redirectToIndex(resp);
    }

    @RequestMapping(value = "/stopBE", method = RequestMethod.GET)
    public void stopBackend(HttpServletResponse resp) throws Exception {
        log.warn("Received stop signal for backend - fire one!");

        infoService.killBackend();

        try {
            Thread.sleep(SLEEP_BEFORE_KILL);
        } catch (Throwable e) {
            log.warn("stopBackend: Could not sleep: " + e, e);
        }
        redirectToIndex(resp);
    }

    @RequestMapping(value = "/backend", method = RequestMethod.POST)
    public String changeBackend(@RequestBody Map<String, String> params, HttpServletResponse resp) throws Exception {
        log.warn("Changing backend with " + params);
        String newName = params == null ? null : params.get("newBackend");
        infoService.changeBackend(newName);
        resp.setStatus(HTTP_OK);
        return "{\"Result\": \"OK changed to " + infoService.getBackendHost() + ":" + infoService.getBackendPort() + "\"}";
    }

    private void redirectToIndex(HttpServletResponse resp) {
        resp.setHeader(HTTP_HDR_LOC, "/");
        resp.setStatus(HTTP_REDIRECT);
    }

    @RequestMapping(value = "/stop/{id}", method = RequestMethod.GET)
    public void stopSpecific(HttpServletResponse resp, @PathVariable("id") String id) throws Exception {
        log.info("accepted a signal to stop for " + id + ", let's find us a target....");
        switch (id) {
        case "FE": {
            stopThis(resp);
            break;
        }
        case "BE": {
            stopBackend(resp);
            break;
        }
        case "ALL": {
            stopBackend(resp);
            stopThis(resp);
            break;
        }
        default: {
            resp.sendError(HTTP_NOTFOUND, "Could not find a target for " + id);
        }
        }
    }

    class WaitBeforeKill extends Thread {
        public void run() {
            try {
                Thread.sleep(SLEEP_BEFORE_KILL);
                System.exit(0);
            } catch (Throwable e) {
                log.warn("Kill FE: Could not sleep: " + e, e);
            }
        }
    }

}
