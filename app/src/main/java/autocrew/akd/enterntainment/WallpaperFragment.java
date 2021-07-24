package autocrew.akd.enterntainment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class WallpaperFragment extends Fragment {

    public WallpaperFragment() {
        // Required empty public constructor
    }

    //    private SwipeRefreshLayout refreshLayout;
//    private NestedScrollView nestedScrollView;
    public static RecyclerView wallpaperRecyclerView;
    private WallpaperAdapter wallpaperAdapter;


    private boolean isLoading = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallpaper, container, false);

        ////////// Initialization
//        nestedScrollView = view.findViewById(R.id.nested_scroll_view);
//        refreshLayout = view.findViewById(R.id.refresh_layout);
        wallpaperRecyclerView = view.findViewById(R.id.wallpaper_recyclerView);
        ////////// Initialization

        ////////// RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        wallpaperRecyclerView.setLayoutManager(gridLayoutManager);

        wallpaperAdapter = new WallpaperAdapter(getContext(), DBQueries.wallpaperModelList);
        wallpaperRecyclerView.setAdapter(wallpaperAdapter);
        wallpaperAdapter.notifyDataSetChanged();
        ////////// RecyclerView

        DBQueries.loadWallpapers(getContext(), new SwipeRefreshLayout(getContext()), wallpaperRecyclerView);

        return view;
    }
}
