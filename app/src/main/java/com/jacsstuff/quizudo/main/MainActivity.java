package com.jacsstuff.quizudo.main;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jacsstuff.quizudo.R;
import com.jacsstuff.quizudo.configure.CreateQuizActivity;
import com.jacsstuff.quizudo.options.PreferencesActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

   // private InterstitialAd mInterstitialAd;

    boolean areAdsEnabled = false;
    private Button newQuizButton, optionsButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();
        setupViews();
        Log.i("Main", "Finished onCreate()");
    }


    private void setupViews(){
        newQuizButton = setupButton(R.id.newQuizButton);
        optionsButton = setupButton(R.id.optionsButton);
    }

    private Button setupButton(int id){
        Button button = findViewById(id);
        button.setOnClickListener(this);
        return button;
    }

    public void onClick(View view){

        int id = view.getId();
        if(id == newQuizButton.getId()){
            Intent intent = new Intent(this, CreateQuizActivity.class);
            startActivity(intent);
        }
        else if(id == optionsButton.getId()){

            Intent intent = new Intent(this, PreferencesActivity.class);
            startActivity(intent);
        }
    }

    private void setupBannerAd(){
        AdView ad = findViewById(R.id.adView);
        if(ad != null) {
            ad.setVisibility(View.VISIBLE);
            ad.loadAd(new AdRequest.Builder().build());
        }
    }


   // public void showAd(){
   //     mInterstitialAd.show();
   // }
    public boolean areAdsEnabled(){
        return this.areAdsEnabled;
    }


    private void setupToolbar(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.about_menu_item) {
            Intent intent = new Intent(this, AboutAppActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}
