package uz.dckroff.statisfy.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uz.dckroff.statisfy.data.remote.api.AuthApi
import uz.dckroff.statisfy.data.remote.api.CategoryApi
import uz.dckroff.statisfy.data.remote.api.FactApi
import uz.dckroff.statisfy.data.remote.interceptor.AuthInterceptor
import uz.dckroff.statisfy.utils.Constants
import uz.dckroff.statisfy.utils.Logger
import uz.dckroff.statisfy.utils.PreferenceManager
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Модуль для предоставления сетевых зависимостей
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideAuthInterceptor(preferenceManager: PreferenceManager): AuthInterceptor {
        return AuthInterceptor(preferenceManager)
    }
    
    /**
     * Предоставляет OkHttpClient с настроенными перехватчиками
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        @ApplicationContext context: Context
    ): OkHttpClient {
        Logger.d("NetworkModule: Creating OkHttpClient")
        
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Logger.d("OkHttp: $message")
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val customInterceptor = LoggingInterceptor()
        
        val chuckerInterceptor = ChuckerInterceptor.Builder(context)
            .collector(ChuckerCollector(context))
            .maxContentLength(250000L)
            .redactHeaders(listOf("Authorization"))
            .alwaysReadResponseBody(true)
            .build()
        
        Logger.i("NetworkModule: Adding interceptors to OkHttpClient")
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(customInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(chuckerInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     * Предоставляет Retrofit с настроенным OkHttpClient
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        Logger.i("NetworkModule: Initializing Retrofit with base URL: ${Constants.BASE_URL}")
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    /**
     * Предоставляет AuthApi
     */
    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        Logger.i("NetworkModule: Creating AuthApi instance")
        return retrofit.create(AuthApi::class.java)
    }
    
    /**
     * Предоставляет FactApi
     */
    @Provides
    @Singleton
    fun provideFactApi(retrofit: Retrofit): FactApi {
        Logger.i("NetworkModule: Creating FactApi instance")
        return retrofit.create(FactApi::class.java)
    }
    
    /**
     * Предоставляет CategoryApi
     */
    @Provides
    @Singleton
    fun provideCategoryApi(retrofit: Retrofit): CategoryApi {
        Logger.i("NetworkModule: Creating CategoryApi instance")
        return retrofit.create(CategoryApi::class.java)
    }
} 