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

import com.facebook.login.LoginManager;

import matwes.zpi.Common;
import matwes.zpi.MainActivity;
import matwes.zpi.R;
import matwes.zpi.api.ApiInterface;
import matwes.zpi.api.RestService;
import matwes.zpi.domain.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInByCodeActivity extends AppCompatActivity {
    private EditText etEmail, etCode;

    private ApiInterface api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_by_code);

        api = RestService.getApiInstance();

        etEmail = findViewById(R.id.etEmailInCode);
        etCode = findViewById(R.id.etCode);

        Button btnSignIn = findViewById(R.id.btnSignInByCode2);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString();
        String password = etCode.getText().toString();

        if (!Common.isEmailOk(email)) {
            etEmail.setError("Invalid email address");
        } else {
            handleLogin(api.loginByCode(email, password));
        }
    }

    private void handleLogin(Call<User> call) {
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                Intent intent = new Intent(SignInByCodeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Common.setLoginStatus(SignInByCodeActivity.this, true);
                startActivity(intent);
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                new AlertDialog.Builder(SignInByCodeActivity.this)
                        .setTitle("Error")
                        .setMessage("Wrong email or code")
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
}
