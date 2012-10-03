package de.hsaugsburg.cep.visualisation.model

import scala.xml.Node

case class PlantElement(name: String, sensors: List[Sensor])

object PlantElement {
  def fromXML(node: Node) = {
    val name = (node \ "@name").text
    val sensors = for {
      sensorNode <- node \ "sensor"
      sensor = Sensor.fromXML(sensorNode)
    } yield sensor
    new PlantElement(name, sensors.toList)
  }
}