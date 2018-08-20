package app.bonapp.fragments.merchant;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.bonapp.R;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Locale;

import app.bonapp.activities.MerchantActivity;
import app.bonapp.activities.OrderHistoryActivity;
import app.bonapp.constants.ApiKeys;
import app.bonapp.models.merchantstats.DATA;
import app.bonapp.models.merchantstats.MerchantStatsModel;
import app.bonapp.models.merchantstats.Result;
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

/**
 * this class contains logic of showing the stats of a merchant
 */
public class MerchantStatsFragment extends Fragment {
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_total_deals_created)
    TextView tvTotalDealsCreated;
    @BindView(R.id.tv_total_items_sale)
    TextView tvTotalItemsSale;
    @BindView(R.id.tv_items_sold)
    TextView tvItemsSold;
    @BindView(R.id.tv_perc_sold)
    TextView tvPercSold;
    @BindView(R.id.tv_sold_for)
    TextView tvSoldFor;
    @BindView(R.id.tv_sold_for_ex_vat)
    TextView tvSoldForExVat;
    @BindView(R.id.tv_income)
    TextView tvIncome;
    @BindView(R.id.tv_income_ex_vat)
    TextView tvIncomeExVat;
    @BindView(R.id.btn_order_history)
    Button btnOrderHistory;
    @BindView(R.id.rl_merchant_stats)
    RelativeLayout rlMerchantStats;
    @BindView(R.id.ll_date)
    LinearLayout llDate;


    private Activity mActivity;
    private AppUtils appUtils;

    private final String ALL_TIME="1",CURRENT_MONTH="2",CURRENT_WEEK="3",TODAY="4";

    public MerchantStatsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_merchant_stats, container, false);
        ButterKnife.bind(this, view);
        appUtils = AppUtils.getInstance();
        mActivity = getActivity();
        if (appUtils.isInternetOn(mActivity)) {
            hitApiForMerchantStats(ALL_TIME);
        } else {
            appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.internet_offline));
        }
        ((MerchantActivity)mActivity).pushMerchantStatsToolbar();

        return view;
    }



    /**
     * method to hit api for merchant stats
     */
    private void hitApiForMerchantStats(String type) {
        appUtils.showProgressDialog(mActivity, true);

        ApiInterface service = RestApi.createService(ApiInterface.class);
        HashMap<String,String> params=new HashMap<>();
        params.put(ApiKeys.TYPE_KEY,type);
        Call<ResponseBody> call = service.merchantStats(AppSharedPrefs.getInstance(mActivity).
                getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, ""),appUtils.encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                if (isAdded()) {
                    MerchantStatsModel merchantStatsModel = new Gson().fromJson(response, MerchantStatsModel.class);
                    if (merchantStatsModel.getCODE() == 200) {

                        setData(merchantStatsModel.getDATA());

                    } else if (merchantStatsModel.getCODE() == ApiKeys.UNAUTHORISED_CODE) {
                        appUtils.logoutFromApp(mActivity, merchantStatsModel.getMESSAGE());
                    } else {
                        appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), merchantStatsModel.getMESSAGE());
                    }
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
     * method to set data
     *
     * @param daTum
     */
    private void setData(DATA daTum) {
        TransitionManager.beginDelayedTransition(rlMerchantStats);
        String[] timeArray=getResources().getStringArray(R.array.time_array);
        String type=daTum.getType();
        switch (type){
            case ALL_TIME:
                tvDate.setText(timeArray[0]);
                break;
            case CURRENT_MONTH:
                tvDate.setText(timeArray[1]);
                break;
            case CURRENT_WEEK:
                tvDate.setText(timeArray[2]);
                break;
            case TODAY:
                tvDate.setText(timeArray[3]);
                break;
        }
        Result result=daTum.getResult();
        tvTotalDealsCreated.setText(result.getDealCreated());
        if (result.getTotalItemForSale()!=null)
        tvTotalItemsSale.setText(result.getTotalItemForSale());
        else tvTotalItemsSale.setText("0");
        if (result.getItemSold()!=null)
        tvItemsSold.setText(result.getItemSold());
        else tvItemsSold.setText("0");
        if (result.getSoldPercentage()!=null)
        tvPercSold.setText(result.getSoldPercentage());
        else tvPercSold.setText("0");
        double soldFor = 0, soldForExVat = 0, income = 0, incomeExVat = 0;
        try {
            soldFor = Double.parseDouble(result.getSoldFor());
            soldForExVat = Double.parseDouble(result.getSoldForWtx());
            if (result.getYourIncome() != null)
                income = Double.parseDouble(result.getYourIncome());
            if (result.getYourIncomeWtx() != null)
                incomeExVat = Double.parseDouble(result.getYourIncomeWtx());
        } catch (NumberFormatException ne) {
            ne.printStackTrace();
        }
        tvSoldFor.setText(String.format(Locale.US, "%.2f", soldFor));
        tvSoldFor.append(" ");
        tvSoldFor.append(getString(R.string.currency));

        tvSoldForExVat.setText(String.format(Locale.US, "%.2f", soldForExVat));
        tvSoldForExVat.append(" ");
        tvSoldForExVat.append(getString(R.string.currency));

        tvIncome.setText(String.format(Locale.US, "%.2f", income));
        tvIncome.append(" ");
        tvIncome.append(getString(R.string.currency));

        tvIncomeExVat.setText(String.format(Locale.US, "%.2f", incomeExVat));
        tvIncomeExVat.append(" ");
        tvIncomeExVat.append(getString(R.string.currency));
    }


    @OnClick({R.id.ll_date, R.id.btn_order_history})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_date:
                showTimeFilterDialog();
                break;
            case R.id.btn_order_history:
                Intent intent = new Intent(mActivity, OrderHistoryActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * method to show dialog with items for applying filtering
     */
    private void showTimeFilterDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(mActivity);
        builder.setTitle("");
        String[] items=getResources().getStringArray(R.array.time_array);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (appUtils.isInternetOn(mActivity)){
                    hitApiForMerchantStats(String.valueOf(which+1));
                }else {
                    appUtils.showSnackBar(mActivity.findViewById(android.R.id.content),getString(R.string.internet_offline));
                }
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimationZoom;
        alertDialog.show();
    }

}
