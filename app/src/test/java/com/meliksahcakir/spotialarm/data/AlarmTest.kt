package com.meliksahcakir.spotialarm.data

import junit.framework.TestCase
import org.junit.Test
import java.time.LocalDateTime
import java.time.Month
import java.time.temporal.ChronoUnit

class AlarmTest : TestCase() {

    @Test
    fun testNearestDate_withFutureDays() {
        val time = LocalDateTime.of(2020, Month.DECEMBER, 27, 16, 30)
        val alarm = Alarm(8, 30, true, Alarm.TUESDAY or Alarm.WEDNESDAY)
        val nearestDate = alarm.nearestDate(time)
        assertEquals(40 * 60, time.until(nearestDate, ChronoUnit.MINUTES))
    }

    @Test
    fun testNearestDate_withNoSelectedDays() {
        val time = LocalDateTime.of(2020, Month.DECEMBER, 27, 16, 30)
        val alarm = Alarm(8, 30, true, 0)
        val nearestDate = alarm.nearestDate(time)
        assertEquals(16 * 60, time.until(nearestDate, ChronoUnit.MINUTES))
    }

    @Test
    fun testNearestDate_withDisabled() {
        val time = LocalDateTime.of(2020, Month.DECEMBER, 27, 16, 30)
        val alarm = Alarm(8, 30, false, Alarm.ALL_DAYS)
        val nearestDate = alarm.nearestDate(time)
        assertEquals(null, nearestDate)
    }

    @Test
    fun testNearestDate_withAllDays() {
        val time = LocalDateTime.of(2020, Month.DECEMBER, 27, 16, 30)
        val alarm = Alarm(8, 30, true, Alarm.ALL_DAYS)
        val nearestDate = alarm.nearestDate(time)
        assertEquals(16 * 60, time.until(nearestDate, ChronoUnit.MINUTES))
    }

    @Test
    fun testNearestDate_withPreviousDays() {
        val time = LocalDateTime.of(2020, Month.DECEMBER, 27, 16, 30)
        val alarm = Alarm(8, 30, true, Alarm.SUNDAY)
        val nearestDate = alarm.nearestDate(time)
        assertEquals((7 * 24 - 8) * 60, time.until(nearestDate, ChronoUnit.MINUTES))
    }

    @Test
    fun testNearestDate_withMixedDays() {
        val time = LocalDateTime.of(2020, Month.DECEMBER, 27, 16, 30)
        val alarm = Alarm(8, 30, true, Alarm.SUNDAY or Alarm.FRIDAY)
        val nearestDate = alarm.nearestDate(time)
        assertEquals((5 * 24 - 8) * 60, time.until(nearestDate, ChronoUnit.MINUTES))
    }
}
