package autocrew.akd.enterntainment;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.regex.Pattern;


public class LoginFragment extends Fragment {


    public LoginFragment() {
        // Required empty public constructor
    }

    private FirebaseAuth firebaseAuth;
    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 1;

    private TextInputEditText emailInputEditText;
    private TextInputEditText passwordInputEditText;
    private Button logInBtn;
    private TextView forgotPasswordBtn;

    private Dialog loadingDialog;

    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        ImageView carIconImageView = view.findViewById(R.id.car_icon_imageview);
        Glide.with(getContext())
                .load(R.drawable.hi_icon)
                .into(carIconImageView);

        //        Loading Dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        ImageView dialogImage = loadingDialog.findViewById(R.id.load_image);
        Glide.with(this)
                .load(R.drawable.loading_gif)
                .into(dialogImage);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //        Loading Dialog

        firebaseAuth = FirebaseAuth.getInstance();
        emailInputEditText = view.findViewById(R.id.email);
        passwordInputEditText = view.findViewById(R.id.password);
        logInBtn = view.findViewById(R.id.login_btn);
        forgotPasswordBtn = view.findViewById(R.id.forgot_password_textView_btn);

        forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new ForgotPasswordFragment(), MainActivity.FORGOT_PASSWORD_FRAGMENT);
            }
        });

        TextView signUpBtnTextView = view.findViewById(R.id.signup_btn_textview);
        signUpBtnTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.fromWalkThrough = false;
                setFragment(new SignUpFragment(), MainActivity.SIGNUP_FRAGMENT);
            }
        });

        //Google Authentication
        ConstraintLayout loginWithGoogleBtn = view.findViewById(R.id.loginWithGoogleBtn);
        loginWithGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        //Google Authentication

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        passwordInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailInputEditText.setError(null);
                passwordInputEditText.setError(null);

                if (emailInputEditText.getText().toString().trim().isEmpty()) {
                    emailInputEditText.setError("Required");
                    return;
                }
                if (passwordInputEditText.getText().toString().trim().isEmpty()) {
                    passwordInputEditText.setError("Required");
                    return;
                }
                if (VALID_EMAIL_ADDRESS_REGEX.matcher(emailInputEditText.getText().toString().trim()).find()) {
                    loadingDialog.show();
                    logInWithEmailAndPassword(emailInputEditText.getText().toString(), passwordInputEditText.getText().toString());
                } else if (emailInputEditText.getText().toString().matches("\\d{10}")) {
                    loadingDialog.show();
                    FirebaseFirestore.getInstance().collection("USERS").whereEqualTo("phone_no", "+91" + emailInputEditText.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                                        if (documentSnapshots.isEmpty()) {
                                            emailInputEditText.setError("Phone not found!");
                                            loadingDialog.dismiss();
                                            return;
                                        } else {
                                            String email = documentSnapshots.get(0).get("email").toString();
                                            logInWithEmailAndPassword(email, passwordInputEditText.getText().toString());
                                        }
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    emailInputEditText.setError("Please enter a valid Email or Phone!");
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (MainActivity.fromGoogle) {
            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                GoogleSignInSystem.handleSignInResult(result, firebaseAuth, getContext(), loadingDialog);
            }
        }
    }

    private void setFragment(Fragment fragment, int currentPosition) {
        MainActivity.currentFragment = currentPosition;
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slideout_from_left);
        fragmentTransaction.replace(MainActivity.frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void signIn() {
        MainActivity.fromGoogle = true;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("65125700011-nhkvqn50canfjk4hm7j68d1o560kqa5v.apps.googleusercontent.com")//you can also use R.string.default_web_client_id
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage((FragmentActivity) getContext(), new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getContext(), "Connection Failed!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, RC_SIGN_IN);
    }

    private void logInWithEmailAndPassword(String email, String password) {
        if (VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()) {
            if (password.length() >= 8) {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseFirestore.getInstance().collection("USERS").document(firebaseAuth.getCurrentUser().getUid())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (getContext() != null) {
                                                            Toast.makeText(getContext(), "Welcome Back, " + document.get("first_name").toString(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                    MainActivity.fromGoogle = false;
                                    Intent appMainActivityIntent = new Intent(getContext(), AppMainActivity.class);
                                    getContext().startActivity(appMainActivityIntent);
                                    ((Activity) getContext()).finish();
                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                }
                                loadingDialog.dismiss();
                            }
                        });
            } else {
                Toast.makeText(getActivity(), "Incorrect email or password!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Invalid Email!", Toast.LENGTH_SHORT).show();
            emailInputEditText.setError("Invalid Email!");
        }
    }

    private void checkInputs() {
        if (!TextUtils.isEmpty(emailInputEditText.getText())) {
            if (!TextUtils.isEmpty(passwordInputEditText.getText())) {
                logInBtn.setEnabled(true);
                logInBtn.setTextColor(getResources().getColor(R.color.colorAccent));
            } else {
                logInBtn.setEnabled(false);
                logInBtn.setTextColor(Color.argb(50, 255, 255, 255));
            }
        } else {
            logInBtn.setEnabled(false);
            logInBtn.setTextColor(Color.argb(50, 255, 255, 255));
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (MainActivity.fromGoogle) {
            if (googleApiClient != null) {
                googleApiClient.stopAutoManage(getActivity());
                googleApiClient.disconnect();
            }
        }
    }

}
