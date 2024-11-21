/*
 * Copyright (C) 2024 Cash App
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
package com.example.benchmark

import android.content.Intent
import android.net.Uri
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MemoryCountersMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.TraceSectionMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This is an example startup benchmark.
 *
 * It navigates to the device's home screen, and launches the default activity.
 *
 * Before running this benchmark:
 * 1) switch your app's active build variant in the Studio (affects Studio runs only)
 * 2) add `<profileable android:shell="true" />` to your app's manifest, within the `<application>` tag
 *
 * Run this benchmark from Studio to see startup measurements, and captured system traces
 * for investigating your app's performance.
 */
@RunWith(AndroidJUnit4::class)
class StartupBenchmark {
  private val DEFAULT_ITERATIONS: Int = 50
  private val DEFAULT_REPEAT: Int = 100

  @get:Rule
  val benchmarkRule = MacrobenchmarkRule()

  //@get:Rule
  //val composeTestRule = createComposeRule()

  @Test
  fun startupWithZipline() = benchmarkRule.measureRepeated(
    packageName = "com.mafunes.zipline.localize",
    metrics = listOf(StartupTimingMetric()),
    iterations = DEFAULT_ITERATIONS,
    startupMode = StartupMode.COLD,
    setupBlock = {
      // Press home button before each run to ensure the starting activity isn't visible.
      pressHome()
    }
  ) {
    // starts default launch activity
    startActivityAndWait(Intent(Intent.ACTION_VIEW, Uri.parse("localize://start?dynamic=true")))
  }

  @Test
  fun startupWithoutZipline() = benchmarkRule.measureRepeated(
    packageName = "com.mafunes.zipline.localize",
    metrics = listOf(StartupTimingMetric()),
    iterations = DEFAULT_ITERATIONS,
    startupMode = StartupMode.COLD,
    setupBlock = {
      // Press home button before each run to ensure the starting activity isn't visible.
      pressHome()
    }
  ) {
    // starts default launch activity
    startActivityAndWait(Intent(Intent.ACTION_VIEW, Uri.parse("localize://start?dynamic=false")))
  }


  @OptIn(ExperimentalMetricApi::class)
  @Test
  fun inputTextWithoutZipline() {
    benchmarkRule.measureRepeated(
      packageName = "com.mafunes.zipline.localize",
      metrics = listOf(
        FrameTimingMetric(),
        MemoryCountersMetric(),
        TraceSectionMetric("TracedText")),
      // Try switching to different compilation modes to see the effect
      // it has on frame timing metrics.
      compilationMode = CompilationMode.None(),
      startupMode = StartupMode.WARM, // restarts activity each iteration
      iterations = DEFAULT_ITERATIONS,
      setupBlock = {
        // Before starting to measure, navigate to the UI to be measured
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("localize://start?dynamic=false"))
        startActivityAndWait(intent)
      }
    ) {
      val input = device.findObject(By.res("TextField"))
      Thread.sleep(100)
      repeat(DEFAULT_REPEAT) {
        input.text = ""
        input.text = "Benchmark input $it"
      }
    }
  }

  @OptIn(ExperimentalMetricApi::class)
  @Test
  fun inputTextWithZipline() {
    benchmarkRule.measureRepeated(
      packageName = "com.mafunes.zipline.localize",
      metrics = listOf(
        FrameTimingMetric(),
        MemoryCountersMetric(),
        TraceSectionMetric("TracedText")),
      // Try switching to different compilation modes to see the effect
      // it has on frame timing metrics.
      compilationMode = CompilationMode.None(),
      startupMode = StartupMode.WARM, // restarts activity each iteration
      iterations = DEFAULT_ITERATIONS,
      setupBlock = {
        // Before starting to measure, navigate to the UI to be measured
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("localize://start?dynamic=true"))
        startActivityAndWait(intent)
      }
    ) {
      val input = device.findObject(By.res("TextField"))
      Thread.sleep(100)
      repeat(DEFAULT_REPEAT) {
        input.text = ""
        input.text = "Benchmark input $it"
      }
    }
  }
}
