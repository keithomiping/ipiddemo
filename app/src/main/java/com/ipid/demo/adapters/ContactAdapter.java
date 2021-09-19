package com.ipid.demo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ipid.demo.models.ContactModel;
import com.ipid.demo.R;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private List<ContactModel> contactModelList;
    private ItemClickListener mItemListener;

    public ContactAdapter(List<ContactModel> arrayList, ItemClickListener itemClickListener) {
        this.contactModelList = arrayList;
        this.mItemListener = itemClickListener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContactModel model = contactModelList.get(position);

        holder.tvName.setText(model.getName());
        holder.tvNumber.setText(model.getNumber());
        holder.tvEmail.setText(model.getEmail());
        holder.tvImage.setText(model.getName().charAt(0) + "");

        if (model.getEmail() == null || model.getEmail().isEmpty()) {
            holder.tvEmail.setVisibility(View.GONE);
        } else {
            holder.tvEmail.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(view -> {
            mItemListener.onItemClick(contactModelList.get(position));
        });
    }

    public void clearData() {
        contactModelList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return contactModelList.size();
    }

    public void filterList(List<ContactModel> filteredList) {
        contactModelList = filteredList;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClick(ContactModel contactModel);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvName;
        TextView tvNumber;
        TextView tvEmail;
        TextView tvImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvNumber = itemView.findViewById(R.id.tv_number);
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvImage = itemView.findViewById(R.id.iv_image);
        }
    }
}
