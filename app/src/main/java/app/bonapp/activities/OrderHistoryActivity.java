package app.bonapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.bonapp.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.bonapp.adapters.OrderHistoryAdapter;
import app.bonapp.constants.ApiKeys;
import app.bonapp.constants.Constants;
import app.bonapp.interfaces.OnRecyclerViewItemClickListener;
import app.bonapp.models.orderehistory.DATum;
import app.bonapp.models.orderehistory.OrderHistoryModel;
import app.bonapp.network.ApiCall;
import app.bonapp.network.ApiInterface;
import app.bonapp.network.NetworkListener;
import app.bonapp.network.RestApi;
import app.bonapp.utils.AppSharedPrefs;
import app.bonapp.utils.AppUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * This class shows history of all orders for merchants and for customers , rows/views are little change for both the
 * user type.
 */
public class OrderHistoryActivity extends AppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ll_no_orders)
    LinearLayout llNoOrders;
    @BindView(R.id.rv_order_history)
    RecyclerView rvOrderHistory;
    @BindView(R.id.swipe_orders)
    SwipeRefreshLayout swipeOrders;
    @BindView(R.id.tv_not_loaded)
    TextView tvNotLoaded;
    @BindView(R.id.ll_not_loaded)
    LinearLayout llNotLoaded;
    @BindView(R.id.activity_order_history)
    RelativeLayout activityOrderHistory;
    @BindView(R.id.tv_no_orders)
    TextView tvNoOrders;
    private AppUtils appUtils;
    private Activity mActivity;
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading = true;
    private LinearLayoutManager linearLayoutManager;
    private List<DATum> ordersList;
    private OrderHistoryAdapter orderHistoryAdapter;
    private int nextCount = 0;
    private boolean isRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        ButterKnife.bind(this);
        appUtils = AppUtils.getInstance();
        mActivity = OrderHistoryActivity.this;
        setUpViews();
        setupRecyclerView();

        setUpRefreshing();
        if (appUtils.isInternetOn(mActivity)) {
            appUtils.showProgressDialog(mActivity, true);
            hitApiForOrdersListing(String.valueOf(nextCount));
        } else {
            llNotLoaded.setVisibility(View.VISIBLE);
            tvNotLoaded.setText(R.string.internet_offline);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    private void setUpViews() {
        tvTitle.setText(getString(R.string.order_history));
        ivBack.setVisibility(View.VISIBLE);

    }

    /**
     * method to setup recylerviews , also contains the pagination logic
     */
    private void setupRecyclerView() {
        ordersList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(mActivity);
        orderHistoryAdapter = new OrderHistoryAdapter(ordersList, mActivity);
        rvOrderHistory.setLayoutManager(linearLayoutManager);
        rvOrderHistory.setAdapter(orderHistoryAdapter);

        rvOrderHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView,
                                   int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager
                        .findLastVisibleItemPosition();
                if (!loading
                        && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    // End has been reached
                    // Do something
                    if (appUtils.isInternetOn(mActivity)) {
                        hitApiForOrdersListing(String.valueOf(nextCount));
                        loading = true;
                    } else {
                        appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.internet_offline));
                    }
                }
            }
        });

        orderHistoryAdapter.setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onRecyclerViewItemClick(View view, int position) {
                Intent intent = new Intent(mActivity, OrderDetailActivity.class);
                intent.putExtra(Constants.ORDER_ID, ordersList.get(position).getOrderId());
                intent.putExtra(Constants.CUSTOMER_NAME,ordersList.get(position).getCustomerName());
                startActivity(intent);
            }

            @Override
            public void onRecyclerViewItemLongClick(View view, int position) {

            }
        });
    }

    /**
     * set up refreshing on swipe
     */
    private void setUpRefreshing() {
        swipeOrders.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (appUtils.isInternetOn(mActivity)) {
                    //shouldLoadMore = false;
                    nextCount = 0;
                    isRefresh = true;
                    hitApiForOrdersListing(String.valueOf(nextCount));

                } else {
                    if (swipeOrders.isRefreshing())
                        swipeOrders.setRefreshing(false);

                    appUtils.showSnackBar(activityOrderHistory, getString(R.string.internet_offline));

                }

            }
        });
    }

    public void setLoaded() {
        loading = false;
    }


    /**
     * hit api for merchant deals listing
     *
     * @param count
     */
    private void hitApiForOrdersListing(String count) {
        HashMap<String, String> params = new HashMap<>();
        params.put(ApiKeys.COUNT_KEY, count);
        params.put(ApiKeys.ORDER_TYPE, AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.USER_TYPE, ""));

        ApiInterface service = RestApi.createService(ApiInterface.class);
        Call<ResponseBody> call = service.orderHistory(AppSharedPrefs.getInstance(mActivity).
                getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, ""), appUtils.encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                OrderHistoryModel orderHistoryModel = new Gson().fromJson(response, OrderHistoryModel.class);
                if (orderHistoryModel.getCODE() == 200) {
                    if (isRefresh) {
                        ordersList.clear();
                        isRefresh = false;
                    }

                    llNotLoaded.setVisibility(View.GONE);
                    ordersList.addAll(orderHistoryModel.getDATA());
                    nextCount = Integer.parseInt(orderHistoryModel.getNextCount());
                    if (Integer.parseInt(orderHistoryModel.getNextCount()) > 0) {
                        setLoaded();
                    }

                    orderHistoryAdapter.notifyDataSetChanged();

                } else if (orderHistoryModel.getCODE() == 300) {
                    llNotLoaded.setVisibility(View.GONE);
                    llNoOrders.setVisibility(View.VISIBLE);
                    if (AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.USER_TYPE, "1").equals(Constants.MERCHANT_TYPE)) {
                        tvNoOrders.setText(getString(R.string.merchant_order_blank_msg));
                    } else {
                        tvNoOrders.setText(getString(R.string.customer_order_blank_msg));
                    }
                } else if (orderHistoryModel.getCODE() == ApiKeys.UNAUTHORISED_CODE) {
                    appUtils.logoutFromApp(mActivity, orderHistoryModel.getMESSAGE());
                } else {
                    appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), orderHistoryModel.getMESSAGE());
                }
                if (swipeOrders.isRefreshing())
                    swipeOrders.setRefreshing(false);
            }

            @Override
            public void onSuccessErrorBody(String response) {

            }

            @Override
            public void onFailure() {
                if (swipeOrders.isRefreshing())
                    swipeOrders.setRefreshing(false);
                llNotLoaded.setVisibility(View.VISIBLE);
                tvNotLoaded.setText(R.string.not_load_history);
            }
        });

    }


    @OnClick({R.id.iv_back, R.id.btn_retry})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_retry:
                if (appUtils.isInternetOn(mActivity)) {
                    llNotLoaded.setVisibility(View.GONE);
                    nextCount = 0;
                    appUtils.showProgressDialog(mActivity, true);
                    hitApiForOrdersListing(String.valueOf(nextCount));
                } else {
                    llNotLoaded.setVisibility(View.VISIBLE);
                    tvNotLoaded.setText(R.string.internet_offline);
                }
                break;
        }
    }
}
