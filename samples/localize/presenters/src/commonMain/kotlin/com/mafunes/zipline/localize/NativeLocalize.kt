package com.mafunes.zipline.localize

class NativeLocalize {

  companion object {
    val STATIC_MAP = mapOf(
      "MLA" to buildMap {
        put("hello", "Hola Ale")
        for (i in 0..5) {
          put("Benchmark input $i", "located!")
        }
      },
      "MLB" to buildMap {
        put("hello", "Oi")
        for (i in 0..5) {
          put("Benchmark input $i", "located!")
        }
      }
    )
  }

  fun get(key: String, site: String): String {

    val siteMap: Map<String, String>? = STATIC_MAP[site]
    return siteMap?.get(key) ?: return "Key not found"
  }
}
