package com.xxxx.note.filter;

import com.xxxx.note.po.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 非法访问拦截
 *  拦截的资源
 *      所有资源
 *
 *  需要被放行的资源
 *      1.指定页面，放行 (用户无需登录的即可访问的页面：例如：登陆页面login.jsp，注册页面等)
 *      2.静态资源，放行 (存放在static目录下的资源：例如,JS,CSS,image)
 *      3.指定行为，放行 (用户无需登录即可执行的操作；例如：登录操作)
 *      4.登陆状态，放行 (判断session作用域中是否存在user对象，存在放行，不存在，则拦截跳转到登陆页面)
 *
 *  免登录(自动登录)
 *      通过cookie和session对象实现
 *
 *      什么时候使用免登录：
 *          当用户处于未登录状态，且去请求需要登录才能访问的资源时，调用自动登录功能
 *
 *      目的：
 *          让用户处于登陆状态(自动调用登录方法)
 *
 *      实现：
 *          从cookie对象中获取用户的姓名和密码，自动执行登录操作
 *              1，获取cookie数组
 *              2.判断cookie数组
 *              3.遍历数组，获取指定的cookie对象  (name为user的cookie对象)
 *              4.得到对应的cookie对象的value （姓名与密码：userName-userPwd）
 *              5.通过split()方法将value字符串分割成数组
 *              6.从数组中分别得到对应的姓名和密码
 *              7.请求转发到登陆操作  user?actionName=login&userName=xxx&userPwd=xxx
 *              8.return
 *      如果以上判断都不满足，则拦截跳转到登陆页面
 *
 *
 */

@WebFilter("/*")
public class LoginAccessFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //基于HTTP
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //得到访问的路径
        String path = request.getRequestURI();//格式：项目路径/资源路径

        if (path.contains("/login.jsp")){
            filterChain.doFilter(request,response);
            return;
        }

        if (path.contains("/static")){
            filterChain.doFilter(request,response);
            return;
        }

        if (path.contains("/statics")){
            filterChain.doFilter(request,response);
            return;
        }

        if (path.contains("/user")){
            //得到用户行为
            String actionName = request.getParameter("actionName");
            //判断 是否是登陆操作
            if("login".equals(actionName)){
                filterChain.doFilter(request,response);
                return;
            }
        }

        //获取session作用域中的user对象
        User user = (User) request.getSession().getAttribute("user");
        //判断user对象是否为空
        if (user != null){
            filterChain.doFilter(request,response);
            return;
        }

        /**
         * 免登录
         */
        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0){
            for (Cookie cookie:cookies) {
                if ("user".equals(cookie.getName())){
                    String value = cookie.getValue();
                    String[] val = value.split("-");
                    String userName = val[0];
                    String userPwd = val[1];
                    String url = "user?actionName=login&rem=1&userName="+userName+"&userPwd="+userPwd;
                    request.getRequestDispatcher(url).forward(request,response);
                    return;
                }
            }
        }


        //拦截请求，重定向跳转到登陆页面
        response.sendRedirect("login.jsp");

    }

    @Override
    public void destroy() {

    }
}
