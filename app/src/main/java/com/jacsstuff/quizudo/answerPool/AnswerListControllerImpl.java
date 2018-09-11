package com.jacsstuff.quizudo.answerPool;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jacsstuff.quizudo.options.DialogActivity;

import java.util.List;

public class AnswerListControllerImpl implements AnswerListController {

    private AnswerPoolDBManager db;
    private Context context;
    private AnswerListView view;
    private String answerPoolName;
    private List<String> items;

    AnswerListControllerImpl(AnswerListView view, Context context, String answerPoolName){
        this.view = view;
        this.context = context;
        db = new AnswerPoolDBManager(context);
        this.answerPoolName = answerPoolName;
    }

    private void log(String msg){
        Log.i("AnswerListCtrlImpl", msg);
    }
    public void refreshListFromDb(){
        log("Entered refreshListFromDb()");
        items = db.getAnswerItems(answerPoolName);
        log("Items retrieved from answer pool db manager");
        view.setupList(items);
    }

    public void addAnswer(String text){
        text = text.trim();
        if(items.contains(text)){
            return;
        }
       // items.add(text);
        db.addAnswerPoolItem(answerPoolName, text);
        view.updateList(text);
    }


    public void showDialogWithText(String message, String answer, String answerPoolName){

        Intent intent = new Intent(context, DeleteAnswerDialogActivity.class);
        intent.putExtra(DeleteAnswerDialogActivity.ANSWER.POOL_NAME.toString(), answerPoolName);
        intent.putExtra(DeleteAnswerDialogActivity.ANSWER.VALUE.toString(),answer);
        intent.putExtra(DialogActivity.Dialog.MESSAGE.toString(), message);

        context.startActivity(intent);
    }


}
