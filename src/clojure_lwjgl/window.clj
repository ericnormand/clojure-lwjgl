(ns clojure-lwjgl.window
  (:import [org.lwjgl.opengl GLContext PixelFormat Display DisplayMode GL11 GL12  GL30 ARBVertexBufferObject]
           [org.lwjgl.input Mouse]
           [org.lwjgl BufferUtils]
           [java.awt Frame Canvas]
           [java.awt.event WindowAdapter ComponentAdapter]
           [java.nio IntBuffer FloatBuffer]))

(defrecord Window [frame
                   close-requested
                   resize-requested
                   width
                   height])

(defn resize [window]
  (when @(:resize-requested window)
    (GL11/glViewport 0 0 @(:width window)  @(:height window))
    (GL11/glMatrixMode GL11/GL_PROJECTION)
    (GL11/glLoadIdentity)
    (GL11/glOrtho 0, @(:width window), 0, @(:height window), -1, 1)
    (GL11/glMatrixMode GL11/GL_MODELVIEW)
    (reset! (:resize-requested window) false)))

(defn update [window]
  (resize window)
  (Display/update)
  (Display/sync 1)
  window)

(defn create []
  (let [canvas (Canvas.)
        frame (new Frame)
        resize-requested (atom false)
        close-requested (atom false)
        width (atom 300)
        height (atom 300)]

    (.addComponentListener canvas
                           (proxy [ComponentAdapter] []
                             (componentResized [e]
                               (println "Resizing")
                               (reset! width (-> canvas .getSize .getWidth))
                               (reset! height (-> canvas .getSize .getHeight)))))

    (doto frame
      (.add canvas)
      (.addWindowListener
       (proxy [WindowAdapter] []
         (windowClosing [e]
           (println "Frame closed")
           (reset! close-requested true))))
      (.setSize 400 400)
      .show)

    (Display/setParent canvas)

    (Display/create)
    (Window. frame
             resize-requested
             close-requested
             width
             height)))

(defn close [window]
  (println "Destroying window")
  (Display/destroy)
  (.dispose (:frame window))
  (reset! (:close-requested window) false))





