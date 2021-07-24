package autocrew.akd.enterntainment;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.badoo.mobile.util.WeakHandler;
import com.github.piasy.biv.loader.ImageLoader;
import com.github.piasy.biv.view.BigImageView;

import java.io.File;
import java.util.List;

public class WallpaperViewPagerAdapter extends PagerAdapter {

    private Context context;
    private List<WallpaperModel> wallpaperModelList;
    private LayoutInflater layoutInflater;

    public WallpaperViewPagerAdapter(Context context, List<WallpaperModel> wallpaperModelList) {
        this.context = context;
        this.wallpaperModelList = wallpaperModelList;
    }

    @Override
    public int getCount() {
        return wallpaperModelList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.full_screen_viewpager_layout, null);
        BigImageView zoomView = view.findViewById(R.id.mBigImage);
        LottieAnimationView progressBar = view.findViewById(R.id.progressBar);


        if (wallpaperModelList.get(position) != null) {
            loadNetWorkImage(zoomView, wallpaperModelList.get(position).getImageUrl1080p(), progressBar);
        }

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }

    public void loadNetWorkImage(BigImageView zoomView, String imageUrl, LottieAnimationView progressBar) {
        final WeakHandler mHandler = new WeakHandler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                loadNetWorkImage(zoomView, imageUrl, progressBar);
            }
        };

        progressBar.setVisibility(View.VISIBLE);
        zoomView.showImage(Uri.parse(imageUrl));
        zoomView.setImageLoaderCallback(new ImageLoader.Callback() {
            @Override
            public void onCacheHit(int imageType, File image) {

            }

            @Override
            public void onCacheMiss(int imageType, File image) {

            }

            @Override
            public void onStart() {
            }

            @Override
            public void onProgress(int progress) {
                progressBar.setFrame(((progress * 60)/99));
            }

            @Override
            public void onFinish() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(File image) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFail(Exception error) {
                mHandler.postDelayed(runnable, 1);
            }
        });
    }
}
