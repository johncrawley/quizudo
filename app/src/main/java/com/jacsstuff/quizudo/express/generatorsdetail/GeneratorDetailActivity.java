package com.jacsstuff.quizudo.express.generatorsdetail;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class GeneratorDetailActivity extends AppCompatActivity
        implements ListActionExecutor,
                    ConfirmDialog.OnFragmentInteractionListener,
                    View.OnClickListener{


    private Context context;
    private QuestionGeneratorDbManager dbManager;
    private ListAdapterHelper listAdapterHelper;
    private SimpleListItem selectedItem;
    private String currentGeneratorName;
    private long currentGeneratorId;
    public static final String INTENT_KEY_QUESTION_SET_NAME = "questionSetName";
    public static final String INTENT_KEY_QUESTION_SET_ID = "questionSetId";
    private Button generateButton;
    private String questionPackName;
    private QuestionGenerator questionGenerator;
    private List<SimpleListItem> questionSets;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator_detail);
        setupIntentVars();
        context = GeneratorDetailActivity.this;
        dbManager = new QuestionGeneratorDbManager(context);
        questionGenerator = new QuestionGenerator(context);
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
        EditText questionSetNameEditText = findViewById(R.id.generatorNameEditText);
        listAdapterHelper.setupKeyInput( questionSetNameEditText, true, true );
        EditText questionPackNameEditText = findViewById(R.id.questionPackNameEditText);
        listAdapterHelper.setupKeyInput( questionPackNameEditText, false );
        generateButton = findViewById(R.id.generateQuestionsButton);
        generateButton.setOnClickListener(this);
        setExistingQuestionPackName(questionPackNameEditText);

    }

    private void setExistingQuestionPackName(EditText editText){
        String existingQuestionPackName = dbManager.getQuestionSetName(currentGeneratorId);
        editText.setText(existingQuestionPackName);
        questionPackName = existingQuestionPackName;
    }


    private void setupToolbar(){
        ToolbarBuilder.setupToolbarWithTitle(this, getActivityTitle());
        ActionBar actionBar = getActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void onClick(View view){
        if(view.getId() == R.id.generateQuestionsButton){
            SimpleListItem currentGenerator = new SimpleListItem(currentGeneratorName, currentGeneratorId);
            questionGenerator.addQuestionsTo(questionPackName, currentGenerator, questionSets);
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
        intent.putExtra(INTENT_KEY_QUESTION_SET_ID, item.getId());
        startActivity(intent);
    }


    @Override
    public void onLongClick(SimpleListItem item){
        selectedItem = item;
        String message = context.getResources().getString(R.string.delete_generator_dialog_text, selectedItem.getName());
        DialogLoader.loadDialogWith(getFragmentManager(), message);
    }


    @Override
    public void onTextEntered(int viewId, String text){
        if(text == null){
            return;
        }
        text = text.trim();
        if(viewId == R.id.generatorNameEditText) {
            createQuestionSet(text);
        }
        else if(viewId == R.id.questionPackNameEditText){
            setAndSaveQuestionPackName(text);
            setGenerateButtonState(text);
        }
    }


    private void setAndSaveQuestionPackName(String text){

        this.questionPackName = text;
        dbManager.updateQuestionPackName(currentGeneratorId, text);
    }

    private void setGenerateButtonState(String text){
        if(text.isEmpty()){
            generateButton.setEnabled(false);
            return;
        }
        generateButton.setEnabled(true);
    }


    private void createQuestionSet(String questionSetName){
        questionSetName = questionSetName.trim();
        if (listAdapterHelper.contains(questionSetName)) {
            return;
        }
        long questionSetId = dbManager.addQuestionSet(currentGeneratorId, questionSetName);
        listAdapterHelper.addToList(new SimpleListItem(questionSetName, questionSetId));
    }


    @Override
    protected  void onResume(){
        super.onResume();
        refreshListFromDb();
    }


    public void refreshListFromDb(){
        View noResultsFoundView = findViewById(R.id.noResultsFoundText);
        questionSets = dbManager.retrieveQuestionSets(currentGeneratorId);
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
