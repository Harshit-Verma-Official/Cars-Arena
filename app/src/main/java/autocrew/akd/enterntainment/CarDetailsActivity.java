package autocrew.akd.enterntainment;

import android.animation.AnimatorInflater;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarDetailsActivity extends AppCompatActivity {

    public static AppBarLayout appBarLayout;

    private ViewPager viewPager;
    private DotsIndicator viewPagerIndicator;
    private FirebaseFirestore firebaseFirestore;

    private RecyclerView commonSpecsRecyclerView;
    private RecyclerView specificationRecyclerView;

    private TextView carNameTextView;
    private TextView carSubtitleTextView;
    private ImageView carBrandLogo;
    private SparkButton sparkButton;

    private TextView parametersHeading;
    private TextView specificationHeading;

    private ConstraintLayout parametersLayout;
    private ConstraintLayout carSpecLayout;

    private List<String> carImagesList;
    private List<String> carImagesNameList;
    private List<String> commonSpecsList;
    private List<SpecificationModel> specificationModelList;

    private Dialog loadingDialog;

    private boolean isQueryRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_details);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Industry_Ultra_Italic.ttf");
        Typeface font2 = Typeface.createFromAsset(getAssets(), "fonts/Rogan_Medium.otf");
        Typeface font3 = Typeface.createFromAsset(getAssets(), "fonts/Industry Medium Italic.ttf");
        Typeface font4 = Typeface.createFromAsset(getAssets(), "fonts/Bourgeois Medium.ttf");

        //        Loading Dialog
        loadingDialog = new Dialog(CarDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        ImageView dialogImage = loadingDialog.findViewById(R.id.load_image);
        Glide.with(this)
                .load(R.drawable.loading_gif)
                .into(dialogImage);
        loadingDialog.setCancelable(false);
        loadingDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    loadingDialog.dismiss();
                    finish();
                }
                return true;
            }
        });
        loadingDialog.show();
//        Loading Dialog

        ////////// Intent
        Intent intent = getIntent();
        final String carId = intent.getStringExtra("garage_car_id");
        ////////// Intent

        ////////// AppBar And Drawer
        final Toolbar toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appbar_layout);
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        toolbar.getLayoutParams().height += getStatusBarHeight();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout.setStateListAnimator(AnimatorInflater.loadStateListAnimator(this, R.animator.appbar_elevation_off));
        ////////// AppBar And Drawer

        ////////// Initialization
        firebaseFirestore = FirebaseFirestore.getInstance();
        carImagesList = new ArrayList<>();
        carImagesNameList = new ArrayList<>();
        commonSpecsList = new ArrayList<>();
        specificationModelList = new ArrayList<>();
        viewPager = findViewById(R.id.viewPager);
        viewPagerIndicator = findViewById(R.id.viewpager_indicator);
        carNameTextView = findViewById(R.id.car_name_textView);
        carSubtitleTextView = findViewById(R.id.car_subtitle_textView);
        commonSpecsRecyclerView = findViewById(R.id.car_common_spec_recyclerView);
        parametersHeading = findViewById(R.id.parameters_heading);
        specificationHeading = findViewById(R.id.specification_heading);
        parametersLayout = findViewById(R.id.parameters_layout);
        carSpecLayout = findViewById(R.id.car_spec_layout);
        specificationRecyclerView = findViewById(R.id.specification_recyclerView);
        carBrandLogo = findViewById(R.id.brand_logo_imageView);
        sparkButton = findViewById(R.id.spark_button);
        ////////// Initialization

        ////////// Setting Fonts
        carNameTextView.setTypeface(font);
        carSubtitleTextView.setTypeface(font2);
        parametersHeading.setTypeface(font3);
        specificationHeading.setTypeface(font3);

        LinearLayout parameters_first_layout = findViewById(R.id.parameters_first_layout);
        LinearLayout parameters_second_layout = findViewById(R.id.parameters_second_layout);
        for (int i = 0; i < parameters_first_layout.getChildCount(); i++) {
            LinearLayout tempLayout = (LinearLayout) parameters_first_layout.getChildAt(i);
            for (int j = 0; j < tempLayout.getChildCount(); j++) {
                if (j != 1) {
                    TextView tempTextView = (TextView) tempLayout.getChildAt(j);
                    TextView tempTextView2 = (TextView) parameters_second_layout.getChildAt(j);
                    tempTextView.setTypeface(font4);
                    tempTextView2.setTypeface(font4);
                    if (j == 0) {
                        tempTextView.setTextColor(Color.parseColor("#B4B4B4"));
                        tempTextView2.setTextColor(Color.parseColor("#B4B4B4"));
                    } else if (j == 2) {
                        tempTextView.setTextColor(Color.parseColor("#6A6A6A"));
                        tempTextView2.setTextColor(Color.parseColor("#6A6A6A"));
                    }
                } else {
                    TextView tempTextView = (TextView) tempLayout.getChildAt(j);
                    TextView tempTextView2 = (TextView) parameters_second_layout.getChildAt(j);
                    tempTextView.setTypeface(font);
                    tempTextView2.setTypeface(font);
                    tempTextView.setTextColor(Color.parseColor("#ffffff"));
                    tempTextView2.setTextColor(Color.parseColor("#ffffff"));
                }
            }
        }
        ////////// Setting Fonts

        ////////// Add to WishList
        sparkButton.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    if (buttonState) {
                        isQueryRunning = true;
                        sparkButton.setEnabled(false);
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("car_id", carId);
                        userData.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());
                        firebaseFirestore.collection("USERS")
                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .collection("WISHLIST")
                                .document(carId)
                                .set(userData)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            isQueryRunning = false;
                                            sparkButton.setEnabled(true);
                                            Map<String, Object> likeCount = new HashMap<>();
                                            firebaseFirestore.collection("GARAGE").document(carId)
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                                long currentLikesCount = (long) documentSnapshot.get("likes");
                                                                currentLikesCount++;
                                                                likeCount.put("likes", (long) currentLikesCount++);
                                                                firebaseFirestore.collection("GARAGE").document(carId)
                                                                        .update(likeCount)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    Toast.makeText(CarDetailsActivity.this, "Added to Wish List!", Toast.LENGTH_SHORT).show();
                                                                                } else {
                                                                                    String error = task.getException().getMessage();
                                                                                    Toast.makeText(CarDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                            } else {
                                                                String error = task.getException().getMessage();
                                                                Toast.makeText(CarDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                        } else {
                                            String error = task.getException().getMessage();
                                            Toast.makeText(CarDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        isQueryRunning = true;
                        sparkButton.setEnabled(false);
                        firebaseFirestore.collection("USERS")
                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .collection("WISHLIST")
                                .document(carId)
                                .delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            isQueryRunning = false;
                                            sparkButton.setEnabled(true);
                                            Map<String, Object> likeCount = new HashMap<>();
                                            firebaseFirestore.collection("GARAGE").document(carId)
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                                long currentLikesCount = (long) documentSnapshot.get("likes");
                                                                currentLikesCount--;
                                                                likeCount.put("likes", (long) currentLikesCount++);
                                                                firebaseFirestore.collection("GARAGE").document(carId)
                                                                        .update(likeCount)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    Toast.makeText(CarDetailsActivity.this, "Removed from WishList!", Toast.LENGTH_SHORT).show();
                                                                                } else {
                                                                                    String error = task.getException().getMessage();
                                                                                    Toast.makeText(CarDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                            } else {
                                                                String error = task.getException().getMessage();
                                                                Toast.makeText(CarDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });


                                        } else {
                                            String error = task.getException().getMessage();
                                            Toast.makeText(CarDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                } else {
                    if (buttonState) {
                        sparkButton.setChecked(false);
                        MainActivity.isAppMainActivityOpen = false;
                        Intent registerIntent = new Intent(CarDetailsActivity.this, MainActivity.class);
                        MainActivity.fromAppMainActivity = true;
                        startActivity(registerIntent);
                    }
                }
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {

            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {

            }
        });
        ////////// Add to WishList

        ////////// ViewPager
        viewPager.setPadding(0, getStatusBarHeight(), 0, 0);
        viewPager.getLayoutParams().height += getStatusBarHeight();
        final ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, carImagesList, carImagesNameList);
        viewPager.setAdapter(viewPagerAdapter);
        viewPagerIndicator.setViewPager(viewPager);
        firebaseFirestore.collection("GARAGE").document(carId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            carImagesList.clear();
                            final DocumentSnapshot documentSnapshot = task.getResult();

                            Animation a = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.text_scale_animation);
                            a.reset();
                            carNameTextView.clearAnimation();
                            carNameTextView.setText(documentSnapshot.get("car_name").toString());
                            carSubtitleTextView.setText(documentSnapshot.get("car_subtitle").toString());
                            carNameTextView.startAnimation(a);
                            carSubtitleTextView.startAnimation(a);


                            ////////// Setting Brand Logo
                            firebaseFirestore.collection("CAR_BRANDS").whereEqualTo("brand_name", documentSnapshot.get("car_brand").toString()).get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot documentSnapshot1 : task.getResult()) {
                                                    Glide.with(getApplicationContext())
                                                            .load(documentSnapshot1.get("brand_logo_url").toString())
                                                            .into(carBrandLogo);
                                                    Animation a = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.text_scale_animation);
                                                    a.reset();
                                                    carBrandLogo.startAnimation(a);
                                                }
                                            } else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(CarDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            ////////// Setting Brand Logo

                            /////////// Check Favorite
                            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                firebaseFirestore.collection("USERS")
                                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .collection("WISHLIST")
                                        .document(carId)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document != null && document.exists()) {
                                                        ////////// If car is already added to wishList
                                                        sparkButton.setChecked(true);
                                                    }
                                                    Animation a = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.text_scale_animation);
                                                    a.reset();
                                                    sparkButton.setVisibility(View.VISIBLE);
                                                    sparkButton.startAnimation(a);
                                                } else {
                                                    Toast.makeText(CarDetailsActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                a.reset();
                                sparkButton.setVisibility(View.VISIBLE);
                                sparkButton.startAnimation(a);
                            }
                            /////////// Check Favorite

                            /////// Loading Car Images
                            for (int i = 1; i <= (long) documentSnapshot.get("no_of_images"); i++) {
                                carImagesList.add(documentSnapshot.get("car_image_" + String.valueOf(i)).toString());
                                carImagesNameList.add(documentSnapshot.get("car_image_" + String.valueOf(i) + "_name").toString());
                            }
                            /////// Loading Car Images
                            viewPager.startAnimation(a);
                            viewPagerAdapter.notifyDataSetChanged();
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(CarDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        ////////// ViewPager

        ////////// Brand Logo

        ////////// Brand Logo

        ////////// Common Specs RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        commonSpecsRecyclerView.setLayoutManager(gridLayoutManager);
        commonSpecsRecyclerView.setNestedScrollingEnabled(false);


        int spanCount = 3; // 3 columns
        int spacing;
        if (getResources().getString(R.string.density_bucket).equals("xxxhdpi")) {
            spacing = 30; // 50px
        } else if (getResources().getString(R.string.density_bucket).equals("xhdpi")) {
            spacing = 18;
        } else {
            spacing = 25;
        }
        boolean includeEdge = true;
        commonSpecsRecyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

        commonSpecsList.clear();
        firebaseFirestore.collection("GARAGE").document(carId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            for (int i = 1; i <= 5; i++) {
                                if (i != 3) {
                                    commonSpecsList.add(documentSnapshot.get("common_spec_" + String.valueOf(i)).toString());
                                } else {
                                    commonSpecsList.add("0-100 KpH " + documentSnapshot.get("common_spec_" + String.valueOf(i)).toString());
                                }
                            }
                            CommonSpecsAdapter commonSpecsAdapter = new CommonSpecsAdapter(getApplicationContext(), commonSpecsList);
                            commonSpecsRecyclerView.setAdapter(commonSpecsAdapter);
                            Animation a = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.text_scale_animation);
                            a.reset();
                            commonSpecsRecyclerView.startAnimation(a);
                            commonSpecsAdapter.notifyDataSetChanged();
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(CarDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        ////////// Common Specs RecyclerView

        ////////// Parameters
        firebaseFirestore.collection("GARAGE").document(carId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            String acceleration_time = documentSnapshot.get("common_spec_3").toString();
                            String rated_power = documentSnapshot.get("rated_power").toString();
                            String rated_torque = documentSnapshot.get("rated_torque").toString();
                            ((TextView) findViewById(R.id.acceleration_time_textView)).setText(acceleration_time);
                            ((TextView) findViewById(R.id.rated_power_textView)).setText(rated_power);
                            ((TextView) findViewById(R.id.rated_torque_textView)).setText(rated_torque);
                            parametersLayout.setVisibility(View.VISIBLE);
                            Animation a = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.text_scale_animation);
                            a.reset();
                            parametersLayout.startAnimation(a);
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(CarDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        ////////// Parameters

        ////////// Specifications
        LinearLayoutManager specificationLinearLayoutManager = new LinearLayoutManager(this);
        specificationLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        specificationRecyclerView.setLayoutManager(specificationLinearLayoutManager);
        specificationRecyclerView.setNestedScrollingEnabled(false);

        specificationModelList.clear();
        firebaseFirestore.collection("GARAGE").document(carId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            for (int i = 1; i <= (long) documentSnapshot.get("total_spec_titles"); i++) {
                                specificationModelList.add(new SpecificationModel(0, documentSnapshot.get("spec_title_" + String.valueOf(i)).toString()));
                                for (int j = 1; j <= (long) documentSnapshot.get("spec_title_" + String.valueOf(i) + "_total_features"); j++) {
                                    specificationModelList.add(new SpecificationModel(1, documentSnapshot.get("spec_title_" + String.valueOf(i) + "_feature_" + String.valueOf(j)).toString()));
                                }
                            }
                            CarSpecificationsAdapter carSpecificationsAdapter = new CarSpecificationsAdapter(getApplicationContext(), specificationModelList);
                            specificationRecyclerView.setAdapter(carSpecificationsAdapter);
                            carSpecificationsAdapter.notifyDataSetChanged();
                            carSpecLayout.setVisibility(View.VISIBLE);
                            Animation a = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.text_scale_animation);
                            a.reset();
                            specificationRecyclerView.startAnimation(a);
                            loadingDialog.dismiss();
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(CarDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
        ////////// Specifications

        specificationRecyclerView.setPadding(0, 0, 0, AppMainActivity.getSoftButtonsBarHeight());

    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
