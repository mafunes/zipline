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

import app.cash.zipline.Zipline
import app.cash.zipline.loader.LoadResult
import app.cash.zipline.loader.ManifestVerifier.Companion.NO_SIGNATURE_CHECKS
import app.cash.zipline.loader.ZiplineLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import platform.Foundation.NSURLSession

class LocalizeIos(
  private val scope: CoroutineScope,
) {
  private var zipline: Zipline? = null
  private val ziplineDispatcher = Dispatchers.Main
  private val urlSession = NSURLSession.sharedSession


  fun start() {
    scope.launch(ziplineDispatcher + SupervisorJob()) {
      val loadResultFlow: Flow<LoadResult> = startLocalizeZipline(
        ziplineLoader = ZiplineLoader(
          dispatcher = ziplineDispatcher,
          manifestVerifier = NO_SIGNATURE_CHECKS,
          urlSession = urlSession,
        ),
        manifestUrl = "http://localhost:8080/manifest.zipline.json",
      )
      loadResultFlow.collect { result ->
        when (result) {
          is LoadResult.Success -> {
            zipline = result.zipline
          }
          is LoadResult.Failure -> {
            println("Failed to load Zipline ${result.exception}")
          }
        }
      }
    }
  }


  fun getKey(text: String): String {
    val presenter = zipline?.take<LocalizePresenter>("LocalizePresenter")
    return presenter?.getString(text, "MLA")?.value ?: "presenter not found"
  }
}

