(defproject todo-liberator "0.1.0"
  :description "Todo simple app"
  :url "http://example.com/FIXME"
  :ring {:handler todo-liberator.core/handler}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [liberator "0.13"]
                 [compojure "1.3.4"]
                 [org.clojure/data.json "0.2.5"]
                 [ring/ring-core "1.2.1"]
                 [ring/ring-jetty-adapter "1.2.2"]
                 [environ "0.5.0"]]
  :plugins [[lein-ring "0.8.11"]]
  :min-lein-version "2.5.1"
  :uberjar-name "todo-sapp.jar"
  :profiles {:production {:env {:production true}}})
