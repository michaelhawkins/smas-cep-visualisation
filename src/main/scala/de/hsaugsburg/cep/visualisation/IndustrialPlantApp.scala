package de.hsaugsburg.cep.visualisation

import com.jme3.app.SimpleApplication
import com.jme3.scene.shape.Box
import com.jme3.scene.Geometry
import com.jme3.material.Material
import com.jme3.math.Vector3f
import com.jme3.math.ColorRGBA
import com.jme3.system.AppSettings
import com.jme3.light.DirectionalLight
import java.util.Collections
import com.jme3.light.AmbientLight
import com.jme3.math.FastMath
import com.jme3.input.ChaseCamera
import com.jme3.scene.Spatial
import com.jme3.scene.Node
import de.hsaugsburg.cep.visualisation.model.IndustrialPlant
import com.jme3.shadow.BasicShadowRenderer
import com.jme3.renderer.queue.RenderQueue
import scala.collection.immutable.List
import scala.collection.JavaConverters._
import com.jme3.shadow.PssmShadowRenderer
import com.jme3.post.FilterPostProcessor
import com.jme3.post.ssao.SSAOFilter
import scala.xml.XML

object IndustrialPlantApp extends SimpleApplication {

  val DefaultLightDirection = new Vector3f(-1, -1, -2)
  val plant = IndustrialPlant.fromFile(IndustrialPlant.File)

  override def simpleInitApp(): Unit = {
    initCamera()
    initLightSource()

    rootNode setShadowMode RenderQueue.ShadowMode.Off

    val plantScene = plant.load()
    val children = plantScene.getChildren().asScala
    initShadows(children)
  }

  def loadModel(filename: String): Spatial = {
    val model = assetManager loadModel filename

    val material = new Material(assetManager,
      "Common/MatDefs/Light/Lighting.j3md")
    material setBoolean ("UseMaterialColors", true)
    material setColor ("Ambient", ColorRGBA.LightGray)
    material setColor ("Diffuse", ColorRGBA.DarkGray)
    material setColor ("Specular", ColorRGBA.White)
    model setMaterial material

    rootNode attachChild model

    model
  }

  private def initCamera() {
    rootNode attachChild IndustrialPlant.Center
    flyCam setEnabled false

    val chaseCam = new ChaseCamera(cam, IndustrialPlant.Center, inputManager)
    resetCameraPosition(chaseCam)
  }

  def resetCameraPosition(camera: ChaseCamera) {
    camera setSpatial IndustrialPlant.Center
    camera setMaxVerticalRotation 0
    camera setDefaultDistance 30
    camera setDefaultHorizontalRotation 45
  }

  private def initLightSource() {
    val sun = new DirectionalLight()
    sun setDirection DefaultLightDirection.normalizeLocal
    sun setColor ColorRGBA.White
    rootNode addLight sun

    val ambientLight = new AmbientLight()
    ambientLight setColor ColorRGBA.LightGray
    rootNode addLight ambientLight
  }

  private def initShadows(children: Iterable[Spatial]) {
    setShadowMode(children)

    val shadowRenderer = new PssmShadowRenderer(assetManager, 1024, 4)
    shadowRenderer setDirection DefaultLightDirection.normalizeLocal
    shadowRenderer setShadowIntensity 0.5f
    viewPort addProcessor shadowRenderer
  }

  private def setShadowMode(children: Iterable[Spatial]) {
    for (child <- children) {
      val mode =
        child.getName() match {
          case "Table" => RenderQueue.ShadowMode.Receive
          case _ => RenderQueue.ShadowMode.CastAndReceive
        }
      child setShadowMode mode
    }
  }

}
