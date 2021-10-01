package com.jacsstuff.quizudo.list;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.List;


public class SimpleItemArrayAdapter extends ArrayAdapter<SimpleListItem> {

    private final Context context;
    private final List<SimpleListItem> items;

    public SimpleItemArrayAdapter(Context context, int viewResourceId, List<SimpleListItem> items){
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
            view = vi.inflate(android.R.layout.simple_list_item_1,null);
        }

        SimpleListItem item = items.get(position);
        if(item == null || item.getName().isEmpty()){
            return view;
        }

        TextView textView = (TextView)view;
        textView.setText(item.getName());
        return view;
    }


}

