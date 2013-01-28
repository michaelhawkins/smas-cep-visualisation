package de.hsaugsburg.cep.visualisation.model

import scala.xml.Node
import com.jme3.math.Vector3f
import com.jme3.scene.Spatial
import de.hsaugsburg.cep.visualisation.IndustrialPlantApp
import java.util.concurrent.Callable

/**
 * @param name the name of the sensor defined by the corresponding object in the blender scene
 * @param id the id of the sensor defined by the ontology
 */
case class Sensor(name: String, id: String) {

  private var position: Vector3f = null
  private var currentItem: WorkItem = null

  /**
   * performs specific tasks that can only be performed after the blender scene has been loaded.
   *
   * @param model the spatial object representing this sensor in the jME scene
   */
  def load(model: Spatial) {
    position = model.getLocalTranslation
  }

  /**
   * Moves the current work item of this sensor to the target sensor location and removes the
   * reference to the item. The current item must be null.
   *
   * @param target item will be moved to this sensor
   */
  def moveItem(target: Sensor) {
    require(currentItem != null)

    target setCurrentItem currentItem
    currentItem = null
  }

  /**
   * Sets the current work item. The work item will also be moved to the position of this sensor
   * and therefore the current work item must be null.
   *
   * @param item item to move to this sensor
   */
  def setCurrentItem(item: WorkItem) {
    require(currentItem == null)

    currentItem = item
    IndustrialPlantApp.enqueue(new Callable[Spatial] {
      def call(): Spatial = {
        item.model setLocalTranslation position
        item.model
      }
    })

  }
}

object Sensor {

  /**
   * Creates a new sensor using the id to name mapping specified in the configuration.
   *
   * @param node an xml node containing the sensors configuration
   * @return new sensor using the id to name mapping of the configuration
   */
  def fromXML(node: Node) = {
    val name = (node \ "@name").text
    val id = (node \ "@id").text
    new Sensor(name, id)
  }
}