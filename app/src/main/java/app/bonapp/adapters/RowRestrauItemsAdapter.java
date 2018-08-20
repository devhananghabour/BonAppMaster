package app.bonapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.bonapp.R;

import java.util.List;
import java.util.Locale;

import app.bonapp.interfaces.OnRecyclerViewItemClickListener;
import app.bonapp.models.merchantposts.Deal;
import app.bonapp.utils.AppUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin1 on 6/10/17.
 */

public class RowRestrauItemsAdapter extends RecyclerView.Adapter<RowRestrauItemsAdapter.ItemsViewHolder> {


    private Context context;
    private List<Deal> itemsList;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;
    private String merchantId;
    private int pos;
    private OnRestaurantClickListener onRestaurantClickListener;

    public RowRestrauItemsAdapter(Context context, List<Deal> itemsList, String merchantId, int pos, OnRestaurantClickListener listener) {
        this.context = context;
        this.itemsList = itemsList;
        this.merchantId = merchantId;
        this.pos = pos;
        this.onRestaurantClickListener = listener;
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    @Override
    public ItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_restrau_items_home, parent, false);

        return new ItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemsViewHolder holder, int position) {
        if (holder != null) {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            //if you need three fix imageview in width
            int devicewidth = displaymetrics.widthPixels - 80;
            holder.cvItemHome.getLayoutParams().width = devicewidth;

            holder.tvItemOriginalPrice.setPaintFlags(holder.tvItemOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvItemName.setText(itemsList.get(position).getDealTitle());
            holder.tvEndTime.setText(context.getString(R.string.pickup_before) + " " + AppUtils.getInstance().parseDateToTime(itemsList.get(position).getEndTime()));
            holder.tvQuantityLeft.setText(itemsList.get(position).getItemLeft());
            try {
                double originalPrice = Double.parseDouble(itemsList.get(position).getOriginalPrice());
                double sellingPrice = Double.parseDouble(itemsList.get(position).getNewPrice());
                double discount = 100 - (sellingPrice / originalPrice) * 100;
                holder.tvItemDiscount.setText(String.format(Locale.getDefault(), "%.0f", discount) + "%");
                holder.tvItemOriginalPrice.setText(String.format(Locale.getDefault(), "%.2f", originalPrice) + " " + context.getString(R.string.currency));
                holder.tvItemRevisedPrice.setText(String.format(Locale.getDefault(), "%.2f", sellingPrice) + " " + context.getString(R.string.currency));
            } catch (NumberFormatException ne) {
                ne.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    class ItemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.tv_item_name)
        TextView tvItemName;
        @BindView(R.id.tv_item_revised_price)
        TextView tvItemRevisedPrice;
        @BindView(R.id.tv_item_original_price)
        TextView tvItemOriginalPrice;
        @BindView(R.id.tv_item_discount)
        TextView tvItemDiscount;
        @BindView(R.id.tv_quantity_left)
        TextView tvQuantityLeft;
        @BindView(R.id.cv_item_home)
        CardView cvItemHome;
        @BindView(R.id.tv_end_time)
        TextView tvEndTime;
        @BindView(R.id.rl_row_deal)
        RelativeLayout rlRowDeal;


        public ItemsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            //rlRowDeal.setOnTouchListener(this);
        }

        @Override
        public void onClick(View view) {
            if (!itemsList.isEmpty()) {
                onRestaurantClickListener.onItemClicked(merchantId, pos);
            }
        }
    }

    public interface OnRestaurantClickListener{
        void onItemClicked(String merchantId, int position);
    }


}
