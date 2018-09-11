package com.jacsstuff.quizudo.answerPool;

import android.content.Context;
import android.content.Intent;

import com.jacsstuff.quizudo.options.DialogActivity;

import java.util.List;

public class AnswerPoolControllerImpl implements AnswerPoolController {

    private AnswerPoolView view;
    private Context context;
    private AnswerPoolDBManager dbManager;

    AnswerPoolControllerImpl(AnswerPoolView view, Context context){

        this.view = view;
        this.context = context;
        dbManager = new AnswerPoolDBManager(context);
        refreshListFromDb();
    }

    public void closeConnections(){
        dbManager.closeConnection();
    }

    public void refreshListFromDb(){
        List<String> answerPoolNames = dbManager.getAnswerPoolNames();

        view.setupList(answerPoolNames);
    }

    public void showDialogWithText(String message, String answerPoolName){

        Intent intent = new Intent(context, DeleteAnswerPoolDialogActivity.class);
        intent.putExtra(DeleteAnswerPoolDialogActivity.ANSWER.POOL_NAME.toString(), answerPoolName);
        intent.putExtra(DialogActivity.Dialog.MESSAGE.toString(), message);

        context.startActivity(intent);
    }


    @Override
    public void addAnswerPool(String name) {
        if(name == null){
            return;
        }
        String answerPoolName = name.trim();
        dbManager.addAnswerPool(answerPoolName);
        view.addToList(answerPoolName);
    }
}
