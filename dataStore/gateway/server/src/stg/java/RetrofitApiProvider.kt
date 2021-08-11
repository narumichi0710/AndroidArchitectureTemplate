import jp.arsaga.dataStore.gateway.server.IApiType
import retrofit2.Retrofit

fun retrofitApiProvider(retrofit: Retrofit): IApiType = retrofit
    .create(IApiType::class.java)