package de.hsaugsburg.cep.visualisation

import de.hsaugsburg.cep.model.ItemMovedEvent
import de.hsaugsburg.cep.model.WorkEvent
import de.hsaugsburg.cep.model.ItemsChangedEvent
import de.hsaugsburg.cep.model.ChangeType

/**
 * SMAS node that handles incoming events and delegates them to the 3D engine.
 *
 * @author Benny
 */
//TODO implement SMAS node
class IndustrialPlantNode {

  def handleItemMovedEvent(event: ItemMovedEvent) {
    val target = IndustrialPlantApp.plant getSensorName event.targetId
    //    TODO log AssertError
    IndustrialPlantApp.plant moveWorkItem (event.itemId, target)
    //    TODO log event
  }

  def handleWorkEvent(event: WorkEvent) {
    //    TODO implement method to handle work event in IndustrialPlant class
    //    TODO log event
  }

  def handleItemsChangedEvent(event: ItemsChangedEvent) {
    //    TODO log event
  }
}