package matwes.zpi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class RequestAPI extends AsyncTask<String, Void, Integer> {
    private ProgressDialog progressDialog;
    private AsyncTaskCompleteListener<String> callback;

    private String parameter;
    private String method;
    private Context context;
    private boolean dialog;

    public RequestAPI(Context context, String method, String parameter, AsyncTaskCompleteListener<String> cb, boolean withDialog) {
        this.context = context;
        this.callback = cb;
        this.parameter = parameter;
        this.method = method;
        progressDialog = new ProgressDialog(context);
        dialog = withDialog;
    }


    @Override
    protected void onPreExecute() {
        if (dialog) {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }
    }

    @Override
    protected Integer doInBackground(String... params) {
        int responseCode = -1;

        try {
            URL url = new URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(5000);
            if (Common.getLoginStatus(context)) {
                String b = Common.getBearer(context);
                connection.setRequestProperty("Authentication", b);
            }

            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), Charset.forName("UTF-8")));
            wr.write(parameter);
            wr.flush();
            wr.close();

            responseCode = connection.getResponseCode();
            Log.i(method, "Sending 'POST' request to URL : " + url);
            Log.i(method, "Post parameters: " + parameter);
            Log.i(method, "Response Code: " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder responseOutput = new StringBuilder();
            while ((line = br.readLine()) != null) {
                responseOutput.append(line);
            }
            br.close();

            if (!Common.getLoginStatus(context)) {
                String bearer, id;
                try {
                    id = new JSONObject(responseOutput.toString()).getString("id");
                    bearer = connection.getHeaderField("Authentication");
                    SharedPreferences prefs = context.getSharedPreferences("EVENTS", Context.MODE_PRIVATE);
                    prefs.edit().putString("USER_ID", id).apply();
                    prefs.edit().putString("BEARER", bearer).apply();
                    prefs.edit().putString("LOGIN", parameter).apply();

                    System.out.println("ID USERA: " + id);
                    System.out.println(bearer);
                } catch (JSONException ignored) {
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return responseCode;
    }

    @Override
    protected void onPostExecute(Integer result) {
        if (dialog) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }
        callback.onTaskComplete(result + "");
    }
}
