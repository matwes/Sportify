package matwes.zpi.api;

import matwes.zpi.Common;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Mateusz Wesołowski
 */
public class RestService {
    private static ApiInterface API = null;

    protected RestService() {
    }

    public static ApiInterface getApiInstance() {
        if (API == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Common.URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            API = retrofit.create(ApiInterface.class);
        }
        return API;
    }
}
