package autocrew.akd.enterntainment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommonSpecsAdapter extends RecyclerView.Adapter<CommonSpecsAdapter.ViewHolder> {

    private int specImages[] = {R.drawable.car_engine, R.drawable.car_seat, R.drawable.speedometer, R.drawable.car_gear, R.drawable.car_bags};

    private Context context;
    private List<String> commonSpecsList;

    public CommonSpecsAdapter(Context context, List<String> commonSpecsList) {
        this.context = context;
        this.commonSpecsList = commonSpecsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.common_specs_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.specImageView.setImageResource(specImages[position]);
        holder.specTextView.setText(commonSpecsList.get(position));
    }

    @Override
    public int getItemCount() {
        return commonSpecsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView specImageView;
        private TextView specTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            specImageView = itemView.findViewById(R.id.spec_imageview);
            specTextView = itemView.findViewById(R.id.spec_textView);
        }
    }
}
