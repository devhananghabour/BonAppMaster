package app.bonapp.fragments.merchant;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.bonapp.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.bonapp.activities.CreateDealActivity;
import app.bonapp.activities.MerchantActivity;
import app.bonapp.activities.MerchantDealDetailActivity;
import app.bonapp.adapters.MerchantDealsAdapter;
import app.bonapp.constants.ApiKeys;
import app.bonapp.constants.Constants;
import app.bonapp.interfaces.OnRecyclerViewItemClickListener;
import app.bonapp.models.merchantdeals.DATum;
import app.bonapp.models.merchantdeals.MerchantDealsModel;
import app.bonapp.network.ApiCall;
import app.bonapp.network.ApiInterface;
import app.bonapp.network.NetworkListener;
import app.bonapp.network.RestApi;
import app.bonapp.utils.AppSharedPrefs;
import app.bonapp.utils.AppUtils;
import app.bonapp.utils.NpaLinearLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * this class shows all the deals of a merchant.
 */
public class MerchantDealsFragment extends Fragment {

    @BindView(R.id.btn_create_deal)
    Button btnCreateDeal;
    @BindView(R.id.rv_merchant_deals)
    RecyclerView rvMerchantDeals;
    @BindView(R.id.tv_no_deals_found)
    TextView tvNoDealsFound;
    @BindView(R.id.tv_not_loaded)
    TextView tvNotLoaded;
    @BindView(R.id.btn_retry)
    Button btnRetry;
    @BindView(R.id.ll_not_loaded)
    LinearLayout llNotLoaded;
    private Activity mActivity;
    private AppUtils appUtils;
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 4;
    private int lastVisibleItem, totalItemCount;
    private boolean loading = true;
    private NpaLinearLayoutManager linearLayoutManager;
    private MerchantDealsAdapter merchantDealsAdapter;
    private List<DATum> merchantDealList;
    private int nextCount = 0;
    private boolean isRefresh=false;
    private String pickupTime="";

    public MerchantDealsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_merchant_deals, container, false);
        mActivity = getActivity();
        appUtils = AppUtils.getInstance();
        ButterKnife.bind(this, view);
        setupRecyclerView();
        if (appUtils.isInternetOn(mActivity)) {
            appUtils.showProgressDialog(mActivity,true);
            nextCount=0;
            hitApiForMerchantDealsListing(String.valueOf(nextCount));
        } else {
            llNotLoaded.setVisibility(View.VISIBLE);
            tvNotLoaded.setText(R.string.internet_offline);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MerchantActivity) mActivity).showMerchantHomeToolbar();
    }

    /**
     * method to setup recylerviews
     */
    private void setupRecyclerView() {
        merchantDealList = new ArrayList<>();
        merchantDealsAdapter = new MerchantDealsAdapter(merchantDealList, mActivity);
        linearLayoutManager = new NpaLinearLayoutManager(mActivity);
        rvMerchantDeals.setLayoutManager(linearLayoutManager);
        rvMerchantDeals.setAdapter(merchantDealsAdapter);


        rvMerchantDeals.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        hitApiForMerchantDealsListing(String.valueOf(nextCount));
                        loading = true;
                    } else {
                        appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.internet_offline));
                    }
                }
            }
        });

        merchantDealsAdapter.setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onRecyclerViewItemClick(View view, int position) {  //1 for Activated and 2 for deactiviated deal
                if (merchantDealList.get(position).getDealStatus().equals("1")) {
                    Intent intent = new Intent(mActivity, MerchantDealDetailActivity.class);
                    intent.putExtra("deal_model", merchantDealList.get(position));
                    //intent.putExtra(Constants.DEAL_ID_KEY,merchantDealList.get(position).getDealId());
                    intent.putExtra(Constants.PICKUP_TIME_MERCHANT,pickupTime);
                    intent.putExtra(Constants.DEAL_POSITION_KEY, position);

                    startActivityForResult(intent, Constants.DEAL_DETAIL_REQ_CODE);
                }
                else {
                    Intent intent = new Intent(mActivity, CreateDealActivity.class);
                    intent.putExtra("deal_model", merchantDealList.get(position));
                    intent.putExtra(Constants.DEAL_POSITION_KEY, position);
                    intent.putExtra(Constants.PICKUP_TIME_MERCHANT,pickupTime);

                    startActivityForResult(intent, Constants.DEAL_DETAIL_REQ_CODE);
                }
            }

            @Override
            public void onRecyclerViewItemLongClick(View view, int position) {

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
    private void hitApiForMerchantDealsListing(String count) {
        HashMap<String, String> params = new HashMap<>();
        params.put(ApiKeys.COUNT_KEY, count);
        ApiInterface service = RestApi.createService(ApiInterface.class);
        Call<ResponseBody> call = service.merchantDeals(AppSharedPrefs.getInstance(mActivity).
                getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, ""), appUtils.encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                if (isAdded()) {
                    MerchantDealsModel merchantDealsModel = new Gson().fromJson(response, MerchantDealsModel.class);
                    if (merchantDealsModel.getCODE() == 200) {
                        if (isRefresh){
                            merchantDealList.clear();
                            isRefresh=false;
                        }

                        llNotLoaded.setVisibility(View.GONE);
                        tvNoDealsFound.setVisibility(merchantDealsModel.getDATA().size() == 0 ? View.VISIBLE : View.GONE);
                        merchantDealList.addAll(merchantDealsModel.getDATA());
                        if (merchantDealList.size()>1){
                            if (merchantDealList.get(0).getDealStatus().equals(Constants.ACTIVATED_DAL) &&
                                    merchantDealList.get(1).getDealStatus().equals(Constants.ACTIVATED_DAL)){
                                pickupTime=merchantDealList.get(0).getEndTime();
                            }
                        }
                        nextCount = Integer.parseInt(merchantDealsModel.getNextCount());
                        if (Integer.parseInt(merchantDealsModel.getNextCount()) > 0) {
                            setLoaded();
                        }
                        merchantDealsAdapter.notifyDataSetChanged();
                        //appUtils.notifyDataSetChangedWithAnim(rvMerchantDeals);
                    } else if (merchantDealsModel.getCODE() == 300) {
                        llNotLoaded.setVisibility(View.GONE);
                        tvNoDealsFound.setVisibility(View.VISIBLE);
                    } else if (merchantDealsModel.getCODE() == ApiKeys.UNAUTHORISED_CODE) {
                        appUtils.logoutFromApp(mActivity, merchantDealsModel.getmESSAGE());
                    } else {
                        appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), merchantDealsModel.getmESSAGE());
                    }
                }
                appUtils.hideProgressDialog(mActivity);
            }

            @Override
            public void onSuccessErrorBody(String response) {

            }

            @Override
            public void onFailure() {
                if (isAdded()){
                llNotLoaded.setVisibility(View.VISIBLE);
                tvNotLoaded.setText(R.string.deals_not_able_to_load);
            }
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.DEAL_DETAIL_REQ_CODE && resultCode == Constants.DEAL_EDIT_RESULT_CODE) {
            int pos = data.getIntExtra(Constants.DEAL_POSITION_KEY, 0);
            merchantDealList.get(pos).setDealTitle(data.getStringExtra(Constants.DEAL_TITLE));
            merchantDealList.get(pos).setStartTime(data.getStringExtra(Constants.BEGIN_TIME_KEY));
            merchantDealList.get(pos).setEndTime(data.getStringExtra(Constants.END_TIME_KEY));
            String createdOn = data.getStringExtra(Constants.DEAL_CREATED_DATE_KEY);
            if (createdOn != null)
                merchantDealList.get(pos).setDealCreatedOn(createdOn);
            merchantDealList.get(pos).setTotalItems(data.getStringExtra(Constants.TOTAL_ITEMS_KEY));

            merchantDealsAdapter.notifyItemChanged(pos);

        } else if (requestCode == Constants.DEAL_DETAIL_REQ_CODE && resultCode == Constants.DEAL_DEACTIVIATE_RESULT_CODE) {
            int pos = data.getIntExtra(Constants.DEAL_POSITION_KEY, 0);
            merchantDealList.get(pos).setDealStatus(data.getStringExtra(Constants.DEAL_STATUS_KEY));

            merchantDealsAdapter.notifyItemChanged(pos);
            pickupTime="";
            if (merchantDealList.size()>1){
                if (merchantDealList.get(0).getDealStatus().equals(Constants.ACTIVATED_DAL) &&
                        merchantDealList.get(1).getDealStatus().equals(Constants.ACTIVATED_DAL)){
                    pickupTime=merchantDealList.get(0).getEndTime();
                }
            }

        }else if (requestCode == Constants.DEAL_DETAIL_REQ_CODE && resultCode == Constants.DEAL_ACTIVIATE_RESULT_CODE) {
            if (appUtils.isInternetOn(mActivity)){
                isRefresh=true;
                nextCount=0;

                appUtils.showProgressDialog(mActivity,true);
                hitApiForMerchantDealsListing(String.valueOf(nextCount));

            }
            else {
                llNotLoaded.setVisibility(View.VISIBLE);
                tvNotLoaded.setText(R.string.internet_offline);
            }
        }
        else if (requestCode==Constants.CREATE_DEAL_REQ_CODE && resultCode==Constants.CREATE_DEAL_REQ_CODE){
            if (appUtils.isInternetOn(mActivity)){
                isRefresh=true;
                nextCount=0;

                appUtils.showProgressDialog(mActivity,true);
                hitApiForMerchantDealsListing(String.valueOf(nextCount));

            }
            else {
                llNotLoaded.setVisibility(View.VISIBLE);
                tvNotLoaded.setText(R.string.internet_offline);
            }
        }

    }


    @OnClick({R.id.btn_create_deal, R.id.btn_retry})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_create_deal:
                if (merchantDealList.size()>0){
                    if (merchantDealList.get(0).getDealStatus().equals(Constants.ACTIVATED_DAL)){
                        pickupTime=merchantDealList.get(0).getEndTime();
                    }
                }

                Intent intent=new Intent(mActivity, CreateDealActivity.class);
                intent.putExtra(Constants.IS_CREATE_DEAL,true);
                intent.putExtra(Constants.PICKUP_TIME_MERCHANT,pickupTime);

                this.startActivityForResult(intent,Constants.CREATE_DEAL_REQ_CODE);
                break;
            case R.id.btn_retry:
                if (appUtils.isInternetOn(mActivity)){
                    llNotLoaded.setVisibility(View.GONE);
                    nextCount=0;
                    appUtils.showProgressDialog(mActivity,true);
                    hitApiForMerchantDealsListing(String.valueOf(nextCount));
                }else {
                    llNotLoaded.setVisibility(View.VISIBLE);
                    tvNotLoaded.setText(R.string.internet_offline);
                }
                break;
        }
    }
}
