package autocrew.akd.enterntainment;

import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DBQueries {
    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public static List<BrandsModel> brandsModelList = new ArrayList<>();
    public static List<CarTypeModel> carTypeModelList = new ArrayList<>();
    public static List<ExploreCarsModel> exploreCarsModelList = new ArrayList<>();
    public static List<NewCarsModel> newCarsModelList = new ArrayList<>();
    public static List<UpcomingCarModel> upcomingCarModelList = new ArrayList<>();
    public static List<UpcomingCarModel> myWishList = new ArrayList<>();
    public static List<WallpaperModel> wallpaperModelList = new ArrayList<>();

    public static DocumentSnapshot lastVisible;
    public static boolean isScrolling = false;
    public static boolean isLastItemReached = false;
    public static boolean isLoading = false;

    public static DocumentSnapshot lastVisible2;
    public static boolean isScrolling2 = false;
    public static boolean isLastItemReached2 = false;
    public static boolean isLoading2 = false;

    public static DocumentSnapshot lastVisible3;
    public static boolean isScrolling3 = false;
    public static boolean isLastItemReached3 = false;
    public static boolean isLoading3 = false;


    public static void loadBrandsData(final Context context, final SwipeRefreshLayout refreshLayout, final RecyclerView recyclerView) {
        brandsModelList.clear();
        firebaseFirestore.collection("CAR_BRANDS")
                .orderBy("index", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                brandsModelList.add(new BrandsModel(documentSnapshot.get("brand_logo_url").toString(), documentSnapshot.get("brand_back_image_url").toString(), documentSnapshot.get("brand_name").toString()));
                            }
                            BrandsAdapter brandsAdapter = new BrandsAdapter(context, DBQueries.brandsModelList);
                            recyclerView.setAdapter(brandsAdapter);
                            brandsAdapter.notifyDataSetChanged();
                            runLayoutAnimation(recyclerView);
                            refreshLayout.setRefreshing(false);
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void loadCarTypesData(final Context context, final RecyclerView recyclerView) {
        carTypeModelList.clear();
        firebaseFirestore.collection("CAR_TYPES").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        carTypeModelList.add(new CarTypeModel(documentSnapshot.get("type_title").toString(), documentSnapshot.get("type_image_url").toString()));
                    }
                    CarTypesAdapter carTypesAdapter = new CarTypesAdapter(context, carTypeModelList);
                    recyclerView.setAdapter(carTypesAdapter);
                    carTypesAdapter.notifyDataSetChanged();
                    runLayoutAnimation(recyclerView);
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_simple_fade);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    public static void loadCarCollections(final Context context, final SwipeRefreshLayout refreshLayout, final RecyclerView recyclerView) {
        exploreCarsModelList.clear();
        ////////// Explore car RecyclerView
        List<ExploreCarsModel> tempExploreModelList = new ArrayList<>();
        tempExploreModelList.add(new ExploreCarsModel("", ""));
        tempExploreModelList.add(new ExploreCarsModel("", ""));
        tempExploreModelList.add(new ExploreCarsModel("", ""));
        tempExploreModelList.add(new ExploreCarsModel("", ""));
        tempExploreModelList.add(new ExploreCarsModel("", ""));
        ExploreCarsAdapter tempExploreCarsAdapter = new ExploreCarsAdapter(context, AppMainActivity.COLLECTION_IN_MAIN_FRAGMENT, tempExploreModelList);
        recyclerView.setAdapter(tempExploreCarsAdapter);

        LinearLayoutManager exploreCarsLayoutManager = new LinearLayoutManager(context);
        exploreCarsLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(exploreCarsLayoutManager);
        firebaseFirestore.collection("CARS_CATEGORIES").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                exploreCarsModelList.add(new ExploreCarsModel(documentSnapshot.get("category_name").toString(), documentSnapshot.get("image_url").toString()));
                            }
                            ExploreCarsAdapter exploreCarsAdapter = new ExploreCarsAdapter(context, AppMainActivity.COLLECTION_IN_MAIN_FRAGMENT, exploreCarsModelList);
                            recyclerView.setAdapter(exploreCarsAdapter);
                            runLayoutAnimation(recyclerView);
                            refreshLayout.setRefreshing(false);
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        ////////// Explore car RecyclerView
    }

    public static void loadNewCars(final Context context, final SwipeRefreshLayout refreshLayout, final RecyclerView recyclerView) {
        List<NewCarsModel> tempNewCarsList = new ArrayList<>();
        tempNewCarsList.clear();
        tempNewCarsList.add(new NewCarsModel("", "", "", "", ""));
        tempNewCarsList.add(new NewCarsModel("", "", "", "", ""));
        tempNewCarsList.add(new NewCarsModel("", "", "", "", ""));
        tempNewCarsList.add(new NewCarsModel("", "", "", "", ""));
        tempNewCarsList.add(new NewCarsModel("", "", "", "", ""));
        tempNewCarsList.add(new NewCarsModel("", "", "", "", ""));
        tempNewCarsList.add(new NewCarsModel("", "", "", "", ""));
        NewCarsAdapter tempNewCarsAdapter = new NewCarsAdapter(context, tempNewCarsList);
        recyclerView.setAdapter(tempNewCarsAdapter);
        tempNewCarsAdapter.notifyDataSetChanged();

        newCarsModelList.clear();
        firebaseFirestore.collection("GARAGE")
                .whereEqualTo("status", "New")
                .limit(15)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                newCarsModelList.add(new NewCarsModel(documentSnapshot.get("car_name").toString(), documentSnapshot.get("car_brand").toString(), documentSnapshot.get("car_subtitle").toString(), documentSnapshot.get("car_image_1").toString(), documentSnapshot.get("car_id").toString()));

                                if (newCarsModelList.size() == task.getResult().size()) {
                                    NewCarsAdapter newCarsAdapter = new NewCarsAdapter(context, newCarsModelList);
                                    recyclerView.setAdapter(newCarsAdapter);
                                    newCarsAdapter.notifyDataSetChanged();
                                    runLayoutAnimation(recyclerView);
                                    refreshLayout.setRefreshing(false);

                                    lastVisible2 = task.getResult().getDocuments().get(task.getResult().size() - 1);
                                    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                                        @Override
                                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                            super.onScrollStateChanged(recyclerView, newState);
                                            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                                isScrolling2 = true;
                                            }
                                        }

                                        @Override
                                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                            super.onScrolled(recyclerView, dx, dy);

                                            LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                                            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                                            int visibleItemCount = linearLayoutManager.getChildCount();
                                            int totalItemCount = linearLayoutManager.getItemCount();
                                            if (isScrolling2 && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached2) {
                                                isScrolling2 = false;

                                                if (!isLoading2) {
                                                    isLoading2 = true;
                                                    DBQueries.newCarsModelList.add(null);
                                                    recyclerView.getAdapter().notifyItemInserted(DBQueries.newCarsModelList.size() - 1);
                                                    int tempPos = DBQueries.newCarsModelList.size() - 1;

                                                    Query nextQuery = firebaseFirestore.collection("GARAGE")
                                                            .whereEqualTo("status", "New")
                                                            .startAfter(lastVisible2)
                                                            .limit(15);
                                                    nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                                    DBQueries.newCarsModelList.add(new NewCarsModel(snapshot.get("car_name").toString(), snapshot.get("car_brand").toString(), snapshot.get("car_subtitle").toString(), snapshot.get("car_image_1").toString(), snapshot.get("car_id").toString()));
                                                                    recyclerView.getAdapter().notifyItemInserted(DBQueries.newCarsModelList.size() - 1);
                                                                }
                                                                DBQueries.newCarsModelList.remove(tempPos);
                                                                recyclerView.getAdapter().notifyItemRemoved(tempPos);
                                                                if ((task.getResult().size() - 1) != -1) {
                                                                    lastVisible2 = task.getResult().getDocuments().get(task.getResult().size() - 1);
                                                                }
                                                                isLoading2 = false;
                                                                if (task.getResult().size() < 15) {
                                                                    isLastItemReached2 = true;
                                                                }
                                                            } else {
                                                                String error = task.getException().getMessage();
                                                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    };
                                    recyclerView.addOnScrollListener(onScrollListener);
                                }
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void loadUpcomingCars(final Context context, final RecyclerView recyclerView) {
        List<UpcomingCarModel> tempUpcomingCarsList = new ArrayList<>();
        tempUpcomingCarsList.clear();
        tempUpcomingCarsList.add(new UpcomingCarModel("", "", "", ""));
        tempUpcomingCarsList.add(new UpcomingCarModel("", "", "", ""));
        tempUpcomingCarsList.add(new UpcomingCarModel("", "", "", ""));
        tempUpcomingCarsList.add(new UpcomingCarModel("", "", "", ""));
        tempUpcomingCarsList.add(new UpcomingCarModel("", "", "", ""));
        tempUpcomingCarsList.add(new UpcomingCarModel("", "", "", ""));
        tempUpcomingCarsList.add(new UpcomingCarModel("", "", "", ""));
        UpComingCarAdapter tempUpcomingCarAdapter = new UpComingCarAdapter(context, tempUpcomingCarsList, AppMainActivity.UPCOMING_IN_MAIN_FRAGMENT, AppMainActivity.TYPE_UPCOMING);
        tempUpcomingCarAdapter.setHasStableIds(true);
        recyclerView.setAdapter(tempUpcomingCarAdapter);

        upcomingCarModelList.clear();
        firebaseFirestore.collection("GARAGE")
                .whereEqualTo("status", "Up Coming")
                .limit(15)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                upcomingCarModelList.add(new UpcomingCarModel(documentSnapshot.get("car_name").toString(), documentSnapshot.get("car_brand").toString(), documentSnapshot.get("car_image_1").toString(), documentSnapshot.get("car_id").toString()));

                                if (upcomingCarModelList.size() == task.getResult().size()) {
                                    UpComingCarAdapter upComingCarAdapter = new UpComingCarAdapter(context, upcomingCarModelList, AppMainActivity.UPCOMING_IN_MAIN_FRAGMENT, AppMainActivity.TYPE_UPCOMING);
                                    recyclerView.setAdapter(upComingCarAdapter);
                                    runLayoutAnimation(recyclerView);

                                    lastVisible3 = task.getResult().getDocuments().get(task.getResult().size() - 1);

                                    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                                        @Override
                                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                            super.onScrollStateChanged(recyclerView, newState);
                                            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                                isScrolling3 = true;
                                            }
                                        }

                                        @Override
                                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                            super.onScrolled(recyclerView, dx, dy);

                                            LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                                            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                                            int visibleItemCount = linearLayoutManager.getChildCount();
                                            int totalItemCount = linearLayoutManager.getItemCount();
                                            if (isScrolling3 && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached3) {
                                                isScrolling3 = false;

                                                if (!isLoading3) {
                                                    isLoading3 = true;
                                                    DBQueries.upcomingCarModelList.add(null);
                                                    recyclerView.getAdapter().notifyItemInserted(DBQueries.upcomingCarModelList.size() - 1);
                                                    int tempPos = DBQueries.upcomingCarModelList.size() - 1;

                                                    Query nextQuery = firebaseFirestore.collection("GARAGE")
                                                            .whereEqualTo("status", "Up Coming")
                                                            .startAfter(lastVisible3)
                                                            .limit(15);
                                                    nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                                    DBQueries.upcomingCarModelList.add(new UpcomingCarModel(snapshot.get("car_name").toString(), snapshot.get("car_brand").toString(), snapshot.get("car_image_1").toString(), snapshot.get("car_id").toString()));
                                                                    recyclerView.getAdapter().notifyItemInserted(DBQueries.upcomingCarModelList.size() - 1);
                                                                }
                                                                DBQueries.upcomingCarModelList.remove(tempPos);
                                                                recyclerView.getAdapter().notifyItemRemoved(tempPos);
                                                                if ((task.getResult().size() - 1) != -1) {
                                                                    lastVisible3 = task.getResult().getDocuments().get(task.getResult().size() - 1);
                                                                }
                                                                isLoading3 = false;
                                                                if (task.getResult().size() < 15) {
                                                                    isLastItemReached3 = true;
                                                                }
                                                            } else {
                                                                String error = task.getException().getMessage();
                                                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    };
                                    recyclerView.addOnScrollListener(onScrollListener);
                                }
                            }

                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void loadWishList(final Context context, final SwipeRefreshLayout refreshLayout, final RecyclerView recyclerView, LinearLayout emptyListLayout) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            myWishList.clear();
            firebaseFirestore.collection("USERS")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .collection("WISHLIST")
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                int tempSize = task.getResult().size();
                                Task<QuerySnapshot> tempTask = task;
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
                                                        myWishList.add(new UpcomingCarModel(snapshot.get("car_name").toString(), snapshot.get("car_brand").toString(), snapshot.get("car_image_1").toString(), snapshot.get("car_id").toString()));
                                                    } else {
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                                    }
                                                    if (myWishList.size() == tempSize) {
                                                        UpComingCarAdapter myWishListAdapter = new UpComingCarAdapter(context, myWishList, AppMainActivity.UPCOMING_IN_MAIN_FRAGMENT, AppMainActivity.TYPE_WISHLIST);
                                                        recyclerView.setAdapter(myWishListAdapter);
                                                        ((LottieAnimationView) emptyListLayout.getChildAt(0)).cancelAnimation();
                                                        emptyListLayout.setVisibility(View.GONE);
                                                        runLayoutAnimation(recyclerView);
                                                    }
                                                }
                                            });
                                }
                                if (myWishList.size() == 0) {
                                    UpComingCarAdapter myWishListAdapter = new UpComingCarAdapter(context, myWishList, AppMainActivity.UPCOMING_IN_MAIN_FRAGMENT, AppMainActivity.TYPE_WISHLIST);
                                    recyclerView.setAdapter(myWishListAdapter);
                                    ((LottieAnimationView) emptyListLayout.getChildAt(0)).playAnimation();
                                    emptyListLayout.setVisibility(View.VISIBLE);
                                    runLayoutAnimation(recyclerView);
                                }
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                            }
                            refreshLayout.setRefreshing(false);
                        }
                    });
        }
    }

    public static void loadWallpapers(Context context, SwipeRefreshLayout refreshLayout, RecyclerView recyclerView) {
        firebaseFirestore.collection("WALLPAPERS")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(15)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            wallpaperModelList.clear();
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                wallpaperModelList.add(new WallpaperModel(documentSnapshot.get("imageUrl4k").toString(), documentSnapshot.get("imageUrl1080p").toString(), documentSnapshot.get("imageUrl720p").toString(), documentSnapshot.get("imageUrl480p").toString(), documentSnapshot.get("image_id").toString(), (Long) documentSnapshot.get("downloads"), (Long) documentSnapshot.get("views"), documentSnapshot.get("resolution").toString()));
                                if (wallpaperModelList.size() == task.getResult().size()) {
                                    WallpaperAdapter wallpaperAdapter = new WallpaperAdapter(context, wallpaperModelList);
                                    recyclerView.setAdapter(wallpaperAdapter);
                                    wallpaperAdapter.notifyDataSetChanged();
                                    runLayoutAnimation(recyclerView);
                                    refreshLayout.setRefreshing(false);

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
                                                    DBQueries.wallpaperModelList.add(null);
                                                    recyclerView.getAdapter().notifyItemInserted(DBQueries.wallpaperModelList.size() - 1);
                                                    recyclerView.getAdapter().notifyDataSetChanged();
                                                    int tempPos = DBQueries.wallpaperModelList.size() - 1;


                                                    Query nextQuery = firebaseFirestore.collection("WALLPAPERS").orderBy("timestamp", Query.Direction.DESCENDING).startAfter(lastVisible).limit(15);
                                                    nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                                            if (t.isSuccessful()) {
                                                                for (DocumentSnapshot d : t.getResult()) {
                                                                    wallpaperModelList.add(new WallpaperModel(d.get("imageUrl4k").toString(), d.get("imageUrl1080p").toString(), d.get("imageUrl720p").toString(), d.get("imageUrl480p").toString(), d.get("image_id").toString(), (Long) d.get("downloads"), (Long) d.get("views"), d.get("resolution").toString()));
                                                                }
                                                                wallpaperModelList.remove(tempPos);
                                                                recyclerView.getAdapter().notifyItemRemoved(tempPos);
                                                                recyclerView.getAdapter().notifyDataSetChanged();
                                                                if ((t.getResult().size() - 1) != -1) {
                                                                    lastVisible = t.getResult().getDocuments().get(t.getResult().size() - 1);
                                                                }
                                                                isLoading = false;
                                                                if (FullScreenWallpaperActivity.wallpaperViewPager != null) {
                                                                    FullScreenWallpaperActivity.wallpaperViewPager.getAdapter().notifyDataSetChanged();
                                                                }

                                                                if (t.getResult().size() < 15) {
                                                                    isLastItemReached = true;
                                                                }
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    };
                                    recyclerView.addOnScrollListener(onScrollListener);
                                }
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
