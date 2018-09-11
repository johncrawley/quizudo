package com.jacsstuff.quizudo.answerPool;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.jacsstuff.quizudo.main.MainActivity;
import com.jacsstuff.quizudo.R;
import com.jacsstuff.quizudo.utils.ToolbarBuilder;
import com.jacsstuff.quizudo.utils.Utils;

import java.util.List;

public class AnswerListActivity extends AppCompatActivity implements AnswerListView{


    private Context context;
    private AnswerListController controller;
    private ArrayAdapter<String> arrayAdapter;
    private String answerPoolName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_list);
        answerPoolName = getIntent().getStringExtra(AnswerPoolActivity.props.SELECTED_ACTIVITY_POOL.toString());
        context = AnswerListActivity.this;
        controller = new AnswerListControllerImpl(this, context, answerPoolName);
        controller.refreshListFromDb();
        ToolbarBuilder.setupToolbarWithTitle(this, answerPoolName);
        setupKeyInput();
    }


    protected void onResume(){
        super.onResume();
        controller.refreshListFromDb();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_enabled_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.home_menu_item) {
            Utils.bringBackActivity(this, MainActivity.class);
        }
        return super.onOptionsItemSelected(item);
    }


    public void setupList(final List<String> items) {


        AdapterView.OnItemLongClickListener listener = new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String answer = items.get(position);
                String message = context.getResources().getString(R.string.remove_answer_dialog_text, answer);
                controller.showDialogWithText(message, answer, answerPoolName);
                return true;
            }
        };
        ListView list = findViewById(R.id.list1);
        arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, items);
        list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        list.setAdapter(arrayAdapter);
        list.setSelector(R.color.selectedListItemHidden);
        list.setEmptyView(findViewById(R.id.noResultsFoundText));
        list.setOnItemLongClickListener(listener);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    private EditText addAnswerPoolEditText;
    private void setupKeyInput() {
        addAnswerPoolEditText = findViewById(R.id.authorPoolEditText);
        addAnswerPoolEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.hideSoftInputFromWindow(addAnswerPoolEditText.getWindowToken(), 0);

                    controller.addAnswer(v.getText().toString());

                    addAnswerPoolEditText.getText().clear();
                    return true;
                }
                return false;
            }
        });
    }

    public void updateList(String text){
        arrayAdapter.add(text);
    }

}
