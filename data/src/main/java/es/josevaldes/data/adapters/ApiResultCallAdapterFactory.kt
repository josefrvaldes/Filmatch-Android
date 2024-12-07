package es.josevaldes.data.adapters

import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.results.calls.ApiResultCall
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

// this class has been inspired by Hardik Parmar's article
// https://canopas.com/retrofit-effective-error-handling-with-kotlin-coroutine-and-result-api-405217e9a73d
// https://gitlab.com/cp-hardik-p/error-handling
class ApiResultCallAdapterFactory : CallAdapter.Factory() {

    @Suppress("UNCHECKED_CAST")
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Call::class.java || returnType !is ParameterizedType) {
            return null
        }
        val upperBound = getParameterUpperBound(0, returnType)

        return if (upperBound is ParameterizedType && upperBound.rawType == ApiResult::class.java) {
            object : CallAdapter<Any, Call<Result<*>>> {
                override fun responseType(): Type = getParameterUpperBound(0, upperBound)

                override fun adapt(call: Call<Any>): Call<Result<*>> {
                    val result = ApiResultCall(call) as Call<Result<*>>
                    return result
                }
            }
        } else {
            null
        }
    }
}
