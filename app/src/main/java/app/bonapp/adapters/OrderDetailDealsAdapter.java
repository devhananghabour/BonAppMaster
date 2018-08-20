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
import java.util.Locale;

import app.bonapp.models.ListData;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin1 on 5/10/17.
 */

public class OrderDetailDealsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



    private List<ListData> checkoutItemsList;
    private Context context;

    public OrderDetailDealsAdapter(List<ListData> checkoutItemsList, Context context) {
        this.checkoutItemsList = checkoutItemsList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_order_detail_deal, parent, false);

            return new CheckoutItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.row_tax, parent, false);

            return new TaxViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CheckoutItemViewHolder) {
            ((CheckoutItemViewHolder) holder).tvItemName.setText(checkoutItemsList.get(position).getName());
            ((CheckoutItemViewHolder) holder).tvPrice.setText(checkoutItemsList.get(position).getQuantity() + "x" +
                    checkoutItemsList.get(position).getPrice());

        } else {
            ((TaxViewHolder) holder).tvTaxNamePerc.setText(checkoutItemsList.get(position).getName());
            try {
                double price = Double.parseDouble(checkoutItemsList.get(position).getPrice());
                ((TaxViewHolder) holder).tvTaxPrice.setText(String.format(Locale.getDefault(), "%.2f", price));
            } catch (NumberFormatException ne) {
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

    class CheckoutItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_item_name)
        TextView tvItemName;
        @BindView(R.id.tv_price)
        TextView tvPrice;

        public CheckoutItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

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
