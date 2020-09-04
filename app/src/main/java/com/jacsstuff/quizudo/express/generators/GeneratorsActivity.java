package com.jacsstuff.quizudo.express.generators;

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
import com.jacsstuff.quizudo.express.generatorsdetail.GeneratorDetailActivity;
import com.jacsstuff.quizudo.dialog.ConfirmDialog;
import com.jacsstuff.quizudo.dialog.DialogLoader;
import com.jacsstuff.quizudo.list.ListActionExecutor;
import com.jacsstuff.quizudo.list.ListAdapterHelper;
import com.jacsstuff.quizudo.list.SimpleListItem;
import com.jacsstuff.quizudo.main.MainActivity;
import com.jacsstuff.quizudo.utils.ToolbarBuilder;
import com.jacsstuff.quizudo.utils.Utils;
import java.util.List;


public class GeneratorsActivity extends AppCompatActivity implements ListActionExecutor, ConfirmDialog.OnFragmentInteractionListener {

    private Context context;
    private GeneratorsDbManager dbManager;
    private ListAdapterHelper listAdapterHelper;
    private SimpleListItem selectedItem;
    public final static String NAME_TAG = "1";
    public final static String ID_TAG = "2";
    private GeneratorFileReader generatorFileReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generators);

        context = GeneratorsActivity.this;
        dbManager = new GeneratorsDbManager(this);
        ListView list = findViewById(R.id.list1);
        listAdapterHelper = new ListAdapterHelper(context, list, this);
        EditText generatorNameEditText = findViewById(R.id.generatorNameEditText);
        listAdapterHelper.setupKeyInput( generatorNameEditText, true, true);
        generatorFileReader = new GeneratorFileReader(context);
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
        getMenuInflater().inflate(R.menu.generator_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.home_menu_item:
                Utils.bringBackActivity(this, MainActivity.class);
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.import_generator:
                openFileLoader();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(SimpleListItem item){
        Intent intent = new Intent(context,  GeneratorDetailActivity.class);
        intent.putExtra(NAME_TAG, item.getName());
        intent.putExtra(ID_TAG, item.getId());
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
        String name = text.trim();
        long id = dbManager.addQuestionGenerator(name);
        listAdapterHelper.addToList(new SimpleListItem(name,id));
    }


    @Override
    protected  void onResume(){
        super.onResume();
        refreshListFromDb();
    }


    public void refreshListFromDb(){
        View noResultsFoundView = findViewById(R.id.noResultsFoundText);
        List<SimpleListItem> questionGeneratorNames = dbManager.retrieveGeneratorNames();
        listAdapterHelper.setupList(questionGeneratorNames, android.R.layout.simple_list_item_1, noResultsFoundView);
    }


    private final int SELECT_FILE_CODE = 1001;

    private void openFileLoader(){

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        //intent.putExtra("browseCoa", itemToBrowse);
        //Intent chooser = Intent.createChooser(intent, "Select a File to Upload");
        try {
            //startActivityForResult(chooser, FILE_SELECT_CODE);
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), SELECT_FILE_CODE);
        } catch (Exception ex) {
            System.out.println("browseClick :"+ex);//android.content.ActivityNotFoundException ex
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_FILE_CODE) {
               generatorFileReader.readFileAndSaveGenerator(data);
            }
        }
    }









    @Override
    public void onFragmentInteraction(boolean confirmed) {
        if (confirmed) {
            dbManager.removeQuestionGenerator(selectedItem);
            refreshListFromDb();
        }
    }
}
