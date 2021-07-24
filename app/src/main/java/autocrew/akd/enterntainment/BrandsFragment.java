package autocrew.akd.enterntainment;


import android.animation.AnimatorInflater;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;


public class BrandsFragment extends Fragment {


    public BrandsFragment() {
        // Required empty public constructor
    }

    private SwipeRefreshLayout refreshLayout;
    private NestedScrollView nestedScrollView;
    private ConstraintLayout constraintLayout;

    private TextView brandHeading;
    private TextView typeHeading;
    private RecyclerView brandRecyclerView;
    private RecyclerView typeRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_brands, container, false);

        ////////// Initialization
        nestedScrollView = view.findViewById(R.id.nested_scroll_view);
        refreshLayout = view.findViewById(R.id.refresh_layout);
        brandHeading = view.findViewById(R.id.brand_heading);
        typeHeading = view.findViewById(R.id.type_heading);
        brandRecyclerView = view.findViewById(R.id.brand_recyclerView);
        typeRecyclerView = view.findViewById(R.id.type_recyclerView);
        constraintLayout = view.findViewById(R.id.constraintLayout);

        brandHeading.setTypeface(AppMainActivity.fontIndustryUltraItalic);
        typeHeading.setTypeface(AppMainActivity.fontIndustryUltraItalic);
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

        ////////// Brands Section
        reloadData();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadData();
            }
        });
        GridLayoutManager brandsGridLayoutManager = new GridLayoutManager(getContext(), 3);
        brandRecyclerView.setLayoutManager(brandsGridLayoutManager);
        brandRecyclerView.setNestedScrollingEnabled(false);
        ////////// Brands Section

        ////////// Car Type Section
        GridLayoutManager carTypeGridLayoutManager = new GridLayoutManager(getContext(), 2);
        typeRecyclerView.setLayoutManager(carTypeGridLayoutManager);
        typeRecyclerView.setNestedScrollingEnabled(false);
        ////////// Car Type Section

        typeRecyclerView.setPadding(0, 0, 0, AppMainActivity.getSoftButtonsBarHeight());

        return view;
    }

    private void reloadData() {
        DBQueries.brandsModelList.clear();
        DBQueries.carTypeModelList.clear();
        loadBrandsData();
        loadCarTypeData();
    }

    private void loadBrandsData() {
        List<BrandsModel> tempBrandsModelList = new ArrayList<>();
        tempBrandsModelList.clear();
        tempBrandsModelList.add(new BrandsModel("", "", ""));
        tempBrandsModelList.add(new BrandsModel("", "", ""));
        tempBrandsModelList.add(new BrandsModel("", "", ""));
        tempBrandsModelList.add(new BrandsModel("", "", ""));
        tempBrandsModelList.add(new BrandsModel("", "", ""));
        tempBrandsModelList.add(new BrandsModel("", "", ""));
        tempBrandsModelList.add(new BrandsModel("", "", ""));
        tempBrandsModelList.add(new BrandsModel("", "", ""));
        tempBrandsModelList.add(new BrandsModel("", "", ""));
        tempBrandsModelList.add(new BrandsModel("", "", ""));
        tempBrandsModelList.add(new BrandsModel("", "", ""));
        tempBrandsModelList.add(new BrandsModel("", "", ""));
        tempBrandsModelList.add(new BrandsModel("", "", ""));
        tempBrandsModelList.add(new BrandsModel("", "", ""));
        tempBrandsModelList.add(new BrandsModel("", "", ""));
        tempBrandsModelList.add(new BrandsModel("", "", ""));
        tempBrandsModelList.add(new BrandsModel("", "", ""));
        tempBrandsModelList.add(new BrandsModel("", "", ""));
        BrandsAdapter tempBrandsAdapter = new BrandsAdapter(getContext(), tempBrandsModelList);
        brandRecyclerView.setAdapter(tempBrandsAdapter);
        DBQueries.runLayoutAnimation(brandRecyclerView);
        tempBrandsAdapter.notifyDataSetChanged();
        DBQueries.loadBrandsData(getContext(), refreshLayout, brandRecyclerView);
    }

    private void loadCarTypeData() {
        List<CarTypeModel> tempCarTypesList = new ArrayList<>();
        tempCarTypesList.clear();
        tempCarTypesList.add(new CarTypeModel("", ""));
        tempCarTypesList.add(new CarTypeModel("", ""));
        tempCarTypesList.add(new CarTypeModel("", ""));
        tempCarTypesList.add(new CarTypeModel("", ""));
        tempCarTypesList.add(new CarTypeModel("", ""));
        tempCarTypesList.add(new CarTypeModel("", ""));
        tempCarTypesList.add(new CarTypeModel("", ""));
        tempCarTypesList.add(new CarTypeModel("", ""));
        tempCarTypesList.add(new CarTypeModel("", ""));
        tempCarTypesList.add(new CarTypeModel("", ""));
        tempCarTypesList.add(new CarTypeModel("", ""));
        tempCarTypesList.add(new CarTypeModel("", ""));
        CarTypesAdapter tempCarTypeAdapter = new CarTypesAdapter(getContext(), tempCarTypesList);
        typeRecyclerView.setAdapter(tempCarTypeAdapter);
        DBQueries.runLayoutAnimation(typeRecyclerView);
        tempCarTypeAdapter.notifyDataSetChanged();
        DBQueries.loadCarTypesData(getContext(), typeRecyclerView);
    }
}
