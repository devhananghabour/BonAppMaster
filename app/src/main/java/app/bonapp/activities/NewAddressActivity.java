package app.bonapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.app.bonapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.bonapp.constants.ApiKeys;
import app.bonapp.constants.Constants;
import app.bonapp.fragments.ListDialogFragment;
import app.bonapp.models.ApiModel;
import app.bonapp.models.FilterModel;
import app.bonapp.models.user.AddressModel;
import app.bonapp.network.ApiInterface;
import app.bonapp.network.RestApi;
import app.bonapp.utils.AppSharedPrefs;
import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewAddressActivity extends BaseActivity {

    private static final String TAG = NewAddressActivity.class.getName();
    public static final String ADDRESS_LIST = "addressList";


    @BindView(R.id.main_toolbar_toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_news_address_etName)
    EditText etName;
    @BindView(R.id.activity_news_address_etAddress)
    EditText etAddress;
    @BindView(R.id.activity_new_address_tvCity)
    TextView tvCity;
    @BindView(R.id.activity_new_address_tvArea)
    TextView tvArea;
    @BindView(R.id.activity_news_address_etPhone)
    EditText etPhone;
    @BindView(R.id.activity_news_address_etNotes)
    EditText etNotes;

    private ArrayList<ListDialogFragment.DialogList> areas = new ArrayList<>();
    private ArrayList<ListDialogFragment.DialogList> cities = new ArrayList<>();

    private ListDialogFragment areaListDialog;
    private ListDialogFragment cityListDialog;

    private FilterModel selectedArea;
    private FilterModel selectedCity;

    @Override
    int getLayoutResourceId() {
        return R.layout.activity_new_address;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpViews();
        getAreasFromApi();
        getCitiesFromApi();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.add_new_address_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.add_new_address_menu_save:
                if (validateInput()) {
                    saveUserAddress();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void setUpViews() {
        setupToolbar(toolbar, getString(R.string.my_addesses), true);
    }

    @OnClick({R.id.activity_new_address_tvCity, R.id.activity_new_address_tvArea})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.activity_new_address_tvCity:
                showCitiesDialog();
                break;
            case R.id.activity_new_address_tvArea:
                showAreasDialog();
                break;
        }
    }

    private void getAreasFromApi() {
        final String accessToken = AppSharedPrefs.getInstance(this).getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, null);
        RestApi.createService(ApiInterface.class).getAreas(Constants.API_KEY, accessToken)
                .enqueue(new Callback<ApiModel<List<FilterModel>>>() {
                    @Override
                    public void onResponse(Call<ApiModel<List<FilterModel>>> call, Response<ApiModel<List<FilterModel>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            loadArea(response.body().getData());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiModel<List<FilterModel>>> call, Throwable t) {
                        Log.e(TAG, "error fetching areas", t);
                    }
                });
    }

    private void loadArea(List<FilterModel> data) {
        if (data != null && !data.isEmpty()) {
            setSelectedArea(data.get(0));
            areas.clear();
            areas.addAll(data);
        }
    }

    private void setSelectedArea(FilterModel area) {
        selectedArea = area;
        tvArea.setText(area.getName());
    }

    private void getCitiesFromApi() {
        final String accessToken = AppSharedPrefs.getInstance(this).getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, null);
        RestApi.createService(ApiInterface.class).getCities(Constants.API_KEY, accessToken)
                .enqueue(new Callback<ApiModel<List<FilterModel>>>() {
                    @Override
                    public void onResponse(Call<ApiModel<List<FilterModel>>> call, Response<ApiModel<List<FilterModel>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            loadCity(response.body().getData());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiModel<List<FilterModel>>> call, Throwable t) {
                        Log.e(TAG, "error fetching cities", t);
                    }
                });
    }

    private void loadCity(List<FilterModel> data) {
        if (data != null && !data.isEmpty()) {
            setSelectedCity(data.get(0));
            cities.clear();
            cities.addAll(data);
        }
    }

    private void setSelectedCity(FilterModel city) {
        selectedCity = city;
        tvCity.setText(city.getName());
    }

    private void showAreasDialog() {
        if (areaListDialog == null) {
            areaListDialog = ListDialogFragment.newInstance(areas, getString(R.string.select_area));
        }
        areaListDialog.show(getFragmentManager(), null);
        areaListDialog.setOnItemSelectedListener(new ListDialogFragment.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Object object) {
                setSelectedArea((FilterModel) object);
            }
        });
    }

    private void showCitiesDialog() {
        if (cityListDialog == null) {
            cityListDialog = ListDialogFragment.newInstance(cities, getString(R.string.select_city));
        }
        cityListDialog.show(getFragmentManager(), null);
        cityListDialog.setOnItemSelectedListener(new ListDialogFragment.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Object object) {
                setSelectedCity((FilterModel) object);
            }
        });
    }

    private boolean validateInput() {
        if (etName.getText().toString().isEmpty()) {
            showToastMessage("");
            return false;
        } else if (etAddress.getText().toString().isEmpty()) {
            showToastMessage("s");
            return false;
        } else if (etPhone.getText().toString().isEmpty()) {
            showToastMessage("b");
            return false;
        }

        return true;
    }

    private void saveUserAddress() {
        final String accessToken = AppSharedPrefs.getInstance(this).getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, "");
        HashMap<String, String> params = new HashMap<>();
        params.put(ApiKeys.NAME, etName.getText().toString());
        params.put(ApiKeys.ADDRESS, etAddress.getText().toString());
        params.put(ApiKeys.CITY_ID, String.valueOf(selectedCity.getId()));
        params.put(ApiKeys.AREA_ID, String.valueOf(selectedArea.getId()));
        params.put(ApiKeys.PHONE, etPhone.getText().toString());
        params.put(ApiKeys.NOTES, etNotes.getText().toString());
        RestApi.createService(ApiInterface.class).saveUserAddress(Constants.API_KEY, accessToken, params)
                .enqueue(new Callback<ApiModel<List<AddressModel>>>() {
                    @Override
                    public void onResponse(Call<ApiModel<List<AddressModel>>> call, Response<ApiModel<List<AddressModel>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            addingAddressSuccessful(response.body().getData());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiModel<List<AddressModel>>> call, Throwable t) {
                        Log.e(TAG, "error fetching user addresses", t);

                    }
                });
    }

    private void addingAddressSuccessful(List<AddressModel> addresses) {
        if (addresses != null) {
            Intent returnIntent = new Intent();
            returnIntent.putParcelableArrayListExtra(ADDRESS_LIST, new ArrayList<>(addresses));
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
    }

}
