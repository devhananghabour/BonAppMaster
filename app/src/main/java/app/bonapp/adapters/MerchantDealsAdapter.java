package app.bonapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.bonapp.R;

import java.util.List;

import app.bonapp.interfaces.OnRecyclerViewItemClickListener;
import app.bonapp.models.merchantdeals.DATum;
import app.bonapp.utils.AppUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin1 on 5/10/17.
 */

public class MerchantDealsAdapter extends RecyclerView.Adapter<MerchantDealsAdapter.MerchantDealViewHoldewr> {

    private List<DATum> merchantDealsList;
    private Context context;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;
    private AppUtils appUtils;

    public MerchantDealsAdapter(List<DATum> merchantDealsList, Context context) {
        this.merchantDealsList = merchantDealsList;
        this.context = context;
        appUtils=AppUtils.getInstance();
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    @Override
    public MerchantDealViewHoldewr onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_merchant_deals, parent, false);

        return new MerchantDealViewHoldewr(view);
    }

    @Override
    public void onBindViewHolder(MerchantDealViewHoldewr holder, int position) {
        if (holder != null) {
            holder.tvDealName.setText(merchantDealsList.get(position).getDealTitle());
            if (appUtils.isToday(AppUtils.getInstance().milliseconds(merchantDealsList.get(position).getDealCreatedOn()))){
                String time= AppUtils.getInstance().parseDateToTime(merchantDealsList.get(position).getStartTime())+" - "+AppUtils.getInstance().parseDateToTime(merchantDealsList.get(position).getEndTime());
                holder.tvTime.setText(time);
                /*holder.tvTime.setText(context.getString(R.string.pickup_before) + " " +
                        AppUtils.getInstance().parseDateToTime(merchantDealsList.get(position).getEndTime()));*/
            }else {
                if (appUtils.isYesterday(appUtils.milliseconds(merchantDealsList.get(position).getDealCreatedOn()))){
                    holder.tvTime.setText(R.string.yesterday);
                }else {
                    holder.tvTime.setText(AppUtils.getInstance().parseToDateFormat(merchantDealsList.get(position).getDealCreatedOn()));
                }
            }

            //1 activate 2 deactivate
            if (merchantDealsList.get(position).getDealStatus().equals("1")){
                holder.viewSide.setVisibility(View.VISIBLE);
                holder.tvLeftCount.setText(merchantDealsList.get(position).getItemLeft()+" "+context.getString(R.string.left));
            }else {
                holder.viewSide.setVisibility(View.GONE);
                holder.tvLeftCount.setText(merchantDealsList.get(position).getItemSold()+" "+context.getString(R.string.sold));
            }
            //animateView(holder.itemView);

        }
    }

    @Override
    public int getItemCount() {
        return merchantDealsList.size();
    }

    class MerchantDealViewHoldewr extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.view_side)
        View viewSide;
        @BindView(R.id.iv_right)
        ImageView ivRight;
        @BindView(R.id.tv_deal_name)
        TextView tvDealName;
        @BindView(R.id.tv_left_count)
        TextView tvLeftCount;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.ll_row)
        LinearLayout llRow;
        public MerchantDealViewHoldewr(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            llRow.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onRecyclerViewItemClickListener !=null){
                onRecyclerViewItemClickListener.onRecyclerViewItemClick(view,getAdapterPosition());
            }
        }
    }


    private void animateView(View itemView){
        Animation fallDownAnimation = AnimationUtils.loadAnimation(context,R.anim.item_animation_fall_down);
        itemView.startAnimation(fallDownAnimation);
    }

}
