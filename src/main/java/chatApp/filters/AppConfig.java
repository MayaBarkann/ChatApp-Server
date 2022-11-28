package chatApp.filters;

import chatApp.entities.Response;
import chatApp.entities.User;
import chatApp.service.AuthService;
import chatApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    private final AuthService authService;
    private final UserService userService;
    @Autowired
    public AppConfig(AuthService authService, UserService userService) {
        System.out.println("AppConfig is created");
        this.authService = authService;
        this.userService = userService;
        Response<User> userById =  userService.findUserById(6);
    }/*
    @Bean
    public FilterRegistrationBean<LoginFilter> loginFilterBean() {
        FilterRegistrationBean <LoginFilter> registrationBean = new FilterRegistrationBean<>();
        LoginFilter loginFilter= new LoginFilter(authService);
        registrationBean.setFilter(loginFilter);
        //registrationBean.addUrlPatterns("/login");
        registrationBean.setOrder(1); //set precedence
        return registrationBean;
    }*/

    @Bean
    public FilterRegistrationBean<TokenFilter> filterRegistrationBean() {
        System.out.println("FilterRegistrationBean has been created");
        FilterRegistrationBean <TokenFilter> registrationBean = new FilterRegistrationBean<>();
        TokenFilter customURLFilter = new TokenFilter(authService);
        registrationBean.setFilter(customURLFilter);
        registrationBean.addUrlPatterns("/auth/*");
        registrationBean.setOrder(1); //set precedence
        return registrationBean;
    }
}
