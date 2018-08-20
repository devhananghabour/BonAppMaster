package app.bonapp.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.bonapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.bonapp.interfaces.OnRecyclerViewItemClickListener;
import app.bonapp.models.merchantposts.Deal;
import app.bonapp.utils.AppUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin1 on 5/10/17.
 */

public class RestaurantDealsListingAdapter extends RecyclerView.Adapter<RestaurantDealsListingAdapter.MerchantDealViewHoldewr> {


    private List<Deal> merchantDealsList;
    private Context context;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    public RestaurantDealsListingAdapter(List<Deal> merchantDealsList, Context context) {
        this.merchantDealsList = merchantDealsList;
        this.context = context;
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    @Override
    public MerchantDealViewHoldewr onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_restrau_deals, parent, false);

        return new MerchantDealViewHoldewr(view);
    }

    @Override
    public void onBindViewHolder(MerchantDealViewHoldewr holder, int position) {
        if (holder != null) {
            holder.tvItemName.setText(merchantDealsList.get(position).getDealTitle());
            holder.tvItemOriginalPrice.setText(merchantDealsList.get(position).getOriginalPrice()+" " + context.getString(R.string.currency));
            holder.tvItemRevisedPrice.setText(merchantDealsList.get(position).getNewPrice()+" " + context.getString(R.string.currency));
            holder.tvItemOriginalPrice.setPaintFlags(holder.tvItemOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvItemDescription.setText(merchantDealsList.get(position).getDealDescriptoin());
            holder.tvQuantityLeft.setText(merchantDealsList.get(position).getItemLeft());

            if (isToday(milliseconds(merchantDealsList.get(position).getDealCreatedOn()))) {
                /*String time = AppUtils.getInstance().parseDateToTime(merchantDealsList.get(position).getStartTime()) + " - " +
                        AppUtils.getInstance().parseDateToTime(merchantDealsList.get(position).getEndTime());
                holder.tvTime.setText(time);*/
                holder.tvTime.setText(context.getString(R.string.pickup_before) + " " +
                        AppUtils.getInstance().parseDateToTime(merchantDealsList.get(position).getEndTime()));
            } else {
                if (isYesterday(milliseconds(merchantDealsList.get(position).getDealCreatedOn()))) {
                    holder.tvTime.setText(R.string.yesterday);
                } else {
                    holder.tvTime.setText(AppUtils.getInstance().parseToDateFormat(merchantDealsList.get(position).getDealCreatedOn()));
                }
            }
            try {
                double originalPrice = Double.parseDouble(merchantDealsList.get(position).getOriginalPrice());
                double sellingPrice = Double.parseDouble(merchantDealsList.get(position).getNewPrice());
                double discount = 100 - (sellingPrice / originalPrice) * 100;
                holder.tvDiscountPerc.setText( String.format(Locale.getDefault(), "%.0f", discount) + "%");
            } catch (NumberFormatException ne) {
                ne.printStackTrace();
            }

            holder.flFaded.setVisibility(merchantDealsList.get(position).getItemLeft().equals("0") ? View.VISIBLE : View.GONE);

            //1 activate 2 deactivate
            if (merchantDealsList.get(position).isAddedToCart()) {
                holder.viewSide.setVisibility(View.VISIBLE);
            } else {
                holder.viewSide.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return merchantDealsList.size();
    }

    class MerchantDealViewHoldewr extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.view_side)
        View viewSide;
        @BindView(R.id.tv_item_name)
        TextView tvItemName;
        @BindView(R.id.tv_item_revised_price)
        TextView tvItemRevisedPrice;
        @BindView(R.id.tv_item_original_price)
        TextView tvItemOriginalPrice;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.ll_price)
        LinearLayout llPrice;
        @BindView(R.id.tv_quantity_left)
        TextView tvQuantityLeft;
        @BindView(R.id.tv_item_description)
        TextView tvItemDescription;
        @BindView(R.id.fl_faded)
        FrameLayout flFaded;
        @BindView(R.id.tv_discount_perc)
        TextView tvDiscountPerc;


        public MerchantDealViewHoldewr(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onRecyclerViewItemClickListener != null) {
                onRecyclerViewItemClickListener.onRecyclerViewItemClick(view, getAdapterPosition());
            }
        }
    }

    public boolean isYesterday(long date) {
        Calendar now = Calendar.getInstance();
        Calendar cdate = Calendar.getInstance();
        cdate.setTimeInMillis(date);

        now.add(Calendar.DATE, -1);

        return now.get(Calendar.YEAR) == cdate.get(Calendar.YEAR)
                && now.get(Calendar.MONTH) == cdate.get(Calendar.MONTH)
                && now.get(Calendar.DATE) == cdate.get(Calendar.DATE);
    }

    private boolean isToday(long date) {
        return DateUtils.isToday(date);
    }

    private long milliseconds(String date) {
        //String date_ = date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date mDate = sdf.parse(date);
            long timeInMilliseconds = AppUtils.getInstance().gmttoLocalDate(mDate).getTime();
            return timeInMilliseconds;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }


}
