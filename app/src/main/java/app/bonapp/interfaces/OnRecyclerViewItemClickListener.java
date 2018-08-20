package app.bonapp.interfaces;

import android.view.View;

/**
 * Created by admin1 on 11/10/17.
 */

public interface OnRecyclerViewItemClickListener {

    void onRecyclerViewItemClick(View view,int position);

    void onRecyclerViewItemLongClick(View view,int position);
}
