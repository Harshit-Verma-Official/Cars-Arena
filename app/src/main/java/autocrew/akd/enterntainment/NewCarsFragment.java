package autocrew.akd.enterntainment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class NewCarsFragment extends Fragment {


    public NewCarsFragment() {
        // Required empty public constructor
    }

//    private NestedScrollView nestedScrollView;
//    private SwipeRefreshLayout refreshLayout;

    private RecyclerView newCarsRecyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_cars, container, false);

        ////////// Initialization
//        nestedScrollView = view.findViewById(R.id.nested_scroll_view);
//        refreshLayout = view.findViewById(R.id.refresh_layout);
        newCarsRecyclerView = view.findViewById(R.id.new_cars_recyclerview);

        ////////// Initialization

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            nestedScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//                @Override
//                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                    if (scrollY == 0) {
//                        AppMainActivity.appBarLayout.setStateListAnimator(AnimatorInflater.loadStateListAnimator(getContext(), R.animator.appbar_elevation_off));
//                    } else {
//                        AppMainActivity.appBarLayout.setStateListAnimator(AnimatorInflater.loadStateListAnimator(getContext(), R.animator.appbar_elevation_on));
//                    }
//                }
//            });
//        }

//        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                refreshLayout.setRefreshing(true);
//                DBQueries.loadNewCars(getContext(), refreshLayout, newCarsRecyclerView);
//            }
//        });

        ////////// New Cars RecyclerView
        LinearLayoutManager newCarsLayoutManager = new LinearLayoutManager(getContext());
        newCarsLayoutManager.setOrientation(RecyclerView.VERTICAL);
        newCarsRecyclerView.setLayoutManager(newCarsLayoutManager);
        DBQueries.loadNewCars(getContext(), new SwipeRefreshLayout(getContext()), newCarsRecyclerView);
        ////////// New Cars RecyclerView
        return view;
    }

}
