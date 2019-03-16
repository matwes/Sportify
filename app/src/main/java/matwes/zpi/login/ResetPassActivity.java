package matwes.zpi.login;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.login.LoginManager;

import matwes.zpi.Common;
import matwes.zpi.R;
import matwes.zpi.api.ApiInterface;
import matwes.zpi.api.RestService;
import matwes.zpi.domain.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPassActivity extends AppCompatActivity {
    private EditText etEmail;

    private ApiInterface api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        api = RestService.getApiInstance();

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
        if (Common.isEmailOk(email)) {
            handleResetPassword(api.resetPassword(email));
        } else {
            Toast.makeText(this, "Email is not OK!", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleResetPassword(Call<User> call) {
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                Toast.makeText(ResetPassActivity.this,
                        "Your password has been reset successfully!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                new AlertDialog.Builder(ResetPassActivity.this, R.style.AlertDialogTheme)
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
}