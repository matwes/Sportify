package matwes.zpi.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.io.IOException;

import matwes.zpi.Common;
import matwes.zpi.MainActivity;
import matwes.zpi.R;
import matwes.zpi.api.ApiInterface;
import matwes.zpi.api.RestService;
import matwes.zpi.domain.AuthToken;
import matwes.zpi.utils.CustomDialog;
import matwes.zpi.utils.LoadingDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private LoadingDialog dialog;
    private CallbackManager callbackManager;

    private ApiInterface api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        api = RestService.getApiInstance();

        dialog = new LoadingDialog(this);
        etEmail = findViewById(R.id.etEmailIn);
        etPassword = findViewById(R.id.etPasswordIn);

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
        //btnFbLogin.setReadPermissions(Common.permission);
        btnFbLogin.setPermissions("email", "public_profile");
        btnFbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                System.out.println(object);
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender");
                request.setParameters(parameters);
                request.executeAsync();

                AccessToken accessToken = loginResult.getAccessToken();

                handleLogin(api.loginByFacebook(accessToken.getToken()), true);
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

        if (Common.isEmailWrong(email)) {
            etEmail.setError(getString(R.string.error_wrong_email));
        } else if (Common.isMocked()) {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Common.setLoginStatus(SignInActivity.this, true);
            startActivity(intent);
        } else {
            dialog.showLoadingDialog(getString(R.string.loading));
            handleLogin(api.login(email, password), false);
        }
    }

    private void handleLogin(Call<AuthToken> call, final boolean facebook) {
        call.enqueue(new Callback<AuthToken>() {
            @Override
            public void onResponse(@NonNull Call<AuthToken> call, @NonNull Response<AuthToken> response) {

                if (response.errorBody() != null) {
                    if (facebook) {
                        LoginManager.getInstance().logOut();
                    }
                    try {
                        String errorMessage = response.errorBody().string();
                        CustomDialog.showError(SignInActivity.this, errorMessage);
                    } catch (IOException e) {
                        CustomDialog.showError(SignInActivity.this, getString(R.string.error_message));
                        e.printStackTrace();
                    }
                    dialog.hideLoadingDialog();
                } else if (response.body() != null && response.body().getToken() != null) {
                    Common.setToken(SignInActivity.this, response.body().getToken());

                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    Common.setLoginStatus(SignInActivity.this, true);
                    dialog.hideLoadingDialog();

                    startActivity(intent);
                } else {
                    if (facebook) {
                        LoginManager.getInstance().logOut();
                    }
                    CustomDialog.showError(SignInActivity.this, getString(R.string.wrongLoginPass));
                    dialog.hideLoadingDialog();
                }

            }

            @Override
            public void onFailure(@NonNull Call<AuthToken> call, @NonNull Throwable t) {
                dialog.hideLoadingDialog();
                CustomDialog.showError(SignInActivity.this, getString(R.string.error_message));
                LoginManager.getInstance().logOut();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}