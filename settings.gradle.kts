rootProject.name = "zipline-root"

include(":zipline")
include(":zipline-api-validator")
include(":zipline-bytecode")
include(":zipline-cli")
include(":zipline-cryptography")
include(":zipline-gradle-plugin")
include(":zipline-kotlin-plugin")
include(":zipline-kotlin-plugin-tests")
include(":zipline-loader")
include(":zipline-loader-testing")
include(":zipline-profiler")
include(":zipline-testing")


include(":sample:trivia:trivia-host")
include(":sample:trivia:trivia-js")
include(":sample:trivia:trivia-shared")
include(":sample:world-clock:android")
include(":sample:world-clock:ios:shared")
include(":sample:world-clock:presenters")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
