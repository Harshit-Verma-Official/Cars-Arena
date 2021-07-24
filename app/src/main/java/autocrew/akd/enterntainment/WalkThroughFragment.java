package autocrew.akd.enterntainment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import static android.content.Context.MODE_PRIVATE;


public class WalkThroughFragment extends Fragment {


    public WalkThroughFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_walk_through, container, false);

//        ImageView carIconImageView = view.findViewById(R.id.car_icon_imageview);
//        Glide.with(getContext())
//                .load(R.drawable.car_icon)
//                .into(carIconImageView);

        Button signUpBtn = view.findViewById(R.id.signup_btn);
        Button logInBtn = view.findViewById(R.id.login_btn);
        TextView skipBtn = view.findViewById(R.id.skip_textview_btn);
        TextView appNameTextView = view.findViewById(R.id.appNameTextView);
        TextView appDescriptionTextView = view.findViewById(R.id.app_description_textView);
        appNameTextView.setTypeface(MainActivity.fontEarthOrbiterBold);
        appDescriptionTextView.setTypeface(MainActivity.fontCircularPro);

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putBoolean("IS_AUTHENTICATION_SKIPPED", true);
                myEdit.apply();

                Intent appMainActivityIntent = new Intent(getContext(), AppMainActivity.class);
                getContext().startActivity(appMainActivityIntent);
                getActivity().finish();
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.fromWalkThrough = true;
                setFragment(new SignUpFragment(), MainActivity.SIGNUP_FRAGMENT);
            }
        });

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.fromWalkThrough = true;
                setFragment(new LoginFragment(), MainActivity.LOGIN_FRAGMENT);
            }
        });

        return view;
    }

    private void setFragment(Fragment fragment, int currentPosition) {
        MainActivity.currentFragment = currentPosition;
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slideout_from_left);
        fragmentTransaction.replace(MainActivity.frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

}
