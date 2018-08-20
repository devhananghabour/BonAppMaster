package app.bonapp.network;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.app.bonapp.R;

import app.bonapp.constants.Constants;
import app.bonapp.utils.AppUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by admin1 on 21/9/17.
 */

public class ApiCall {

    private static ApiCall apiCall;
    private String LOG_TAG = "LOG_TAG";

    public static ApiCall getInstance() {
        if (apiCall == null) {
            apiCall = new ApiCall();
        }
        return apiCall;
    }


    /**
     * This class is used to hit the service and handle their responses
     *
     * @param context  - Context of the class
     * @param bodyCall - retrofit call
     */
    public void hitService(final Context context, Call<ResponseBody> bodyCall, final NetworkListener networkListener) {
        final AppUtils appUtils = AppUtils.getInstance();
        bodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                appUtils.hideProgressDialog((Activity) context);
                try {
                    if (response != null) {
                        ResponseBody responseBody=response.body();
                        if (responseBody != null) {
                            String encryptResp = responseBody.string();
                           // String decresp = appUtils.decryptMsg(encryptResp.getBytes(), appUtils.generateKey(Constants.SecretKey));
                            networkListener.onSuccess(response.code(), encryptResp.trim());
                        } else if (response.errorBody() != null) {
                            try {
                                networkListener.onSuccessErrorBody(response.errorBody().string());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        appUtils.showToast(context, context.getString(R.string.txt_something_went_wrong));
                    }
                } catch (Exception e) {
                    Log.d(LOG_TAG, e.getMessage()!=null?e.getMessage():"error calling api");
                    //appUtils.showToast(context, context.getString(R.string.txt_something_went_wrong));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                appUtils.hideProgressDialog((Activity) context);
                //appUtils.showToast(context, context.getString(R.string.txt_something_went_wrong));
                networkListener.onFailure();
            }
        });
    }

}
