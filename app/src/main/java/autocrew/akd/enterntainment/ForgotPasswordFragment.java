package autocrew.akd.enterntainment;


import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;


public class ForgotPasswordFragment extends Fragment {


    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    private TextInputEditText email;
    private Button sendBtn;
    private FirebaseAuth firebaseAuth;
    private Dialog loadingDialog;
    private ViewGroup emailIconContainer;
    private TextView emailIconText;
    private ProgressBar progressBar;
    private ImageView emailIcon;
    private LinearLayout goBackBtn;

    private Timer timer;
    private boolean isTimerCancelled = false;
    private int count = 60;

    private final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        //        Loading Dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //        Loading Dialog

        firebaseAuth = FirebaseAuth.getInstance();


        email = view.findViewById(R.id.email);
        sendBtn = view.findViewById(R.id.send_btn);
        emailIconContainer = view.findViewById(R.id.forgot_password_email_icon_container);
        emailIconText = view.findViewById(R.id.forgot_password_email_icon_text);
        emailIcon = view.findViewById(R.id.forgot_password_email_icon);
        progressBar = view.findViewById(R.id.forgot_password_progressbar);
        goBackBtn = view.findViewById(R.id.go_back_textview);

        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new LoginFragment(), MainActivity.LOGIN_FRAGMENT);
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!email.getText().toString().trim().equals("")) {
                    if (VALID_EMAIL_ADDRESS_REGEX.matcher(email.getText().toString()).find()) {
                        cancelTimer(timer);
                        timer = new Timer();
                        isTimerCancelled = false;
                        timer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (count == 0) {
                                            sendBtn.setText("Resend Email");
                                            sendBtn.setEnabled(true);
                                            sendBtn.setTextColor(Color.rgb(255, 255, 255));
                                        } else {
                                            sendBtn.setText("Resend in " + count + "s");
                                            count--;
                                        }
                                    }
                                });
                            }
                        }, 0, 1000);

                        TransitionManager.beginDelayedTransition(emailIconContainer);
                        emailIconText.setVisibility(View.GONE);
                        TransitionManager.beginDelayedTransition(emailIconContainer);
                        emailIcon.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        resetPassword(email.getText().toString());

                        sendBtn.setEnabled(false);
                        sendBtn.setTextColor(Color.argb(50, 255, 255, 255));
                        count = 60;
                    } else {
                        email.setError("Invalid Email!");
                        emailIconText.setText("Invalid Email!");
                        emailIcon.setVisibility(View.GONE);
                    }
                } else {
                    email.setError("Enter an Email!");
                    emailIcon.setVisibility(View.GONE);
                    emailIconText.setWidth(600);
                    emailIconText.setText("Enter an Email!");
                    emailIconText.setTextColor(Color.parseColor("#FF0000"));
                    TransitionManager.beginDelayedTransition(emailIconContainer);
                    emailIconText.setVisibility(View.VISIBLE);
                }
            }
        });

        return view;
    }

    private void resetPassword(String emailAddress) {
        firebaseAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    ScaleAnimation scaleAnimation = new ScaleAnimation(1, 0, 1, 0, emailIcon.getWidth() / 2, emailIcon.getHeight() / 2);
                    scaleAnimation.setDuration(100);
                    scaleAnimation.setInterpolator(new AccelerateInterpolator());
                    scaleAnimation.setRepeatMode(Animation.REVERSE);
                    scaleAnimation.setRepeatCount(1);

                    scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            emailIconText.setWidth(600);
                            emailIconText.setText("Recovery email sent successfully ! check your inbox");
                            emailIconText.setTextColor(Color.parseColor("#11A10C"));
                            TransitionManager.beginDelayedTransition(emailIconContainer);
                            emailIconText.setVisibility(View.VISIBLE);
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            emailIcon.setImageResource(R.drawable.green_email);
                        }
                    });
                    emailIcon.startAnimation(scaleAnimation);
                } else {
                    cancelTimer(timer);
                    sendBtn.setText("Send");
                    sendBtn.setTextColor(Color.rgb(255, 255, 255));
                    sendBtn.setEnabled(true);
                    String error = task.getException().getMessage();
                    emailIconText.setWidth(600);
                    emailIconText.setText(error.toString());
                    emailIconText.setTextColor(Color.parseColor("#FF0000"));
                    TransitionManager.beginDelayedTransition(emailIconContainer);
                    emailIconText.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            isTimerCancelled = true;
        }
    }

    private void cancelTimer(Timer timer) {
        if (!isTimerCancelled) {
            if (timer != null) {
                timer.cancel();
                isTimerCancelled = true;
            }
        }
    }

    private void setFragment(Fragment fragment, int currentPosition) {
        MainActivity.currentFragment = currentPosition;
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slideout_from_right);
        fragmentTransaction.replace(MainActivity.frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

}
