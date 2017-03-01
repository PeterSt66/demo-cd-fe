package demo.cd.fe.web;

import java.io.IOException;
import java.util.Random;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CidFilter implements Filter {
    private final static String CID = "cid";
    private final static String BCID = "bcid";

    private static final int UUID_RAND_TYPE = 4;
    private final static int IDX_UUID_TYPE = 4;
    private static final int IDX_DASH_1 = 8;
    private static final int IDX_DASH_2 = 13;
    private static final int IDX_DASH_3 = 18;
    private static final int IDX_DASH_4 = 23;
    private static final int IDX_SPECIAL = 19;
    private static final int HEX_A = 10;
    private static final int HEX_MAX = 16;
    private static final int LEN_UUID = 36;

    private static final int BIT1 = 0x01;
    private static final int BIT2 = 0x02;
    private static final int BIT4 = 0x08;

    private Random random = new Random();

    public CidFilter() {
        // nothing
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        String requestCid = request.getHeader(CID);
        String cid = calcCorrelationId();
        if (requestCid != null) {
            MDC.put(BCID, requestCid);
        }
        MDC.put(CID, cid);
        log.info("Incoming {} request for endpoint {} with data of type {} and length {}", request.getMethod(),
                request.getRequestURL(), request.getContentType(), request.getContentLength());

        try {
            chain.doFilter(req, res);
        } finally {
            log.info("Done:  {} request for endpoint {} returns data of type {}", request.getMethod(), request.getRequestURL(),
                    res.getContentType());
            MDC.remove(CID);
            MDC.remove(BCID);
        }

    }

    private String calcCorrelationId() {
        StringBuffer uuid = new StringBuffer();
        for (int position = 0; position < LEN_UUID; position++) {
            switch (position) {
            case IDX_UUID_TYPE: {
                uuid.append(UUID_RAND_TYPE);
                break;
            }
            case IDX_DASH_1:
            case IDX_DASH_2:
            case IDX_DASH_3:
            case IDX_DASH_4: {
                uuid.append('-');
                break;
            }
            default: {
                int c = random.nextInt(HEX_MAX);
                if (position == IDX_SPECIAL) {
                    c = (c & (BIT1 + BIT2)) | BIT4;
                }
                char d = (char) (c < HEX_A ? '0' + c : 'a' + c - HEX_A);
                uuid.append(d);
            }

            }
        }
        return uuid.toString();
    }

    @Override
    public void destroy() {
        // nothing
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // nothing
    }

}