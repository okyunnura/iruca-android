package jp.co.tagbangers.iruca;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

interface IrucaService {

    @FormUrlEncoded
    @PUT("members/{id}")
    Observable<Void> putStatus(@Path("id") String id, @Field("name") String name, @Field("status") String status);
}
