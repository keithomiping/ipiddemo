package com.ipid.demo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ipid.demo.models.CountryModel;
import com.ipid.demo.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CountryAdapter extends ArrayAdapter<CountryModel> {

    public CountryAdapter(Context context, List<CountryModel> countryList) {
        super(context, 0, countryList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NotNull ViewGroup parent) {
        return initDropdownView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.country_spinner_row, parent, false);
        }

        ImageView imageViewFlag = convertView.findViewById(R.id.imageViewFlag);
        TextView textViewName = convertView.findViewById(R.id.textViewCountryName);

        CountryModel currentItem = getItem(position);

        if (currentItem != null) {
            imageViewFlag.setImageResource(currentItem.getCountryFlagImage());
            textViewName.setText("");
        }

        return convertView;
    }

    private View initDropdownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.country_spinner_row, parent, false);
        }

        ImageView imageViewFlag = convertView.findViewById(R.id.imageViewFlag);
        TextView textViewName = convertView.findViewById(R.id.textViewCountryName);

        CountryModel currentItem = getItem(position);

        if (currentItem != null) {
            imageViewFlag.setImageResource(currentItem.getCountryFlagImage());
            textViewName.setText(currentItem.getCountryName());
        }

        return convertView;
    }
}
