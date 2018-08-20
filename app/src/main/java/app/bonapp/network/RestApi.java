package app.bonapp.network;

import android.util.Base64;
import android.util.Log;

import com.app.bonapp.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.instabug.library.okhttplogger.InstabugOkhttpInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import app.bonapp.constants.Constants;
import app.bonapp.models.merchantposts.DATum;
import app.bonapp.utils.CustomDATumDeserializer;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RestApi {

    //public static final String API_BASE_URL = "http://bonappstage.appnationz.com/";
    //public static final String API_BASE_URL = "http://api.bonapp.ae/";         //live
    //public static final String API_BASE_URL="http://staging.bonapp.ae/V2/"; //updated version
    //public static final String API_BASE_URL="http://staging.bonapp.ae/";

    //public static final String API_BASE_URL="http://bonapp-uae-prod.herokuapp.com/V2/"; //with heroku
//    public static final String API_BASE_URL="http://api.bonapp.ae/V2/"; //final updated
    public static final String API_BASE_URL_STAGING = "http://staging.bonapp.ae/V2/";
    public static final String API_BASE_URL         = "https://api.bonapp.ae/V2/";


    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder().readTimeout(30000,
                                                                                            TimeUnit.MILLISECONDS).writeTimeout(30000, TimeUnit.MILLISECONDS);


    private static Retrofit.Builder retrofitBuilder       = new Retrofit.Builder().baseUrl(API_BASE_URL).
            addConverterFactory(GsonConverterFactory.create(buildGson()));
    private static Retrofit.Builder retrofitBuilderGoogle = new Retrofit.Builder().
            baseUrl("https://maps.googleapis.com/maps/api/").addConverterFactory(GsonConverterFactory.create());

   /* public static <S> S createService(Class<S> aClass) {
        Retrofit retrofit = retrofitBuilder.client(httpClient.build()).build();
        return retrofit.create(aClass);
    }*/

    private static Gson buildGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DATum.class, new CustomDATumDeserializer());
        return gsonBuilder.create();
    }

    public static <S> S createGoogleService(Class<S> aClass) {
        Retrofit retrofit = retrofitBuilderGoogle.client(httpClient.build()).build();
        return retrofit.create(aClass);
    }


    public static <S> S createService(Class<S> aClass) {

        if (Constants.basicAuthUsername != null && Constants.basicAuthPassword != null) {
            String credentials = Constants.basicAuthUsername + ":" + Constants.basicAuthPassword;
            final String basic = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            //final String basic="Basic YzJWbGNuWmxYMkZ3Y0dsdWRtVnVkR2wyOmMyVmxjblpsWDJGd2NHeHBZMkYwYVc5dQ==";
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", basic)
                            .method(original.method(), original.body());
                    Request request = requestBuilder.build();
                    Response response = chain.proceed(request);
                    Log.e("Response =", response.message());
                    return response;
                }
            });
        }

        if (BuildConfig.DEBUG) {
            retrofitBuilder.baseUrl(API_BASE_URL_STAGING);
            addNetworkInterceptor(httpClient);
        } else {
            retrofitBuilder.baseUrl(API_BASE_URL);
//            addInstabugInterceptor(httpClient);

        }
        Retrofit retrofit = retrofitBuilder.client(httpClient.build()).build();

        return retrofit.create(aClass);
    }


    private static void addNetworkInterceptor(OkHttpClient.Builder httpClient) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.NONE);
        if (BuildConfig.DEBUG)
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(logging);
    }

    private static void addInstabugInterceptor(OkHttpClient.Builder httpClient) {
        InstabugOkhttpInterceptor instabugOkhttpInterceptor = new InstabugOkhttpInterceptor();
        httpClient.addInterceptor(instabugOkhttpInterceptor);
    }

    public static RequestBody getRequestBody(String params) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), params);
    }

}
