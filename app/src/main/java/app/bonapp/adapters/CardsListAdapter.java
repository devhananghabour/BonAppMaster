package app.bonapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
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

public class CardsListAdapter extends RecyclerView.Adapter<CardsListAdapter.CardsListViewHolder> {


    private Context context;
    private List<RESULT> cardsList;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    public CardsListAdapter(Context context, List<RESULT> cardsList) {
        this.context = context;
        this.cardsList = cardsList;
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    @Override
    public CardsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_payment_details, parent, false);

        return new CardsListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardsListViewHolder holder, int position) {
        if (holder != null) {
            holder.tvCardNo.setText("****");
            holder.tvCardNo.append(cardsList.get(position).getCardNumber());
            holder.tvCardName.setText(cardsList.get(position).getCardName());

            animateView(holder.itemView);
        }
    }

    @Override
    public int getItemCount() {
        return cardsList.size();
    }

    class CardsListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_card_no)
        TextView tvCardNo;
        @BindView(R.id.iv_delete)
        ImageView ivDelete;
        @BindView(R.id.tv_card_name)
        TextView tvCardName;
        public CardsListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            ivDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onRecyclerViewItemClickListener!=null){
                onRecyclerViewItemClickListener.onRecyclerViewItemClick(v,getAdapterPosition());
            }
        }
    }

    private void animateView(View itemView){
        Animation fallDownAnimation = AnimationUtils.loadAnimation(context,R.anim.item_animation_fall_down);
        itemView.startAnimation(fallDownAnimation);
    }
}
