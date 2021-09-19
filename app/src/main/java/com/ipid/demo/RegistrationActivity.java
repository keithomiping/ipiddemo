package com.ipid.demo;

import static com.ipid.demo.constants.Constants.CUSTOMER_ID_PREFIX;
import static com.ipid.demo.constants.Constants.DEFAULT_PASSWORD;
import static com.ipid.demo.constants.Constants.EMAIL_ALREADY_EXISTS;
import static com.ipid.demo.constants.Constants.INVALID_EMAIL;
import static com.ipid.demo.constants.Constants.INVALID_FIRST_NAME;
import static com.ipid.demo.constants.Constants.INVALID_LAST_NAME;
import static com.ipid.demo.constants.Constants.INVALID_PASSWORD;
import static com.ipid.demo.constants.Constants.INVALID_PHONE_NUMBER;
import static com.ipid.demo.constants.Constants.PHONE_NUMBER_ALREADY_EXISTS;
import static com.ipid.demo.constants.Constants.RC_SIGN_IN;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.hbb20.CountryCodePicker;
import com.ipid.demo.db.AppDatabase;
import com.ipid.demo.db.entity.Customer;
import com.ipid.demo.services.impl.ValidationServiceImpl;
import com.ipid.demo.utils.RandomUtils;

import org.json.JSONObject;

import java.util.Arrays;

public class RegistrationActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "RegistrationActivity";

    private ValidationServiceImpl validationService;
    private CallbackManager callbackManager;
    private ConstraintLayout btnGoogle;
    private ConstraintLayout btnFacebook;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        validationService = new ValidationServiceImpl();

        configureGoogle();
        configureFacebook();
        initRegisterButton();
    }

    private void initRegisterButton() {
        EditText firstName = findViewById(R.id.textViewFirstName);
        EditText lastName = findViewById(R.id.textViewLastName);
        CountryCodePicker ccp = findViewById(R.id.ccp);
        EditText phoneNumber = findViewById(R.id.textViewPhoneNumber);
        EditText email = findViewById(R.id.textViewEmail);
        EditText password  = findViewById(R.id.textViewPassword);
        Button registerButton = findViewById(R.id.buttonProfileSignup);

        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());

        registerButton.setOnClickListener(v -> {
            if (!validate()) {
                return;
            }
            String fullNumber = ccp.getSelectedCountryCodeWithPlus() + " " + phoneNumber.getText().toString();

            if (validationService.isRegisteredPhoneNumber(db, fullNumber)) {
                Toast.makeText(RegistrationActivity.this, PHONE_NUMBER_ALREADY_EXISTS, Toast.LENGTH_SHORT).show();
            } else if (validationService.isRegisteredEmail(db, email.getText().toString())) {
                Toast.makeText(RegistrationActivity.this, EMAIL_ALREADY_EXISTS, Toast.LENGTH_SHORT).show();
            } else {
                saveNewCustomer(firstName.getText().toString(), lastName.getText().toString(), fullNumber, email.getText().toString(), password.getText().toString());
            }
        });
    }

    private void configureGoogle() {
        btnGoogle = findViewById(R.id.btnGoogle);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        btnGoogle.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    private void configureFacebook() {
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "Facebook sign in successful.");
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Facebook sign in cancelled.");
                // Toast.makeText(RegistrationActivity.this, "Facebook sign in cancelled.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "Facebook sign in failed.");
                // Toast.makeText(RegistrationActivity.this, "Facebook sign in failed.", Toast.LENGTH_SHORT).show();
            }
        });

        btnFacebook = findViewById(R.id.btnFacebook);
        btnFacebook.setOnClickListener(v -> {
            LoginManager.getInstance().logInWithReadPermissions(RegistrationActivity.this, Arrays.asList("public_profile", "user_friends"));
        });
    }

    private boolean validate() {
        EditText firstName = findViewById(R.id.textViewFirstName);
        EditText lastName = findViewById(R.id.textViewLastName);
        EditText phoneNumber = findViewById(R.id.textViewPhoneNumber);
        EditText email = findViewById(R.id.textViewEmail);
        EditText password  = findViewById(R.id.textViewPassword);

        if (firstName.getText().toString().isEmpty()) {
            Toast.makeText(RegistrationActivity.this, INVALID_FIRST_NAME, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (lastName.getText().toString().isEmpty()) {
            Toast.makeText(RegistrationActivity.this, INVALID_LAST_NAME, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (phoneNumber.getText().toString().isEmpty()) {
            Toast.makeText(RegistrationActivity.this, INVALID_PHONE_NUMBER, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (email.getText().toString().isEmpty()) {
            Toast.makeText(RegistrationActivity.this, INVALID_EMAIL, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.getText().toString().isEmpty()) {
            Toast.makeText(RegistrationActivity.this, INVALID_PASSWORD, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveNewCustomer(String firstName, String lastName, String phoneNumber, String email, String password) {
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());

        Customer customer = new Customer();
        customer.customerId = CUSTOMER_ID_PREFIX + RandomUtils.getRandomNumberString();
        customer.firstName = firstName;
        customer.lastName = lastName;
        customer.phoneNumber = phoneNumber;
        customer.emailAddress = email;
        customer.password = password;
        customer.status = true;

        // Save and flush data in DB
        db.customerDao().insertCustomer(customer);
        finish();

        // Redirect to splash screen after successful registration
        startActivity(new Intent(this, SplashActivity.class));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data); // For Facebook

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            handleSaveAccount(account);
        } catch(ApiException e) {
            Log.e(TAG, "handleSignInResult: " + e.getStatusCode());
        }
    }

    AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
        if (currentAccessToken == null) {
            // Logged out user
        } else {
            loginUserProfile(currentAccessToken);
        }
        }
    };

    private void loginUserProfile(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                // TODO: Facebook sign up
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void handleSaveAccount(GoogleSignInAccount account) {
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());

        if (account != null) {
            Log.i(TAG, "handleSignInResult: " + account.getDisplayName());
            Log.i(TAG, "handleSignInResult: " + account.getGivenName());
            Log.i(TAG, "handleSignInResult: " + account.getFamilyName());
            Log.i(TAG, "handleSignInResult: " + account.getEmail());

            Customer customer = new Customer();
            customer.customerId = CUSTOMER_ID_PREFIX + RandomUtils.getRandomNumberString();
            customer.firstName = account.getGivenName();
            customer.lastName = account.getFamilyName();
            customer.emailAddress = account.getEmail();
            customer.password = DEFAULT_PASSWORD;
            customer.status = true;

            Customer savedCustomer = db.customerDao().findByEmail(customer.emailAddress);
            if (savedCustomer == null) {
                // Save in DB if user is not registered
                db.customerDao().insertCustomer(customer);
                Customer registeredCustomer = db.customerDao().findByEmail(customer.emailAddress);
                ((MyApplication) this.getApplication()).setId(registeredCustomer.id);
            } else {
                savedCustomer.firstName = customer.firstName;
                savedCustomer.lastName = customer.lastName;
                db.customerDao().update(savedCustomer);
                ((MyApplication) this.getApplication()).setId(savedCustomer.id);
            }
            // Redirect to home screen
            startActivity(new Intent(this, TransitRegistration.class));
        }
    }
}