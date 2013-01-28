package de.hsaugsburg.cep.visualisation

import com.jme3.app.SimpleApplication
import com.jme3.material.Material
import com.jme3.math.Vector3f
import com.jme3.math.ColorRGBA
import com.jme3.light.DirectionalLight
import com.jme3.light.AmbientLight
import com.jme3.input.{MouseInput, ChaseCamera}
import com.jme3.scene.Spatial
import de.hsaugsburg.cep.visualisation.model.IndustrialPlant
import com.jme3.renderer.queue.RenderQueue
import scala.collection.JavaConverters._
import com.jme3.shadow.PssmShadowRenderer
import com.jme3.niftygui.NiftyJmeDisplay
import com.jme3.input.controls.MouseButtonTrigger
import de.lessvoid.nifty.controls.Console

/**
 * An application that displays a Fischer Technik industrial plant.
 *
 * @author Benny
 */
object IndustrialPlantApp extends SimpleApplication {

  object Nifty {
    private[IndustrialPlantApp] val ConfigFile = "interface.xml"
    private[IndustrialPlantApp] val ScreenId = "industrial_plant_ui"
    private[IndustrialPlantApp] val EventLogId = "event_log"
  }

  val DefaultLightDirection = new Vector3f(-1, -1, -2)
  val plant = IndustrialPlant.fromFile(IndustrialPlant.File)

  private var listBox: Console = null

  def getListBox = listBox

  override def simpleInitApp() {
    initCamera()
    initLightSource()
    //initGUI()

    //    disable shadows by default to improve performance
    //    shadows should only be enabled individually
    rootNode setShadowMode RenderQueue.ShadowMode.Off

    val plantScene = plant.load()
    val children = plantScene.getChildren.asScala
    initShadows(children)
  }

  /**
   * Loads the model specified by the <code>filename</code> into the jME scene, sets a default color and material
   * and returns the created <code>Spatial</code> object.
   *
   * @param filename the name of a file containing a model or scene
   * @return the <code>Spatial</code> object created by jME
   */
  def loadModel(filename: String): Spatial = {
    val model = assetManager loadModel filename

    val material = new Material(assetManager,
      "Common/MatDefs/Light/Lighting.j3md")
    material setBoolean("UseMaterialColors", true)
    material setColor("Ambient", ColorRGBA.LightGray)
    material setColor("Diffuse", ColorRGBA.DarkGray)
    material setColor("Specular", ColorRGBA.White)
    model setMaterial material

    rootNode attachChild model

    model
  }

  /**
   * Initializes the camera to a chase camera and sets the default position.
   */
  private def initCamera() {
    rootNode attachChild IndustrialPlant.Center
    flyCam setEnabled false

    val chaseCam = new ChaseCamera(cam, IndustrialPlant.Center, inputManager)
    chaseCam.setToggleRotationTrigger(new MouseButtonTrigger(MouseInput.BUTTON_RIGHT))
    resetCameraPosition(chaseCam)

    inputManager.addMapping(MouseInputListener.PickCameraTarget, new MouseButtonTrigger(MouseInput.BUTTON_LEFT))
    inputManager.addListener(
      new MouseInputListener(rootNode, inputManager, cam, chaseCam),
      MouseInputListener.PickCameraTarget)
  }

  /**
   * Resets the camera to the default position.
   *
   * @param camera the camera to reset
   */
  def resetCameraPosition(camera: ChaseCamera) {
    camera setSpatial IndustrialPlant.Center
    camera setMaxVerticalRotation 0
    camera setDefaultDistance 30
    camera setDefaultHorizontalRotation 45
  }

  /**
   * Initializes the default light source consisting of a directional light that uses
   * the default direction (same as the camera) and an ambient light.
   */
  private def initLightSource() {
    val sun = new DirectionalLight()
    sun setDirection DefaultLightDirection.normalizeLocal
    sun setColor ColorRGBA.White
    rootNode addLight sun

    val ambientLight = new AmbientLight()
    ambientLight setColor ColorRGBA.LightGray
    rootNode addLight ambientLight
  }

  /**
   * Initializes the shadows of the scene.
   *
   * @param children all children of the scene which should cast or receive a shadow
   */
  private def initShadows(children: Iterable[Spatial]) {
    setShadowMode(children)

    val shadowRenderer = new PssmShadowRenderer(assetManager, 1024, 4)
    shadowRenderer setDirection DefaultLightDirection.normalizeLocal
    shadowRenderer setShadowIntensity 0.5f
    viewPort addProcessor shadowRenderer
  }

  /**
   * Sets the shadow mode for each child. The table only receives the shadow while
   * all other children also cast a shadow.
   *
   * @param children all children of the scene which should cast or receive a shadow
   */
  private def setShadowMode(children: Iterable[Spatial]) {
    for (child <- children) {
      val mode =
        child.getName match {
          case "Table" => RenderQueue.ShadowMode.Receive
          case _ => RenderQueue.ShadowMode.CastAndReceive
        }
      child setShadowMode mode
    }
  }

  private def initGUI() {
    val display = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort)
    val nifty = display.getNifty
    nifty.fromXml(Nifty.ConfigFile, Nifty.ScreenId)
    guiViewPort addProcessor display
    listBox = nifty.getCurrentScreen findNiftyControl(Nifty.EventLogId, classOf[Console])
    listBox.setFocusable(false)
  }
}
