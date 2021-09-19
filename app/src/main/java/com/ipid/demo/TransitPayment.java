package com.ipid.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.ipid.demo.databinding.FragmentTransitPaymentBinding;

public class TransitPayment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "TransitPaymentFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentTransitPaymentBinding binding;

    public TransitPayment() {
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
    public static TransitPayment newInstance(String param1, String param2) {
        TransitPayment fragment = new TransitPayment();
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
        inflater.inflate(R.layout.fragment_transit_payment, container, false);
        binding = FragmentTransitPaymentBinding.inflate(inflater,container,false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            TransitPaymentArgs transitPaymentArgs = TransitPaymentArgs.fromBundle(getArguments());
            String message = transitPaymentArgs.getMessage();
            Log.i(TAG, "onViewCreated: " + message);

            TextView textViewDescription = binding.textViewDescription;
            textViewDescription.setText(message);
        }

        initGoButton();
    }

    private void initGoButton() {
        Button btnGo = binding.btnGo;
        btnGo.setOnClickListener((v -> {
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main).navigate(R.id.action_transitPaymentFragment_to_HomeFragment);
        }));
    }
}