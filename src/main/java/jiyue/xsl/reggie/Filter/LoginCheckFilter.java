package jiyue.xsl.reggie.Filter;


import com.alibaba.fastjson.JSON;
import jiyue.xsl.reggie.Common.BaseContext;
import jiyue.xsl.reggie.Common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检测用户是否登录过滤器
 */


@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    // 路径匹配器 解决通配符方法
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    //实现过滤方法
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        // 强转输出
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获得请求uli
        String Requesturi = request.getRequestURI();

        log.info("filter已请求到:{}", request.getRequestURI());

        //放行路径
        String[] srls = new String[]{
                "/employee/login",  //登录请求
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };

        //判断本次请求是否需要处理
        boolean check = check(srls, Requesturi);

        //不需要处理 放行
        if(check){
            log.info("本次请求不需要处理:{}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        //如果已经登录
        if (request.getSession().getAttribute("employee") != null){

            //通过threadlocal 将id存入线程中
            Long emoId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setShreadid(emoId);
            log.info("将id存入threadlocal:{}",emoId);

            log.info("用户已登录， id为:{}", request.getSession().getAttribute("employee"));
            filterChain.doFilter(request, response);
            return;
        }

        //4-2、判断登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("user") != null){
            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("user"));

            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setShreadid(userId);

            filterChain.doFilter(request,response);
            return;
        }




        //如果未登录 , 通过输出流方式向客户端页面响应数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
//        log.info("filter已请求到:{}", request.getRequestURI());

//        // 放行
//        filterChain.doFilter(request, response);
    }




    /**
     * 遍历 查看本次请求是否需要放行
     * @param urls
     * @param requesturi
     * @return
     */
    public boolean check(String[]urls, String requesturi){
        for(String url : urls){
            boolean match = PATH_MATCHER.match(url, requesturi);
            if(match){
                return true;
            }
        }
        return false;
    }
}
