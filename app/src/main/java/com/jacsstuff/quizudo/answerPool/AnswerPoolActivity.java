package com.jacsstuff.quizudo.answerPool;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.jacsstuff.quizudo.list.ListActionExecutor;
import com.jacsstuff.quizudo.list.ListAdapterHelper;
import com.jacsstuff.quizudo.main.MainActivity;
import com.jacsstuff.quizudo.R;
import com.jacsstuff.quizudo.options.DialogActivity;
import com.jacsstuff.quizudo.utils.ToolbarBuilder;
import com.jacsstuff.quizudo.utils.Utils;

import java.util.List;

public class AnswerPoolActivity extends AppCompatActivity implements ListActionExecutor {

    private Context context;
    public enum props { SELECTED_ACTIVITY_POOL}
    private AnswerPoolDBManager dbManager;
    private ListAdapterHelper listAdapterHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_pool);
        context = AnswerPoolActivity.this;
        dbManager = new AnswerPoolDBManager(this);
        listAdapterHelper = new ListAdapterHelper(context, this);
        ToolbarBuilder.setupToolbarWithTitle(this, getResources().getString(R.string.answer_pools_activity_title));
        EditText addAnswerPoolEditText = findViewById(R.id.authorPoolEditText);
        listAdapterHelper.setupKeyInput( addAnswerPoolEditText);
    }


    @Override
    protected  void onDestroy(){
        closeConnections();
        super.onDestroy();
    }


    @Override
    protected  void onResume(){
        super.onResume();
        refreshListFromDb();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_enabled_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.home_menu_item) {
            Utils.bringBackActivity(this, MainActivity.class);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(String item){
        Intent intent = new Intent(context,  AnswerListActivity.class);
        intent.putExtra(props.SELECTED_ACTIVITY_POOL.toString(), item);
        startActivity(intent);
    }


    @Override
    public void onLongClick(String item){
        String message = context.getResources().getString(R.string.remove_answer_pool_dialog_text, item);
        showDialogWithText(message, item);
    }


    @Override
    public void onTextEntered(String text){
        if(text == null){
            return;
        }
        String answerPoolName = text.trim();
        dbManager.addAnswerPool(answerPoolName);
        listAdapterHelper.addToList(answerPoolName);
    }


    public void closeConnections(){
        dbManager.closeConnection();
    }


    public void refreshListFromDb(){
        View noResultsFoundView = findViewById(R.id.noResultsFoundText);
        ListView list = findViewById(R.id.list1);
        List<String> answerPoolNames = dbManager.getAnswerPoolNames();
        listAdapterHelper.setupList(list, answerPoolNames, noResultsFoundView);
    }


    public void showDialogWithText(String message, String answerPoolName){
        Intent intent = new Intent(context, DeleteAnswerPoolDialogActivity.class);
        intent.putExtra(DeleteAnswerPoolDialogActivity.ANSWER.POOL_NAME.toString(), answerPoolName);
        intent.putExtra(DialogActivity.Dialog.MESSAGE.toString(), message);
        context.startActivity(intent);
    }


}
