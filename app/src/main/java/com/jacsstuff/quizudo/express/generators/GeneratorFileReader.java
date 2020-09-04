package com.jacsstuff.quizudo.express.generators;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class GeneratorFileReader {

    private Context context;
    private FileParser fileParser;
    private GeneratorsDbManager generatorsDbManager;

    public GeneratorFileReader(Context context){
        this.context = context;
        fileParser = new FileParser();
        generatorsDbManager = new GeneratorsDbManager(context);
    }


    public void readFileAndSaveGenerator(Intent data){
        Uri fileUri = data.getData();
        if(fileUri == null || fileUri.getPath() == null){
            return;
        }

        String importToastMessage = "Couldn't import generator, please check the format!";
        try{
            InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
            if(inputStream == null){
                return;
            }
            Reader reader = new InputStreamReader(inputStream);
            BufferedReader buf = new BufferedReader(reader);

            String line = buf.readLine();
            while(line != null){
                fileParser.parse(line);
                line = buf.readLine();
            }
            if(fileParser.finish()){
             generatorsDbManager.save(fileParser.getGeneratorEntity());
             importToastMessage = "generator imported!";
            }
            reader.close();
        }catch (IOException e){
            importToastMessage = "There was a problem loading the file, import failed.";
        }

        Toast.makeText(context.getApplicationContext(), importToastMessage, Toast.LENGTH_SHORT).show();
    }
}
