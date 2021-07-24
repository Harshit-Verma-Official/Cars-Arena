package autocrew.akd.enterntainment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    public SearchFragment() {
        // Required empty public constructor
    }

    private FirebaseFirestore firebaseFirestore;

    private RecyclerView searchRecyclerView;

    private List<PopularCarModel> searchCarList;

    private LottieAnimationView progressBar;
    private TextView noCarsTextView;

    private CollectionReference GARAGE;

    private DocumentSnapshot[] lastVisible = new DocumentSnapshot[1];
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;
    private boolean isLoading = false;

    private DocumentSnapshot[] lastVisible2 = new DocumentSnapshot[1];
    private boolean isScrolling2 = false;
    private boolean isLastItemReached2 = false;
    private boolean isLoading2 = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        ////////// Initialization
        searchRecyclerView = view.findViewById(R.id.search_recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        noCarsTextView = view.findViewById(R.id.noCarsTextView);

        firebaseFirestore = FirebaseFirestore.getInstance();

        GARAGE = firebaseFirestore.collection("GARAGE");

        searchCarList = new ArrayList<>();
        ////////// Initialization

        /////////// RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        searchRecyclerView.setLayoutManager(gridLayoutManager);

        GridCarsAdapter gridCarsAdapter = new GridCarsAdapter(getContext(), searchCarList);
        searchRecyclerView.setAdapter(gridCarsAdapter);
        gridCarsAdapter.notifyDataSetChanged();

        int spanCount = 2; // 3 columns
        int spacing;
        if (getResources().getString(R.string.density_bucket).equals("xxxhdpi")) {
            spacing = 30; // 50px
        } else if (getResources().getString(R.string.density_bucket).equals("xhdpi")) {
            spacing = 20;
        } else {
            spacing = 25;
        }
        boolean includeEdge = true;
        searchRecyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        /////////// RecyclerView

        if (AppMainActivity.searchView != null) {
            AppMainActivity.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    lastVisible = new DocumentSnapshot[1];
                    isScrolling = false;
                    isLastItemReached = false;
                    isLoading = false;

                    lastVisible2 = new DocumentSnapshot[1];
                    isScrolling2 = false;
                    isLastItemReached2 = false;
                    isLoading2 = false;


                    progressBar.playAnimation();
                    progressBar.setVisibility(View.VISIBLE);
                    noCarsTextView.setVisibility(View.GONE);
                    GARAGE.whereGreaterThanOrEqualTo("case_insensitive_car_name", newText.trim().toLowerCase())
                            .whereLessThanOrEqualTo("case_insensitive_car_name", newText.trim().toLowerCase() + "\uF7FF")
                            .limit(8)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        searchCarList.clear();
                                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                            searchCarList.add(new PopularCarModel(
                                                    documentSnapshot.get("car_name").toString(),
                                                    documentSnapshot.get("car_brand").toString(),
                                                    documentSnapshot.get("car_image_1").toString(),
                                                    documentSnapshot.get("car_id").toString()
                                            ));

                                            if (searchCarList.size() == task.getResult().size() && searchCarList.size() != 0) {
                                                gridCarsAdapter.notifyDataSetChanged();
                                                DBQueries.runLayoutAnimation(searchRecyclerView);
                                                progressBar.setVisibility(View.GONE);
                                                progressBar.cancelAnimation();

                                                lastVisible[0] = task.getResult().getDocuments().get(task.getResult().size() - 1);

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

                                                            if (!isLoading && lastVisible[0] != null) {
                                                                isLoading = true;
                                                                searchCarList.add(null);
                                                                recyclerView.getAdapter().notifyItemInserted(searchCarList.size() - 1);
                                                                int tempPos = searchCarList.size() - 1;
                                                                Query nextQuery = GARAGE.whereGreaterThanOrEqualTo("case_insensitive_car_name", newText.trim().toLowerCase())
                                                                        .whereLessThanOrEqualTo("case_insensitive_car_name", newText.trim().toLowerCase() + "\uF7FF")
                                                                        .startAfter(lastVisible[0])
                                                                        .limit(8);
                                                                nextQuery.get()
                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                                                        searchCarList.add(new PopularCarModel(
                                                                                                snapshot.get("car_name").toString(),
                                                                                                snapshot.get("car_brand").toString(),
                                                                                                snapshot.get("car_image_1").toString(),
                                                                                                snapshot.get("car_id").toString()
                                                                                        ));
                                                                                    }
                                                                                    if (tempPos < searchCarList.size()) {
                                                                                        searchCarList.remove(tempPos);
                                                                                        recyclerView.getAdapter().notifyItemRemoved(tempPos);
                                                                                    }
                                                                                    recyclerView.getAdapter().notifyDataSetChanged();
                                                                                    if ((task.getResult().size() - 1) != -1) {
                                                                                        lastVisible[0] = task.getResult().getDocuments().get(task.getResult().size() - 1);
                                                                                    }
                                                                                    isLoading = false;

                                                                                    if (task.getResult().size() < 8) {
                                                                                        isLastItemReached = true;
                                                                                    }
                                                                                } else {
                                                                                    String error = task.getException().getMessage();
                                                                                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    }
                                                };
                                                searchRecyclerView.addOnScrollListener(onScrollListener);
                                            }
                                        }
                                        if (searchCarList.size() == 0 && !newText.equals("")) {
                                            GARAGE.whereGreaterThanOrEqualTo("case_insensitive_car_brand", newText.trim().toLowerCase())
                                                    .whereLessThanOrEqualTo("case_insensitive_car_brand", newText.trim().toLowerCase() + "\uF7FF")
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                searchCarList.clear();
                                                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                                    searchCarList.add(new PopularCarModel(
                                                                            documentSnapshot.get("car_name").toString(),
                                                                            documentSnapshot.get("car_brand").toString(),
                                                                            documentSnapshot.get("car_image_1").toString(),
                                                                            documentSnapshot.get("car_id").toString()
                                                                    ));

                                                                    if (searchCarList.size() == task.getResult().size() && searchCarList.size() != 0) {
                                                                        gridCarsAdapter.notifyDataSetChanged();
                                                                        DBQueries.runLayoutAnimation(searchRecyclerView);
                                                                        progressBar.setVisibility(View.GONE);
                                                                        progressBar.cancelAnimation();

                                                                        if ((task.getResult().size() - 1) != -1) {
                                                                            lastVisible2[0] = task.getResult().getDocuments().get(task.getResult().size() - 1);
                                                                        }

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

                                                                                GridLayoutManager linearLayoutManager = ((GridLayoutManager) recyclerView.getLayoutManager());
                                                                                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                                                                                int visibleItemCount = linearLayoutManager.getChildCount();
                                                                                int totalItemCount = linearLayoutManager.getItemCount();

                                                                                if (isScrolling2 && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached2) {
                                                                                    isScrolling2 = false;

                                                                                    if (!isLoading2 && lastVisible2[0] != null) {
                                                                                        isLoading2 = true;
                                                                                        searchCarList.add(null);
                                                                                        recyclerView.getAdapter().notifyItemInserted(searchCarList.size() - 1);
                                                                                        int tempPos = searchCarList.size() - 1;
                                                                                        Query nextQuery = GARAGE.whereGreaterThanOrEqualTo("case_insensitive_car_brand", newText.trim().toLowerCase())
                                                                                                .whereLessThanOrEqualTo("case_insensitive_car_brand", newText.trim().toLowerCase() + "\uF7FF")
                                                                                                .startAfter(lastVisible2[0])
                                                                                                .limit(8);
                                                                                        nextQuery.get()
                                                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                        if (task.isSuccessful()) {
                                                                                                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                                                                                searchCarList.add(new PopularCarModel(
                                                                                                                        snapshot.get("car_name").toString(),
                                                                                                                        snapshot.get("car_brand").toString(),
                                                                                                                        snapshot.get("car_image_1").toString(),
                                                                                                                        snapshot.get("car_id").toString()
                                                                                                                ));
                                                                                                            }
                                                                                                            if (tempPos < searchCarList.size()) {
                                                                                                                searchCarList.remove(tempPos);
                                                                                                                recyclerView.getAdapter().notifyItemRemoved(tempPos);
                                                                                                            }
                                                                                                            recyclerView.getAdapter().notifyDataSetChanged();
                                                                                                            lastVisible2[0] = task.getResult().getDocuments().get(task.getResult().size() - 1);
                                                                                                            isLoading2 = false;

                                                                                                            if (task.getResult().size() < 8) {
                                                                                                                isLastItemReached2 = true;
                                                                                                            }
                                                                                                        } else {
                                                                                                            String error = task.getException().getMessage();
                                                                                                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                                                                        }
                                                                                                    }
                                                                                                });
                                                                                    }
                                                                                }
                                                                            }
                                                                        };
                                                                        searchRecyclerView.addOnScrollListener(onScrollListener);
                                                                    }
                                                                }
                                                                if (searchCarList.size() == 0) {
                                                                    noCarsTextView.setText("No cars available named \"" + newText + "\'");
                                                                    noCarsTextView.setVisibility(View.VISIBLE);
                                                                } else {
                                                                    noCarsTextView.setVisibility(View.GONE);
                                                                }
                                                                gridCarsAdapter.notifyDataSetChanged();
                                                                DBQueries.runLayoutAnimation(searchRecyclerView);
                                                            } else {
                                                                String error = task.getException().getMessage();
                                                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            noCarsTextView.setVisibility(View.GONE);
                                        }
                                        gridCarsAdapter.notifyDataSetChanged();
                                        DBQueries.runLayoutAnimation(searchRecyclerView);
                                        progressBar.setVisibility(View.GONE);
                                        progressBar.cancelAnimation();
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    return true;
                }
            });
        }
        return view;
    }
}
