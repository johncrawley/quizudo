package com.jacsstuff.quizudo.download;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jacsstuff.quizudo.utils.LoadingDialog;
import com.jacsstuff.quizudo.main.MainActivity;
import com.jacsstuff.quizudo.R;
import com.jacsstuff.quizudo.utils.ToolbarBuilder;
import com.jacsstuff.quizudo.list.QuestionPackList;
import com.jacsstuff.quizudo.model.QuestionPackOverview;
import com.jacsstuff.quizudo.utils.Utils;

import java.util.List;

import static com.jacsstuff.quizudo.utils.Consts.DOWNLOAD_URL_PREFERENCE;
import static com.jacsstuff.quizudo.utils.Consts.QUIZ_SETTINGS_PREFERENCES;

public class DownloadQuestionsActivity extends AppCompatActivity implements DownloadQuestionsView{

    private TextView urlTextView;
    private QuestionPackList qpList;
    private DownloadQuestionsController controller;
    private Context context;
    private LoadingDialog loadingDialog;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_questions);
        context = DownloadQuestionsActivity.this;
        ToolbarBuilder.setupToolbarWithTitle(DownloadQuestionsActivity.this, getResources().getString(R.string.download_questions_activity_title));
        controller = new DownloadQuestionsControllerImpl(context, this);
        loadingDialog = new LoadingDialog(context, R.string.download_question_packs_loading_message);
        setupViews();
        setupList();
        ActivityCompat.requestPermissions(DownloadQuestionsActivity.this,
                new String[]{Manifest.permission.INTERNET},
                1);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                       String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                     // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Toast.makeText(DownloadQuestionsActivity.this, getResources().getString(R.string.download_no_permissions_granted_message), Toast.LENGTH_SHORT).show();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.download_questions_menu, menu);
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
        else if(id == R.id.refresh_menu_item){
            loadingDialog.show();
            controller.retrieveQuestionPackNames();
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupViews(){
        preferences = getSharedPreferences(QUIZ_SETTINGS_PREFERENCES, MODE_PRIVATE);

        urlTextView = findViewById(R.id.urlTextInputField);

        urlTextView.setText(preferences.getString(DOWNLOAD_URL_PREFERENCE, ""));

        urlTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm == null){
                        return false;
                    }
                    imm.hideSoftInputFromWindow(urlTextView.getWindowToken(), 0);
                    loadingDialog.show();
                    controller.retrieveQuestionPackNames();
                    saveUrlPreference(urlTextView.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    private void saveUrlPreference(String url){

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(DOWNLOAD_URL_PREFERENCE, url.trim());
        editor.apply();
    }


    public String getUrl(){
        return this.urlTextView.getText().toString();
    }


    private void setupList(){

        ListView listView = findViewById(R.id.list);
        Button downloadButton = findViewById(R.id.downloadButton);
        TextView noItemsFoundText = findViewById(R.id.noItemsFoundText);

        qpList = new QuestionPackList(context, this, listView, downloadButton, noItemsFoundText);
        qpList.initializeList();
        qpList.setButtonOnClick(new View.OnClickListener(){
            public void onClick(View view){
                loadingDialog.show();
              controller.retrieveQuestionPacks(urlTextView.getText().toString(), qpList.getSelectedNames());
            }
        });
    }

    public void dismissLoadingDialogAndToast(int id){
        loadingDialog.dismiss();
        Toast.makeText(context, getResources().getString(id), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void displayDownloadingDialog() {
        loadingDialog.show();
    }

    @Override
    public void displayQuestionPacksDownloadedMessage(int number) {
        loadingDialog.dismiss();
        String message;
        if(number == 1){
            message = getResources().getString(R.string.downloaded_question_pack_message);
        }
        else {
            message = getResources().getString(R.string.downloaded_question_packs_message, number);
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void displayServerNotFoundMessage() {
        dismissLoadingDialogAndToast(R.string.download_server_not_found_message);
    }

    @Override
    public void displayNotFoundMessage() {
        dismissLoadingDialogAndToast(R.string.download_resource_not_found_message);
    }

    @Override
    public void updateQuestionPackList(List<QuestionPackOverview> questionPackOverviews) {

       qpList.updateList(questionPackOverviews);
       loadingDialog.dismiss();
    }

    @Override
    public void displayServerErrorMessage() {

        dismissLoadingDialogAndToast(R.string.download_server_error_message);
    }

    @Override
    public void displayCatchAllErrorMessage() {
        dismissLoadingDialogAndToast(R.string.download_catch_all_error_message);
    }

}
