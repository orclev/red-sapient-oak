package org.tenletters.model

import java.lang.Math.log
import java.lang.Math.max
import java.time.Duration
import java.time.ZonedDateTime
import java.util.*

/**
 * Created by kylemurphy on 4/2/16.
 */
class QueueEntry(public val id: Int, public val added: ZonedDateTime) {

  fun getEntryType(): EntryType {
    when {
      id % 15 == 0 -> return EntryType.Manager
      id % 5 == 0 -> return EntryType.VIP
      id % 3 == 0 -> return EntryType.Priority
      else -> return EntryType.Normal
    }
  }

  private fun inQueue(now: ZonedDateTime): Long {
    return Duration.between(now, added).seconds;
  }

  fun getScore(now: ZonedDateTime): Long {
    when (this.getEntryType()) {
      EntryType.Manager, EntryType.Normal -> return inQueue(now)
      EntryType.Priority -> return max(3.0, inQueue(now).toDouble() * log(inQueue(now).toDouble())).toLong()
      EntryType.VIP -> return max(4.0, 2 * inQueue(now).toDouble() * log(inQueue(now).toDouble())).toLong()
    }
  }

  enum class EntryType {
    Normal, Manager, VIP, Priority
  }

  class QueueComparator(private val now: ZonedDateTime): Comparator<QueueEntry> {
    override fun compare(p0: QueueEntry, p1: QueueEntry): Int {
      if(p0.getEntryType() == EntryType.Manager || p1.getEntryType() == EntryType.Manager) {
        if(p0.getEntryType() == EntryType.Manager && p1.getEntryType() == EntryType.Manager) {
          return p0.getScore(now).compareTo(p1.getScore(now));
        } else if(p0.getEntryType() == EntryType.Manager) {
          return 1;
        } else {
          return -1;
        }
      } else {
        return p0.getScore(now).compareTo(p1.getScore(now));
      }
    }
  }
}

