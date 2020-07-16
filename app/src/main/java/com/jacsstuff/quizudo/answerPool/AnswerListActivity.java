package com.jacsstuff.quizudo.answerPool;

import android.content.Context;
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

public class AnswerListActivity extends AppCompatActivity implements ListActionExecutor, ConfirmDialog.OnFragmentInteractionListener  {


    private Context context;
    private ListAdapterHelper listAdapterHelper;
    private String answerPoolName;
    private EditText addAnswerPoolEditText;
    private AnswerPoolDBManager db;
    private View noResultsFoundView;
    private String selectedAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_list);
        answerPoolName = getIntent().getStringExtra(AnswerPoolActivity.props.SELECTED_ACTIVITY_POOL.toString());
        context = AnswerListActivity.this;
        db = new AnswerPoolDBManager(context);
        setupList();
        ToolbarBuilder.setupToolbarWithTitle(this, answerPoolName);
        setupOtherViews();
    }

    private void setupList(){
        ListView list = findViewById(R.id.list1);
        listAdapterHelper = new ListAdapterHelper(context, list, this);
        listAdapterHelper.dismissKeyboardOnDone(false);
        refreshListFromDb();
    }

    private void setupOtherViews(){
            addAnswerPoolEditText = findViewById(R.id.answerPoolEditText);
            noResultsFoundView = findViewById(R.id.noResultsFoundText);
            listAdapterHelper.setupKeyInput(addAnswerPoolEditText);
    }

    protected void onResume(){
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
    public void onDestroy(){
        super.onDestroy();
    }


    @Override
    public void onClick(String item){
        // do nothing
    }


    @Override
    public void onLongClick(String answer){
        String message = context.getResources().getString(R.string.remove_answer_dialog_text, answer);
        selectedAnswer = answer;
        DialogLoader.loadDialogWith(getFragmentManager(), message);
    }


    @Override
    public void onTextEntered(String text){
        if(text == null){
            return;
        }
        addAnswerPoolEditText.getText().clear();
        String item = text.trim();
        db.addAnswerPoolItem(answerPoolName, item);
        listAdapterHelper.addToList(item);
    }


    public void refreshListFromDb(){
        List<String> items = db.getAnswerItems(answerPoolName);
        listAdapterHelper.setupList(items, android.R.layout.simple_list_item_1, noResultsFoundView);
    }


    @Override
    public void onFragmentInteraction(boolean confirmed) {
        if (confirmed) {
            if( db.removeAnswer(answerPoolName, selectedAnswer)){
                Toast.makeText(context, R.string.answer_pool_deleted_item_toast, Toast.LENGTH_SHORT).show();
                refreshListFromDb();
            }
        }
    }

}
