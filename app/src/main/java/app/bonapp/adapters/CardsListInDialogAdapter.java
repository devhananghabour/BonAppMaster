package app.bonapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.bonapp.R;

import java.util.List;

import app.bonapp.interfaces.OnRecyclerViewItemClickListener;
import app.bonapp.models.cardslist.RESULT;
import butterknife.BindView;
import butterknife.ButterKnife;
/**
 * Created by appinventiv on 27/11/17.
 */

public class CardsListInDialogAdapter extends RecyclerView.Adapter<CardsListInDialogAdapter.CardsListViewHolder> {

    private Context context;
    private List<RESULT> cardsList;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    public CardsListInDialogAdapter(Context context, List<RESULT> cardsList) {
        this.context = context;
        this.cardsList = cardsList;
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    @Override
    public CardsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_dialog_card, parent, false);

        return new CardsListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardsListViewHolder holder, int position) {
        if (holder != null) {

            holder.tvCardLabel.setText(cardsList.get(position).getCardName());
            holder.tvCardLabel.append("****");
            holder.tvCardLabel.append(cardsList.get(position).getCardNumber());
        }
    }

    @Override
    public int getItemCount() {
        return cardsList.size();
    }

    class CardsListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_card_label)
        TextView tvCardLabel;
        public CardsListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onRecyclerViewItemClickListener != null) {
                onRecyclerViewItemClickListener.onRecyclerViewItemClick(v, getAdapterPosition());
            }
        }
    }

}
