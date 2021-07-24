package autocrew.akd.enterntainment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnProgressListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.google.android.material.appbar.AppBarLayout;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FullScreenImageActivity extends AppCompatActivity {

    public static AppBarLayout appBarLayout;
    private List<String> carImagesList;
    private List<String> carImagesNameList;

    private ViewPager viewPager;
    private DotsIndicator viewPagerIndicator;

    private Dialog progressDialog;
    private LottieAnimationView progressBar;
    private int downloadID;

    public static int currentItemPosition;

    public static ImageView downloadBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BigImageViewer.initialize(GlideImageLoader.with(getApplicationContext()));
        setContentView(R.layout.activity_full_screen_image);

        ////////// Intent
        currentItemPosition = getIntent().getIntExtra("currentItemPosition", -1);
        ////////// Intent

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
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        toolbar.getLayoutParams().height += getStatusBarHeight();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout.setStateListAnimator(AnimatorInflater.loadStateListAnimator(this, R.animator.appbar_elevation_off));
        ////////// AppBar And Drawer

        ////////// Initialization
        carImagesList = new ArrayList<>();
        carImagesNameList = new ArrayList<>();
        carImagesList.clear();
        viewPagerIndicator = findViewById(R.id.viewpager_indicator);
        downloadBtn = findViewById(R.id.download_btn);
        downloadBtn.setPadding(0, getStatusBarHeight(), 0, 0);
        carImagesList = (ArrayList<String>) getIntent().getSerializableExtra("carImagesList");
        carImagesNameList = (ArrayList<String>) getIntent().getSerializableExtra("carImagesNameList");
        ////////// Initialization

        ////////// ViewPager
        viewPager = findViewById(R.id.viewPager);
        FullScreenViewPagerAdapter fullScreenViewPagerAdapter = new FullScreenViewPagerAdapter(getApplicationContext(), carImagesList);
        viewPager.setAdapter(fullScreenViewPagerAdapter);
        viewPagerIndicator.setViewPager(viewPager);
        viewPager.setCurrentItem(currentItemPosition);
        viewPagerIndicator.setPadding(0, 0, 0, AppMainActivity.getSoftButtonsBarHeight());
        ////////// ViewPager

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                downloadID = downloadFile(carImagesList.get(viewPager.getCurrentItem()), carImagesNameList.get(viewPager.getCurrentItem()));
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(FullScreenImageActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        };

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TedPermission.with(getApplicationContext())
                        .setPermissionListener(permissionListener)
                        .setDeniedMessage("Allow Storage Access to download images!")
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check();
            }
        });

    }


    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private int downloadFile(String url, String fileName) {
        progressBar.setMinAndMaxFrame(146, 246);
        progressBar.setFrame(146);
        progressDialog.show();
        String newFileName = String.valueOf(FullScreenWallpaperActivity.generateRandomDigits(10)) + fileName.substring(fileName.lastIndexOf("."));
        String dirPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Cars Arena Wallpapers";

        return /*download id*/ PRDownloader.download(url, dirPath, newFileName)
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
                        Toast.makeText(FullScreenImageActivity.this, "Download Cancelled!", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(FullScreenImageActivity.this, "Download Completed!", Toast.LENGTH_SHORT).show();
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
                    }

                    @Override
                    public void onError(Error error) {
                        Toast.makeText(FullScreenImageActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
