package chatApp.filters;


import chatApp.service.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    public static final Logger logger = LogManager.getLogger(AppConfig.class);
    private final AuthService authService;
    @Autowired
    public AppConfig(AuthService authService) {
        System.out.println("AppConfig is created");
        this.authService = authService;
    }
    /*
    @Bean
    public FilterRegistrationBean<LoginFilter> loginFilterBean() {
        FilterRegistrationBean <LoginFilter> registrationBean = new FilterRegistrationBean<>();
        LoginFilter loginFilter= new LoginFilter(authService);
        registrationBean.setFilter(loginFilter);
        registrationBean.addUrlPatterns("/login");
        registrationBean.setOrder(1); //set precedence
        return registrationBean;
    }*/

    /*
    * this method is used to register the token filter
    * the token filter initialized with the auth service
    * the token filter is running first in the filter chain
    * @return FilterRegistrationBean<TokenFilter>
    *
    */
    @Bean
    public FilterRegistrationBean<TokenFilter> filterRegistrationBean() {
        logger.info("FilterRegistrationBean has been created");
        FilterRegistrationBean <TokenFilter> registrationBean = new FilterRegistrationBean<>();
        TokenFilter customURLFilter = new TokenFilter(authService);
        registrationBean.setFilter(customURLFilter);
        registrationBean.addUrlPatterns("/auth/*");
        registrationBean.setOrder(1); //set precedence
        return registrationBean;
    }
}
