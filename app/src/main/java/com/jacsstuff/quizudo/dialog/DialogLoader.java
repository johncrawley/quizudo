package com.jacsstuff.quizudo.dialog;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;

public class DialogLoader {

    public static void loadDialogWith(FragmentManager fragmentManager, String msg){
        DialogFragment dialogFrag = new ConfirmDialog();
        Bundle args = new Bundle();
        args.putString(ConfirmDialog.TEXT_KEY, msg);
        dialogFrag.setArguments(args);
        dialogFrag.show(fragmentManager, "dialog");

    }
}
