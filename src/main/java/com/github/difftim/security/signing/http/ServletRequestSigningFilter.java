package com.github.difftim.security.signing.http;

import com.github.difftim.security.signing.SignatureVerifier;
import org.apache.commons.io.IOUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class ServletRequestSigningFilter implements Filter {

    private HttpSignatureVerifier httpSignatureVerifier;

    public ServletRequestSigningFilter(SignatureVerifier.KeyStorage keyStorage) {
        this.httpSignatureVerifier = new HttpSignatureVerifier(keyStorage);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // parameters
        Map<String, List<String>> newParameters = new HashMap<>();
        Map<String, String[]> parameters = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
            newParameters.put(entry.getKey(), Arrays.asList(entry.getValue()));
        }

        // headers
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                headers.put(headerName, request.getHeader(headerName));
            }
        }

        byte[] payload = IOUtils.toByteArray(request.getInputStream());

        // verify signature
        try {
            httpSignatureVerifier.verify(request.getMethod(), request.getRequestURI(), newParameters, headers, payload, request.getRemoteAddr());
        } catch (HttpSignatureVerifier.InvalidSignatureException e) {
            response.sendError(401);
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
