import jp.arsaga.dataStore.gateway.server.IApiType
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.mock.BehaviorDelegate
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior
import java.util.concurrent.TimeUnit

fun retrofitApiProvider(retrofit: Retrofit): IApiType = MockRetrofit
    .Builder(retrofit)
    .networkBehavior(behavior)
    .build()
    .create(IApiType::class.java)
    .run(::RetrofitApiProvider)

private val behavior = NetworkBehavior.create().apply {
    setDelay(1000, TimeUnit.MILLISECONDS)
    setFailurePercent(0)
    setErrorPercent(0)
}

private class RetrofitApiProvider(
    private val behaviorDelegate: BehaviorDelegate<IApiType>
) : IApiType {

}