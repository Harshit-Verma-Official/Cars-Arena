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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.badoo.mobile.util.WeakHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class NewCarsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private int lastPosition = -1;

    private List<NewCarsModel> newCarsModelList;
    private Context context;
    DrawableCrossFadeFactory factory =
            new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();

    public NewCarsAdapter(Context context, List<NewCarsModel> newCarsModelList) {
        this.newCarsModelList = newCarsModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_car_item_layout, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return newCarsModelList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            NewCarsModel currentItem = newCarsModelList.get(position);
            String newCarName = currentItem.getCarName();
            String newCarBrand = currentItem.getCarBrand();
            String newCarSubtitle = currentItem.getCarDescription();
            String newCarImageUrl = currentItem.getCarImageUrl();

            ((ViewHolder) holder).newCarNameTextView.setText(newCarName);
            ((ViewHolder) holder).newCarSubtitleTextView.setText(newCarSubtitle);
            loadImage(context, ((ViewHolder) holder).newCarImageView, newCarImageUrl);

            FirebaseFirestore.getInstance().collection("CAR_BRANDS").whereEqualTo("brand_name", newCarBrand)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    String logoUrl = documentSnapshot.get("brand_logo_url").toString();
                                    loadImage(holder.itemView.getContext(), ((ViewHolder) holder).brandLogoImageView, logoUrl);
                                }
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(holder.itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            if (!newCarsModelList.get(position).getCarName().equals("")) {
                ((ViewHolder) holder).constraintLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startIntent(((ViewHolder) holder).newCarImageView, holder.itemView, position);
                    }
                });
                ((ViewHolder) holder).showDetailsIconImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startIntent(((ViewHolder) holder).newCarImageView, holder.itemView, position);
                    }
                });
            }
        } else if (holder instanceof LoadingViewHolder) {
            ((LoadingViewHolder) holder).showLoadingView((LoadingViewHolder) holder, position);
        }

        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.item_fade_in);
            holder.itemView.setAnimation(animation);
            lastPosition = position;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView newCarNameTextView;
        private TextView newCarSubtitleTextView;
        private ImageView newCarImageView;
        private ConstraintLayout constraintLayout;
        private ImageView brandLogoImageView;
        private ImageView showDetailsIconImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            newCarNameTextView = itemView.findViewById(R.id.new_car_name_textview);
            newCarSubtitleTextView = itemView.findViewById(R.id.new_car_subtitle_textview);
            newCarImageView = itemView.findViewById(R.id.new_car_imageview);
            constraintLayout = itemView.findViewById(R.id.constraintLayout2);
            brandLogoImageView = itemView.findViewById(R.id.brand_logo_imageView);
            showDetailsIconImageView = itemView.findViewById(R.id.show_details_icon_imageView);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        private void showLoadingView(LoadingViewHolder viewHolder, int position) {
            //ProgressBar would be displayed

        }
    }

    @Override
    public int getItemCount() {
        return newCarsModelList == null ? 0 : newCarsModelList.size();
    }

    private void startIntent(ImageView newCarImageView, View itemView, int position) {
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(newCarImageView, "testTransition");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) itemView.getContext(), pairs);
        Intent CarDetailsIntent = new Intent(itemView.getContext(), CarDetailsActivity.class);
        CarDetailsIntent.putExtra("garage_car_id", newCarsModelList.get(position).getGarageCarId());
        context.startActivity(CarDetailsIntent, options.toBundle());
    }

    private void loadImage(Context context, ImageView imageView, String imageUrl) {
        final WeakHandler mHandler = new WeakHandler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                loadImage(context, imageView, imageUrl);
            }
        };
        if (!((Activity) context).isFinishing()) {
            if (!imageUrl.equals("")) {
                Glide.with(context)
                        .load(imageUrl)
                        .fitCenter()
                        .transition(withCrossFade(factory))
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
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).brandLogoImageView.setImageDrawable(null);
            ((ViewHolder) holder).newCarImageView.setImageDrawable(null);
        }
    }
}
