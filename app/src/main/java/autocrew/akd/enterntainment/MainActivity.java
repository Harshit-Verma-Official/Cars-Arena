package autocrew.akd.enterntainment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    public static boolean IS_AUTHENTICATION_SKIPPED;
    public static int currentFragment = 0;
    public static FrameLayout frameLayout;
    public static final int WALKTHROUGH_FRAGMENT = 0;
    public static final int LOGIN_FRAGMENT = 1;
    public static final int SIGNUP_FRAGMENT = 2;
    public static final int FORGOT_PASSWORD_FRAGMENT = 3;
    public static final int OTP_FRAGMENT = 4;
    public static final int EDITPROFILE_FRAGMENT = 5;
    public static final int SPLASH_FRAGMENT = 6;
    public static boolean fromWalkThrough = false;
    public static GoogleSignInAccount currentGoogleAccount;

    public static boolean fromGoogle = false;
    public static boolean fromAppMainActivity = false;
    public static boolean fromEditProfile = false;
    public static boolean isAppMainActivityOpen = false;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private final int SPLASH_TIME_OUT = 1500;

    public static Typeface fontCocoBiker;
    public static Typeface fontEarthOrbiterBold;
    public static Typeface fontCircularPro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fontCocoBiker = Typeface.createFromAsset(getAssets(), "fonts/CocoBiker_Regular.ttf");
        fontEarthOrbiterBold = Typeface.createFromAsset(getAssets(), "fonts/EarthOrbiterExtraBold.otf");
        fontCircularPro = Typeface.createFromAsset(getAssets(), "fonts/Circular Pro.otf");

        firebaseAuth = FirebaseAuth.getInstance();

        frameLayout = findViewById(R.id.main_framelayout);

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        IS_AUTHENTICATION_SKIPPED = sharedPreferences.getBoolean("IS_AUTHENTICATION_SKIPPED", false);

        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            if (!fromEditProfile) {
                startSplashScreen();
            } else {
                setDefaultFragment(new EditProfileFragment(), EDITPROFILE_FRAGMENT);
            }
        } else {
            if (IS_AUTHENTICATION_SKIPPED && !fromAppMainActivity){
                startSplashScreen();
            } else if (!fromAppMainActivity) {
                setDefaultFragment(new WalkThroughFragment(), WALKTHROUGH_FRAGMENT);
            } else {
                setDefaultFragment(new LoginFragment(), LOGIN_FRAGMENT);
            }
        }

        if (isAppMainActivityOpen) {
            isAppMainActivityOpen = false;
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!fromAppMainActivity) {
                if (currentFragment == SIGNUP_FRAGMENT) {
                    setFragment(new LoginFragment(), LOGIN_FRAGMENT);
                    return false;
                } else if (currentFragment == LOGIN_FRAGMENT) {
                    setFragment(new WalkThroughFragment(), WALKTHROUGH_FRAGMENT);
                    return false;
                } else if (currentFragment == FORGOT_PASSWORD_FRAGMENT) {
                    setFragment(new LoginFragment(), LOGIN_FRAGMENT);
                    return false;
                } else if (currentFragment == OTP_FRAGMENT) {
                    setFragment(new SignUpFragment(), SIGNUP_FRAGMENT);
                    return false;
                }
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setDefaultFragment(Fragment fragment, int currentFragment) {
        MainActivity.currentFragment = currentFragment;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void setFragment(Fragment fragment, int currentFragment) {
        MainActivity.currentFragment = currentFragment;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slideout_from_right);
        fragmentTransaction.replace(MainActivity.frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        fromEditProfile = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setSystemBarTheme(this, false);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static final void setSystemBarTheme(final Activity pActivity, final boolean pIsDark) {
        // Fetch the current flags.
        final int lFlags = pActivity.getWindow().getDecorView().getSystemUiVisibility();
        // Update the SystemUiVisibility dependening on whether we want a Light or Dark theme.
        pActivity.getWindow().getDecorView().setSystemUiVisibility(pIsDark ? (lFlags & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) : (lFlags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));
    }

    private void startSplashScreen(){
        setDefaultFragment(new SplashFragmentFragment(), SPLASH_FRAGMENT);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent appMainActivity = new Intent(MainActivity.this, AppMainActivity.class);
                startActivity(appMainActivity);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
