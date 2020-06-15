package com.jacsstuff.quizudo.db;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.jacsstuff.quizudo.model.QuestionPackDbEntity;
import com.jacsstuff.quizudo.utils.JSONParser;
import com.jacsstuff.quizudo.model.Question;
import com.jacsstuff.quizudo.model.QuestionPackOverview;
import com.jacsstuff.quizudo.model.QuestionPackFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.jacsstuff.quizudo.utils.Consts.AUTHOR_DELIMITER;
import static com.jacsstuff.quizudo.utils.Consts.FILENAME_EXTENSION;
/**
 * Created by John on 08/12/2016.
 *
 *  The aim is to supply the name of the author alongside the quiz pack when it is displayed in a list
 *  Without a database, the author will have to be added to the stored filename. This also has the benefit of
 *  being able to store unique files with the same original quiz pack names but from different authors.
 *
 *  When the filename is being displayed, it will have to be modified to look more like a list entry, rather than a
 *  file.
 */
// TODO: revisit if we ever want to load questions from the filesystem
public class QuestionPackFileManager{//} implements QuestionPackManager {

    private Context context;
    private Map<Integer, QuestionPackFile> questionPacks;

    public QuestionPackFileManager(Context context){
        int keyCount = 0;
        this.context = context;
        questionPacks = new HashMap<>();
        for(File file: getStoredQuizFiles(context)){
            questionPacks.put(keyCount, new QuestionPackFile(file));
            keyCount++;
        }
    }


    public boolean isEmpty(){
        return this.questionPacks.isEmpty();
    }

    public List<QuestionPackOverview> getQuestionPackDetails(){
        List<QuestionPackOverview> qpDetails = new ArrayList<>();
        for(int key: questionPacks.keySet()){
            qpDetails.add(questionPacks.get(key).getQuestionPackOverview());
        }
        return qpDetails;
    }

    public int saveQuestionPacks(Map<String, String> dataChunks, String authorName){
        int downloadCount = 0;
        for(String key: dataChunks.keySet()){
            String filename = createFilename(key, authorName);
            String data = dataChunks.get(key);
            boolean wasDownloadSuccessful =  writeToFile(filename, data.trim());
            if(wasDownloadSuccessful){
                downloadCount++;
                Log.i("saveQpMethod", "downloadCount incremented, is now: " + downloadCount);
            }
        }
        return downloadCount;
    }

    public List<QuestionPackDbEntity> getQuestionPacks(Set<Integer> ids){
        List<QuestionPackDbEntity> questionPackList = new ArrayList<>();
        JSONParser parser = new JSONParser();

        for(int id: ids){
            QuestionPackFile qpFile = questionPacks.get(id);
            File file = qpFile.getFile();
            String path = file.getAbsolutePath();
            QuestionPackDbEntity qp = parser.parseFile(path);
            questionPackList.add(qp);
        }

        return questionPackList;
    }
    public List<Question> getQuestions(Set<Integer> ids){
        List<Question> questionsList = new ArrayList<>();
        JSONParser parser = new JSONParser();

        for(int id: ids){
            QuestionPackFile qpFile = questionPacks.get(id);
            File file = qpFile.getFile();
            String path = file.getAbsolutePath();
            QuestionPackDbEntity qp = parser.parseFile(path);
            questionsList.addAll(qp.getQuestions());
        }

        return questionsList;
    }

    //@Override
    public List<Integer> getQuestionIds(Set<Integer> ids) {
        return null;
    }


    public int deleteQuestionPacks(Set<Integer> ids){
        int deletedCount = 0;
        for(int id: ids){

            QuestionPackFile qpFile = questionPacks.get(id);
            if(qpFile == null){
                continue;
            }
            if(qpFile.delete()){
                deletedCount++;
                questionPacks.remove(id);
            }
        }

        return deletedCount;
    }


    private String createFilename(String originalFilename, String author){

        return author + AUTHOR_DELIMITER + originalFilename + FILENAME_EXTENSION;
    }


    private List<File> getStoredQuizFiles(Context context){

        String externalPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File externalDir = new File(externalPath);
        File internalDir = context.getFilesDir();
        List <File> files = new ArrayList<>();
        files.addAll(getFilesFromStorage(externalDir));
        files.addAll(getFilesFromStorage(internalDir));

        return files;
    }


    private static boolean isQuizFile(File file){
        return file.getName().endsWith(FILENAME_EXTENSION);
    }


    private List<File> getFilesFromStorage(File directory){
        File files[] = directory.listFiles();
        if (files == null) {
            return new ArrayList<>();
        }
        List<File> fileList = new ArrayList<>();

        for(File file : files){
            if(isQuizFile(file)){
                fileList.add(file);
            }
        }
        return fileList;
    }

    private boolean writeToFile(String filename, String data){

        boolean wasDownloadSuccessful = false;
        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            outputStream.close();
            wasDownloadSuccessful = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wasDownloadSuccessful;
    }

    public void closeConnections(){
        //only really applicable to DB manager.
    }

}
