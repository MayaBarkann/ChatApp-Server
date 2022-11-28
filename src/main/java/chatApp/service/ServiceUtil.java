package chatApp.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Base64;

public class ServiceUtil {

    /**
     * Checks if the given date is between two other given dates.
     *
     * @param theDate LocalDateTime, the date that is checked if its between the two other dates.
     * @param startDate LocalDateTime, the start date in the between range.
     * @param endDate LocalDateTime, the end date in the between range.
     * @return true - if theDate is between startDate and endDate, otherwise- false.
     */
    public static boolean dateIsBetween(LocalDateTime theDate,LocalDateTime startDate,LocalDateTime endDate)
    {
        return theDate.isAfter(startDate) && theDate.isBefore(endDate);
    }

    /**
     * Encrypts a given password String using the BCryptPasswordEncoder() Spring Security method.
     *
     * @param password String to be encrypted.
     * @return String containing the encrypted password.
     */
    public static String encryptPassword(String password){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * Checks if the password the user inputted matches the encrypted password saved in the database.
     *
     * @param encryptedPassword String, encrypted user password as saved in database.
     * @param rawPassword String, password inputted by user that needs to be checked.
     * @return boolean, true - if the password user inputted matches user's password in the database; false - if inputted password is incorrect.
     */
    public static boolean isPasswordCorrect(String encryptedPassword, String rawPassword){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(rawPassword, encryptedPassword);
    }

    /**
     * Checks if given authentication token is in proper format
     *
     * @param authToken String, user's authentication token.
     * @return boolean, true - if the given token is in correct format, otherwise - false.
     */
    public static boolean isTokenFormatValid(String authToken){
        if(authToken==null){
            return false;
        }
        try{
            Long.parseLong(new String(Base64.getDecoder().decode(authToken.split("-")[0])));
            Long.parseLong(new String(Base64.getDecoder().decode(authToken.split("-")[2])));
        }catch(NumberFormatException e){
            return false;
        }
        return true;
    }

    /**
     * Applies a calculation, that can be reversed, on a given number
     *
     * @param num long on which the calculation is applied.
     * @return long, result of the calculation.
     */
    public static long encodeWithReversibleFunction(long num){
        return (7*num)+3;
    }

    /**
     * Applies a calculation, that reverses the calculation of the decodeWithReversibleFunction() method, on a given number
     *
     * @param num long on which the calculation is applied.
     * @return long, result of the calculation.
     */
    public static long decodeWithReversibleFunction(long num){
        return (num-3)/7;
    }

}
