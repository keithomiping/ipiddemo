package com.ipid.demo.ui.dashboard;

import static com.ipid.demo.constants.Constants.ACTION_RECEIVED;
import static com.ipid.demo.constants.Constants.ACTION_SENT;
import static com.ipid.demo.constants.Constants.INVITATION_PREFIX;

import android.app.DownloadManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ipid.demo.adapters.ActivityAdapter;
import com.ipid.demo.models.ActivityModel;
import com.ipid.demo.MyApplication;
import com.ipid.demo.R;
import com.ipid.demo.constants.RequestType;
import com.ipid.demo.databinding.FragmentDashboardBinding;
import com.ipid.demo.db.AppDatabase;
import com.ipid.demo.db.entity.Customer;
import com.ipid.demo.db.entity.LookupCurrency;
import com.ipid.demo.db.entity.Notification;
import com.ipid.demo.db.entity.Payment;
import com.ipid.demo.db.entity.PaymentDetails;
import com.ipid.demo.utils.DateUtils;
import com.ipid.demo.utils.NameUtils;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;

    private RecyclerView recyclerView;
    private List<ActivityModel> activityModelList = new ArrayList<>();
    private ActivityAdapter activityAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        inflater.inflate(R.layout.fragment_dashboard, container, false);
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        recyclerView = getView().findViewById(R.id.activityRecyclerView);

        if (activityAdapter != null) {
            activityAdapter.clearData();
        }

        initRequestActivities();
    }

    private void initRequestActivities() {
        ConstraintLayout layoutEmptyActivity = binding.layoutEmptyActivity;
        RecyclerView activityRecyclerView = binding.activityRecyclerView;

        int id = ((MyApplication) this.getActivity().getApplication()).getId();
        AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
        List<Notification> notificationList = db.notificationDao().findAllByCustomerId(id);

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

                if (notification.paymentId != null) {
                    paymentDetails = db.paymentDetailsDao().findByPaymentId(notification.paymentId);
                    lookupCurrency = db.lookupCurrencyDao().findById(paymentDetails.currencyFrom);
                    payment = db.paymentDao().findById(notification.paymentId);
                    activityModel.setAmount(paymentDetails.amountFrom + " " + lookupCurrency.description);
                    activityModel.setRemarks(payment.remarks);
                }

                // Check if notification is the logged in user
                if (notification.customerFrom == id && notification.customerTo == id) {
                    if (notification.type.equalsIgnoreCase(RequestType.PAY.name()) || notification.type.equalsIgnoreCase(RequestType.GET_PAID.name())) {
                        payment = db.paymentDao().findById(notification.paymentId);
                        Customer customerTo = db.customerDao().findById(payment.customerTo);
                        activityModel.setName(NameUtils.getFullName(customerTo.firstName, customerTo.lastName));
                        activityModel.setType(RequestType.valueOf(notification.type));
                    } else {
                        // INVITATION
                        String name = notification.description.replace(INVITATION_PREFIX, "");
                        activityModel.setName(name.replace(".", ""));
                        activityModel.setType(RequestType.INVITATION);
                    }
                    activityModel.setAction(ACTION_SENT);
                } else {
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
            activityAdapter = new ActivityAdapter(activityModelList, activityModel -> {
                if (activityModel.getType() == RequestType.PAY || activityModel.getType() == RequestType.GET_PAID) {
                    DashboardFragmentDirections.ActionDashBoardFragmentToActivityDetailsFragment action = DashboardFragmentDirections.actionDashBoardFragmentToActivityDetailsFragment();
                    action.setNotificationId(activityModel.getNotificationId());
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main).navigate(action);
                }
            });
            recyclerView.setAdapter(activityAdapter);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}