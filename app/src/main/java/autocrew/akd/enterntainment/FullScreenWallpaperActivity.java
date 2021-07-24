package autocrew.akd.enterntainment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.cocosw.bottomsheet.BottomSheet;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnProgressListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FullScreenWallpaperActivity extends AppCompatActivity {

    private int clickedPosition = -1;

    private AppBarLayout appBarLayout;

    public static ViewPager wallpaperViewPager;
    private ImageView expandBtn;
    private ImageView downloadBtn;
    private ImageView shareBtn;
    private ImageView setWallpaperBtn;
    private SparkButton likeBtn;
    private TextView viewCountTextView;
    private TextView downloadsCountTextView;
    private TextView likesCountTextView;
    private TextView resolutionTextView;
    private TextView viewsHeadingTextView;
    private TextView downloadsHeadingTextView;
    private TextView resolutionHeadingTextView;
    private TextView likesHeadingTextView;

    private Dialog progressDialog;
    private LottieAnimationView progressBar;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private SlidingUpPanelLayout slidingUpPanelLayout;

    private String currentImagePath;
    private boolean isSetWallpaper = false;
    private boolean isShare = false;
    private boolean isQueryRunning = false;

    private int downloadID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BigImageViewer.initialize(GlideImageLoader.with(getApplicationContext()));
        setContentView(R.layout.activity_full_screen_wallpaper);

        ///////// Getting Intent
        Intent intent = getIntent();
        clickedPosition = intent.getIntExtra("clicked_position", -1);
        ///////// Getting Intent

        ///////// Initialization
        wallpaperViewPager = findViewById(R.id.wallpaper_viewpager);
        slidingUpPanelLayout = findViewById(R.id.sliding_layout);
        expandBtn = findViewById(R.id.expand_btn);
        downloadBtn = findViewById(R.id.download_btn);
        shareBtn = findViewById(R.id.share_btn);
        likeBtn = findViewById(R.id.spark_button);
        setWallpaperBtn = findViewById(R.id.set_wallpaper_btn);
        viewCountTextView = findViewById(R.id.views_count_textView);
        downloadsCountTextView = findViewById(R.id.downloads_count_textView);
        likesCountTextView = findViewById(R.id.likes_textview);
        resolutionTextView = findViewById(R.id.resolution_textview);
        viewsHeadingTextView = findViewById(R.id.views_heading_textView);
        downloadsHeadingTextView = findViewById(R.id.downloads_heading_textView);
        resolutionHeadingTextView = findViewById(R.id.resolution_heading_textView);
        likesHeadingTextView = findViewById(R.id.likes_heading_textView);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        ///////// Initialization

        ///////// Setting Fonts
        viewsHeadingTextView.setTypeface(AppMainActivity.fontCircularProBold);
        downloadsHeadingTextView.setTypeface(AppMainActivity.fontCircularProBold);
        resolutionHeadingTextView.setTypeface(AppMainActivity.fontCircularProBold);
        likesHeadingTextView.setTypeface(AppMainActivity.fontCircularProBold);
        viewCountTextView.setTypeface(AppMainActivity.fontCircularProSemiBold);
        downloadsCountTextView.setTypeface(AppMainActivity.fontCircularProSemiBold);
        resolutionTextView.setTypeface(AppMainActivity.fontCircularProSemiBold);
        likesCountTextView.setTypeface(AppMainActivity.fontCircularProSemiBold);
        ///////// Setting Fonts

        ////////// Fetching MetaData
        fetchMetaData(clickedPosition);
        ////////// Fetching MetaData

        ///////// Setting Views
        setViews(clickedPosition);
        ///////// Setting Views

        ///////// Dialog
        PermissionListener bottomSheetPermissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                new BottomSheet.Builder(FullScreenWallpaperActivity.this).darkTheme().title("Select Resolution").sheet(R.menu.bottom_sheet_menu).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.res_4k:
                                downloadID = downloadFile(DBQueries.wallpaperModelList.get(wallpaperViewPager.getCurrentItem()).getImageUrl4k());
                                break;
                            case R.id.res_1080p:
                                downloadID = downloadFile(DBQueries.wallpaperModelList.get(wallpaperViewPager.getCurrentItem()).getImageUrl1080p());
                                break;
                            case R.id.res_720p:
                                downloadID = downloadFile(DBQueries.wallpaperModelList.get(wallpaperViewPager.getCurrentItem()).getImageUrl720p());
                                break;
                            case R.id.res_480p:
                                downloadID = downloadFile(DBQueries.wallpaperModelList.get(wallpaperViewPager.getCurrentItem()).getImageUrl480p());
                                break;
                        }
                    }
                }).show();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(FullScreenWallpaperActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        };
        ///////// Dialog

        ///////// ProgressDialog
        progressDialog = new Dialog(this);
        progressDialog.setContentView(R.layout.progress_dialog_box);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    PRDownloader.cancel(downloadID);
                    progressDialog.dismiss();
                }
                return true;
            }
        });
        progressBar = progressDialog.findViewById(R.id.progressBar);
        ///////// ProgressDialog

        ////////// AppBar And Drawer
        final Toolbar toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appbar_layout);
        toolbar.setPadding(0, AppMainActivity.getStatusBarHeight(), 0, 0);
        toolbar.getLayoutParams().height += AppMainActivity.getStatusBarHeight();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout.setStateListAnimator(AnimatorInflater.loadStateListAnimator(this, R.animator.appbar_elevation_off));
        ////////// AppBar And Drawer

        //////// Like Spark Button

        likeBtn.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                if (!isQueryRunning) {
                    if (firebaseUser != null) {
                        if (buttonState) {
                            likeIt(wallpaperViewPager.getCurrentItem());
                        } else {
                            unLikeIt(wallpaperViewPager.getCurrentItem());
                        }
                    } else {
                        if (buttonState) {
                            likeBtn.setChecked(false);
                            MainActivity.isAppMainActivityOpen = false;
                            Intent registerIntent = new Intent(FullScreenWallpaperActivity.this, MainActivity.class);
                            MainActivity.fromAppMainActivity = true;
                            startActivity(registerIntent);
//                            SweetAlertDialog pDialog = new SweetAlertDialog(FullScreenWallpaperActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
//                            pDialog.setContentText("Sign in for wish listing");
//                            pDialog.setConfirmText("Sign in");
//                            pDialog.setCustomImage(R.drawable.ic_alert_wishlist);
//                            pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                @Override
//                                public void onClick(SweetAlertDialog sDialog) {
//                                    MainActivity.isAppMainActivityOpen = false;
//                                    Intent registerIntent = new Intent(FullScreenWallpaperActivity.this, MainActivity.class);
//                                    MainActivity.fromAppMainActivity = true;
//                                    startActivity(registerIntent);
//                                    sDialog.dismissWithAnimation();
//                                }
//                            });
//                            pDialog.show();
                        }
                    }
                }
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {

            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {

            }
        });
        //////// Like Spark Button

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShare = true;
                isSetWallpaper = false;
//                dialogDownloadBtn.setText("Share");
//                resolutionDialog.show();
                TedPermission.with(getApplicationContext())
                        .setPermissionListener(bottomSheetPermissionListener)
                        .setDeniedMessage("Allow Storage Access to download images!")
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check();
            }
        });

        setWallpaperBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dialogDownloadBtn.setText("Set Wallpaper");
                isSetWallpaper = true;
                isShare = false;
//                resolutionDialog.show();
                TedPermission.with(getApplicationContext())
                        .setPermissionListener(bottomSheetPermissionListener)
                        .setDeniedMessage("Allow Storage Access to download images!")
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check();
            }
        });
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dialogDownloadBtn.setText("Download");
                isSetWallpaper = false;
                isShare = false;
//                resolutionDialog.show();

                TedPermission.with(getApplicationContext())
                        .setPermissionListener(bottomSheetPermissionListener)
                        .setDeniedMessage("Allow Storage Access to download images!")
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check();
            }
        });

        ////////// SlidingPanel
        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (slideOffset == 1) {
                    AnimationSet animSet = new AnimationSet(true);
                    animSet.setInterpolator(new DecelerateInterpolator());
                    animSet.setFillAfter(true);
                    animSet.setFillEnabled(true);

                    final RotateAnimation animRotate = new RotateAnimation(0.0f, -180.0f,
                            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                            RotateAnimation.RELATIVE_TO_SELF, 0.5f);

                    animRotate.setDuration(200);
                    animRotate.setFillAfter(true);
                    animSet.addAnimation(animRotate);
                    expandBtn.startAnimation(animRotate);
                } else if (slideOffset == 0) {
                    AnimationSet animSet = new AnimationSet(true);
                    animSet.setInterpolator(new DecelerateInterpolator());
                    animSet.setFillAfter(true);
                    animSet.setFillEnabled(true);

                    final RotateAnimation animRotate = new RotateAnimation(-180.0f, 0.0f,
                            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                            RotateAnimation.RELATIVE_TO_SELF, 0.5f);

                    animRotate.setDuration(200);
                    animRotate.setFillAfter(true);
                    animSet.addAnimation(animRotate);
                    expandBtn.startAnimation(animRotate);
                }
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

            }
        });
        ////////// SlidingPanel

        ///////// ViewPager
        WallpaperViewPagerAdapter wallpaperViewPagerAdapter = new WallpaperViewPagerAdapter(this, DBQueries.wallpaperModelList);
        wallpaperViewPager.setAdapter(wallpaperViewPagerAdapter);
        wallpaperViewPager.setCurrentItem(clickedPosition);

        wallpaperViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position == DBQueries.wallpaperModelList.size() - 3) {
                    if (!DBQueries.isLoading) {
                        DBQueries.isLoading = true;
                        Query nextQuery = firebaseFirestore.collection("WALLPAPERS").orderBy("timestamp", Query.Direction.DESCENDING).startAfter(DBQueries.lastVisible).limit(15);
                        nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                if (t.isSuccessful()) {
                                    if (t.getResult().size() != 0) {
                                        for (DocumentSnapshot d : t.getResult()) {
                                            DBQueries.wallpaperModelList.add(new WallpaperModel(d.get("imageUrl4k").toString(), d.get("imageUrl1080p").toString(), d.get("imageUrl720p").toString(), d.get("imageUrl480p").toString(), d.get("image_id").toString(), (Long) d.get("downloads"), (Long) d.get("views"), d.get("resolution").toString()));
                                        }
                                        DBQueries.lastVisible = t.getResult().getDocuments().get(t.getResult().size() - 1);
                                        DBQueries.isLoading = false;
                                        WallpaperFragment.wallpaperRecyclerView.getAdapter().notifyDataSetChanged();
                                        wallpaperViewPager.getAdapter().notifyDataSetChanged();

                                        if (t.getResult().size() < 15) {
                                            DBQueries.isLastItemReached = true;
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                ////////// Fetching MetaData
                fetchMetaData(position);
                ////////// Fetching MetaData

                ///////// Setting Views
                setViews(position);
                ///////// Setting Views
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    DBQueries.isScrolling = true;
                }
            }
        });
        ///////// ViewPager
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Bitmap newOriginalBM = loadBitmap(resultUri);
                reloadWallpaper(newOriginalBM);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private int downloadFile(String url) {
        progressBar.setMinAndMaxFrame(146, 246);
        progressBar.setFrame(146);
        progressDialog.show();
        String fileName = String.valueOf(generateRandomDigits(10)) + ".jpeg";
        String dirPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Cars Arena Wallpapers";

        currentImagePath = dirPath + File.separator + fileName;

        return /*download id*/ PRDownloader.download(url, dirPath, fileName)
                .build()
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        int prog = (int) ((Float.valueOf(progress.currentBytes) / Float.valueOf(progress.totalBytes)) * 100);
                        progressBar.setFrame(146 + ((int) ((prog * 41) / 100)));
                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {
                        Toast.makeText(FullScreenWallpaperActivity.this, "Download Cancelled!", Toast.LENGTH_SHORT).show();
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        progressBar.setMinFrame(187);
                        progressBar.addAnimatorListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                Toast.makeText(FullScreenWallpaperActivity.this, "Download Completed!", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                        progressBar.playAnimation();
                        ///////// Setting Downloads
                        setDownloads(wallpaperViewPager.getCurrentItem());
                        ///////// Setting Downloads
                        if (isSetWallpaper) {
                            DisplayMetrics metrics = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(metrics);
                            int height = metrics.heightPixels;
                            int width = metrics.widthPixels;
                            CropImage.activity(Uri.fromFile(new File(currentImagePath)))
                                    .setAspectRatio(width, height)
                                    .start(FullScreenWallpaperActivity.this);
                        } else if (isShare) {
                            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                            sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Uri uri = FileProvider.getUriForFile(FullScreenWallpaperActivity.this, BuildConfig.APPLICATION_ID + ".provider", new File(currentImagePath));
                            sharingIntent.setType("*/*");
                            sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            startActivity(Intent.createChooser(sharingIntent, "Share using"));
                        }
                    }

                    @Override
                    public void onError(Error error) {
                        Toast.makeText(FullScreenWallpaperActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public static int generateRandomDigits(int n) {
        int m = (int) Math.pow(10, n - 1);
        return m + new Random().nextInt(9 * m);
    }

    private Bitmap loadBitmap(Uri src) {
        Bitmap bm = null;
        try {
            bm = BitmapFactory.decodeStream(
                    getBaseContext().getContentResolver().openInputStream(src));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bm;
    }

    private void reloadWallpaper(Bitmap bm) {
        if (bm != null) {
            WallpaperManager myWallpaperManager =
                    WallpaperManager.getInstance(getApplicationContext());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (myWallpaperManager.isWallpaperSupported()) {
                    try {
                        myWallpaperManager.setBitmap(bm);
                        Toast.makeText(this, "Wallpaper Successfully Changed!", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(FullScreenWallpaperActivity.this,
                            "isWallpaperSupported() NOT SUPPORTED",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                try {
                    myWallpaperManager.setBitmap(bm);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(FullScreenWallpaperActivity.this, "bm == null", Toast.LENGTH_LONG).show();
        }
    }

    private void setViews(int position) {
        ///////// Setting Views
        firebaseFirestore.collection("WALLPAPERS")
                .document(DBQueries.wallpaperModelList.get(position).getImageId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            long currentViews = (long) snapshot.get("views");
                            currentViews++;
                            Map<String, Object> updatedViews = new HashMap<>();
                            updatedViews.put("views", currentViews);
                            firebaseFirestore.collection("WALLPAPERS")
                                    .document(DBQueries.wallpaperModelList.get(position).getImageId())
                                    .update(updatedViews)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                            } else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(FullScreenWallpaperActivity.this, error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(FullScreenWallpaperActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        ///////// Setting Views
    }

    private void setDownloads(int position) {
        ///////// Setting Downloads
        firebaseFirestore.collection("WALLPAPERS")
                .document(DBQueries.wallpaperModelList.get(position).getImageId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            long currentDownloads = (long) snapshot.get("downloads");
                            currentDownloads++;
                            Map<String, Object> updatedViews = new HashMap<>();
                            updatedViews.put("downloads", currentDownloads);
                            firebaseFirestore.collection("WALLPAPERS")
                                    .document(DBQueries.wallpaperModelList.get(position).getImageId())
                                    .update(updatedViews)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                fetchMetaData(position);
                                            } else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(FullScreenWallpaperActivity.this, error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(FullScreenWallpaperActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        ///////// Setting Downloads
    }

    private void fetchMetaData(int position) {

        if (firebaseUser != null) {
            likeBtn.setEnabled(false);
            firebaseFirestore.collection("USERS")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .collection("LIKED_WALLPAPERS")
                    .document(DBQueries.wallpaperModelList.get(position).getImageId())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null && document.exists()) {
                                    ////////// If car is already added to wishList
                                    likeBtn.setChecked(true);
                                } else {
                                    likeBtn.setChecked(false);
                                }
                                likeBtn.setEnabled(true);
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(FullScreenWallpaperActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        ////////// Fetching MetaData
        firebaseFirestore.collection("WALLPAPERS")
                .document(DBQueries.wallpaperModelList.get(position).getImageId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            viewCountTextView.setText(String.valueOf((long) snapshot.get("views")));
                            downloadsCountTextView.setText(String.valueOf((long) snapshot.get("downloads")));
                            resolutionTextView.setText(snapshot.get("resolution").toString());
                            likesCountTextView.setText(String.valueOf((long) snapshot.get("likes")));
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(FullScreenWallpaperActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        ////////// Fetching MetaData
    }

    private void likeIt(int position) {

        isQueryRunning = true;
        likeBtn.setEnabled(false);
        Map<String, Object> likeData = new HashMap<>();

        firebaseFirestore.collection("WALLPAPERS")
                .whereEqualTo("image_id", DBQueries.wallpaperModelList.get(position).getImageId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                long currentLikes = (long) documentSnapshot.get("likes");
                                currentLikes++;
                                likeData.put("likes", currentLikes);
                                firebaseFirestore.collection("WALLPAPERS")
                                        .document(documentSnapshot.getId())
                                        .update(likeData)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Map<String, Object> userLikesData = new HashMap<>();
                                                    userLikesData.put("image_id", DBQueries.wallpaperModelList.get(position).getImageId());
                                                    firebaseFirestore.collection("USERS")
                                                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .collection("LIKED_WALLPAPERS")
                                                            .document(DBQueries.wallpaperModelList.get(position).getImageId())
                                                            .set(userLikesData)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(FullScreenWallpaperActivity.this, "Wallpaper Liked!", Toast.LENGTH_SHORT).show();
                                                                        fetchMetaData(position);
                                                                        isQueryRunning = false;
                                                                        likeBtn.setEnabled(true);
                                                                    } else {
                                                                        String error = task.getException().getMessage();
                                                                        Toast.makeText(FullScreenWallpaperActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                } else {
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(FullScreenWallpaperActivity.this, error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(FullScreenWallpaperActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void unLikeIt(int position) {
        isQueryRunning = true;
        likeBtn.setEnabled(false);
        Map<String, Object> likeData = new HashMap<>();

        firebaseFirestore.collection("WALLPAPERS")
                .whereEqualTo("image_id", DBQueries.wallpaperModelList.get(position).getImageId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                long currentLikes = (long) documentSnapshot.get("likes");
                                currentLikes--;
                                likeData.put("likes", currentLikes);
                                firebaseFirestore.collection("WALLPAPERS")
                                        .document(DBQueries.wallpaperModelList.get(position).getImageId())
                                        .update(likeData)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Map<String, Object> userLikesData = new HashMap<>();
                                                    userLikesData.put("image_id", documentSnapshot.getId());
                                                    firebaseFirestore.collection("USERS")
                                                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .collection("LIKED_WALLPAPERS")
                                                            .document(DBQueries.wallpaperModelList.get(position).getImageId())
                                                            .delete()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(FullScreenWallpaperActivity.this, "Wallpaper Unliked!", Toast.LENGTH_SHORT).show();
                                                                        fetchMetaData(position);
                                                                        isQueryRunning = false;
                                                                        likeBtn.setEnabled(true);
                                                                    } else {
                                                                        String error = task.getException().getMessage();
                                                                        Toast.makeText(FullScreenWallpaperActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                } else {
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(FullScreenWallpaperActivity.this, error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(FullScreenWallpaperActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
