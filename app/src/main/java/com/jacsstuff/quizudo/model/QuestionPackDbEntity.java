package com.jacsstuff.quizudo.model;

import android.util.Log;

import com.jacsstuff.quizudo.model.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 07/01/2017.
 *
 *
 QuestionPAckDBDetail
 name
 author
 description
 date created - set by the server on QP creation
 date downloaded (to be added).. by the parser class?
 [[topics? no!!! only a question has a topic]]
 */
public class QuestionPackDbEntity {

    private String name;
    private String author;
    private String description;
    private String dateCreated;
    private List<Question> questions;
    private long dateDownloaded;
    private String uniqueName;
    private int version;

    public QuestionPackDbEntity(){
        this.name = "";
        this.author = "";
        this.description = "";
        this.dateCreated = "";
        this.dateDownloaded = 0;
        this.uniqueName = "";
        this.questions = new ArrayList<>();
        this.version = 1;
    }
    public QuestionPackDbEntity(String name, String author, String description, String dateCreated, long dateDownloaded, int numberOfQuestions, int version){

        this.name = name;
        this.author = author;
        this.description = description;
        this.dateCreated = dateCreated;
        this.dateDownloaded = dateDownloaded;
        this.questions = new ArrayList<>();
        setUniqueName(name, author);
        this.version = version;
    }

    public void addQuestion(Question question){
        this.questions.add(question);
    }
    public void addQuestions(List<Question> questions){
        if(questions == null){
            Log.i("QPDbDetail", "addQuestions() - questions  list to add is null");
            return;
        }
        if(this.questions == null){
            Log.i("QPDbDetail", "addQuestions() - existing questions list is null");
            return;
        }
        this.questions.addAll(questions);
    }

    public void setQuestions(List<Question> questions){
        this.questions.clear();
        addQuestions(questions);
    }

    public boolean hasQuestions(){
        return !this.questions.isEmpty();
    }

    public boolean hasNoQuestions(){
        return this.questions.isEmpty();
    }

    public int getNumberOfQuestions() {
        return this.questions.size();
    }

    public String getUniqueName(){return this.uniqueName;}

    public List<Question> getQuestions(){
        return new ArrayList<>(this.questions);
    }

    public long getDateDownloaded() {
        return dateDownloaded;
    }

    public void setDateDownloaded(long dateDownloaded) {
        this.dateDownloaded = dateDownloaded;
    }

    public String getName() {
        return name;
    }
    public int getVersion() {
        return version;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNameAndAuthor(String name, String author){
        this.name = name;
        this.author = author;
        setUniqueName(name, author);
    }

    private void setUniqueName(String name, String author){

        this.uniqueName = author + "_" + name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public int getDifficulty() {
        return 3;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }







}
