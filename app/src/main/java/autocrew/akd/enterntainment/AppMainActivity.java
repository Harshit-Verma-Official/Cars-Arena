package autocrew.akd.enterntainment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AppMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    boolean doubleBackToExitPressedOnce = false;

    public static WindowManager windowManager;

    public static int TYPE_UPCOMING = 0;
    public static int TYPE_WISHLIST = 1;

    public static int COLLECTION_IN_MAIN_FRAGMENT = 0;
    public static int COLLECTION_IN_HOME_FRAGMENT = 1;

    public static int UPCOMING_IN_MAIN_FRAGMENT = 0;
    public static int UPCOMING_IN_HOME_FRAGMENT = 1;

    public static final int HOME_FRAGMENT = 0;
    public static Fragment homeFragment = new HomeFragment();

    public static final int BRANDS_FRAGMENT = 1;
    public static Fragment brandsFragment = new BrandsFragment();

    public static final int COLLECTION_FRAGMENT = 2;
    public static Fragment collectionFragment = new CollectionFragment();

    public static final int NEW_CARS_FRAGMENT = 3;
    public static Fragment newCarsFragment = new NewCarsFragment();

    public static final int UPCOMING_FRAGMENT = 4;
    public static Fragment upcomingFragment = new UpComingFragment();

    public static final int WALLPAPER_FRAGMENT = 5;
    public static Fragment wallpaperFragment = new WallpaperFragment();

    public static final int WISHLIST_FRAGMENT = 6;
    public static Fragment wishListFragment = new WishlistFragment();

    public static final int MYACCOUNT_FRAGMENT = 7;
    public static Fragment myAccountFragment = new MyAccountFragment();

    public static final int SEARCH_FRAGMENT = 8;

    public static ActionBar actionBar;
    public static Resources resources;

    public static Fragment active = homeFragment;
    final FragmentManager fragmentManager = getSupportFragmentManager();

    private FrameLayout frameLayout;
    public static NavigationView navigationView;
    private int currentFragment;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore firebaseFirestore;

    private TextView headerEmail;
    private TextView userName;

    private ImageView profile_avtar;

    public static boolean isFromMyAccountBtn = false;

    public static AppBarLayout appBarLayout;
    public static DrawerLayout drawer;

    private ActionBarDrawerToggle drawerToggle;

    public static Typeface fontCircularProBold;
    public static Typeface fontCircularProSemiBold;
    public static Typeface fontTypoRoundBold;
    public static Typeface fontUbuntuMedium;
    public static Typeface fontIndustryUltraItalic;
    public static Typeface fontIndustryMediumItalic;

    public static SearchView searchView;

    public static Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        windowManager = getWindowManager();

        actionBar = getSupportActionBar();
        resources = getResources();

        fontCircularProBold = Typeface.createFromAsset(getAssets(), "fonts/Circular Pro_bold.otf");
        fontCircularProSemiBold = Typeface.createFromAsset(getAssets(), "fonts/Circular Pro_semi_bold.otf");
        fontTypoRoundBold = Typeface.createFromAsset(getAssets(), "fonts/Typo_Round_Bold.otf");
        fontUbuntuMedium = Typeface.createFromAsset(getAssets(), "fonts/Ubuntu_Medium.ttf");
        fontIndustryUltraItalic = Typeface.createFromAsset(getAssets(), "fonts/Industry_Ultra_Italic.ttf");
        fontIndustryMediumItalic = Typeface.createFromAsset(getAssets(), "fonts/Industry Medium Italic.ttf");

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        ////////// AppBar And Drawer
        drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                frameLayout.setTranslationX(slideOffset * drawerView.getWidth() / 2);
                appBarLayout.setTranslationX(slideOffset * drawerView.getWidth() / 2);
                drawer.bringChildToFront(drawerView);
                drawer.requestLayout();
            }
        };
        drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        appBarLayout = findViewById(R.id.appbar_layout);
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        toolbar.getLayoutParams().height += getStatusBarHeight();
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.menu_icon, null);
                toolbar.setNavigationIcon(d);
            }
        });
        ////////// AppBar And Drawer

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        // Navigation Header
        View hView = navigationView.getHeaderView(0);
        profile_avtar = hView.findViewById(R.id.profile_pic);
        Glide.with(this)
                .load(R.drawable.batman)
                .fitCenter()
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(profile_avtar);
        headerEmail = hView.findViewById(R.id.email);
        userName = hView.findViewById(R.id.userName);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Circular Pro_semi_bold.otf");
        headerEmail.setTypeface(font);
        userName.setTypeface(font);
        // Navigation Header

        // Navigation Menu Items Font
        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }
            applyFontToMenuItem(mi);
        }
        // Navigation Menu Items Font

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        frameLayout = findViewById(R.id.main_framelayout);

        fragmentManager.beginTransaction().add(R.id.main_framelayout, brandsFragment, "1").hide(brandsFragment).commit();
        fragmentManager.beginTransaction().add(R.id.main_framelayout, collectionFragment, "2").hide(collectionFragment).commit();
        fragmentManager.beginTransaction().add(R.id.main_framelayout, newCarsFragment, "3").hide(newCarsFragment).commit();
        fragmentManager.beginTransaction().add(R.id.main_framelayout, upcomingFragment, "4").hide(upcomingFragment).commit();
        fragmentManager.beginTransaction().add(R.id.main_framelayout, wishListFragment, "5").hide(wishListFragment).commit();
        fragmentManager.beginTransaction().add(R.id.main_framelayout, myAccountFragment, "6").hide(myAccountFragment).commit();
        fragmentManager.beginTransaction().add(R.id.main_framelayout, wallpaperFragment, "7").hide(wallpaperFragment).commit();
        fragmentManager.beginTransaction().add(R.id.main_framelayout, homeFragment, "0").commit();
        setNoRepeatFragment("Home", active, homeFragment);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        SpannableString s = new SpannableString("Home");
        s.setSpan(new TypefaceSpan(this, "Industry_Ultra_Italic.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setSystemBarTheme(this, true);
        }

        MainActivity.isAppMainActivityOpen = true;
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setEnabled(true);
            firebaseFirestore.collection("USERS").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        String userProfilePicUrl = documentSnapshot.get("user_profile_pic").toString();
                        String userFirstName = documentSnapshot.get("first_name").toString();
                        String userLastName = documentSnapshot.get("last_name").toString();
                        String userEmail = documentSnapshot.get("email").toString();
                        userName.setVisibility(View.VISIBLE);
                        headerEmail.setTextSize(12);
                        headerEmail.setText(userEmail);
                        userName.setText(userFirstName + " " + userLastName);
                        if (!userProfilePicUrl.equals("")) {
                            Glide.with(getApplicationContext())
                                    .load(userProfilePicUrl)
                                    .centerCrop()
                                    .apply(RequestOptions.circleCropTransform())
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(profile_avtar);
                        } else {
                            Glide.with(getApplicationContext())
                                    .load(R.drawable.batman)
                                    .centerCrop()
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(profile_avtar);
                        }
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(AppMainActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            headerEmail.setTextSize(16);
            headerEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isFromMyAccountBtn = true;
                    MainActivity.isAppMainActivityOpen = false;
                    Intent registerIntent = new Intent(AppMainActivity.this, MainActivity.class);
                    MainActivity.fromAppMainActivity = true;
                    startActivity(registerIntent);
                }
            });
            navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_main, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type Car Name");

        Fragment searchFragment = new SearchFragment();
        fragmentManager.beginTransaction().add(R.id.main_framelayout, searchFragment, "7").hide(searchFragment).commit();

        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out);
                fragmentTransaction.hide(active).show(searchFragment).commit();
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out);
                fragmentTransaction.hide(searchFragment).show(active).commit();
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:
                TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.toolbar));
                item.expandActionView();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    MenuItem menuItem;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        menuItem = item;
        int id = menuItem.getItemId();
        if (id == R.id.nav_home) {
            invalidateOptionsMenu();
            setNoRepeatFragment("Home", active, homeFragment);
        } else if (id == R.id.nav_brands) {
            setNoRepeatFragment("Brands", active, brandsFragment);
        } else if (id == R.id.nav_collections) {
            setNoRepeatFragment("Collection", active, collectionFragment);
        } else if (id == R.id.nav_new_cars) {
            setNoRepeatFragment("New Cars", active, newCarsFragment);
        } else if (id == R.id.nav_upcoming) {
            setNoRepeatFragment("Up Coming", active, upcomingFragment);
        } else if (id == R.id.nav_wallpaper) {
            setNoRepeatFragment("Arena Showcase", active, wallpaperFragment);
        } else if (id == R.id.nav_wishlist) {
            if (firebaseAuth.getCurrentUser() != null) {
                setNoRepeatFragment("Wishlist", active, wishListFragment);
            } else {
                isFromMyAccountBtn = true;
                MainActivity.isAppMainActivityOpen = false;
                Intent registerIntent = new Intent(AppMainActivity.this, MainActivity.class);
                MainActivity.fromAppMainActivity = true;
                startActivity(registerIntent);
            }
        } else if (id == R.id.nav_my_account) {
            if (firebaseAuth.getCurrentUser() != null) {
                setNoRepeatFragment("My Account", active, myAccountFragment);
            } else {
                isFromMyAccountBtn = true;
                MainActivity.isAppMainActivityOpen = false;
                Intent registerIntent = new Intent(AppMainActivity.this, MainActivity.class);
                MainActivity.fromAppMainActivity = true;
                startActivity(registerIntent);
            }
        } else if (id == R.id.nav_sign_out) {
            signOut(this, firebaseAuth);
            isFromMyAccountBtn = true;
            MainActivity.isAppMainActivityOpen = false;
            MainActivity.fromAppMainActivity = true;
            Intent registerIntent = new Intent(AppMainActivity.this, MainActivity.class);
            startActivity(registerIntent);
            finish();
        }
        return true;
    }

    private void setNoRepeatFragment(String title, Fragment oldActive, Fragment newFragment) {
        if (oldActive != newFragment) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            SpannableString s = new SpannableString(title);
            s.setSpan(new TypefaceSpan(this, "Industry_Ultra_Italic.ttf"), 0, s.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            getSupportActionBar().setTitle(s);
            invalidateOptionsMenu();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.hide(oldActive).show(newFragment).commit();
            active = newFragment;
        }
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Circular Pro_semi_bold.otf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (active == homeFragment) {
            currentFragment = -1;
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press back again to exit.", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            invalidateOptionsMenu();
            setNoRepeatFragment("Home", active, homeFragment);
            navigationView.getMenu().getItem(0).setChecked(true);
        }
    }

    @Override
    protected void onResume() {
        isFromMyAccountBtn = false;
        if (currentUser != null) {
            navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setEnabled(true);
            if (MainActivity.currentFragment == MainActivity.EDITPROFILE_FRAGMENT) {
                setNoRepeatFragment("My Account", active, active);
                if (active == homeFragment) {
                    navigationView.getMenu().getItem(HOME_FRAGMENT).setChecked(true);
                } else if (active == brandsFragment) {
                    navigationView.getMenu().getItem(BRANDS_FRAGMENT).setChecked(true);
                } else if (active == collectionFragment) {
                    navigationView.getMenu().getItem(COLLECTION_FRAGMENT).setChecked(true);
                } else if (active == upcomingFragment) {
                    navigationView.getMenu().getItem(UPCOMING_FRAGMENT).setChecked(true);
                } else if (active == wallpaperFragment) {
                    navigationView.getMenu().getItem(WALLPAPER_FRAGMENT).setChecked(true);
                } else if (active == wishListFragment) {
                    navigationView.getMenu().getItem(WISHLIST_FRAGMENT).setChecked(true);
                } else if (active == myAccountFragment) {
                    navigationView.getMenu().getItem(MYACCOUNT_FRAGMENT).setChecked(true);
                } else if (active == newCarsFragment) {
                    navigationView.getMenu().getItem(NEW_CARS_FRAGMENT).setChecked(true);
                }
            } else {
                setNoRepeatFragment("My Account", active, active);
                if (active == homeFragment) {
                    navigationView.getMenu().getItem(HOME_FRAGMENT).setChecked(true);
                } else if (active == brandsFragment) {
                    navigationView.getMenu().getItem(BRANDS_FRAGMENT).setChecked(true);
                } else if (active == collectionFragment) {
                    navigationView.getMenu().getItem(COLLECTION_FRAGMENT).setChecked(true);
                } else if (active == upcomingFragment) {
                    navigationView.getMenu().getItem(UPCOMING_FRAGMENT).setChecked(true);
                } else if (active == wallpaperFragment) {
                    navigationView.getMenu().getItem(WALLPAPER_FRAGMENT).setChecked(true);
                } else if (active == wishListFragment) {
                    navigationView.getMenu().getItem(WISHLIST_FRAGMENT).setChecked(true);
                } else if (active == myAccountFragment) {
                    navigationView.getMenu().getItem(MYACCOUNT_FRAGMENT).setChecked(true);
                } else if (active == newCarsFragment) {
                    navigationView.getMenu().getItem(NEW_CARS_FRAGMENT).setChecked(true);
                }
            }
        } else {
            if (active == homeFragment) {
                navigationView.getMenu().getItem(HOME_FRAGMENT).setChecked(true);
            } else if (active == brandsFragment) {
                navigationView.getMenu().getItem(BRANDS_FRAGMENT).setChecked(true);
            } else if (active == collectionFragment) {
                navigationView.getMenu().getItem(COLLECTION_FRAGMENT).setChecked(true);
            } else if (active == newCarsFragment) {
                navigationView.getMenu().getItem(NEW_CARS_FRAGMENT).setChecked(true);
            } else if (active == upcomingFragment) {
                navigationView.getMenu().getItem(UPCOMING_FRAGMENT).setChecked(true);
            } else if (active == wallpaperFragment) {
                navigationView.getMenu().getItem(WALLPAPER_FRAGMENT).setChecked(true);
            } else if (active == wishListFragment) {
                navigationView.getMenu().getItem(WISHLIST_FRAGMENT).setChecked(true);
            }
            navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setEnabled(false);
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.isAppMainActivityOpen = false;
        MainActivity.fromAppMainActivity = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isFromMyAccountBtn) {
            MainActivity.isAppMainActivityOpen = true;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public static final void setSystemBarTheme(final Activity pActivity, final boolean pIsDark) {
        // Fetch the current flags.
        final int lFlags = pActivity.getWindow().getDecorView().getSystemUiVisibility();
        // Update the SystemUiVisibility dependening on whether we want a Light or Dark theme.
        pActivity.getWindow().getDecorView().setSystemUiVisibility(pIsDark ? (lFlags & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) : (lFlags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));
    }

    public static void signOut(Context context, FirebaseAuth firebaseAuth) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("65125700011-nhkvqn50canfjk4hm7j68d1o560kqa5v.apps.googleusercontent.com")//you can also use R.string.default_web_client_id
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        mGoogleSignInClient.signOut();
        firebaseAuth.signOut();
    }

    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @SuppressLint("NewApi")
    public static int getSoftButtonsBarHeight() {
        // getRealMetrics is only available with API 17 and +
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            windowManager.getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }
}
