package com.ipid.demo;

import static com.ipid.demo.constants.Constants.CUSTOMER_ID_PREFIX;
import static com.ipid.demo.constants.Constants.DEFAULT_LOGIN_USER_ID;
import static com.ipid.demo.constants.Constants.DEFAULT_PASSWORD;
import static com.ipid.demo.constants.Constants.RC_SIGN_IN;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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
import com.ipid.demo.db.AppDatabase;
import com.ipid.demo.db.entity.Customer;
import com.ipid.demo.utils.RandomUtils;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "LoginActivity";

    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private Executor executor;

    private CallbackManager callbackManager;
    private ConstraintLayout btnGoogle;
    private ConstraintLayout btnFacebook;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initBiometrics();
        configureGoogle();
        configureFacebook();
    }

    private void initBiometrics() {
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                // Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                // Set email in application context
                ((MyApplication) getApplication()).setId(DEFAULT_LOGIN_USER_ID); // Adrian Baery
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                // Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                // Toast.makeText(LoginActivity.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Sign in")
                .setDescription("Confirm fingerprint to continue")
                .setNegativeButtonText("CANCEL")
                .setConfirmationRequired(false)
                .build();
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
            LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "user_friends"));
        });
    }

    public void onClickLogin(View view) {
        final EditText email = findViewById(R.id.textViewEmail);
        final EditText password = findViewById(R.id.textViewPassword);
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        Customer customer = db.customerDao().findByEmailAndPassword(email.getText().toString(), password.getText().toString());

        if (customer == null) {
            final TextView errorTextView = findViewById(R.id.textViewError);
            errorTextView.setVisibility(View.VISIBLE);
        } else {
            // Set email in application context
            ((MyApplication) this.getApplication()).setId(customer.id);
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    public void authenticate(View view) {
        BiometricManager biometricManager = BiometricManager.from(this);
        if (biometricManager.canAuthenticate() != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(LoginActivity.this, "Biometric Not Supported", Toast.LENGTH_SHORT).show();
            return;
        }

        biometricPrompt.authenticate(promptInfo);
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
            handleLoginAccount(account);
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
        GraphRequest request = GraphRequest.newMeRequest(accessToken, (object, response) -> {
            // TODO: Facebook sign in
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void handleLoginAccount(GoogleSignInAccount account) {
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
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}