/*
 * Copyright (C) 2022 Block, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mafunes.zipline.localize

import android.content.Context
import app.cash.zipline.Zipline
import app.cash.zipline.loader.LoadResult
import app.cash.zipline.loader.ManifestVerifier.Companion.NO_SIGNATURE_CHECKS
import app.cash.zipline.loader.ZiplineLoader
import com.mafunes.zipline.localize.LocalLocalizePresenter
import com.mafunes.zipline.localize.LocalizePresenter
import com.mafunes.zipline.localize.startLocalizeZipline
import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

class LocalizeAndroid(
  private val applicationContext: Context? = null,
  private val scope: CoroutineScope,
  private val useDynamic: Boolean = false,
) {
  private var zipline: Zipline? = null
  private val ziplineExecutorService = Executors.newSingleThreadExecutor { Thread(it, "Zipline") }
  private val ziplineDispatcher = ziplineExecutorService.asCoroutineDispatcher()
  private val okHttpClient = OkHttpClient()

  fun start() {
    if (useDynamic) {
      scope.launch(ziplineDispatcher + SupervisorJob()) {
        val loadResultFlow: Flow<LoadResult> = startLocalizeZipline(
          ziplineLoader = ZiplineLoader(
            dispatcher = ziplineDispatcher,
            manifestVerifier = NO_SIGNATURE_CHECKS,
            httpClient = okHttpClient,
          ),
          manifestUrl = "http://localhost:8080/manifest.zipline.json",
        )

        loadResultFlow.collect { result ->
          if (result is LoadResult.Success) {
            zipline = result.zipline
          } else {
            println("Failed to load Zipline ${(result as LoadResult.Failure).exception}")
          }
        }
      }
    }
  }

  fun close() {
    if (useDynamic) ziplineExecutorService.shutdown()
  }

  fun getKey(text: String): String {
    if(useDynamic) {
      val presenter = zipline?.take<LocalizePresenter>("LocalizePresenter")
      return "From zipline: " + (presenter?.getString(text, "MLA")?.value ?: "")
    } else {
      return "From native: " + (LocalLocalizePresenter().getString(text, "MLA").value ?: "")
    }
  }
}
