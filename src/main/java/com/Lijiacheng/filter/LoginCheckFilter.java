package com.Lijiacheng.filter;

import com.Lijiacheng.common.BaseContext;
import com.Lijiacheng.common.Result;
import com.alibaba.fastjson.JSON;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;

@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    /**
     * 包含通配符的路径匹配
     */
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     * Servlet的过滤器对登录功能进行数据增强：访问界面如果未登录则自动跳转到登录界面
     * */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 1、获取本次请求的URL
        String requestURI = request.getRequestURI();
//        log.info("拦截到的请求 {}", requestURI);
        // 2、判断本次请求是否需要处理
        // 定义不需要处理的请求路径
        String[] urls = {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/",
                "/common/**",
                "/user/sendMsg",  // 移动端发送短信
                "/user/login"  // 移动端登录
        };
        boolean check = checkFilter(urls, requestURI);

        // 3、如果不需要处理则直接放行
        if (check) {
//            log.info("该请求 {} 请放行", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // 4-1、后台：判断登录状态，如果已经登录则直接放行，如果未登录则通过输出流的方式返回未登录结果
        if (request.getSession().getAttribute("employee") != null) {
//            log.info("用户已登录");
            /**
             * 在同一个http请求中相同线程中设置当前登录的用户id ：
             * (例如发送update请求，前端的请求首先通过Filter判断是否登录，再到达Controller层的update的方法，再
             *  到达公共字段自动填充的MetaObject类。在这次请求中它们的线程都是相同的)
             * */
            BaseContext.setCurrentId((Long)request.getSession().getAttribute("employee"));

            filterChain.doFilter(request, response);
            return;
        }

        // 4-2、移动端：判断登录状态，如果已经登录则直接放行，如果未登录则通过输出流的方式返回未登录结果
        if (request.getSession().getAttribute("user") != null) {
//            log.info("用户已登录");
            /**
             * 在同一个http请求中相同线程中设置当前登录的用户id ：
             * (例如发送update请求，前端的请求首先通过Filter判断是否登录，再到达Controller层的update的方法，再
             *  到达公共字段自动填充的MetaObject类。在这次请求中它们的线程都是相同的)
             * */
            BaseContext.setCurrentId((Long)request.getSession().getAttribute("user"));

            filterChain.doFilter(request, response);
            return;
        }

        response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));
//        log.info("用户未登录");
    }

    /**
     * 路径匹配
     */
    public boolean checkFilter(String[] urls, String requestURI) {
        for (String url : urls) {
            if (PATH_MATCHER.match(url, requestURI)) {
                return true;
            }
        }
        return false;
    }
}
