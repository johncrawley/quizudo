package com.jacsstuff.quizudo.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;

/**
 * Created by John on 14/12/2016.
 *
 *  A loading dialog that disappears after a set amount of seconds.
 *  This is useful in the rare instance where an uncaught error will cause the dialog to not get dismissed.
 */
public class LoadingDialog {

    private ProgressDialog mDialog;

    public LoadingDialog(Context context, int messageId){
            mDialog = new ProgressDialog(context);
            mDialog.setMessage(context.getResources().getString(messageId));
            mDialog.setCancelable(false);
    }

    public void show(){
        mDialog.show();
        timerDelayRemoveDialog(mDialog);
    }

    private void timerDelayRemoveDialog(final ProgressDialog d){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                d.dismiss();
            }
        }, Consts.LOADING_DIALOG_TIMEOUT);
    }

   public void dismiss(){
       mDialog.hide();
       mDialog.dismiss();
   }


}
