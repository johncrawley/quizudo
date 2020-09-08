package com.jacsstuff.quizudo.express.generators;

import android.util.Log;

import com.jacsstuff.quizudo.express.questionset.ChunkEntity;
import com.jacsstuff.quizudo.express.questionset.QuestionSetEntity;

import java.util.HashSet;
import java.util.Set;

public class FileParser {

    private enum ExpectedTag { QUESTION_SET, QUESTION, GENERATOR, NONE }
    private final String GENERATOR_TAG = "**Generator:";
    private final String QUESTION_SET_TAG = "**Set:";
    private final String QUESTION_TAG = "**Question:";
    private final String CHUNK_LINE_DELIMITER = ",,";
    private GeneratorEntity generatorEntity;
    private QuestionSetEntity questionSetEntity;
    private ExpectedTag expectedTag = ExpectedTag.GENERATOR;
    private boolean wasFileParsedSuccessfully = true;
    private boolean finished = false;
    private Set<String> setNames;
    private int lineCount = 0;

    public FileParser(){
        generatorEntity = new GeneratorEntity();
        setNames = new HashSet<>();
        lineCount = 0;
    }





    public boolean parse(String line){
        if(finished){
            throw new ParserExhaustedException();
        }
        line = line.trim();
        lineCount++;
        log("line: " + lineCount + "  " + line);
        boolean result = true;
        switch (expectedTag){
            case GENERATOR: result = parseGenerator(line); break;
            case QUESTION_SET: result = parseQuestionSet(line);break;
            case QUESTION: result = parseQuestion(line); break;
            case NONE: result = parseLine(line);
        }
        wasFileParsedSuccessfully = result;
        if(!result){
            System.out.println("FileParser fail, lineCount: " + lineCount);
        }
        return result;
    }


    public GeneratorEntity getGeneratorEntity(){
        if(!wasFileParsedSuccessfully){
            return null;
        }
        return this.generatorEntity;
    }

    public boolean finish(){
        finished = true;
        if(questionSetEntity != null){
            generatorEntity.addQuestionSetEntity(questionSetEntity);
            return wasFileParsedSuccessfully;
        }
        return false;
    }


    private boolean parseGenerator(String line){
        if(isLineInvalid(GENERATOR_TAG, line)) {
            log("failing because line is invalid : " + line);
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
        if(line.startsWith(GENERATOR_TAG) || line.startsWith(QUESTION_TAG)){
            log("failing because line starts with generator tag or question tag ("  + lineCount + ")");
            wasFileParsedSuccessfully = false;
            return false;
        }
        parseChunk(line);
        return true;
    }

    private void log(String msg){
        System.out.println("FileParser: " + msg);
    }

    private void parseChunk(String line){
        String[] items = line.split(CHUNK_LINE_DELIMITER);
        if(items.length < 2){
            return;
        }
        String questionSubject = items[0];
        String answer = items[1];
        if(questionSubject.trim().isEmpty() || answer.trim().isEmpty()){
            return;
        }
        questionSetEntity.add(new ChunkEntity(questionSubject, answer,-1));
    }


    private boolean parseQuestionSet(String line){
        saveExistingQuestionSet();
        if (isLineInvalid(QUESTION_SET_TAG, line)) {
            log("failing because question set tag line is invalid");
            return false;
        }
        String questionSetName = extractValueFrom(line, QUESTION_SET_TAG);
        questionSetEntity = new QuestionSetEntity();
        questionSetEntity.setName(questionSetName);
        if(setNames.contains(questionSetName)){
            return false;
        }
        setNames.add(questionSetName);
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
