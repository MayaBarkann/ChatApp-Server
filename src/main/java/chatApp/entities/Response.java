package chatApp.entities;

public class Response<T> {
    private final T data;
    private final String message;
    private final boolean isSucceed;
    private Response(boolean isSucceed,T data,String message)
    {
        this.data=data;
        this.isSucceed=isSucceed;
        this.message=message;
    }

    /**
     * Creates and returns a Response Object representing a successful response (to have an indication of a successful operation).
     * @param data - a non-null data object the response will contain.
     * @return returns a Response Object representing a Successful Response - contains a non-null data object, a null error message, and true boolean value to indicate success.
     * @param <T> - type of the data object received as param and that the response will contain.
     */
    public static<T> Response<T> createSuccessfulResponse(T data)
    {
        return  new Response<>(true,data,null);
    }

    /**
     * Creates and returns a Response Object representing a failure response (in order to not return exceptions as is, and explain the user what's the problem).
     * @param message- String, contains the explanation for the error,exception or failure of operation.
     * @return returns a Response Object representing a Failure Response - contains a non-null data object, a null error message, and true boolean value to indicate success.
     * @param <T> - type of the data object received as param and the type of the data the returned response will contain.
     */
    public  static <T> Response<T> createFailureResponse(String message)
    {
        return  new Response<>(false,null,message);
    }

    public boolean isSucceed()
    {
        return isSucceed;
    }

    public T getData()
    {
        if(isSucceed) return data;
        return null;
    }

    public String getMessage()
    {
        return message;
    }
}
