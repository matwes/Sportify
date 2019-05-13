package matwes.zpi.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import matwes.zpi.domain.AuthToken;
import matwes.zpi.utils.CustomDialog;
import matwes.zpi.utils.LoadingDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    private EditText etFirstName, etLastName, etEmail, etPassword;
    private CallbackManager callbackManager;
    private LoadingDialog dialog;

    private ApiInterface api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        api = RestService.getApiInstance();

        dialog = new LoadingDialog(this);
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

        if (Common.isEmailWrong(email)) {
            etEmail.setError(getString(R.string.error_wrong_email));
        } else {
            dialog.showLoadingDialog(getString(R.string.loading));
            Call<AuthToken> register = api.register(email, password, firstName, lastName);
            handleRegister(register, false);
        }
    }

    private void handleRegister(Call<AuthToken> call, final boolean facebook) {

        call.enqueue(new Callback<AuthToken>() {
            @Override
            public void onResponse(@NonNull Call<AuthToken> call, @NonNull Response<AuthToken> response) {
                dialog.hideLoadingDialog();
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
            public void onFailure(@NonNull Call<AuthToken> call, @NonNull Throwable t) {
                final EditText input = new EditText(SignUpActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);

                CustomDialog.showError(SignUpActivity.this, getString(R.string.error_message));

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