package chatApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class SpringApp {
    public static void main(String[] args){
       SpringApplication application=new  SpringApplication(SpringApp.class);
        System.out.println(args.length);
        for (String arg : args) {
            System.out.println(arg);
        }
       application.run(args);
    }

}