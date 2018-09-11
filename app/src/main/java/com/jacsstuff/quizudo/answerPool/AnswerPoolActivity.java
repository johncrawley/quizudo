package com.jacsstuff.quizudo.answerPool;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class AnswerPoolActivity extends AppCompatActivity implements AnswerPoolView{


    private AnswerPoolController controller;
    private Context context;
    private ArrayAdapter<String> arrayAdapter;
    public enum props { SELECTED_ACTIVITY_POOL}
    private EditText addAnswerPoolEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_pool);
        context = AnswerPoolActivity.this;

        ToolbarBuilder.setupToolbarWithTitle(this, getResources().getString(R.string.answer_pools_activity_title));
        controller = new AnswerPoolControllerImpl(this, context);
        setupKeyInput();
    }

    @Override
    protected  void onDestroy(){
        controller.closeConnections();
        super.onDestroy();
    }

    @Override
    protected  void onResume(){
        super.onResume();
        controller.refreshListFromDb();
    }


    public void setupList(final List<String> items){

        ListView list = findViewById(R.id.list1);
        if(list == null){
            Log.i("AnswerPoolActivity", "Couldn't find list id from layout.");
            return;
        }

        arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, items);

        AdapterView.OnItemLongClickListener longClickListener = new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String item = items.get(position);
                String message = context.getResources().getString(R.string.remove_answer_pool_dialog_text, item);
                controller.showDialogWithText(message, item);
                return true;
            }
        };

        AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(context,  AnswerListActivity.class);
                String answerPoolName = items.get(position);
                intent.putExtra(props.SELECTED_ACTIVITY_POOL.toString(), answerPoolName);
                startActivity(intent);
            }
        };

        list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        list.setAdapter(arrayAdapter);
        list.setSelector(R.color.selectedListItemHidden);
        list.setEmptyView(findViewById(R.id.noResultsFoundText));
        list.setOnItemLongClickListener(longClickListener);
        list.setOnItemClickListener(clickListener);
    }

    public void addToList(String name){
        arrayAdapter.add(name);
    }

    private void setupKeyInput() {
        addAnswerPoolEditText = findViewById(R.id.authorPoolEditText);
        addAnswerPoolEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm == null){
                        return false;
                    }
                    imm.hideSoftInputFromWindow(addAnswerPoolEditText.getWindowToken(), 0);
                    Log.i("AnswerPoolActivity", "content: " + v.getText().toString());
                    controller.addAnswerPool(v.getText().toString());
                    addAnswerPoolEditText.getText().clear();
                    return true;
                }
                return false;
            }
        });
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

    public void updateList(){

    }
}
