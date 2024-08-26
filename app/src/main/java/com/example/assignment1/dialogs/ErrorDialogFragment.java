package com.example.assignment1.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ErrorDialogFragment extends DialogFragment {
    private ErrorCallback callback;
    public static final String TAG = "ErrorDialogFragment";
    private String errorMessage;

    public ErrorDialogFragment() {}

    public ErrorDialogFragment setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public ErrorDialogFragment setCallback(ErrorCallback callback) {
        this.callback = callback;
        return this;
    }

    /**
     * Interface definition for a callback to be invoked when the user clicks ok.
     */
    public interface ErrorCallback {
        void onConfirm();
    }

    /**
     * Creates the dialog to be displayed.
     *
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The Dialog to be displayed.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.setCancelable(false);
        return new MaterialAlertDialogBuilder(getContext())
                .setTitle("Error!")
                .setMessage(errorMessage)
                .setPositiveButton(getString(android.R.string.ok), (dialogInterface, i) -> {
                    if (callback != null)
                        callback.onConfirm();
                    dialogInterface.cancel();
                })
                .show();
    }
}