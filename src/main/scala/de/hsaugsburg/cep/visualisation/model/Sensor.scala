package de.hsaugsburg.cep.visualisation.model

import scala.xml.Node
import com.jme3.math.Vector3f

case class Sensor(name: String, id: String)

object Sensor {
  def fromXML(node: Node) = {
	  val name = (node \ "@name").text
	  val id = (node \ "@id").text
	  new Sensor(name, id)
	}
}