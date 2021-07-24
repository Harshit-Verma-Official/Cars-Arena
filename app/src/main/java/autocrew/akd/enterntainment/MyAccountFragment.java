package autocrew.akd.enterntainment;


import android.animation.AnimatorInflater;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyAccountFragment extends Fragment {

    public MyAccountFragment() {
        // Required empty public constructor
    }

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private NestedScrollView nestedScrollView;
    private ImageView userImage;
    private TextView userName;
    private TextView userUserName;
    private TextView userEmail;
    private Button signOutButton;

    private ConstraintLayout editProfileBtn;
    private ConstraintLayout myWishListBtn;
    private TextView wishListHeadingTextView;
    private TextView editProfileHeadingTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);

        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Circular Pro_semi_bold.otf");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        // Variables Initialization
        nestedScrollView = view.findViewById(R.id.nested_scroll_view);
        userImage = view.findViewById(R.id.user_profile_pic);
        userEmail = view.findViewById(R.id.user_email);
        userName = view.findViewById(R.id.user_name);
        userUserName = view.findViewById(R.id.user_user_name);
        editProfileBtn = view.findViewById(R.id.edit_profile_btn);
        myWishListBtn = view.findViewById(R.id.my_wishlist_btn);
        signOutButton = view.findViewById(R.id.sign_out_btn);
        wishListHeadingTextView = view.findViewById(R.id.my_wishlist_heading_textView);
        editProfileHeadingTextView = view.findViewById(R.id.edit_profile_heading_textView);
        userEmail.setTypeface(font);
        userName.setTypeface(font);
        wishListHeadingTextView.setTypeface(AppMainActivity.fontTypoRoundBold);
        editProfileHeadingTextView.setTypeface(AppMainActivity.fontTypoRoundBold);
        // Variables Initialization

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


        Glide.with(getContext())
                .load(R.drawable.batman)
                .fitCenter()
                .into(userImage);

        myWishListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent carsGridIntent = new Intent(getContext(), CarsGridActivity.class);
                carsGridIntent.putExtra("fieldName", "wishList");
                carsGridIntent.putExtra("fieldValue", "My Wishlist");
                getContext().startActivity(carsGridIntent);
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editProfileIntent = new Intent(getContext(), MainActivity.class);
                MainActivity.fromEditProfile = true;
                AppMainActivity.isFromMyAccountBtn = true;
                MainActivity.isAppMainActivityOpen = false;
                MainActivity.fromAppMainActivity = true;
                getContext().startActivity(editProfileIntent);
            }
        });

        signOutButton.setTypeface(AppMainActivity.fontCircularProBold);

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppMainActivity.signOut(getContext(), firebaseAuth);
                AppMainActivity.isFromMyAccountBtn = true;
                MainActivity.isAppMainActivityOpen = false;
                MainActivity.fromAppMainActivity = true;
                Intent registerIntent = new Intent(getContext(), MainActivity.class);
                startActivity(registerIntent);
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            firebaseFirestore.collection("USERS").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        final String userProfilePicUrl = documentSnapshot.get("user_profile_pic").toString();
                        String userFirstName = documentSnapshot.get("first_name").toString();
                        String userLastName = documentSnapshot.get("last_name").toString();
                        String userEmailAddress = documentSnapshot.get("email").toString();
                        String userAppUserName = documentSnapshot.get("user_name").toString();
                        userEmail.setText(userEmailAddress);
                        userName.setText(userFirstName + " " + userLastName);
                        userUserName.setText("(" + userAppUserName + ")");
                        if (getContext() != null) {
                            if (!userProfilePicUrl.equals("")) {
                                Glide.with(getContext())
                                        .load(userProfilePicUrl)
                                        .centerCrop()
                                        .apply(RequestOptions.circleCropTransform())
                                        .transition(DrawableTransitionOptions.withCrossFade(500))
                                        .into(userImage);
                                userImage.setPadding(8, 8, 8, 8);
                            } else {
                                Glide.with(getContext())
                                        .load(R.drawable.batman)
                                        .fitCenter()
                                        .transition(DrawableTransitionOptions.withCrossFade(500))
                                        .into(userImage);
                                userImage.setPadding(28, 28, 28, 28);
                            }
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
