package com.jacsstuff.quizudo.express.generatorsdetail;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.jacsstuff.quizudo.R;
import com.jacsstuff.quizudo.express.generators.GeneratorsActivity;
import com.jacsstuff.quizudo.dialog.ConfirmDialog;
import com.jacsstuff.quizudo.dialog.DialogLoader;
import com.jacsstuff.quizudo.express.questionset.GeneratorQuestionSetActivity;
import com.jacsstuff.quizudo.list.ListActionExecutor;
import com.jacsstuff.quizudo.list.ListAdapterHelper;
import com.jacsstuff.quizudo.list.SimpleListItem;
import com.jacsstuff.quizudo.main.MainActivity;
import com.jacsstuff.quizudo.utils.ToolbarBuilder;
import com.jacsstuff.quizudo.utils.Utils;

import java.util.List;

public class GeneratorDetailActivity extends AppCompatActivity  implements ListActionExecutor, ConfirmDialog.OnFragmentInteractionListener{


    private Context context;
    private QuestionGeneratorDbManager dbManager;
    private ListAdapterHelper listAdapterHelper;
    private SimpleListItem selectedItem;
    private String currentGeneratorName;
    private long currentGeneratorId;
    public static final String INTENT_KEY_QUESTION_SET_NAME = "questionSetName";
    public static final String INTENT_KEY_QUESTION_SET_ID = "questionSetId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator_detail);
        setupIntentVars();
        context = GeneratorDetailActivity.this;
        dbManager = new QuestionGeneratorDbManager(this);
        setupViews();
        setupToolbar();
    }

    private void setupIntentVars(){
        Intent intent = getIntent();
        currentGeneratorName = intent.getStringExtra(GeneratorsActivity.NAME_TAG);
        currentGeneratorId = intent.getLongExtra(GeneratorsActivity.ID_TAG, -1);
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
        return activityName + " " + currentGeneratorName;
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
    public void onClick(SimpleListItem item){
        Intent intent = new Intent(context,  GeneratorQuestionSetActivity.class);
        intent.putExtra(INTENT_KEY_QUESTION_SET_NAME, item.getName());
        Log.i("quizz", "onClick()  item name: " + item.getName() + " item id:  " + item.getId());
        intent.putExtra(INTENT_KEY_QUESTION_SET_ID, item.getId());
        startActivity(intent);
    }


    @Override
    public void onLongClick(SimpleListItem item){
        selectedItem = item;
        Log.i("quizz", "GenDetail onLongClick - item name: " + item.getName() + " id: " + item.getId());
        String message = context.getResources().getString(R.string.delete_generator_dialog_text, selectedItem.getName());
        DialogLoader.loadDialogWith(getFragmentManager(), message);
    }


    @Override
    public void onTextEntered(int viewId, String text){
        if(text == null){
            return;
        }
        String questionSetName = text.trim();
        if(listAdapterHelper.contains(questionSetName)){
            return;
        }
        long questionSetId = dbManager.addQuestionSet(currentGeneratorId, questionSetName);
        Log.i("quizz", "onTextEntered() added question set: " + questionSetName + ", "  + questionSetId + "  to generator with id: " + currentGeneratorId);
        listAdapterHelper.addToList(new SimpleListItem(questionSetName, questionSetId));
    }


    @Override
    protected  void onResume(){
        super.onResume();
        refreshListFromDb();
    }


    public void refreshListFromDb(){
        View noResultsFoundView = findViewById(R.id.noResultsFoundText);
        List<SimpleListItem> questionSets = dbManager.retrieveQuestionSets(currentGeneratorId);
        listAdapterHelper.setupList(questionSets, android.R.layout.simple_list_item_1, noResultsFoundView);
    }


    @Override
    public void onFragmentInteraction(boolean confirmed) {
        if (confirmed) {
            dbManager.removeQuestionSet(selectedItem);
            refreshListFromDb();
        }
    }
}
