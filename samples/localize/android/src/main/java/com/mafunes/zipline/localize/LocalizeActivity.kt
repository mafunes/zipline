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

import android.os.Bundle
import android.os.Trace
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NoLiveLiterals
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mafunes.zipline.localize.LocalizeAndroid
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

@NoLiveLiterals
class LocalizeActivity : ComponentActivity() {
  private val scope = MainScope()
  private lateinit var localizeAndroid: LocalizeAndroid

  @OptIn(ExperimentalComposeUiApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val useDynamic = intent.data?.getQueryParameter("dynamic") ?: "false"
    localizeAndroid = LocalizeAndroid(applicationContext, scope, useDynamic.toBoolean())
    localizeAndroid.run { start() }

    setContent {
      MaterialTheme {
        Box(
          modifier = Modifier.semantics {
            // Allows to use testTag() for UiAutomator's resource-id.
            // It can be enabled high in the compose hierarchy,
            // so that it's enabled for the whole subtree
            testTagsAsResourceId = true
          }
        ) {
          InputTextExample(localizeAndroid)
        }
      }
    }
  }

  override fun onDestroy() {
    localizeAndroid.close()
    scope.cancel()
    super.onDestroy()
  }
}

@Composable
@VisibleForTesting
private fun InputTextExample(localizeAndroid: LocalizeAndroid) {
  var textState by rememberSaveable(stateSaver = TextFieldValue.Saver) {
    mutableStateOf(TextFieldValue())
  }
  var input by remember { mutableStateOf("") }

  LaunchedEffect(textState) {
    try {
      Trace.beginSection("TracedText")
      input = localizeAndroid.getKey(textState.text)
    } finally {
      Trace.endSection()
    }
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight(),
  ) {
    TextField(
      value = textState,
      onValueChange = {
        textState = it
      },
      label = {
        Text("key")
      },
      modifier = Modifier
        .testTag("TextField")
        .fillMaxWidth()
        .padding(8.dp)
    )
    Text(
      text = input,
      fontSize = 30.sp,
      textAlign = TextAlign.Left,
      modifier = Modifier
        .testTag("Text")
        .fillMaxWidth()
        .padding(8.dp)
    )
  }
}

@Preview(showSystemUi = true)
@Composable
fun InputTextExamplePreview() {
  val localizeAndroid = LocalizeAndroid(null, MainScope(), false)
  localizeAndroid.run { start() }
  InputTextExample(localizeAndroid)
}
