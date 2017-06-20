package matwes.zpi.Login;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import matwes.zpi.AsyncTaskCompleteListener;
import matwes.zpi.RequestAPI;

/**
 * Created by mateu on 30.03.2017.
 */

class FbCallback implements FacebookCallback<LoginResult>
{
    private Context context;
    private AsyncTaskCompleteListener<String> inter;
    private static final String SIGN_UP_FB = "https://zpiapi.herokuapp.com/session/facebook";

    FbCallback(Context context, AsyncTaskCompleteListener<String> inter) {
        this.context = context;
        this.inter = inter;
    }

    @Override
    public void onSuccess(final LoginResult loginResult)
    {
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender");
        request.setParameters(parameters);
        request.executeAsync();
        new RequestAPI(context, "POST", "{ \"token\": \""+loginResult.getAccessToken().getToken()+"\"}", inter, true)
                .execute(SIGN_UP_FB);
    }

    @Override
    public void onCancel() {
        Log.v("LoginActivity", "Cancelled");
    }

    @Override
    public void onError(FacebookException error) {
        Log.v("LoginActivity", error.getCause().toString());
    }
}
