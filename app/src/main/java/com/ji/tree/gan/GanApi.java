package com.ji.tree.gan;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GanApi {
    @GET("day/history")
    Observable<GanHistoryDate> getHistory();

    @GET("day/{year}/{month}/{day}")
    Observable<GanDailyData> getDailyData(@Path("year") String year, @Path("month") String month, @Path("day") String day);
}
