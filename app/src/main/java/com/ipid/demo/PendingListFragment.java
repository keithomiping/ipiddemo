package com.ipid.demo;

import static com.ipid.demo.constants.Constants.ACTION_RECEIVED;
import static com.ipid.demo.constants.Constants.ACTION_SENT;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ipid.demo.adapters.PendingListAdapter;
import com.ipid.demo.constants.RequestType;
import com.ipid.demo.databinding.FragmentPendingListBinding;
import com.ipid.demo.db.AppDatabase;
import com.ipid.demo.db.entity.Customer;
import com.ipid.demo.db.entity.LookupCurrency;
import com.ipid.demo.db.entity.Notification;
import com.ipid.demo.db.entity.Payment;
import com.ipid.demo.db.entity.PaymentDetails;
import com.ipid.demo.models.ActivityModel;
import com.ipid.demo.utils.DateUtils;
import com.ipid.demo.utils.NameUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PendingListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PendingListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentPendingListBinding binding;

    private RecyclerView recyclerView;
    private List<ActivityModel> activityModelList = new ArrayList<>();
    private PendingListAdapter pendingListAdapter;

    public PendingListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PendingListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PendingListFragment newInstance(String param1, String param2) {
        PendingListFragment fragment = new PendingListFragment();
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
        inflater.inflate(R.layout.fragment_dashboard, container, false);
        binding = FragmentPendingListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        recyclerView = getView().findViewById(R.id.activityRecyclerView);

        if (pendingListAdapter != null) {
            pendingListAdapter.clearData();
        }

        initPendingList();
    }

    private void initPendingList() {
        ConstraintLayout layoutEmptyActivity = binding.layoutEmptyActivity;
        RecyclerView activityRecyclerView = binding.activityRecyclerView;

        int id = ((MyApplication) this.getActivity().getApplication()).getId();
        AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
        List<Notification> notificationList = db.notificationDao().findAllPendingByCustomerId(id);

        if (notificationList.size() == 0) {
            layoutEmptyActivity.setVisibility(View.VISIBLE);
            activityRecyclerView.setVisibility(View.INVISIBLE);
        } else {
            layoutEmptyActivity.setVisibility(View.INVISIBLE);
            activityRecyclerView.setVisibility(View.VISIBLE);

            // Populate activity model list from notifications
            for (Notification notification : notificationList) {
                ActivityModel activityModel = new ActivityModel();

                Customer customer = db.customerDao().findById(notification.customerFrom);
                PaymentDetails paymentDetails;
                LookupCurrency lookupCurrency;
                Payment payment;

                // Set amount for pay, get paid (excluding invitation)
                if (notification.paymentId != null) {
                    paymentDetails = db.paymentDetailsDao().findByPaymentId(notification.paymentId);
                    lookupCurrency = db.lookupCurrencyDao().findById(paymentDetails.currencyFrom);
                    payment = db.paymentDao().findById(notification.paymentId);
                    activityModel.setAmount(paymentDetails.amountFrom + " " + lookupCurrency.description);
                    activityModel.setRemarks(payment.remarks);
                }

                // Check if notification is the logged in user
                if (notification.customerFrom == id && notification.customerTo == id) {
                    payment = db.paymentDao().findById(notification.paymentId);
                    Customer customerTo = db.customerDao().findById(payment.customerTo);
                    activityModel.setName(NameUtils.getFullName(customerTo.firstName, customerTo.lastName));
                    activityModel.setType(RequestType.valueOf(notification.type));
                    activityModel.setAction(ACTION_SENT);
                } else {
                    // Non-logged in users
                    activityModel.setName(NameUtils.getFullName(customer.firstName, customer.lastName));
                    activityModel.setType(RequestType.valueOf(notification.type));
                    activityModel.setAction(ACTION_RECEIVED);
                }

                activityModel.setId(notification.customerTo);
                activityModel.setDate(DateUtils.getFormattedDateTime(notification.createdDate));
                activityModel.setNotificationId(notification.id);
                activityModelList.add(activityModel);
            }

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);
            recyclerView.setLayoutManager(linearLayoutManager);
            pendingListAdapter = new PendingListAdapter(activityModelList, getActivity(), activityModel -> {
                // Nothing to do since we have buttons to redirect
            });
            recyclerView.setAdapter(pendingListAdapter);
        }
    }
}