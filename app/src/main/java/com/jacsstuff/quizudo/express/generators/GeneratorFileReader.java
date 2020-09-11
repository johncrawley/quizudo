package com.jacsstuff.quizudo.express.generators;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.jacsstuff.quizudo.express.generators.parser.FileParser;
import com.jacsstuff.quizudo.express.generators.parser.ParserResult;

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
        generatorsDbManager = new GeneratorsDbManager(context);
    }


    public void readFileAndSaveGenerator(Intent data){
        fileParser = new FileParser();
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
            boolean wasLineCorrect = true;
            while(line != null && wasLineCorrect){
                wasLineCorrect = fileParser.parse(line);
                line = buf.readLine();
            }
            ParserResult parserResult = fileParser.finish();
            if(parserResult.isSuccess()){
                generatorsDbManager.save(fileParser.getGeneratorEntity());
                importToastMessage = "generator imported!";
            }
            else{
                importToastMessage = parserResult.getMessage() + " (line "  + parserResult.getLine() + ")";
            }
            reader.close();
        }catch (IOException e){
            importToastMessage = "There was a problem loading the file, import failed.";
        }

        Toast.makeText(context.getApplicationContext(), importToastMessage, Toast.LENGTH_LONG).show();
    }
}
