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
package app.cash.zipline.samples.worldclock

import app.cash.zipline.Zipline
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private val zipline by lazy { Zipline.get() }

@OptIn(ExperimentalJsExport::class)
@JsExport
fun main() {
  val worldClockHost = zipline.take<WorldClockHost>("WorldClockHost")
  zipline.bind<WorldClockPresenter>(
    name = "WorldClockPresenter",
    instance = RealWorldClockPresenter(worldClockHost),
  )
}

class RealWorldClockPresenter(
  private val host: WorldClockHost,
) : WorldClockPresenter {
  override fun models(
    events: Flow<WorldClockEvent>,
  ): Flow<WorldClockModel> {
    return flow {
      while (true) {
        emit(
          WorldClockModel(
            label = TimeFormatter().formatWorldTime(),
          ),
        )
        delay(16)
      }
    }
  }
}
