package com.ji.tree.app.tencent;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TencentApi {
    @GET("https://sj.qq.com/myapp/searchAjax.htm?")
    Observable<SearchAppData> search(@Query("kw") String kw);

    @GET("https://mapp.qzone.qq.com/cgi-bin/mapp/mapp_applist?apptype=soft_top&platform=touch")
    Observable<ResponseBody>    softTop(@Query("pageNo") int pageNo, @Query("pageSize") int pageSize);
}
