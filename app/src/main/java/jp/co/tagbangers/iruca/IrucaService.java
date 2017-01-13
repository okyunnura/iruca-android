package jp.co.tagbangers.iruca;

import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Field;

interface IrucaService {

    @FormUrlEncoded
    @PUT("members/{id}")
    Call<Void> putStatus(@Path("id") String id, @Field("name") String name, @Field("status") String status);
}
