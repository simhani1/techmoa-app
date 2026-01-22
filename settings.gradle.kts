rootProject.name = "app"

include("app-api")

include("app-core")

include("app-batch:app-batch-rss")

include("app-infra")
include("app-infra:app-infra-db")
include("app-infra:app-infra-oauth")