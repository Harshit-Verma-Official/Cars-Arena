package autocrew.akd.enterntainment;


import android.animation.AnimatorInflater;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class WishlistFragment extends Fragment {


    public WishlistFragment() {
        // Required empty public constructor
    }

    public static RecyclerView wishListRecyclerView;
    public static SwipeRefreshLayout refreshLayout;
    private NestedScrollView nestedScrollView;

    public static LinearLayout emptyListLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);



        ////////// Initialization
        wishListRecyclerView = view.findViewById(R.id.wishList_cars_recyclerview);
        refreshLayout = view.findViewById(R.id.refresh_layout);
        nestedScrollView = view.findViewById(R.id.nested_scroll_view);
        emptyListLayout = view.findViewById(R.id.wishlist_empty_layout);
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


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                DBQueries.loadWishList(getContext(), refreshLayout, wishListRecyclerView, emptyListLayout);
            }
        });

        ///////// WishList RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        wishListRecyclerView.setLayoutManager(linearLayoutManager);
        wishListRecyclerView.setNestedScrollingEnabled(false);
        ///////// WishList RecyclerView

        wishListRecyclerView.setPadding(0,0,0,AppMainActivity.getSoftButtonsBarHeight());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        DBQueries.loadWishList(getContext(), refreshLayout, wishListRecyclerView, emptyListLayout);
    }
}
