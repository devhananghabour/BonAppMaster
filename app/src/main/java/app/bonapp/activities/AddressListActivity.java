package app.bonapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.app.bonapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.bonapp.adapters.AddressAdapter;
import app.bonapp.constants.ApiKeys;
import app.bonapp.constants.Constants;
import app.bonapp.customviews.SwipeToDeleteCallback;
import app.bonapp.models.ApiModel;
import app.bonapp.models.user.AddressModel;
import app.bonapp.network.ApiInterface;
import app.bonapp.network.RestApi;
import app.bonapp.utils.AppSharedPrefs;
import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressListActivity extends BaseActivity {

    private static final int ADD_NEW_ADDRESS = 100;
    public static final String SELECTED_ADDRESS = "select_address";
    public static final String OPENED_FROM_CHECKOUT = "opened_from_checkout";

    @BindView(R.id.main_toolbar_toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_address_list_rvAddresses)
    RecyclerView rvAddresses;
    @BindView(R.id.activity_address_list_tvTitle)
    TextView tvAddressListTitle;
    @BindView(R.id.activity_address_list_btnSelectAddress)
    Button btnSelectAddress;

    private final List<AddressModel> addressList = new ArrayList<>();
    private AddressAdapter addressAdapter;
    private AddressModel selectedAddress;

    @Override
    int getLayoutResourceId() {
        return R.layout.activity_address_list;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpViews();
        getAddressListFromApi();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.address_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.address_list_menu_new:
                startActivityForResult(new Intent(this, NewAddressActivity.class), ADD_NEW_ADDRESS);
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void setUpViews() {

        setupToolbar(toolbar, getToolbarTitle(), true);

        if(isActivityOpenedFromCheckout()){
            tvAddressListTitle.setVisibility(View.VISIBLE);
            btnSelectAddress.setVisibility(View.VISIBLE);
        }

        rvAddresses.setLayoutManager(new LinearLayoutManager(this));
        addressAdapter = new AddressAdapter(addressList, isActivityOpenedFromCheckout());
        rvAddresses.setAdapter(addressAdapter);
        addressAdapter.setOnAddressSelectedListener(new AddressAdapter.OnAddressSelectedListener() {
            @Override
            public void onAddressSelected(AddressModel address) {
                selectedAddress = address;
                //returnSelectedAddressToPreviousActivity(address);
            }
        });

        ItemTouchHelper.Callback callback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                super.onSwiped(viewHolder, direction);
                deleteUserAddress(addressList.get(viewHolder.getAdapterPosition()).getId());
                addressAdapter.removeAt(viewHolder.getAdapterPosition());
            }
        };

        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rvAddresses);
    }


    @OnClick(R.id.activity_address_list_btnSelectAddress)
    public void onClick(View view) {
        if (selectedAddress != null) {
            returnSelectedAddressToPreviousActivity(selectedAddress);
        } else {
            showToastMessage(getString(R.string.you_have_to_select_address));
        }
    }

    private void returnSelectedAddressToPreviousActivity(AddressModel selectedAddress) {
        Intent intent = new Intent();
        intent.putExtra(SELECTED_ADDRESS, selectedAddress);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void getAddressListFromApi() {
        final String accessToken = AppSharedPrefs.getInstance(this).
                getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, null);
        RestApi.createService(ApiInterface.class).getUserAddresses(Constants.API_KEY, accessToken)
                .enqueue(new Callback<ApiModel<List<AddressModel>>>() {
                    @Override
                    public void onResponse(Call<ApiModel<List<AddressModel>>> call, Response<ApiModel<List<AddressModel>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            loadAddresses(response.body().getData());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiModel<List<AddressModel>>> call, Throwable t) {
                        Toast.makeText(AddressListActivity.this, getString(R.string.error_loading_addresses), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void deleteUserAddress(String addressId) {
        final String accessToken = AppSharedPrefs.getInstance(this).
                getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, null);
        final HashMap<String, String> params = new HashMap<>();
        params.put(ApiKeys.ADDRESS_ID, addressId);
        RestApi.createService(ApiInterface.class).deleteUserAddress(Constants.API_KEY, accessToken, params)
                .enqueue(new Callback<ApiModel<List<AddressModel>>>() {
                    @Override
                    public void onResponse(Call<ApiModel<List<AddressModel>>> call, Response<ApiModel<List<AddressModel>>> response) {
                        //Do nothing
                    }

                    @Override
                    public void onFailure(Call<ApiModel<List<AddressModel>>> call, Throwable t) {
                        //Do nothing
                    }
                });
    }

    private void loadAddresses(List<AddressModel> addresses) {
        addressList.clear();
        addressList.addAll(addresses);
        addressAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_NEW_ADDRESS) {
            if (resultCode == Activity.RESULT_OK && data.getExtras() != null) {
                loadAddresses(data.getExtras().<AddressModel>getParcelableArrayList(NewAddressActivity.ADDRESS_LIST));
            }
        }
    }

    private String getToolbarTitle(){
        if(isActivityOpenedFromCheckout()){
            return getString(R.string.deliver_to);
        }else{
            return getString(R.string.my_addesses);
        }
    }

    private boolean isActivityOpenedFromCheckout(){
        return getIntent().getExtras() != null && getIntent().getExtras().getBoolean(OPENED_FROM_CHECKOUT, false);
    }

}
