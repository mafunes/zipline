package com.mafunes.zipline.localize

import app.cash.zipline.Zipline
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

private val zipline by lazy { Zipline.get() }

@OptIn(ExperimentalJsExport::class)
@JsExport
fun main() {
  zipline.bind<LocalizePresenter>(
    name = "LocalizePresenter",
    instance = RealLocalizePresenter(),
  )
}

class RealLocalizePresenter() : LocalizePresenter {
  override fun getString(key: String, site: String): LocalizeModel {
    return LocalizeModel(
            value = Localize().get(key, site)
        )
  }
}
