package com.mafunes.zipline.localize

import app.cash.zipline.Zipline
import app.cash.zipline.ZiplineManifest
import app.cash.zipline.loader.FreshnessChecker
import app.cash.zipline.loader.LoadResult
import app.cash.zipline.loader.ManifestVerifier
import app.cash.zipline.loader.ZiplineLoader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

suspend fun startLocalizeZipline(
  ziplineLoader: ZiplineLoader,
  manifestUrl: String,
) : Flow<LoadResult> {
  return launchZipline(
    ziplineLoader = ziplineLoader,
    manifestUrl = manifestUrl)
}

suspend fun launchZipline(
                  ziplineLoader: ZiplineLoader,
                  manifestUrl: String): Flow<LoadResult>  {

    val freshnessCheckerFresh = object : FreshnessChecker {
      override fun isFresh(manifest: ZiplineManifest, freshAtEpochMs: Long): Boolean {
        return true
      }
    }

  return ziplineLoader.load(
    applicationName = "localize",
    freshnessChecker = freshnessCheckerFresh,
    manifestUrlFlow = repeatFlow(manifestUrl, 60000L),
    initializer = { },
  )
}

suspend fun getKey(zipline: Zipline, text: String): LocalizeModel {
  val presenter = zipline.take<LocalizePresenter>("LocalizePresenter")
    return presenter.getString(text, "MLA")
}

/** Poll for code updates by emitting the manifest on an interval. */
fun <T> repeatFlow(content: T, delayMillis: Long): Flow<T> {
  return flow {
    while (true) {
      emit(content)
      delay(delayMillis)
    }
  }
}

