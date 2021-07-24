package autocrew.akd.enterntainment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.badoo.mobile.util.WeakHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class BrandsAdapter extends RecyclerView.Adapter<BrandsAdapter.ViewHolder> {

    private List<BrandsModel> brandsModelList;
    private Context context;
    DrawableCrossFadeFactory factory =
            new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();

    public BrandsAdapter(Context context, List<BrandsModel> brandsModelList) {
        this.brandsModelList = brandsModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public BrandsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_brand_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BrandsAdapter.ViewHolder holder, int position) {
        BrandsModel currentBrandItem = brandsModelList.get(position);
        String brandLogoUrl = currentBrandItem.getBrandLogoUrl();
        String brandBackImageLogoUrl = currentBrandItem.getBrandBackImageUrl();
        String brandName = currentBrandItem.getBrandName();

        holder.brandName.setText(brandName);
        holder.brandName.setTypeface(AppMainActivity.fontUbuntuMedium);
        holder.loadLogo(context, holder.brandLogo, brandLogoUrl);
        holder.loadNetWorkImage(context, holder.brandCarBackImageView, brandBackImageLogoUrl);
    }

    @Override
    public int getItemCount() {
        return brandsModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView brandLogo;
        private TextView brandName;
        private ConstraintLayout constraintLayout;
        private ImageView brandCarBackImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            brandLogo = itemView.findViewById(R.id.brand_image);
            brandName = itemView.findViewById(R.id.brand_name);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
            brandCarBackImageView = itemView.findViewById(R.id.brand_car_back_imageView);

            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent carsGridIntent = new Intent(itemView.getContext(), CarsGridActivity.class);
                    carsGridIntent.putExtra("fieldName", "car_brand");
                    carsGridIntent.putExtra("fieldValue", brandsModelList.get(getAdapterPosition()).getBrandName());
                    itemView.getContext().startActivity(carsGridIntent);
                }
            });
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
                    .transition(withCrossFade(factory))
                    .transform(new CenterCrop(), new RoundedCorners(27))
                    .priority(Priority.HIGH)
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

        private void loadLogo(Context context, ImageView imageView, String imageUrl) {
            final WeakHandler mHandler = new WeakHandler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    loadLogo(context, imageView, imageUrl);
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
    }
}
