package com.jacsstuff.quizudo.express.questionset;

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
import com.jacsstuff.quizudo.dialog.ConfirmDialog;
import com.jacsstuff.quizudo.dialog.DialogLoader;
import com.jacsstuff.quizudo.express.generatorsdetail.GeneratorDetailActivity;
import com.jacsstuff.quizudo.list.ListActionExecutor;
import com.jacsstuff.quizudo.list.ListAdapterHelper;
import com.jacsstuff.quizudo.list.SimpleListItem;
import com.jacsstuff.quizudo.main.MainActivity;
import com.jacsstuff.quizudo.utils.ToolbarBuilder;
import com.jacsstuff.quizudo.utils.Utils;

import java.util.List;

import static com.jacsstuff.quizudo.express.generatorsdetail.GeneratorDetailActivity.INTENT_KEY_QUESTION_SET_NAME;

public class GeneratorQuestionSetActivity extends AppCompatActivity   implements ListActionExecutor, ConfirmDialog.OnFragmentInteractionListener {

    private String questionSetName;
    private long questionSetId;
    private Context context;
    private ListAdapterHelper listAdapterHelper;
    private QuestionSetDbManager dbManager;
    private SimpleListItem currentItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator_question_set);
        context = GeneratorQuestionSetActivity.this;
        dbManager = new QuestionSetDbManager(context);
        getIntentData();
        setupList();
        restoreQuestionTemplateText();
        ToolbarBuilder.setupToolbarWithTitle(this, getString(R.string.question_set_toolbar_title) + questionSetName);
    }

    private void setupList(){
        ListView list = findViewById(R.id.list1);
       // NB. adding a header view to a listView seems to mess up the clicked item order, so don't!
        listAdapterHelper = new ListAdapterHelper(context, list, this);
        EditText chunkEditText = findViewById(R.id.chunkEditText);
        listAdapterHelper.setupKeyInput( chunkEditText, true, false);
        EditText questionTemplateEditText = findViewById(R.id.questionTemplateEditText);
        listAdapterHelper.setupKeyInput( questionTemplateEditText, false, true);

        questionTemplateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(!hasFocus){
                    EditText editText = (EditText)v;
                    updateQuestionTemplate(questionSetId,  editText.getText().toString());
                }
            }
        });
    }


    private void restoreQuestionTemplateText(){
        EditText questionTemplateEditText = findViewById(R.id.questionTemplateEditText);
        questionTemplateEditText.setText(dbManager.getQuestionTemplateText(questionSetId));
    }


    private void getIntentData(){
        Intent intent = getIntent();
        questionSetName = intent.getStringExtra(INTENT_KEY_QUESTION_SET_NAME);
        questionSetId = intent.getLongExtra(GeneratorDetailActivity.INTENT_KEY_QUESTION_SET_ID,-1);
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
        else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void refreshListFromDb(){
        View noResultsFoundView = findViewById(R.id.noResultsFoundText);
        List<SimpleListItem> items = dbManager.retrieveChunkListItemsFor(questionSetId);
        listAdapterHelper.setupList(items, android.R.layout.simple_list_item_1, noResultsFoundView);
    }


    @Override
    public void onClick(SimpleListItem item){
    }


    @Override
    public void onLongClick(SimpleListItem item){

        String message = context.getResources().getString(R.string.remove_chunk_dialog_text, item.getName());
        DialogLoader.loadDialogWith(getFragmentManager(), message);
        currentItem = item;
    }


    @Override
    public void onTextEntered(int viewId, String text){
        if(text == null){
            return;
        }
        text = text.trim();

        switch(viewId){
            case R.id.questionTemplateEditText:
                updateQuestionTemplate(questionSetId, text);
                break;
            case R.id. chunkEditText:
               addChunk(text);
        }
    }


    private void updateQuestionTemplate(long questionSetId, String text){
        dbManager.updateQuestionTemplate(questionSetId, text);
    }


    @Override
    public void onFragmentInteraction(boolean confirmed) {
        if (confirmed) {
            dbManager.remove(currentItem);
           refreshListFromDb();
        }
    }


    private void addChunk(String text){
        if (listAdapterHelper.contains(text)) {
            return;
        }
        long chunkId = dbManager.addChunk(questionSetId, text);
        listAdapterHelper.addToList(new SimpleListItem(text, chunkId));
    }

}
