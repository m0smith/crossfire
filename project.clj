(defproject crossfire "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.4.0"]]
  :plugins [[lein-cljsbuild "0.2.4"]]
  :hooks [leiningen.cljsbuild]
  :source-path "src"
  :cljsbuild {
    :builds [{:source-path "src-cljs" :compiler {:output-to "main.js"}}]
    :crossovers [crossfire]
  }
  :main crossfire.core)
