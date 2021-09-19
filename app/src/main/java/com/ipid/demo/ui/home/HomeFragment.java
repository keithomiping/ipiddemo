package com.ipid.demo.ui.home;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.ipid.demo.MyApplication;
import com.ipid.demo.R;
import com.ipid.demo.constants.RequestType;
import com.ipid.demo.databinding.FragmentHomeBinding;
import com.ipid.demo.db.AppDatabase;
import com.ipid.demo.db.entity.BankAccount;
import com.ipid.demo.db.entity.Notification;
import com.ipid.demo.utils.DateUtils;

import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private Dialog dialog;

    private boolean isPayClicked = false;
    private boolean isGetPaidClicked = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dialog = new Dialog(getActivity());
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initPay();
        initGetPaid();
        initNotification();
        initNotificationListener();
    }

    private void initPay() {
        ImageView payImage = binding.payBtn;
        ImageView getPaidImage = binding.getPaidBtn;
        ConstraintLayout payLayout = binding.payLayout;
        ConstraintLayout getPaidLayout = binding.getPaidLayout;
        ConstraintLayout emptyLayout = binding.emptyLayout;
        Button btnPayGo = binding.btnPayGo;

        payImage.setOnClickListener(v -> {
            // Check if the pay button is active and clicked again
            if (isPayClicked) {
                if (!validateBankAccount()) {
                    showDialog();
                } else {
                    HomeFragmentDirections.ActionHomeFragmentToPayTransactFragment action = HomeFragmentDirections.actionHomeFragmentToPayTransactFragment();
                    action.setRequestType(RequestType.PAY.name());
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main).navigate(action);
                }
            } else {
                // Otherwise, display the steps as it was previously inactive.
                payImage.setImageResource(R.drawable.pay);
                payLayout.setVisibility(View.VISIBLE);
                getPaidImage.setImageResource(R.drawable.get_paid_inactive);
                getPaidLayout.setVisibility(View.INVISIBLE);
                emptyLayout.setVisibility(View.INVISIBLE);
                isGetPaidClicked = false;
            }
            isPayClicked = true;
        });

        btnPayGo.setOnClickListener(v -> {
            if (!validateBankAccount()) {
                showDialog();
            } else {
                HomeFragmentDirections.ActionHomeFragmentToPayTransactFragment action = HomeFragmentDirections.actionHomeFragmentToPayTransactFragment();
                action.setRequestType(RequestType.PAY.name());
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main).navigate(action);
            }
        });
    }

    private void initGetPaid() {
        ImageView payImage = binding.payBtn;
        ImageView getPaidImage = binding.getPaidBtn;
        ConstraintLayout payLayout = binding.payLayout;
        ConstraintLayout getPaidLayout = binding.getPaidLayout;
        ConstraintLayout emptyLayout = binding.emptyLayout;
        Button btnGetPaidGo = binding.btnGetPaidGo;

        getPaidImage.setOnClickListener(v -> {
            // Check if the get paid button is active and clicked again
            if (isGetPaidClicked) {
                if (!validateBankAccount()) {
                    showDialog();
                } else {
                    HomeFragmentDirections.ActionHomeFragmentToPayTransactFragment action = HomeFragmentDirections.actionHomeFragmentToPayTransactFragment();
                    action.setRequestType(RequestType.GET_PAID.name());
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main).navigate(action);
                }
            } else {
                // Otherwise, display the steps as it was previously inactive.
                getPaidImage.setImageResource(R.drawable.get_paid);
                getPaidLayout.setVisibility(View.VISIBLE);
                payImage.setImageResource(R.drawable.pay_inactive);
                payLayout.setVisibility(View.INVISIBLE);
                emptyLayout.setVisibility(View.INVISIBLE);
                isPayClicked = false;
            }
            isGetPaidClicked = true;
        });

        btnGetPaidGo.setOnClickListener(v -> {
            if (!validateBankAccount()) {
                showDialog();
            } else {
                HomeFragmentDirections.ActionHomeFragmentToPayTransactFragment action = HomeFragmentDirections.actionHomeFragmentToPayTransactFragment();
                action.setRequestType(RequestType.GET_PAID.name());
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main).navigate(action);
            }
        });
    }

    private void initNotification() {
        ConstraintLayout notificationLayout = getView().findViewById(R.id.notificationLayout);
        ConstraintLayout dateLayout = getView().findViewById(R.id.dateLayout);
        ConstraintLayout contentLayout = getView().findViewById(R.id.contentLayout);
        TextView textViewHeader = getView().findViewById(R.id.textViewHeader);
        TextView textViewDate = getView().findViewById(R.id.textViewDate);
        TextView textViewContent = getView().findViewById(R.id.textViewContent);

        int id = ((MyApplication) this.getActivity().getApplication()).getId();
        AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
        Notification notification = db.notificationDao().findLatestPendingByCustomerId(id);

        if (notification != null) {
            // Change border
            notificationLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.textview_radius_border_background));
            textViewHeader.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            textViewDate.setText(DateUtils.getFormattedDateTime(notification.createdDate));
            textViewContent.setText(notification.description);
        } else {
            // Use white border
            notificationLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.textview_radius_border));
            textViewHeader.setTextColor(ContextCompat.getColor(getContext(), R.color.font_color));
            dateLayout.setVisibility(View.INVISIBLE);
            contentLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void initNotificationListener() {
        ConstraintLayout notificationLayout = getView().findViewById(R.id.notificationLayout);
        notificationLayout.setOnClickListener(v -> {
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main).navigate(R.id.action_homeFragment_to_pendingListFragment);
        });
    }

    private void showDialog() {
        dialog.setContentView(R.layout.popup_setup_bank);

        ImageView closeBtn = dialog.findViewById(R.id.closePopupImg);
        Button btnNext = dialog.findViewById(R.id.btnNext);

        closeBtn.setOnClickListener(v -> {
            dialog.dismiss();
        });

        btnNext.setOnClickListener(v -> {
            dialog.dismiss();

            // Redirect to bank accounts screen
            HomeFragmentDirections.ActionHomeFragmentToProfileFragment action = HomeFragmentDirections.actionHomeFragmentToProfileFragment();
            action.setAccountSetup(false);
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main).navigate(action);
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private boolean validateBankAccount() {
        AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
        int id = ((MyApplication) this.getActivity().getApplication()).getId();
        List<BankAccount> bankAccounts = db.bankAccountDao().findByCustomerId(id);

        if (bankAccounts.size() == 0) {
            return false;
        }

        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}