package matwes.zpi.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import matwes.zpi.AsyncTaskCompleteListener;
import matwes.zpi.Common;
import matwes.zpi.R;
import matwes.zpi.RequestAPI;

public class ResetPassActivity extends AppCompatActivity implements AsyncTaskCompleteListener<String> {
    private static final String RESET = Common.URL + "/session/code";
    private EditText etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        etEmail = findViewById(R.id.etEmailReset);

        Button btnResetPass = findViewById(R.id.btnResetPass);
        btnResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isEmailOk(etEmail.getText().toString())) {
                    new RequestAPI(v.getContext(), "PUT",
                            "{ \"email\": \"" + etEmail.getText().toString() + "\"}",
                            ResetPassActivity.this, true).execute(RESET);

                } else
                    Toast.makeText(v.getContext(), "Email is not OK!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTaskComplete(String result) {
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    }
}