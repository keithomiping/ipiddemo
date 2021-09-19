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

import com.ipid.demo.models.CurrencyModel;
import com.ipid.demo.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CurrencyAdapter extends ArrayAdapter<CurrencyModel> {

    public CurrencyAdapter(Context context, List<CurrencyModel> currencyList) {
        super(context, 0, currencyList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NotNull ViewGroup parent) {
        return initDropDownView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.currency_spinner_row, parent, false);
        }

        ImageView imageViewFlag = convertView.findViewById(R.id.imageViewFlag);
        TextView textViewName = convertView.findViewById(R.id.textViewCountryName);
        TextView textViewCurrency = convertView.findViewById(R.id.textViewCurency);

        CurrencyModel currentItem = getItem(position);

        if (currentItem != null) {
            imageViewFlag.setImageResource(currentItem.getCountryFlagImage());
            textViewName.setText("");
            textViewCurrency.setText("");
        }

        return convertView;
    }

    private View initDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.currency_spinner_row, parent, false);
        }

        ImageView imageViewFlag = convertView.findViewById(R.id.imageViewFlag);
        TextView textViewName = convertView.findViewById(R.id.textViewCountryName);
        TextView textViewCurrency = convertView.findViewById(R.id.textViewCurency);

        CurrencyModel currentItem = getItem(position);

        if (currentItem != null) {
            imageViewFlag.setImageResource(currentItem.getCountryFlagImage());
            textViewName.setText(currentItem.getCountryName());
            textViewCurrency.setText(currentItem.getCurrency());
        }

        return convertView;
    }
}