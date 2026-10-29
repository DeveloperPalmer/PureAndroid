package ru.sla.pure.android.core.data.network.adapter

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import ru.sla.pure.android.core.data.network.convertServerError
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ApiErrorCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Call::class.java || returnType !is ParameterizedType) return null
        @Suppress("UNCHECKED_CAST")
        val delegate = retrofit.nextCallAdapter(this, returnType, annotations) as CallAdapter<Any, Any>
        return object : CallAdapter<Any, Any> {
            override fun responseType(): Type = delegate.responseType()
            override fun adapt(call: Call<Any>): Any = delegate.adapt(ErrorHandlingCall(call))
        }
    }
}

private class ErrorHandlingCall(private val delegate: Call<Any>) : Call<Any> by delegate {
    override fun enqueue(callback: Callback<Any>) {
        delegate.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if (response.isSuccessful) callback.onResponse(this@ErrorHandlingCall, response)
                else callback.onFailure(this@ErrorHandlingCall, convertServerError(HttpException(response)))
            }

            override fun onFailure(call: Call<Any>, error: Throwable) {
                callback.onFailure(this@ErrorHandlingCall, convertServerError(error))
            }
        })
    }

    override fun execute(): Response<Any> {
        val response = try {
            delegate.execute()
        } catch (error: Exception) {
            throw convertServerError(error)
        }
        if (!response.isSuccessful) throw convertServerError(HttpException(response))
        return response
    }

    override fun clone(): Call<Any> = ErrorHandlingCall(delegate.clone())
}
