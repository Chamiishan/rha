package roadcondition.cynsore.cyient.com.cynsore.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import android.util.Log;
import android.widget.Toast;


import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;


import roadcondition.cynsore.cyient.com.cynsore.view.aboutus.FeedbackActivity;
import roadcondition.cynsore.cyient.com.cynsore.utility.Constants;

public class AsyncServiceDataTask extends AsyncTask<String, Void, String>  {
    private Context mContext;
    public final static int SERVICE_CONNECTION_TIMEOUT = 20000;
    public final static int SOCKET_TIMEOUT = 20000;
    private String option;
    private JSONArray foodArray;
    ProgressDialog progressDialog;

    public AsyncServiceDataTask(Context context, String option) {
        this.mContext = context;
        this.option = option;

    }
    // constructor for food order object
    public AsyncServiceDataTask(Context context, JSONArray array, String option) {
        this.mContext = context;
        this.option = option;
        this.foodArray = array;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Loading Please wait...!");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            progressDialog.show();

    }

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection urlConnection = null;

        StringBuilder total = new StringBuilder();
        try {
            URL url = new URL(params[0]);

            urlConnection = (HttpURLConnection) url
                    .openConnection();
            urlConnection.setConnectTimeout(SERVICE_CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(SOCKET_TIMEOUT);

            InputStream in = urlConnection.getInputStream();
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
        } catch (SocketTimeoutException e) {
            progressDialog.dismiss();
//            urlConnection.disconnect();
//            requestTimeOutDialog();
            Toast.makeText(mContext, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
        } catch (ConnectTimeoutException e) {
            progressDialog.dismiss();
//            urlConnection.disconnect();
//            requestTimeOutDialog();
            Toast.makeText(mContext.getApplicationContext(), "Something went wrong, please try again", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
//            progressDialog.dismiss();
//            urlConnection.disconnect();
//            requestTimeOutDialog();
            e.printStackTrace();
            Toast.makeText(mContext.getApplicationContext(), "Something went wrong, please try again", Toast.LENGTH_LONG).show();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        Log.d("feedback", "" + total.toString());
        return total.toString();

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        try {
                if (option.equalsIgnoreCase(Constants.FEEDBACK_RESPONSE)) {
                    ((FeedbackActivity) mContext).reslut(s);
                }
                else {

                }


        } catch (Exception e) {
            Log.d("RESPONSE EXCEPTION --> ", "" + e);

        }
    }


}
