package com.pthw.graphql_with_compose

import com.apollographql.apollo.ApolloClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by P.T.H.W on 29/08/2024.
 */

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApolloClient(): ApolloClient = ApolloClient.Builder()
        .serverUrl(BuildConfig.BASE_URL)
        .addHttpHeader("Authorization", "bearer ${BuildConfig.TOKEN}")
        .build()
}