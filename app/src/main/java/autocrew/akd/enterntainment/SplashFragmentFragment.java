package autocrew.akd.enterntainment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class SplashFragmentFragment extends Fragment {


    public SplashFragmentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_splash, container, false);

        TextView appNameHeading = view.findViewById(R.id.app_name_textview);
        appNameHeading.setTypeface(MainActivity.fontEarthOrbiterBold);

        return view;
    }

}
