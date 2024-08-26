package com.example.assignment1.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class DeleteDialogFragment extends DialogFragment {
    private DeleteCallback callback;
    public static final String TAG = "DeleteDialogFragment";

    public DeleteDialogFragment() {}

    public DeleteDialogFragment setCallback(DeleteCallback callback) {
        this.callback = callback;
        return this;
    }

    /**
     * Interface definition for a callback to be invoked when the user confirms deletion.
     */
    public interface DeleteCallback {
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
                .setMessage("Are you sure you want to delete user?")
                .setPositiveButton(getString(android.R.string.ok), (dialogInterface, i) -> {
                    if (callback != null)
                        callback.onConfirm();
                    dialogInterface.dismiss();
                })
                .setNegativeButton(getString(android.R.string.cancel), (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }
}