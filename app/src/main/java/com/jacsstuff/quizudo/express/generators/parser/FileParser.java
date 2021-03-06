package com.jacsstuff.quizudo.express.generators.parser;

import com.jacsstuff.quizudo.express.generators.GeneratorEntity;
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
    private String resultMessage;

    public FileParser(){
        generatorEntity = new GeneratorEntity();
        setNames = new HashSet<>();
        lineCount = 0;
        resultMessage = "Some unknown error occurred";
    }


    public boolean parse(String line){
        if(finished){
            throw new ParserExhaustedException();
        }
        line = line.trim();
        lineCount++;
        boolean result = true;
        switch (expectedTag){
            case GENERATOR: result = parseGenerator(line); break;
            case QUESTION_SET: result = parseQuestionSet(line);break;
            case QUESTION: result = parseQuestion(line); break;
            case NONE: result = parseLine(line);
        }
        wasFileParsedSuccessfully = result;
        System.out.println("FileParser, was parsedSuccessfully: " + result);
        if(!wasFileParsedSuccessfully){
            finished = true;
        }
        return result;
    }


    public GeneratorEntity getGeneratorEntity(){
        if(!wasFileParsedSuccessfully){
            return null;
        }
        return this.generatorEntity;
    }


    public ParserResult finish(){
        finished = true;
        if( !wasFileParsedSuccessfully || questionSetEntity == null){
            return new BadParserResult(resultMessage, lineCount);
        }
        generatorEntity.addQuestionSetEntity(questionSetEntity);
        return new GoodParserResult();
    }


    private boolean parseGenerator(String line){
        if(isLineInvalid(GENERATOR_TAG, line)) {
            resultMessage = "Generator tag line is invalid";
            return false;
        }
        expectedTag = ExpectedTag.QUESTION_SET;
        String name = extractValueFrom(line, GENERATOR_TAG);
        generatorEntity.setGeneratorName(name);
        return true;
    }


    private boolean parseQuestion(String line){
        if(isLineInvalid(QUESTION_TAG, line)){
            resultMessage = "The Question Tag is invalid";
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
        if(line.startsWith(GENERATOR_TAG)) {
            resultMessage = "Only one generator tag should be present, and at the top of the file";
            return false;
        }
        if(line.startsWith(QUESTION_TAG)){
            resultMessage = "The question tag should only come after a set tag";
            return false;
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
        if(questionSubject.trim().isEmpty() || answer.trim().isEmpty()){
            return;
        }
        questionSetEntity.add(new ChunkEntity(questionSubject, answer,-1));
    }


    private boolean parseQuestionSet(String line){
        saveExistingQuestionSet();
        if (isLineInvalid(QUESTION_SET_TAG, line)) {
            resultMessage = "The set tag is missing or improperly formed";
            return false;
        }
        String questionSetName = extractValueFrom(line, QUESTION_SET_TAG);
        questionSetEntity = new QuestionSetEntity();
        questionSetEntity.setName(questionSetName);
        if(setNames.contains(questionSetName)){
            System.out.println("Duplicate set name! " + questionSetName);
            resultMessage = "Duplicate set names are not allowed";
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
