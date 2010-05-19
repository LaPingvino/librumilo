(ns librumilo.core ;; If you use this as a template, don't forget to change the name here...
  (:use compojure.core
        hiccup.core
        ring.middleware.file
        ring.adapter.jetty)
  (:import com.petebevin.markdown.MarkdownProcessor))

(def *default-title* "Librumilo") ;; ...and here
(def *default-stylesheet* "default.css")

(defn output
  "This function should be used by all pages to produce their output, to deliver valid HTML that looks pretty much the same for every page."
  [{:keys [title style stylesheet head body]
    :or {title *default-title*,
         stylesheet *default-stylesheet*}}]
  (html
   "<!DOCTYPE html>\n"
   [:html
    [:head
     [:title title]
     (when stylesheet
       [:link {:rel "stylesheet"
               :type "text/css"
               :href stylesheet}])
     head]
    [:body
     body]]))

(defn markdown-to-html
  "A wrapper around MarkdownJ. You put markdown in and get HTML out of it."
  [input]
  (.markdown (MarkdownProcessor.) input))

(defroutes main-routes
  (GET "/" []
    (output {:body (markdown-to-html "# Test of markdown\n\nThis should work")}))
  (ANY "*" []
    {:status 404
     :body (output {:body [:h1 "Unless you have been poking around, you shouldn't see this page..."]})}))

(wrap! main-routes (:file "assets")) ;; Enable file serving

(run-jetty main-routes {:port 8080})