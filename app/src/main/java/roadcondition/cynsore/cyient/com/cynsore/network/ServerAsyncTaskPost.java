package roadcondition.cynsore.cyient.com.cynsore.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import roadcondition.cynsore.cyient.com.cynsore.R;

public class ServerAsyncTaskPost extends AsyncTask<String, Integer, String> {

    private String encodeUrl;

    public ServerAsyncTaskPost(Context context, ServerHelper serverHelper) {
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
                } catch (Exception e) {
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

        String JsonResponse = null;
        String JsonDATA = params[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            encodeUrl = (mServerHelper.getUrl());
            URL url = new URL(encodeUrl);
            Log.e("URL", encodeUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            // is output buffer writter
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setConnectTimeout(mServerHelper.getConnTimeout());
            //set headers and method
            Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
            writer.write(JsonDATA);
            // json data
            writer.close();
            InputStream inputStream = urlConnection.getInputStream();
            //input stream
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String inputLine;
            while ((inputLine = reader.readLine()) != null)
                buffer.append(inputLine + "\n");
            if (buffer.length() == 0) {
                // Stream was empty. No point in parsing.
                return null;
            }
            JsonResponse = buffer.toString();
            //response data
            Log.i("TAG", JsonResponse);
            try {
                //send to post execute
                return JsonResponse;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("TAG", "Error closing stream", e);
                }
            }
        }
        return null;
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
                    Log.e("REQUEST", encodeUrl);
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