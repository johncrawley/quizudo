package com.jacsstuff.quizudo.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.jacsstuff.quizudo.R;


public class ConfirmDialog extends DialogFragment implements DialogInterface.OnClickListener {


    public OnFragmentInteractionListener mCallback;
    public static final String TEXT_KEY = "textKey";

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(boolean confirmed);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String msg = getArguments().getString(TEXT_KEY);
        return new AlertDialog.Builder(getActivity()).setTitle(R.string.app_name).setMessage(msg)
                .setPositiveButton("OK", this).setNegativeButton("CANCEL", null).create();
    }


    @Override
    public void onClick(DialogInterface dialog, int position) {
        try {
            mCallback = (OnFragmentInteractionListener) getActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mCallback.onFragmentInteraction(true);
        dialog.dismiss();
    }
}