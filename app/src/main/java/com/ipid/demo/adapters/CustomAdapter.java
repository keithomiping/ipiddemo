package com.ipid.demo.adapters;

import static com.ipid.demo.constants.Constants.INVALID_ACCOUNT_NUMBER;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.ipid.demo.R;
import com.ipid.demo.db.AppDatabase;
import com.ipid.demo.db.entity.BankAccount;
import com.ipid.demo.models.BankModel;

import java.util.List;

public class CustomAdapter extends BaseAdapter {

     Activity activity;
     List<BankModel> bankModelList;
     LayoutInflater inflater;

    public CustomAdapter(Activity activity, List<BankModel> bankModelList) {
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
            view = inflater.inflate(R.layout.list_view_item, viewGroup, false);
            holder = new ViewHolder();
            holder.tvBankName = view.findViewById(R.id.tv_bank_name);
            holder.tvBankNumber = view.findViewById(R.id.tv_bank_number);
            holder.tvPreferred = view.findViewById(R.id.tv_preferred);
            holder.ivCheckBox = view.findViewById(R.id.iv_check_box);
            holder.ivCountryFlag = view.findViewById(R.id.iv_country_flag);
            holder.ivEdit = view.findViewById(R.id.iv_edit);
            holder.ivDelete = view.findViewById(R.id.iv_delete);
            holder.editTextBankNumber = view.findViewById(R.id.editText_bankNumber);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        BankModel bankModel = bankModelList.get(i);

        holder.tvBankName.setText(bankModel.getBankName());
        holder.tvBankNumber.setText(bankModel.getBankNumber());
        holder.ivCountryFlag.setImageResource(bankModel.getCountryFlagImage());

        if (!bankModel.isEdited()) {
            if (bankModel.isSelected()) {
                holder.tvBankName.setTextColor(ContextCompat.getColor(view.getContext(), R.color.white));
                holder.tvBankNumber.setTextColor(ContextCompat.getColor(view.getContext(), R.color.white));
                holder.tvPreferred.setTextColor(ContextCompat.getColor(view.getContext(), R.color.white));
                holder.ivCheckBox.setBackgroundResource(R.drawable.ic_baseline_radio_button_checked_24);
                holder.ivEdit.setBackgroundResource(R.drawable.ic_white_edit_24);
                holder.ivDelete.setBackgroundResource(R.drawable.ic_white_delete_24);
                view.setBackgroundResource(R.color.font_color_700);
            } else {
                holder.tvBankName.setTextColor(ContextCompat.getColor(view.getContext(), R.color.font_color));
                holder.tvBankNumber.setTextColor(ContextCompat.getColor(view.getContext(), R.color.font_color));
                holder.tvPreferred.setTextColor(ContextCompat.getColor(view.getContext(), R.color.font_color));
                holder.ivCheckBox.setBackgroundResource(R.drawable.ic_baseline_radio_button_unchecked_24);
                holder.ivEdit.setBackgroundResource(R.drawable.ic_baseline_edit_24);
                holder.ivDelete.setBackgroundResource(R.drawable.ic_baseline_delete_24);
                view.setBackgroundResource(R.color.white);
            }

            if (bankModel.isShowManageIcons() && !bankModel.isPreferred()) {
                holder.ivEdit.setVisibility(View.VISIBLE);
                holder.ivDelete.setVisibility(View.VISIBLE);
            } else {
                holder.ivEdit.setVisibility(View.INVISIBLE);
                holder.ivDelete.setVisibility(View.INVISIBLE);
            }

            if (bankModel.isPreferred()) {
                holder.tvPreferred.setVisibility(View.VISIBLE);
            } else {
                holder.tvPreferred.setVisibility(View.GONE);
            }

            holder.ivEdit.setOnClickListener(v -> {
                if (bankModel.isEdited()) {
                    // Validate
                    if (holder.editTextBankNumber.getText().toString().isEmpty()) {
                        Toast.makeText(v.getContext(), INVALID_ACCOUNT_NUMBER, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Save and refresh data
                    holder.editTextBankNumber.setVisibility(View.GONE);
                    holder.tvBankNumber.setVisibility(View.VISIBLE);

                    if (bankModel.isSelected()) {
                        holder.ivEdit.setBackgroundResource(R.drawable.ic_white_edit_24);
                        holder.ivDelete.setBackgroundResource(R.drawable.ic_white_delete_24);
                    } else {
                        holder.ivEdit.setBackgroundResource(R.drawable.ic_baseline_edit_24);
                        holder.ivDelete.setBackgroundResource(R.drawable.ic_baseline_delete_24);
                    }

                    // Update bank model
                    bankModelList.get(i).setBankNumber(holder.editTextBankNumber.getText().toString());
                    bankModelList.get(i).setEdited(Boolean.FALSE);

                    // Save changes in database
                    AppDatabase db = AppDatabase.getDbInstance(v.getContext().getApplicationContext());
                    BankAccount bankAccount = db.bankAccountDao().findById(bankModel.getBankId());
                    bankAccount.accountNumber = holder.editTextBankNumber.getText().toString();
                    db.bankAccountDao().update(bankAccount);

                    updateRecords(bankModelList);

                } else {
                    bankModel.setEdited(Boolean.TRUE);
                    bankModelList.get(i).setEdited(Boolean.TRUE);

                    // Show and allow editing of account number
                    holder.editTextBankNumber.setVisibility(View.VISIBLE);
                    holder.tvBankNumber.setVisibility(View.GONE);

                    if (bankModel.isSelected()) {
                        holder.ivEdit.setBackgroundResource(R.drawable.ic_white_baseline_save_24);
                        holder.ivDelete.setBackgroundResource(R.drawable.ic_white_baseline_close_24);
                        holder.editTextBankNumber.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.edittext_white_bottom_line));
                        holder.editTextBankNumber.setTextColor(ContextCompat.getColor(v.getContext(), R.color.white));
                    } else {
                        holder.ivEdit.setBackgroundResource(R.drawable.ic_baseline_save_24);
                        holder.ivDelete.setBackgroundResource(R.drawable.ic_baseline_close_24);
                        holder.editTextBankNumber.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.edittext_bottom_line));
                        holder.editTextBankNumber.setTextColor(ContextCompat.getColor(v.getContext(), R.color.font_color));
                    }

                    holder.editTextBankNumber.setText(bankModel.getBankNumber());
                }
            });

            holder.ivDelete.setOnClickListener(v -> {
                if (bankModel.isEdited()) {
                    // Close button for editing
                    holder.editTextBankNumber.setVisibility(View.GONE);
                    holder.tvBankNumber.setVisibility(View.VISIBLE);

                    if (bankModel.isSelected()) {
                        holder.ivEdit.setBackgroundResource(R.drawable.ic_white_edit_24);
                        holder.ivDelete.setBackgroundResource(R.drawable.ic_white_delete_24);
                    } else {
                        holder.ivEdit.setBackgroundResource(R.drawable.ic_baseline_edit_24);
                        holder.ivDelete.setBackgroundResource(R.drawable.ic_baseline_delete_24);
                    }
                    // Set bank account number
                    holder.tvBankNumber.setText(bankModel.getBankNumber());
                    bankModel.setEdited(Boolean.FALSE);
                    bankModelList.get(i).setEdited(Boolean.FALSE);

                } else {
                    // Delete button
                    AppDatabase db = AppDatabase.getDbInstance(v.getContext().getApplicationContext());
                    db.bankAccountDao().softDeleteBankAccount(bankModel.getBankId());

                    if (bankModel.isSelected()) {
                        // Set preferred as selected if currently selected is deleted
                        bankModelList.get(0).setSelected(Boolean.TRUE);
                    }

                    bankModelList.remove(i);
                    updateRecords(bankModelList);
                }
            });
        }

        return view;
    }

    public void updateRecords(List<BankModel> bankModelList) {
        this.bankModelList = bankModelList;
        notifyDataSetChanged();
    }

    class ViewHolder {
        TextView tvBankName;
        TextView tvBankNumber;
        TextView tvPreferred;
        ImageView ivCheckBox;
        ImageView ivCountryFlag;
        ImageView ivEdit;
        ImageView ivDelete;
        EditText editTextBankNumber;
    }
}
