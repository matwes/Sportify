package matwes.zpi.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import matwes.zpi.Common;
import matwes.zpi.R;
import matwes.zpi.api.ApiInterface;
import matwes.zpi.api.RestService;
import matwes.zpi.domain.SuccessResponse;
import matwes.zpi.events.AddEventActivity;
import matwes.zpi.utils.CustomDialog;
import matwes.zpi.utils.LoadingDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResendLinkActivity extends AppCompatActivity {
    @BindView(R.id.messageText)
    TextView messageText;

    @BindView(R.id.buttonLogin)
    Button btnMessageText;

    @BindView(R.id.buttonResendLink)
    Button btnResentLink;

    String message = "";
    String email = "";
    private LoadingDialog dialog;
    private ApiInterface api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resend_link);
        ButterKnife.bind(this);

        api = RestService.getApiInstance();
        dialog = new LoadingDialog(this);

        Intent intent = getIntent();
        message = intent.getStringExtra("message");
        email = intent.getStringExtra("email");

        messageText.setText(message);
    }

    @OnClick(R.id.buttonLogin)
    public void backToLoginTap() {
        finish();
    }

    @OnClick(R.id.buttonResendLink)
    public void resendLinkTap() {
        dialog.showLoadingDialog(getString(R.string.loading));
        Call<SuccessResponse> call = api.resendEmail(email);
        call.enqueue(new Callback<SuccessResponse>() {
            @Override
            public void onResponse(@NonNull Call<SuccessResponse> call, @NonNull Response<SuccessResponse> response) {
                dialog.hideLoadingDialog();
            }

            @Override
            public void onFailure(@NonNull Call<SuccessResponse> call, @NonNull Throwable t) {
                dialog.hideLoadingDialog();
                CustomDialog.showError(ResendLinkActivity.this, getString(R.string.error_message));
            }
        });
    }
}
