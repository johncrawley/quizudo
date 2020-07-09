package com.jacsstuff.quizudo.creator.express;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.jacsstuff.quizudo.R;
import com.jacsstuff.quizudo.answerPool.AnswerListActivity;
import com.jacsstuff.quizudo.answerPool.AnswerPoolActivity;
import com.jacsstuff.quizudo.dialog.ConfirmDialog;
import com.jacsstuff.quizudo.list.ListActionExecutor;
import com.jacsstuff.quizudo.list.ListAdapterHelper;
import com.jacsstuff.quizudo.main.MainActivity;
import com.jacsstuff.quizudo.utils.ToolbarBuilder;
import com.jacsstuff.quizudo.utils.Utils;

import java.util.List;


public class GeneratorsActivity extends AppCompatActivity implements ListActionExecutor, ConfirmDialog.OnFragmentInteractionListener {

    private Context context;
    private QuestionGeneratorDbManager dbManager;
    private ListAdapterHelper listAdapterHelper;
    private String selectedName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generators);

        context = GeneratorsActivity.this;
        dbManager = new QuestionGeneratorDbManager(this);
        listAdapterHelper = new ListAdapterHelper(context, this);
        EditText editText = findViewById(R.id.nameEditText);
        listAdapterHelper.setupKeyInput( editText);
        setupToolbar();
    }


    private void setupToolbar(){

        ToolbarBuilder.setupToolbarWithTitle(this, getResources().getString(R.string.question_generator_activity_title));
        ActionBar actionBar = getActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    protected  void onDestroy(){
        dbManager.closeConnection();
        super.onDestroy();
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
        else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(String item){
        Intent intent = new Intent(context,  AnswerListActivity.class);
        intent.putExtra(AnswerPoolActivity.props.SELECTED_ACTIVITY_POOL.toString(), item);
        startActivity(intent);
    }


    @Override
    public void onLongClick(String item){
        String message = context.getResources().getString(R.string.delete_generator_dialog_text, item);
        showDialogWith(message);
        selectedName = item;
    }


    private void showDialogWith(String msg){

        DialogFragment dialogFrag = new ConfirmDialog();
        Bundle args = new Bundle();
        args.putString(ConfirmDialog.TEXT_KEY,msg);
        dialogFrag.setArguments(args);
        dialogFrag.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onTextEntered(String text){
        if(text == null){
            return;
        }
        String name = text.trim();
        dbManager.addQuestionGenerator(name);
        listAdapterHelper.addToList(name);
    }


    @Override
    protected  void onResume(){
        super.onResume();
        refreshListFromDb();
    }


    public void refreshListFromDb(){
        View noResultsFoundView = findViewById(R.id.noResultsFoundText);
        ListView list = findViewById(R.id.list1);
        List<String> questionGeneratorNames = dbManager.retrieveGeneratorNames();
        listAdapterHelper.setupList(list, questionGeneratorNames, noResultsFoundView);
    }



    @Override
    public void onFragmentInteraction(boolean confirmed) {
        if (confirmed) {
            dbManager.removeQuestionGenerator(selectedName);
        }
    }
}
