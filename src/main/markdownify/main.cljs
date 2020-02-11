(ns markdownify.main
  (:require [reagent.core :as reagent]
            ["showdown" :as showdown]))


; Use defonce instead of def so that you can reload the code without clearing
; the value of the atom.
(defonce markdown (reagent/atom "Initial value"))

; Adapted from javascript
; converter = new showdown.Converter()
(defonce showdown-converter (showdown/Converter.))

; Adapted from javascript
; converter.makeHtml(text);
(defn md->html [md]
  (.makeHtml showdown-converter md))

(defn app
  []
  [:<>
   [:h1 "Markdownify"]
   [:div
    {:style {:display :flex}}
    [:div
     {:style {:flex "1"}}
     [:h2 "Markdown"]
     [:textarea
      {:on-change #(reset! markdown (-> % .-target .-value))
       :value @markdown
       :style {:resize "none"
               :height "500px"
               :width "100%"}}]]
    [:div
     {:style {:flex "1"
              :padding-left "2em"}}
     [:h2 "HTML Preview"]
     [:div {:style {:height "500px"}
            :dangerouslySetInnerHTML {:__html (md->html @markdown)}}]
     [:div (md->html @markdown)]]]])


;var showdown  = require('showdown'),
;    converter = new showdown.Converter(),
;    text      = '# hello, markdown!',
;    html      = converter.makeHtml(text);











(defn mount!
  []
  (reagent/render [app]
                  (.getElementById js/document "app")))

(defn main!
  []
  (mount!))

(defn reload!
  []
  (mount!))