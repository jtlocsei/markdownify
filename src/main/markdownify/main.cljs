(ns markdownify.main
  (:require [reagent.core :as reagent]
            ["showdown" :as showdown]))


; Use defonce instead of def so that you can reload the code without clearing
; the value of the atom.
(defonce markdown (reagent/atom "Initial value"))

; Example code from showdown npm library
; var showdown  = require('showdown'),
;    converter = new showdown.Converter(),
;    text      = '# hello, markdown!',
;    html      = converter.makeHtml(text);

; Adapted from javascript
; converter = new showdown.Converter()
(defonce showdown-converter (showdown/Converter.))

; Adapted from javascript
; converter.makeHtml(text);
(defn md->html [md]
  (.makeHtml showdown-converter md))


(defn copy-to-clipboard
  "Copy a given string to the clipboard. Adapted from the javascript here:
  https://hackernoon.com/copying-text-to-clipboard-with-javascript-df4d4988697f"
  [s]
  (let [; const el = document.createElement('textarea')
        el (.createElement js/document "textarea")
        ;  const selected =
        ;    document.getSelection().rangeCount > 0    // Check if there is any content selected previously
        ;      ? document.getSelection().getRangeAt(0) // Store selection if found
        ;      : false;                                // Mark as false to know no selection existed before
        selected (when (pos? (-> js/document .getSelection .-rangeCount))
                   (-> js/document .getSelection (.getRangeAt 0)))]
    (set! (.-value el) s) ; el.value = str;
    (.setAttribute el "readonly" "") ; el.setAttribute('readonly', '');
    (set! (-> el .-style .-position) "absolute") ; el.style.position = 'absolute';
    (set! (-> el .-style .-left) "-9999px") ; el.style.left = '-9999px';
    (-> js/document .-body (.appendChild el)) ; document.body.appendChild(el);
    (.select el) ;  el.select();
    (.execCommand js/document "copy") ;  document.execCommand('copy');
    (-> js/document .-body (.removeChild el)) ;  document.body.removeChild(el);
    ;  if (selected) {                                 // If a selection existed before copying
    ;    document.getSelection().removeAllRanges();    // Unselect everything on the HTML document
    ;    document.getSelection().addRange(selected);   // Restore the original selection
    ;  }
    (when selected
      (-> js/document .getSelection .removeAllRanges)
      (-> js/document .getSelection (.addRange selected)))))











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
               :width "100%"}}]
     [:button
      {:on-click #(copy-to-clipboard @markdown)
       :style {:background-color :green
               :padding "1em"
               :color :white
               :border-radius 10}}
      "Copy markdown"]]
    [:div
     {:style {:flex "1"
              :padding-left "2em"}}
     [:h2 "HTML Preview"]
     [:div {:style {:height "500px"}
            :dangerouslySetInnerHTML {:__html (md->html @markdown)}}]
     [:button
      {:on-click #(copy-to-clipboard (md->html @markdown))
       :style {:background-color :green
               :padding "1em"
               :color :white
               :border-radius 10}}
      "Copy html"]]]])












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