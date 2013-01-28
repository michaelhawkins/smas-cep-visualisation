package de.hsaugsburg.cep.visualisation

import ch.qos.logback.core.AppenderBase
import ch.qos.logback.classic.spi.ILoggingEvent
import java.util.Calendar

class JME3ConsoleAppender[E] extends AppenderBase[E] {

  def append(eventObject: E) {
    val console = IndustrialPlantApp.getListBox
    if (console != null) {
      eventObject match {
        case event: ILoggingEvent =>
          console.output(formatEvent(event))
      }
    }
  }

  // TODO very bad implementation, find way to use formatting implementing in logging framework
  private def formatEvent(event: ILoggingEvent) = {
    val date = Calendar.getInstance()
    date.setTimeInMillis(event.getTimeStamp)
    val time= date.get(Calendar.HOUR_OF_DAY) + ":" + date.get(Calendar.MINUTE) + ":" + date.get(Calendar.SECOND)
    time + " - " + event.getLevel + " - " + event.getFormattedMessage
  }
}

