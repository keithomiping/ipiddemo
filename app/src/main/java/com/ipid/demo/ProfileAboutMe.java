package com.ipid.demo;

import static com.ipid.demo.constants.Constants.ABOUT_ME_ALIASES;
import static com.ipid.demo.constants.Constants.ABOUT_ME_DETAILS;
import static com.ipid.demo.constants.Constants.EDIT;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.ipid.demo.databinding.FragmentProfileAboutMeBinding;

public class ProfileAboutMe extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentProfileAboutMeBinding binding;

    private Button buttonProfileEdit;

    private static final String TAG = "ProfileAboutMe";

    public ProfileAboutMe() {
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
    public static ProfileAboutMe newInstance(String param1, String param2) {
        ProfileAboutMe fragment = new ProfileAboutMe();
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
        // Inflate the layout for this fragment
        inflater.inflate(R.layout.fragment_profile_about_me, container, false);
        binding = FragmentProfileAboutMeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getParentFragmentManager().beginTransaction().replace(R.id.profileAboutMeFragment, new ProfileDetails()).commit();
        initProfileDetails();
        initProfileAliases();
    }

    private void initProfileDetails() {
        TextView textViewDetails = binding.textViewAboutMeDetails;
        TextView textViewAliases = binding.textViewAboutMeAliases;

        textViewDetails.setOnClickListener(v -> {
            textViewDetails.setTextColor(ContextCompat.getColor(getContext(), R.color.font_color));
            textViewAliases.setTextColor(ContextCompat.getColor(getContext(), R.color.font_color_200));
            getParentFragmentManager().beginTransaction().replace(R.id.profileAboutMeFragment, new ProfileDetails(), ABOUT_ME_DETAILS).commit();
        });
    }

    private void initProfileAliases() {
        TextView textViewDetails = binding.textViewAboutMeDetails;
        TextView textViewAliases = binding.textViewAboutMeAliases;

        textViewAliases.setOnClickListener(v -> {
            textViewDetails.setTextColor(ContextCompat.getColor(getContext(), R.color.font_color_200));
            textViewAliases.setTextColor(ContextCompat.getColor(getContext(), R.color.font_color));
            getParentFragmentManager().beginTransaction().replace(R.id.profileAboutMeFragment, new ProfileAliases(), ABOUT_ME_ALIASES).commit();
        });
    }
}