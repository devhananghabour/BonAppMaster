package app.bonapp.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.transition.TransitionManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.bonapp.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import app.bonapp.adapters.RestaurantDealsListingAdapter;
import app.bonapp.constants.ApiKeys;
import app.bonapp.constants.Constants;
import app.bonapp.fragments.CheckoutFragment;
import app.bonapp.interfaces.DialogButtonClickListener;
import app.bonapp.interfaces.OnRecyclerViewItemClickListener;
import app.bonapp.models.CartItemModel;
import app.bonapp.models.FilterModel;
import app.bonapp.models.merchantposts.Deal;
import app.bonapp.models.restaurantdetal.DATA;
import app.bonapp.models.restaurantdetal.RestaurantDetailModel;
import app.bonapp.models.taxlist.DATum;
import app.bonapp.models.taxlist.TaxListModel;
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

public class RestaurantDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    static final String TAG = RestaurantDetailActivity.class.getName();

    @BindView(R.id.iv_restrau_image)
    ImageView ivRestrauImage;
    @BindView(R.id.iv_call)
    ImageView ivCall;
    @BindView(R.id.tv_restrau_name)
    TextView tvRestrauName;
    @BindView(R.id.tv_restrau_address)
    TextView tvRestrauAddress;
    @BindView(R.id.tv_description)
    TextView tvDescription;
    @BindView(R.id.tv_dine_in_takeaway)
    TextView tvDineInTakeaway;
    @BindView(R.id.rv_deals_listing)
    RecyclerView rvDealsListing;
    @BindView(R.id.tv_total_price)
    TextView tvTotalPrice;
    @BindView(R.id.tv_item_added)
    TextView tvItemAdded;
    @BindView(R.id.rl_cart_shortcut)
    RelativeLayout rlCartShortcut;
    @BindView(R.id.iv_cross)
    ImageView ivCross;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_layout)
    CollapsingToolbarLayout collapsingLayout;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.activity_restr_detail)
    CoordinatorLayout activityRestrDetail;
    @BindView(R.id.fl_frag_container)
    FrameLayout flFragContainer;
    @BindView(R.id.tv_norecord_messsge)
    TextView tvNorecordMesssge;
    @BindView(R.id.btn_retry)
    Button btnRetry;
    @BindView(R.id.cv_no_record)
    CardView cvNoRecord;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.ll_no_record)
    LinearLayout llNoRecord;
    @BindView(R.id.nsv)
    NestedScrollView nsv;
    @BindView(R.id.iv_transparent_image)
    ImageView ivTransparentImage;
    @BindView(R.id.activity_restaurant_llDeliveryConstraints)
    LinearLayout llDeliveryConstraints;
    @BindView(R.id.activity_restaurant_tvMinimumOrder)
    TextView tvMinimumOrder;
    @BindView(R.id.activity_restaurant_tvMinimumOrderValue)
    TextView tvMinimumOrderValue;
    @BindView(R.id.activity_restaurant_tvDeliveryFees)
    TextView tvDeliveryFees;
    @BindView(R.id.activity_restaurant_tvDeliveryFeesValue)
    TextView tvDeliveryFeesValue;
    @BindView(R.id.activity_restaurant_llDeliverAreas)
    LinearLayout llDeliveryAreas;
    @BindView(R.id.activity_restaurant_tvDeliverAreasValue)
    TextView tvDeliveryAreasValue;
    @BindView(R.id.activity_restaurant_tvDeliveryRules)
    TextView tvDeliveryRules;
    @BindView(R.id.activity_restaurant_tvShowMore)
    TextView tvShowMore;

    private int MAP_REQ_CODE = 111;

    private String merchantId;
    private Activity mActivity;
    private AppUtils appUtils;
    public RestaurantDealsListingAdapter restaurantDealsListingAdapter;
    private List<Deal> merchantDealList;
    private String mobileNo;
    private String restrLat, restLong;
    private boolean isFavorite;
    private ArrayList<CartItemModel> checkOutItemList;
    private int selectedDealPos;
    private String titleName = "";
    private List<DATum> taxList;
    private Intent callIntent;
    private String image, name, address;
    private boolean isFromPush;
    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private DATA restaurantDetails;

    public void setCheckOutItemList(ArrayList<CartItemModel> checkOutItemList) {
        this.checkOutItemList = checkOutItemList;
        updateCart();
    }

    public List<DATum> getTaxList() {
        return taxList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);
        ButterKnife.bind(this);
        appUtils = AppUtils.getInstance();
        mActivity = RestaurantDetailActivity.this;

        if (getIntent().getExtras() != null) {
            merchantId = getIntent().getStringExtra(Constants.MERCHANT_ID_KEY);
            selectedDealPos = getIntent().getIntExtra("pos", 0);
            image = getIntent().getStringExtra(Constants.RESTRAU_IMAGE_KEY);
            name = getIntent().getStringExtra(Constants.RESTRAU_NAME_KEY);
            address = getIntent().getStringExtra(Constants.RESTRAU_ADDRESS_KEY);
            isFromPush = getIntent().getBooleanExtra(Constants.IS_FROM_PUSH, false);
            isFavorite = getIntent().getBooleanExtra(Constants.IS_FAVORITE, false);
        }
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentByTag("com.google.android.gms.maps.SupportMapFragment");
        if (mapFragment != null)
            mapFragment.getMapAsync(this);
//       mapView.onCreate(savedInstanceState);
//        mapView.getMapAsync(this);
        handleMapTouch();
        initViews();
        setUpRecyclerView();
//        CoordinatorLayout.LayoutParams layoutParams=new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT);
//        layoutParams.gravity= Gravity.FILL_VERTICAL;
//        nsv.setLayoutParams(layoutParams);

        if (appUtils.isInternetOn(mActivity)) {
            hitRestaurantDetailApi();
            hitTaxListApi();
        } else {
            cvNoRecord.setVisibility(View.VISIBLE);
            tvNorecordMesssge.setText(R.string.internet_offline);
        }

        appUtils.trackScreen(Constants.MERCHANT_DETAIL_SCREEN_NAME, mActivity);
    }

    @Override
    public void onResume() {
//        mapView.onResume();
        super.onResume();
    }

    public void turnOffToolbarScrolling() {


        //turn off scrolling
        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) collapsingLayout.getLayoutParams();
        toolbarLayoutParams.setScrollFlags(0);
        collapsingLayout.setLayoutParams(toolbarLayoutParams);

        CoordinatorLayout.LayoutParams appBarLayoutParams = (CoordinatorLayout.LayoutParams) appbar.getLayoutParams();
        appBarLayoutParams.setBehavior(null);
        appbar.setLayoutParams(appBarLayoutParams);
    }

    public void turnOnToolbarScrolling() {

        //turn on scrolling
        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        toolbarLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        toolbar.setLayoutParams(toolbarLayoutParams);

        CoordinatorLayout.LayoutParams appBarLayoutParams = (CoordinatorLayout.LayoutParams) appbar.getLayoutParams();
        appBarLayoutParams.setBehavior(new AppBarLayout.Behavior());
        appbar.setLayoutParams(appBarLayoutParams);
    }

    private void handleMapTouch() {

        ivTransparentImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        nsv.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        nsv.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        nsv.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });
//        mapView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_MOVE:
//                        nsv.requestDisallowInterceptTouchEvent(true);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                    case MotionEvent.ACTION_CANCEL:
//                        nsv.requestDisallowInterceptTouchEvent(false);
//                        break;
//                }
//                return mapView.onTouchEvent(event);
//            }
//        });
        mapFragment.getView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

   /* private void loadMap(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission
                    (this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    &&
                    ActivityCompat.checkSelfPermission
                            (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, MAP_REQ_CODE); // 111 is requestCode

            }else {

            }
        }else {

        }
    }
*/

    /**
     * method to initialize views
     */
    private void initViews() {
        if (image != null) {
            Glide.with(mActivity).load(image).into(ivRestrauImage);
        }
        if (name != null) {
            tvRestrauName.setText(name);
        }
        if (address != null) {
            tvRestrauAddress.setText(address);
        }

        taxList = new ArrayList<>();
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(mActivity, R.color.colorWhite));
        collapsingLayout.setCollapsedTitleTextColor(ContextCompat.getColor(mActivity, R.color.colorWhite));
        collapsingLayout.setTitleEnabled(false);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        flFragContainer.setPadding(0, getStatusBarHeight(), 0, 0);
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


    /**
     * method to setup recyler view , also contains the items click logic
     */
    private void setUpRecyclerView() {
        checkOutItemList = new ArrayList<>();
        merchantDealList = new ArrayList<>();
        rvDealsListing.setLayoutManager(new LinearLayoutManager(mActivity));
        restaurantDealsListingAdapter = new RestaurantDealsListingAdapter(merchantDealList, mActivity);
        rvDealsListing.setAdapter(restaurantDealsListingAdapter);
        rvDealsListing.setNestedScrollingEnabled(false);

        restaurantDealsListingAdapter.setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onRecyclerViewItemClick(View view, int position) {
                if (merchantDealList.size() > 0) {
                    int leftCount = Integer.parseInt(merchantDealList.get(position).getItemLeft());
                    if (leftCount > 0) {
                        int count = 0, addedItemPos = 0;
                        for (int i = 0; i < checkOutItemList.size(); i++) {
                            if (merchantDealList.get(position).getDealId().equals(checkOutItemList.get(i).getDealId())) {
                                count = 1;
                                addedItemPos = i;
                                break;
                            }
                        }
                        if (count == 0) {
                            CartItemModel cartItemModel = new CartItemModel();
                            cartItemModel.setDealId(merchantDealList.get(position).getDealId());
                            cartItemModel.setItemQuantity(1);
                            cartItemModel.setItemName(merchantDealList.get(position).getDealTitle());
                            cartItemModel.setItemPrice(merchantDealList.get(position).getNewPrice());
                            cartItemModel.setItemLeft(Integer.parseInt(merchantDealList.get(position).getItemLeft()));
                            cartItemModel.setEndTime(merchantDealList.get(position).getEndTime());

                            checkOutItemList.add(cartItemModel);

                        } else {
                            checkOutItemList.get(addedItemPos).setItemQuantity(checkOutItemList.get(addedItemPos).getItemQuantity() + 1);
                        }

                        leftCount -= 1;
                        merchantDealList.get(position).setItemLeft(String.valueOf(leftCount));
                        merchantDealList.get(position).setAddedToCart(true);
                        restaurantDealsListingAdapter.notifyItemChanged(position);
                        rlCartShortcut.setVisibility(View.VISIBLE);
                        updateCart();

                    }
                }
            }

            @Override
            public void onRecyclerViewItemLongClick(View view, int position) {

            }
        });
    }

    /**
     * this method contains the logic of increasing and decreasing the amount
     *
     * @param type
     * @param dealId
     */
    public void increaseDecreaseLeftCount(int type, String dealId) {
        //type 1 for increase 2 for decrease
        if (type == 1) {
            for (int i = 0; i < merchantDealList.size(); i++) {
                if (merchantDealList.get(i).getDealId().equals(dealId)) {
                    int itemLeft = Integer.parseInt(merchantDealList.get(i).getItemLeft());
                    itemLeft += 1;
                    merchantDealList.get(i).setItemLeft(String.valueOf(itemLeft));
                    break;
                }
            }
        } else if (type == 2) {
            for (int i = 0; i < merchantDealList.size(); i++) {
                if (merchantDealList.get(i).getDealId().equals(dealId)) {
                    int itemLeft = Integer.parseInt(merchantDealList.get(i).getItemLeft());
                    itemLeft -= 1;
                    merchantDealList.get(i).setItemLeft(String.valueOf(itemLeft));
                    break;
                }
            }
        }
        restaurantDealsListingAdapter.notifyDataSetChanged();

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

    /**
     * this method contains the logic of updating the cart when an item is removed
     *
     * @param dealId
     */
    public void removedFromCard(String dealId) {
        for (int i = 0; i < merchantDealList.size(); i++) {
            if (merchantDealList.get(i).getDealId().equals(dealId)) {
                merchantDealList.get(i).setAddedToCart(false);
                merchantDealList.get(i).setItemLeft(merchantDealList.get(i).getOriginalQuant());
                break;
            }
        }
        for (int i = 0; i < checkOutItemList.size(); i++) {
            if (checkOutItemList.get(i).getDealId().equals(dealId)) {
                checkOutItemList.remove(i);
                break;
            }
        }
        updateCart();
        restaurantDealsListingAdapter.notifyDataSetChanged();
    }

    /**
     * this method contains the logic of updating the cart.
     */
    private void updateCart() {
        rlCartShortcut.setVisibility(checkOutItemList.size() == 0 ? View.GONE : View.VISIBLE);
        double totalPrice = 0.0;
        for (int i = 0; i < checkOutItemList.size(); i++) {
            double dealPrice = checkOutItemList.get(i).getItemQuantity() * Double.parseDouble(checkOutItemList.get(i).getItemPrice());
            totalPrice += dealPrice;
        }
        String price = String.format(Locale.US, "%.2f", totalPrice) + " " + getString(R.string.currency);
        tvTotalPrice.setText(price);
        tvItemAdded.setText(String.valueOf(checkOutItemList.size()));
    }

    @OnClick({R.id.iv_call, /*R.id.iv_map,*/ R.id.iv_cross, R.id.rl_cart_shortcut, R.id.activity_restaurant_tvShowMore})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_call:
                if (mobileNo != null) {
                    if (callIntent == null) {
                        callIntent = new Intent(Intent.ACTION_DIAL);
                    }
                    callIntent.setData(Uri.parse("tel:" + mobileNo));
                    startActivity(callIntent);
                }
                break;
            /*case R.id.iv_map:
                if (restrLat != null && restLong != null) {
                    String url = "http://maps.google.com/maps?f=d&daddr=" + restrLat + "," + restLong + "&dirflg=d&layer=t";
                    Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent1.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intent1);
                } else {
                    appUtils.showSnackBar(activityRestrDetail, getString(R.string.loc_not_found));
                }
                break;*/
            case R.id.iv_cross:
                onBackPressed();
                break;
            case R.id.rl_cart_shortcut:
                pushCheckoutFragment();
                break;
            case R.id.activity_restaurant_tvShowMore:
                showMore(view);
                break;
        }
    }

    /**
     * method to hit api for getting restaurant details
     */
    private void hitRestaurantDetailApi() {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, String> params = new HashMap<>();
        params.put(ApiKeys.MERCHANT_ID, merchantId);

        ApiInterface service = RestApi.createService(ApiInterface.class);
        Call<ResponseBody> call = service.merchantDetail(Constants.API_KEY, appUtils.encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                RestaurantDetailModel restaurantDetailModel = new Gson().fromJson(response, RestaurantDetailModel.class);
                progressBar.setVisibility(View.GONE);
                if (restaurantDetailModel.getCODE() == 200) {
                    cvNoRecord.setVisibility(View.GONE);
                    setData(restaurantDetailModel.getDATA());

                } else {
                    appUtils.showSnackBar(activityRestrDetail, restaurantDetailModel.getmESSAGE());
                }
                TransitionManager.beginDelayedTransition(activityRestrDetail);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onSuccessErrorBody(String response) {

            }

            @Override
            public void onFailure() {
                TransitionManager.beginDelayedTransition(activityRestrDetail);
                progressBar.setVisibility(View.GONE);
                cvNoRecord.setVisibility(View.VISIBLE);
                tvNorecordMesssge.setText(getString(R.string.txt_something_went_wrong));

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (AppSharedPrefs.getInstance(this).getBoolean(AppSharedPrefs.PREF_KEY.IS_LOGIN, false)) {
            getMenuInflater().inflate(R.menu.restaurant_details_menu, menu);
            changeFavoriteMenuItem(menu.findItem(R.id.restaurant_details_activity_favorite), isFavorite);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.restaurant_details_activity_favorite:
                boolean favorite = !isFavorite;
                addRestaurantsToFavoriteApi(favorite);
                changeFavoriteMenuItem(item, favorite);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void changeFavoriteMenuItem(MenuItem menuItem, boolean isFavorite) {
        if (isFavorite) {
            menuItem.setIcon(R.drawable.ic_favorite_selected);
        } else {
            menuItem.setIcon(R.drawable.ic_favorite);
        }
    }

    private void addRestaurantsToFavoriteApi(boolean isFavorite) {
        this.isFavorite = isFavorite;
        HashMap<String, String> params = new HashMap<>();
        params.put("merchant_id", merchantId);
        ApiInterface service = RestApi.createService(ApiInterface.class);
        Call<ResponseBody> api;
        if (isFavorite) {
            api = service.addFavorite(AppSharedPrefs.getInstance(this).
                    getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, ""), params);
        } else {
            api = service.deleteFavorite(AppSharedPrefs.getInstance(this).
                    getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, ""), params);
        }

        ApiCall.getInstance().hitService(this, api, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
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
     * method to set data
     *
     * @param data
     */
    private void setData(DATA data) {
        this.restaurantDetails = data;
        ivCall.setVisibility(View.VISIBLE);
        Glide.with(mActivity).load(data.getProfilePicture()).into(ivRestrauImage);
        tvRestrauName.setText(data.getName());
        titleName = data.getName();
        //    if (getSupportActionBar() != null)
        //        getSupportActionBar().setTitle(data.getName());
        tvDescription.setText(data.getDescription());
        tvRestrauAddress.setText(data.getAddress());
        mobileNo = data.getMobileCountryCode() + data.getMobileNumber();
        tvDineInTakeaway.setText(data.getSubDescription());

        merchantDealList.addAll(data.getDeals());
        if (merchantDealList.size() <= 1) {
            turnOffToolbarScrolling();
        }/*else {
            turnOnToolbarScrolling();
        }*/

        for (Deal deal : merchantDealList) {
            deal.setOriginalQuant(deal.getItemLeft());
        }
        restaurantDealsListingAdapter.notifyDataSetChanged();
        restrLat = data.getLatitude();
        restLong = data.getLongitude();

        addMarker(restrLat, restLong, data.getName());

        if ((restaurantDetails.getMinOrder() != null && !restaurantDetails.getMinOrder().isEmpty()) || isDeliveryAvailable(restaurantDetails)) {
            tvShowMore.setVisibility(View.VISIBLE);
        }

        /**
         * this code is commented because we replaced the static map approach to actual map
         */

       /* String url = "http://maps.google.com/maps/api/staticmap";

        url += "?zoom=12&size=" + 400 + "x" + 400;

        url += "&maptype=roadmap";

        url += "&markers=color:red|label:" + data.getActiveDealsCount() + "|" + data.getLatitude() + "," + data.getLongitude();

        url += "&sensor=true";

        Glide.with(mActivity)
                .load(url)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        ivMap.setImageBitmap(resource); // Possibly runOnUiThread()
                    }
                });*/
    }

    private String getDeliveryAreasString(ArrayList<FilterModel> deliveryAreas) {
        StringBuilder deliveryAreasString = new StringBuilder();
        for (FilterModel filterModel : deliveryAreas) {
            deliveryAreasString.append("- ")
                    .append(filterModel.getName())
                    .append("\n");
        }
        return deliveryAreasString.toString();
        //return StringUtils.join(deliveryAreas.toArray(), "-\n");
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentById(R.id.fl_frag_container) instanceof CheckoutFragment) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down);
            fragmentTransaction.remove(getSupportFragmentManager().findFragmentById(R.id.fl_frag_container));
            fragmentTransaction.commit();
        } else {
            if (checkOutItemList.size() > 0) {
                showDiscardDialog();
            } else if (isFromPush) {
                startActivity(new Intent(mActivity, HomeActivity.class));
                finish();
            } else
                super.onBackPressed();
        }
    }

    /**
     * method shows a dialog to cancel the order and finish activity
     */
    private void showDiscardDialog() {
        appUtils.showAlertDialog(mActivity, getString(R.string.cancel_ur_order), getString(R.string.items_discards_message), getString(R.string.yes),
                getString(R.string.cancel), new DialogButtonClickListener() {
                    @Override
                    public void positiveButtonClick() {
                        finish();
                    }

                    @Override
                    public void negativeButtonClick() {

                    }
                });
    }


    /*
     *method to push checkout fragment
     */
    private void pushCheckoutFragment() {
        Fragment fragment = CheckoutFragment.newInstance(checkOutItemList, merchantId, restaurantDetails.getDeliveryFees(), restaurantDetails.getMinOrder(), isDeliveryAvailable(restaurantDetails), restaurantDetails.getDeliveryAreas());
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down);
        fragmentTransaction.add(R.id.fl_frag_container, fragment);
        fragmentTransaction.commit();
    }

    private boolean isDeliveryAvailable(DATA data) {
        return data.getDeliveryAreas() != null && !data.getDeliveryAreas().isEmpty();
    }

    /**
     * method to hit api for tax listing
     */
    private void hitTaxListApi() {
        ApiInterface service = RestApi.createService(ApiInterface.class);
        Call<ResponseBody> call = service.getTaxList(Constants.API_KEY);
        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                TaxListModel taxListModel = new Gson().fromJson(response, TaxListModel.class);
                if (taxListModel.getCODE() == 200) {
                    taxList.addAll(taxListModel.getDATA());
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

    @OnClick(R.id.btn_retry)
    public void onViewClicked() {
        cvNoRecord.setVisibility(View.GONE);
        if (appUtils.isInternetOn(mActivity)) {
            hitRestaurantDetailApi();
            hitTaxListApi();
        } else {
            cvNoRecord.setVisibility(View.VISIBLE);
            tvNorecordMesssge.setText(R.string.internet_offline);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        /**
         * This is used to get map permission form user,
         * which is mandatory for map
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission();
            } else {
                //granted
                googleMap.setMyLocationEnabled(false);
            }
        } else {
            //below M permission not req
            googleMap.setMyLocationEnabled(false);

        }
    }


    /**
     * This method is used to get the Location permission for the user
     */
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(mActivity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                MAP_REQ_CODE
        );
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MAP_REQ_CODE) {
            if (permissions.length > 0 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //granted
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap.setMyLocationEnabled(false);


            } else if (!ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permissions[0]) ||
                    !ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permissions[1])) {//has selected do not allow allow
                appUtils.showAllowPermissionFromSettingDialog(mActivity);
            } else {
                requestLocationPermission();
            }
        }
    }

    /**
     * this method is used to show marker
     *
     * @param latitude
     * @param longitude
     * @param restrauName
     */
    private void addMarker(String latitude, String longitude, String restrauName) throws NumberFormatException {
        double restLat = Double.parseDouble(latitude);
        double restLong = Double.parseDouble(longitude);

        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(restLat, restLong)).title(restrauName);

        googleMap.addMarker(marker);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(restLat, restLong)));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(18f));

    }

    private void showMore(View showMoreView) {
        showMoreView.setVisibility(View.GONE);
        llDeliveryConstraints.setVisibility(View.VISIBLE);
        Double minOrder = 0.0;
        try {
            minOrder = Double.parseDouble(restaurantDetails.getMinOrder());
        } catch (Exception ex) {
            Log.w(TAG, ex.getMessage());
        }

        if (minOrder > 0) {
            tvMinimumOrderValue.setText(restaurantDetails.getMinOrder());
            tvMinimumOrderValue.append(" AED");
        }
        tvMinimumOrderValue.setVisibility(View.VISIBLE);
        tvMinimumOrder.setVisibility(View.VISIBLE);

        if (restaurantDetails.getDeliveryFees() != null && !restaurantDetails.getDeliveryFees().isEmpty()) {
            tvDeliveryFeesValue.setVisibility(View.VISIBLE);
            tvDeliveryFees.setVisibility(View.VISIBLE);
            tvDeliveryFeesValue.setText(restaurantDetails.getDeliveryFees());
        }
        if (isDeliveryAvailable(restaurantDetails)) {
            llDeliveryAreas.setVisibility(View.VISIBLE);
            tvDeliveryAreasValue.setText(getDeliveryAreasString(restaurantDetails.getDeliveryAreas()));
            tvDeliveryRules.setText(restaurantDetails.getDeliveryRules());
            tvDeliveryRules.setVisibility(View.VISIBLE);
        }
    }

}
