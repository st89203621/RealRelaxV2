package com.ehear.aiot.cloud.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//登录session问题
public class LoginFilter implements Filter {

    public LoginFilter() {
        // TODO Auto-generated constructor stub
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        // TODO Auto-generated method stub
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession();
        // 如果session不为空，则可以浏览其他页面
        String url = request.getServletPath();
        System.out.println(url);
        // 这里判断目录，后缀名，当然也可以写在web.xml中，用url-pattern进行拦截映射
        if (request.getServletPath().contains("user.jsp") || request.getServletPath().contains("manual.jsp")
                || request.getServletPath().contains("custom.jsp")) {
            if (session.getAttribute("user") == null) {
                session.invalidate();
                response.setContentType("text/html;charset=gb2312");
                PrintWriter out = response.getWriter();
                out.println("<script language='javascript' type='text/javascript'>");
                out.println(
                        "alert(\"Since you haven't operated for a long time, it caused Session to fail. Please login again!\");window.parent.location.href='"
                                + request.getContextPath() + "/login.jsp'");
                out.println("</script>");
            } else {
                try {
                    chain.doFilter(request, response);
                } catch (Exception e) {
                    session.invalidate();
                    response.setContentType("text/html;charset=gb2312");
                    PrintWriter out = response.getWriter();
                    out.println("<script language='javascript' type='text/javascript'>");
                    out.println(
                            "alert(\"Since you haven't operated for a long time, it caused Session to fail. Please login again!\");window.parent.location.href='"
                                    + request.getContextPath() + "/login.jsp'");
                    out.println("</script>");
                }

            }

        } else {
            try {
                chain.doFilter(request, response);
            } catch (Exception e) {
                session.invalidate();
                response.setContentType("text/html;charset=gb2312");
                PrintWriter out = response.getWriter();
                out.println("<script language='javascript' type='text/javascript'>");
                out.println(
                        "alert(\"Since you haven't operated for a long time, it caused Session to fail. Please login again!\");window.parent.location.href='"
                                + request.getContextPath() + "/login.jsp'");
                out.println("</script>");
            }

        }

    }

    public void init(FilterConfig fConfig) throws ServletException {

    }

    public void destroy() {

    }
}