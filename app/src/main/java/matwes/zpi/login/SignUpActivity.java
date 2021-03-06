package matwes.zpi.login;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import matwes.zpi.Common;
import matwes.zpi.MainActivity;
import matwes.zpi.R;
import matwes.zpi.api.ApiInterface;
import matwes.zpi.api.RestService;
import matwes.zpi.domain.RegisterData;
import matwes.zpi.utils.CustomDialog;
import matwes.zpi.utils.LoadingDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    @BindView(R.id.plec_man)
    RadioButton sex_man;
    @BindView(R.id.plec_woman)
    RadioButton sex_woman;
    @BindView(R.id.birth_date)
    TextInputEditText birthDate;
    private EditText etFirstName, etLastName, etEmail, etPassword;
    private CallbackManager callbackManager;
    private LoadingDialog dialog;
    private ApiInterface api;
    private String sDate, sTime;
    private double dLat, dLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        api = RestService.getApiInstance();

        dialog = new LoadingDialog(this);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        final Calendar calendar;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            calendar = Calendar.getInstance();
            final DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    sDate = String.format("%02d-%02d-%02d", year, month + 1, day);
                    birthDate.setText(String.format("%d-%d-%d", year, month + 1, day));
                }
            };

            birthDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);

                        new DatePickerDialog(SignUpActivity.this, datePicker, year, month, day).show();
                    }
                }
            });
        } else {
            birthDate.setText("1995-04-02");
        }

        sex_man.setChecked(true);
        fbButtonSettings();
    }

    @OnClick(R.id.btnSignUp2)
    public void signUp() {
        attemptRegister();
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

//                handleRegister(api.loginByFacebook(loginResult.getAccessToken().getToken()), true);
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
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String dateOfBirth = birthDate.getText().toString().trim();
        String sex = null;
        if (sex_man.isChecked() || sex_woman.isChecked()) {
            sex = sex_man.isChecked() ? "M" : "F";
        }

        if (email.equals("") || password.equals("") || firstName.equals("") || lastName.equals("") || dateOfBirth.equals("")) {
            CustomDialog.showError(SignUpActivity.this, "Wszystkie pola są wymagane");
            return;
        }
        RegisterData registerData = new RegisterData(email, password, firstName, lastName, dateOfBirth, sex);

        if (Common.isEmailWrong(email)) {
            etEmail.setError(getString(R.string.error_wrong_email));
        } else {
            dialog.showLoadingDialog(getString(R.string.loading));
            Call<ResponseBody> register = api.register(registerData);
            handleRegister(register, false);
        }
    }

    private void handleRegister(Call<ResponseBody> call, final boolean facebook) {

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                dialog.hideLoadingDialog();

                Class className = null;
                if (facebook) {
                    className = MainActivity.class;
                    Common.setLoginStatus(SignUpActivity.this, true);
                } else if (response.errorBody() != null) {
                    try {
                        CustomDialog.showError(SignUpActivity.this, response.errorBody().string());
                    } catch (IOException e) {
                        CustomDialog.showError(SignUpActivity.this, getString(R.string.error_message));
                        e.printStackTrace();
                    }
                } else if (response.code() != 200 || response.body() == null) {
                    CustomDialog.showError(SignUpActivity.this, getString(R.string.error_message));
                } else {
                    try {
                        CustomDialog.showInfo(SignUpActivity.this, response.body().string());
                        className = SignInActivity.class;
                    } catch (IOException e) {
                        CustomDialog.showError(SignUpActivity.this, getString(R.string.error_message));
                    }
                }

                if (className != null) {
                    Intent intent = new Intent(SignUpActivity.this, className);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
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