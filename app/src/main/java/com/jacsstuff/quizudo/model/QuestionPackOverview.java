package com.jacsstuff.quizudo.model;

/**
 * Created by John on 12/12/2016.
 *
 * Contains the name and author of a question pack.
 * Also contains a reference to the key that holds the parent questionPackFile in the QuestionPackManager.
 *
 * Very similar to the QuestionPackDbEntity but doesn't contain references to any representation of actual questions.
 *
 *
 */
public class QuestionPackOverview {

    private String name;
    private String author;
    private long dateDownloaded;
    private String uniqueName;
    private String description;
    private int numberOfQuestions;
    private String dateCreated;
    private long id; // holds the db row id - [update 2018[, more importantly, it holds the id tag for the list item.

    private int version;

    public QuestionPackOverview(){}

    public QuestionPackOverview(String name, String author, int numberOfQuestions){
        this.name = name;
        this.author = author;
        this.numberOfQuestions = numberOfQuestions;
    }

    public QuestionPackOverview(String name, String author, int numberOfQuestions, int id){
        this(name,author,numberOfQuestions);
        this.id = id;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getDateDownloaded() {
        return dateDownloaded;
    }

    public void setDateDownloaded(long dateDownloaded) {
        this.dateDownloaded = dateDownloaded;
    }


    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String getDescription() {
        return description;
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

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }
}
