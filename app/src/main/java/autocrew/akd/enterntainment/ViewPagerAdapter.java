package autocrew.akd.enterntainment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.badoo.mobile.util.WeakHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.Serializable;
import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private List<String> carImagesList;
    private List<String> carImagesNameList;
    private LayoutInflater layoutInflater;

    public ViewPagerAdapter(Context context, List<String> carImagesList, List<String> carImagesNameList) {
        this.context = context;
        this.carImagesList = carImagesList;
        this.carImagesNameList = carImagesNameList;
    }

    @Override
    public int getCount() {
        return carImagesList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = layoutInflater.inflate(R.layout.viewpager_layout, null);
        final ImageView imageView = view.findViewById(R.id.car_imageview);

        loadNetWorkImage(context, imageView, carImagesList.get(position));

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullScreenImageActivity.class);
                intent.putExtra("carImagesList", (Serializable) carImagesList);
                intent.putExtra("carImagesNameList", (Serializable) carImagesNameList);
                intent.putExtra("currentItemPosition", position);

                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(view, "testTransition");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);

                context.startActivity(intent, options.toBundle());
            }
        });

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }

    public void loadNetWorkImage(Context context, ImageView imageView, String imageUrl) {
        final WeakHandler mHandler = new WeakHandler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                loadNetWorkImage(context, imageView, imageUrl);
            }
        };

        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.explore_image_placeholder)
                .fitCenter()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        mHandler.postDelayed(runnable, 1);
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(imageView);
    }
}
