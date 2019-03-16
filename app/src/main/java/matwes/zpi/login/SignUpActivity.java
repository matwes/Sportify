package matwes.zpi.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import matwes.zpi.Common;
import matwes.zpi.MainActivity;
import matwes.zpi.R;
import matwes.zpi.api.ApiInterface;
import matwes.zpi.api.RestService;
import matwes.zpi.domain.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    private EditText etFirstName, etLastName, etEmail, etPassword;
    private CallbackManager callbackManager;

    private ApiInterface api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        api = RestService.getApiInstance();

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        Button btnSignUp = findViewById(R.id.btnSignUp2);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });

        fbButtonSettings();
    }

    private void fbButtonSettings() {
        callbackManager = CallbackManager.Factory.create();
        LoginButton btnFbLogin = findViewById(R.id.btnSignUpFb);
        btnFbLogin.setReadPermissions(Common.permission);
        btnFbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender");
                request.setParameters(parameters);
                request.executeAsync();

                handleRegister(api.loginByFacebook(loginResult.getAccessToken().getToken()), true);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });
    }

    private void attemptRegister() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();

        if (!Common.isEmailOk(email)) {
            etEmail.setError("Wrong email");
        } else {
            Call<User> register = api.register(email, password, firstName, lastName);
            handleRegister(register, false);
        }
    }

    private void handleRegister(Call<User> call, final boolean facebook) {


        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                Class className;
                if (facebook) {
                    className = MainActivity.class;
                    Common.setLoginStatus(SignUpActivity.this, true);
                } else {
                    className = SignInByCodeActivity.class;
                }

                Intent intent = new Intent(SignUpActivity.this, className);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                final EditText input = new EditText(SignUpActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);

                new AlertDialog.Builder(SignUpActivity.this)
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
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}