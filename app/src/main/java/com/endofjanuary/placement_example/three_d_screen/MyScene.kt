/*
package com.endofjanuary.placement_example.three_d_screen

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import com.google.android.filament.Engine
import com.google.android.filament.IndirectLight
import com.google.android.filament.MaterialInstance
import com.google.android.filament.Renderer
import com.google.android.filament.Scene
import com.google.android.filament.View
import com.google.android.filament.utils.Manipulator
import io.github.sceneview.SceneView
import io.github.sceneview.collision.CollisionSystem
import io.github.sceneview.environment.Environment
import io.github.sceneview.gesture.GestureDetector
import io.github.sceneview.loaders.EnvironmentLoader
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.node.CameraNode
import io.github.sceneview.node.LightNode
import io.github.sceneview.node.Node
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironment
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberHitTestGestureDetector
import io.github.sceneview.rememberMainLightNode
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberRenderer
import io.github.sceneview.rememberScene
import io.github.sceneview.rememberView


@Composable
fun MyScene(
    modifier: Modifier = Modifier,
    */
/**
     * Provide your own instance if you want to share Filament resources between multiple views.
     *//*

    engine: Engine = rememberEngine(),
    */
/**
     * Consumes a blob of glTF 2.0 content (either JSON or GLB) and produces a [Model] object, which is
     * a bundle of Filament textures, vertex buffers, index buffers, etc.
     *
     * A [Model] is composed of 1 or more [ModelInstance] objects which contain entities and components.
     *//*

    modelLoader: ModelLoader = rememberModelLoader(engine),
    */
/**
     * A Filament Material defines the visual appearance of an object.
     *
     * Materials function as a templates from which [MaterialInstance]s can be spawned.
     *//*

    materialLoader: MaterialLoader = rememberMaterialLoader(engine),
    */
/**
     * Utility for decoding an HDR file or consuming KTX1 files and producing Filament textures,
     * IBLs, and sky boxes.
     *
     * KTX is a simple container format that makes it easy to bundle miplevels and cubemap faces
     * into a single file.
     *//*

    environmentLoader: EnvironmentLoader = rememberEnvironmentLoader(engine),
    */
/**
     * Encompasses all the state needed for rendering a {@link Scene}.
     *
     * [View] instances are heavy objects that internally cache a lot of data needed for
     * rendering. It is not advised for an application to use many View objects.
     *
     * For example, in a game, a [View] could be used for the main scene and another one for the
     * game's user interface. More <code>View</code> instances could be used for creating special
     * effects (e.g. a [View] is akin to a rendering pass).
     *//*

    view: View = rememberView(engine),
    */
/**
     * Controls whether the render target (SurfaceView) is opaque or not.
     * The render target is considered opaque by default.
     *//*

    isOpaque: Boolean = true,
    */
/**
     * A [Renderer] instance represents an operating system's window.
     *
     * Typically, applications create a [Renderer] per window. The [Renderer] generates drawing
     * commands for the render thread and manages frame latency.
     *//*

    renderer: Renderer = rememberRenderer(engine),
    */
/**
     * Provide your own instance if you want to share [Node]s' scene between multiple views.
     *//*

    scene: Scene = rememberScene(engine),
    */
/**
     * Defines the lighting environment and the skybox of the scene.
     *
     * Environments are usually captured as high-resolution HDR equirectangular images and processed
     * by the cmgen tool to generate the data needed by IndirectLight.
     *
     * You can also process an hdr at runtime but this is more consuming.
     *
     * - Currently IndirectLight is intended to be used for "distant probes", that is, to represent
     * global illumination from a distant (i.e. at infinity) environment, such as the sky or distant
     * mountains.
     * Only a single IndirectLight can be used in a Scene. This limitation will be lifted in the
     * future.
     *
     * - When added to a Scene, the Skybox fills all untouched pixels.
     *
     * @see [EnvironmentLoader]
     *//*

    environment: Environment = rememberEnvironment(environmentLoader, isOpaque = isOpaque),
    */
/**
     * Always add a direct light source since it is required for shadowing.
     *
     * We highly recommend adding an [IndirectLight] as well.
     *//*

    mainLightNode: LightNode? = rememberMainLightNode(engine),
    */
/**
     * Represents a virtual camera, which determines the perspective through which the scene is
     * viewed.
     *
     * All other functionality in Node is supported. You can access the position and rotation of the
     * camera, assign a collision shape to it, or add children to it.
     *//*

    cameraNode: CameraNode = rememberCameraNode(engine),
    */
/**
     * Helper that enables camera interaction similar to sketchfab or Google Maps.
     *
     * Needs to be a callable function because it can be reinitialized in case of viewport change
     * or camera node manual position changed.
     *
     * The first onTouch event will make the first manipulator build. So you can change the camera
     * position before any user gesture.
     *
     * Clients notify the camera manipulator of various mouse or touch events, then periodically
     * call its getLookAt() method so that they can adjust their camera(s). Three modes are
     * supported: ORBIT, MAP, and FREE_FLIGHT. To construct a manipulator instance, the desired mode
     * is passed into the create method.
     *//*

    cameraManipulator: ((View, CameraNode) -> Manipulator)? = SceneView.defaultCameraManipulator,
    */
/**
     * List of the scene's nodes that can be linked to a `mutableStateOf<List<Node>>()`
     *//*

    childNodes: List<Node> = rememberNodes(),
    */
/**
     * Physics system to handle collision between nodes, hit testing on a nodes,...
     *//*

    collisionSystem: CollisionSystem = rememberCollisionSystem(view),
    */
/**
     * Detects various gestures and events.
     *
     * The gesture listener callback will notify users when a particular motion event has occurred.
     * Responds to Android touch events with listeners.
     *//*

    gestureDetector: GestureDetector = rememberHitTestGestureDetector(
        LocalContext.current,
        collisionSystem
    ),
    */
/**
     * The listener invoked for all the gesture detector callbacks.
     *//*

    onGestureListener: GestureDetector.OnGestureListener? = rememberOnGestureListener(),
    activity: ComponentActivity? = LocalContext.current as? ComponentActivity,
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    */
/**
     * Invoked when an frame is processed.
     *
     * Registers a callback to be invoked when a valid Frame is processing.
     *
     * The callback to be invoked once per frame **immediately before the scene is updated.
     *
     * The callback will only be invoked if the Frame is considered as valid.
     *//*

    onFrame: ((frameTimeNanos: Long) -> Unit)? = null,
    onViewUpdated: (SceneView.() -> Unit)? = null,
    onViewCreated: (SceneView.() -> Unit)? = null
) {
    if (LocalInspectionMode.current) {
        ScenePreview(modifier)
    } else {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                SceneView(
                    context,
                    null,
                    0,
                    0,
                    engine,
                    modelLoader,
                    materialLoader,
                    environmentLoader,
                    scene,
                    view,
                    renderer,
                    cameraNode,
                    cameraManipulator,
                    mainLightNode,
                    environment,
                    isOpaque,
                    collisionSystem,
                    gestureDetector,
                    onGestureListener,
                    activity,
                    lifecycle,
                ).apply {
                    onViewCreated?.invoke(this)
                }
            },
            update = { sceneView ->
                sceneView.scene = scene
                sceneView.environment = environment
                sceneView.mainLightNode = mainLightNode
                sceneView.setCameraNode(cameraNode)
                sceneView.childNodes = childNodes
                sceneView.gestureDetector = gestureDetector
                sceneView.onGestureListener = onGestureListener
                sceneView.onFrame = onFrame

                onViewUpdated?.invoke(sceneView)
            },
            onReset = {},
            onRelease = { sceneView ->
                sceneView.childNodes = emptyList()
                sceneView.destroy()
            }
        )
    }
}
@Composable
private fun ScenePreview(modifier: Modifier) {
    Box(
        modifier = modifier
            .background(Color.DarkGray)
    )
}*/
