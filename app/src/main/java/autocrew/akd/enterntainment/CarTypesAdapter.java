package autocrew.akd.enterntainment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
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
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CarTypesAdapter extends RecyclerView.Adapter<CarTypesAdapter.ViewHolder> {

    private List<CarTypeModel> carTypeModelList;
    private Context context;

    private List<String> colorList = new ArrayList<String>() {{
        add("#ecb390");
        add("#beebe9");
        add("#f64b3c");
        add("#c295d8");
        add("#6e5773");
        add("#ffb677");
    }};

    public CarTypesAdapter(Context context, List<CarTypeModel> carTypeModelList) {
        this.context = context;
        this.carTypeModelList = carTypeModelList;
    }

    @NonNull
    @Override
    public CarTypesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_type_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarTypesAdapter.ViewHolder holder, int position) {
        CarTypeModel currentCarType = carTypeModelList.get(position);
        String carTyoeTitle = currentCarType.getCarTypeName();
        String carTypeImageUrl = currentCarType.getCarTypeImageUrl();

        holder.carTypeName.setText(carTyoeTitle);
        holder.loadType(context, holder.carTypeImage, carTypeImageUrl);
        customView(holder.constraintLayout, "#52575d", "#00000000");
    }

    @Override
    public int getItemCount() {
        return carTypeModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView carTypeName;
        private ImageView carTypeImage;
        private ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            carTypeName = itemView.findViewById(R.id.car_type_name);
            carTypeImage = itemView.findViewById(R.id.car_type_image);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);

            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent carsGridIntent = new Intent(itemView.getContext(), CarsGridActivity.class);
                    carsGridIntent.putExtra("fieldName", "type");
                    carsGridIntent.putExtra("fieldValue", carTypeModelList.get(getAdapterPosition()).getCarTypeName());
                    itemView.getContext().startActivity(carsGridIntent);
                }
            });
        }

        private void loadType(Context context, ImageView imageView, String imageUrl){
            final WeakHandler mHandler = new WeakHandler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    loadType(context, imageView,imageUrl);
                }
            };

            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.explore_image_placeholder)
                    .fitCenter()
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            mHandler.postDelayed(runnable,1);
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

    public static void customView(ConstraintLayout constraintLayout, String topColor, String bottomColor) {
        int[] colors = {Color.parseColor(topColor), Color.parseColor(bottomColor)};
        GradientDrawable shape = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{40, 40, 40, 40, 40, 40, 40, 40});
        constraintLayout.setBackground(shape);
    }

    public String getRandomElement(List<String> list) {
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }
}
