package matwes.zpi.Login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import matwes.zpi.AsyncTaskCompleteListener;
import matwes.zpi.Common;
import matwes.zpi.MainActivity;
import matwes.zpi.R;
import matwes.zpi.RequestAPI;

public class SignInActivity extends AppCompatActivity implements AsyncTaskCompleteListener<String>
{
    private EditText etEmail, etPassword;
    private CallbackManager callbackManager;
    private static final String SIGN_IN = "https://zpiapi.herokuapp.com/session";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        etEmail = (EditText) findViewById(R.id.etEmailIn);
        etPassword = (EditText) findViewById(R.id.etPasswordIn);

        TextView tvResetPassword = (TextView) findViewById(R.id.tvResetPassword);
        tvResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), ResetPassActivity.class));
            }
        });

        Button btnSignIn = (Button) findViewById(R.id.btnSignIn2);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.isEmailOk(etEmail.getText().toString())) {
                    new RequestAPI(v.getContext(), "POST", sessionString(), SignInActivity.this, true)
                            .execute(SIGN_IN);
                }
                else
                    etEmail.setError("Invalid email address");
            }
        });

        callbackManager = CallbackManager.Factory.create();

        LoginButton btnFbLogin = (LoginButton) findViewById(R.id.btnSignInFb);
        btnFbLogin.setReadPermissions(Common.permission);
        btnFbLogin.registerCallback(callbackManager, new FbCallback(this, this));

        Button btnSignInCode = (Button) findViewById(R.id.btnSignInByCode);
        btnSignInCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), SignInByCodeActivity.class));
            }
        });
    }

    @Override
    public void onTaskComplete(String result) {
        if(result.equals("200"))
        {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Common.setLoginStatus(this, true);
            startActivity(intent);
        }
        else
        {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Wrong email or password")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            LoginManager.getInstance().logOut();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    protected String sessionString()
    {
        JSONObject json = new JSONObject();
        try {
            json.put("email", etEmail.getText().toString());
            json.put("password", etPassword.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}
