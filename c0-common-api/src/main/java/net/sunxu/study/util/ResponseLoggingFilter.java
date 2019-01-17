package net.sunxu.study.util;

import org.bouncycastle.util.io.TeeOutputStream;
import org.springframework.core.annotation.Order;
import org.springframework.mock.web.DelegatingServletOutputStream;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Component
@WebFilter(filterName = "ResponseLoggingFilter", urlPatterns = "/**")
public class ResponseLoggingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
        HttpServletResponse wrapper = wrapResponse(response, responseStream);
        try {
            filterChain.doFilter(request, wrapper);
        } finally {
            String message = getMessage(wrapper, responseStream);
            logger.debug(message);
        }
    }

    private String getMessage(HttpServletResponse response, ByteArrayOutputStream responseStream) {
        StringBuilder sb = new StringBuilder(128);
        sb.append("Response ").append(response.getStatus()).append(" [\n");
        for (var headerName : response.getHeaderNames()) {
            sb.append(headerName).append(" : ").append(response.getHeader(headerName)).append('\n');
        }
        sb.append('\n');
        String payload = responseStream.toString();
        if (!StringUtils.isEmpty(payload)) {
            sb.append(payload).append('\n');
        }
        sb.append(']');
        return sb.toString();
    }

    private HttpServletResponse wrapResponse(HttpServletResponse response, OutputStream outputStream) {
        return new HttpServletResponseWrapper(response) {
            @Override
            public ServletOutputStream getOutputStream() throws IOException {
                return new DelegatingServletOutputStream(
                        new TeeOutputStream(super.getOutputStream(), outputStream)
                );
            }
        };
    }

}
