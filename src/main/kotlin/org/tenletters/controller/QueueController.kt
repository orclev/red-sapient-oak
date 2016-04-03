package org.tenletters.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.tenletters.model.QueueEntry
import java.time.ZonedDateTime

/**
 * Created by kylemurphy on 4/2/16.
 */
@RestController
class QueueController {

  private val queue: MutableSet<QueueEntry> = mutableSetOf();

  @RequestMapping(value = "/queue", method = arrayOf(RequestMethod.GET))
  fun getQueueEntries(): List<QueueEntry> {
    val now = ZonedDateTime.now();
    synchronized(queue) {
      val queues = queue.groupBy { it.getEntryType() == QueueEntry.EntryType.Manager };
      val managers = queues.get(true).orEmpty().sortedWith(QueueEntry.QueueComparator(now));
      val nonManagers = queues.get(false).orEmpty().sortedWith(QueueEntry.QueueComparator(now));
      return managers.plus(nonManagers);
    }
  }
}