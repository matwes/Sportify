package matwes.zpi.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class SignInActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private View loginForm, progress;
    private CallbackManager callbackManager;

    private ApiInterface api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        api = RestService.getApiInstance();

        etEmail = findViewById(R.id.etEmailIn);
        etPassword = findViewById(R.id.etPasswordIn);
        loginForm = findViewById(R.id.loginForm);
        progress = findViewById(R.id.loginProgress);

        TextView tvResetPassword = findViewById(R.id.tvResetPassword);
        tvResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), ResetPassActivity.class));
            }
        });

        Button btnSignIn = findViewById(R.id.btnSignIn2);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        fbButtonSettings();

        Button btnSignInCode = findViewById(R.id.btnSignInByCode);
        btnSignInCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), SignInByCodeActivity.class));
            }
        });
    }

    private void fbButtonSettings() {
        callbackManager = CallbackManager.Factory.create();
        LoginButton btnFbLogin = findViewById(R.id.btnSignInFb);
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

                handleLogin(api.loginByFacebook(loginResult.getAccessToken().getToken()));
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (!Common.isEmailOk(email)) {
            etEmail.setError("Invalid email address");
        } else if (Common.isMocked()) {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Common.setLoginStatus(SignInActivity.this, true);
            startActivity(intent);
        } else {
            showProgress(true);
            handleLogin(api.login(email, password));
        }
    }

    private void handleLogin(Call<User> call) {
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Common.setLoginStatus(SignInActivity.this, true);

                showProgress(false);

                startActivity(intent);
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                showProgress(false);
                new AlertDialog.Builder(SignInActivity.this, R.style.AlertDialogTheme)
                        .setTitle("Błąd")
                        .setMessage("Brak połączenia z serwerem.")
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
        });
    }

    private void showProgress(boolean show) {
        loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}