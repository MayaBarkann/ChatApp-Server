package chatApp.filters;


import chatApp.entities.Response;
import chatApp.filters.entities.MutableHttpServletRequest;
import chatApp.service.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class TokenFilter implements Filter {
    public static final Logger logger = LogManager.getLogger(TokenFilter.class);
    private final AuthService authService;
    public TokenFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        logger.info("Auth filter is working on the following request: " + servletRequest);
        //ServletRequest servletRequestWithParameter = (HttpServletRequest) servletRequest.;
        MutableHttpServletRequest req =new MutableHttpServletRequest ((HttpServletRequest) servletRequest);
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        String authToken = req.getHeader("Authorization");
        if (authToken != null) {
            Response<Integer> tokenCorrect = authService.isTokenCorrect(authToken);
            if(tokenCorrect.isSucceed())
            {
               //req.addHeader("userId", tokenCorrect.getData().toString());
                req.setAttribute("userId", tokenCorrect.getData());
               filterChain.doFilter(req,res);
            }
            else returnBadResponse(res);
        }
        else returnBadResponse(res);
    }

    private void returnBadResponse(HttpServletResponse res) throws IOException {
        res.sendError(401, "Unauthorized");
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}

