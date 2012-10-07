package de.hsaugsburg.cep.visualisation.model

import scala.xml.Node
import com.jme3.scene.Spatial
import com.jme3.scene.{Node => jME3Node}
import com.jme3.math.Vector3f

class PlantElement(val name: String, val id: String, val sensors: List[Sensor]) {
  def load(scene: jME3Node) {
    for (sensor <- sensors) {
      sensor load scene.getChild(sensor.name)
    }
  }
}

class MachineElement(override val name: String, override val id: String,
                     val armId: String, override val sensors: List[Sensor])
  extends PlantElement(name, id, sensors) {

  private var machineArm: Spatial = null

  private def lowerPos = new Vector3f(0, 0, MachineElement.Movement)
  private def upperPos = new Vector3f(0, 0, 0)

  override def load(scene: jME3Node) {
    super.load(scene)

    machineArm = scene getChild armId
  }

  def beginWork() {
    machineArm setLocalTranslation lowerPos
  }

  def endWork() {
    machineArm setLocalTranslation upperPos
  }
}

object PlantElement {
  def fromXML(node: Node) = {
    val name = (node \ "@name").text
    val id = (node \ "@id").text
    val armId = (node \ "@armId").headOption
    val sensors = for {
      sensorNode <- node \ "sensor"
      sensor = Sensor.fromXML(sensorNode)
    } yield sensor
    if (armId.isDefined)
      new MachineElement(name, id, armId.get.text, sensors.toList)
    else
      new PlantElement(name, id, sensors.toList)
  }
}

object MachineElement {
  private val Movement = 1.5f
}