package matwes.zpi.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import matwes.zpi.Common;
import matwes.zpi.MainActivity;
import matwes.zpi.R;

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

        final EditText serverAddress = findViewById(R.id.serverIp);
        serverAddress.setText(Common.URL);
        serverAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serverAddress.setVisibility(View.VISIBLE);
            }
        });

        Button changeAddress = findViewById(R.id.changeIp);
        changeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.URL = serverAddress.getText().toString();
                Toast.makeText(getApplicationContext(), "Ip has changed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkConnection() {
        if (!Common.isOnline(this)) {
            showDialog();
        }
    }

    private void showDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Connection Required")
                .setMessage("Make sure your wireless is on and connected")
                .setCancelable(false)
                .setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkConnection();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
