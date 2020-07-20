package com.jacsstuff.quizudo.options;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jacsstuff.quizudo.R;
import com.jacsstuff.quizudo.creator.QuizWriterActivity;
import com.jacsstuff.quizudo.answerPool.AnswerPoolActivity;
import com.jacsstuff.quizudo.express.generators.GeneratorsActivity;
import com.jacsstuff.quizudo.download.DownloadQuestionsActivity;

import static com.jacsstuff.quizudo.utils.Consts.DOWNLOAD_QUESTION_PACKS_PREFERENCE;
import static com.jacsstuff.quizudo.utils.Consts.QUESTION_CREATOR_EXPRESS_PREFERENCE;
import static com.jacsstuff.quizudo.utils.Consts.QUESTION_CREATOR_PREFERENCE;
import static com.jacsstuff.quizudo.utils.Consts.REMOVE_QUESTIONS_PREFERENCE;
import static com.jacsstuff.quizudo.utils.Consts.ANSWER_POOL_PREFERENCE;


/**
 * Created by John on 29/11/2016.
 *  The onCreate() method does the important stuff for us here.
 *  This was created by a wizard, as stated above, nothing but the onCreate() method has anything unique
 */
public class SettingsFragment extends PreferenceFragment {

    private OnFragmentInteractionListener mListener;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    public SettingsFragment() {
        // Required empty public constructor

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        setupPreferences();
    }

    private void setupPreferences(){

        for(String preferenceName : new String[]{
                QUESTION_CREATOR_EXPRESS_PREFERENCE,
                DOWNLOAD_QUESTION_PACKS_PREFERENCE,
                REMOVE_QUESTIONS_PREFERENCE,
                ANSWER_POOL_PREFERENCE,
                QUESTION_CREATOR_PREFERENCE }){
            setupPreference(preferenceName);
        }
    }

    private void setupPreference(String preferenceName){
        Preference preference = findPreference(preferenceName);
        setOnClickListener(preference);
    }

    private void launchActivity(Class activity){
        Intent intent = new Intent(getActivity(), activity);
        startActivity(intent);
    }

    private void setOnClickListener(Preference preference){
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                switch(preference.getKey()){
                    case DOWNLOAD_QUESTION_PACKS_PREFERENCE:
                        launchActivity(DownloadQuestionsActivity.class);
                        break;
                    case REMOVE_QUESTIONS_PREFERENCE:
                        launchActivity(RemoveQuestionPacksActivity.class);
                        break;
                    case ANSWER_POOL_PREFERENCE:
                        launchActivity(AnswerPoolActivity.class);
                        break;
                    case QUESTION_CREATOR_EXPRESS_PREFERENCE:
                        launchActivity(GeneratorsActivity.class);
                        break;
                    case QUESTION_CREATOR_PREFERENCE:
                        launchActivity(QuizWriterActivity.class);
                }
                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_settings, container, false);

    }

    // NOTTODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // NOTTODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
