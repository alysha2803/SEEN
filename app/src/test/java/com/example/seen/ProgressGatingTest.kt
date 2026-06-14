package com.example.seen

import com.example.seen.data.AppId
import com.example.seen.data.GateState
import com.example.seen.data.MiniGame
import com.example.seen.data.gateState
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for the pure gating logic. No Android dependencies.
 *
 * The progression rule: mini-games unlock strictly in order (1–5).
 * Only the mini-game whose order == highestCompletedOrder + 1 is UNLOCKED.
 * All earlier games are COMPLETED; all later ones are LOCKED.
 */
class ProgressGatingTest {

    // ── Initial state (nothing completed yet) ──────────────────────────────

    @Test
    fun `at start only MG1 is unlocked`() {
        assertEquals(GateState.UNLOCKED, MiniGame.AFFECTION_OR_RED_FLAG.gateState(0))
    }

    @Test
    fun `at start MG2 through MG5 are locked`() {
        listOf(
            MiniGame.LOCK_IT_DOWN,
            MiniGame.TRACE_IT_BACK,
            MiniGame.IS_THIS_PHONE_CLEAN,
            MiniGame.WHAT_DOES_SHE_DO
        ).forEach { mg ->
            assertEquals("Expected LOCKED for $mg at order 0", GateState.LOCKED, mg.gateState(0))
        }
    }

    // ── Locked / Unlocked / Completed boundaries ────────────────────────────

    @Test
    fun `completing MG1 unlocks MG2 and completes MG1`() {
        val done = 1
        assertEquals(GateState.COMPLETED, MiniGame.AFFECTION_OR_RED_FLAG.gateState(done))
        assertEquals(GateState.UNLOCKED, MiniGame.LOCK_IT_DOWN.gateState(done))
        assertEquals(GateState.LOCKED, MiniGame.TRACE_IT_BACK.gateState(done))
    }

    @Test
    fun `completing MG2 unlocks MG3`() {
        val done = 2
        assertEquals(GateState.COMPLETED, MiniGame.AFFECTION_OR_RED_FLAG.gateState(done))
        assertEquals(GateState.COMPLETED, MiniGame.LOCK_IT_DOWN.gateState(done))
        assertEquals(GateState.UNLOCKED, MiniGame.TRACE_IT_BACK.gateState(done))
        assertEquals(GateState.LOCKED, MiniGame.IS_THIS_PHONE_CLEAN.gateState(done))
    }

    @Test
    fun `completing MG3 unlocks MG4`() {
        val done = 3
        assertEquals(GateState.UNLOCKED, MiniGame.IS_THIS_PHONE_CLEAN.gateState(done))
        assertEquals(GateState.LOCKED, MiniGame.WHAT_DOES_SHE_DO.gateState(done))
    }

    @Test
    fun `completing MG4 unlocks MG5 only`() {
        val done = 4
        assertEquals(GateState.COMPLETED, MiniGame.IS_THIS_PHONE_CLEAN.gateState(done))
        assertEquals(GateState.UNLOCKED, MiniGame.WHAT_DOES_SHE_DO.gateState(done))
    }

    @Test
    fun `all five completed when highestOrder is 5`() {
        MiniGame.entries.forEach { mg ->
            assertEquals("Expected COMPLETED for $mg", GateState.COMPLETED, mg.gateState(5))
        }
    }

    // ── Out-of-range highestCompletedOrder ─────────────────────────────────

    @Test
    fun `highestOrder higher than all games marks everything completed`() {
        MiniGame.entries.forEach { mg ->
            assertEquals(GateState.COMPLETED, mg.gateState(99))
        }
    }

    // ── Each game knows its own order and host app ──────────────────────────

    @Test
    fun `mini-game orders are 1 through 5 inclusive`() {
        val orders = MiniGame.entries.map { it.order }.sorted()
        assertEquals(listOf(1, 2, 3, 4, 5), orders)
    }

    @Test
    fun `each mini-game is hosted by the correct app`() {
        assertEquals(AppId.MESSAGES, MiniGame.AFFECTION_OR_RED_FLAG.hostApp)
        assertEquals(AppId.INSTAGRAM, MiniGame.LOCK_IT_DOWN.hostApp)
        assertEquals(AppId.NOTES, MiniGame.TRACE_IT_BACK.hostApp)
        assertEquals(AppId.MONITOR, MiniGame.IS_THIS_PHONE_CLEAN.hostApp)
        assertEquals(AppId.CLIMAX, MiniGame.WHAT_DOES_SHE_DO.hostApp)
    }

    // ── No game can be both UNLOCKED and COMPLETED simultaneously ───────────

    @Test
    fun `gateState is always exactly one of the three values`() {
        for (completedOrder in 0..5) {
            MiniGame.entries.forEach { mg ->
                val gs = mg.gateState(completedOrder)
                val count = listOf(
                    gs == GateState.LOCKED,
                    gs == GateState.UNLOCKED,
                    gs == GateState.COMPLETED
                ).count { it }
                assertEquals("gateState($completedOrder) for $mg produced ambiguous state", 1, count)
            }
        }
    }
}
