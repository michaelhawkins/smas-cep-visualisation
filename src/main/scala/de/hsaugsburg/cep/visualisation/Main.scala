package de.hsaugsburg.cep.visualisation

import com.jme3.system.AppSettings
import scala.actors.Actor.actor
import de.hsaugsburg.cep.model.{ChangeType, ItemsChangedEvent, ItemMovedEvent}

/**
 * Class used to test the visualization.
 *
 * @author Benny
 */
object Main extends App {
  IndustrialPlantApp setShowSettings false

  val settings = new AppSettings(true)
  settings setResolution (1024, 768)
  settings setBitsPerPixel 32
  settings setSamples 4

  IndustrialPlantApp setSettings (settings)
  IndustrialPlantApp start ()
  testItemMovement()

  /**
   * Tests item movement using the following steps. Add a work item, move the item to a neighboring position
   * and remove the item. Each action is performed with a delay of approx. 2 seconds. Can only be verified
   * visually.
   */
  def testItemMovement() {
    val node = new IndustrialPlantNode
    actor {
      val itemName = "TestItem01"

      Thread sleep 2000
      val addItemEvent = ItemsChangedEvent("changed01", System.nanoTime(), itemName, ChangeType.Added)
//      val industrialPlant = IndustrialPlantApp.plant
//      val item = industrialPlant addWorkItem itemName
      node handleItemsChangedEvent addItemEvent
      println("Item added")

      Thread sleep 2000
      val moveEvent = ItemMovedEvent("moved01", System.nanoTime(), itemName, "sensor01", "sensor02")
      node handleItemMovedEvent moveEvent
      println("Item moved")

      Thread sleep 2000
      val removeItemEvent = ItemsChangedEvent("changed02", System.nanoTime(), itemName, ChangeType.Removed)
//      industrialPlant removeWorkItem itemName
      node handleItemsChangedEvent removeItemEvent
      println("Item removed")
    }

  }
}