package com.jacsstuff.quizudo.express.generators;

import com.jacsstuff.quizudo.express.questionset.ChunkEntity;
import com.jacsstuff.quizudo.express.questionset.QuestionSetEntity;

public class FileParser {

    private enum ExpectedTag { QUESTION_SET, QUESTION, GENERATOR, NONE }
    private final String GENERATOR_TAG = "**Generator:";
    private final String QUESTION_SET_TAG = "**Set:";
    private final String QUESTION_TAG = "**Question:";
    private final String CHUNK_LINE_DELIMITER = ",,";
    private GeneratorEntity generatorEntity;
    private QuestionSetEntity questionSetEntity;
    private ExpectedTag expectedTag = ExpectedTag.GENERATOR;


    public FileParser(){
        generatorEntity = new GeneratorEntity();
    }


    public boolean parse(String line){

        line = line.trim();
        switch (expectedTag){
            case GENERATOR: return parseGenerator(line);
            case QUESTION_SET: return parseQuestionSet(line);
            case QUESTION: return parseQuestion(line);
            case NONE: return parseLine(line);
        }
        return true;
    }

    public GeneratorEntity getGeneratorEntity(){
        return this.generatorEntity;
    }

    public void finish(){
        if(questionSetEntity != null){
            generatorEntity.addQuestionSetEntity(questionSetEntity);
        }
    }


    private boolean parseGenerator(String line){
        if(isLineInvalid(GENERATOR_TAG,line)) {
            return false;
        }
        expectedTag = ExpectedTag.QUESTION_SET;
        String name = extractValueFrom(line, GENERATOR_TAG);
        generatorEntity.setGeneratorName(name);
        return true;
    }


    private boolean parseQuestion(String line){
        if(isLineInvalid(QUESTION_TAG, line)){
            return false;
        }
        String questionTemplate = extractValueFrom(line, QUESTION_TAG);
        questionSetEntity.setQuestionTemplate(questionTemplate);
        expectedTag = ExpectedTag.NONE;
        return true;
    }


    private boolean parseLine(String line){
        if(line.startsWith(QUESTION_SET_TAG)){
            return parseQuestionSet(line);
        }
        parseChunk(line);
        return true;
    }


    private void parseChunk(String line){
        String[] items = line.split(CHUNK_LINE_DELIMITER);
        if(items.length < 2){
            return;
        }
        String questionSubject = items[0];
        String answer = items[1];
        questionSetEntity.add(new ChunkEntity(questionSubject, answer,-1));
    }


    private boolean parseQuestionSet(String line){
        saveExistingQuestionSet();
        if (isLineInvalid(QUESTION_SET_TAG, line)) {
            return false;
        }
        String questionSetName = extractValueFrom(line, QUESTION_SET_TAG);
        questionSetEntity = new QuestionSetEntity();
        questionSetEntity.setName(questionSetName);
        expectedTag = ExpectedTag.QUESTION;
        return true;
    }


    private boolean isLineInvalid(String tag, String line){
        return !line.startsWith(tag) || line.equals(GENERATOR_TAG);
    }


    private void saveExistingQuestionSet(){
        if(questionSetEntity == null){
            return;
        }
        generatorEntity.addQuestionSetEntity(questionSetEntity);
        questionSetEntity = null;
    }


    private String extractValueFrom(String line, String tag){
        int nameStartIndex = tag.length();
        return line.substring(nameStartIndex).trim();
    }

}
