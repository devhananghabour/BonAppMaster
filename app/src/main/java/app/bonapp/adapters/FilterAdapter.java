package app.bonapp.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.bonapp.R;

import java.util.List;

import app.bonapp.models.FilterModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder> {

    private List<FilterModel>    filters;
    private OnFilterItemListener listener;
    private int selectedItemPosition = -1;

    public FilterAdapter(List<FilterModel> filters, OnFilterItemListener listener) {
        this.filters = filters;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_filter, parent, false);
        return new FilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int position) {
        holder.bind(filters.get(position), selectedItemPosition==position);
    }

    @Override
    public int getItemCount() {
        return filters != null ? filters.size() : 0;
    }

    public interface OnFilterItemListener {
        void onItemClicked(FilterModel filterModel);
    }

    class FilterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row_filter_name)
        TextView name;

        FilterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(FilterModel filterModel, boolean selected) {
            name.setText(filterModel.getName());
            itemView.setSelected(selected);
        }

        @OnClick(R.id.row_filter_root)
        public void onItemClicked(View view) {
            if(view.isSelected()) {
                selectedItemPosition = -1;
                listener.onItemClicked(null);
            }else{
                selectedItemPosition = getAdapterPosition();
                listener.onItemClicked(filters.get(getAdapterPosition()));
            }
            notifyDataSetChanged();
        }
    }

}
