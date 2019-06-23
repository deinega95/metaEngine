package com.meta_engine.common.di

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.meta_engine.common.network.Api
import com.meta_engine.common.network.Api.Companion.BASE_URL
import com.meta_engine.common.network.MainInterceptor
import com.meta_engine.common.services.NearbyService
import com.meta_engine.common.storage.PrefsManager
import com.meta_engine.common.utils.MyLog
import com.meta_engine.services.GeoPositionService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class AndroidModule(private val application: Application) {


    init {
        MyLog.show("init android module")
    }

    @Provides
    @Singleton
    internal fun provideGson(): Gson {
        return GsonBuilder()
            // .registerTypeAdapter(Data.class, new MyDeserializer())
            //.registerTypeAdapter(DataArray.class, new MyDeserializer())
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
            .create()
    }


    @Provides
    @Singleton
    internal fun provideOkHttp(prefsManager: PrefsManager): OkHttpClient {
        return OkHttpClient().newBuilder()
            .addInterceptor(MainInterceptor())
            .build();
    }

    @Provides
    @Singleton
    internal fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        MyLog.show("create REST client")

        return Retrofit.Builder()
            .baseUrl(BASE_URL) //Базовая часть адреса
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson)) //Конвертер, необходимый для преобразования JSON'а в объекты
            // .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
            .build()

    }

    @Provides
    @Singleton
    internal fun provideApi(retrofit: Retrofit): Api {
        return retrofit.create(Api::class.java)
    }

    @Provides
    @Singleton
    internal fun providePrefsManager(): PrefsManager {
        return PrefsManager(
            application.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        )
    }

    @Provides
    internal fun provideContentResolver(): ContentResolver {
        return application.contentResolver
    }

    @Provides
    internal fun provideGeoService(): GeoPositionService {
        return GeoPositionService(application.applicationContext)
    }

    @Provides
    @Singleton
    internal fun provideNearby(): NearbyService {
        return NearbyService(
            Nearby.getConnectionsClient(application.applicationContext),
            application.applicationContext.packageName
        )

    }

}