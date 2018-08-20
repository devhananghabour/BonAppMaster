package app.bonapp.activities;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Color;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.bonapp.R;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import app.bonapp.adapters.OrderDetailDealsAdapter;
import app.bonapp.constants.ApiKeys;
import app.bonapp.constants.Constants;
import app.bonapp.interfaces.DialogButtonClickListener;
import app.bonapp.models.DeliveryType;
import app.bonapp.models.ListData;
import app.bonapp.models.OrderStatus;
import app.bonapp.models.orderdetail.DATA;
import app.bonapp.models.orderdetail.OrderDetailModel;
import app.bonapp.models.orderdetail.Orderdatum;
import app.bonapp.network.ApiCall;
import app.bonapp.network.ApiInterface;
import app.bonapp.network.NetworkListener;
import app.bonapp.network.RestApi;
import app.bonapp.serivces.SoundService;
import app.bonapp.utils.AppSharedPrefs;
import app.bonapp.utils.AppUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrderDetailActivity extends AppCompatActivity {

    static public String REFRESH_ORDER_STATUS = "refresh_order_status";

    @BindView(R.id.iv_restrau_image)
    ImageView ivRestrauImage;
    @BindView(R.id.iv_call)
    ImageView ivCall;
    @BindView(R.id.iv_location)
    ImageView ivLocation;
    @BindView(R.id.tv_restrau_name)
    TextView tvRestrauName;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_layout)
    CollapsingToolbarLayout collapsingLayout;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.rv_orders_listing)
    RecyclerView rvOrdersListing;
    @BindView(R.id.activity_restr_detail)
    CoordinatorLayout activityRestrDetail;
    @BindView(R.id.tv_order_code)
    TextView tvOrderCode;
    @BindView(R.id.tv_total_price)
    TextView tvTotalPrice;

    @BindView(R.id.tv_pickup_time)
    TextView tvPickupTime;
    @BindView(R.id.activity_order_detail_rgDeliveryStatus)
    LinearLayout rgDeliveryStatus;
    @BindView(R.id.activity_order_detail_rbOrderPlaced)
    Button rbOrderPlaced;
    @BindView(R.id.activity_order_detail_rbOrderAccepted)
    Button rbOrderAccepted;
    @BindView(R.id.activity_order_detail_rbOrderOnTheWay)
    Button rbOrderOnTheWay;
    @BindView(R.id.activity_order_detail_rbOrderDelivered)
    Button rbOrderDelivered;
    @BindView(R.id.activity_order_detail_tvAddressAndContactInfo)
    TextView tvAddressAndContactInfo;
    @BindView(R.id.activity_order_detail_tvCustomerName)
    TextView tvCustomerName;
    @BindView(R.id.activity_order_detail_llCustomerDetails)
    LinearLayout llCustomerDetails;
    @BindView(R.id.activity_order_details_tvDeliveryFees)
    TextView tvDeliveryFees;

    @BindView(R.id.cultryValue)
    TextView cultryValue;



    private Activity mActivity;
    private AppUtils appUtils;
    private String titleName = "";
    private String orderId;
    private DATA orderDetailData;
    private String restrLat, restLong;
    private String mobileNo;
    private List<ListData> ordersList;
    private OrderDetailDealsAdapter orderDetailDealsAdapter;
    private boolean isFromNotification = false;
    private String shouldRate;
    private String customerName = "";
    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        appUtils = AppUtils.getInstance();
        mActivity = this;
        initViews();
        stopService(new Intent(this, SoundService.class));
        if (getIntent().getExtras() != null) {
            orderId = getIntent().getStringExtra(Constants.ORDER_ID);
            isFromNotification = getIntent().getBooleanExtra(Constants.IS_FROM_NOTIFICATION, false);
            shouldRate = getIntent().getStringExtra(Constants.SHOULD_RATE_KEY);
            customerName = getIntent().getStringExtra(Constants.CUSTOMER_NAME);
        }
        setUpRecyclerView();
        if (shouldRate != null && shouldRate.equals("1")) {
            appUtils.showAlertDialog(mActivity, getString(R.string.rate_bonapp), getString(R.string.rating_popup_msg),
                    getString(R.string.yes), getString(R.string.remind_me_later), new DialogButtonClickListener() {
                        @Override
                        public void positiveButtonClick() {
                            Intent intent = new Intent(mActivity, RatingActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void negativeButtonClick() {

                        }
                    });
        }

        if (appUtils.isInternetOn(mActivity)) {
            hitApiForOrderDetail();
        } else {
            appUtils.showSnackBar(activityRestrDetail, getString(R.string.internet_offline));
        }
        localBroadcastManager = LocalBroadcastManager.getInstance(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        localBroadcastManager.registerReceiver(refreshOrderStatus, new IntentFilter(REFRESH_ORDER_STATUS));
    }

    @Override
    protected void onPause() {
        super.onPause();
        localBroadcastManager.unregisterReceiver(refreshOrderStatus);
    }

    /**
     * this method animates the text with different colors
     */
    public void animateIt() {
        ObjectAnimator a = ObjectAnimator.ofInt(tvOrderCode, "textColor",
                ContextCompat.getColor(mActivity, R.color.colorPrimary), ContextCompat.getColor(mActivity, R.color.colorAccent), ContextCompat.getColor(mActivity, R.color.yellow),
                ContextCompat.getColor(mActivity, R.color.skyblue));
        a.setInterpolator(new LinearInterpolator());
        a.setDuration(2000);
        a.setRepeatCount(ValueAnimator.INFINITE);
        a.setRepeatMode(ValueAnimator.REVERSE);
        a.setEvaluator(new ArgbEvaluator());
        AnimatorSet t = new AnimatorSet();
        t.play(a);
        t.start();
    }


    /**
     * this method shows the text with mixed different colors
     */
    private void withoutAnimationColor() {
        Bitmap bitmap
                = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.group_2);
        Shader shader = new BitmapShader(
                bitmap,
                Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP);
        tvOrderCode.getPaint().setShader(shader);

    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    /**
     * method to setup views
     */
    private void initViews() {
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(mActivity, R.color.colorWhite));
        collapsingLayout.setCollapsedTitleTextColor(ContextCompat.getColor(mActivity, R.color.colorWhite));
        collapsingLayout.setTitleEnabled(false);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                    getSupportActionBar().setTitle(titleName);
                } else {
                    getSupportActionBar().setTitle(" ");
                }
            }
        });
    }

    // A method to find height of the status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @OnClick({R.id.iv_call, R.id.iv_location, R.id.activity_order_detail_rbOrderPlaced, R.id.activity_order_detail_rbOrderAccepted, R.id.activity_order_detail_rbOrderOnTheWay, R.id.activity_order_detail_rbOrderDelivered})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_call:
                if (mobileNo != null) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + mobileNo));
                    startActivity(intent);
                }
                break;
            case R.id.iv_location:
                if (restrLat != null && restLong != null) {
                    String url = "http://maps.google.com/maps?f=d&daddr=" + restrLat + "," + restLong + "&dirflg=d&layer=t";
                    Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent1.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intent1);
                } else {
                    appUtils.showSnackBar(activityRestrDetail, getString(R.string.loc_not_found));
                }
                break;
            case R.id.activity_order_detail_rbOrderPlaced:
                if (!isDeliveryStatusAlreadySelected(rgDeliveryStatus, view))
                    showChangeOrderStatusConfirmationDialog(view, OrderStatus.OrderStatuses.ORDER_PLACED.getValue());
                break;
            case R.id.activity_order_detail_rbOrderAccepted:
                if (!isDeliveryStatusAlreadySelected(rgDeliveryStatus, view))
                    showChangeOrderStatusConfirmationDialog(view, OrderStatus.OrderStatuses.ORDER_ACCEPTED.getValue());
                break;
            case R.id.activity_order_detail_rbOrderOnTheWay:
                if (!isDeliveryStatusAlreadySelected(rgDeliveryStatus, view))
                    showChangeOrderStatusConfirmationDialog(view, OrderStatus.OrderStatuses.ORDER_ON_THE_WAY.getValue());
                break;
            case R.id.activity_order_detail_rbOrderDelivered:
                if (!isDeliveryStatusAlreadySelected(rgDeliveryStatus, view))
                    showChangeOrderStatusConfirmationDialog(view, OrderStatus.OrderStatuses.ORDER_DELIVERED.getValue());
                break;
        }
    }

    private void showChangeOrderStatusConfirmationDialog(final View view, final String statusId) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getString(R.string.confirmation));
        dialog.setMessage(getString(R.string.change_delivery_status_confirmation_message));
        dialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearAllDeliveryStatusSelections(rgDeliveryStatus);
                view.setSelected(true);
                hitApiChangeDeliveryStatus(statusId);
                dialog.dismiss();
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        if (!orderDetailData.getOrderdata().isEmpty() && isMerchant(orderDetailData.getOrderdata().get(0).getVendorId()))
            dialog.show();
    }

    private boolean isMerchant(String vendorId) {
        final String userId = AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.USER_ID, "");
        return vendorId.equals(userId);

    }

    private boolean isDeliveryStatusAlreadySelected(ViewGroup viewGroup, View view) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i).getId() == view.getId() && viewGroup.getChildAt(i).isSelected()) {
                return true;
            }
        }
        return false;
    }

    private void clearAllDeliveryStatusSelections(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            viewGroup.getChildAt(i).setSelected(false);
        }
    }


    private void hitApiChangeDeliveryStatus(String statusId) {
        final String accessToken = AppSharedPrefs.getInstance(this).
                getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, null);
        ApiInterface service = RestApi.createService(ApiInterface.class);
        HashMap<String, String> params = new HashMap<>();
        params.put(ApiKeys.ORDER_ID, orderId);
        params.put(ApiKeys.STATUS, statusId);

        Call<ResponseBody> call = service.changeDeliveryStatus(AppSharedPrefs.getInstance(mActivity).
                getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, ""), accessToken, params);
        ApiCall.getInstance().hitService(this, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response) {

            }

            @Override
            public void onSuccessErrorBody(String response) {
                Toast.makeText(OrderDetailActivity.this, response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure() {

            }
        });
    }

    /*  */

    /**
     * method to setup recyler view
     */
    private void setUpRecyclerView() {
        ordersList = new ArrayList<>();
        rvOrdersListing.setLayoutManager(new LinearLayoutManager(mActivity));
        orderDetailDealsAdapter = new OrderDetailDealsAdapter(ordersList, mActivity);
        rvOrdersListing.setAdapter(orderDetailDealsAdapter);
        rvOrdersListing.setNestedScrollingEnabled(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * method to hit api for order detail
     */
    private void hitApiForOrderDetail() {
        appUtils.showProgressDialog(mActivity, true);
        ApiInterface service = RestApi.createService(ApiInterface.class);
        HashMap<String, String> params = new HashMap<>();
        params.put(ApiKeys.ORDER_ID, orderId);
        params.put(ApiKeys.ORDER_TYPE, AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.USER_TYPE, "1"));

        Call<ResponseBody> call = service.orderDetail(AppSharedPrefs.getInstance(mActivity).
                getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, ""), appUtils.encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                OrderDetailModel orderDetailModel = new Gson().fromJson(response, OrderDetailModel.class);
                if (orderDetailModel.getCODE() == 200) {
                    orderDetailData = orderDetailModel.getDATA();
                    if (!mActivity.isFinishing())
                        setData();

                } else if (orderDetailModel.getCODE() == ApiKeys.UNAUTHORISED_CODE) {
                    appUtils.logoutFromApp(mActivity, orderDetailModel.getMESSAGE());
                } else {
                    appUtils.showSnackBar(activityRestrDetail, orderDetailModel.getMESSAGE());
                }
            }

            @Override
            public void onSuccessErrorBody(String response) {

            }

            @Override
            public void onFailure() {

            }
        });

    }

    /**
     * this method contains the logic of setting data
     */
    private void setData() {
        if (orderDetailData != null) {
            ordersList.clear();
            if (!orderDetailData.getDealData().isEmpty()) {
                String endTime = orderDetailData.getDealData().get(0).getEndTime();
                if (AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.USER_TYPE, "1").equals(Constants.CUSTOMER_TYPE)) {
                    if (orderDetailData.getOrderdata().get(0).getDeliveryType().equals(DeliveryType.DELIVERY.getValue())) {
                        tvPickupTime.setText(getString(R.string.show_this_to_driver));
                    } else {
                        tvPickupTime.setText(getString(R.string.show_this_to_staff));
                        tvPickupTime.append(" " + appUtils.parseToTimeAndDate(endTime));
                    }
                } else if (AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.USER_ID, "").
                        equals(orderDetailData.getOrderdata().get(0).getCustomerId())) {
                    if (orderDetailData.getOrderdata().get(0).getDeliveryType().equals(DeliveryType.DELIVERY.getValue())) {
                        tvPickupTime.setText(getString(R.string.show_this_to_driver));
                    } else {
                        tvPickupTime.setText(getString(R.string.show_this_to_staff));
                        tvPickupTime.append(" " + appUtils.parseToTimeAndDate(endTime));
                    }
                } else {
                    if (orderDetailData.getOrderdata().get(0).getDeliveryType().equals(DeliveryType.PICKUP_DINE_IN.getValue())) {
                        tvPickupTime.setText(R.string.will_be_picked);
                        tvPickupTime.append(" " + customerName);
                        tvPickupTime.append(" " + getString(R.string.before) + " " + appUtils.parseToTimeAndDate(endTime));
                    }

                }
                if (!orderDetailData.getOrderdata().isEmpty()) {
                    if (!appUtils.isBeforeCurrentTime(endTime) && appUtils.isToday(appUtils.milliseconds(orderDetailData.getOrderdata().get(0).getOrderedOn()))) {
                        animateIt();
                    } else {

                        withoutAnimationColor();
                    }
                }
            }

            if (!orderDetailData.getOrderdata().isEmpty()) {
                titleName = orderDetailData.getOrderdata().get(0).getName();
                Glide.with(mActivity).load(orderDetailData.getOrderdata().get(0).getProfile_picture()).into(ivRestrauImage);
                tvRestrauName.setText(orderDetailData.getOrderdata().get(0).getName());
                restrLat = orderDetailData.getOrderdata().get(0).getLatitude();
                restLong = orderDetailData.getOrderdata().get(0).getLongitude();
                mobileNo = orderDetailData.getOrderdata().get(0).getMobileNumber();
                DecimalFormat decimalFormat = new DecimalFormat("#.00");
                String price =  decimalFormat.format(Double.parseDouble(orderDetailData.getOrderdata().get(0).getSubTotal()))+ " " + getString(R.string.currency);
                String deliveryFees = decimalFormat.format(Double.parseDouble(orderDetailData.getOrderdata().get(0).getDeliveryCharges())) + " " + getString(R.string.currency);
                tvTotalPrice.setText(price);
                tvDeliveryFees.setText(deliveryFees);
                tvOrderCode.setText(orderDetailData.getOrderdata().get(0).getOrderCode());
                setupDeliveryStatusView(orderDetailData.getOrderdata().get(0));
            }
            for (int i = 0; i < orderDetailData.getDealData().size(); i++) {
                ListData listData = new ListData();
                listData.setViewType(1);
                listData.setName(orderDetailData.getDealData().get(i).getItemName());
                listData.setQuantity(orderDetailData.getDealData().get(i).getItemQuantity());
                listData.setPrice(orderDetailData.getDealData().get(i).getPrice());

                ordersList.add(listData);
            }
            for (int i = 0; i < orderDetailData.getTaxData().size(); i++) {
                ListData listData = new ListData();
                listData.setViewType(2);
                double taxPercent = 0.0;
                try {
                    taxPercent = Double.parseDouble(orderDetailData.getTaxData().get(i).getTaxPercent());
                } catch (NumberFormatException ne) {
                    ne.printStackTrace();
                }
                listData.setName(orderDetailData.getTaxData().get(i).getTaxName() + " (" + String.format(Locale.US, "%.0f", taxPercent) + "%)");
                listData.setPrice(orderDetailData.getTaxData().get(i).getTaxAmount());

                ordersList.add(listData);
            }
            setupCustomerDetails(orderDetailData.getOrderdata().get(0));
            orderDetailDealsAdapter.notifyDataSetChanged();

        }
    }

    private void resetDeliveryStatusViews(){
        rbOrderPlaced.setSelected(false);
        rbOrderAccepted.setSelected(false);
        rbOrderOnTheWay.setSelected(false);
        rbOrderDelivered.setSelected(false);
    }

    private void setupDeliveryStatusView(Orderdatum order) {
        resetDeliveryStatusViews();
        if (order.getDeliveryType().equals(DeliveryType.DELIVERY.getValue())) {
            rgDeliveryStatus.setVisibility(View.VISIBLE);
            if (order.getOrderStatus().equals(OrderStatus.OrderStatuses.ORDER_PLACED.getValue())) {
                rbOrderPlaced.setSelected(true);
            } else if (order.getOrderStatus().equals(OrderStatus.OrderStatuses.ORDER_ACCEPTED.getValue())) {
                rbOrderAccepted.setSelected(true);
            } else if (order.getOrderStatus().equals(OrderStatus.OrderStatuses.ORDER_ON_THE_WAY.getValue())) {
                rbOrderOnTheWay.setSelected(true);
            } else if (order.getOrderStatus().equals(OrderStatus.OrderStatuses.ORDER_DELIVERED.getValue())) {
                rbOrderDelivered.setSelected(true);
            }


            if (orderDetailData.getOrderdata().get(0).getCutlery().equals("0")) {
                cultryValue.setText("Not requested");
            }else if (orderDetailData.getOrderdata().get(0).getCutlery().equals("1"))
            {
                cultryValue.setText("Requested");
                cultryValue.setTextColor(Color.parseColor("#E6A40B"));
            }else if (orderDetailData.getOrderdata().get(0).getCutlery().equals("null")||
                    orderDetailData.getOrderdata().get(0).getCutlery()== null)
            {
                cultryValue.setText("Not Requested");
                cultryValue.setTextColor(Color.parseColor("#045461"));

            }


        }
    }

    private void setupCustomerDetails(Orderdatum order) {
        if (isMerchant(order.getVendorId())) {
            llCustomerDetails.setVisibility(View.VISIBLE);
            tvAddressAndContactInfo.setText(orderDetailData.getOrderdata().get(0).getAddress());
            tvCustomerName.setText(orderDetailData.getOrderdata().get(0).getCustomerName());

        }

    }

    @Override
    public void onBackPressed() {
        if (isFromNotification) {
            Intent intent = new Intent(mActivity, HomeActivity.class);
            startActivity(intent);
            finish();
        } else
            super.onBackPressed();

    }

    private BroadcastReceiver refreshOrderStatus = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                if (appUtils.isInternetOn(mActivity)) {
                    hitApiForOrderDetail();
                } else {
                    appUtils.showSnackBar(activityRestrDetail, getString(R.string.internet_offline));
                }
            }
    };

}
