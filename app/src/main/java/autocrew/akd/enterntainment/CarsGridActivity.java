package autocrew.akd.enterntainment;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CarsGridActivity extends AppCompatActivity {

    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private RecyclerView gridRecyclerView;

    private String fieldName;
    private String fieldValue;

    private TextView toolbarTitleTextView;
    private ImageView brandLogoImageView;

    private List<PopularCarModel> gridCarsList;

    private FirebaseFirestore firebaseFirestore;

    private ShimmerFrameLayout shimmerFrameLayout;

    private GridCarsAdapter gridCarsAdapter;

    private Query query;

    private ConstraintLayout emptyListLayout;

    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars_grid);

        ///////// Intent
        Intent intent = getIntent();
        fieldName = intent.getStringExtra("fieldName");
        fieldValue = intent.getStringExtra("fieldValue");
        ///////// Intent

        /////// Appbar Layout
        appBarLayout = findViewById(R.id.appbar_layout);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        toolbar.getLayoutParams().height += getStatusBarHeight();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /////// Appbar Layout

        ////////// Initialization
        gridRecyclerView = findViewById(R.id.grid_recyclerView);
        shimmerFrameLayout = findViewById(R.id.shimmer_layout);
        emptyListLayout = findViewById(R.id.empty_list_layout);

        toolbarTitleTextView = findViewById(R.id.toolbar_title);
        brandLogoImageView = findViewById(R.id.brandLogo);

        gridCarsList = new ArrayList<>();

        firebaseFirestore = FirebaseFirestore.getInstance();
        ////////// Initialization

        ///////// Setting Toolbar Title
        toolbarTitleTextView.setText(fieldValue);
        toolbarTitleTextView.setTypeface(AppMainActivity.fontCircularProBold);
        if (fieldName.equals("car_brand")) {
            firebaseFirestore.collection("CAR_BRANDS").whereEqualTo("brand_name", fieldValue).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Glide.with(getApplicationContext())
                                        .load(documentSnapshot.get("brand_logo_url").toString())
                                        .fitCenter()
                                        .into(brandLogoImageView);
                            }
                            Animation a = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.text_scale_animation);
                            a.reset();
                            brandLogoImageView.startAnimation(a);
                        }
                    });
            brandLogoImageView.setVisibility(View.VISIBLE);
        }
        ///////// Setting Toolbar Title

        /////////// RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridRecyclerView.setLayoutManager(gridLayoutManager);

        gridCarsAdapter = new GridCarsAdapter(this, gridCarsList);
        gridRecyclerView.setAdapter(gridCarsAdapter);

        int spanCount = 2; // 3 columns
        int spacing;
        if (getResources().getString(R.string.density_bucket).equals("xxhdpi")) {
            spacing = 30; // 50px
        } else if (getResources().getString(R.string.density_bucket).equals("xhdpi")) {
            spacing = 20;
        } else {
            spacing = 25;
        }
        boolean includeEdge = true;
        gridRecyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

        gridCarsAdapter.notifyDataSetChanged();
        /////////// RecyclerView

        ////////// Creating Reference
        if (fieldName.equals("likes")) {
            query = firebaseFirestore.collection("GARAGE").orderBy("likes", Query.Direction.DESCENDING);
            runQuery();
        } else if (fieldName.equals("wishList")) {
            firebaseFirestore.collection("USERS")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .collection("WISHLIST")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                gridCarsList.clear();
                                int tempSize = task.getResult().size();
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    String carId = documentSnapshot.get("car_id").toString();
                                    firebaseFirestore.collection("GARAGE")
                                            .document(carId)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot snapshot = task.getResult();
                                                        gridCarsList.add(new PopularCarModel(
                                                                snapshot.get("car_name").toString(),
                                                                snapshot.get("car_brand").toString(),
                                                                snapshot.get("car_image_1").toString(),
                                                                snapshot.get("car_id").toString()
                                                        ));
                                                        if (gridCarsList.size() == tempSize) {
                                                            emptyListLayout.setVisibility(View.GONE);
                                                            ((LottieAnimationView) ((LinearLayout) emptyListLayout.findViewById(R.id.wishlist_empty_layout)).getChildAt(0)).cancelAnimation();
                                                            notifyDataSetChanged();
                                                        }
                                                    } else {
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(CarsGridActivity.this, error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                                if (gridCarsList.size() == 0) {
                                    ((LottieAnimationView) ((LinearLayout) emptyListLayout.findViewById(R.id.wishlist_empty_layout)).getChildAt(0)).playAnimation();
                                    emptyListLayout.setVisibility(View.VISIBLE);
                                    notifyDataSetChanged();
                                }
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(CarsGridActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            query = firebaseFirestore.collection("GARAGE").whereEqualTo(fieldName, fieldValue);
            runQuery();
        }
        ////////// Creating Reference

    }

    private void runQuery() {
        ////////// Fetching Data
        query.limit(16).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            gridCarsList.clear();
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                gridCarsList.add(new PopularCarModel(
                                        documentSnapshot.get("car_name").toString(),
                                        documentSnapshot.get("car_brand").toString(),
                                        documentSnapshot.get("car_image_1").toString(),
                                        documentSnapshot.get("car_id").toString()
                                ));
                                if (gridCarsList.size() == task.getResult().size()) {
                                    notifyDataSetChanged();
                                    lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);

                                    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                                        @Override
                                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                            super.onScrollStateChanged(recyclerView, newState);
                                            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                                isScrolling = true;
                                            }
                                        }

                                        @Override
                                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                            super.onScrolled(recyclerView, dx, dy);

                                            GridLayoutManager linearLayoutManager = ((GridLayoutManager) recyclerView.getLayoutManager());
                                            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                                            int visibleItemCount = linearLayoutManager.getChildCount();
                                            int totalItemCount = linearLayoutManager.getItemCount();

                                            if (isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
                                                isScrolling = false;
                                                if (!isLoading) {
                                                    isLoading = true;

                                                    gridCarsList.add(null);
                                                    recyclerView.getAdapter().notifyItemInserted(gridCarsList.size() - 1);
                                                    int tempPos = gridCarsList.size() - 1;

                                                    Query nextQuery = query.startAfter(lastVisible).limit(16);
                                                    nextQuery.get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                                            gridCarsList.add(new PopularCarModel(
                                                                                    snapshot.get("car_name").toString(),
                                                                                    snapshot.get("car_brand").toString(),
                                                                                    snapshot.get("car_image_1").toString(),
                                                                                    snapshot.get("car_id").toString()
                                                                            ));
                                                                            recyclerView.getAdapter().notifyItemInserted(gridCarsList.size() - 1);
                                                                        }
                                                                        gridCarsList.remove(tempPos);
                                                                        recyclerView.getAdapter().notifyItemRemoved(tempPos);
                                                                        if ((task.getResult().size() - 1) != -1) {
                                                                            lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                                                                        }
                                                                        isLoading = false;

                                                                        if (task.getResult().size() < 16) {
                                                                            isLastItemReached = true;
                                                                        }
                                                                    } else {
                                                                        String error = task.getException().getMessage();
                                                                        Toast.makeText(CarsGridActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        }
                                    };
                                    gridRecyclerView.addOnScrollListener(onScrollListener);
                                }
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(CarsGridActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        ////////// Fetching Data
    }

    private void notifyDataSetChanged() {
        gridCarsAdapter.notifyDataSetChanged();
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        gridRecyclerView.setVisibility(View.VISIBLE);
        DBQueries.runLayoutAnimation(gridRecyclerView);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setSystemBarTheme(this, true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static final void setSystemBarTheme(final Activity pActivity, final boolean pIsDark) {
        // Fetch the current flags.
        final int lFlags = pActivity.getWindow().getDecorView().getSystemUiVisibility();
        // Update the SystemUiVisibility dependening on whether we want a Light or Dark theme.
        pActivity.getWindow().getDecorView().setSystemUiVisibility(pIsDark ? (lFlags & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) : (lFlags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));
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
