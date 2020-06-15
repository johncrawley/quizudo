package com.jacsstuff.quizudo.model;

import java.io.File;

import static com.jacsstuff.quizudo.utils.Consts.FILENAME_EXTENSION;
import static com.jacsstuff.quizudo.utils.Consts.AUTHOR_DELIMITER;

/**
 * Created by John on 12/12/2016.
 */
public class QuestionPackFile {

    private File file;
    private QuestionPackOverview questionPackOverview;
    private boolean isSelected = false;

    public QuestionPackFile(File file) {

        this.file = file;
        String filename = file.getName();
        String author = getAuthor(filename);
        String displayName= getDisplayName(filename);
        questionPackOverview = new QuestionPackOverview(displayName, author, 0);

    }

    // filename is in the format author,delimiter,displayname,extension
    private String getDisplayName(String filename) {
        int delimiterIndex = filename.indexOf(AUTHOR_DELIMITER) + AUTHOR_DELIMITER.length();
        int extensionIndex = filename.indexOf(FILENAME_EXTENSION);
        boolean areIndexesWithinBounds = delimiterIndex != -1 && extensionIndex != -1 && delimiterIndex < extensionIndex;
        if (areIndexesWithinBounds) {
            return filename.substring(delimiterIndex, extensionIndex);
        }
        return filename;
    }

    public boolean delete(){

        return file.delete();
    }

    private String getAuthor(String filename) {
        int delimiterIndex = filename.indexOf(AUTHOR_DELIMITER);
        if (delimiterIndex != -1) {
            return filename.substring(0, delimiterIndex);
        }
        return "";
    }

    public QuestionPackOverview getQuestionPackOverview(){
        return this.questionPackOverview;
    }

    public void setSelected(boolean isSelected){
        this.isSelected = isSelected;
    }

    public boolean isSelected(){
        return this.isSelected;
    }

    public File getFile(){
        return this.file;
    }
}
