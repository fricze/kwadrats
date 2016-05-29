(defproject anim "0.1.0-SNAPSHOT"
  :description "simple canvas game"
  
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.8.51"]
                 [org.clojure/core.async "0.2.374"]]

  :plugins [[lein-cljsbuild "1.1.3"]
            [lein-figwheel "0.5.1"]]

  :profiles {:dev {:dependencies [[com.cemerick/piggieback "0.2.1"]
                                  [figwheel-sidecar "0.5.2"]]}}

  :source-paths ["src"]

  :cljsbuild {:builds [{:id "anim"
                        :source-paths ["src"]
                        :figwheel true
                        :compiler {:main "anim.core"
                                   :asset-path "js/out"
                                   :output-to "resources/public/js/main.js"
                                   :output-dir "resources/public/js/out"
                                   :optimizations :none
                                   :source-map true}}]})
