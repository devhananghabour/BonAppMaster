package app.bonapp.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetDialog;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.app.bonapp.BuildConfig;
import com.app.bonapp.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import app.bonapp.activities.AddCardActivity;
import app.bonapp.activities.AddressListActivity;
import app.bonapp.activities.DeliverInfoActivity;
import app.bonapp.activities.OrderDetailActivity;
import app.bonapp.activities.PaymentDetailsActivity;
import app.bonapp.activities.RegisterActivity;
import app.bonapp.activities.RestaurantDetailActivity;
import app.bonapp.adapters.CardsListInDialogAdapter;
import app.bonapp.adapters.CheckoutItemsAdapter;
import app.bonapp.constants.ApiKeys;
import app.bonapp.constants.Constants;
import app.bonapp.interfaces.DialogButtonClickListener;
import app.bonapp.interfaces.OnRecyclerViewItemClickListener;
import app.bonapp.models.CartItemModel;
import app.bonapp.models.DeliveryType;
import app.bonapp.models.FilterModel;
import app.bonapp.models.cardslist.CardsListModel;
import app.bonapp.models.cardslist.RESULT;
import app.bonapp.models.taxlist.DATum;
import app.bonapp.models.user.AddressModel;
import app.bonapp.models.user.BillingAddressModel;
import app.bonapp.network.ApiCall;
import app.bonapp.network.ApiInterface;
import app.bonapp.network.NetworkListener;
import app.bonapp.network.RestApi;
import app.bonapp.operations.UserOperations;
import app.bonapp.utils.AppSharedPrefs;
import app.bonapp.utils.AppUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import paytabs.project.PayTabActivity;
import retrofit2.Call;

import static android.content.Context.MODE_PRIVATE;


public class CheckoutFragment extends Fragment {

    private static final int SELECT_DELIVERY_ADDRESS = 100;
    private static final String CART_ITEMS = "cart_items";
    private static final String MERCHANT_ID = "merchant_id";
    private static final String DELIVERY_FEES = "delivery_fees";
    private static final String MINIMUM_ORDER = "minimum_order";
    private static final String DELIVERY_AVAILABLE = "delivery_available";
    private static final String DELIVERY_AREAS = "delivery_areas";

    @BindView(R.id.tv_edit)
    TextView tvEdit;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_down)
    ImageView ivDown;
    @BindView(R.id.tv_add_card)
    TextView tvAddCard;
    @BindView(R.id.btn_order_food)
    Button btnOrderFood;
    @BindView(R.id.ll_order)
    LinearLayout llOrder;
    @BindView(R.id.rv_cart_items)
    RecyclerView rvCartItems;
    @BindView(R.id.tv_vat_perc)
    TextView tvVatPerc;
    @BindView(R.id.tv_new_price)
    TextView tvNewPrice;
    @BindView(R.id.ll_vat)
    LinearLayout llVat;
    @BindView(R.id.tv_total_price)
    TextView tvTotalPrice;
    @BindView(R.id.ll_total)
    LinearLayout llTotal;
    @BindView(R.id.sv_items)
    ScrollView svItems;
    @BindView(R.id.fragment_checkout_llDeliveryFees)
    LinearLayout llDeliveryFees;
    @BindView(R.id.fragment_checkout_tvDeliveryFeesValue)
    TextView tvDeliveryFeeValue;
    @BindView(R.id.fragment_checkout_rgOrderDeliveryOption)
    RadioGroup rgOrderDeliveryOption;


    @BindView(R.id.fragment_checkout_rbPickupOrDineIn)
    RadioButton bPickupOrDineIn;

    @BindView(R.id.fragment_checkout_rbDeliver)
    RadioButton rbDeliver;

    @BindView(R.id.swCultery)
    Switch swCultery;

    @BindView(R.id.lview)
    View lview;

    @BindView(R.id.lnSwitchDelivery)
    LinearLayout lnSwitchDelivery;


    private CheckoutItemsAdapter checkoutItemsAdapter;
    private Activity mActivity;
    private AppUtils appUtils;
    private List<CartItemModel> checkoutItemList;
    private boolean isEditModeOn=false;
    private List<CartItemModel> list;
    private String merchantId;
    private double deliveryFees = 0d;
    private double minimumOrder = 0d;
    private boolean deliveryAvailable;
    private UserOperations userOperations;
    private String address;
    private ArrayList<FilterModel> deliveryAreas;
    String swCulteryValue = "0"  ;
    public CheckoutFragment() {
        // Required empty public constructor
    }


    public static CheckoutFragment newInstance(ArrayList<CartItemModel> cartItems,String merchantId, String deliveryFees, String minimumOrder, Boolean deliveryAvailable, ArrayList<FilterModel> deliveryAreas) {
        CheckoutFragment fragment = new CheckoutFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(CART_ITEMS, cartItems);
        args.putString(MERCHANT_ID,merchantId);
        args.putString(DELIVERY_FEES, deliveryFees);
        args.putString(MINIMUM_ORDER, minimumOrder);
        args.putBoolean(DELIVERY_AVAILABLE, deliveryAvailable);
        args.putParcelableArrayList(DELIVERY_AREAS, deliveryAreas);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            checkoutItemList = getArguments().getParcelableArrayList(CART_ITEMS);
            merchantId = getArguments().getString(MERCHANT_ID, "");
            deliveryFees = Double.valueOf(getArguments().getString(DELIVERY_FEES, "0.0"));
            minimumOrder = Double.valueOf(getArguments().getString(MINIMUM_ORDER, "0.0"));
            deliveryAvailable = getArguments().getBoolean(DELIVERY_AVAILABLE, false);
            deliveryAreas = getArguments().getParcelableArrayList(DELIVERY_AREAS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_checkout, container, false);
        ButterKnife.bind(this, view);
        mActivity=getActivity();
        appUtils=AppUtils.getInstance();

        rgOrderDeliveryOption.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                final boolean deliveryOptionSelected = isDeliveryOptionSelected();
                updateBalance(deliveryOptionSelected);
                showDeliveryFees(deliveryOptionSelected);

            }
        });


        swCultery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked)
                    swCulteryValue = "1";
                 else
                    swCulteryValue = "0";
            }
        });

        setUpRecyclerView();
        updateDeliveryFee();
        showOrderDeliveryOptions(deliveryAvailable);
        updateBalance(isDeliveryOptionSelected());

        appUtils.trackScreen(Constants.CHECKOUT_SCREEN_NAME,mActivity);
        userOperations = new UserOperations(AppSharedPrefs.getInstance(mActivity));

        return view;
    }

    private void showDeliveryFees(boolean deliveryOptionSelected) {
        if (deliveryOptionSelected) {
            llDeliveryFees.setVisibility(View.VISIBLE);
            lnSwitchDelivery.setVisibility(View.VISIBLE);
            lview.setVisibility(View.VISIBLE);

        }
        else {
            llDeliveryFees.setVisibility(View.GONE);
            lnSwitchDelivery.setVisibility(View.GONE);
            lview.setVisibility(View.GONE);
        }
    }

    private void showOrderDeliveryOptions(boolean deliveryAvailable){
        if(deliveryAvailable)
            rgOrderDeliveryOption.setVisibility(View.VISIBLE);
        else
            rgOrderDeliveryOption.setVisibility(View.GONE);
    }

    /**
     * method to setup recycler view
     */
    private void setUpRecyclerView(){
        list=new ArrayList<>();
        list.addAll(checkoutItemList);
        List<DATum> taxList=((RestaurantDetailActivity)mActivity).getTaxList();
        double totalPrice = 0.0;
        for (int i = 0; i < checkoutItemList.size(); i++) {
            double dealPrice = checkoutItemList.get(i).getItemQuantity() * Double.parseDouble(checkoutItemList.get(i).getItemPrice());
            totalPrice += dealPrice;
        }

        if (taxList!=null)
        for (DATum daTum:taxList){
            CartItemModel cartItemModel1=new CartItemModel();
            cartItemModel1.setItemName(daTum.getTaxName());
            double percentageDisc= Double.parseDouble(daTum.getTaxPercentage());
            cartItemModel1.setItemPrice(String.valueOf(totalPrice*(percentageDisc/100)));
            cartItemModel1.setViewType(2);
            cartItemModel1.setPercentageTax(percentageDisc);
            if (daTum.getTaxStatus().equals("1")){
                list.add(cartItemModel1);
            }
        }

        checkoutItemsAdapter=new CheckoutItemsAdapter(list,mActivity);
        rvCartItems.setLayoutManager(new LinearLayoutManager(mActivity));
        rvCartItems.setAdapter(checkoutItemsAdapter);
        rvCartItems.setNestedScrollingEnabled(false);

        checkoutItemsAdapter.setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onRecyclerViewItemClick(View view, int position) {
                switch (view.getId()){
                    case R.id.iv_plus:
                        int itemLeft=checkoutItemList.get(position).getItemLeft();
                        int quant=checkoutItemList.get(position).getItemQuantity();
                        if (quant<itemLeft) {
                            quant += 1;
                            checkoutItemList.get(position).setItemQuantity(quant);
                            updateTaxPrice();
                            //checkoutItemsAdapter.notifyItemChanged(position);
                            checkoutItemsAdapter.notifyDataSetChanged();
                            updateBalance(isDeliveryOptionSelected());
                            //decrease left count
                            ((RestaurantDetailActivity)mActivity).increaseDecreaseLeftCount(2,checkoutItemList.get(position).getDealId());

                        }
                        else {
                            appUtils.showSnackBar(mActivity.findViewById(android.R.id.content),getString(R.string.no_more_items_left));
                        }
                        break;
                    case R.id.iv_minus:
                        int quantity=checkoutItemList.get(position).getItemQuantity();
                        if (quantity>1){
                            quantity-=1;
                            checkoutItemList.get(position).setItemQuantity(quantity);
                            //increase left count
                            ((RestaurantDetailActivity)mActivity).increaseDecreaseLeftCount(1,checkoutItemList.get(position).getDealId());
                        }
                        updateTaxPrice();
                        //checkoutItemsAdapter.notifyItemChanged(position);
                        checkoutItemsAdapter.notifyDataSetChanged();
                        updateBalance(isDeliveryOptionSelected());
                        break;
                    case R.id.rl_delete:
                        ((RestaurantDetailActivity)mActivity).removedFromCard(checkoutItemList.get(position).getDealId());
                        list.remove(position);
                        //checkoutItemsAdapter.notifyItemRemoved(position);
                        //checkoutItemsAdapter.notifyItemRangeChanged(position,1);
                        updateTaxPrice();
                        TransitionManager.beginDelayedTransition(svItems);
                        checkoutItemsAdapter.notifyDataSetChanged();
                        updateBalance(isDeliveryOptionSelected());
                        int count=0;
                        for (int i=0;i<list.size();i++){
                            if (list.get(i).getViewType()==1){
                                count++;
                            }
                            break;
                        }
                        if (count==0){
                            mActivity.onBackPressed();
                        }
                        break;
                }
            }

            @Override
            public void onRecyclerViewItemLongClick(View view, int position) {

            }
        });
    }

    private boolean isDeliveryOptionSelected() {
        switch (rgOrderDeliveryOption.getCheckedRadioButtonId()) {
            case R.id.fragment_checkout_rbDeliver:
                return true;
            default:
                return false;
        }
    }

    /**
     * this method updates the price value of the taxes when any quantity changes
     */
    private void updateTaxPrice(){
        double totalPrice = 0.0;
        for (int i = 0; i < checkoutItemList.size(); i++) {
            double dealPrice = checkoutItemList.get(i).getItemQuantity() * Double.parseDouble(checkoutItemList.get(i).getItemPrice());
            totalPrice += dealPrice;
        }
        for (CartItemModel cartItemModel:list) {
            if (cartItemModel.getViewType() == 2) {
                double percentageDisc = cartItemModel.getPercentageTax();
                cartItemModel.setItemPrice(String.valueOf(totalPrice * (percentageDisc / 100)));
            }
        }
    }

    /**
     * this method is to update total balance
     */
    private void updateBalance(boolean includeDeliveryFee) {
        double totalPrice = 0.0;
        try {
            for (int i = 0; i < list.size(); i++) {
                double dealPrice;
                if (list.get(i).getViewType() == 1) {       //1 for deal and 2 for tax
                    dealPrice = list.get(i).getItemQuantity() * Double.parseDouble(list.get(i).getItemPrice());
                } else {
                    dealPrice = Double.parseDouble(list.get(i).getItemPrice());
                }
                totalPrice += dealPrice;
            }
            if (includeDeliveryFee)
                totalPrice += deliveryFees;
        } catch (NumberFormatException ne) {
            ne.printStackTrace();
        }
        String totPrice = String.format(Locale.US, "%.2f", totalPrice) + " " + getString(R.string.currency);
        tvTotalPrice.setText(totPrice);

    }

    private void updateDeliveryFee(){
        if(deliveryFees>0){
            String deliveryFee = String.format(Locale.US, "%.2f", deliveryFees) + " " + getString(R.string.currency);
            tvDeliveryFeeValue.setText(deliveryFee);
        }else{
            llDeliveryFees.setVisibility(View.GONE);
        }
    }

    private double getTotalPrice(){
        double totalPrice = 0.0;
        try {
            for (int i = 0; i < list.size(); i++) {
                double dealPrice;
                if (list.get(i).getViewType() == 1) {       //1 for deal and 2 for tax
                    dealPrice = list.get(i).getItemQuantity() * Double.parseDouble(list.get(i).getItemPrice());
                } else {
                    dealPrice = Double.parseDouble(list.get(i).getItemPrice());
                }
                totalPrice += dealPrice;
            }
            totalPrice += deliveryFees;
        }
        catch (NumberFormatException ne){
            ne.printStackTrace();
        }

        return totalPrice;
    }

    @OnClick({R.id.tv_edit, R.id.iv_down, R.id.tv_add_card, R.id.btn_order_food,R.id.rl_checkout_fragment})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_edit:
                if (checkoutItemList.size()>0){
                    if (!isEditModeOn){
                        isEditModeOn=true;
                        tvEdit.setText(R.string.done);
                        for (CartItemModel cartItemModel:checkoutItemList){
                            cartItemModel.setEditModeOn(true);
                        }
                        checkoutItemsAdapter.notifyDataSetChanged();
                    }else {
                        isEditModeOn=false;
                        tvEdit.setText(getString(R.string.edit));
                        for (CartItemModel cartItemModel:checkoutItemList){
                            cartItemModel.setEditModeOn(false);
                        }
                        checkoutItemsAdapter.notifyDataSetChanged();
                        ((RestaurantDetailActivity)mActivity).setCheckOutItemList((ArrayList<CartItemModel>) checkoutItemList);
                    }
                }
                break;
            case R.id.iv_down:
                if (isEditModeOn){
                    appUtils.showSnackBar(mActivity.findViewById(android.R.id.content),getString(R.string.edit_ur_order_first));
                }
                else {
                    for (CartItemModel cartItemModel:checkoutItemList){
                        cartItemModel.setEditModeOn(false);
                    }
                    mActivity.onBackPressed();
                }
                break;
            case R.id.tv_add_card:
                if (AppSharedPrefs.getInstance(mActivity).getBoolean(AppSharedPrefs.PREF_KEY.IS_LOGIN,false)){
                    Intent intent=new Intent(mActivity, PaymentDetailsActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent =new Intent(mActivity, RegisterActivity.class);
                    intent.putExtra(Constants.FROM_CHECKOUT_KEY,true);
                    startActivity(intent);
                }
                break;
            case R.id.btn_order_food:
                if (isEditModeOn) {
                    appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.edit_ur_order_first));
                } else {
                    selectAddresses();
                }
                break;
            case R.id.rl_checkout_fragment:
                break;
        }
    }

    private void selectAddresses() {
        if (AppSharedPrefs.getInstance(mActivity).getBoolean(AppSharedPrefs.PREF_KEY.IS_LOGIN, false)) {
            if (isDeliveryOptionSelected()) {
                if (getTotalPrice() < minimumOrder) {
                    appUtils.showAlertDialog(getContext(), getString(R.string.app_name), getString(R.string.minimum_order_not_met, minimumOrder, "AED")
                            , getString(R.string.ok), "",
                            new DialogButtonClickListener() {
                                @Override
                                public void positiveButtonClick() {
                                    //do nothing
                                }

                                @Override
                                public void negativeButtonClick() {
                                    //do nothing
                                }
                            });
                } else {
                    startOrderDeliveryProcess();
                }
            } else {
                startOderPickupDineInProcess();
            }
        } else {
            showLoginDialog();
        }
    }

    private void startOrderDeliveryProcess(){
        Intent intent = new Intent(getActivity(), AddressListActivity.class);
        intent.putExtra(AddressListActivity.OPENED_FROM_CHECKOUT, true);
        startActivityForResult(intent, SELECT_DELIVERY_ADDRESS);
    }

    private void startOderPickupDineInProcess(){
        if(userOperations.isBillingAddressValid()) {
            showPickupOrDineInDialog();
            address = AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.ADDRESS, "");
        }else{
            setBillingAddress(null);
        }
    }

    private void showLoginDialog(){
        appUtils.showAlertDialog(mActivity, "", getString(R.string.login_signup_to_continue),
                getString(R.string.ok), getString(R.string.cancel), new DialogButtonClickListener() {
                    @Override
                    public void positiveButtonClick() {
                        Intent intent = new Intent(mActivity, RegisterActivity.class);
                        intent.putExtra(Constants.FROM_CHECKOUT_KEY, true);
                        startActivity(intent);
                    }

                    @Override
                    public void negativeButtonClick() {

                    }
                });
    }

    private void showAddBillingAddressDialog() {
        appUtils.showAlertDialog(mActivity, "", getString(R.string.enter_ur_billing_address), getString(R.string.ok), getString(R.string.cancel), new DialogButtonClickListener() {
            @Override
            public void positiveButtonClick() {
                Intent intent = new Intent(mActivity, DeliverInfoActivity.class);
                startActivity(intent);

            }

            @Override
            public void negativeButtonClick() {

            }
        });
    }

    private void showSetBillingAddressSameAsShippingAddress(final AddressModel shippingAddress) {
        appUtils.showAlertDialog(mActivity, getString(R.string.app_name), getString(R.string.billing_address_same_as_shipping_address), getString(R.string.ok), getString(R.string.cancel), new DialogButtonClickListener() {
            @Override
            public void positiveButtonClick() {
                final BillingAddressModel billingAddressModel =
                        BillingAddressModel.builder()
                                .address(shippingAddress.getAddress())
                                .cityName(shippingAddress.getCityName())
                                .countryName("United Arab Emirates")
                                .stateName(shippingAddress.getCityName())
                                .isoCode("ARE")
                                .postalCode("000000")
                                .build();
                userOperations.saveBillingAddress(billingAddressModel);
                if (appUtils.isInternetOn(mActivity)) {
                    hitApiForCardsList();
                } else {
                    appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.internet_offline));
                }

            }

            @Override
            public void negativeButtonClick() {
                Intent intent = new Intent(mActivity, DeliverInfoActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setBillingAddress(AddressModel shippingAddress){
        if(shippingAddress==null){
            showAddBillingAddressDialog();
        }else{
            if(!userOperations.isBillingAddressValid()) {
                showSetBillingAddressSameAsShippingAddress(shippingAddress);
            }else{
                hitApiForCardsList();
            }
        }
    }

    private void showPickupOrDineInDialog(){
        String pickUpDeadLine = getString(R.string.please_note_the_pickup, "time");
        if (!list.isEmpty()) {
            pickUpDeadLine = getString(R.string.please_note_the_pickup, appUtils.parseDateToTime(list.get(0).getEndTime()));
//                    pickUpDeadLine = pickUpDeadLine.replace("time", appUtils.parseDateToTime(list.get(0).getEndTime()));
        }
        appUtils.showAlertDialog(mActivity, getString(R.string.review_pickup_title), pickUpDeadLine,
                getString(R.string.ok), getString(R.string.cancel), new DialogButtonClickListener() {
                    @Override
                    public void positiveButtonClick() {
                        if (appUtils.isInternetOn(mActivity)) {
                            hitApiForCardsList();
                        } else {
                            appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.internet_offline));
                        }
                    }

                    @Override
                    public void negativeButtonClick() {

                    }
                });
    }

    private String getDeliveryType(){
        return isDeliveryOptionSelected()? DeliveryType.DELIVERY.getValue():DeliveryType.PICKUP_DINE_IN.getValue();
    }

    private void pickupOrDineIn() {
//        if (AppSharedPrefs.getInstance(mActivity).getBoolean(AppSharedPrefs.PREF_KEY.IS_LOGIN, false)) {
//            if (AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.ADDRESS,"").equals("") ||
//                    AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.USER_MOBILE,"").equals("") ||
//                    AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.POSTAL_CODE,"").equals("")||
//                    AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.COUNTRY_NAME,"").equals("")||
//                    AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.STATE_NAME,"").equals("")||
//                    AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.CITY_NAME,"").equals("")){
//
//                appUtils.showAlertDialog(mActivity, "", getString(R.string.enter_ur_billing_address), getString(R.string.ok), getString(R.string.cancel), new DialogButtonClickListener() {
//                    @Override
//                    public void positiveButtonClick() {
//                        Intent intent=new Intent(mActivity, DeliverInfoActivity.class);
//                        startActivity(intent);
//
//                    }
//
//                    @Override
//                    public void negativeButtonClick() {
//
//                    }
//                });
//
//            }
//            else {
//                if (isEditModeOn){
//                    appUtils.showSnackBar(mActivity.findViewById(android.R.id.content),getString(R.string.edit_ur_order_first));
//                }else {
//            if (getTotalPrice() > 0) {
                //openPaymentPage();
//                String pickUpDeadLine = getString(R.string.please_note_the_pickup, "time");
//                if (!list.isEmpty()) {
//                    pickUpDeadLine = getString(R.string.please_note_the_pickup, appUtils.parseDateToTime(list.get(0).getEndTime()));
////                    pickUpDeadLine = pickUpDeadLine.replace("time", appUtils.parseDateToTime(list.get(0).getEndTime()));
//                }
//                appUtils.showAlertDialog(mActivity, getString(R.string.review_pickup_title), pickUpDeadLine,
//                        getString(R.string.ok), getString(R.string.cancel), new DialogButtonClickListener() {
//                            @Override
//                            public void positiveButtonClick() {
//                                if (appUtils.isInternetOn(mActivity)) {
//                                    hitApiForCardsList();
//                                } else {
//                                    appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.internet_offline));
//                                }
//                            }
//
//                            @Override
//                            public void negativeButtonClick() {
//
//                            }
//                        });

//            } else {
//                appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.amount_more_then_zero));
//            }
//                }
//            }
//        } else {
//            appUtils.showAlertDialog(mActivity, "", getString(R.string.login_signup_to_continue),
//                    getString(R.string.ok), getString(R.string.cancel), new DialogButtonClickListener() {
//                        @Override
//                        public void positiveButtonClick() {
//                            Intent intent = new Intent(mActivity, RegisterActivity.class);
//                            intent.putExtra(Constants.FROM_CHECKOUT_KEY, true);
//                            startActivity(intent);
//                        }
//
//                        @Override
//                        public void negativeButtonClick() {
//
//                        }
//                    });
//        }
    }

    /**
     * method to open payment
     */
    private void openPaymentPageForSavedCard(String token,String tokenCustomerEmail,String tokenCustomerPwd){
        //String myDemoSecretKey="4QfQv0ewOVN7t4s4N2nOsGSEBgF6ijhm9wdEwsaloYsS2UWQWkEp1w4CNKJ530sdxGS383px1Rp4MmUIGz8I6ckTX3Z03seA86Eh";
        //String myDemoEmail="shivam.yadav@appinventiv.com";

        Intent in = new
            Intent(mActivity, PayTabActivity.class);
        if(BuildConfig.DEBUG){
            in.putExtra("pt_secret_key",Constants.PAYTABS_SECRET_KEY_TEST);
            in.putExtra("pt_merchant_email",Constants.PAYTABS_EMAIL_TEST);
        }else{
            in.putExtra("pt_merchant_email",Constants.PAYTABS_EMAIL);
            in.putExtra("pt_secret_key",Constants.PAYTABS_SECRET_KEY);
        }
    //Add your Secret Key Here
        in.putExtra("pt_transaction_title", AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.USER_NAME,""));
        in.putExtra("pt_amount", String.format(Locale.US,"%.2f",getTotalPrice()));
        in.putExtra("pt_currency_code", "AED");

    //Use Standard 3 character ISO
        in.putExtra("pt_shared_prefs_name", Constants.VALUE_SHAREDPREF_NAME);
        in.putExtra("pt_customer_email", AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.USER_EMAIL,""));
        in.putExtra("pt_customer_phone_number",AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.USER_MOBILE,""));
        //in.putExtra("pt_customer_phone_number","8802895752");
        in.putExtra("pt_order_id", "1234567");
        StringBuilder prodNames=new StringBuilder();
        for (int i=0;i<checkoutItemList.size();i++){
            prodNames.append(checkoutItemList.get(i).getItemLeft());
            if (i!=checkoutItemList.size()-1)
            prodNames.append(",");
        }
        in.putExtra("pt_product_name", prodNames.toString());
        in.putExtra("pt_timeout_in_seconds", "300");
    //Optional
    //Billing Address
        in.putExtra("pt_address_billing", AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.ADDRESS,""));
        in.putExtra("pt_city_billing",  AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.CITY_NAME,""));
        in.putExtra("pt_state_billing",  AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.STATE_NAME,""));
        in.putExtra("pt_country_billing",  AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.ISO_CODE,""));
        in.putExtra("pt_postal_code_billing",  AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.POSTAL_CODE,""));
    //Put Country Phone code if Postal code not available '00973'
    //Shipping Address
        in.putExtra("pt_address_shipping", AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.ADDRESS,""));
        in.putExtra("pt_city_shipping", AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.CITY_NAME,""));
        in.putExtra("pt_state_shipping", AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.STATE_NAME,""));
        in.putExtra("pt_country_shipping", AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.ISO_CODE,""));
        in.putExtra("pt_postal_code_shipping", AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.POSTAL_CODE,""));
    //Put Country Phone code if Postal code not available '00973'
        in.putExtra("pt_is_tokenization", "TRUE");
        in.putExtra("pt_is_existing_customer", "yess");

    //Pass empty values to check
        in.putExtra("pt_pt_token",token);
        in.putExtra("pt_customer_email",tokenCustomerEmail);
        in.putExtra("pt_customer_password",tokenCustomerPwd);
        int requestCode = 0;
        this.startActivityForResult(in, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String cardName="",cardNo="";
        switch (requestCode){
            case 0:
                SharedPreferences shared_prefs = mActivity.getSharedPreferences(Constants.VALUE_SHAREDPREF_NAME,MODE_PRIVATE);

                String pt_response_code =shared_prefs.getString("pt_response_code","");
                String pt_transaction_id = shared_prefs.getString("pt_transaction_id","");
                String token=shared_prefs.getString("pt_token","");
                String pt_token_customer_email=shared_prefs.getString("pt_token_customer_email","");
                String pt_token_customer_password=shared_prefs.getString("pt_token_customer_password","");
                shared_prefs.edit().clear().apply();
                switch (pt_response_code) {
                    case "100":
                        hitApiForPlacingOrder(pt_transaction_id, cardNo, cardName, token, pt_token_customer_email, pt_token_customer_password,
                                getDeliveryType(), address, String.valueOf(deliveryFees),swCulteryValue);
                        break;
                    case "481":
                        appUtils.showAlertDialog(mActivity, getString(R.string.error), getString(R.string.transaction_error_481), getString(R.string.okay), "", new DialogButtonClickListener() {
                            @Override
                            public void positiveButtonClick() {
                            }

                            @Override
                            public void negativeButtonClick() {
                            }
                        });
                        break;
                    default:
                        appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.payment_not_successful));
                        break;
                }

                break;
            case Constants.ADD_CARD_REQ_CODE:
                if (resultCode==Constants.ADD_CARD_REQ_CODE){
                    cardName=data.getStringExtra(Constants.CARD_NAME_KEY);
                    cardNo=data.getStringExtra(Constants.CARD_NO_KEY);
                    SharedPreferences shared_prefs1 = mActivity.getSharedPreferences(Constants.VALUE_SHAREDPREF_NAME,MODE_PRIVATE);

                    String pt_response_code1 =shared_prefs1.getString("pt_response_code","");
                    String pt_transaction_id1 = shared_prefs1.getString("pt_transaction_id","");
                    String token1=shared_prefs1.getString("pt_token","");
                    String pt_token_customer_email1=shared_prefs1.getString("pt_token_customer_email","");
                    String pt_token_customer_password1=shared_prefs1.getString("pt_token_customer_password","");
                    shared_prefs1.edit().clear().apply();
                    //Toast.makeText(mActivity,"PayTabs Response Code : " +pt_response_code,Toast.LENGTH_LONG).show();
                    //Toast.makeText(mActivity,"Paytabs transaction ID after payment : " +pt_transaction_id, Toast.LENGTH_LONG).show();
                    switch (pt_response_code1) {
                        case "100":
                            hitApiForPlacingOrder(pt_transaction_id1, cardNo, cardName, token1, pt_token_customer_email1, pt_token_customer_password1,
                                    getDeliveryType(), address, String.valueOf(deliveryFees) , swCulteryValue);
                            break;
                        case "481":
                            appUtils.showAlertDialog(mActivity, getString(R.string.error), getString(R.string.transaction_error_481), getString(R.string.okay), "", null);
                            break;
                        default:
                            appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.payment_not_successful));
                            break;
                    }
                }
                break;
            case SELECT_DELIVERY_ADDRESS:
                if(resultCode== Activity.RESULT_OK){
                   final AddressModel selectedAddress = data.getParcelableExtra(AddressListActivity.SELECTED_ADDRESS);
                    address = selectedAddress.getAddress() + "\n" +
                            selectedAddress.getAreaName() + "\n" +
                            selectedAddress.getCityName() + "\n" +
                            selectedAddress.getNotes() + "\n" +
                            getString(R.string.phone).toUpperCase()+" "+selectedAddress.getPhone();
                   if(isOrderDeliverableInUserArea(selectedAddress)) {
                       setBillingAddress(selectedAddress);
                   }else {
                       showOrderNotDeliverableInSelectedArea();
                   }
                }else{
                    Toast.makeText(getActivity(), getString(R.string.you_have_to_select_address), Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    /**
     * method to hit api for placing order
     * @param transactionId is the id of the transaction
     */
    private void hitApiForPlacingOrder(String transactionId,String cardNo,String cardName,
                                       String token,String tokenCustomerEmail,String tokenCustomerPwd,
                                       String deliveryType, String address, String deliveryFee , String cultry){
        appUtils.showProgressDialog(mActivity,false);

        ApiInterface service= RestApi.createService(ApiInterface.class);
        HashMap<String,String> params =new HashMap<>();
        params.put(ApiKeys.MERCHANT_ID,merchantId);
        JSONArray dealsArray=new JSONArray();
        JSONArray taxArray=new JSONArray();
        for (int i=0;i<list.size();i++){
            if (list.get(i).getViewType()==1){
                JSONObject dealObject=new JSONObject();
                try {
                    dealObject.put("deal_id",list.get(i).getDealId());
                    dealObject.put("item_name",list.get(i).getItemName());
                    dealObject.put("quantity",list.get(i).getItemQuantity());
                    dealObject.put("price",list.get(i).getItemPrice());
                    dealObject.put("deal_end_time",list.get(i).getEndTime());

                    dealsArray.put(dealObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if (list.get(i).getViewType()==2){
                JSONObject taxObject=new JSONObject();
                try {
                    taxObject.put("tax_name",list.get(i).getItemName());
                    taxObject.put("tax_percent",list.get(i).getPercentageTax());
                    taxObject.put("tax_amount",list.get(i).getItemPrice());

                    taxArray.put(taxObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        params.put("deal_data", String.valueOf(dealsArray));
        params.put("total_tax", String.valueOf(taxArray));
        params.put("sub_total",String.format(Locale.US,"%.2f",getTotalPrice()));
        params.put("transaction_id",transactionId);
        params.put("delivery_type", deliveryType);
        params.put("address", address);
        params.put("delivery_charges", deliveryFee);
        params.put("cutlery",cultry);

        if (!cardName.equals("") && !token.equals("")) {
            JSONObject cardObject = new JSONObject();
            try {
                cardObject.put(ApiKeys.CARD_NAME_KEY, cardName);
                cardObject.put(ApiKeys.CARD_NO_KEY, cardNo);
                cardObject.put(ApiKeys.CARD_TOKEN_KEY, token);
                cardObject.put(ApiKeys.EMAIL, tokenCustomerEmail);
                cardObject.put(ApiKeys.PASSWROD, tokenCustomerPwd);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            params.put("card_data", cardObject.toString());
        }
        else {
            params.put("card_data", "");
        }


        Call<ResponseBody> call=service.orderPlaceApi(AppSharedPrefs.getInstance(mActivity).
                getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN,""),appUtils.encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                Log.d("resp",response);
                try {
                    JSONObject mainObject=new JSONObject(response);
                    if (mainObject.getInt(ApiKeys.CODE)==200){
                        Intent intent1 = new Intent(Constants.REFRESH_RECCEIVER_KEY);
                        // refreshing list on home
                        intent1.putExtra("refresh", true);
                        LocalBroadcastManager.getInstance(mActivity).sendBroadcast(intent1);

                        JSONObject dataObject=mainObject.getJSONObject(ApiKeys.DATA);
                            JSONArray dataArray=dataObject.getJSONArray(ApiKeys.ORDER_DATA);
                            JSONObject orderDataObject=null;
                            if (dataArray.length()>0) {
                                orderDataObject = dataArray.getJSONObject(0);
                            }
                            if (orderDataObject!=null) {
                                String orderId = orderDataObject.getString(ApiKeys.ORDER_ID);
                                String shouldRate=orderDataObject.getString(ApiKeys.RATING_KEY);
                                showPaymentSuccessDialog(orderId,shouldRate);
                            }
                    }else {
                        appUtils.showSnackBar(mActivity.findViewById(android.R.id.content),mainObject.optString(ApiKeys.MESSAGE));
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

    /**
     * method to show dialog of successful payment
     * @param orderId
     */
    private void showPaymentSuccessDialog(final String orderId, final String shouldRate){
        final Dialog dialog=new Dialog(mActivity);
        if (dialog.getWindow()!=null)
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_success_payment);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.DIM_AMOUNT_CHANGED);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.9f;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ((TextView)dialog.findViewById(R.id.tv_title)).setText(R.string.payment_success_title);
        ((TextView)dialog.findViewById(R.id.tv_message)).setText(R.string.payment_success_message);

        if (dialog.getWindow()!=null)
        dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimationZoom;
        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                Intent intent = new Intent(mActivity, OrderDetailActivity.class);
                intent.putExtra(Constants.ORDER_ID, orderId);
                intent.putExtra(Constants.SHOULD_RATE_KEY,shouldRate);
                intent.putExtra(Constants.CUSTOMER_NAME,AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.USER_NAME,""));
                startActivity(intent);
                mActivity.finish();
            }
        },2000);
    }

    /**
     * method to hit api for getting cards list
     */
    private void hitApiForCardsList(){
        appUtils.showProgressDialog(mActivity,false);
        ApiInterface service=RestApi.createService(ApiInterface.class);
        Call<ResponseBody> call=service.getCards(AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN,""));
        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                CardsListModel cardsListModel=new Gson().fromJson(response,CardsListModel.class);
                if (cardsListModel.getCODE()==200){
                    if (cardsListModel.getRESULT()!=null){
                        showAddedCardsDialog(cardsListModel.getRESULT(),false);
                    }

                }else if (cardsListModel.getCODE()==207){
                    showAddedCardsDialog(null,true);
                }
                else if (cardsListModel.getCODE()==ApiKeys.UNAUTHORISED_CODE){
                    appUtils.logoutFromApp(mActivity,cardsListModel.getMESSAGE());
                }

            }

            @Override
            public void onSuccessErrorBody(String response) {

            }

            @Override
            public void onFailure() {

            }
        });


//        showAddedCardsDialog(cardsList);
    }

    /**
     * method to show dialog for added cards and ad new
     * @param cardsList
     */
    private void showAddedCardsDialog(final List<RESULT> cardsList, boolean isCardsBlank){
        final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(mActivity);
        bottomSheetDialog.setContentView(R.layout.dialog_checkout_cards);
        LinearLayout root = (LinearLayout)bottomSheetDialog.findViewById(R.id.ll_dialog_main);
         ((View) root.getParent()).setBackgroundColor(ContextCompat.getColor(mActivity,android.R.color.transparent));
        RecyclerView rvCards=(RecyclerView)bottomSheetDialog.findViewById(R.id.rv_cards);
        if (!isCardsBlank && rvCards!=null) {
            rvCards.setVisibility(View.VISIBLE);
            CardsListInDialogAdapter cardsListInDialogAdapter = new CardsListInDialogAdapter(mActivity, cardsList);
            rvCards.setLayoutManager(new LinearLayoutManager(mActivity));
            rvCards.setAdapter(cardsListInDialogAdapter);
            cardsListInDialogAdapter.setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
                @Override
                public void onRecyclerViewItemClick(View view, int position) {
                    String token=cardsList.get(position).getCardToken();
                    String tokenEmail=cardsList.get(position).getTokenCustomerEmail();
                    String tokenPwd=cardsList.get(position).getTokenCustomerPassword();
                    if (token!=null && token.length()>0 && tokenPwd!=null && tokenPwd.length()>0) {
                        openPaymentPageForSavedCard(token, tokenEmail,
                                tokenPwd);
                    }
                    else {
                        appUtils.showSnackBar(mActivity.findViewById(android.R.id.content),getString(R.string.token_failure_msg));
                    }
                    bottomSheetDialog.dismiss();
                }
                @Override
                public void onRecyclerViewItemLongClick(View view, int position) {

                }
            });

        }
        else {
            if (rvCards != null) {
                rvCards.setVisibility(View.GONE);
            }
        }
        bottomSheetDialog.findViewById(R.id.tv_add_new_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataForPaymentWithNewCard();
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });


        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.show();
    }

    /**
     * method to send data for payment when a new card is added.
     */
    private void sendDataForPaymentWithNewCard(){
        //String myDemoSecretKey="4QfQv0ewOVN7t4s4N2nOsGSEBgF6ijhm9wdEwsaloYsS2UWQWkEp1w4CNKJ530sdxGS383px1Rp4MmUIGz8I6ckTX3Z03seA86Eh";
        //String myDemoEmail="shivam.yadav@appinventiv.com";


        Intent in = new
                Intent(mActivity, PayTabActivity.class);
        if(BuildConfig.DEBUG){
            in.putExtra("pt_secret_key",Constants.PAYTABS_SECRET_KEY_TEST);
            in.putExtra("pt_merchant_email",Constants.PAYTABS_EMAIL_TEST);
        }else{
            in.putExtra("pt_merchant_email",Constants.PAYTABS_EMAIL);
            in.putExtra("pt_secret_key",Constants.PAYTABS_SECRET_KEY);
        }
        //Add your Secret Key Here
        in.putExtra("pt_transaction_title", AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.USER_NAME,""));
        in.putExtra("pt_amount", String.format(Locale.US,"%.2f",getTotalPrice()));
        in.putExtra("pt_currency_code", "AED");

        //Use Standard 3 character ISO
        in.putExtra("pt_shared_prefs_name", Constants.VALUE_SHAREDPREF_NAME);
        in.putExtra("pt_customer_email", AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.USER_EMAIL,""));
        in.putExtra("pt_customer_phone_number",AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.USER_MOBILE,""));
        in.putExtra("pt_order_id", "1234567");
        StringBuilder prodNames=new StringBuilder();
        for (int i=0;i<checkoutItemList.size();i++){
            prodNames.append(checkoutItemList.get(i).getItemLeft());
            if (i!=checkoutItemList.size()-1)
                prodNames.append(",");
        }
        in.putExtra("pt_product_name", prodNames.toString());
        in.putExtra("pt_timeout_in_seconds", "300");
        //Optional
        //Billing Address
        in.putExtra("pt_address_billing", AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.ADDRESS,""));
        in.putExtra("pt_city_billing",  AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.CITY_NAME,""));
        in.putExtra("pt_state_billing",  AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.STATE_NAME,""));
        in.putExtra("pt_country_billing",  AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.ISO_CODE,""));
        in.putExtra("pt_postal_code_billing",  AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.POSTAL_CODE,""));
        //Put Country Phone code if Postal code not available '00973'
        //Shipping Address
        in.putExtra("pt_address_shipping", AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.ADDRESS,""));
        in.putExtra("pt_city_shipping", AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.CITY_NAME,""));
        in.putExtra("pt_state_shipping", AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.STATE_NAME,""));
        in.putExtra("pt_country_shipping", AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.ISO_CODE,""));
        in.putExtra("pt_postal_code_shipping", AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.POSTAL_CODE,""));
        //Put Country Phone code if Postal code not available '00973'
        in.putExtra("pt_is_tokenization", "TRUE");
        in.putExtra("pt_is_existing_customer", "no");

//        this.startActivityForResult(in, requestCode);

        Intent intent=new Intent(mActivity, AddCardActivity.class);
        intent.putExtra("paytabs_intent",in);
        this.startActivityForResult(intent,Constants.ADD_CARD_REQ_CODE);

    }

    private boolean isOrderDeliverableInUserArea(AddressModel deliveryAddress){
        for (FilterModel deliveryArea : deliveryAreas) {
            if(deliveryArea.getName().equals(deliveryAddress.getAreaName())){
                return true;
            }
        }
        return false;
    }

    private void showOrderNotDeliverableInSelectedArea(){
        appUtils.showAlertDialog(getContext(), getString(R.string.app_name), getString(R.string.order_not_deliverable_in_selected_area)
                , getString(R.string.ok), "",
                new DialogButtonClickListener() {
                    @Override
                    public void positiveButtonClick() {
                        //do nothing
                    }

                    @Override
                    public void negativeButtonClick() {
                        //do nothing
                    }
                });
    }

}
