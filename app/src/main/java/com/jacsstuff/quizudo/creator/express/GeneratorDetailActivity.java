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
import com.jacsstuff.quizudo.dialog.DialogLoader;
import com.jacsstuff.quizudo.list.ListActionExecutor;
import com.jacsstuff.quizudo.list.ListAdapterHelper;
import com.jacsstuff.quizudo.main.MainActivity;
import com.jacsstuff.quizudo.utils.ToolbarBuilder;
import com.jacsstuff.quizudo.utils.Utils;

import java.util.List;

public class GeneratorDetailActivity extends AppCompatActivity  implements ListActionExecutor, ConfirmDialog.OnFragmentInteractionListener{


    private Context context;
    private QuestionGeneratorDbManager dbManager;
    private ListAdapterHelper listAdapterHelper;
    private String selectedName;
    private String currentQuestionGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator_detail);
        currentQuestionGenerator = getIntent().getStringExtra(GeneratorsActivity.NAME_TAG);
        context = GeneratorDetailActivity.this;
        dbManager = new QuestionGeneratorDbManager(this);
        setupViews();
        setupToolbar();
    }


    private void setupViews(){
        ListView list = findViewById(R.id.list1);
        listAdapterHelper = new ListAdapterHelper(context, list , this);
        EditText editText = findViewById(R.id.nameEditText);
        listAdapterHelper.setupKeyInput( editText);
    }

    private void setupToolbar(){
        ToolbarBuilder.setupToolbarWithTitle(this, getActivityTitle());
        ActionBar actionBar = getActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    private String getActivityTitle(){
        String activityName = getResources().getString(R.string.question_generator_detail_activity_title);
        return activityName + " " + currentQuestionGenerator;
    }


    @Override
    protected  void onDestroy(){
        super.onDestroy();
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
        else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(String item){
        Intent intent = new Intent(context,  GeneratorQuestionSetActivity.class);
       //TODO: add intent extras intent.putExtra(AnswerPoolActivity.props.GeneratorQuestionSetActivity.toString(), item);
        startActivity(intent);
    }


    @Override
    public void onLongClick(String item){
        String message = context.getResources().getString(R.string.delete_generator_dialog_text, item);
        DialogLoader.loadDialogWith(getFragmentManager(), message);
        selectedName = item;
    }


    @Override
    public void onTextEntered(String text){
        if(text == null){
            return;
        }
        String questionSetName = text.trim();
        dbManager.addQuestionSet(currentQuestionGenerator, questionSetName);
        listAdapterHelper.addToList(questionSetName);
    }


    @Override
    protected  void onResume(){
        super.onResume();
        refreshListFromDb();
    }


    public void refreshListFromDb(){
        View noResultsFoundView = findViewById(R.id.noResultsFoundText);
        List<String> questionGeneratorNames = dbManager.retrieveQuestionSetNames(currentQuestionGenerator);
        listAdapterHelper.setupList(questionGeneratorNames, android.R.layout.simple_list_item_1, noResultsFoundView);
    }


    @Override
    public void onFragmentInteraction(boolean confirmed) {
        if (confirmed) {
            dbManager.removeQuestionSet(selectedName);
            refreshListFromDb();
        }
    }
}
