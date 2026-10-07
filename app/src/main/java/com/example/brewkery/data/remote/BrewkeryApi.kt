package com.example.brewkery.data.remote


import com.example.brewkery.data.model.MenuResponse
import retrofit2.http.GET

interface BrewkeryApi {

    /** Full menu + store config. */
    @GET("data.json")
    suspend fun getMenu(): MenuResponse
}