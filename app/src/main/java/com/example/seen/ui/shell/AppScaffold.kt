package com.example.seen.ui.shell

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.seen.audio.LocalSoundManager
import com.example.seen.audio.SoundEffect
import com.example.seen.data.*
import com.example.seen.state.ProgressViewModel
import com.example.seen.ui.apps.*
import com.example.seen.ui.theme.DarkBackground
import com.example.seen.ui.theme.OnDarkBackground
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    appId: AppId,
    vm: ProgressViewModel,
    onBack: () -> Unit,
    onHelp: () -> Unit,
    onLaunchMiniGame: (MiniGame) -> Unit
) {
    val state by vm.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val sound = LocalSoundManager.current
    // Screens with internal back-stack (Messages, Instagram) set this to override
    // the TopAppBar arrow without needing to pop the nav graph.
    var overrideBack by remember { mutableStateOf<(() -> Unit)?>(null) }

    val hostedMiniGame = remember(appId) { MiniGame.entries.find { it.hostApp == appId } }
    val gateState = hostedMiniGame?.gateState(state.highestCompletedOrder)

    LaunchedEffect(appId) { vm.markVisited(appId) }

    val onTriggerTap: () -> Unit = {
        when (gateState) {
            GateState.UNLOCKED, GateState.COMPLETED -> hostedMiniGame?.let {
                sound?.play(SoundEffect.MINIGAME_START)
                onLaunchMiniGame(it)
            }
            GateState.LOCKED -> scope.launch {
                snackbarHostState.showSnackbar(
                    message = ContentRepository.lockedNudge,
                    duration = SnackbarDuration.Short
                )
            }
            null -> {}
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(appId.displayName(), color = OnDarkBackground)
                },
                navigationIcon = {
                    IconButton(onClick = { overrideBack?.invoke() ?: onBack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = OnDarkBackground
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onHelp) {
                        Icon(
                            Icons.Default.Help,
                            contentDescription = "Leave / get help",
                            tint = OnDarkBackground.copy(alpha = 0.7f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (appId) {
                AppId.MESSAGES -> MessagesScreen(
                    conversations = ContentRepository.conversations,
                    gateState = gateState ?: GateState.LOCKED,
                    onTriggerTap = onTriggerTap,
                    onConversationOpened = { contactId -> vm.markConversationOpened(contactId) },
                    onSetBackOverride = { overrideBack = it }
                )
                AppId.PHONE -> PhoneScreen(
                    contactName = ContentRepository.callContactName,
                    summary = ContentRepository.callLogSummary,
                    callLog = ContentRepository.callLog,
                    conversations = ContentRepository.conversations
                )
                AppId.GALLERY -> GalleryScreen(
                    albumTitle = ContentRepository.galleryAlbumTitle,
                    itemCount = ContentRepository.galleryItemCount,
                    items = ContentRepository.gallery,
                    onSetBackOverride = { overrideBack = it }
                )
                AppId.NOTES -> NotesScreen(
                    notes = ContentRepository.notes,
                    gateState = gateState ?: GateState.LOCKED,
                    onTriggerTap = onTriggerTap
                )
                AppId.INSTAGRAM -> InstagramScreen(
                    handle = ContentRepository.ainaHandle,
                    feed = ContentRepository.instagramFeed,
                    gateState = gateState ?: GateState.LOCKED,
                    onTriggerTap = onTriggerTap,
                    onSetBackOverride = { overrideBack = it }
                )
                AppId.X -> XScreen(
                    handle = ContentRepository.ainaHandle,
                    displayName = ContentRepository.xDisplayName,
                    bio = ContentRepository.xBio,
                    location = ContentRepository.xLocation,
                    following = ContentRepository.xFollowing,
                    followers = ContentRepository.xFollowers,
                    tweets = ContentRepository.tweets
                )
                AppId.BROWSER -> BrowserScreen(searches = ContentRepository.searches)
                AppId.MAPS -> MapsScreen(
                    staticImageKey = ContentRepository.mapStaticKey,
                    pins = ContentRepository.mapPins
                )
                AppId.MONITOR -> MonitorScreen(
                    activity = ContentRepository.monitorActivity,
                    gateState = gateState ?: GateState.LOCKED,
                    onTriggerTap = onTriggerTap
                )
                AppId.CLIMAX -> ClimaxScreen(
                    gateState = gateState ?: GateState.LOCKED,
                    onTriggerTap = onTriggerTap
                )
            }
        }
    }
}

fun AppId.displayName(): String = when (this) {
    AppId.MESSAGES -> "Messages"
    AppId.PHONE -> "Phone"
    AppId.GALLERY -> "Gallery"
    AppId.NOTES -> "Notes"
    AppId.INSTAGRAM -> "Instagram"
    AppId.X -> "X"
    AppId.BROWSER -> "Browser"
    AppId.MAPS -> "Maps"
    AppId.MONITOR -> ""
    AppId.CLIMAX -> "SEEN"
}
