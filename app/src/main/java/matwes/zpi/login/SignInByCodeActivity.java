package matwes.zpi.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.login.LoginManager;
import matwes.zpi.AsyncTaskCompleteListener;
import matwes.zpi.Common;
import matwes.zpi.MainActivity;
import matwes.zpi.R;
import matwes.zpi.RequestAPI;

import org.json.JSONException;
import org.json.JSONObject;

public class SignInByCodeActivity
        extends AppCompatActivity implements AsyncTaskCompleteListener<String> {
    public static final String SIGN_IN = Common.URL + "/session/code";
    EditText etEmail, etCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_by_code);

        etEmail = findViewById(R.id.etEmailInCode);
        etCode = findViewById(R.id.etCode);

        Button btnSignIn = findViewById(R.id.btnSignInByCode2);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isEmailOk(etEmail.getText().toString())) {
                    new RequestAPI(v.getContext(), "POST", sessionCode(), SignInByCodeActivity.this, true)
                            .execute(SIGN_IN);
                } else
                    etEmail.setError("Invalid email address");
            }
        });
    }

    String sessionCode() {
        JSONObject json = new JSONObject();
        try {
            json.put("email", etEmail.getText().toString());
            json.put("code", etCode.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    @Override
    public void onTaskComplete(String result) {
        if (result.equals("200")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Common.setLoginStatus(this, true);
            startActivity(intent);
        } else {
            new AlertDialog.Builder(this)
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
    }
}
