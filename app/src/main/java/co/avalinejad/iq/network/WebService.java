package co.avalinejad.iq.network;


import co.avalinejad.iq.component.update.Repo.model.UpdateResult;
import co.avalinejad.iq.model.AppInstall;
import co.avalinejad.iq.model.AppInstallResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface WebService {

//    @GET("f1.jsp")
//    Call<List<Content>> getContentList(@Query("s") String contentType, @Query("t") String category);
//
//    @GET("f1.jsp?s=c")
//    Call<List<Content>> getFavoritedList(@Query("ids") String idSet);
//
//    @GET("http://185.128.80.34:8080/content/f1.jsp?s=t&appName=iq")
//    Call<List<Exam>> getExamList();
//
//    @GET("http://anonymousapp.ir/api/near/{lat}/{lng}/150")
//    Call<List<Earthquake>> getEarthquakeList(@Path("lat") double lat, @Path("lng") double lng);

    @Headers({"Accept: application/json"})
    @POST("http://anonymousapp.ir/api/add-user")
    Call<EmptyResponse> sendUserLocation(@Body RequestBody body);

//    @POST("http://api.l.cheragh.com/campaigns/installed_app/")
//    Call<AppInstallResponse> addAppInstall(@Body AppInstall appInstall);

    @POST("http://api.l.cheragh.com/campaigns/app_activity/")
    Call<AppInstallResponse> addAppInstall(@Body AppInstall appInstall);


    @GET("http://api.l.cheragh.com/campaigns/roja/app_version")
    Call<UpdateResult> getResults(@Query("os") String os,
                                  @Query("appName") String appTypem,
                                  @Query("versionCode") int version);

}
