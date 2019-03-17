package matwes.zpi.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;

import matwes.zpi.R;

/**
 * Created by Mateusz Wesołowski
 */
public class CustomDialog {

    public static void showError(Context context, String message) {
        new AlertDialog
                .Builder(new ContextThemeWrapper(context, R.style.AlertDialogTheme))
                .setTitle(context.getString(R.string.error))
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static void showInfo(Context context, String message) {
        new AlertDialog
                .Builder(new ContextThemeWrapper(context, R.style.AlertDialogTheme))
                .setTitle(context.getString(R.string.info))
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }
}
