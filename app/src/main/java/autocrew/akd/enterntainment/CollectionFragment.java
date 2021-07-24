package autocrew.akd.enterntainment;


import android.animation.AnimatorInflater;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class CollectionFragment extends Fragment {


    public CollectionFragment() {
        // Required empty public constructor
    }

    private NestedScrollView nestedScrollView;
    private RecyclerView carCollectionRecyclerView;
    private SwipeRefreshLayout refreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_collection, container, false);

        ////////// Initialization
        nestedScrollView = view.findViewById(R.id.nested_scroll_view);
        refreshLayout = view.findViewById(R.id.refresh_layout);
        carCollectionRecyclerView = view.findViewById(R.id.car_collection_recyclerview);

        ////////// Initialization

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


        ////////// Car Collection RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        carCollectionRecyclerView.setLayoutManager(layoutManager);
        carCollectionRecyclerView.setNestedScrollingEnabled(false);
        DBQueries.loadCarCollections(getContext(), refreshLayout, carCollectionRecyclerView);
        ////////// Car Collection RecyclerView

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                DBQueries.loadCarCollections(getContext(), refreshLayout, carCollectionRecyclerView);
            }
        });

        carCollectionRecyclerView.setPadding(0,0,0,AppMainActivity.getSoftButtonsBarHeight());

        return view;
    }

}
