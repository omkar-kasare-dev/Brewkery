package com.example.brewkery.data

import android.util.Log
import com.example.brewkery.data.model.MenuResponse
import com.example.brewkery.data.remote.BrewkeryApi
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

enum class MenuError { NETWORK, SERVER, UNKNOWN }

sealed interface MenuResult {
    data class Success(val menu: MenuResponse) : MenuResult
    data class Failure(val error: MenuError) : MenuResult
}

@Singleton
class MenuRepository @Inject constructor(
    private val api: BrewkeryApi
) {

    private var cachedMenu: MenuResponse? = null

    /** Returns the cached menu if present, so Detail opens instantly without another request. */
    suspend fun getMenu(): MenuResult {
        cachedMenu?.let { return MenuResult.Success(it) }
        return try {
            val menu = api.getMenu()
            cachedMenu = menu
            MenuResult.Success(menu)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load menu", e)
            MenuResult.Failure(
                when (e) {
                    is IOException -> MenuError.NETWORK
                    is HttpException -> MenuError.SERVER
                    else -> MenuError.UNKNOWN
                }
            )
        }
    }

    private companion object {
        const val TAG = "MenuRepository"
    }
}