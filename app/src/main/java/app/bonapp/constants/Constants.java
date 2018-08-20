package app.bonapp.constants;

import android.os.Environment;

/**
 * It is constants class which contains all the constants including keys
 * on 28/3/17.
 */

public class Constants {

    public final static long splashTimeout = 2000;
    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final String FOLDER_NAME_IMAGE_CAPTURE = "menu fitness";

    public static final String ISFROM_SPLASH = "isFormSplash";
    public static final String ANDROID_TYPE = "android";
    public static final String DEAL_ID_KEY = "deal_id";
    public static final int DEAL_DETAIL_REQ_CODE = 21;
    public static final String TO = "damirmi@gmail.com";
    public static final String DEAL_POSITION_KEY = "deal_pos_key";
    public static final String DEAL_STATUS_KEY = "deal_status";
    public static final String TOTAL_ITEMS_KEY = "total_items";
    public static final String BEGIN_TIME_KEY = "begin_time";
    public static final String END_TIME_KEY = "end_time";
    public static final String DEAL_TITLE = "deal_title";
    public static final String DEAL_CREATED_DATE_KEY = "deal_created";
    public static final int DEAL_EDIT_RESULT_CODE = 22;
    public static final int DEAL_DEACTIVIATE_RESULT_CODE = 23;
    public static final int DEAL_ACTIVIATE_RESULT_CODE = 24;
    public static final String MERCHANT_ID_KEY = "merch_id";
    public static final String BONAPP_SUPPORT_EMAIL = "save@bonapp.ae";
    public static final String FROM_CHECKOUT_KEY = "from_checkout";
    public static final String IS_CREATE_DEAL = "is_create_deal";
    public static final String VALUE_SHAREDPREF_NAME = "bonapp_shared";
    public static final String ORDER_ID = "order_id";
    public static final int ADD_CARD_REQ_CODE = 3333;
    public static final String CARD_NAME_KEY = "card_name";
    public static final String CARD_NO_KEY = "card_no";
    public static final String RESTRAU_IMAGE_KEY = "rest_image";
    public static final String RESTRAU_NAME_KEY = "rest_name";
    public static final String RESTRAU_ADDRESS_KEY = "rest_address";
    public static final String IS_FROM_NOTIFICATION = "is_notif";
    public static final String SHOULD_RATE_KEY = "should_rate";
    public static final String ACTIVATED_DAL = "1";
    public static final String DEACTIVATED_DAL = "2";
    public static final String PICKUP_TIME_MERCHANT = "pickup_time";
    public static final String REFRESH_RECCEIVER_KEY = "refresh_receiver";
    public static final int CREATE_DEAL_REQ_CODE = 44;

    public static final String CHECKOUT_SCREEN_NAME = "Checkout";
    public static final String MERCHANT_LIST_SCREEN_NAME = "MerchantsListing";
    public static final String MERCHANT_DETAIL_SCREEN_NAME = "MerchantsDetail";
    public static final String IS_FROM_PUSH = "is_from_push";
    public static final String CUSTOMER_NAME = "customer_name";
    public static final String IS_FAVORITE = "is_favorite";


    public static String APP_IMAGE_FOLDER = Environment.getExternalStorageDirectory().toString() + "/BonApp";
    public static String API_KEY="c2VlcnZlX2FwcF9pbnZlbnRpdg";


    public static String basicAuthUsername="c2VlcnZlX2FwcGludmVudGl2";
    public static String basicAuthPassword="c2VlcnZlX2FwcGxpY2F0aW9u";

    public static String MERCHANT_TYPE="1";
    public static String CUSTOMER_TYPE="2";

    public static String PAYTABS_EMAIL ="alice@bonapp.ae";
    public static String PAYTABS_SECRET_KEY="usGRRuLdj9xEdaUkzLgIGF5VYmFZ9lnDN4vFygTSqqxDBrYadfTUUYPTlvSvQg8ElEkTVdMlDjZfLXpYdjS8hI1DdU83ahIwV2pR";

    public static final String PAYTABS_SECRET_KEY_TEST ="i81eqeFXBSIr9xEp90VOde2s9CSUXVff3gutu9q619ybGcBQcwFqLHTUkMOhH4xKk2dPbEVdNupuG1lTy0kjIed202jI6RtqTHg5";
    public static final String PAYTABS_EMAIL_TEST ="mustafeez_post@paytabs.com";


    public static final String initVector="Bon10370app#@*17";
    public static final String SecretKey="%$pro012#@78app7%$pro012#@78bon7";

}
