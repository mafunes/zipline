package com.mafunes.zipline.localize

class LocalLocalizePresenter()  {
  fun getString(key: String, site: String): LocalizeModel {
    return LocalizeModel(
      value = NativeLocalize().get(key, site)
    )
  }
}
