package matwes.zpi.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import matwes.zpi.Common;
import matwes.zpi.MainActivity;
import matwes.zpi.R;
import matwes.zpi.utils.CustomDialog;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        if (Common.getLoginStatus(this)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        Button login = findViewById(R.id.btnSignIn);
        Button reg = findViewById(R.id.btnSignUp);

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), SignUpActivity.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), SignInActivity.class));
            }
        });
        checkConnection();

//        final EditText serverAddress = findViewById(R.id.serverIp);
//        serverAddress.setText(Common.URL);
//        serverAddress.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                serverAddress.setVisibility(View.VISIBLE);
//            }
//        });

//        Button changeAddress = findViewById(R.id.changeIp);
//        changeAddress.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Common.URL = serverAddress.getText().toString();
//                Toast.makeText(getApplicationContext(), "Ip has changed", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void checkConnection() {
        if (!Common.isOnline(this)) {
            CustomDialog.showError(this, getString(R.string.error_no_internet));
        }
    }
}
