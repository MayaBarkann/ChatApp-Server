package chatApp.service;

import java.time.LocalDateTime;

public class ServiceUtil {
    public static boolean dateIsBetween(LocalDateTime theDate,LocalDateTime startDate,LocalDateTime endDate)
    {
        return theDate.isAfter(startDate) && theDate.isBefore(endDate);
    }
}
