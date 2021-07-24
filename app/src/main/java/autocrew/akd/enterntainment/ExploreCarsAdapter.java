package autocrew.akd.enterntainment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.List;

public class ExploreCarsAdapter extends RecyclerView.Adapter<ExploreCarsAdapter.ViewHolder> {

    private List<ExploreCarsModel> exploreCarsModelList;
    private Context context;
    private int type;

    public ExploreCarsAdapter(Context context, int type, List<ExploreCarsModel> exploreCarsModelList) {
        this.exploreCarsModelList = exploreCarsModelList;
        this.type = type;
        this.context = context;
    }

    @NonNull
    @Override
    public ExploreCarsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (type) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_collection_item_layout, parent, false);
                break;
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mini_car_collection_item_layout, parent, false);
                break;
            default:
                Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExploreCarsAdapter.ViewHolder holder, int position) {
        if (exploreCarsModelList.size() != 0) {
            String carCollectionName = exploreCarsModelList.get(position).getCarCollectionName();
            String carCollectionImageUrl = exploreCarsModelList.get(position).getCarCollectionImageUrl();

            holder.carCollectionName.setText(carCollectionName);
            holder.carCollectionName.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Circular Pro_semi_bold.otf"));
            if (!((Activity) context).isFinishing()) {
                holder.setImage(context, holder.carCollectionImage, carCollectionImageUrl);
            }

            if (!exploreCarsModelList.get(position).getCarCollectionName().equals("")) {
                holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent carsGridIntent = new Intent(holder.itemView.getContext(), CarsGridActivity.class);
                        carsGridIntent.putExtra("fieldName", "collection");
                        carsGridIntent.putExtra("fieldValue", exploreCarsModelList.get(position).getCarCollectionName());
                        holder.itemView.getContext().startActivity(carsGridIntent);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        if (this.type == 1) {
            return 5;
        } else {
            return exploreCarsModelList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView carCollectionImage;
        private TextView carCollectionName;
        private ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            carCollectionImage = itemView.findViewById(R.id.car_collection_image);
            carCollectionName = itemView.findViewById(R.id.car_collection_name);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
        }

        private void setImage(Context context, ImageView imageView, String imageUrl) {
            final WeakHandler mHandler = new WeakHandler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    setImage(context, imageView, imageUrl);
                }
            };
            if (!((Activity) context).isFinishing()) {
                if (!imageUrl.equals("")) {
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
        }
    }
}
