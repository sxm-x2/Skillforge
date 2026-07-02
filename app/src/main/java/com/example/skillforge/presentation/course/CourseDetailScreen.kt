package com.example.skillforge.presentation.course

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.example.skillforge.data.remote.dto.Course
import com.example.skillforge.data.remote.dto.Lesson
import com.example.skillforge.ui.theme.*
import com.example.skillforge.util.UiState
import java.util.Locale
import androidx.core.graphics.toColorInt

@Composable
fun CourseDetailScreen(
    courseId: String,
    viewModel: CourseViewModel,
    onLessonClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(courseId) {
        viewModel.getCourseById(courseId)
    }

    Scaffold(
        containerColor = Background,
        bottomBar = {
            if (uiState is UiState.Success) {
                EnrollBottomBar()
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryTeal)
                    }
                }
                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Error: ${state.message}", color = Color.Red)
                            Button(
                                onClick = { viewModel.getCourseById(courseId) },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
                is UiState.Success -> {
                    CourseDetailContent(
                        course = state.data.course,
                        categoryColor = state.data.categoryColor,
                        onBackClick = onBackClick,
                        onLessonClick = onLessonClick
                    )
                }
            }
        }
    }
}

@Composable
private fun EnrollBottomBar() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Surface,
        shadowElevation = 24.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "PRICE",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Free",
                    color = Primary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .height(64.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = "Enroll now",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun CourseDetailContent(
    course: Course,
    categoryColor: String,
    onBackClick: () -> Unit,
    onLessonClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            HeroSection(course = course, categoryColor = categoryColor, onBackClick = onBackClick)
        }
        item {
            CourseInfoSection(course = course)
        }
        item {
            InstructorSection(course = course)
        }
        item {
            DescriptionSection(course = course)
        }
        item {
            CourseContentHeader(course = course)
        }
        items(course.lessons) { lesson ->
            LessonItem(lesson = lesson, onClick = { onLessonClick(lesson.id) })
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HeroSection(course: Course, categoryColor: String, onBackClick: () -> Unit) {
    val themeColor = try { Color(categoryColor.toColorInt()) } catch (_: Exception) { Primary }
    val isThumbnailValid = course.thumbnailUrl.isNotBlank() && course.thumbnailUrl.startsWith("http")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Primary, Primary.copy(alpha = 0.85f))
                )
            )
    ) {
        // Decorative Circles
        Box(
            modifier = Modifier
                .offset(x = (-60).dp, y = (-60).dp)
                .size(220.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 50.dp, y = 30.dp)
                .size(160.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.06f))
        )

        if (isThumbnailValid) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(course.thumbnailUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.6f
            )
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.35f),
                            Color.Transparent,
                            Color.White.copy(alpha = 0.4f),
                            Color.White.copy(alpha = 0.95f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f))
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
                        .background(Color.White.copy(alpha = 0.25f))
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(bottom = 4.dp)) {
                Surface(
                    color = themeColor,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "// ${course.tags.firstOrNull()?.lowercase() ?: "course"}",
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = course.title,
                    color = TextPrimary,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    lineHeight = 38.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    course.tags.take(3).forEach { tag ->
                        Surface(
                            color = PrimaryLight,
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = tag,
                                color = Primary,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .height(36.dp)
                                    .padding(horizontal = 16.dp)
                                    .wrapContentHeight(Alignment.CenterVertically),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseInfoSection(course: Course) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
        Text(
            text = course.subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp),
            lineHeight = 24.sp
        )
        Row(
            modifier = Modifier.padding(top = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Star,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = " ${course.rating}",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = " ${String.format(Locale.US, "%,d", course.studentsEnrolled)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextSecondary
                )
            }
            Text(
                text = "🕒 ${course.durationHours}h",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextSecondary
            )
            Surface(
                color = PrimaryLight,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = course.level,
                    color = Primary,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}

@Composable
private fun InstructorSection(course: Course) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Border)
    ) {
        Row(
            modifier = Modifier.padding(28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = course.instructor.name.split(" ").mapNotNull { it.firstOrNull() }.joinToString("").take(2).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = course.instructor.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = course.instructor.title,
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = "Follow",
                color = Primary,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun DescriptionSection(course: Course) {
    Text(
        text = course.description,
        modifier = Modifier.padding(24.dp),
        style = MaterialTheme.typography.bodyLarge,
        color = TextSecondary,
        lineHeight = 24.sp
    )
}

@Composable
private fun CourseContentHeader(course: Course) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Course content",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        val totalMinutes = course.lessons.sumOf { it.durationMinutes }
        Text(
            text = "${course.lessons.size} lessons · ${totalMinutes} min",
            color = TextSecondary,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun LessonItem(lesson: Lesson, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Border)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (lesson.isFree) PrimaryLight else Border.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (lesson.isFree) Icons.Default.PlayArrow else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (lesson.isFree) Primary else TextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "${lesson.durationMinutes} min",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
            if (lesson.isFree) {
                Surface(
                    color = PrimaryLight,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "FREE",
                        color = Primary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
