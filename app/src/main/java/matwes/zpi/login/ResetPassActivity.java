package matwes.zpi.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.login.LoginManager;

import matwes.zpi.Common;
import matwes.zpi.R;
import matwes.zpi.api.ApiInterface;
import matwes.zpi.api.RestService;
import matwes.zpi.domain.User;
import matwes.zpi.utils.CustomDialog;
import matwes.zpi.utils.LoadingDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPassActivity extends AppCompatActivity {
    private EditText etEmail;
    private LoadingDialog dialog;

    private ApiInterface api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        api = RestService.getApiInstance();

        dialog = new LoadingDialog(this);
        etEmail = findViewById(R.id.etEmailReset);

        Button btnResetPass = findViewById(R.id.btnResetPass);
        btnResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptResetPassword();
            }
        });
    }

    private void attemptResetPassword() {
        String email = etEmail.getText().toString();
        if (Common.isEmailWrong(email)) {
            etEmail.setError(getString(R.string.error_wrong_email));
        } else {
            dialog.showLoadingDialog(getString(R.string.loading));
            handleResetPassword(api.resetPassword(email));
        }
    }

    private void handleResetPassword(Call<User> call) {
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                dialog.hideLoadingDialog();
                CustomDialog.showInfo(ResetPassActivity.this, getString(R.string.info_password_reset));
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                dialog.hideLoadingDialog();
                CustomDialog.showError(ResetPassActivity.this, getString(R.string.error_message));
                LoginManager.getInstance().logOut();
            }
        });
    }
}