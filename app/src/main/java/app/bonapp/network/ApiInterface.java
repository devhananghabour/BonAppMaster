package app.bonapp.network;


import java.util.HashMap;
import java.util.List;

import app.bonapp.models.ApiModel;
import app.bonapp.models.FilterModel;
import app.bonapp.models.user.AddressModel;
import okhttp3.Callback;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;


public interface ApiInterface {


    /* @Multipart
     @POST("/Api/Registration")
     Call<ResponseBody> signup(@Header("AuthToken") String header, @Part MultipartBody.Part part, @Part MultipartBody.Part part2, @Part MultipartBody.Part part3, @PartMap HashMap<String, RequestBody> map);
 */
    @FormUrlEncoded
    @POST("api/merchant-deal-list")
    Call<ResponseBody> merchant_deal_list(@Header("api-key") String header, @Header("access-token") String accessToken,
                                          @FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("api/signup")
    Call<ResponseBody> signup(@Header("api-key") String header, @FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("api/login")
    Call<ResponseBody> login(@Header("api-key") String header, @FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("api/social-action")
    Call<ResponseBody> social_login(@Header("api-key") String header, @FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("api/myDeals")
    Call<ResponseBody> merchantDeals(@Header("access-token") String header, @FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("api/add-deals")
    Call<ResponseBody> createDeal(@Header("access-token") String header, @FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("api/deal-view")
    Call<ResponseBody> viewDeal(@Header("api-key") String apiKey, @FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("api/deactive-deal")
    Call<ResponseBody> deactivateDeal(@Header("access-token") String accessToken, @FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("api/edit-deal")
    Call<ResponseBody> editDeal(@Header("access-token") String accessToken, @FieldMap HashMap<String, String> map);

    @POST("api/logout")
    Call<ResponseBody> logout(@Header("access-token") String accessToken);

    @FormUrlEncoded
    @POST("api/merchant_detail")
    Call<ResponseBody> merchantDetail(@Header("api-key") String apiKey, @FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("api/forgetpassword")
    Call<ResponseBody> forgetPassword(@Header("api-key") String apiKey, @FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("api/change-password")
    Call<ResponseBody> changePassword(@Header("access-token") String accessToken, @FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("api/change-email")
    Call<ResponseBody> changeEmail(@Header("access-token") String accessToken, @FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("api/edit-profile")
    Call<ResponseBody> editProfile(@Header("access-token") String accessToken, @FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("api/add-address")
    Call<ResponseBody> addAdress(@Header("access-token") String accessToken, @FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("api/customer-order-placed")
    Call<ResponseBody> orderPlaceApi(@Header("access-token") String accessToken, @FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("api/merchant-order-list")
    Call<ResponseBody> orderHistory(@Header("access-token") String accessToken, @FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("api/customer-order-view")
    Call<ResponseBody> orderDetail(@Header("access-token") String accessToken, @FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("api/merchant-stats")
    Call<ResponseBody> merchantStats(@Header("access-token") String accessToken, @FieldMap HashMap<String, String> params);

    @POST("api/tax-list")
    Call<ResponseBody> getTaxList(@Header("api-key") String apiKey);

    @GET("api/profile")
    Call<ResponseBody> getCountryCityStateDropdown(@Header("api-key") String apiKey, @QueryMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("api/Feedback")
    Call<ResponseBody> feedback(@Header("access-token") String accessToken, @FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST("api/Check_name")
    Call<ResponseBody> validateCardName(@Header("access-token") String accessToken, @FieldMap HashMap<String, String> map);

    @GET("api/Card_list")
    Call<ResponseBody> getCards(@Header("access-token") String accessToken);

    @GET("api/Delete_card")
    Call<ResponseBody> deleteCard(@Header("access-token") String accessToken, @QueryMap HashMap<String, String> map);

    @POST("api/add_favorite")
    Call<ResponseBody> addFavorite(@Header("access-token") String accessToken, @Body HashMap<String, String> map);

    @POST("api/delete_favorite")
    Call<ResponseBody> deleteFavorite(@Header("access-token") String accessToken, @Body HashMap<String, String> map);

    @POST("api/get_favorites")
    Call<ResponseBody> getFavorites(@Header("api-key") String apiKey, @Header("access-token") String accessToken, @Body HashMap<String, String> map);

    @POST("api/areas")
    Call<ApiModel<List<FilterModel>>> getAreas(@Header("api-key") String apiKey, @Header("access-token") String accessToken);

    @POST("api/types")
    Call<ApiModel<List<FilterModel>>> getTypes(@Header("api-key") String apiKey, @Header("access-token") String accessToken);

    @POST("api/delivery_types")
    Call<ApiModel<List<FilterModel>>> getDeliveryTypes(@Header("api-key") String apiKey, @Header("access-token") String accessToken);

    @POST("api/addresses")
    Call<ApiModel<List<AddressModel>>> getUserAddresses(@Header("api-key") String apiKey, @Header("access-token") String accessToken);

    @POST("api/cities")
    Call<ApiModel<List<FilterModel>>> getCities(@Header("api-key") String apiKey, @Header("access-token") String accessToken);

    @POST("api/new-address")
    Call<ApiModel<List<AddressModel>>> saveUserAddress(@Header("api-key") String apiKey, @Header("access-token") String accessToken, @Body HashMap<String, String> map);

    @POST("api/delete-address")
    Call<ApiModel<List<AddressModel>>> deleteUserAddress(@Header("api-key") String apiKey, @Header("access-token") String accessToken, @Body HashMap<String, String> map);

    @POST("api/order_status_update")
    Call<ResponseBody> changeDeliveryStatus(@Header("api-key") String apiKey, @Header("access-token") String accessToken, @Body HashMap<String, String> map);

}


