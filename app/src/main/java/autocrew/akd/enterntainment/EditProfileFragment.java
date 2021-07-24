package autocrew.akd.enterntainment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class EditProfileFragment extends Fragment {


    public EditProfileFragment() {
        // Required empty public constructor
    }

    private Context context;

    private EditText firstName;
    private EditText lastName;
    private EditText userName;
    private TextView dateOfBirth;
    private EditText userEmail;
    private Button saveProfileBtn;
    private ImageView uploadImageButton;
    private ImageView userProfilePic;

    private Dialog loadingDialog;

    private int mYear, mMonth, mDay;
    private String userDateOfBirth;

    private DatePickerDialog datePickerDialog;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser currentUser;
    private StorageReference storageReference;

    // User Data
    private String userFirstName;
    private String userLastName;
    private String userUserName;
    private String userProfilePicImageUrl;
    private String userEmailAddress;
    private String userDOB;
    // User Data

    private final int PICK_IMAGE_REQUEST = 22;
    private Uri localImageUri;
    private byte[] profilePic;

    private boolean fromSelectImage = false;
    private boolean fromCropImage = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        //        Loading Dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        ImageView dialogImage = loadingDialog.findViewById(R.id.load_image);
        Glide.with(this)
                .load(R.drawable.loading_gif)
                .into(dialogImage);
        loadingDialog.setCancelable(false);
        loadingDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    getActivity().finish();
                }
                return true;
            }
        });
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //        Loading Dialog

        context = getContext();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        loadUserData(loadingDialog, true);

        if (localImageUri != null) {
            Glide.with(getContext())
                    .load(localImageUri.getPath())
                    .into(userProfilePic);
        }

        firstName = view.findViewById(R.id.first_name);
        lastName = view.findViewById(R.id.last_name);
        userName = view.findViewById(R.id.username);
        dateOfBirth = view.findViewById(R.id.date_of_birth);
        userEmail = view.findViewById(R.id.user_email);
        saveProfileBtn = view.findViewById(R.id.save_profile_btn);
        userProfilePic = view.findViewById(R.id.user_profile_pic);
        uploadImageButton = view.findViewById(R.id.upload_image_button);

        saveProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData(context, profilePic, firstName.getText().toString().trim(), lastName.getText().toString().trim(), userName.getText().toString().trim(), dateOfBirth.getText().toString().trim(), loadingDialog);
            }
        });

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromSelectImage = true;
                selectImage();
            }
        });


        dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), R.style.DialogTheme,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                if (String.valueOf(monthOfYear).length() != 2) {
                                    userDateOfBirth = dayOfMonth + "-" + "0" + (monthOfYear + 1) + "-" + year;
                                } else {
                                    userDateOfBirth = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                                }
                                dateOfBirth.setText(userDateOfBirth);
                                dateOfBirth.setTextColor(Color.parseColor("#1E202B"));
                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.show();
            }
        });
        return view;
    }

    private void loadUserData(final Dialog loadingDialog, final boolean loadImage) {
        loadingDialog.show();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseFirestore.collection("USERS")
                .document(currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            userFirstName = documentSnapshot.get("first_name").toString();
                            userLastName = documentSnapshot.get("last_name").toString();
                            userUserName = documentSnapshot.get("user_name").toString();
                            userProfilePicImageUrl = documentSnapshot.get("user_profile_pic").toString();
                            userEmailAddress = documentSnapshot.get("email").toString();
                            userDOB = documentSnapshot.get("DOB").toString();

                            // Setting User Data
                            firstName.setText(userFirstName);
                            lastName.setText(userLastName);
                            userName.setText(userUserName);
                            dateOfBirth.setText(userDOB);
                            dateOfBirth.setTextColor(Color.parseColor("#1E202B"));
                            userEmail.setText(userEmailAddress);
                            if (loadImage) {
                                if (!userProfilePicImageUrl.equals("")) {
                                    if (getContext() != null) {
                                        Glide.with(getContext())
                                                .load(userProfilePicImageUrl)
                                                .apply(RequestOptions.circleCropTransform())
                                                .transition(DrawableTransitionOptions.withCrossFade(500))
                                                .into(userProfilePic);
                                    }
                                }
                            }
                            // Setting User Data
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();
                    }
                });
    }

    private void loadImage(Uri imageUri) {
        Glide.with(getContext())
                .load(imageUri)
                .apply(RequestOptions.circleCropTransform())
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(userProfilePic);
    }

    private void selectImage() {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (fromSelectImage) {
            if (requestCode == PICK_IMAGE_REQUEST
                    && resultCode == RESULT_OK
                    && data != null
                    && data.getData() != null) {
                fromSelectImage = false;
                localImageUri = data.getData();
                CropImage.activity(localImageUri)
                        .setAspectRatio(500, 500)
                        .start(context, EditProfileFragment.this);
            }
        } else {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    ByteArrayOutputStream baos;
                    Uri resultUri = result.getUri();
                    Bitmap newOriginalBM = loadBitmap(resultUri);
                    if (newOriginalBM.getHeight() > 500){
                        newOriginalBM = scaleDown(newOriginalBM, 500, true);
                        baos = new ByteArrayOutputStream();
                        newOriginalBM.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        profilePic = baos.toByteArray();
                    } else {
                        baos = new ByteArrayOutputStream();
                        newOriginalBM.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        profilePic = baos.toByteArray();
                    }
                    Glide.with(getContext())
                            .load(newOriginalBM)
                            .apply(RequestOptions.circleCropTransform())
                            .transition(DrawableTransitionOptions.withCrossFade(500))
                            .into(userProfilePic);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private Bitmap loadBitmap(Uri src) {
        Bitmap bm = null;
        try {
            bm = BitmapFactory.decodeStream(
                    getContext().getContentResolver().openInputStream(src));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bm;
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    private void uploadData(final Context context, final byte[] imageUri, final String userFirstName, final String userLastName, final String userUserName, final String userDateOfBirth, final Dialog loadingDialog) {
        loadingDialog.show();

        if (imageUri != null) {
            final StorageReference filePath = storageReference.child("user_profile_images/" + firebaseAuth.getCurrentUser().getUid());
            filePath.putBytes(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("user_profile_pic", uri.toString());
                            firebaseFirestore
                                    .collection("USERS")
                                    .document(firebaseAuth.getCurrentUser().getUid())
                                    .update(userData)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                loadImage(uri);
                                                localImageUri = null;
                                                loadUserData(loadingDialog, true);
                                            } else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    });
                }
            });
        }

        Map<String, Object> userData = new HashMap<>();
        if (!userFirstName.equals("")) {
            userData.put("first_name", userFirstName);
        }
        if (!userLastName.equals("")) {
            userData.put("last_name", userLastName);
        }
        if (!userUserName.equals("")) {
            userData.put("user_name", userUserName);
        }
        if (!userDateOfBirth.equals("")) {
            userData.put("DOB", userDateOfBirth);
        }
        firebaseFirestore
                .collection("USERS")
                .document(firebaseAuth.getCurrentUser().getUid())
                .update(userData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Profile Updated!", Toast.LENGTH_SHORT).show();
                            if (imageUri == null) {
                                loadUserData(loadingDialog, false);
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                        if (imageUri == null) {
                            loadingDialog.dismiss();
                        }
                    }
                });
    }
}
