package autocrew.akd.enterntainment;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mukesh.OtpView;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class OTPFragment extends Fragment {


    public OTPFragment() {
        // Required empty public constructor
    }

    private Button verifyBtn;
    private OtpView otpView;
    private TextView resendBtn;
    private String fullName, email, phone, password;
    private LinearLayout goBackBtn;

    private Timer timer;
    private int count = 60;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private Dialog loadingDialog;

    public OTPFragment(String fullName, String email, String phone, String password) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ot, container, false);


        //        Loading Dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //        Loading Dialog

        verifyBtn = view.findViewById(R.id.verifyBtn);
        otpView = view.findViewById(R.id.otp_view);
        resendBtn = view.findViewById(R.id.resend_otp_btn_textview);
        goBackBtn = view.findViewById(R.id.go_back_textview);

        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new LoginFragment(), MainActivity.LOGIN_FRAGMENT);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        sendOtp();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (count == 0) {
                            resendBtn.setText(" Resend OTP");
                            resendBtn.setEnabled(true);
                            resendBtn.setAlpha(1f);
                        } else {
                            resendBtn.setText(" Resend in " + count);
                            count--;
                        }
                    }
                });
            }
        }, 0, 1000);

        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resentOtp();
                resendBtn.setEnabled(false);
                resendBtn.setAlpha(0.5f);
                count = 60;
            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otpView.getText() == null || otpView.getText().toString().isEmpty()) {
                    return;
                }
                loadingDialog.show();
                otpView.setError(null);
                String code = otpView.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                signInWithPhoneAuthCredential(credential, loadingDialog);

            }
        });

        return view;
    }

    public void sendOtp() {

        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    otpView.setError(e.getMessage());
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    otpView.setError(e.getMessage());
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phone
                , 60
                , TimeUnit.SECONDS
                , getActivity()
                , mCallback
        );
    }

    private void resentOtp() {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phone
                , 60
                , TimeUnit.SECONDS
                , getActivity()
                , mCallback
                , mResendToken
        );

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential, final Dialog loadingDialog) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            AuthCredential authCredential = EmailAuthProvider.getCredential(email, password);
                            user.linkWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Map<String, Object> userData = new HashMap<>();
                                        String[] splittedNameList = fullName.trim().split(" ");
                                        if (splittedNameList.length == 2) {
                                            userData.put("first_name", splittedNameList[0]);
                                            userData.put("last_name", splittedNameList[1]);
                                        } else {
                                            userData.put("first_name", splittedNameList[0]);
                                            userData.put("last_name", "");
                                        }
                                        userData.put("phone_no", "+91" + phone);
                                        userData.put("email", email);
                                        userData.put("DOB", "dd/mm/yyyy");
                                        userData.put("user_id", firebaseAuth.getCurrentUser().getUid());
                                        userData.put("user_name", (fullName.trim().replace(" ", "").toLowerCase() + String.valueOf(GoogleSignInSystem.gen())));
                                        userData.put("user_profile_pic", "");
                                        firebaseFirestore
                                                .collection("USERS")
                                                .document(firebaseAuth.getUid())
                                                .set(userData)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            loadingDialog.dismiss();
                                                            Toast.makeText(getContext(), "Sign Up phone Successful!", Toast.LENGTH_SHORT).show();
                                                            MainActivity.fromGoogle = false;
                                                            Intent appMainActivityIntent = new Intent(getContext(), AppMainActivity.class);
                                                            getContext().startActivity(appMainActivityIntent);
                                                            ((Activity) getContext()).finish();
                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        loadingDialog.dismiss();
                                        String error = task.getException().getMessage();
                                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                otpView.setError("Invalid OTP");
                                loadingDialog.dismiss();
                            }
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    private void setFragment(Fragment fragment, int currentPosition) {
        MainActivity.currentFragment = currentPosition;
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slideout_from_right);
        fragmentTransaction.replace(MainActivity.frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }
}
