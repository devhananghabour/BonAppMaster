package app.bonapp.operations;

import app.bonapp.models.user.BillingAddressModel;
import app.bonapp.models.user.UserModel;
import app.bonapp.utils.AppSharedPrefs;

public class UserOperations {

    private AppSharedPrefs prefs;

    public UserOperations(AppSharedPrefs prefs) {
        this.prefs = prefs;
    }

    public void saveUser(UserModel userModel) {
        prefs.putString(AppSharedPrefs.PREF_KEY.USER_NAME, userModel.getUserName());
        prefs.putString(AppSharedPrefs.PREF_KEY.USER_EMAIL, userModel.getEmail());
        prefs.putString(AppSharedPrefs.PREF_KEY.USER_MOBILE, userModel.getMobile());
        prefs.putString(AppSharedPrefs.PREF_KEY.USER_TYPE, String.valueOf(userModel.getUserType()));
        prefs.putString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, userModel.getAccessToken());
        prefs.putString(AppSharedPrefs.PREF_KEY.USER_ID, String.valueOf(userModel.getId()));
        saveBillingAddress(userModel.getBillingAddress());

    }

    public boolean isBillingAddressValid() {
        return !prefs.getString(AppSharedPrefs.PREF_KEY.ADDRESS, "").equals("") &&
                //!prefs.getString(AppSharedPrefs.PREF_KEY.USER_MOBILE, "").equals("") &&
                !prefs.getString(AppSharedPrefs.PREF_KEY.POSTAL_CODE, "").equals("") &&
                !prefs.getString(AppSharedPrefs.PREF_KEY.COUNTRY_NAME, "").equals("") &&
                !prefs.getString(AppSharedPrefs.PREF_KEY.STATE_NAME, "").equals("") &&
                !prefs.getString(AppSharedPrefs.PREF_KEY.CITY_NAME, "").equals("");
    }

    public void saveBillingAddress(BillingAddressModel billingAddressModel) {
        prefs.putString(AppSharedPrefs.PREF_KEY.ADDRESS, billingAddressModel.getAddress());
        prefs.putString(AppSharedPrefs.PREF_KEY.COUNTRY_NAME, billingAddressModel.getCountryName());
        prefs.putString(AppSharedPrefs.PREF_KEY.STATE_NAME, billingAddressModel.getStateName());
        prefs.putString(AppSharedPrefs.PREF_KEY.CITY_NAME, billingAddressModel.getCityName());
        prefs.putString(AppSharedPrefs.PREF_KEY.COUNTRY_ID, billingAddressModel.getCountryId());
        prefs.putString(AppSharedPrefs.PREF_KEY.STATE_ID, billingAddressModel.getStateId());
        prefs.putString(AppSharedPrefs.PREF_KEY.CITY_ID, billingAddressModel.getCityId());
        prefs.putString(AppSharedPrefs.PREF_KEY.SOCIAL_TYPE, billingAddressModel.getSocialType());
        prefs.putString(AppSharedPrefs.PREF_KEY.POSTAL_CODE, billingAddressModel.getPostalCode());
        prefs.putString(AppSharedPrefs.PREF_KEY.ISO_CODE, billingAddressModel.getIsoCode());
    }
}
