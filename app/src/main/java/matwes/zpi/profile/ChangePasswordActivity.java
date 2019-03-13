package matwes.zpi.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import matwes.zpi.AsyncTaskCompleteListener;
import matwes.zpi.Common;
import matwes.zpi.R;
import matwes.zpi.RequestAPI;

public class ChangePasswordActivity extends AppCompatActivity {
    private static final String UPDATE_PROFILE = Common.URL + "/users/";
    private EditText newPassword;
    private EditText repeatPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Intent intent = getIntent();
        final long userId = intent.getLongExtra("userId", -1);

        newPassword = findViewById(R.id.etNewPassword);
        repeatPassword = findViewById(R.id.etRepeatPassword);
        Button changePassword = findViewById(R.id.btnChangePassword);

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNewPasswordOK()) {
                    new RequestAPI(v.getContext(), "PATCH",
                            "{\"password\":\"" + newPassword.getText() + "\"}",
                            new AsyncTaskCompleteListener<String>() {
                                @Override
                                public void onTaskComplete(String result) {
                                    if (result.equals("200")) {
                                        showAlert("Info", getString(R.string.password_changed), android.R.drawable.ic_dialog_info);
                                    } else
                                        showAlert("Error", getString(R.string.connection_problem), android.R.drawable.ic_dialog_alert);
                                }
                            }, true).execute(UPDATE_PROFILE + userId);
                }
            }
        });
    }

    boolean isNewPasswordOK() {
        if (newPassword.length() == 0) {
            newPassword.setError(getString(R.string.password_required));
            return false;
        } else if (!newPassword.getText().toString().equals(repeatPassword.getText().toString())) {
            repeatPassword.setError(getString(R.string.identical_passwords));
            return false;
        }
        return true;
    }

    private void showAlert(String title, String message, int icon) {
        new AlertDialog
                .Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setIcon(icon)
                .show();
    }
}
