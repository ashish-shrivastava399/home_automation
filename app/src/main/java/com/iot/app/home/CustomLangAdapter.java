package com.iot.app.home;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iot.app.R;

import java.util.List;

public class CustomLangAdapter extends ArrayAdapter<LangData> {
    private List<LangData> langData;

    public CustomLangAdapter(@NonNull Context context, int resource, List<LangData> langData) {
        super(context, 0, langData);
        this.langData = langData;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(getContext()).inflate(R.layout.view_spinner_item, parent, false);

        LangData current = langData.get(position);

        ImageView image = listItem.findViewById(R.id.flag_icon);
        image.setImageResource(current.getImageId());

        TextView name = listItem.findViewById(R.id.name_lang);
        name.setText(current.getName());
        return listItem;
    }
}