package autocrew.akd.enterntainment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class UpComingFragment extends Fragment {

    public UpComingFragment() {
        // Required empty public constructor
    }

    private RecyclerView upcomingCarsRecyclerview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_up_coming, container, false);

        ////////// Initialization
        upcomingCarsRecyclerview = view.findViewById(R.id.upcoming_cars_recyclerview);
        ////////// Initialization

        ////////// Upcoming Cars RecyclerView
        LinearLayoutManager upcomingCarsLayoutManager = new LinearLayoutManager(getContext());
        upcomingCarsLayoutManager.setOrientation(RecyclerView.VERTICAL);
        upcomingCarsRecyclerview.setLayoutManager(upcomingCarsLayoutManager);
        DBQueries.loadUpcomingCars(getContext(), upcomingCarsRecyclerview);
        ////////// Upcoming Cars RecyclerView
        return view;
    }

}
