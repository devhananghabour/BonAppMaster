package app.bonapp.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.bonapp.R;
import com.bumptech.glide.Glide;

import java.util.List;

import app.bonapp.interfaces.OnRecyclerViewItemClickListener;
import app.bonapp.models.merchantposts.DATum;
import app.bonapp.utils.AppSharedPrefs;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin1 on 5/10/17.
 */

public class HomePostsAdapter extends RecyclerView.Adapter<HomePostsAdapter.HomePostViewHolder> {

    private List<DATum>                                      merchantPostsList;
    private Context                                          context;
//    private OnRecyclerViewItemClickListener                  onRecyclerViewItemClickListener;
    private RowRestrauItemsAdapter.OnRestaurantClickListener onRestaurantClickListener;
    private OnHomePostItemClickedListener onHomePostItemClickedListener;


    public HomePostsAdapter(List<DATum> merchantPostsList, Context context, RowRestrauItemsAdapter.OnRestaurantClickListener listener, OnHomePostItemClickedListener onHomePostItemClickedListener) {
        this.merchantPostsList = merchantPostsList;
        this.context = context;
        this.onRestaurantClickListener = listener;
        this.onHomePostItemClickedListener = onHomePostItemClickedListener;
    }

//    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
//        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
//    }

    @Override
    public HomePostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_home_post, parent, false);
        return new HomePostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HomePostViewHolder holder, int position) {
        if (holder != null) {
            if (merchantPostsList.get(position).getDeals() != null) {
                RowRestrauItemsAdapter rowRestrauItemsAdapter = new RowRestrauItemsAdapter(context, merchantPostsList.get(position).getDeals(),
                                                                                           merchantPostsList.get(position).getId(), position, onRestaurantClickListener);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                SnapHelper snapHelper = new PagerSnapHelper();
                holder.rvItems.setLayoutManager(linearLayoutManager);
                holder.rvItems.setOnFlingListener(null);
                snapHelper.attachToRecyclerView(holder.rvItems);
                holder.rvItems.setAdapter(rowRestrauItemsAdapter);
                holder.rvItems.setNestedScrollingEnabled(false);
            }

            if (AppSharedPrefs.getInstance(holder.itemView.getContext()).getBoolean(AppSharedPrefs.PREF_KEY.IS_LOGIN, false)) {
                holder.ivFavorite.setVisibility(View.VISIBLE);
                if (merchantPostsList.get(position).isFavorite()) {
                    holder.ivFavorite.setImageResource(R.drawable.ic_favorite_selected);
                } else {
                    holder.ivFavorite.setImageResource(R.drawable.ic_favorite);
                }
            } else {
                holder.ivFavorite.setVisibility(View.GONE);
            }

            float distance = Float.parseFloat(merchantPostsList.get(position).getDistanceKm());
            if (distance < 9999) {
                String distanceText = String.format(" %.2f km away ", distance);
                holder.tvRestrauDistance.setText(distanceText);
            } else {
                holder.tvRestrauDistance.setVisibility(View.GONE);
            }
            holder.tvRestrauName.setText(merchantPostsList.get(position).getName());
            holder.tvRestrauAddress.setText(merchantPostsList.get(position).getAddress());
            Glide.with(context).load(merchantPostsList.get(position).getProfilePicture())
                    .into(holder.ivRestrauImage);
            if (merchantPostsList.get(position).getDeals() != null) {
                holder.viewFaded.setVisibility(merchantPostsList.get(position).getDeals().isEmpty() ? View.VISIBLE : View.GONE);
            } else {
                holder.viewFaded.setVisibility(View.GONE);
            }

            if(merchantPostsList.get(position).isAllowDelivery()){
                holder.ivDelivery.setVisibility(View.VISIBLE);
            }else{
                holder.ivDelivery.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return merchantPostsList.size();
    }

    public interface OnHomePostItemClickedListener{
        void onHomeItemClicked(DATum daTum);
        void onFavoriteItemClicked(DATum daTum);
    }

    class HomePostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.row_home_iv_favorite)
        ImageView    ivFavorite;
        @BindView(R.id.iv_restrau_image)
        ImageView    ivRestrauImage;
        @BindView(R.id.tv_restrau_name)
        TextView     tvRestrauName;
        @BindView(R.id.tv_restrau_address)
        TextView     tvRestrauAddress;
        @BindView(R.id.rv_items)
        RecyclerView rvItems;
        @BindView(R.id.view_faded)
        View         viewFaded;
        @BindView(R.id.fl_overlay)
        FrameLayout  flOverlay;
        @BindView(R.id.tv_restrau_distance)
        TextView     tvRestrauDistance;
        @BindView(R.id.row_home_post_ivDelivery)
        ImageView ivDelivery;


        public HomePostViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            ivFavorite.setOnClickListener(this);
            //rvItems.setOnTouchListener(this);
        }

        @Override
        public void onClick(View view) {
//            if (onHomePostItemClickedListener != null) {
//                onRecyclerViewItemClickListener.onRecyclerViewItemClick(view, getAdapterPosition());
//            }
            switch (view.getId()){
                case R.id.row_home_post_fl_root:
                    onHomePostItemClickedListener.onHomeItemClicked(merchantPostsList.get(getAdapterPosition()));
                    break;
                case R.id.row_home_iv_favorite:
                    onHomePostItemClickedListener.onFavoriteItemClicked(merchantPostsList.get(getAdapterPosition()));
                    break;
            }
        }

    }

}
