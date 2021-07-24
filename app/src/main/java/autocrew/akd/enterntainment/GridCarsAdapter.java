package autocrew.akd.enterntainment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class GridCarsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private int lastPosition = -1;


    private Context context;
    private List<PopularCarModel> gridCarsList;

    public GridCarsAdapter(Context context, List<PopularCarModel> gridCarsList) {
        this.context = context;
        this.gridCarsList = gridCarsList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_car_item_layout, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            PopularCarModel currentItem = gridCarsList.get(position);
            String carName = currentItem.getCarName();
            String carBrand = currentItem.getCarBrand();
            String carImageUrl = currentItem.getCarImageUrl();

            ((ViewHolder) holder).carNameTextView.setText(carName);
            ((ViewHolder) holder).carBrandTextView.setText(carBrand);

            ((ViewHolder) holder).carNameTextView.setTypeface(Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "fonts/Industry_Ultra_Italic.ttf"));
            ((ViewHolder) holder).carBrandTextView.setTypeface(Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "fonts/Circular Pro_semi_bold.otf"));

            loadNetWorkImage(context, ((ViewHolder) holder).carImageImageView, carImageUrl);
            FirebaseFirestore.getInstance().collection("CAR_BRANDS").whereEqualTo("brand_name", carBrand).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                if (!((Activity) context).isFinishing()) {
                                    loadNetWorkImage(context, ((ViewHolder) holder).carBrandLogoImageView, documentSnapshot.get("brand_logo_url").toString());
                                }
                            }
                            Animation a = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.text_scale_animation);
                            a.reset();
                            ((ViewHolder) holder).carBrandLogoImageView.startAnimation(a);
                        }
                    });
        } else {
            ((LoadingViewHolder) holder).showLoadingView((LoadingViewHolder) holder, position);
        }
        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.item_fade_in);
            holder.itemView.setAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return gridCarsList == null ? 0 : gridCarsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return gridCarsList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView carNameTextView;
        private TextView carBrandTextView;
        private ImageView carImageImageView;
        private ImageView carBrandLogoImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            carNameTextView = itemView.findViewById(R.id.car_name_textView);
            carBrandTextView = itemView.findViewById(R.id.car_brand_textView);
            carImageImageView = itemView.findViewById(R.id.car_imageView);
            carBrandLogoImageView = itemView.findViewById(R.id.brand_logo_imageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Pair[] pairs = new Pair[1];
                    pairs[0] = new Pair<View, String>(carImageImageView, "testTransition");
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) itemView.getContext(), pairs);
                    Intent CarDetailsIntent = new Intent(itemView.getContext(), CarDetailsActivity.class);
                    CarDetailsIntent.putExtra("garage_car_id", gridCarsList.get(getAdapterPosition()).getGarageCarId());
                    context.startActivity(CarDetailsIntent, options.toBundle());
                }
            });
        }
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

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).carBrandLogoImageView.setImageDrawable(null);
            ((ViewHolder) holder).carImageImageView.setImageDrawable(null);
        }
    }
}
