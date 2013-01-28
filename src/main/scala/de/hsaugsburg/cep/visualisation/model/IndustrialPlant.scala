package de.hsaugsburg.cep.visualisation.model

import com.jme3.scene.{Spatial, Node}
import scala.xml.XML
import de.hsaugsburg.cep.visualisation.IndustrialPlantApp
import com.jme3.renderer.queue.RenderQueue
import collection.mutable
import java.util.concurrent.Callable

/**
 * This class represents the Fischer Technik industrial plant. It provides method to interact with the plant
 * and handle incoming events from the CEP agent. It also manages the plant elements, sensors and the general
 * state of the plant.
 *
 * @author Benny
 */
case class IndustrialPlant(file: String, elements: List[PlantElement], itemFile: String) {

  private var scene: Node = null
  private val items = new mutable.HashMap[String, WorkItem]

  /**
   * Loads the industrial plant scene, adds it to the jME scene and returns the scenes <code>Spatial</code>.
   *
   * @return the <code>Spatial</code> of the loaded industrial plant scene
   */
  def load() = {
    scene = IndustrialPlantApp.loadModel(file).asInstanceOf[Node]

    for (element <- elements) {
      element load scene
    }

    scene
  }

  /**
   * Creates a new work item using the specified <code>name</code>, adds it to the jME scene
   * and returns the added <code>WorkItem</code>.
   *
   * @param name the name of the work item
   * @return the added <code>WorkItem</code>
   */
  def addWorkItem(name: String) = {
    require(name != null)

    val itemScene = IndustrialPlantApp.loadModel(itemFile)
    itemScene setShadowMode RenderQueue.ShadowMode.Off
    val item = new WorkItem(name, itemScene)
    items += item.name -> item

    val entryPoint = getSensorByName(IndustrialPlant.ItemEntrySensor)
    entryPoint match {
      case Some(ep) =>
        ep setCurrentItem item
      case None =>
        throw new IllegalStateException("Config Error: could not find item entry sensor (name spelled wrong?)")
    }

    item
  }

  /**
   * Removes the <code>WorkItem</code> specified by the <code>name</code> from the jME scene
   * and the data model.
   *
   * @param name the name of the <code>WorkItem</code> to remove
   * @return <code>true</code> if the item was successfully removed, otherwise <code>false</code>
   */
  def removeWorkItem(name: String) = {
    require(items contains name)

    val item = items(name)
    items -= item.name
    IndustrialPlantApp.enqueue(new Callable[Spatial] {
      def call(): Spatial = {
        item.model.removeFromParent()
        item.model
      }
    })
    item.model.removeFromParent()
  }

  /**
   * Returns the sensor specified by the <code>id</code> or <code>None</code> if there is
   * no sensor of the specified <code>id</code>
   *
   * @param id the id of a sensor
   * @return the sensor using the specified id
   */
  def getSensorById(id: String): Option[Sensor] = {
    getSensor(_.id == id)
  }

  /**
   * Returns the sensor specified by the <code>name</code> or <code>None</code> if there is
   * no sensor of the specified <code>name</code>.
   *
   * @param name the name of a sensor
   * @return the sensor using the specified name
   */
  def getSensorByName(name: String): Option[Sensor] = {
    getSensor(_.name == name)
  }

  /**
   * Searches for a sensor that applies to the specified filter. The filter should
   * not be applicable to more than one sensor, otherwise <code>None</code> is returned.
   *
   * @param filter filter to describe the sensor to search for
   * @return the found sensor, or <code>None</code> if no matching sensor was found
   */
  private def getSensor(filter: (Sensor) => Boolean): Option[Sensor] = {
    val namesFound = for {
      element <- elements
      sensor <- element.sensors.filter(filter)
    } yield sensor

    namesFound match {
      case List(sensor: Sensor) => Option(sensor)
      case _ => None
    }
  }

  /**
   * Returns the <code>MachineElement</code> using the specified <code>id</code> or <code>None</none>
   * if there is none or more than one machine using the id.
   *
   * @param id the id of an element
   * @return the <code>MachineElement</code> using the specified <code>id</code>
   */
  def getMachine(id: String): Option[MachineElement] = {
    val machinesFound = for {
      element <- elements
      if element.id == id
    } yield element

    machinesFound match {
      case List(machine: MachineElement) => Option(machine)
      case _ => None
    }
  }
}

object IndustrialPlant {
  val File = "IndustrialPlantConfig.xml"
  val Center = new Node("Industrial Plant Center")
  Center.setLocalTranslation(10f, 0.0f, -6f)

  val ItemEntrySensor = "ItemEntrySensor"

  /**
   * Creates a new <code>IndustrialPlant</code> using the configuration specified by the
   * <code>file</code>. The plant is not loaded in the jME scene.
   *
   * @param file the name of the file containing the plant configuration
   * @return a new <code>IndustrialPlant</code> object
   */
  def fromFile(file: String) = {
    val inputStream = ClassLoader getSystemResourceAsStream file
    val plantNode = XML load inputStream
    IndustrialPlant fromXML plantNode
  }

  /**
   * Creates a new <code>IndustrialPlant</code> using the configuration specified by the
   * <code>node</code>. The plant is not loaded in the jME scene.
   *
   * @param node the node containing the configuration of the plant
   * @return a new <code>IndustrialPlant</code> object
   */
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