package app.bonapp.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.bonapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.bonapp.activities.RestaurantDetailActivity;
import app.bonapp.adapters.FilterAdapter;
import app.bonapp.adapters.HomePostsAdapter;
import app.bonapp.adapters.RowRestrauItemsAdapter;
import app.bonapp.constants.ApiKeys;
import app.bonapp.constants.Constants;
import app.bonapp.models.ApiModel;
import app.bonapp.models.FilterModel;
import app.bonapp.models.merchantposts.DATum;
import app.bonapp.models.merchantposts.MerchantPostsModel;
import app.bonapp.network.ApiCall;
import app.bonapp.network.ApiInterface;
import app.bonapp.network.NetworkListener;
import app.bonapp.network.RestApi;
import app.bonapp.utils.AppSharedPrefs;
import app.bonapp.utils.CustomDATumDeserializer;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscoverFragment extends BaseFragment implements RowRestrauItemsAdapter.OnRestaurantClickListener,
                                                              HomePostsAdapter.OnHomePostItemClickedListener,
                                                              GoogleApiClient.ConnectionCallbacks,
                                                              GoogleApiClient.OnConnectionFailedListener,
                                                              LocationListener,
                                                              NestedScrollView.OnScrollChangeListener {

    private static final String TAG = DiscoverFragment.class.getName();

    @BindView(R.id.fragment_discover_ll_filter)            LinearLayout       llFilter;
    @BindView(R.id.fragment_discover_rl_areas)             RecyclerView       rvAreas;
    @BindView(R.id.fragment_discover_rl_types)             RecyclerView       rvTypes;
    @BindView(R.id.fragment_discover_rl_delivery_types)    RecyclerView       rvDeliveryTypes;
    @BindView(R.id.fragment_discover_recycler_view_posts)  RecyclerView       rvPosts;
    @BindView(R.id.fragment_discover_swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.fragment_discover_cv_no_record)         CardView           cvNoRecord;
    @BindView(R.id.fragment_discover_tv_no_record_message) TextView           tvNoRecordMessage;
    @BindView(R.id.fragment_discover_nested_scrollView)    NestedScrollView   nestedScrollView;
    @BindView(R.id.fragment_discover_ll_selected_criteria) LinearLayout       llSelectedFilters;

    private HomePostsAdapter homePostsAdapter;
    private List<DATum> merchantPostsList = new ArrayList<>();

    private FilterAdapter filterAreaAdapter;
    private FilterAdapter filterTypeAdapter;
    private FilterAdapter filterDeliveryTypeAdapter;

    private List<FilterModel> filterAreaList         = new ArrayList<>();
    private List<FilterModel> filterTypeList         = new ArrayList<>();
    private List<FilterModel> filterDeliveryTypeList = new ArrayList<>();

    private FilterModel selectedFilterArea;
    private FilterModel selectedFilterType;
    private FilterModel selectedFilterDeliveryType;

    private int nextCount = 0;
    private double currentLatitude;
    private double currentLongitude;
    private boolean isFirstTime = true;
    private boolean isRefresh   = false;

    private BroadcastReceiver refreshReceiver;
    private              int    CONNECTION_FAILURE_RESOLUTION_REQUEST   = 1;
    private static final String LOG_TAG                                 = "More Info Fragment";
    private static final int    REQUEST_CHECK_SETTINGS                  = 0x1;
    private static final long   UPDATE_INTERVAL_IN_MILLISECONDS         = 10 * 1000;//10 seconds
    private static final long   FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5 * 1000;//10 seconds
    private static final int    LOCATION_PERMISSION_REQ_CODE            = 1001;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    final Rect scrollBounds = new Rect();

    public static DiscoverFragment newInstance() {
        return new DiscoverFragment();
    }

    @Override
    protected int getResourceLayoutId() {
        return R.layout.fragment_discover;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    private BroadcastReceiver getRefreshReceiver(final Activity activity) {
        if (refreshReceiver == null) {
            refreshReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    boolean shouldRefresh = intent.getBooleanExtra("refresh", false);
                    if (shouldRefresh && appUtils.isInternetOn(activity)) {
                        //shouldLoadMore = false;
                        nextCount = 0;
                        isRefresh = true;
                        hitApiForMerchantPostsList(String.valueOf(nextCount), false);

                    }
                }
            };
        }
        return refreshReceiver;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (appUtils.isInternetOn(getActivity())) {
            getLocationPermission();
        } else {
            cvNoRecord.setVisibility(View.VISIBLE);
            tvNoRecordMessage.setText(R.string.internet_offline);
        }

        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(getRefreshReceiver(getActivity()),
                                                                              new IntentFilter(Constants.REFRESH_RECCEIVER_KEY));
        }
        nestedScrollView.getHitRect(scrollBounds);
        appUtils.trackScreen(Constants.MERCHANT_LIST_SCREEN_NAME, getActivity());
        nestedScrollView.setOnScrollChangeListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupRecyclerView();
        setupFilterView();
        getFiltersFromApi();
        setUpRefreshing();
//        if (merchantPostsList.isEmpty()) {
//            hitApiForMerchantPostsList(String.valueOf(nextCount), true);
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(getRefreshReceiver(getActivity()));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
        // stop GoogleApiClient
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();

        }
    }

    private void setupRecyclerView() {
        homePostsAdapter = new HomePostsAdapter(merchantPostsList, getActivity(), this, this);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvPosts.setLayoutManager(linearLayoutManager);
        rvPosts.setAdapter(homePostsAdapter);
        rvPosts.setNestedScrollingEnabled(false);

    }

    private void setupFilterView() {
        filterAreaAdapter = new FilterAdapter(filterAreaList, new FilterAdapter.OnFilterItemListener() {
            @Override
            public void onItemClicked(FilterModel filterModel) {
                isRefresh = true;
                selectedFilterArea = filterModel;
                buildSelectedFilterView();
                hitApiForMerchantPostsList("0", false);
            }
        });

        rvAreas.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayout.HORIZONTAL, false));
        rvAreas.setAdapter(filterAreaAdapter);

        filterTypeAdapter = new FilterAdapter(filterTypeList, new FilterAdapter.OnFilterItemListener() {
            @Override
            public void onItemClicked(FilterModel filterModel) {
                isRefresh = true;
                selectedFilterType = filterModel;
                buildSelectedFilterView();
                hitApiForMerchantPostsList("0", false);
            }
        });

        rvTypes.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayout.HORIZONTAL, false));
        rvTypes.setAdapter(filterTypeAdapter);

        filterDeliveryTypeAdapter = new FilterAdapter(filterDeliveryTypeList, new FilterAdapter.OnFilterItemListener() {
            @Override
            public void onItemClicked(FilterModel filterModel) {
                isRefresh = true;
                selectedFilterDeliveryType = filterModel;
                buildSelectedFilterView();
                hitApiForMerchantPostsList("0", false);
            }
        });

        rvDeliveryTypes.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayout.HORIZONTAL, false));
        rvDeliveryTypes.setAdapter(filterDeliveryTypeAdapter);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.discover_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.discover_fragment_menu_filter:
                if (llFilter.getVisibility() == View.VISIBLE) {
                    llFilter.setVisibility(View.GONE);
                    llSelectedFilters.setVisibility(View.INVISIBLE);
                } else {
                    llFilter.setVisibility(View.VISIBLE);
                    nestedScrollView.scrollTo(0, 0);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * method to open restaurant detail
     *
     * @param merchantId
     * @param pos
     */
    public void openRestaurantDetail(String merchantId, int pos) {
        if (!merchantPostsList.isEmpty()) {
            Intent intent = new Intent(getActivity(), RestaurantDetailActivity.class);
            intent.putExtra(Constants.MERCHANT_ID_KEY, merchantId);
            intent.putExtra(Constants.RESTRAU_IMAGE_KEY, merchantPostsList.get(pos).getProfilePicture());
            intent.putExtra(Constants.RESTRAU_NAME_KEY, merchantPostsList.get(pos).getName());
            intent.putExtra(Constants.RESTRAU_ADDRESS_KEY, merchantPostsList.get(pos).getAddress());
            intent.putExtra(Constants.IS_FAVORITE, merchantPostsList.get(pos).isFavorite());
            startActivity(intent);
        }
    }


    /**
     * set up refreshing on swipe
     */
    private void setUpRefreshing() {

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (appUtils.isInternetOn(getActivity())) {
                    //shouldLoadMore = false;
                    nextCount = 0;
                    isRefresh = true;
                    hitApiForMerchantPostsList(String.valueOf(nextCount), false);

                } else {
                    if (swipeRefreshLayout.isRefreshing())
                        swipeRefreshLayout.setRefreshing(false);

                    appUtils.showSnackBar(getView(), getString(R.string.internet_offline));

                }

            }
        });
    }

    /**
     * hit api for getting merchant deals and restraus
     *
     * @param count for the pagination
     */
    public void hitApiForMerchantPostsList(String count, boolean showProgress) {
        if (showProgress)
            appUtils.showProgressDialog(getContext(), true);
        ApiInterface service = RestApi.createService(ApiInterface.class);
        HashMap<String, String> params = new HashMap<>();
        params.put(ApiKeys.LATITUDE_KEY, String.valueOf(currentLatitude));
        params.put(ApiKeys.LONGITUDE_KEY, String.valueOf(currentLongitude));
        params.put(ApiKeys.COUNT_KEY, count);
        if (selectedFilterArea != null)
            params.put(ApiKeys.AREA_ID, String.valueOf(selectedFilterArea.getId()));
        if (selectedFilterType != null)
            params.put(ApiKeys.TYPE_ID, String.valueOf(selectedFilterType.getId()));
        if (selectedFilterDeliveryType != null)
            params.put(ApiKeys.DELIVERY_TYPE_ID, String.valueOf(selectedFilterDeliveryType.getId()));


        Call<ResponseBody> call = service.merchant_deal_list(Constants.API_KEY,
                                                             AppSharedPrefs.getInstance(getActivity()).getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, null),
                                                             appUtils.encryptData(params));
        ApiCall.getInstance().hitService(getContext(), call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(DATum.class, new CustomDATumDeserializer());
                MerchantPostsModel merchantPostsModel = gsonBuilder.create().fromJson(response, MerchantPostsModel.class);
                if (merchantPostsModel.getCODE() == 200) {
                    cvNoRecord.setVisibility(View.GONE);
                    if (isRefresh) {
                        merchantPostsList.clear();
                        isRefresh = false;
                    }

                    merchantPostsList.addAll(merchantPostsModel.getDATA());

                    nextCount = Integer.parseInt(merchantPostsModel.getNextCount());
                    homePostsAdapter.notifyDataSetChanged();

                } else if (merchantPostsModel.getCODE() == 300) {
                    merchantPostsList.clear();
                    homePostsAdapter.notifyDataSetChanged();
                    cvNoRecord.setVisibility(View.VISIBLE);
                    tvNoRecordMessage.setText(R.string.no_restraus_found);
                } else {
                    appUtils.showSnackBar(getView(), merchantPostsModel.getmESSAGE());
                }

                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onSuccessErrorBody(String response) {

            }

            @Override
            public void onFailure() {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    cvNoRecord.setVisibility(View.VISIBLE);
                    tvNoRecordMessage.setText(R.string.not_able_to_load_please_try_again);
                }
            }
        });
    }


    @Override
    public void onItemClicked(String merchantId, int position) {
        openRestaurantDetail(merchantId, position);
    }

    private void requestLocationPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                           LOCATION_PERMISSION_REQ_CODE);
    }

    private void getLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getContext() != null) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission();
            } else {
                buildGoogleApiClient(getContext());
                createLocationRequest();
                buildLocationSettingsRequest();
            }
        } else {
            buildGoogleApiClient(getContext());
            createLocationRequest();
            buildLocationSettingsRequest();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQ_CODE && getActivity() != null) {
            if (permissions.length > 0 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient(getContext());
                createLocationRequest();
                buildLocationSettingsRequest();
            } else if (permissions.length > 0 && (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissions[0]) /*||
                    !ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissions[1])*/)) {//has selected do not allow allow
                appUtils.showAllowPermissionFromSettingDialog(getActivity());
                hitApiForMerchantPostsList("0", false);
            } else {
                requestLocationPermission();
            }
        }
    }


    private void buildGoogleApiClient(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                // Sets the desired interval for active location updates. This interval is
                // inexact. You may not receive updates at all if no location sources are available, or
                // you may receive them slower than requested. You may also receive updates faster than
                // requested if other applications are requesting location at a faster interval.
                .setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
                // Sets the fastest rate for active location updates. This interval is exact, and your
                // application will never receive updates faster than this value.
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
                .setSmallestDisplacement(10);//set smallest displacement to change lat long
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);//this make sure dialog is always visible

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                final LocationSettingsStates locationSettingsStates = locationSettingsResult.getLocationSettingsStates();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        startLocationUpdates();
                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user a dialog.
                        try {
                            if(getActivity()!=null)
                                status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);//this prompts a user with a dialog, we get its call in onActivityResult.
                        } catch (IntentSender.SendIntentException ignored) {
                            //do nothing
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                if (resultCode == Activity.RESULT_OK) {

                    if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                        mGoogleApiClient.disconnect();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mGoogleApiClient.connect();
                            }
                        }, 1000);
                    } else {
                        buildLocationSettingsRequest();//otherwise show him the location dialog
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.i(TAG, "User chose not to make required location settings changes.");
                    buildLocationSettingsRequest();
                }
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressWarnings({"MissingPermission"})//permission has been handled already
    protected void startLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }


    //Connection Callbacks Method
    @Override
    @SuppressWarnings({"MissingPermission"})//permission has been handled already
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);//
            isFirstTime = true;
        } else {

            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
            if (isFirstTime && getActivity() != null) {
                if (appUtils.isInternetOn(getActivity())) {
                    isFirstTime = false;
                    hitApiForMerchantPostsList(String.valueOf(nextCount), false);
                } else {
                    cvNoRecord.setVisibility(View.VISIBLE);
                    tvNoRecordMessage.setText(R.string.internet_offline);
                }
            }
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "Google API client connection suspended");
    }


    //onConnection Failed method
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "Google API client connection failed");
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(LOG_TAG, "Google Location Service Stopped");
        }
    }


    // Location listener method - this method gets call back when requestLocationUpdates is called
    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.d(LOG_TAG, "Google Location Changed latitude =" + location.getLatitude() + "longitude =" + location.getLongitude());
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
            if (isFirstTime && getActivity()!=null) {
                if (appUtils.isInternetOn(getActivity())) {
                    hitApiForMerchantPostsList(String.valueOf(nextCount), false);
                    isFirstTime = false;
                } else {
                    cvNoRecord.setVisibility(View.VISIBLE);
                    tvNoRecordMessage.setText(R.string.internet_offline);
                }
            }
        }
    }

    @Override
    public void onHomeItemClicked(DATum daTum) {

        Intent intent = new Intent(getActivity(), RestaurantDetailActivity.class);
        intent.putExtra(Constants.MERCHANT_ID_KEY, daTum.getId());
        intent.putExtra(Constants.RESTRAU_IMAGE_KEY, daTum.getProfilePicture());
        intent.putExtra(Constants.RESTRAU_NAME_KEY, daTum.getName());
        intent.putExtra(Constants.RESTRAU_ADDRESS_KEY, daTum.getAddress());
        intent.putExtra(Constants.IS_FAVORITE, daTum.isFavorite());
        startActivity(intent);
    }

    @Override
    public void onFavoriteItemClicked(DATum daTum) {
        addRestaurantsToFavoriteApi(!daTum.isFavorite(), daTum.getId());
        daTum.setFavorite(!daTum.isFavorite());
    }

    private void addRestaurantsToFavoriteApi(boolean isFavorite, String merchantId) {
        HashMap<String, String> params = new HashMap<>();
        params.put("merchant_id", merchantId);
        ApiInterface service = RestApi.createService(ApiInterface.class);
        Call<ResponseBody> api;
        if (isFavorite) {
            api = service.addFavorite(AppSharedPrefs.getInstance(getActivity()).
                    getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, ""), params);
        } else {
            api = service.deleteFavorite(AppSharedPrefs.getInstance(getActivity()).
                    getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, ""), params);
        }

        ApiCall.getInstance().hitService(getActivity(), api, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                homePostsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onSuccessErrorBody(String response) {
            }

            @Override
            public void onFailure() {

            }
        });
    }

    private void getFiltersFromApi() {
        getAreaFilter();
        getTypesFilter();
        getDeliveryTypesFilter();
    }

    private void getAreaFilter() {
        RestApi.createService(ApiInterface.class).getAreas(Constants.API_KEY, AppSharedPrefs.getInstance(getActivity()).
                getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, null))
                .enqueue(new Callback<ApiModel<List<FilterModel>>>() {
                    @Override
                    public void onResponse(Call<ApiModel<List<FilterModel>>> call, Response<ApiModel<List<FilterModel>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            filterAreaList.clear();
                            filterAreaList.addAll(response.body().getData());
                            filterAreaAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiModel<List<FilterModel>>> call, Throwable t) {
                        Log.e(TAG, "error fetching filters", t);
                    }
                });
    }

    private void getTypesFilter() {
        RestApi.createService(ApiInterface.class).getTypes(Constants.API_KEY, AppSharedPrefs.getInstance(getActivity()).
                getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, null))
                .enqueue(new Callback<ApiModel<List<FilterModel>>>() {
                    @Override
                    public void onResponse(Call<ApiModel<List<FilterModel>>> call, Response<ApiModel<List<FilterModel>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            filterTypeList.clear();
                            filterTypeList.addAll(response.body().getData());
                            filterTypeAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiModel<List<FilterModel>>> call, Throwable t) {
                        Log.e(TAG, "error fetching filters", t);
                    }
                });
    }

    private void getDeliveryTypesFilter() {
        RestApi.createService(ApiInterface.class).getDeliveryTypes(Constants.API_KEY, AppSharedPrefs.getInstance(getActivity()).
                getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, null))
                .enqueue(new Callback<ApiModel<List<FilterModel>>>() {
                    @Override
                    public void onResponse(Call<ApiModel<List<FilterModel>>> call, Response<ApiModel<List<FilterModel>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            filterDeliveryTypeList.clear();
                            filterDeliveryTypeList.addAll(response.body().getData());
                            filterDeliveryTypeAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiModel<List<FilterModel>>> call, Throwable t) {
                        Log.e(TAG, "error fetching filters", t);
                    }
                });
    }

    @OnClick(R.id.fragment_discover_btn_retry)
    public void onClick() {
        if (appUtils.isInternetOn(getActivity())) {
            //getLocationPermission();
            isRefresh = true;
            nextCount = 0;
            cvNoRecord.setVisibility(View.GONE);
            hitApiForMerchantPostsList(String.valueOf(nextCount), true);
        } else {
            cvNoRecord.setVisibility(View.VISIBLE);
            tvNoRecordMessage.setText(R.string.internet_offline);
        }
    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if (llFilter.getVisibility() == View.VISIBLE && !isFilterVisibleOnScreen() && llSelectedFilters.getChildCount() > 0) {
            llSelectedFilters.setVisibility(View.VISIBLE);
        } else {
            llSelectedFilters.setVisibility(View.INVISIBLE);
        }

        if (v.getChildAt(v.getChildCount() - 1) != null) {
            if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                    scrollY > oldScrollY) {
                if (appUtils.isInternetOn(getActivity())) {
                    hitApiForMerchantPostsList(String.valueOf(nextCount), false);
                } else {
                    appUtils.showSnackBar(getView(), getString(R.string.internet_offline));
                }
            }
        }
    }

    private boolean isFilterVisibleOnScreen() {
        return llFilter.getLocalVisibleRect(scrollBounds);
    }

    private void buildSelectedFilterView() {
        llSelectedFilters.removeAllViews();

        if (selectedFilterArea != null) {
            llSelectedFilters.addView(buildSelectedFilterTextView(selectedFilterArea.getName()));
        }
        if (selectedFilterType != null) {
            llSelectedFilters.addView(buildSelectedFilterTextView(selectedFilterType.getName()));
        }
        if (selectedFilterDeliveryType != null) {
            llSelectedFilters.addView(buildSelectedFilterTextView(selectedFilterDeliveryType.getName()));
        }
    }

    private TextView buildSelectedFilterTextView(String text) {
        TextView textView = new TextView(getActivity());
        textView.setText(text);
        textView.setSelected(true);
        textView.setPadding(18, 18, 18, 18);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(18, 18, 18, 18);
        textView.setLayoutParams(params);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(R.style.TextAppearance_FontRegular);
        } else {
            textView.setTextAppearance(getContext(), R.style.TextAppearance_FontRegular);
        }
        textView.setBackground(getResources().getDrawable(R.drawable.filter_item_selector));
        textView.setTextColor(getResources().getColor(R.color.appDarkGreen));
        return textView;
    }
}
