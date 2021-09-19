package com.ipid.demo.adapters;

import static com.ipid.demo.constants.Constants.ACTION_RECEIVED;
import static com.ipid.demo.constants.Constants.ACTION_SENT;
import static com.ipid.demo.constants.Constants.ADD_ACCOUNT;
import static com.ipid.demo.constants.Constants.PROCEED;
import static com.ipid.demo.constants.Constants.REAL_FORMATTER;
import static com.ipid.demo.constants.Constants.RESEND;
import static com.ipid.demo.constants.Constants.SHARE;
import static com.ipid.demo.constants.Constants.SPACE;

import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.ipid.demo.MyApplication;
import com.ipid.demo.PendingListFragmentDirections;
import com.ipid.demo.R;
import com.ipid.demo.constants.RequestType;
import com.ipid.demo.db.AppDatabase;
import com.ipid.demo.db.entity.Customer;
import com.ipid.demo.db.entity.LookupCurrency;
import com.ipid.demo.db.entity.Notification;
import com.ipid.demo.db.entity.Payment;
import com.ipid.demo.db.entity.PaymentDetails;
import com.ipid.demo.models.ActivityModel;
import com.ipid.demo.utils.DateUtils;
import com.ipid.demo.utils.NameUtils;

import java.util.List;

public class PendingListAdapter extends RecyclerView.Adapter<PendingListAdapter.ViewHolder> {

    private List<ActivityModel> activityModelList;
    private ItemClickListener mItemListener;
    private FragmentActivity fragmentActivity;

    public PendingListAdapter(List<ActivityModel> activityModelList, FragmentActivity fragmentActivity, ItemClickListener itemClickListener) {
        this.activityModelList = activityModelList;
        this.mItemListener = itemClickListener;
        this.fragmentActivity = fragmentActivity;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pending_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActivityModel model = activityModelList.get(position);

        holder.tvName.setText(getPrefixName(model) + SPACE + model.getName());
        holder.tvDate.setText(model.getDate());
        holder.tvAmount.setText(model.getAmount());
        holder.tvImage.setText(model.getName().charAt(0) + "");

        if (!model.getRemarks().isEmpty()) {
            holder.tvRemarks.setText(model.getRemarks());
        } else {
            holder.tvRemarks.setText("Not specified");
        }

        // Pay and Get paid receive
        if (model.getAction().equalsIgnoreCase(ACTION_RECEIVED)) {
            holder.btnFirst.setVisibility(View.INVISIBLE);
            holder.btnSecond.setVisibility(View.VISIBLE);

            if (model.getType() == RequestType.PAY) {
                holder.btnSecond.setText(ADD_ACCOUNT);
            } else if (model.getType() == RequestType.GET_PAID) {
                holder.btnSecond.setText(PROCEED);
            }
        }

        // Pay and Get paid sent
        if (model.getAction().equalsIgnoreCase(ACTION_SENT)) {
            holder.btnFirst.setVisibility(View.VISIBLE);
            holder.btnSecond.setVisibility(View.VISIBLE);

            if (model.getType() == RequestType.PAY) {
                AppDatabase db = AppDatabase.getDbInstance(fragmentActivity.getApplicationContext());
                Notification notification = db.notificationDao().findById(model.getNotificationId());
                List<Notification> notificationsList = db.notificationDao().findPendingByPaymentId(notification.paymentId);

                if (notificationsList.size() > 1) {
                    holder.btnFirst.setText(RESEND);
                } else {
                    holder.btnFirst.setText(PROCEED);
                }
                holder.btnSecond.setText(SHARE);
            }

            if (model.getType() == RequestType.GET_PAID) {
                holder.btnFirst.setText(RESEND);
                holder.btnSecond.setText(SHARE);
            }
        }

        holder.btnFirst.setOnClickListener(view -> {
            // Pay
            if (model.getType() == RequestType.PAY) {
                if (holder.btnFirst.getText().toString().equalsIgnoreCase(PROCEED)) {
                    AppDatabase db = AppDatabase.getDbInstance(fragmentActivity.getApplicationContext());
                    Notification notification = db.notificationDao().findById(model.getNotificationId());
                    Payment payment = db.paymentDao().findById(notification.paymentId);
                    PaymentDetails paymentDetails = db.paymentDetailsDao().findByPaymentId(notification.paymentId);
                    LookupCurrency lookupCurrencyFrom = db.lookupCurrencyDao().findById(paymentDetails.currencyFrom);
                    LookupCurrency lookupCurrencyTo = db.lookupCurrencyDao().findById(paymentDetails.currencyTo);

                    // Send to step 3 of pay workflow
                    PendingListFragmentDirections.ActionPendingListFragmentToResultFragment action = PendingListFragmentDirections.actionPendingListFragmentToResultFragment();
                    action.setId(payment.customerTo); // Send to recipient using payment.customerTo
                    action.setAmountFrom(REAL_FORMATTER.format(paymentDetails.amountFrom));
                    action.setAmountTo(REAL_FORMATTER.format(paymentDetails.amountTo));
                    action.setCurrencyFrom(lookupCurrencyFrom.description);
                    action.setCurrencyTo(lookupCurrencyTo.description);
                    action.setCountryFrom(lookupCurrencyFrom.country);
                    action.setCountryTo(lookupCurrencyTo.country);
                    action.setExchangeRate(REAL_FORMATTER.format(paymentDetails.exchangeRate));
                    action.setRemarks(payment.remarks);
                    action.setNotificationId(notification.id);
                    Navigation.findNavController(fragmentActivity, R.id.nav_host_fragment_activity_main).navigate(action);

                } else {
                    // Resend
                    AppDatabase db = AppDatabase.getDbInstance(fragmentActivity.getApplicationContext());
                    Notification notification = db.notificationDao().findById(model.getNotificationId());
                    List<Notification> notificationsList = db.notificationDao().findPendingByPaymentId(notification.paymentId);

                    for (Notification notif : notificationsList) {
                        Notification newNotification = new Notification();
                        newNotification.customerFrom = notif.customerFrom;
                        newNotification.customerTo = notif.customerTo;
                        newNotification.paymentId = notif.paymentId;
                        newNotification.description = notif.description;
                        newNotification.type = notif.type;
                        newNotification.status = true;
                        newNotification.pending = false;
                        newNotification.resent = true;
                        newNotification.createdDate = DateUtils.getDateTime();
                        db.notificationDao().insertNotification(newNotification);
                    }
                    Toast.makeText(fragmentActivity, "Notification has been resent. Please refer to your activity list.", Toast.LENGTH_SHORT).show();
                }
            }

            // Get Paid
            if (model.getType() == RequestType.GET_PAID) {
                // Resend
                AppDatabase db = AppDatabase.getDbInstance(fragmentActivity.getApplicationContext());
                Notification notification = db.notificationDao().findById(model.getNotificationId());
                List<Notification> notificationsList = db.notificationDao().findPendingByPaymentId(notification.paymentId);

                for (Notification notif : notificationsList) {
                    Notification newNotification = new Notification();
                    newNotification.customerFrom = notif.customerFrom;
                    newNotification.customerTo = notif.customerTo;
                    newNotification.paymentId = notif.paymentId;
                    newNotification.description = notif.description;
                    newNotification.type = notif.type;
                    newNotification.status = true;
                    newNotification.pending = false;
                    newNotification.resent = true;
                    newNotification.createdDate = DateUtils.getDateTime();
                    db.notificationDao().insertNotification(newNotification);
                }
                Toast.makeText(fragmentActivity, "Notification has been resent. Please refer to your activity list.", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnSecond.setOnClickListener(view -> {
            AppDatabase db = AppDatabase.getDbInstance(fragmentActivity.getApplicationContext());
            Notification notification = db.notificationDao().findById(model.getNotificationId());
            Payment payment = db.paymentDao().findById(notification.paymentId);
            PaymentDetails paymentDetails = db.paymentDetailsDao().findByPaymentId(notification.paymentId);
            LookupCurrency lookupCurrencyFrom = db.lookupCurrencyDao().findById(paymentDetails.currencyFrom);
            LookupCurrency lookupCurrencyTo = db.lookupCurrencyDao().findById(paymentDetails.currencyTo);

            // Both pay and get paid goes to result screen (Pay step 3)
            if (model.getType() == RequestType.GET_PAID) {
                if (holder.btnSecond.getText().toString().equalsIgnoreCase(PROCEED)) {
                    PendingListFragmentDirections.ActionPendingListFragmentToResultFragment action = PendingListFragmentDirections.actionPendingListFragmentToResultFragment();
                    action.setId(notification.customerFrom); // Requester
                    action.setAmountFrom(REAL_FORMATTER.format(paymentDetails.amountFrom));
                    action.setAmountTo(REAL_FORMATTER.format(paymentDetails.amountTo));
                    action.setCurrencyFrom(lookupCurrencyFrom.description);
                    action.setCurrencyTo(lookupCurrencyTo.description);
                    action.setCountryFrom(lookupCurrencyFrom.country);
                    action.setCountryTo(lookupCurrencyTo.country);
                    action.setExchangeRate(REAL_FORMATTER.format(paymentDetails.exchangeRate));
                    action.setRemarks(payment.remarks);
                    action.setNotificationId(notification.id);
                    Navigation.findNavController(fragmentActivity, R.id.nav_host_fragment_activity_main).navigate(action);
                } else {
                    // Share
                    int id = ((MyApplication) fragmentActivity.getApplication()).getId();
                    Customer customerFrom = db.customerDao().findById(id);
                    ClipboardManager clipboard = (ClipboardManager) fragmentActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(NameUtils.getFullName(customerFrom.firstName, customerFrom.lastName) + " has requested you to pay " + paymentDetails.amountFrom + ". Please log in to iPiD.");

                    Toast.makeText(fragmentActivity, "Link copied!", Toast.LENGTH_SHORT).show();
                }
            }

            if (model.getType() == RequestType.PAY) {
                if (holder.btnSecond.getText().toString().equalsIgnoreCase(ADD_ACCOUNT)) {
                    PendingListFragmentDirections.ActionPendingListFragmentToPreferredAccountFragment action = PendingListFragmentDirections.actionPendingListFragmentToPreferredAccountFragment();
                    action.setNotificationId(notification.id);
                    Navigation.findNavController(fragmentActivity, R.id.nav_host_fragment_activity_main).navigate(action);
                } else {
                    // Share
                    int id = ((MyApplication) fragmentActivity.getApplication()).getId();
                    Customer customerFrom = db.customerDao().findById(id);
                    ClipboardManager clipboard = (ClipboardManager) fragmentActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(NameUtils.getFullName(customerFrom.firstName, customerFrom.lastName) + " wants to pay " + paymentDetails.amountFrom + " to you. Please log in to iPiD.");


                    Toast.makeText(fragmentActivity, "Invite link copied!", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
        Button btnFirst;
        Button btnSecond;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvImage = itemView.findViewById(R.id.iv_image);
            tvRemarks = itemView.findViewById(R.id.tv_remarks);
            btnFirst = itemView.findViewById(R.id.firstButton);
            btnSecond = itemView.findViewById(R.id.secondButton);
        }
    }
}