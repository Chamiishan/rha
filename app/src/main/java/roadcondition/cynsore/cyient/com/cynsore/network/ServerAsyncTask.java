package roadcondition.cynsore.cyient.com.cynsore.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import roadcondition.cynsore.cyient.com.cynsore.R;

/**
 * Created by ij39559 on 05/Jan/2017.
 */

public class ServerAsyncTask extends AsyncTask<String, Integer, String> {

    private String url;

    public ServerAsyncTask(Context context, ServerHelper serverHelper) {
        mServerHelper = serverHelper;
        mContext = context;
    }

    private ProgressDialog pDialog3;
    private ServerHelper mServerHelper;
    private Context mContext;
    private boolean mShowProgress = true;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mShowProgress) {
            try {
                pDialog3 = new ProgressDialog(mContext, R.style.MyTheme);
                pDialog3.setIndeterminate(false);
                pDialog3.setCancelable(false);
                pDialog3.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
                try {
                    pDialog3.show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
                ((Activity) mContext).finish();
            }
        }
    }

    public void showCallProgress(boolean showProgress) {
        mShowProgress = showProgress;
    }

    @Override
    protected String doInBackground(String... params) {
        String response = null;
        try {
            String encodeUrl = (mServerHelper.getUrl());
            url = encodeUrl;
            Log.e("URL", url);
            URL mUrl = new URL(encodeUrl);
            HttpURLConnection httpConnection = (HttpURLConnection) mUrl.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Content-length", "0");
            httpConnection.setUseCaches(true);
            httpConnection.setAllowUserInteraction(false);
            httpConnection.setConnectTimeout(30000);
            httpConnection.setReadTimeout(mServerHelper.getConnTimeout());
            System.out.print(mUrl);
            HashMap<String, String> httpHeaderMap = mServerHelper.getHttpHeader();
            if (httpHeaderMap != null && httpHeaderMap.size() > 0) {
                Iterator iterator = httpHeaderMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry pair = (Map.Entry) iterator.next();
                    httpConnection.setRequestProperty(String.valueOf(pair.getKey()), String.valueOf(pair.getValue()));
                }
            }

            httpConnection.connect();
            int responseCode = httpConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                response = sb.toString();
            } else if (responseCode == 500) {
                Log.e("SERVER ERROR", "Internal Server Error");
                return "500";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return response;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Toast.makeText(mContext, "Cancelled", Toast.LENGTH_LONG).show();
        if (pDialog3 != null && pDialog3.isShowing()) {
            pDialog3.dismiss();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (!isCancelled()) {
            try {
                if (pDialog3 != null && pDialog3.isShowing()) {
                    pDialog3.dismiss();
                }
                if (result != null && !result.equalsIgnoreCase("500")) {
                    Log.e("REQUEST", url);
                    Log.e("RESPONSE", result);
                    mServerHelper.onSuccess(result);
                } else if (result != null && result.equalsIgnoreCase("500")) {
                    mServerHelper.onServerError("Internal server error.");
                } else {
                    mServerHelper.onFailure(result);
                }

            } catch (WindowManager.BadTokenException e) {
                e.printStackTrace();
                mServerHelper.onFailure(null);
            } catch (Exception e) {
                e.printStackTrace();
                mServerHelper.onFailure(null);
            }
        }
    }
}