package chatApp.filters;


import chatApp.entities.Response;
import chatApp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class LoginFilter implements Filter {
    private final AuthService authService;
    @Autowired
    public LoginFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        String authToken = req.getHeader("Authorization");
        if (authToken != null) {
            Response<?> tokenCorrect = authService.isTokenCorrect(authToken);
            if(!tokenCorrect.isSucceed())
            {

            }
        } else {
            res.sendError(401, "Unauthorized");
        }

    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
    @Bean
    public FilterRegistrationBean<LoginFilter> filter()
    {
        FilterRegistrationBean<LoginFilter> bean = new FilterRegistrationBean<>();

        bean.setFilter(new LoginFilter());
        bean.addUrlPatterns("/execute/*");  // or, use `setUrlPatterns()`

        return bean;
    }
}

