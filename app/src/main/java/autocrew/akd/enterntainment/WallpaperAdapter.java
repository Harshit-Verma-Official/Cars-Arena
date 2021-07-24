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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.badoo.mobile.util.WeakHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

public class WallpaperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private int lastPosition = -1;

    private Context context;
    private List<WallpaperModel> wallpaperModelList;

    public WallpaperAdapter(Context context, List<WallpaperModel> wallpaperModelList) {
        this.context = context;
        this.wallpaperModelList = wallpaperModelList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallpaper_item_layout, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return wallpaperModelList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            WallpaperModel currentItem = wallpaperModelList.get(position);
            String imageUrl480p = currentItem.getImageUrl480p();

            if (!imageUrl480p.equals("")) {
                ((ViewHolder) holder).loadNetWorkImage(context, ((ViewHolder) holder).wallpaperImageView, imageUrl480p);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent fullScreenWallpaperIntent = new Intent(holder.itemView.getContext(), FullScreenWallpaperActivity.class);
                        fullScreenWallpaperIntent.putExtra("clicked_position", position);

                        Pair[] pairs = new Pair[1];
                        pairs[0] = new Pair<View, String>(((ViewHolder) holder).wallpaperImageView, "testTransition");
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);

                        holder.itemView.getContext().startActivity(fullScreenWallpaperIntent, options.toBundle());
                    }
                });
            }
        } else if (holder instanceof LoadingViewHolder) {
            ((LoadingViewHolder) holder).showLoadingView((LoadingViewHolder) holder, position);
        }
        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.scale_animation);
            holder.itemView.setAnimation(animation);
            lastPosition = position;
        }
    }


    @Override
    public int getItemCount() {
        return wallpaperModelList == null ? 0 : wallpaperModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView wallpaperImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            wallpaperImageView = itemView.findViewById(R.id.wallpaper_imageView);
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
                    .centerCrop()
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

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        LottieAnimationView progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }

        private void showLoadingView(LoadingViewHolder viewHolder, int position) {
            //ProgressBar would be displayed

        }
    }
}
