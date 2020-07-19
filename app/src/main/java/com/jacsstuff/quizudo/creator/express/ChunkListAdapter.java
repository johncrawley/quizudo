package com.jacsstuff.quizudo.creator.express;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jacsstuff.quizudo.R;

import java.util.List;

public class ChunkListAdapter extends ArrayAdapter<ChunkEntity> {

    private Context context;
    private List<ChunkEntity> items;

    public ChunkListAdapter(Context context, int viewResourceId, List<ChunkEntity> items){
        super(context, viewResourceId, items);
        this.context = context;
        this.items = items;
    }


    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent){
        View view = convertView;
        if(view == null){
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.generator_chunk_input,null);
        }

        ChunkEntity chunkEntity = items.get(position);
        if(chunkEntity == null || chunkEntity.getQuestionSubject().isEmpty() || chunkEntity.getAnswer().isEmpty()){
            return view;
        }

        TextView editText = view.findViewById(R.id.editText);
        editText.setText(getEditStringFrom(chunkEntity));
        return view;
    }

    private String getEditStringFrom(ChunkEntity chunk){
        String delimiter = context.getString(R.string.question_generator_chunk_delimiter);
        return chunk.getQuestionSubject() + delimiter + chunk.getAnswer();
    }
}

