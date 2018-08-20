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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import app.bonapp.adapters.HomePostsAdapter;
import app.bonapp.adapters.RowRestrauItemsAdapter;
import app.bonapp.constants.ApiKeys;
import app.bonapp.constants.Constants;
import app.bonapp.models.merchantposts.DATum;
import app.bonapp.models.merchantposts.MerchantPostsModel;
import app.bonapp.network.ApiCall;
import app.bonapp.network.ApiInterface;
import app.bonapp.network.NetworkListener;
import app.bonapp.network.RestApi;
import app.bonapp.utils.AppSharedPrefs;
import app.bonapp.utils.CustomDATumDeserializer;
import butterknife.BindView;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class FavoritesFragment extends BaseFragment implements RowRestrauItemsAdapter.OnRestaurantClickListener,
        HomePostsAdapter.OnHomePostItemClickedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        NestedScrollView.OnScrollChangeListener {

    @BindView(R.id.fragment_favorites_swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.fragment_favorites_recycler_view_favorites)
    RecyclerView recyclerView;
    @BindView(R.id.fragment_favorites_ll_no_record)
    LinearLayout llNoRecord;
    @BindView(R.id.fragment_favorites_tv_no_record_message)
    TextView tvNoRecordMessage;
    @BindView(R.id.fragment_favorites_nested_scrollView)
    NestedScrollView nestedScrollView;

    private HomePostsAdapter homePostsAdapter;
    private List<DATum> merchantPostsList = new ArrayList<>();

    public static FavoritesFragment newInstances() {
        return new FavoritesFragment();
    }

    private int nextCount = 0;
    private double currentLatitude;
    private double currentLongitude;
    private BroadcastReceiver refreshReceiver;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private int CONNECTION_FAILURE_RESOLUTION_REQUEST = 1;
    private static final String LOG_TAG = "More Info Fragment";
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10 * 1000;//10 seconds
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5 * 1000;//10 seconds
    private static final int LOCATION_PERMISSION_REQ_CODE = 1001;

    private boolean isFirstTime = true;
    private boolean isRefresh = false;

    final Rect scrollBounds = new Rect();

    @Override
    protected int getResourceLayoutId() {
        return R.layout.fragment_favorites;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (appUtils.isInternetOn(getActivity())) {
            getLocationPermission();
        } else {
            llNoRecord.setVisibility(View.VISIBLE);
            tvNoRecordMessage.setText(R.string.internet_offline);
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpRefreshing();
        setupAdapter();
//        getFavoritesFromApi(String.valueOf(nextCount), false);

        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(getRefreshReceiver(getActivity()),
                    new IntentFilter(Constants.REFRESH_RECCEIVER_KEY));
        }
        nestedScrollView.getHitRect(scrollBounds);
        appUtils.trackScreen(Constants.MERCHANT_LIST_SCREEN_NAME, getActivity());
        nestedScrollView.setOnScrollChangeListener(this);
    }

    private void setupAdapter() {
        homePostsAdapter = new HomePostsAdapter(merchantPostsList, getContext(), this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(homePostsAdapter);
        recyclerView.setNestedScrollingEnabled(false);
    }

    private void setUpRefreshing() {

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (appUtils.isInternetOn(getActivity())) {
//                    shouldLoadMore = false;
                    nextCount = 0;
                    isRefresh = true;
                    getFavoritesFromApi(String.valueOf(nextCount), false);

                } else {
                    if (swipeRefreshLayout.isRefreshing())
                        swipeRefreshLayout.setRefreshing(false);

                    appUtils.showSnackBar(getView(), getString(R.string.internet_offline));

                }

            }
        });
    }



    private void getFavoritesFromApi(String count, boolean showProgress) {
        if (count == "-1")
            return;
        if (showProgress)
            appUtils.showProgressDialog(getContext(), true);

        ApiInterface service = RestApi.createService(ApiInterface.class);
        HashMap<String, String> params = new HashMap<>();

        params.put(ApiKeys.LATITUDE_KEY, String.valueOf(currentLatitude));
        params.put(ApiKeys.LONGITUDE_KEY, String.valueOf(currentLongitude));
        params.put(ApiKeys.COUNT_KEY, count);

        Call<ResponseBody> call = service.getFavorites(Constants.API_KEY, AppSharedPrefs.getInstance(getActivity()).
                getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, null), params);
        ApiCall.getInstance().hitService(getContext(), call, new NetworkListener() {

            @Override
            public void onSuccess(int responseCode, String response) {

                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(DATum.class, new CustomDATumDeserializer());
                MerchantPostsModel merchantPostsModel = gsonBuilder.create().fromJson(response, MerchantPostsModel.class);
                if (merchantPostsModel.getCODE() == 200) {
                    llNoRecord.setVisibility(View.GONE);
                    if (isRefresh) {
                        merchantPostsList.clear();
                        isRefresh = false;
                    }

                    merchantPostsList.addAll(merchantPostsModel.getDATA());
                    Log.d(LOG_TAG, "Data Loaded");

                    nextCount = Integer.parseInt(merchantPostsModel.getNextCount());
                    Log.d(LOG_TAG, "Next Count: " + nextCount);
//                    if (Integer.parseInt(merchantPostsModel.getNextCount()) > 0) {
//                        setLoaded();
//                    }

                    homePostsAdapter.notifyDataSetChanged();

                } else if (merchantPostsModel.getCODE() == 300) {
                    merchantPostsList.clear();
                    homePostsAdapter.notifyDataSetChanged();
                    llNoRecord.setVisibility(View.VISIBLE);
                    tvNoRecordMessage.setText(R.string.favorites_not_added);
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

            }
        });
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
                        getFavoritesFromApi(String.valueOf(nextCount), false);

                    }
                }
            };
        }
        return refreshReceiver;
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
        removeFromFavorites(daTum.getId());
        daTum.setFavorite(!daTum.isFavorite());
    }

    private void removeFromFavorites(String merchantId) {
        HashMap<String, String> params = new HashMap<>();
        params.put("merchant_id", merchantId);
        ApiInterface service = RestApi.createService(ApiInterface.class);
        Call<ResponseBody> api = service.deleteFavorite(AppSharedPrefs.getInstance(getActivity()).
                getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, ""), params);

        ApiCall.getInstance().hitService(getActivity(), api, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                getFavoritesFromApi(String.valueOf(nextCount), false);
            }

            @Override
            public void onSuccessErrorBody(String response) {
            }

            @Override
            public void onFailure() {

            }
        });
    }

    @Override
    public void onItemClicked(String merchantId, int position) {
        openRestaurantDetail(merchantId, position);
    }

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

    private void requestLocationPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION_REQ_CODE);
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

    protected void startLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() && getActivity() != null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.d(LOG_TAG, "Google Location Changed latitude =" + location.getLatitude() + "longitude =" + location.getLongitude());
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
            if (isFirstTime && getActivity()!=null) {
                if (appUtils.isInternetOn(getActivity())) {
                    getFavoritesFromApi(String.valueOf(nextCount), false);
                    isFirstTime = false;
                } else {
                    llNoRecord.setVisibility(View.VISIBLE);
                    tvNoRecordMessage.setText(R.string.internet_offline);
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "Google API client connection suspended");
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
                    getFavoritesFromApi(String.valueOf(nextCount), false);
                } else {
                    tvNoRecordMessage.setText(R.string.internet_offline);
                }
            }
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(getRefreshReceiver(getActivity()));
        }
    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {


        if (v.getChildAt(v.getChildCount() - 1) != null) {
            if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                    scrollY > oldScrollY) {
                if (appUtils.isInternetOn(getActivity())) {
                    getFavoritesFromApi(String.valueOf(nextCount), false);
                } else {
                    appUtils.showSnackBar(getView(), getString(R.string.internet_offline));
                }
            }
        }
    }
}
