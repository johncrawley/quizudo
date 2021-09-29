package com.jacsstuff.quizudo.express.generators;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.jacsstuff.quizudo.R;
import com.jacsstuff.quizudo.express.generators.parser.FileParser;
import com.jacsstuff.quizudo.express.generators.parser.ParserResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class GeneratorFileReader {

    private final Context context;
    private final GeneratorsDbManager generatorsDbManager;

    public GeneratorFileReader(Context context){
        this.context = context;
        generatorsDbManager = new GeneratorsDbManager(context);
    }


    public void readFileAndSaveGenerator(Intent data){
        FileParser fileParser = new FileParser();
        Uri fileUri = data.getData();
        if(fileUri == null || fileUri.getPath() == null){
            return;
        }

        String importToastMessage;
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
                importToastMessage = context.getString(R.string.toast_message_generator_imported_success);
            }
            else{
                importToastMessage = parserResult.getMessage() + " (line "  + parserResult.getLine() + ")";
            }
            reader.close();
        }catch (IOException e){
            importToastMessage = context.getString(R.string.toast_message_generator_imported_fail);
        }

        Toast.makeText(context.getApplicationContext(), importToastMessage, Toast.LENGTH_LONG).show();
    }
}
