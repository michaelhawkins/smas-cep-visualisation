package de.hsaugsburg.cep.visualisation

import com.jme3.system.AppSettings
import scala.actors.Actor.actor
import de.hsaugsburg.cep.model.ItemMovedEvent

object Main extends App {
  IndustrialPlantApp setShowSettings false

  val settings = new AppSettings(true)
  settings setResolution (1024, 768)
  settings setBitsPerPixel 32
  settings setSamples 4

  IndustrialPlantApp setSettings (settings)
  IndustrialPlantApp start ()
  testItemMovement

  def testItemMovement() {
    val node = new IndustrialPlantNode
    actor {
      val itemName = "TestItem01"

      Thread sleep 2000
      val industrialPlant = IndustrialPlantApp.plant
      val item = industrialPlant addWorkItem itemName

      Thread sleep 2000
      val moveEvent = new ItemMovedEvent("someEventId", System.nanoTime(), itemName, "sensor01", "sensor02")
      node handleItemMovedEvent moveEvent

      Thread sleep 2000
      industrialPlant removeWorkItem itemName
    }

  }
}