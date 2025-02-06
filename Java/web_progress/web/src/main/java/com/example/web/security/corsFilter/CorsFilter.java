//package com.example.web.security.corsFilter;
//
//import com.example.web.constant.CorsFilterConstants;
//import jakarta.servlet.Filter;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.FilterConfig;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.access.AccessDeniedException;
//
//@Slf4j
//@Configuration
//public class CorsFilter implements Filter {
//
//  @Override
//  public void init(FilterConfig filterConfig) throws ServletException {
//
//  }
//
//  @Override
//  public void doFilter(
//      ServletRequest servletRequest,
//      ServletResponse servletResponse,
//      FilterChain filterChain
//  ) throws IOException, ServletException {
//    HttpServletResponse response = (HttpServletResponse) servletResponse;
//    HttpServletRequest request = (HttpServletRequest) servletRequest;
//
//    if ("OPTIONS".equals(request.getMethod())) {
//      response.setStatus(HttpServletResponse.SC_OK);
//      setCorsHeaders(response);
//      return;
//    }
//
//    setCorsHeaders(response);
//
//    filterChain.doFilter(servletRequest, servletResponse);
//
//
//  }
//
//  private void setCorsHeaders(HttpServletResponse response) {
//    response.setHeader(CorsFilterConstants.HEADER_ACCESS_CONTROL_ALLOW_ORIGIN
//        , CorsFilterConstants.VALUE_ORIGIN);
//    response.setHeader(CorsFilterConstants.HEADER_ACCESS_CONTROL_ALLOW_METHODS,
//        CorsFilterConstants.VALUE_WILD_CARD);
//    response.setHeader(CorsFilterConstants.HEADER_ACCESS_CONTROL_ALLOW_HEADERS,
//        CorsFilterConstants.VALUE_WILD_CARD);
//    response.setHeader(CorsFilterConstants.HEADER_ACCESS_CONTROL_MAX_AGE,
//        CorsFilterConstants.VALUE_WILD_CARD);
//    response.setHeader(CorsFilterConstants.HEADER_ACCESS_CONTROL_EXPOSE_HEADERS
//        , CorsFilterConstants.VALUE_WILD_CARD);
//  }
//
//  @Override
//  public void destroy() {
//
//  }
//}
