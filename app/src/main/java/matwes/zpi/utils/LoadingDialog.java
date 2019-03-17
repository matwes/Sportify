package matwes.zpi.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Mateusz Wesołowski
 */
public class LoadingDialog {
    private ProgressDialog progressDialog;

    public LoadingDialog(Context context) {
        progressDialog = new ProgressDialog(context);
    }

    public void showLoadingDialog(String message) {
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void hideLoadingDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
