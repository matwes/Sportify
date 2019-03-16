package matwes.zpi.api;

import matwes.zpi.Common;
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
            Retrofit retrofit = new Retrofit
                    .Builder()
                    .baseUrl(Common.URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            API = retrofit.create(ApiInterface.class);
        }
        return API;
    }
}
