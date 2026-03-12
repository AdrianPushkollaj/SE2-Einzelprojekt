package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import org.mockito.Mockito.`when` as whenever // when is a reserved keyword in Kotlin

import kotlin.test.assertFailsWith
import org.springframework.web.server.ResponseStatusException


class LeaderboardControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: LeaderboardController

    @BeforeEach
    fun setup() {
        mockedService = mock<GameResultService>()
        controller = LeaderboardController(mockedService)
    }

    @Test
    fun test_getLeaderboard_correctScoreSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 15, 10.0)
        val third = GameResult(3, "third", 10, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val res: List<GameResult> = controller.getLeaderboard()

        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(first, res[0])
        assertEquals(second, res[1])
        assertEquals(third, res[2])
    }

    @Test
    fun test_getLeaderboard_sameScore_CorrectTimeSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 20, 10.0)
        val third = GameResult(3, "third", 20, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val res: List<GameResult> = controller.getLeaderboard()

        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(second, res[0])
        assertEquals(third, res[1])
        assertEquals(first, res[2])
    }

    @Test
    fun test_getLeaderboard_invalidRankTooSmall() {
        whenever(mockedService.getGameResults()).thenReturn(emptyList())

        assertFailsWith<ResponseStatusException> {
            controller.getLeaderboard(-1)
        }
    }

    @Test
    fun test_getLeaderboard_invalidRankTooLarge() {
        val results = listOf(
            GameResult(1, "p1", 10, 10.0)
        )

        whenever(mockedService.getGameResults()).thenReturn(results)

        assertFailsWith<ResponseStatusException> {
            controller.getLeaderboard(5)
        }
    }

    @Test
    fun test_getLeaderboard_rankNull_returnsFullList() {
        val first = GameResult(1, "first", 30, 30.0)
        val second = GameResult(2, "second", 20, 20.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first))

        val res: List<GameResult> = controller.getLeaderboard(null)

        verify(mockedService).getGameResults()
        assertEquals(2, res.size)
        assertEquals(first, res[0])
        assertEquals(second, res[1])
    }

    @Test
    fun test_getLeaderboard_validRank_returnsSubset() {
        val results = listOf(
            GameResult(1, "p1", 100, 10.0),
            GameResult(2, "p2", 90, 11.0),
            GameResult(3, "p3", 80, 12.0),
            GameResult(4, "p4", 70, 13.0),
            GameResult(5, "p5", 60, 14.0),
            GameResult(6, "p6", 50, 15.0),
            GameResult(7, "p7", 40, 16.0),
            GameResult(8, "p8", 30, 17.0)
        )

        whenever(mockedService.getGameResults()).thenReturn(results)

        val res: List<GameResult> = controller.getLeaderboard(5)

        verify(mockedService).getGameResults()
        assertEquals(7, res.size)
        assertEquals("p2", res[0].playerName)
        assertEquals("p8", res[6].playerName)
    }

}