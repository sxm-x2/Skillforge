package com.example.skillforge.presentation.lesson

import kotlin.OptIn
import androidx.annotation.OptIn as AndroidxOptIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.PlaybackException
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay
import com.example.skillforge.data.remote.dto.Course
import com.example.skillforge.data.remote.dto.Lesson
import com.example.skillforge.ui.theme.*
import com.example.skillforge.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    courseId: String,
    lessonId: String,
    viewModel: LessonViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(courseId, lessonId) {
        viewModel.getLessonData(courseId, lessonId)
    }

    Scaffold(
        containerColor = Background
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                }
                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Error: ${state.message}", color = Color.Red)
                            Button(
                                onClick = { viewModel.getLessonData(courseId, lessonId) },
                                colors = ButtonDefaults.buttonColors(containerColor = Primary)
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
                is UiState.Success -> {
                    LessonContent(
                        course = state.data.course,
                        currentLesson = state.data.currentLesson,
                        onBackClick = onBackClick,
                        onLessonClick = { newLessonId ->
                            viewModel.getLessonData(courseId, newLessonId)
                        }
                    )
                }
            }
        }
    }
}

@AndroidxOptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LessonContent(
    course: Course,
    currentLesson: Lesson,
    onBackClick: () -> Unit,
    onLessonClick: (String) -> Unit
) {
    val context = LocalContext.current
    val isVideoValid = currentLesson.videoUrl.isNotBlank() && 
                      (currentLesson.videoUrl.startsWith("http") || currentLesson.videoUrl.startsWith("content"))

    val exoPlayer = remember(currentLesson.id) {
        if (isVideoValid) {
            ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(currentLesson.videoUrl))
                prepare()
                playWhenReady = true
            }
        } else null
    }

    var currentPosition by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }
    var isPlaying by remember { mutableStateOf(exoPlayer?.isPlaying ?: false) }
    var playbackError by remember { mutableStateOf<String?>(null) }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    duration = exoPlayer?.duration?.coerceAtLeast(0L) ?: 0L
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                playbackError = error.message
            }
        }
        exoPlayer?.addListener(listener)
        onDispose {
            exoPlayer?.removeListener(listener)
            exoPlayer?.release()
        }
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying && exoPlayer != null) {
            while (true) {
                currentPosition = exoPlayer.currentPosition
                delay(1000L)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(Color.Black)
        ) {
            if (isVideoValid && exoPlayer != null) {
                AndroidView(
                    factory = {
                        PlayerView(it).apply {
                            player = exoPlayer
                            useController = false 
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "Video preview not available",
                            color = Color.White.copy(alpha = 0.5f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Fullscreen,
                        contentDescription = "Fullscreen",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            var isPlayingLocally by remember { mutableStateOf(true) }
            LaunchedEffect(isPlaying) {
                isPlayingLocally = isPlaying
            }
            
            if (isVideoValid && playbackError == null) {
                IconButton(
                    onClick = {
                        if (exoPlayer?.isPlaying == true) {
                            exoPlayer.pause()
                        } else {
                            exoPlayer?.play()
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        imageVector = if (isPlayingLocally) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = Color.Black
                    )
                }
            } else if (isVideoValid) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Playback Error",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = playbackError ?: "Unknown error",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )
                    Button(
                        onClick = {
                            playbackError = null
                            exoPlayer?.prepare()
                            exoPlayer?.play()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Retry")
                    }
                }
            }

            // Duration Bar Section
            if (isVideoValid) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatTime(currentPosition),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Slider(
                        value = if (duration > 0) currentPosition.toFloat() / duration else 0f,
                        onValueChange = {
                            exoPlayer?.seekTo((it * duration).toLong())
                            currentPosition = exoPlayer?.currentPosition ?: 0L
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Primary,
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp),
                        thumb = {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        },
                        track = { sliderState ->
                            SliderDefaults.Track(
                                sliderState = sliderState,
                                modifier = Modifier.height(8.dp),
                                thumbTrackGapSize = 0.dp,
                                trackInsideCornerSize = 4.dp,
                                colors = SliderDefaults.colors(
                                    activeTrackColor = Primary,
                                    inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                                )
                            )
                        }
                    )

                        Text(
                            text = formatTime(duration),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        LessonInfoSection(course = course, lesson = currentLesson)

        LessonTabs(course = course, currentLesson = currentLesson, onLessonClick = onLessonClick)
    }
}

@Composable
private fun LessonInfoSection(course: Course, lesson: Lesson) {
    Column(modifier = Modifier.padding(24.dp)) {
        Text(
            text = "LESSON ${course.lessons.indexOf(lesson) + 1} · ${course.title.uppercase()}",
            color = Primary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = lesson.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp),
            color = TextPrimary
        )
        Text(
            text = lesson.content,
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            modifier = Modifier.padding(top = 8.dp),
            lineHeight = 24.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LessonTabs(
    course: Course,
    currentLesson: Lesson,
    onLessonClick: (String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Lessons", "Notes", "Resources")

    Column {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Surface,
            contentColor = Primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Primary,
                    height = 3.dp
                )
            },
            divider = { HorizontalDivider(color = Border) }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    },
                    unselectedContentColor = TextSecondary
                )
            }
        }

        when (selectedTab) {
            0 -> LessonsList(course = course, currentLesson = currentLesson, onLessonClick = onLessonClick)
            1 -> PlaceholderTab("No notes available for this lesson.")
            2 -> PlaceholderTab("No resources available.")
        }
    }
}

@Composable
private fun LessonsList(
    course: Course,
    currentLesson: Lesson,
    onLessonClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(course.lessons) { lesson ->
            LessonListItem(
                lesson = lesson,
                isActive = lesson.id == currentLesson.id,
                onClick = { onLessonClick(lesson.id) }
            )
        }
    }
}

@Composable
private fun LessonListItem(
    lesson: Lesson,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) PrimaryLight else Surface
        ),
        border = if (isActive) androidx.compose.foundation.BorderStroke(1.5.dp, Primary.copy(alpha = 0.5f)) else androidx.compose.foundation.BorderStroke(1.dp, Border)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isActive) Primary else if (lesson.isFree) PrimaryLight else Border.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isActive) Icons.Default.Pause else if (lesson.isFree) Icons.Default.PlayArrow else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (isActive) Color.White else if (lesson.isFree) Primary else TextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) Primary else TextPrimary
                )
                Text(
                    text = if (isActive) "Now playing · ${lesson.durationMinutes} min" else "${lesson.durationMinutes} min",
                    color = if (isActive) Primary.copy(alpha = 0.8f) else TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium
                )
            }
            if (!isActive && lesson.isFree) {
                Surface(
                    color = PrimaryLight,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "FREE",
                        color = Primary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaceholderTab(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message, color = TextSecondary)
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
}
