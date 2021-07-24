package autocrew.akd.enterntainment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class TestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<UpcomingCarModel> upcomingCarModelList;
    private Context context;
    private int type;
    private int dataType;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private int LOAD_IMAGE = 0;
    private int LOAD_LOGO = 1;

    private int lastPosition = -1;

    public TestAdapter(Context context, List<UpcomingCarModel> upcomingCarModelList, int type, int dataType) {
        this.upcomingCarModelList = upcomingCarModelList;
        this.context = context;
        this.type = type;
        this.dataType = dataType;
    }

    @Override
    public int getItemViewType(int position) {
        return upcomingCarModelList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = null;
            switch (type) {
                case 0:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.upcoming_item_layout, parent, false);
                    break;
                case 1:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mini_upcoming_item_layout, parent, false);
                    break;
            }
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            String carImageUrl = upcomingCarModelList.get(position).getCarImageUrl();
            String carTitle = upcomingCarModelList.get(position).getCarTitle();
            String carsubtitle = upcomingCarModelList.get(position).getCarSubtitle();
            Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/Circular Pro_bold.otf");

            if (!((Activity) context).isFinishing()) {
                ((ViewHolder) holder).loadImage(context, ((ViewHolder) holder).carImageView, carImageUrl, LOAD_IMAGE);
            }

            ((ViewHolder) holder).carTitle.setText(carTitle);
            ((ViewHolder) holder).carSubtitle.setText(carsubtitle);

            ((ViewHolder) holder).carTitle.setTypeface(font);
            ((ViewHolder) holder).carSubtitle.setTypeface(font);
            if (!carTitle.equals("")) {
                ((ViewHolder) holder).detailsBtn.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).detailsBtn.setTypeface(font);
            }

            if (!upcomingCarModelList.get(position).getCarTitle().equals("")) {
                ((ViewHolder) holder).detailsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ViewHolder) holder).setIntent();
                    }
                });
            }

            if (type == 1 && !upcomingCarModelList.get(position).getCarTitle().equals("")) {
                ((ViewHolder) holder).constraintLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ViewHolder) holder).setIntent();
                    }
                });
            } else if (type == 0 && !upcomingCarModelList.get(position).getCarTitle().equals("")) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ViewHolder) holder).setIntent();
                    }
                });
            }

            FirebaseFirestore.getInstance().collection("CAR_BRANDS").whereEqualTo("brand_name", carsubtitle)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    String logoUrl = documentSnapshot.get("brand_logo_url").toString();
                                    if (!((Activity) context).isFinishing()) {
                                        ((ViewHolder) holder).loadImage(context, ((ViewHolder) holder).brandLogoImageView, logoUrl, LOAD_LOGO);
                                    }
                                }
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(holder.itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
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

        private ImageView carImageView;
        private TextView carTitle;
        private TextView carSubtitle;
        private Button detailsBtn;
        private ImageView brandLogoImageView;

        private ConstraintLayout constraintLayout;
        private ConstraintLayout constraintLayout2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            carImageView = itemView.findViewById(R.id.car_imageview);
            carTitle = itemView.findViewById(R.id.car_title);
            carSubtitle = itemView.findViewById(R.id.car_subtitle);
            detailsBtn = itemView.findViewById(R.id.details_btn);
            brandLogoImageView = itemView.findViewById(R.id.brand_logo_imageView);

            if (type == 1) {
                constraintLayout = itemView.findViewById(R.id.constraintLayout);
            }
            if (dataType == 1) {
                constraintLayout2 = itemView.findViewById(R.id.constraintLayout2);
                constraintLayout2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#3f4b83")));
            }
        }

        private void setIntent() {
            Pair[] pairs = new Pair[1];
            pairs[0] = new Pair<View, String>(carImageView, "testTransition");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) itemView.getContext(), pairs);
            Intent CarDetailsIntent = new Intent(itemView.getContext(), CarDetailsActivity.class);
            CarDetailsIntent.putExtra("garage_car_id", upcomingCarModelList.get(getAdapterPosition()).getGarageCarId());
            itemView.getContext().startActivity(CarDetailsIntent, options.toBundle());
        }

        private void loadImage(Context context, ImageView imageView, String imageUrl, int type) {
            final WeakHandler mHandler = new WeakHandler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    loadImage(context, imageView, imageUrl, type);
                }
            };

            if (!imageUrl.equals("")) {
                if (type == LOAD_IMAGE) {
                    if (!((Activity) context).isFinishing()) {
                        Glide.with(context)
                                .load(imageUrl)
                                .placeholder(R.drawable.popular_cars_image_placeholder)
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
                } else if (type == LOAD_LOGO) {
                    if (!((Activity) context).isFinishing()) {
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
                }
            }
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
        return upcomingCarModelList == null ? 0 : upcomingCarModelList.size();
    }
}
