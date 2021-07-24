package autocrew.akd.enterntainment;


import android.animation.AnimatorInflater;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.Pivot;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }

    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;

    private FirebaseFirestore firebaseFirestore;

    private NestedScrollView nestedScrollView;
    private RecyclerView exploreCarsRecyclerview;
    private GridLayout newCarsGridview;
    private DiscreteScrollView upcomingCarsRecyclerview;
    private DiscreteScrollView popularRecyclerview;

    public static SwipeRefreshLayout swipeRefreshLayout;

    private TextView exploreHeading;
    private TextView newCarsHeading;
    private TextView upcomingHeading;
    private TextView popularHeading;

    private List<ExploreCarsModel> exploreCarsModelList;
    private List<NewCarsModel> newCarsModelList;
    private List<PopularCarModel> popularCarModelList;
    private List<UpcomingCarModel> upcomingCarModelList;

    private ConstraintLayout noInternetConnectionLayout;
    private Button tryAgainBtn;

    public static Button exploreViewAllBtn;
    public static Button popularViewAllBtn;
    public static Button newViewAllBtn;
    public static Button upComingViewAllBtn;

    private LottieAnimationView lottieAnimationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Proxima Nova Sbold.otf");

        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        firebaseFirestore = FirebaseFirestore.getInstance();

        exploreCarsModelList = new ArrayList<>();
        exploreCarsModelList.clear();

        newCarsModelList = new ArrayList<>();
        newCarsModelList.clear();

        popularCarModelList = new ArrayList<>();
        popularCarModelList.clear();

        upcomingCarModelList = new ArrayList<>();
        upcomingCarModelList.clear();

        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);

        nestedScrollView = view.findViewById(R.id.nested_scroll_view);
        exploreCarsRecyclerview = view.findViewById(R.id.explore_cars_recyclerview);
        exploreCarsRecyclerview.setNestedScrollingEnabled(false);
        newCarsGridview = view.findViewById(R.id.new_cars_gridview);
        upcomingCarsRecyclerview = view.findViewById(R.id.upcoming_cars_recyclerview);
        upcomingCarsRecyclerview.setNestedScrollingEnabled(false);
        popularRecyclerview = view.findViewById(R.id.popular_recyclerview);
        popularRecyclerview.setNestedScrollingEnabled(false);

        exploreHeading = view.findViewById(R.id.explore_heading);
        newCarsHeading = view.findViewById(R.id.new_cars_heading);
        upcomingHeading = view.findViewById(R.id.upcoming_heading);
        popularHeading = view.findViewById(R.id.popular_heading);
        exploreHeading.setTypeface(AppMainActivity.fontIndustryUltraItalic);
        newCarsHeading.setTypeface(AppMainActivity.fontIndustryUltraItalic);
        upcomingHeading.setTypeface(AppMainActivity.fontIndustryUltraItalic);
        popularHeading.setTypeface(AppMainActivity.fontIndustryUltraItalic);

        exploreViewAllBtn = view.findViewById(R.id.explore_view_all_btn);
        popularViewAllBtn = view.findViewById(R.id.popular_view_all_btn);
        newViewAllBtn = view.findViewById(R.id.new_view_all_btn);
        upComingViewAllBtn = view.findViewById(R.id.upcoming_view_all_btn);

        lottieAnimationView = view.findViewById(R.id.no_internet_connection_anim);

        noInternetConnectionLayout = view.findViewById(R.id.no_internet_connection_layout);
        tryAgainBtn = view.findViewById(R.id.try_again_btn);


        reloadPage();

        exploreViewAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoRepeatFragment("Collection", AppMainActivity.active, AppMainActivity.collectionFragment);
                AppMainActivity.navigationView.getMenu().getItem(AppMainActivity.COLLECTION_FRAGMENT).setChecked(true);
            }
        });

        newViewAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setNoRepeatFragment("New Cars", AppMainActivity.active, AppMainActivity.newCarsFragment);
//                AppMainActivity.navigationView.getMenu().getItem(AppMainActivity.NEW_CARS_FRAGMENT).setChecked(true);
                Intent carGridIntent = new Intent(getContext(), CarsGridActivity.class);
                carGridIntent.putExtra("fieldName", "status");
                carGridIntent.putExtra("fieldValue", "New");
                getContext().startActivity(carGridIntent);
            }
        });

        upComingViewAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoRepeatFragment("Up Coming", AppMainActivity.active, AppMainActivity.upcomingFragment);
                AppMainActivity.navigationView.getMenu().getItem(AppMainActivity.UPCOMING_FRAGMENT).setChecked(true);
            }
        });

        popularViewAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent carGridIntent = new Intent(getContext(), CarsGridActivity.class);
                carGridIntent.putExtra("fieldName", "likes");
                carGridIntent.putExtra("fieldValue", "Popular");
                getContext().startActivity(carGridIntent);
            }
        });

        tryAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadPage();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            nestedScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY == 0) {
                        AppMainActivity.appBarLayout.setStateListAnimator(AnimatorInflater.loadStateListAnimator(getContext(), R.animator.appbar_elevation_off));
                    } else {
                        AppMainActivity.appBarLayout.setStateListAnimator(AnimatorInflater.loadStateListAnimator(getContext(), R.animator.appbar_elevation_on));
                    }
                }
            });
        }

        ////////// No Internet Connection
        for (int i = 1; i < noInternetConnectionLayout.getChildCount(); i++) {
            if (i != 3) {
                TextView tempTextView = (TextView) noInternetConnectionLayout.getChildAt(i);
                tempTextView.setTypeface(font);
            } else {
                Button tempBtn = (Button) noInternetConnectionLayout.getChildAt(i);
                tempBtn.setTypeface(font);
            }
        }
        ////////// No Internet Connection

        ////////// Refresh Home Page
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                reloadPage();
            }
        });
        ////////// Refresh Home Page

        return view;
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_right);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private void clearData() {
        upcomingCarModelList.clear();
        popularCarModelList.clear();
        newCarsModelList.clear();
        exploreCarsModelList.clear();
    }

    private void setNoRepeatFragment(String title, Fragment oldActive, Fragment newFragment) {
        if (oldActive != newFragment) {
            AppMainActivity.actionBar.setDisplayShowTitleEnabled(true);
            SpannableString s = new SpannableString(title);
            s.setSpan(new TypefaceSpan(getContext(), "Industry_Ultra_Italic.ttf"), 0, s.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            AppMainActivity.actionBar.setTitle(s);
            getActivity().invalidateOptionsMenu();
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.hide(oldActive).show(newFragment).commit();
            AppMainActivity.active = newFragment;
        }
    }

    private void reloadPage() {
        networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected() == true) {
            AppMainActivity.appBarLayout.setVisibility(View.VISIBLE);
            AppMainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            noInternetConnectionLayout.setVisibility(View.INVISIBLE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            lottieAnimationView.cancelAnimation();
        } else {
            Toast.makeText(getContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
            AppMainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            swipeRefreshLayout.setVisibility(View.INVISIBLE);
            AppMainActivity.appBarLayout.setVisibility(View.GONE);
            lottieAnimationView.setPadding(0, AppMainActivity.getStatusBarHeight(), 0, 0);
            lottieAnimationView.playAnimation();
            noInternetConnectionLayout.setVisibility(View.VISIBLE);
        }
        clearData();
        loadExploreCars();
        loadPopularCars();
        loadNewCars();
        loadUpComingCars();
    }

    private void loadExploreCars() {
        ////////// Explore car RecyclerView
        List<ExploreCarsModel> tempExploreModelList = new ArrayList<>();
        tempExploreModelList.add(new ExploreCarsModel("", ""));
        tempExploreModelList.add(new ExploreCarsModel("", ""));
        tempExploreModelList.add(new ExploreCarsModel("", ""));
        tempExploreModelList.add(new ExploreCarsModel("", ""));
        tempExploreModelList.add(new ExploreCarsModel("", ""));
        tempExploreModelList.add(new ExploreCarsModel("", ""));
        tempExploreModelList.add(new ExploreCarsModel("", ""));
        ExploreCarsAdapter tempExploreCarsAdapter = new ExploreCarsAdapter(getContext(), AppMainActivity.COLLECTION_IN_HOME_FRAGMENT, tempExploreModelList);
        exploreCarsRecyclerview.setAdapter(tempExploreCarsAdapter);


        LinearLayoutManager exploreCarsLayoutManager = new LinearLayoutManager(getContext());
        exploreCarsLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        exploreCarsRecyclerview.setLayoutManager(exploreCarsLayoutManager);
        firebaseFirestore.collection("CARS_CATEGORIES").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                exploreCarsModelList.add(new ExploreCarsModel(documentSnapshot.get("category_name").toString(), documentSnapshot.get("image_url").toString()));
                            }
                            ExploreCarsAdapter exploreCarsAdapter = new ExploreCarsAdapter(getContext(), AppMainActivity.COLLECTION_IN_HOME_FRAGMENT, exploreCarsModelList);
                            exploreCarsRecyclerview.setAdapter(exploreCarsAdapter);
                            runLayoutAnimation(exploreCarsRecyclerview);
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        ////////// Explore car RecyclerView
    }

    private void loadPopularCars() {
        ////////// Popular RecyclerView
        popularRecyclerview.setItemTransitionTimeMillis(100);
        popularRecyclerview.setHasFixedSize(true);
        popularRecyclerview.setOffscreenItems(5);
        popularRecyclerview.setItemTransformer(new ScaleTransformer.Builder()
                .setMaxScale(1.05f)
                .setMinScale(0.8f)
                .setPivotX(Pivot.X.CENTER) // CENTER is a default one
                .setPivotY(Pivot.Y.CENTER) // CENTER is a default one
                .build());


        List<PopularCarModel> tempPopularCarList = new ArrayList<>();
        tempPopularCarList.add(new PopularCarModel("", "", "", ""));
        tempPopularCarList.add(new PopularCarModel("", "", "", ""));
        tempPopularCarList.add(new PopularCarModel("", "", "", ""));
        tempPopularCarList.add(new PopularCarModel("", "", "", ""));
        tempPopularCarList.add(new PopularCarModel("", "", "", ""));
        tempPopularCarList.add(new PopularCarModel("", "", "", ""));
        tempPopularCarList.add(new PopularCarModel("", "", "", ""));
        tempPopularCarList.add(new PopularCarModel("", "", "", ""));
        PopularCarsAdapter tempPopularCarsAdapter = new PopularCarsAdapter(getContext(), tempPopularCarList);
        tempPopularCarsAdapter.setHasStableIds(true);
        popularRecyclerview.setAdapter(tempPopularCarsAdapter);


        popularCarModelList.clear();
        firebaseFirestore.collection("GARAGE").orderBy("likes", Query.Direction.DESCENDING).limit(10).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                popularCarModelList.add(new PopularCarModel(documentSnapshot.get("car_name").toString(), documentSnapshot.get("car_brand").toString(), documentSnapshot.get("car_image_1").toString(), documentSnapshot.get("car_id").toString()));
                            }
                            PopularCarsAdapter popularCarsAdapter = new PopularCarsAdapter(getContext(), popularCarModelList);
                            popularCarsAdapter.setHasStableIds(true);
                            popularRecyclerview.setAdapter(popularCarsAdapter);
                            runLayoutAnimation(popularRecyclerview);
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        ////////// Popular RecyclerView
    }

    private void loadNewCars() {
        ////////// New Cars GridView

        List<NewCarsModel> tempNewCarsList = new ArrayList<>();
        tempNewCarsList.clear();
        tempNewCarsList.add(new NewCarsModel("", "", "", "", ""));
        tempNewCarsList.add(new NewCarsModel("", "", "", "", ""));
        tempNewCarsList.add(new NewCarsModel("", "", "", "", ""));
        tempNewCarsList.add(new NewCarsModel("", "", "", "", ""));
        if (tempNewCarsList.size() != 0) {
            for (int x = 0; x < 4; x++) {
                ImageView newCarImage = newCarsGridview.getChildAt(x).findViewById(R.id.new_car_image);
                TextView newCarName = newCarsGridview.getChildAt(x).findViewById(R.id.new_car_name);
                TextView newCarDescription = newCarsGridview.getChildAt(x).findViewById(R.id.new_car_description);

                newCarName.setTypeface(AppMainActivity.fontCircularProBold);
                newCarDescription.setTypeface(AppMainActivity.fontCircularProBold);

                if (getContext() != null) {
                    Glide.with(getContext())
                            .load(tempNewCarsList.get(x).getCarImageUrl())
                            .placeholder(R.drawable.new_cars_image_placeholder)
                            .fitCenter()
                            .into(newCarImage);
                }
                newCarName.setText(tempNewCarsList.get(x).getCarName());
                newCarDescription.setText(tempNewCarsList.get(x).getCarDescription());
            }
        }

        newCarsModelList.clear();
        firebaseFirestore.collection("GARAGE").whereEqualTo("status", "New").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                newCarsModelList.add(new NewCarsModel(documentSnapshot.get("car_name").toString(), documentSnapshot.get("car_brand").toString(), documentSnapshot.get("car_subtitle").toString(), documentSnapshot.get("car_image_1").toString(), documentSnapshot.get("car_id").toString()));
                                if (newCarsModelList.size() == 4) {
                                    for (int x = 0; x < 4; x++) {
                                        ImageView newCarImage = newCarsGridview.getChildAt(x).findViewById(R.id.new_car_image);
                                        TextView newCarName = newCarsGridview.getChildAt(x).findViewById(R.id.new_car_name);
                                        TextView newCarDescription = newCarsGridview.getChildAt(x).findViewById(R.id.new_car_description);
                                        ConstraintLayout constraintLayout = newCarsGridview.getChildAt(x).findViewById(R.id.constraintLayout);

                                        newCarName.setTypeface(AppMainActivity.fontCircularProBold);
                                        newCarDescription.setTypeface(AppMainActivity.fontCircularProBold);

                                        if (getContext() != null) {
                                            Glide.with(getContext())
                                                    .load(newCarsModelList.get(x).getCarImageUrl())
                                                    .placeholder(R.drawable.new_cars_image_placeholder)
                                                    .fitCenter()
                                                    .into(newCarImage);
                                        }
                                        newCarName.setText(newCarsModelList.get(x).getCarName());
                                        newCarDescription.setText(newCarsModelList.get(x).getCarDescription());
                                        int finalX = x;
                                        constraintLayout.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Pair[] pairs = new Pair[1];
                                                pairs[0] = new Pair<View, String>(newCarImage, "testTransition");
                                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) getContext(), pairs);
                                                Intent CarDetailsIntent = new Intent(getContext(), CarDetailsActivity.class);
                                                CarDetailsIntent.putExtra("garage_car_id", newCarsModelList.get(finalX).getGarageCarId());
                                                getContext().startActivity(CarDetailsIntent, options.toBundle());
                                            }
                                        });
                                    }
                                }
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        ////////// New Cars GridView
    }

    private void loadUpComingCars() {
        ////////// Up Coming RecyclerView
        upcomingCarsRecyclerview.setItemTransitionTimeMillis(100);
        upcomingCarsRecyclerview.setHasFixedSize(true);
        upcomingCarsRecyclerview.setOffscreenItems(5);
        upcomingCarsRecyclerview.setItemTransformer(new ScaleTransformer.Builder()
                .setMaxScale(1.05f)
                .setMinScale(0.8f)
                .setPivotX(Pivot.X.CENTER) // CENTER is a default one
                .setPivotY(Pivot.Y.CENTER) // CENTER is a default one
                .build());

        List<UpcomingCarModel> tempUpcomingCarsList = new ArrayList<>();
        tempUpcomingCarsList.add(new UpcomingCarModel("", "", "", ""));
        tempUpcomingCarsList.add(new UpcomingCarModel("", "", "", ""));
        tempUpcomingCarsList.add(new UpcomingCarModel("", "", "", ""));
        tempUpcomingCarsList.add(new UpcomingCarModel("", "", "", ""));
        tempUpcomingCarsList.add(new UpcomingCarModel("", "", "", ""));
        tempUpcomingCarsList.add(new UpcomingCarModel("", "", "", ""));
        tempUpcomingCarsList.add(new UpcomingCarModel("", "", "", ""));
        UpComingCarAdapter tempUpcomingCarAdapter = new UpComingCarAdapter(getContext(), tempUpcomingCarsList, AppMainActivity.COLLECTION_IN_HOME_FRAGMENT, -1);
        tempUpcomingCarAdapter.setHasStableIds(true);
        upcomingCarsRecyclerview.setAdapter(tempUpcomingCarAdapter);

        upcomingCarModelList.clear();
        firebaseFirestore.collection("GARAGE").whereEqualTo("status", "Up Coming").limit(10).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                upcomingCarModelList.add(new UpcomingCarModel(documentSnapshot.get("car_name").toString(), documentSnapshot.get("car_brand").toString(), documentSnapshot.get("car_image_1").toString(), documentSnapshot.get("car_id").toString()));
                            }
                            UpComingCarAdapter upComingCarAdapter = new UpComingCarAdapter(getContext(), upcomingCarModelList, AppMainActivity.COLLECTION_IN_HOME_FRAGMENT, -1);
                            upComingCarAdapter.setHasStableIds(true);
                            upcomingCarsRecyclerview.setAdapter(upComingCarAdapter);
                            runLayoutAnimation(upcomingCarsRecyclerview);
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
        ////////// Up Coming RecyclerView
    }

}
