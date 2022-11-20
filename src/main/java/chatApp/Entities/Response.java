package chatApp.Entities;

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
    public static<T> Response<T> createSuccessfulResponse(T data)
    {
        return  new Response<>(true,data,null);
    }
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
