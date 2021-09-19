package com.ipid.demo;

import static com.ipid.demo.constants.Constants.EMPTY_FIELD;
import static com.ipid.demo.constants.Constants.SPACE;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ipid.demo.databinding.FragmentActivityDetailsBinding;
import com.ipid.demo.db.AppDatabase;
import com.ipid.demo.db.entity.Customer;
import com.ipid.demo.db.entity.LookupCurrency;
import com.ipid.demo.db.entity.Notification;
import com.ipid.demo.db.entity.Payment;
import com.ipid.demo.db.entity.PaymentDetails;
import com.ipid.demo.utils.DateUtils;

public class ActivityDetails extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "ActivityDetailsFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentActivityDetailsBinding binding;

    public ActivityDetails() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileAliases.
     */
    // TODO: Rename and change types and number of parameters
    public static ActivityDetails newInstance(String param1, String param2) {
        ActivityDetails fragment = new ActivityDetails();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflater.inflate(R.layout.fragment_activity_details,container,false);
        binding = FragmentActivityDetailsBinding.inflate(inflater,container,false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        TextView textViewContent = getView().findViewById(R.id.textViewContent);
        TextView textViewIcon = getView().findViewById(R.id.textViewIcon);
        TextView textViewDate = getView().findViewById(R.id.textViewDate);
        TextView textViewAmount = binding.textViewAmount;
        TextView textViewRemarks = binding.textViewRemarks;

        if (getArguments() != null) {
            ActivityDetailsArgs args = ActivityDetailsArgs.fromBundle(getArguments());
            int notificationId = args.getNotificationId();
            Log.i(TAG, "onViewCreated: " + notificationId);

            AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
            Notification notification = db.notificationDao().findById(notificationId);
            Customer customer = db.customerDao().findById(notification.customerTo);
            Payment payment= db.paymentDao().findById(notification.paymentId);
            PaymentDetails paymentDetails = db.paymentDetailsDao().findByPaymentId(notification.paymentId);
            LookupCurrency lookupCurrencyFrom = db.lookupCurrencyDao().findById(paymentDetails.currencyFrom);

            textViewIcon.setText(customer.firstName.charAt(0) + "");
            textViewDate.setText(DateUtils.getFormattedDateTime(notification.createdDate));
            textViewContent.setText(notification.description);
            textViewAmount.setText(paymentDetails.amountFrom + SPACE + lookupCurrencyFrom.description);

            if (!payment.remarks.isEmpty()) {
                textViewRemarks.setText(payment.remarks);
            } else {
                textViewRemarks.setText(EMPTY_FIELD);
            }
        }
    }
}