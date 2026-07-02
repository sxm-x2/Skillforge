package com.example.skillforge.presentation.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.skillforge.data.remote.api.RetrofitInstance
import com.example.skillforge.data.repository.CourseRepository
import com.example.skillforge.presentation.course.CourseDetailScreen
import com.example.skillforge.presentation.home.HomeScreen
import com.example.skillforge.presentation.lesson.LessonScreen

@Composable
fun NavGraph(navController: NavHostController) {
    val repository = remember { CourseRepository(RetrofitInstance.apiService) }
    val factory = remember { ViewModelFactory(repository) }
    
    NavHost(
        navController = navController,
        startDestination = Screen.Home,
        enterTransition = { slideInHorizontally { it } + fadeIn() },
        exitTransition = { slideOutHorizontally { -it } + fadeOut() },
        popEnterTransition = { slideInHorizontally { -it } + fadeIn() },
        popExitTransition = { slideOutHorizontally { it } + fadeOut() }
    ) {
        composable<Screen.Home> {
            HomeScreen(
                viewModel = viewModel(factory = factory),
                onCourseClick = { courseId ->
                    navController.navigate(Screen.CourseDetail(courseId)) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable<Screen.CourseDetail> { backStackEntry ->
            val route: Screen.CourseDetail = backStackEntry.toRoute()
            CourseDetailScreen(
                courseId = route.courseId,
                viewModel = viewModel(factory = factory),
                onLessonClick = { lessonId ->
                    navController.navigate(Screen.Lesson(route.courseId, lessonId)) {
                        launchSingleTop = true
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable<Screen.Lesson> { backStackEntry ->
            val route: Screen.Lesson = backStackEntry.toRoute()
            LessonScreen(
                courseId = route.courseId,
                lessonId = route.lessonId,
                viewModel = viewModel(factory = factory),
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
