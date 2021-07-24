package autocrew.akd.enterntainment;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CarSpecificationsAdapter extends RecyclerView.Adapter<CarSpecificationsAdapter.ViewHolder> {

    private Context context;
    private List<SpecificationModel> specificationModelList;

    public CarSpecificationsAdapter(Context context, List<SpecificationModel> specificationModelList) {
        this.context = context;
        this.specificationModelList = specificationModelList;
    }

    @Override
    public int getItemViewType(int position) {
        switch (specificationModelList.get(position).getType()){
            case 0:
                return SpecificationModel.SPECIFICATION_TITLE;
            case 1:
                return SpecificationModel.SPECIFICATION_BODY;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public CarSpecificationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType){
            case SpecificationModel.SPECIFICATION_TITLE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spec_title_layout, parent, false);
                return new ViewHolder(view);
            case SpecificationModel.SPECIFICATION_BODY:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spec_feature_name_layout, parent, false);
                return new ViewHolder(view);
            default:
                return null;

        }
    }

    @Override
    public void onBindViewHolder(@NonNull CarSpecificationsAdapter.ViewHolder holder, int position) {
        switch (specificationModelList.get(position).getType()){
            case SpecificationModel.SPECIFICATION_TITLE:
                holder.setSpecTitle(specificationModelList.get(position).getTitle());
                break;
            case SpecificationModel.SPECIFICATION_BODY:
                holder.setSpecFeatureName(specificationModelList.get(position).getTitle());

        }
    }

    @Override
    public int getItemCount() {
        return specificationModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView specTitleTextView;
        private TextView specFeatureNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        private void setSpecTitle(String specTitle){
            specTitleTextView = itemView.findViewById(R.id.spec_title_textView);
            specTitleTextView.setText(specTitle);
            specTitleTextView.setTypeface(Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Industry Medium Italic.ttf"));
        }

        private void setSpecFeatureName(String specFeatureName){
            specFeatureNameTextView = itemView.findViewById(R.id.spec_feature_name_textView);
            specFeatureNameTextView.setText(specFeatureName);
        }
    }
}
