package com.ipid.demo.constants;

import java.text.DecimalFormat;

public class Constants {

    public static final String EMPTY_FIELD = "-";
    public static final String EMPTY_STRING = "";
    public static final String SPACE = " ";
    public static final String PLUS = " ";
    public static final String UNDERSCORE = "_";
    public static final String DRAWABLE_TYPE = "drawable";
    public static final String NEW_LINE = "\n";
    public static final String PERIOD = ".";
    public static final String COMMA = ",";
    public static final String ZERO = "0";
    public static final String HTML_PERIOD = "&#9679;";

    // Google Places API key
    public static final String GOOGLE_PLACES_API_KEY = "SPECIFY KEY HERE";

    // Google Sign in
    public static final int RC_SIGN_IN = 111;

    // Biometrics
    public static final int DEFAULT_LOGIN_USER_ID = 2; // Adrian Baery
    public static final int DEFAULT_LOGOUT_USER_ID = 0;
    public static final String DEFAULT_PASSWORD = "ipiddemo0701";

    // Compare screen
    public static final double INITIAL_VALUE = 100D;
    public static final String DEFAULT_CURRENCY_AUD = "AUD";
    public static final String DEFAULT_CURRENCY_SGD = "SGD";
    public static final int DEFAULT_SELECTED_CURRENCY = 3; // SGD
    public static final int DEFAULT_SELECTED_CURRENCY_SELECTED = 0; // AUD

    // Bank Account screens
    public static final int ACCOUNT_ROW_HEIGHT = 80;
    public static final int MAX_SCROLL_HEIGHT = 400;

    // This is used to indicate if the notification is from the sender
    // Sender and receiver ID is the same
    public static final int TRANSACTION_SENDER_ID = -1;

    // Pay screen
    public static final int DEFAULT_BANK_ACCOUNT_ID = 1;

    // Buttons
    public static final String CLOSE = "Close";
    public static final String SAVE = "Save";
    public static final String NEXT = "Next";
    public static final String EDIT = "Edit";
    public static final String CONTINUE = "Continue";
    public static final String SEND_INVITE = "Send invite";
    public static final String ADD_ACCOUNT = "Add account";
    public static final String RESEND = "Resend";
    public static final String SHARE = "Share";
    public static final String PROCEED = "Proceed";

    // Arguments
    public static final String ACCOUNT_SETUP = "accountSetup";
    public static final String DEFAULT_ARGS_VALUE = "default";
    public static final int DEFAULT_NOTIFICATION_ID = 0;

    // Fragment tags
    public static final String PROFILE_ABOUT_ME = "PROFILE_ABOUT_ME";
    public static final String MY_ACCOUNTS = "MY_ACCOUNTS";
    public static final String MY_REWARDS = "MY_REWARDS";
    public static final String ABOUT_ME_ALIASES = "ABOUT_ME_ALIASES";
    public static final String ABOUT_ME_DETAILS = "ABOUT_ME_DETAILS";

    // Date
    public static final DecimalFormat REAL_FORMATTER = new DecimalFormat("0.00");
    public static final String YYYYMMDDHHmmss = "yyyy/MM/dd HH:mm:ss";
    public static final String EEEdMMMyyyy = "EEE, d MMM yyyy";
    public static final String EEEdMMMyyyyHHmm = "EEE, d MMM yyyy HH:mm";

    // Randomizer
    public static final String CUSTOMER_ID_PREFIX = "C";
    public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static final int LENGTH = 6;
    public static final int BOUND = 999999;

    // Static texts
    public static final String PREFERRED_ACCOUNT = "(Preferred)";
    public static final String REQUEST = "You request";
    public static final String PAY = "Recipient gets";
    public static final String INVITATION_PREFIX = "You have sent an invitation to ";
    public static final String MEMBER_DIALOG_PREFIX = " is an iPiD member.\nPhone Number: ";
    public static final String NON_MEMBER_DIALOG_PREFIX = " is not registered yet.";
    public static final String NON_MEMBER_DIALOG_INVITATION_PREFIX = "Share Invitation: https://www.ipid.com/invite/";
    public static final String PAY_RECEIVER_NOTIFICATION_PREFIX = " wants to pay you and is asking for your banking details.";
    public static final String PAY_RECEIVER_UPDATE_DETAILS_NOTIFICATION_PREFIX = " has updated their banking details. You may now proceed with your payment.";
    public static final String PAY_SENDER_UPDATE_DETAILS_NOTIFICATION_PREFIX = "You have updated your banking details.";
    public static final String PAY_SENDER_NOTIFICATION_PREFIX = "You have made a payment to ";
    public static final String PAY_SENDER_ASK_DETAILS_NOTIFICATION_PREFIX = "You wanted to pay ";
    public static final String GET_PAID_RECEIVER_NOTIFICATION_PREFIX = " has sent you a payment.";
    public static final String PAY_DETAILS_PREFIX = "We are working on it! Your payment provider is not yet integrated with us.\n\nIn the meantime, you can copy John’s details in your banking app.\n";
    public static final String REQUEST_TO_PAY_PREFIX = " has sent you a\nrequest to pay ";
    public static final String REQUEST_TO_PAY_SENDER_PREFIX = " has sent you a request to pay.";
    public static final String REQUEST_TO_PAY_RECEIVER_PREFIX = "You have requested a payment to ";
    public static final String MARKET_EXCHANGE_PREFIX = "Mid\nmarket,\n";
    public static final String FIRST_BANNER_TITLE = "Pay anywhere";
    public static final String FIRST_BANNER_SUB_TITLE = "Select your beneficiary's phone number or email address and the amount you want to send";
    public static final String SECOND_BANNER_TITLE = "Pay from your existing accounts";
    public static final String SECOND_BANNER_SUB_TITLE = "Use the institutions that you trust and where you already have an account to send money";
    public static final String THIRD_BANNER_TITLE = "Get paid from anywhere";
    public static final String THIRD_BANNER_SUB_TITLE = "Generate your unique payment link, share with anyone based on their phone number or email address";
    public static final String FOURTH_BANNER_TITLE = "Manage your banking details";
    public static final String FOURTH_BANNER_SUB_TITLE = "Save and organize your account details across multiple organizations";
    public static final String PAY_NON_MEMBER = "We will notify you when the recipient details are updated.";
    public static final String ACTION_SENT = "Sent";
    public static final String ACTION_RECEIVED = "Received";
    public static final String ADD_ACCOUNT_TITLE = "Add account to get paid";
    public static final String BANK_ACCOUNTS_TITLE = "My Bank accounts (Name, AC, BIC)";
    public static final String FINALIZE_PAY_TITLE = "Finalize the payment in your bank or payment provider app";

    // Validation
    public static final String INVALID_FIRST_NAME = "Please specify first name.";
    public static final String INVALID_LAST_NAME = "Please specify last name.";
    public static final String INVALID_NAME = "Please specify valid name.";
    public static final String INVALID_ADDRESS = "Please specify address.";
    public static final String INVALID_ACCOUNT_NUMBER = "Please specify account number.";
    public static final String INVALID_PHONE_NUMBER = "Please specify phone number.";
    public static final String INVALID_MOBILE_NUMBER = "Please specify mobile number.";
    public static final String INVALID_EMAIL = "Please specify email address.";
    public static final String INVALID_PASSWORD = "Please specify password.";
    public static final String INVALID_BANK_ACCOUNT = "Please specify bank account.";
    public static final String INVALID_AMOUNT = "Please specify amount.";
    public static final String EMPTY_BANK_ACCOUNT = "Please add at least one (1) account.";
    public static final String EMAIL_ALREADY_EXISTS = "Email already taken.";
    public static final String PHONE_NUMBER_ALREADY_EXISTS = "Phone number already taken.";
}
