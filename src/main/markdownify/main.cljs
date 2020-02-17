(ns markdownify.main
  (:require [reagent.core :as reagent]
            ["showdown" :as showdown]))



(defonce flash-message (reagent/atom nil))
(defonce flash-timeout (reagent/atom nil))

(defn flash
  ([text]
   (flash text 3000))
  ([text milliseconds]
   (js/clearTimeout @flash-timeout)
   (reset! flash-message text)
   (reset! flash-timeout
           (js/setTimeout #(reset! flash-message nil) milliseconds))))


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

(defn html->md [html]
  (.makeMarkdown showdown-converter html))






; Use defonce instead of def so that you can reload the code without clearing
; the value of the atom.
(defonce text-state (reagent/atom {:format :md
                                   :value ""}))

(defn ->md [{:keys [format value]}]
  (case format
    :md value
    :html (html->md value)))

(defn ->html [{:keys [format value]}]
  (case format
    :md (md->html value)
    :html value))






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
  [:div {:style {:position :relative}}


   [:div
    {:style {:position :absolute
             :margin :auto
             :left 0
             :right 0
             :text-align :center
             :max-width 200
             :padding "2em"
             :background-color "lightgreen"
             :z-index 100
             :border-radius 10
             :transform (if @flash-message
                          "scaleY(1)"
                          "scaleY(0)")
             :transition "transform 0.2s"}}
    @flash-message]

   [:h1 "Markdownify"]
   [:div
    {:style {:display :flex}}

    [:div
     {:style {:flex "1"}}
     [:h2 "Markdown"]
     [:textarea
      {:on-change (fn [e]
                    (reset! text-state {:format :md
                                        :value (-> e .-target .-value)}))
       :value (->md @text-state)
       :style {:resize "none"
               :height "400px"
               :width "100%"}}]
     [:button
      {:on-click (fn []
                   (copy-to-clipboard (->md @text-state))
                   (flash "Markdown copied to clipboard"))
       :style {:background-color :green
               :padding "1em"
               :color :white
               :border-radius 10}}
      "Copy markdown"]]

    [:div
     {:style {:flex "1"}}
     [:h2 "HTML"]
     [:textarea
      {:on-change (fn [e]
                    (reset! text-state {:format :html
                                        :value (-> e .-target .-value)}))
       :value (->html @text-state)
       :style {:resize "none"
               :height "400px"
               :width "100%"}}]
     [:button
      {:on-click (fn []
                   (copy-to-clipboard (->html @text-state))
                   (flash "HTML copied to clipboard"))
       :style {:background-color :green
               :padding "1em"
               :color :white
               :border-radius 10}}
      "Copy HTML"]]

    [:div
     {:style {:flex "1"
              :padding-left "2em"}}
     [:h2 "HTML Preview"]
     [:div {:style {:height "400px"}
            :dangerouslySetInnerHTML {:__html (->html @text-state)}}]]]])












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