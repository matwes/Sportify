package matwes.zpi;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Created by Mateusz Wesołowski
 */

public class GetMethodAPI extends AsyncTask<String, Void, String> {
    private AsyncTaskCompleteListener<String> callback;
    private ProgressDialog progressDialog;
    private boolean dialog;
    private Context context;

    public GetMethodAPI(Context context, AsyncTaskCompleteListener<String> callback, boolean withDialog) {
        this.context = context;
        this.callback = callback;
        progressDialog = new ProgressDialog(context);
        dialog = withDialog;
    }

    @Override
    protected void onPreExecute() {
        if (dialog) {
            progressDialog.setMessage("Downloading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        HostnameVerifier hostnameVerifier = new HostnameVerifier() {

            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        String address = params[0];
        URL url;
        HttpURLConnection connection = null;

        try {
            url = new URL(address);
            connection = (HttpURLConnection) url.openConnection();
            //connection.setHostnameVerifier(hostnameVerifier);
            connection.setConnectTimeout(5000);

            String b = Common.getBearer(context);
            connection.setRequestProperty("Authentication", b);

            InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder builder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            reader.close();

            Log.i("GET", url.toString());
            Log.i("GET", builder.toString());

            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        if (dialog)
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        callback.onTaskComplete(s);
    }
}
