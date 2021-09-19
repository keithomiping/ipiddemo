package com.ipid.demo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ipid.demo.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BankAdapter extends ArrayAdapter<String> {

    public BankAdapter(Context context, List<String> bankList) {
        super(context, 0, bankList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NotNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bank_spinner_row, parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.textViewBankName);

        String currentItem = getItem(position);

        if (currentItem != null && !currentItem.isEmpty()) {
            textViewName.setText(currentItem);
        }

        return convertView;
    }
}
