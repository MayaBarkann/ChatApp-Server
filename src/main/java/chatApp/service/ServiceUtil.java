package chatApp.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

public class ServiceUtil {
    public static boolean dateIsBetween(LocalDateTime theDate,LocalDateTime startDate,LocalDateTime endDate)
    {
        return theDate.isAfter(startDate) && theDate.isBefore(endDate);
    }

    public static String encryptPassword(String password){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }
}
