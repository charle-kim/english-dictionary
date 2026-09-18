package com.example.englishdictionary.network

import okhttp3.Dns
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.Inet4Address
import java.net.InetAddress
import java.util.concurrent.TimeUnit

/**
 * Some home routers / ISPs advertise IPv6 addresses that are not actually reachable.
 * A browser races IPv4 and IPv6 in parallel and quietly falls back, but a plain
 * OkHttp client can end up stuck trying an unreachable IPv6 route until it times out.
 * Preferring IPv4 addresses when they exist avoids that class of "browser works,
 * app times out" issue.
 */
object IPv4PreferringDns : Dns {
    override fun lookup(hostname: String): List<InetAddress> {
        val addresses = Dns.SYSTEM.lookup(hostname)
        val ipv4 = addresses.filterIsInstance<Inet4Address>()
        return ipv4.ifEmpty { addresses }
    }
}

object ApiClient {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .dns(IPv4PreferringDns)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    val dictionaryApi: DictionaryApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.dictionaryapi.dev/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DictionaryApiService::class.java)
    }

    val translationApi: TranslationApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.mymemory.translated.net/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TranslationApiService::class.java)
    }
}
