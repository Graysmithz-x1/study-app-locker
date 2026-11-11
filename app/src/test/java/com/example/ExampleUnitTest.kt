package com.example

import com.example.data.DefaultQuestions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun defaultQuestions_containAtLeast10ItemsPerCategory() {
    val questions = DefaultQuestions.getList()
    val mathCount = questions.count { it.category == "MATEMATIKA" }
    val scienceCount = questions.count { it.category == "IPA" }
    val socialCount = questions.count { it.category == "IPS" }

    assertTrue("Matematika questions should be >= 10", mathCount >= 10)
    assertTrue("IPA questions should be >= 10", scienceCount >= 10)
    assertTrue("IPS questions should be >= 10", socialCount >= 10)
  }

  @Test
  fun penaltyCalculation_reducesDurationProperly() {
    val baseDuration = 30
    val penaltyPerMistake = 5
    val mistakes = 2

    val remainingMinutes = maxOf(5, baseDuration - (mistakes * penaltyPerMistake))
    assertEquals(20, remainingMinutes)
  }

  @Test
  fun penaltyCalculation_zeroMistakes_givesFullDuration() {
    val baseDuration = 30
    val penaltyPerMistake = 5
    val mistakes = 0

    val remainingMinutes = maxOf(5, baseDuration - (mistakes * penaltyPerMistake))
    assertEquals(30, remainingMinutes)
  }
}
