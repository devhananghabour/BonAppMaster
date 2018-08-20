package app.bonapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.app.bonapp.R;

import java.util.ArrayList;
import java.util.List;

import app.bonapp.interfaces.OnSelectItemListener;
import app.bonapp.models.countrystatecity.DropDownData;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin1 on 1/8/17.
 */

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.CustomViewHolder> implements Filterable {
    private Context mContext;
    private   List<DropDownData> countryCodeList;
    private   List<DropDownData> filterList;
    private OnSelectItemListener onSelectItemListener;
    private TextView tvNoRecord;

    public ListingAdapter(Context mContext, List<DropDownData> countryCodeList, TextView tvNoRecord) {
        this.mContext = mContext;
        this.countryCodeList = countryCodeList;
        this.filterList = new ArrayList<>();
        filterList.addAll(countryCodeList);
        this.tvNoRecord=tvNoRecord;
    }

    public void setOnSelectItemListener(OnSelectItemListener onSelectItemListener) {
        this.onSelectItemListener = onSelectItemListener;
    }


    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View inflatingView = LayoutInflater.from(mContext).inflate(R.layout.row_code, parent, false);

        return new CustomViewHolder(inflatingView);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.tvCountryNameWithCode.setText(filterList.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }

    @Override
    public Filter getFilter() {
        return new UserFilter(this, countryCodeList,tvNoRecord);
    }


    private static class UserFilter extends Filter {
        private final ListingAdapter adapter;
        private final List<DropDownData> originalList;
        private final List<DropDownData> filterList;
        private TextView tvNoRecord;

        private UserFilter(ListingAdapter adapter, List<DropDownData> originalList, TextView tvNoRecord) {
            super();
            this.adapter = adapter;
            this.originalList = originalList;
            this.filterList = new ArrayList<>();
            this.tvNoRecord=tvNoRecord;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filterList.clear();
            FilterResults filterResults = new FilterResults();
            if (constraint.length() == 0) {
                filterList.addAll(originalList);
            } else {
                filterList.clear();
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (int i = 0; i < originalList.size(); i++) {
                    DropDownData countryCode = originalList.get(i);
                    if (countryCode.getName().toLowerCase().contains(filterPattern)) {
                        filterList.add(countryCode);
                    }
                }
            }

            filterResults.values = filterList;
            filterResults.count = filterList.size();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.filterList.clear();
            adapter.filterList.addAll((List<DropDownData>) results.values);
            tvNoRecord.setVisibility(adapter.filterList.isEmpty()?View.VISIBLE:View.GONE);
            adapter.notifyDataSetChanged();
        }
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.row_tv_country_name)
        TextView tvCountryNameWithCode;

        public CustomViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
           if (onSelectItemListener!=null){
               onSelectItemListener.onSelectItem(filterList.get(getAdapterPosition()).getId(),filterList.get(getAdapterPosition()).getName());
           }
        //    contactCodePickerActivity.sendAndFinishActivity(filterList.get(getAdapterPosition()).getCountryCode());

        }
    }
}
