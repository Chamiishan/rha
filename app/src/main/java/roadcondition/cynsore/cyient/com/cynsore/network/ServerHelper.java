package roadcondition.cynsore.cyient.com.cynsore.network;


import java.util.HashMap;

/**
 * Created by ij39559 on 05/Jan/2017.
 */

public abstract class ServerHelper {

    private String url;
    private HashMap<String, String> httpHeader;
    private Object object;
    private String date;
    private int mConnTimeout = 30000;

    public int getConnTimeout() {
        return mConnTimeout;
    }

    public void setConnTimeout(int connTimeout) {
        mConnTimeout = connTimeout;
    }

    public String getDate() {
        return date;
    }

    public void addHttpHeader(String headerKey, String headerValue) throws NullPointerException {
        if (httpHeader == null) {
            httpHeader = new HashMap<>();
        }
        httpHeader.put(headerKey, headerValue);
    }

    public HashMap<String, String> getHttpHeader() {
        return httpHeader;
    }

    public void setTag(Object object) {
        this.object = object;
    }

    public Object getTag() {
        return object;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public abstract void onFailure(Object o) throws NullPointerException;

    public abstract void onSuccess(Object o) throws NullPointerException;

    public abstract void onServerError(String message);


    public void setDate(String date) {
        this.date = date;
    }
}