package com.jacsstuff.quizudo.list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.jacsstuff.quizudo.model.QuestionPackOverview;
import com.jacsstuff.quizudo.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by John on 12/12/2016.
 * an adapter for UI lists that contain details about the question packs.
 */
public class QuestionPackListAdapter extends ArrayAdapter<QuestionPackOverview> {

    private Context context;
    private List<QuestionPackOverview> items;
   // private List<Integer> selectedIds;
   // private int wasAlreadySetCount = 0;
    enum viewTag{ ID, NAME}

    public QuestionPackListAdapter(Context context, int textViewResourceId, List<QuestionPackOverview> items){
        super(context, textViewResourceId, items);
        this.context = context;
        this.items = items;
        Collections.sort(items, new QuestionPackNameComparator());
        //selectedIds = new ArrayList<>();
    }


    @Override @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent){
        View view = convertView;
        if(view == null){
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = vi.inflate(R.layout.question_pack_list_item, null);
        }

        QuestionPackOverview questionPackOverview = items.get(position);
        if(questionPackOverview != null){
            TextView displayName = view.findViewById(R.id.displayName);
            TextView authorName = view.findViewById(R.id.author);
            CheckedTextView checkbox =  view.findViewById(R.id.checkbox);

            int id = (int) questionPackOverview.getId();
            Log.i("QPListAdapter", "setting id on list item view to "  + id);

            view.setTag(R.id.VIEW_TAG_NAME, questionPackOverview.getName());
            view.setTag(R.id.VIEW_TAG_ID, id);
            checkbox.setTag(id);

            if(displayName != null){
                displayName.setText(questionPackOverview.getName());
            }
            if(authorName != null){
                authorName.setText(questionPackOverview.getAuthor());
            }

        }
        return view;
    }
}
