package app.bonapp.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.bonapp.R;

/**
 * Created by admin1 on 16/10/17.
 */

public class TutorialPagerAdapter extends PagerAdapter {

    private LayoutInflater layoutInflater;
    private Context context;

    public TutorialPagerAdapter(Context context) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = layoutInflater.inflate(R.layout.tutorial_page, container, false);

        final TextView tvHead = (TextView) itemView.findViewById(R.id.tv_head);
        final TextView tvSubHead = (TextView) itemView.findViewById(R.id.tv_sub_head);
        switch (position){
            case 0:
                tvHead.setText(context.getString(R.string.page_one_head));
                tvSubHead.setText(context.getString(R.string.page_one_sub_head));
                break;
            case 1:
                tvHead.setText(context.getString(R.string.page_two_head));
                tvSubHead.setText(context.getString(R.string.page_two_sub_head));

                break;
            case 2:
                tvHead.setText(context.getString(R.string.page_three_head));
                tvSubHead.setText(context.getString(R.string.page_three_sub_head));
                break;
        }

        ((ViewGroup) container).addView(itemView);
        return itemView;
    }

}
