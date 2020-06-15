package com.jacsstuff.quizudo.db;

import android.provider.BaseColumns;

/**
 * Created by John on 31/12/2016.
 *
 * Contains the names and row names of the tables that hold data for the QuestionPack and Question entries.
 */
public final class DbContract {

    private DbContract(){}

    public static class QuestionsEntry implements BaseColumns {
        public static final String TABLE_NAME = "questions";
        public static final String COLUMN_NAME_QUESTION_TEXT = "question";
        public static final String COLUMN_NAME_CORRECT_ANSWER = "correct_answer";
        public static final String COLUMN_NAME_ANSWER_CHOICES = "answer_choices";
        public static final String COLUMN_NAME_TRIVIA = "trivia";
        public static final String COLUMN_NAME_QUESTION_PACK_ID = "question_pack_id";
        public static final String COLUMN_NAME_TOPICS = "topics";
        public static final String COLUMN_NAME_FLAGS = "flags";
        public static final String COLUMN_NAME_DIFFICULTY = "difficulty";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_ANSWER_POOL_NAME = "answer_pool_name";
        public static final String COLUMN_NAME_SOUND = "sound";
    }

    public static class QuestionPackEntry implements BaseColumns {
        public static final String TABLE_NAME = "question_packs";
        public static final String COLUMN_NAME_UNIQUE_NAME = "unique_name";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_AUTHOR = "author";
        public static final String COLUMN_NAME_VERSION = "version";
        public static final String COLUMN_NAME_DATE_CREATED = "date_created";
        public static final String COLUMN_NAME_DATE_DOWNLOADED = "date_downloaded";
        public static final String COLUMN_NAME_THUMBNAIL = "thumbnail";
        public static final String COLUMN_NAME_DIFFICULTY = "difficulty"; //i.e. the average difficulty
    }


    public static class AnswerPoolNamesEntry implements BaseColumns {

        public static final String TABLE_NAME = "answer_pool_names";
        public static final String COLUMN_NAME_APOOL_NAME = "name";
    }

    public static class AnswerPoolItemsEntry implements BaseColumns {

        public static final String TABLE_NAME = "answer_pool_items";
        public static final String COLUMN_NAME_ANSWER = "answer";
        public static final String COLUMN_NAME_APOOL_ID = "answer_pool_ID";
    }

}
