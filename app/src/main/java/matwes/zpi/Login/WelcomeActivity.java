package matwes.zpi.Login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import matwes.zpi.Common;
import matwes.zpi.MainActivity;
import matwes.zpi.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        if(Common.getLoginStatus(this))
        {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        Button login = (Button) findViewById(R.id.btnSignIn);
        Button reg = (Button) findViewById(R.id.btnSignUp);

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

    }

    private void checkConnection()
    {
        if(!Common.isOnline(this))
        {
            showDialog();
        }
    }

    private void showDialog()
    {
        AlertDialog dialog = new AlertDialog.Builder(this)
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
