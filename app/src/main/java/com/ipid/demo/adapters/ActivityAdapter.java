package com.ipid.demo.adapters;

import static com.ipid.demo.constants.Constants.ACTION_SENT;
import static com.ipid.demo.constants.Constants.SPACE;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ipid.demo.constants.RequestType;
import com.ipid.demo.models.ActivityModel;
import com.ipid.demo.R;

import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    private List<ActivityModel> activityModelList;
    private ItemClickListener mItemListener;

    public ActivityAdapter(List<ActivityModel> activityModelList, ItemClickListener itemClickListener) {
        this.activityModelList = activityModelList;
        this.mItemListener = itemClickListener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActivityModel model = activityModelList.get(position);

        holder.tvName.setText(getPrefixName(model) + SPACE + model.getName());
        holder.tvDate.setText(model.getDate());
        holder.tvAmount.setText(model.getAmount());
        holder.tvImage.setText(model.getName().charAt(0) + "");
        holder.tvAction.setText(model.getAction());

        if (model.getType() == RequestType.PAY || model.getType() == RequestType.GET_PAID) {
            if (model.getRemarks() != null && !model.getRemarks().isEmpty()) {
                holder.tvRemarks.setText(model.getRemarks());
            } else {
                holder.tvRemarks.setText("Not specified");
            }
        } else {
            holder.tvRemarks.setText("Non-member");
        }

        holder.itemView.setOnClickListener(view -> {
            mItemListener.onItemClick(activityModelList.get(position));
        });
    }

    private String getPrefixName(ActivityModel model) {
        String prefixName = "";
        switch (model.getType()) {
            case PAY:
                if (model.getAction() == ACTION_SENT) {
                    prefixName = "Pay";
                } else {
                    prefixName = "Get paid from";
                }
                break;
            case GET_PAID:
                if (model.getAction() == ACTION_SENT) {
                    prefixName = "Get paid from";
                } else {
                    prefixName = "Pay";
                }
                break;
            case INVITATION:
                prefixName = "Invited";
                break;
            default:
                break;
        }
        return prefixName;
    }

    public void clearData() {
        activityModelList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return activityModelList.size();
    }

    public interface ItemClickListener {
        void onItemClick(ActivityModel activityModel);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvName;
        TextView tvDate;
        TextView tvAmount;
        TextView tvImage;
        TextView tvRemarks;
        TextView tvAction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvImage = itemView.findViewById(R.id.iv_image);
            tvRemarks = itemView.findViewById(R.id.tv_remarks);
            tvAction = itemView.findViewById(R.id.tv_action);
        }
    }
}