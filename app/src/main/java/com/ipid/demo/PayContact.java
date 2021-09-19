package com.ipid.demo;

import static com.ipid.demo.constants.Constants.CONTINUE;
import static com.ipid.demo.constants.Constants.DEFAULT_BANK_ACCOUNT_ID;
import static com.ipid.demo.constants.Constants.EMPTY_FIELD;
import static com.ipid.demo.constants.Constants.INVITATION_PREFIX;
import static com.ipid.demo.constants.Constants.NON_MEMBER_DIALOG_PREFIX;
import static com.ipid.demo.constants.Constants.PAY_NON_MEMBER;
import static com.ipid.demo.constants.Constants.PAY_RECEIVER_NOTIFICATION_PREFIX;
import static com.ipid.demo.constants.Constants.PAY_SENDER_ASK_DETAILS_NOTIFICATION_PREFIX;
import static com.ipid.demo.constants.Constants.PERIOD;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ipid.demo.adapters.ContactAdapter;
import com.ipid.demo.constants.Constants;
import com.ipid.demo.constants.RequestType;
import com.ipid.demo.databinding.FragmentPayContactBinding;
import com.ipid.demo.db.AppDatabase;
import com.ipid.demo.db.entity.BankAccount;
import com.ipid.demo.db.entity.Customer;
import com.ipid.demo.db.entity.LookupCurrency;
import com.ipid.demo.db.entity.Notification;
import com.ipid.demo.db.entity.Payment;
import com.ipid.demo.db.entity.PaymentDetails;
import com.ipid.demo.models.ContactModel;
import com.ipid.demo.services.impl.ValidationServiceImpl;
import com.ipid.demo.utils.DateUtils;
import com.ipid.demo.utils.NameUtils;
import com.ipid.demo.utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;

public class PayContact extends Fragment {
    private static final String TAG = "PayContactFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentPayContactBinding binding;
    private ValidationServiceImpl validationService;
    private Dialog dialog;
    private RecyclerView recyclerView;
    private List<ContactModel> contactModelList = new ArrayList<>();
    private ContactAdapter contactAdapter;

    // Arguments passed from previous screen, will be saved on the final screen
    private String requestType;
    private String amountFrom;
    private String amountTo;
    private String currencyFrom;
    private String currencyTo;
    private String exchangeRate;
    private String remarks;

    public PayContact() {
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
    public static PayContact newInstance(String param1, String param2) {
        PayContact fragment = new PayContact();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        validationService = new ValidationServiceImpl();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflater.inflate(R.layout.fragment_pay_contact, container, false);
        binding = FragmentPayContactBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dialog = new Dialog(getActivity());

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            PayContactArgs resultArgs = PayContactArgs.fromBundle(getArguments());
            requestType = resultArgs.getRequestType();
            amountFrom = resultArgs.getAmountFrom();
            amountTo = resultArgs.getAmountTo();
            currencyFrom = resultArgs.getCurrencyFrom();
            currencyTo = resultArgs.getCurrencyTo();
            exchangeRate = resultArgs.getExchangeRate();
            remarks = resultArgs.getRemarks();

            Log.i(TAG, "onViewCreated: " + requestType);
            Log.i(TAG, "onViewCreated: " + amountFrom);
            Log.i(TAG, "onViewCreated: " + amountTo);
            Log.i(TAG, "onViewCreated: " + currencyFrom);
            Log.i(TAG, "onViewCreated: " + currencyTo);
            Log.i(TAG, "onViewCreated: " + exchangeRate);
            Log.i(TAG, "onViewCreated: " + remarks);
        }

        recyclerView = getView().findViewById(R.id.recycle_view);

        if (contactAdapter != null) {
            contactAdapter.clearData();
        }

        checkPermission();
        initFilterContacts();
    }

    private void initFilterContacts() {
        EditText editTextSearch = getView().findViewById(R.id.editTextSearch);
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    private void filter(String text) {
        ArrayList<ContactModel> filteredList = new ArrayList<>();

        for (ContactModel contactModel : contactModelList) {
            if (contactModel.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(contactModel);
            }
        }

        contactAdapter.filterList(filteredList);
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, 100);
        } else {
            getContactList();
        }
    }

    private void getContactList() {
        getContactDetails();
        getContactEmails();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        contactAdapter = new ContactAdapter(contactModelList, contactModel -> {
            AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
            // Validate if user is registered in the system using the phone number
            if (!validationService.isRegisteredPhoneNumber(db, contactModel.getNumber())) {
                Log.i(TAG, "User with this number is not registered in the system.");
                showNonMemberDialog(contactModel);
            } else {
                showMemberDialog(contactModel);
            }
        });
        recyclerView.setAdapter(contactAdapter);
    }

    private void getContactDetails() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, sort);

        AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
        int customerId = ((MyApplication) this.getActivity().getApplication()).getId();
        Customer customer = db.customerDao().findById(customerId);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts._ID
                ));
                String name = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                ));
                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";
                Cursor phoneCursor = getContext().getContentResolver().query(uriPhone, null, selection, new String[]{id}, null);
                if (phoneCursor.moveToNext()) {
                    String number = phoneCursor.getString(phoneCursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    ));
                    // Exclude logged in user
                    if (!name.equalsIgnoreCase(NameUtils.getFullName(customer.firstName, customer.lastName)) && !number.equalsIgnoreCase(customer.phoneNumber)) {
                        ContactModel model = new ContactModel();
                        model.setName(name);
                        model.setNumber(number);
                        contactModelList.add(model);
                        phoneCursor.close();
                    }
                }
            }
            cursor.close();
        }
    }

    private void getContactEmails() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, sort);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts._ID
                ));
                String name = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                ));
                Uri uriPhone = ContactsContract.Data.CONTENT_URI;
                String selection = ContactsContract.Data.CONTACT_ID + " =?" + " AND " + "(" +  ContactsContract.Data.MIMETYPE + "='"
                        + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "' OR " + ContactsContract.Data.MIMETYPE
                        + "='" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE +"')";
                Cursor phoneCursor = getContext().getContentResolver().query(uriPhone, new String[] { ContactsContract.Data._ID, ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.CommonDataKinds.Photo.PHOTO }, selection, new String[]{id}, null);
                if (phoneCursor.moveToNext()) {
                    String email = phoneCursor.getString(phoneCursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Email.ADDRESS
                    ));
                    int index = findContactByName(name);
                    if (index != -1) {
                        contactModelList.get(index).setEmail(email);
                        phoneCursor.close();
                    }
                }
            }
            cursor.close();
        }
    }

    private int findContactByName(String name) {
        for (int i = 0; i < contactModelList.size(); i++) {
            if (contactModelList.get(i).getName().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    private void showMemberDialog(ContactModel contactModel) {
        dialog.setContentView(R.layout.popup_member_contact);

        ImageView closeBtn = dialog.findViewById(R.id.closePopupImg);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);
        TextView textViewPhone = dialog.findViewById(R.id.textViewPhone);
        TextView textViewNameContent = dialog.findViewById(R.id.textViewNameContent);
        TextView textViewAddress = dialog.findViewById(R.id.textViewAddress);
        TextView textViewAddressContent = dialog.findViewById(R.id.textViewAddressContent);

        AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
        Customer contact = db.customerDao().findByPhoneNumber(contactModel.getNumber());
        List<BankAccount> bankAccount = db.bankAccountDao().findByCustomerId(contact.id);

        textViewPhone.setText(contactModel.getNumber());
        textViewNameContent.setText(contactModel.getName());

        if (requestType.equalsIgnoreCase(RequestType.PAY.name())) {
            if (bankAccount.size() == 0) {
                textViewAddressContent.setText(EMPTY_FIELD);
            } else {
                textViewAddressContent.setText(bankAccount.get(0).country);
            }
        } else {
            textViewAddress.setVisibility(View.GONE);
            textViewAddressContent.setVisibility(View.GONE);
        }

        closeBtn.setOnClickListener(v -> {
            dialog.dismiss();
        });

        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();

            LookupCurrency lookupCurrencyFrom = db.lookupCurrencyDao().findByCurrency(currencyFrom);
            LookupCurrency lookupCurrencyTo = db.lookupCurrencyDao().findByCurrency(currencyTo);
            Customer customer = db.customerDao().findByPhoneNumber(contactModel.getNumber());

            if (requestType.equalsIgnoreCase(RequestType.PAY.name())) {
                PayContactDirections.ActionPayFragmentToResultFragment action = PayContactDirections.actionPayFragmentToResultFragment();
                action.setId(customer.id);
                action.setAmountFrom(amountFrom);
                action.setAmountTo(amountTo);
                action.setCurrencyFrom(currencyFrom);
                action.setCurrencyTo(currencyTo);   // based on preferred account
                action.setCountryFrom(lookupCurrencyFrom.country);
                action.setCountryTo(lookupCurrencyTo.country);
                action.setExchangeRate(exchangeRate);
                action.setRemarks(remarks);
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main).navigate(action);

            } else {
                PayContactDirections.ActionPayFragmentToPaySendFragment action = PayContactDirections.actionPayFragmentToPaySendFragment();
                action.setId(customer.id);
                action.setAmountFrom(amountFrom);
                action.setAmountTo(amountTo);
                action.setCurrencyFrom(currencyFrom);
                action.setCurrencyTo(currencyTo);
                action.setCountryFrom(lookupCurrencyFrom.country);
                action.setCountryTo(lookupCurrencyTo.country);
                action.setExchangeRate(exchangeRate);
                action.setRemarks(remarks);
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main).navigate(action);
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void showNonMemberDialog(ContactModel contactModel) {
        dialog.setContentView(R.layout.popup_non_member_contact);

        ImageView closeBtn = dialog.findViewById(R.id.closePopupImg);
        Button btnCopyLink = dialog.findViewById(R.id.btnCopyLink);
        Button btnSendInvitation = dialog.findViewById(R.id.btnSendInvitation);
        TextView textViewTitle = dialog.findViewById(R.id.textViewTitle);

        textViewTitle.setText(contactModel.getName() + NON_MEMBER_DIALOG_PREFIX);

        AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
        int id = ((MyApplication) this.getActivity().getApplication()).getId();

        closeBtn.setOnClickListener(v -> {
            dialog.dismiss();
        });

        btnCopyLink.setOnClickListener(v -> {
            Customer customerFrom = db.customerDao().findById(id);
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText("You have been invited by " + NameUtils.getFullName(customerFrom.firstName, customerFrom.lastName) + PERIOD + " Please sign up on iPiD.");

            Toast.makeText(getActivity(), "Invite link copied!", Toast.LENGTH_SHORT).show();
        });

        btnSendInvitation.setOnClickListener(v -> {
            if (btnSendInvitation.getText().toString().equalsIgnoreCase(Constants.SEND_INVITE)) {
                btnSendInvitation.setText(CONTINUE);
                Toast.makeText(getActivity(), "Invitation sent!", Toast.LENGTH_SHORT).show();

                // Save invitation notification (Sender only since it's a non-member)
                Notification notification = new Notification();
                notification.customerFrom = id;
                notification.customerTo = id;
                notification.description = INVITATION_PREFIX + contactModel.getName() + PERIOD;
                notification.type = RequestType.INVITATION.name();
                notification.status = true;
                notification.pending = false;
                notification.resent = false;
                notification.createdDate = DateUtils.getDateTime();
                db.notificationDao().insertNotification(notification);

            } else {
                dialog.dismiss();

                // Register the non-member customer in database for foreign key constraints
                LookupCurrency lookupCurrencyFrom = db.lookupCurrencyDao().findByCurrency(currencyFrom);
                LookupCurrency lookupCurrencyTo = db.lookupCurrencyDao().findByCurrency(currencyTo);

                Customer customer = new Customer();
                customer.firstName = NameUtils.getFirstName(contactModel.getName());
                customer.lastName = NameUtils.getLastName(contactModel.getName());
                customer.phoneNumber = contactModel.getNumber();
                customer.emailAddress = contactModel.getEmail();
                customer.status = true;

                db.customerDao().insertCustomer(customer);
                Customer savedCustomer = db.customerDao().findNonMemberByPhone(customer.phoneNumber);
                Customer customerFrom = db.customerDao().findById(id);
                BankAccount preferredBankAccount = db.bankAccountDao().findPreferredBank(id);

                if (requestType.equalsIgnoreCase(RequestType.PAY.name())) {
                    // Save payment
                    Payment payment = new Payment();
                    payment.customerFrom = id;
                    payment.customerTo = savedCustomer.id;
                    payment.remarks = remarks;
                    payment.status = true;
                    payment.createdDate = DateUtils.getDateTime();
                    db.paymentDao().insertPayment(payment);
                    payment = db.paymentDao().findLatestPaymentByCustomer(id);

                    // Save payment details
                    PaymentDetails paymentDetails = new PaymentDetails();
                    paymentDetails.paymentId = payment.id;
                    paymentDetails.bankAccountFrom = preferredBankAccount.id;
                    paymentDetails.bankAccountTo = DEFAULT_BANK_ACCOUNT_ID; // To be populated by recipient under pending list
                    paymentDetails.currencyFrom = lookupCurrencyFrom.id;
                    paymentDetails.currencyTo = lookupCurrencyTo.id;
                    paymentDetails.amountFrom = Double.parseDouble(amountFrom);;
                    paymentDetails.amountTo = Double.parseDouble(amountTo);
                    paymentDetails.exchangeRate = Double.parseDouble(exchangeRate);
                    db.paymentDetailsDao().insertPaymentDetails(paymentDetails);

                    // Save notification
                    // Receiver notification
                    Notification notification = new Notification();
                    notification.customerFrom = id;
                    notification.customerTo = savedCustomer.id;
                    notification.paymentId = payment.id;
                    notification.description = NameUtils.getFullName(customerFrom.firstName, customerFrom.lastName) + PAY_RECEIVER_NOTIFICATION_PREFIX;
                    notification.type = RequestType.PAY.name();
                    notification.status = true;
                    notification.pending = true;
                    notification.resent = false;
                    notification.createdDate = DateUtils.getDateTime();
                    db.notificationDao().insertNotification(notification);

                    // Sender notification
                    notification = new Notification();
                    notification.customerFrom = id;
                    notification.customerTo = id;
                    notification.paymentId = payment.id;
                    notification.description = PAY_SENDER_ASK_DETAILS_NOTIFICATION_PREFIX + contactModel.getName() + " and asked for the banking details.";
                    notification.type = RequestType.PAY.name();
                    notification.status = true;
                    notification.pending = true;
                    notification.resent = false;
                    notification.createdDate = DateUtils.getDateTime();
                    db.notificationDao().insertNotification(notification);

                    PayContactDirections.ActionPayFragmentToTransitPaymentFragment action = PayContactDirections.actionPayFragmentToTransitPaymentFragment();
                    action.setMessage(PAY_NON_MEMBER);
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main).navigate(action);

                } else {
                    PayContactDirections.ActionPayFragmentToPaySendFragment action = PayContactDirections.actionPayFragmentToPaySendFragment();
                    action.setId(savedCustomer.id);
                    action.setAmountFrom(amountFrom);
                    action.setAmountTo(amountTo);
                    action.setCurrencyFrom(currencyFrom);
                    action.setCurrencyTo(currencyTo);
                    action.setCountryFrom(lookupCurrencyFrom.country);
                    action.setCountryTo(lookupCurrencyTo.country);
                    action.setExchangeRate(exchangeRate);
                    action.setRemarks(remarks);
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main).navigate(action);
                }
            }
       });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getContactList();
        } else {
            Toast.makeText(getActivity(), "Permission Denied.", Toast.LENGTH_SHORT).show();
            checkPermission();
        }
    }
}
