package com.jacsstuff.quizudo.options;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jacsstuff.quizudo.main.MainActivity;
import com.jacsstuff.quizudo.utils.ToolbarBuilder;
import com.jacsstuff.quizudo.db.DBWriter;
import com.jacsstuff.quizudo.list.QuestionPackList;
import com.jacsstuff.quizudo.R;
import com.jacsstuff.quizudo.utils.Utils;

public class RemoveQuestionPacksActivity extends AppCompatActivity{

    private Context context;
    private QuestionPackList questionPackList;
    private DBWriter dbWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_question_packs);
        context = RemoveQuestionPacksActivity.this;
        ToolbarBuilder.setupToolbar(this);
        setupViews();
        dbWriter = new DBWriter(context);
        initializeList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_enabled_menu, menu);
        return true;
    }


    @Override
    protected void onDestroy(){
        dbWriter.closeConnection();
        super.onDestroy();
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

    private void setupViews() {
        TextView textView = findViewById(R.id.no_question_packs_found_text);
        ListView listView = findViewById(R.id.listView);
        Button removeButton = findViewById(R.id.delete_question_packs_button);
        questionPackList = new QuestionPackList(context, this, listView, removeButton, textView);

        questionPackList.setButtonOnClick(new View.OnClickListener(){
            public void onClick(View view){
                new QuizFileDeleter().execute("");
            }
        });
    }

    private void initializeList(){
        questionPackList.initializeList(dbWriter.getQuestionPackDetails());
    }

    private class QuizFileDeleter extends AsyncTask<String, String, Integer> {

        String deleteMessage = "";

        public Integer doInBackground(String... params){

            int deletedFilesCount = dbWriter.deleteQuestionPacks(questionPackList.getSelectedIds());
            //Log.i("RemoveQPActivity", "doInBackground: about to deleteQuestionPacks);
              //      for(String id: questionPackList.ge)
            if(deletedFilesCount > 1){
                deleteMessage = getResources().getString(R.string.files_deleted, deletedFilesCount);
            }
            else{
                deleteMessage = getResources().getString(R.string.file_deleted);
            }
            return 1;
        }

        public void onPostExecute(Integer value){
            initializeList();
            questionPackList.disableButton();
            Toast.makeText(context, deleteMessage, Toast.LENGTH_SHORT).show();
        }
    }

}

