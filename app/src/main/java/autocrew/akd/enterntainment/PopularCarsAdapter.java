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
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class PopularCarsAdapter extends RecyclerView.Adapter<PopularCarsAdapter.ViewHolder> {

    private List<PopularCarModel> popularCarModelList;
    private Context context;

    private int LOAD_IMAGE = 0;
    private int LOAD_LOGO = 1;

    public PopularCarsAdapter(Context context, List<PopularCarModel> popularCarModelList) {
        this.popularCarModelList = popularCarModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public PopularCarsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PopularCarsAdapter.ViewHolder holder, final int position) {
        String carBrand = popularCarModelList.get(position).getCarBrand();
        String carName = popularCarModelList.get(position).getCarName();
        String carImageUrl = popularCarModelList.get(position).getCarImageUrl();
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/Circular Pro_bold.otf");

        holder.popularCarBrand.setText(carBrand);
        holder.popularCarName.setText(carName);

        holder.popularCarName.setTypeface(font);
        holder.popularCarBrand.setTypeface(font);
        holder.detailsTextViewBtn.setTypeface(font);
        ((TextView) holder.takeALookBtn.getChildAt(0)).setTypeface(font);

        holder.loadImage(context, holder.popularCarImageView, carImageUrl, LOAD_IMAGE);

        if (!popularCarModelList.get(position).getGarageCarId().trim().equals("")) {
            FirebaseFirestore.getInstance().collection("CAR_BRANDS").whereEqualTo("brand_name", popularCarModelList.get(position).getCarBrand())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    holder.loadImage(holder.itemView.getContext(), holder.brandLogoImageView, documentSnapshot.get("brand_logo_url").toString(), LOAD_LOGO);
                                }
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(holder.itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Pair[] pairs = new Pair[1];
                    pairs[0] = new Pair<View, String>(holder.popularCarName, "testTransition");
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);
                    Intent CarDetailsIntent = new Intent(context, CarDetailsActivity.class);
                    CarDetailsIntent.putExtra("garage_car_id", popularCarModelList.get(position).getGarageCarId());
                    context.startActivity(CarDetailsIntent, options.toBundle());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return popularCarModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout constraintLayout;
        private ImageView popularCarImageView;
        private TextView popularCarBrand;
        private TextView popularCarName;
        private TextView detailsTextViewBtn;
        private LinearLayout takeALookBtn;
        private ImageView brandLogoImageView;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            constraintLayout = itemView.findViewById(R.id.constraintLayout);
            popularCarImageView = itemView.findViewById(R.id.popular_car_imageview);
            popularCarBrand = itemView.findViewById(R.id.popular_car_brand);
            popularCarName = itemView.findViewById(R.id.popular_car_name);
            detailsTextViewBtn = itemView.findViewById(R.id.details_textview);
            takeALookBtn = itemView.findViewById(R.id.take_a_look_btn);
            brandLogoImageView = itemView.findViewById(R.id.brand_logo_imageView);
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
}
