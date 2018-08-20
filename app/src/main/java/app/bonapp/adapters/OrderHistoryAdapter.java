package app.bonapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.bonapp.R;

import java.util.List;

import app.bonapp.constants.Constants;
import app.bonapp.interfaces.OnRecyclerViewItemClickListener;
import app.bonapp.models.DeliveryType;
import app.bonapp.models.OrderStatus;
import app.bonapp.models.orderehistory.DATum;
import app.bonapp.utils.AppSharedPrefs;
import app.bonapp.utils.AppUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by appinventiv on 7/11/17.
 */

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder> {


    private List<DATum> orderHistoryList;
    private Context context;
    private AppUtils appUtils;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    public OrderHistoryAdapter(List<DATum> orderHistoryList, Context context) {
        this.orderHistoryList = orderHistoryList;
        this.context = context;
        appUtils = AppUtils.getInstance();
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    @Override
    public OrderHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_order_history, parent, false);

        return new OrderHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderHistoryViewHolder holder, int position) {
        if (holder != null) {
            if (AppSharedPrefs.getInstance(context).getString(AppSharedPrefs.PREF_KEY.USER_TYPE, "1").equals(Constants.MERCHANT_TYPE)) {
                holder.tvName.setText(orderHistoryList.get(position).getOrderCode());
                String price = orderHistoryList.get(position).getSubTotal() + " " + context.getString(R.string.currency);
                holder.tvPrice.setText(price);
                holder.viewSide.setVisibility(View.GONE);
                holder.tvCustomerName.setVisibility(View.VISIBLE);
                holder.tvCustomerName.setText(R.string.order_by);
                holder.tvCustomerName.append(" ");
                holder.tvCustomerName.append(orderHistoryList.get(position).getCustomerName());


            } else {
                holder.tvCustomerName.setVisibility(View.GONE);
                holder.tvName.setText(orderHistoryList.get(position).getItemName());
                if (appUtils.isToday(AppUtils.getInstance().milliseconds(orderHistoryList.get(position).getOrderedOn()))) {
                    String time = AppUtils.getInstance().parseToTimeAndDate(orderHistoryList.get(position).getOrderedOn());
                    holder.tvPrice.setText(time);
                    holder.viewSide.setVisibility(View.VISIBLE);
                } else {
                    holder.viewSide.setVisibility(View.GONE);
                    if (appUtils.isYesterday(appUtils.milliseconds(orderHistoryList.get(position).getOrderedOn()))) {
                        holder.tvPrice.setText(R.string.yesterday);
                    } else {
                        holder.tvPrice.setText(AppUtils.getInstance().parseToDateFormat(orderHistoryList.get(position).getOrderedOn()));
                    }
                }
            }
            holder.setOrderStatus(orderHistoryList.get(position));

        }

    }

    @Override
    public int getItemCount() {
        return orderHistoryList.size();
    }

    class OrderHistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.view_side)
        View viewSide;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.ll_order_history)
        LinearLayout llOrderHistory;
        @BindView(R.id.tv_customer_name)
        TextView tvCustomerName;
        @BindView(R.id.row_order_history_tvOrderStatus)
        TextView tvOrderStatus;


        public OrderHistoryViewHolder(View itemView) {
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

        private void setOrderStatus(DATum order) {
            if (order.getDeliveryType().equals(DeliveryType.DELIVERY.getValue())) {
                tvOrderStatus.setVisibility(View.VISIBLE);
                String orderStatus = "";
                if (order.getOrderStatus().equals(OrderStatus.OrderStatuses.ORDER_PLACED.getValue())) {
                    orderStatus = itemView.getContext().getString(R.string.order_placed);
                } else if (order.getOrderStatus().equals(OrderStatus.OrderStatuses.ORDER_ACCEPTED.getValue())) {
                    orderStatus = itemView.getContext().getString(R.string.order_accepted);
                } else if (order.getOrderStatus().equals(OrderStatus.OrderStatuses.ORDER_ON_THE_WAY.getValue())) {
                    orderStatus = itemView.getContext().getString(R.string.order_on_the_way);
                } else if (order.getOrderStatus().equals(OrderStatus.OrderStatuses.ORDER_DELIVERED.getValue())) {
                    orderStatus = itemView.getContext().getString(R.string.order_delivered);
                }
                tvOrderStatus.setText(orderStatus);
            } else {
                tvOrderStatus.setVisibility(View.GONE);
            }
        }
    }
/*
    private void animateView(View itemView){
        Animation fallDownAnimation = AnimationUtils.loadAnimation(context,R.anim.item_animation_fall_down);
        itemView.startAnimation(fallDownAnimation);
    }*/
}
