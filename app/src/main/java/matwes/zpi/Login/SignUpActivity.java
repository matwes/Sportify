package matwes.zpi.Login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

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

public class SignUpActivity extends AppCompatActivity implements AsyncTaskCompleteListener<String>
{
    private EditText etFirstName, etLastName, etEmail, etPassword;
    private CallbackManager callbackManager;
    private static final String SIGN_UP = "https://zpiapi.herokuapp.com/users";
    private boolean facebook = true;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);

        Button btnSignUp = (Button) findViewById(R.id.btnSignUp2);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.isEmailOk(etEmail.getText().toString())) {
                    new RequestAPI(v.getContext(), "POST", newUserString(), SignUpActivity.this, true)
                            .execute(SIGN_UP);
                    facebook = false;
                }
                else
                    etEmail.setError("Wrong email");
            }
        });


        callbackManager = CallbackManager.Factory.create();

        LoginButton btnFbLogin = (LoginButton) findViewById(R.id.btnSignUpFb);
        btnFbLogin.setReadPermissions(Common.permission);
        btnFbLogin.registerCallback(callbackManager, new FbCallback(this, this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onTaskComplete(String result)
    {
        if(result.equals("200"))
        {
            Class className;
            if(facebook) {
                className = MainActivity.class;
                Common.setLoginStatus(this, true);
            }
            else
                className = SignInByCodeActivity.class;
            
            Intent intent = new Intent(this, className);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else
        {
            final EditText input = new EditText(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);

            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Something went wrong :(")
                    .setCancelable(false)
                    .setView(input)
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

    protected String newUserString()
    {
        JSONObject json = new JSONObject();
        try {
            json.put("email", etEmail.getText().toString());
            json.put("password", etPassword.getText().toString());
            json.put("firstName", etFirstName.getText().toString());
            json.put("lastName", etLastName.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}
