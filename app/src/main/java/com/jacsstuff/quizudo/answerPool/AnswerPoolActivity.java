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
import android.widget.Toast;

import com.jacsstuff.quizudo.dialog.ConfirmDialog;
import com.jacsstuff.quizudo.dialog.DialogLoader;
import com.jacsstuff.quizudo.list.ListActionExecutor;
import com.jacsstuff.quizudo.list.ListAdapterHelper;
import com.jacsstuff.quizudo.main.MainActivity;
import com.jacsstuff.quizudo.R;
import com.jacsstuff.quizudo.utils.ToolbarBuilder;
import com.jacsstuff.quizudo.utils.Utils;

import java.util.List;

public class AnswerPoolActivity extends AppCompatActivity implements ListActionExecutor, ConfirmDialog.OnFragmentInteractionListener {

    private Context context;
    public enum props { SELECTED_ACTIVITY_POOL}
    private AnswerPoolDBManager dbManager;
    private ListAdapterHelper listAdapterHelper;
    private String selectedAnswerPoolName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_pool);
        context = AnswerPoolActivity.this;
        dbManager = new AnswerPoolDBManager(this);
        setupList();
        ToolbarBuilder.setupToolbarWithTitle(this, getResources().getString(R.string.answer_pools_activity_title));
    }


    private void setupList(){
        ListView list = findViewById(R.id.list1);
        listAdapterHelper = new ListAdapterHelper(context, list, this);
        EditText addAnswerPoolEditText = findViewById(R.id.answerPoolEditText);
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
        selectedAnswerPoolName = item;
        DialogLoader.loadDialogWith(getFragmentManager(), message);
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
        List<String> answerPoolNames = dbManager.getAnswerPoolNames();
        listAdapterHelper.setupList(answerPoolNames, android.R.layout.simple_list_item_1, noResultsFoundView);
    }


    @Override
    public void onFragmentInteraction(boolean confirmed) {
        if (confirmed) {
             if( dbManager.removeAnswerPool(selectedAnswerPoolName)){
                Toast.makeText(context, R.string.answer_pool_deleted_pool_toast, Toast.LENGTH_SHORT).show();
            }
            refreshListFromDb();
        }
    }
}
