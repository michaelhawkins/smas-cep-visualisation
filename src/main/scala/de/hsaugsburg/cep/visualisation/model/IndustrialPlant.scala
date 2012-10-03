package de.hsaugsburg.cep.visualisation.model

import com.jme3.scene.Node
import scala.xml.XML
import de.hsaugsburg.cep.visualisation.IndustrialPlantApp
import com.jme3.renderer.queue.RenderQueue
import scala.collection.mutable.HashMap

case class IndustrialPlant(file: String, elements: List[PlantElement], itemFile: String) {
  
  private var scene: Node = null
  private val items = new HashMap[String, WorkItem]

  def load() = {
    scene = IndustrialPlantApp.loadModel(file).asInstanceOf[Node]
    scene
  }

  def addWorkItem(name: String) = {
    val itemScene = IndustrialPlantApp.loadModel(itemFile)
    val entryPoint = scene.getChild("ItemEntrySensor").getLocalTranslation()
    itemScene setLocalTranslation entryPoint
    itemScene setShadowMode RenderQueue.ShadowMode.Off
    val item = new WorkItem(name, itemScene)
    items += item.name -> item
    item
  }

  def moveWorkItem(name: String, target: String) {
    require(items contains name)

    val item = items(name)
    val targetSensor = getSensor(target)
    val targetLocation = targetSensor getLocalTranslation

    item.model setLocalTranslation targetLocation
  }
  
  def removeWorkItem(name: String) = {
    require(items contains name)
    
    val item = items(name)
    item.model.removeFromParent()
  }
  
  def moveWorkItem(item: WorkItem, target: String): Unit = moveWorkItem(item.name, target)

  def getSensor(name: String) = scene.getChild(name)
}

object IndustrialPlant {
  val File = "IndustrialPlantConfig.xml"
  val Center = new Node("Industrial Plant Center")
  Center.setLocalTranslation(10f, 0.0f, -6f)

  def fromFile(fileName: String) = {
    val inputstream = ClassLoader getSystemResourceAsStream fileName
    val plantNode = XML load inputstream
    IndustrialPlant fromXML plantNode
  }

  def fromXML(node: scala.xml.Node) = {
    val file = (node \ "@file").text
    val elements =
      for {
        elementNode <- node \\ "element"
        element = PlantElement.fromXML(elementNode)
      } yield element
    val itemFile = (node \ "@itemFile").text
    new IndustrialPlant(file, elements.toList, itemFile)
  }
}