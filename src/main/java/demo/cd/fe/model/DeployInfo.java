package demo.cd.fe.model;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DeployInfo {
    private static SimpleDateFormat BASIC_DATE_FMT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");

    public CompletableFuture<Map<String, Object>> getData() {
        return CompletableFuture.completedFuture(getDataMap());
    }

    public Map<String, Object> getDataMap() {

        Map<String, String> env = System.getenv();

        Map<String, Object> res = new HashMap<>();

        Map<String, Object> sys = new HashMap<>();

        sys.put("Hostname", env.get("HOSTNAME"));
        sys.put("java_runtime_version", System.getProperty("java.runtime.version"));
        sys.put("workdir", env.get("PWD"));
        
        String now = BASIC_DATE_FMT.format(new Date()) + " CET";

        sys.put("request_ts", now);

        // sys.put("PID", System.getProperty("PID"));
        sys.put("java_vm_vendor", System.getProperty("java.vm.vendor"));
        sys.put("java_home", System.getProperty("java.home"));
        sys.put("os_name", System.getProperty("os.name"));
        sys.put("os_version", System.getProperty("os.version"));
        sys.put("os_arch", System.getProperty("os.arch"));
        sys.put("user_name", System.getProperty("user.name"));
        sys.put("java_command", System.getProperty("sun.java.command"));
        sys.put("java_arch", System.getProperty("sun.arch.data.model"));

        List<Map<String, Object>> netinfo = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets)) {
                if ("lo".equals(netint.getDisplayName())) {
                    continue;
                }
                Map<String, Object> ni = new HashMap<>();
                ni.put("displayname", netint.getDisplayName());
                ni.put("name", netint.getName());
                Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                int ifaceNr = 0;
                for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                    ni.put("address" + (ifaceNr++), inetAddress);
                }
                netinfo.add(ni);
            }
        } catch (Exception e) {
            log.error("Cannot enumerate network interfaces: " + e);
        }

        final Properties labels = new Properties();
        try {
            try (InputStream stream = DeployInfo.class.getResourceAsStream("/labels.properties")) {
                labels.load(stream);
            }
        } catch (Exception e) {
            log.error("Cannot load labels: " + e);
        }

        res.put("labels", labels);
        res.put("net", netinfo);
        res.put("system", sys);

        return res;
    }

}
