package com.jacsstuff.quizudo.answerPool;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.jacsstuff.quizudo.R;
import com.jacsstuff.quizudo.answerPool.AnswerPoolDBManager;
import com.jacsstuff.quizudo.options.DialogActivity;

public class DeleteAnswerPoolDialogActivity extends DialogActivity {

    public enum ANSWER{ POOL_NAME, VALUE}
    @Override
    public void yesButtonAction(Intent intent, Context context){

        AnswerPoolDBManager dbManager = new AnswerPoolDBManager(context);
        String answerPoolName = intent.getStringExtra(ANSWER.POOL_NAME.toString());

        if(answerPoolName != null){
           if(dbManager.removeAnswerPool(answerPoolName)){
               Toast.makeText(context, R.string.answer_pool_deleted_pool_toast, Toast.LENGTH_SHORT).show();
           }
        }

        finish();
    }
}
