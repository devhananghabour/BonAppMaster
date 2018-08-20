package app.bonapp.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.bonapp.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.bonapp.adapters.CardsListAdapter;
import app.bonapp.constants.ApiKeys;
import app.bonapp.interfaces.DialogButtonClickListener;
import app.bonapp.interfaces.OnRecyclerViewItemClickListener;
import app.bonapp.models.cardslist.CardsListModel;
import app.bonapp.models.cardslist.RESULT;
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
 * In this class list of all saved cards is shown , also user can delete previously saved cards by
 * clicking delete button.
 */

public class PaymentDetailsActivity extends AppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_end)
    TextView tvEnd;
    @BindView(R.id.activity_payment_details)
    RelativeLayout activityPaymentDetails;
    @BindView(R.id.rv_cards_listing)
    RecyclerView rvCardsListing;
    @BindView(R.id.ll_no_cards)
    LinearLayout llNoCards;
    @BindView(R.id.cv_no_record)
    CardView cvNoRecord;
    @BindView(R.id.tv_norecord_messsge)
    TextView tvNorecordMesssge;

    private Activity mActivity;
    private AppUtils appUtils;
    private CardsListAdapter cardsListAdapter;
    private List<RESULT> cardsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);
        ButterKnife.bind(this);
        mActivity = PaymentDetailsActivity.this;
        appUtils = AppUtils.getInstance();
        setUpViews();
        setUpRecylerViews();
        if (appUtils.isInternetOn(mActivity)) {
            hitApiForCardsList();
        } else {
            cvNoRecord.setVisibility(View.VISIBLE);
            tvNorecordMesssge.setText(R.string.internet_offline);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    /**
     * method to setup views
     */
    private void setUpViews() {
        ivBack.setVisibility(View.VISIBLE);
        tvTitle.setText(R.string.payment_details);

    }

    /**
     * method to set adapter to recycler view , also contains the pagination logic
     */
    private void setUpRecylerViews() {
        cardsList=new ArrayList<>();
        rvCardsListing.setLayoutManager(new LinearLayoutManager(mActivity));
        cardsListAdapter = new CardsListAdapter(mActivity, cardsList);
        rvCardsListing.setAdapter(cardsListAdapter);

        cardsListAdapter.setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onRecyclerViewItemClick(final View view, final int position) {
                appUtils.showAlertDialog(mActivity, getString(R.string.delete_credit_card),
                        getString(R.string.credit_card_delete_msg), getString(R.string.yes), getString(R.string.cancel), new DialogButtonClickListener() {
                            @Override
                            public void positiveButtonClick() {
                                if (appUtils.isInternetOn(mActivity)){
                                    hitApiForDeleteCard(cardsList.get(position).getCardId(),position);
                                }else {
                                    appUtils.showSnackBar(activityPaymentDetails,getString(R.string.internet_offline));
                                }

                            }

                            @Override
                            public void negativeButtonClick() {

                            }
                        });
            }

            @Override
            public void onRecyclerViewItemLongClick(View view, int position) {

            }
        });
    }


    @OnClick({R.id.tv_end, R.id.activity_payment_details, R.id.iv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_payment_details:
                appUtils.hideNativeKeyboard(mActivity);
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }


    /**
     * method to hit api for getting cards list
     */
    private void hitApiForCardsList() {
        appUtils.showProgressDialog(mActivity, true);
        ApiInterface service = RestApi.createService(ApiInterface.class);
        Call<ResponseBody> call = service.getCards(AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, ""));
        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                CardsListModel cardsListModel = new Gson().fromJson(response, CardsListModel.class);
                if (cardsListModel.getCODE() == 200) {
                    llNoCards.setVisibility(View.GONE);
                    if (cardsListModel.getRESULT() != null) {
                        cardsList.addAll(cardsListModel.getRESULT());
                        cardsListAdapter.notifyDataSetChanged();
                    }
                } else if (cardsListModel.getCODE() == 207) {
                    cvNoRecord.setVisibility(View.GONE);
                    llNoCards.setVisibility(View.VISIBLE);
                } else {
                    appUtils.showSnackBar(activityPaymentDetails, cardsListModel.getMESSAGE());
                }
            }

            @Override
            public void onSuccessErrorBody(String response) {

            }

            @Override
            public void onFailure() {
                cvNoRecord.setVisibility(View.VISIBLE);
                tvNorecordMesssge.setText(R.string.unable_to_load);
            }
        });

    }

    @OnClick(R.id.btn_retry)
    public void onViewClicked() {
        if (appUtils.isInternetOn(mActivity)) {
            cvNoRecord.setVisibility(View.GONE);
            hitApiForCardsList();
        }
    }

    /**
     * methd to hit api for deleting card
     * @param cardId
     * @param position
     */
    private void hitApiForDeleteCard(String cardId, final int position){
        appUtils.showProgressDialog(mActivity,true);
        ApiInterface service=RestApi.createService(ApiInterface.class);
        HashMap<String,String> params=new HashMap<>();
        params.put("card_id",cardId);
        Call<ResponseBody> call=service.deleteCard(AppSharedPrefs.getInstance(mActivity).
                getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN,""),appUtils.encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                try {
                    JSONObject mainObjet=new JSONObject(response);
                    if (mainObjet.getInt(ApiKeys.CODE)==200){
                        cardsList.remove(position);
                        TransitionManager.beginDelayedTransition(activityPaymentDetails);
                        cardsListAdapter.notifyItemRemoved(position);

                        llNoCards.setVisibility(cardsList.size()==0?View.VISIBLE:View.GONE);

                    }else if (mainObjet.getInt(ApiKeys.CODE)==ApiKeys.UNAUTHORISED_CODE){
                        appUtils.logoutFromApp(mActivity,mainObjet.optString(ApiKeys.MESSAGE));
                    }
                    else {
                        appUtils.showSnackBar(activityPaymentDetails,mainObjet.optString(ApiKeys.MESSAGE));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
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

}
