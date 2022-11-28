package chatApp.filters;

import chatApp.service.AuthService;

import javax.servlet.*;
import java.io.IOException;

public class LoginFilter  implements Filter {
    private final AuthService authService;

    public LoginFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
