package com.ipid.demo.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ipid.demo.models.BankModel;
import com.ipid.demo.R;
import com.ipid.demo.db.AppDatabase;

import java.util.List;

public class BankAccountAdapter extends BaseAdapter {

     Activity activity;
     List<BankModel> bankModelList;
     LayoutInflater inflater;

    public BankAccountAdapter(Activity activity, List<BankModel> bankModelList) {
        this.activity = activity;
        this.bankModelList = bankModelList;

        inflater = activity.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return bankModelList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {
            view = inflater.inflate(R.layout.list_view_bank_account, viewGroup, false);
            holder = new ViewHolder();
            holder.tvBankName = view.findViewById(R.id.tv_bank_name);
            holder.tvBankNumber = view.findViewById(R.id.tv_bank_number);
            holder.ivCheckBox = view.findViewById(R.id.iv_check_box);
            holder.ivCountryFlag = view.findViewById(R.id.iv_country_flag);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        BankModel bankModel = bankModelList.get(i);

        holder.tvBankName.setText(bankModel.getBankName());
        holder.tvBankNumber.setText(bankModel.getBankNumber());
        holder.ivCountryFlag.setImageResource(bankModel.getCountryFlagImage());
        holder.ivCheckBox.setOnClickListener(v -> {
            AppDatabase db = AppDatabase.getDbInstance(v.getContext().getApplicationContext());
            db.bankAccountDao().softDeleteBankAccount(bankModel.getBankId());

            bankModelList.remove(i);
            updateRecords(bankModelList);
        });

        return view;
    }

    public void updateRecords(List<BankModel> bankModelList) {
        this.bankModelList = bankModelList;
        notifyDataSetChanged();
    }

    class ViewHolder {
        TextView tvBankName;
        TextView tvBankNumber;
        ImageView ivCheckBox;
        ImageView ivCountryFlag;
    }
}
