package app.bonapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.bonapp.R;
import com.daimajia.swipe.SwipeLayout;

import java.util.List;
import java.util.Locale;

import app.bonapp.interfaces.OnRecyclerViewItemClickListener;
import app.bonapp.models.CartItemModel;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin1 on 5/10/17.
 */

public class CheckoutItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



    private List<CartItemModel> checkoutItemsList;
    private Context context;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    public CheckoutItemsAdapter(List<CartItemModel> checkoutItemsList, Context context) {
        this.checkoutItemsList = checkoutItemsList;
        this.context = context;
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_checkout_item, parent, false);

            return new CheckoutItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.row_tax, parent, false);

            return new TaxViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CheckoutItemViewHolder) {
            ((CheckoutItemViewHolder) holder).tvItemName.setText(checkoutItemsList.get(position).getItemName());
            ((CheckoutItemViewHolder) holder).tvPrice.setText(checkoutItemsList.get(position).getItemQuantity() + "x" +
                    checkoutItemsList.get(position).getItemPrice());

            ((CheckoutItemViewHolder) holder).tvQuantity.setText(String.valueOf(checkoutItemsList.get(position).getItemQuantity()));
            ((CheckoutItemViewHolder) holder).llEditQuantity.setVisibility(checkoutItemsList.get(position).isEditModeOn() ? View.VISIBLE : View.GONE);
        } else {
            ((TaxViewHolder)holder).tvTaxNamePerc.setText(checkoutItemsList.get(position).getItemName());
            ((TaxViewHolder)holder).tvTaxNamePerc.append(" (");
            ((TaxViewHolder)holder).tvTaxNamePerc.append(String.format(Locale.getDefault(),"%.0f",checkoutItemsList.get(position).getPercentageTax()));
            ((TaxViewHolder)holder).tvTaxNamePerc.append("%)");
            try {
                double price= Double.parseDouble(checkoutItemsList.get(position).getItemPrice());
                ((TaxViewHolder)holder).tvTaxPrice.setText(String.format(Locale.getDefault(),"%.2f",price));
            }
            catch (NumberFormatException ne){
                ne.printStackTrace();
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        return checkoutItemsList.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return checkoutItemsList.size();
    }

    class CheckoutItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.row_iv_delete)
        ImageView rowIvDelete;
        @BindView(R.id.iv_minus)
        ImageView ivMinus;
        @BindView(R.id.tv_quantity)
        TextView tvQuantity;
        @BindView(R.id.iv_plus)
        ImageView ivPlus;
        @BindView(R.id.ll_edit_quantity)
        LinearLayout llEditQuantity;
        @BindView(R.id.tv_item_name)
        TextView tvItemName;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.swipe)
        SwipeLayout swipe;
        @BindView(R.id.rl_delete)
        RelativeLayout rlDelete;

        public CheckoutItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            rlDelete.setOnClickListener(this);
            ivMinus.setOnClickListener(this);
            ivPlus.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onRecyclerViewItemClickListener != null) {
                onRecyclerViewItemClickListener.onRecyclerViewItemClick(view, getAdapterPosition());
            }
        }
    }

    class TaxViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_tax_name_perc)
        TextView tvTaxNamePerc;
        @BindView(R.id.tv_tax_price)
        TextView tvTaxPrice;
        @BindView(R.id.ll_tax)
        LinearLayout llTax;
        public TaxViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

}
