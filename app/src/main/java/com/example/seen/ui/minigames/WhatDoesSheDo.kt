package com.example.seen.ui.minigames

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.seen.data.ContentRepository
import com.example.seen.data.DecisionOption
import com.example.seen.data.DecisionNode
import com.example.seen.data.MiniGame
import com.example.seen.state.ProgressViewModel
import com.example.seen.ui.theme.*

private sealed class WhatPhase {
    data class Intro(val lineIndex: Int) : WhatPhase()
    data class Decision(val nodeId: String, val feedback: String?) : WhatPhase()
    object Done : WhatPhase()
}

@Composable
fun WhatDoesSheDo(
    vm: ProgressViewModel,
    onHelp: () -> Unit,
    onComplete: () -> Unit
) {
    val introLines = ContentRepository.climaxIntroLines
    val nodes = remember { ContentRepository.decisionNodes.associateBy { it.id } }

    var phase by remember { mutableStateOf<WhatPhase>(WhatPhase.Intro(0)) }

    // When Done: mark complete and navigate
    if (phase is WhatPhase.Done) {
        LaunchedEffect(Unit) {
            vm.completeMiniGame(MiniGame.WHAT_DOES_SHE_DO)
            onComplete()
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Primary)
        }
        return
    }

    AnimatedContent(
        targetState = phase,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        modifier = Modifier.fillMaxSize(),
        label = "what_phase"
    ) { currentPhase ->
        when (currentPhase) {
            is WhatPhase.Intro -> {
                IntroPhase(
                    line = introLines[currentPhase.lineIndex],
                    lineIndex = currentPhase.lineIndex,
                    total = introLines.size,
                    onNext = {
                        phase = if (currentPhase.lineIndex < introLines.lastIndex)
                            WhatPhase.Intro(currentPhase.lineIndex + 1)
                        else
                            WhatPhase.Decision(ContentRepository.decisionStartNodeId, null)
                    },
                    onHelp = onHelp
                )
            }
            is WhatPhase.Decision -> {
                val node = nodes[currentPhase.nodeId]
                if (node != null) {
                    DecisionPhase(
                        node = node,
                        feedback = currentPhase.feedback,
                        onOption = { option ->
                            if (option.safe) {
                                phase = if (option.nextNodeId != null)
                                    WhatPhase.Decision(option.nextNodeId, null)
                                else
                                    WhatPhase.Done
                            } else {
                                phase = WhatPhase.Decision(currentPhase.nodeId, option.feedback)
                            }
                        },
                        onHelp = onHelp
                    )
                }
            }
            is WhatPhase.Done -> { /* handled above */ }
        }
    }
}

@Composable
private fun IntroPhase(
    line: String,
    lineIndex: Int,
    total: Int,
    onNext: () -> Unit,
    onHelp: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .clickable(onClick = onNext)
    ) {
        IconButton(
            onClick = onHelp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp)
                .statusBarsPadding()
        ) {
            Icon(
                Icons.Default.Help,
                contentDescription = "Leave / get help",
                tint = OnDarkBackground.copy(alpha = 0.35f)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = line,
                style = MaterialTheme.typography.titleMedium,
                color = OnDarkBackground,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(36.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(total) { i ->
                    Box(
                        modifier = Modifier
                            .size(if (i == lineIndex) 10.dp else 6.dp)
                            .background(
                                if (i == lineIndex) Primary else DarkSurfaceVariant,
                                CircleShape
                            )
                    )
                }
            }
        }

        Text(
            "tap to continue",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
        )
    }
}

@Composable
private fun DecisionPhase(
    node: DecisionNode,
    feedback: String?,
    onOption: (DecisionOption) -> Unit,
    onHelp: () -> Unit
) {
    val isAina = node.prompt.startsWith("[Aina]")
    val accentColor = if (isAina) Color(0xFF7B9FD9) else Primary
    val displayPrompt = node.prompt.removePrefix("[Aina] ")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Label row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isAina) {
                Surface(
                    color = accentColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        "Aina's perspective",
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            } else {
                Text(
                    "What do you do?",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
            IconButton(onClick = onHelp, modifier = Modifier.size(36.dp)) {
                Icon(
                    Icons.Default.Help,
                    contentDescription = "Leave / get help",
                    tint = TextMuted
                )
            }
        }

        // Prompt
        Surface(
            color = DarkSurface,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = displayPrompt,
                style = MaterialTheme.typography.bodyLarge,
                color = OnDarkBackground,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Feedback from previous unsafe choice
        if (feedback != null) {
            Surface(
                color = PrimaryContainer,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = feedback,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnDarkBackground,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Options — shuffled per node so order is unpredictable each session
        val shuffledOptions = remember(node.id) { node.options.shuffled() }
        shuffledOptions.forEach { option ->
            Button(
                onClick = { onOption(option) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (option.safe) accentColor.copy(alpha = 0.2f) else DarkSurfaceVariant
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = option.label,
                    color = if (option.safe) accentColor else TextMuted,
                    fontWeight = if (option.safe) FontWeight.SemiBold else FontWeight.Normal,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}
