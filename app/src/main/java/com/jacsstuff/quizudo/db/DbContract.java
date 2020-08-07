package com.jacsstuff.quizudo.db;

import android.provider.BaseColumns;

/**
 * Created by John on 31/12/2016.
 *
 * Contains the names and row names of the tables that hold data for the QuestionPack and Question entries.
 */
public final class DbContract {

    private DbContract(){}

     static class QuestionsEntry implements BaseColumns {
         static final String TABLE_NAME = "questions";
         static final String COLUMN_NAME_QUESTION_TEXT = "question";
         static final String COLUMN_NAME_CORRECT_ANSWER = "correct_answer";
         static final String COLUMN_NAME_ANSWER_CHOICES = "answer_choices";
         static final String COLUMN_NAME_TRIVIA = "trivia";
         static final String COLUMN_NAME_QUESTION_PACK_ID = "question_pack_id";
         static final String COLUMN_NAME_TOPICS = "topics";
         static final String COLUMN_NAME_FLAGS = "flags";
         static final String COLUMN_NAME_DIFFICULTY = "difficulty";
         static final String COLUMN_NAME_IMAGE = "image";
         static final String COLUMN_NAME_ANSWER_POOL_NAME = "answer_pool_name";
         static final String COLUMN_NAME_SOUND = "sound";
    }

     static class QuestionPackEntry implements BaseColumns {
         static final String TABLE_NAME = "question_packs";
         static final String COLUMN_NAME_UNIQUE_NAME = "unique_name";
         static final String COLUMN_NAME_DESCRIPTION = "description";
         static final String COLUMN_NAME_TITLE = "title";
         static final String COLUMN_NAME_AUTHOR = "author";
         static final String COLUMN_NAME_VERSION = "version";
         static final String COLUMN_NAME_DATE_CREATED = "date_created";
         static final String COLUMN_NAME_DATE_DOWNLOADED = "date_downloaded";
         static final String COLUMN_NAME_THUMBNAIL = "thumbnail";
         static final String COLUMN_NAME_DIFFICULTY = "difficulty"; //i.e. the average difficulty
    }


    public static class AnswerPoolNamesEntry implements BaseColumns {

        public static final String TABLE_NAME = "answer_pool_names";
        public static final String COLUMN_NAME_APOOL_NAME = "name";
    }

    public static class AnswerPoolItemsEntry implements BaseColumns {

        public static final String TABLE_NAME = "answer_pool_items";
        public static final String COLUMN_NAME_ANSWER = "answer";
        public static final String COLUMN_NAME_APOOL_ID = "answer_pool_ID";
        public static final String COLUMN_NAME_U_VAL = "u_val";
    }


    public static class QuestionGeneratorEntry implements BaseColumns {
        public static final String TABLE_NAME = "question_generators";
        public static final String COLUMN_NAME_GENERATOR_NAME = "name";
        public static final String COLUMN_NAME_QUESTION_PACK_NAME = "question_pack_name";
    }

    public static class QuestionGeneratorSetEntry implements BaseColumns {
        public static final String TABLE_NAME = "question_generator_sets";
        public static final String COLUMN_NAME_GENERATOR_ID = "generator_id";
        public static final String COLUMN_NAME_SET_NAME = "name";
        public static final String COLUMN_NAME_QUESTION_TEMPLATE= "question_template";
    }

    public static class QuestionGeneratorChunkEntry implements BaseColumns {
        public static final String TABLE_NAME = "q_generator_chunks";
        public static final String COLUMN_NAME_QUESTION_SET_ID = "question_set_id";
        public static final String COLUMN_NAME_SUBJECT = "q_subject";
        public static final String COLUMN_NAME_ANSWER= "q_answer";
        public static final String COLUMN_NAME_TRIVIA= "q_trivia";
    }

}
