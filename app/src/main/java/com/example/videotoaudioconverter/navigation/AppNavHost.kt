package com.example.videotoaudioconverter.navigation

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.videotoaudioconverter.presentation.FeedbackScreen.FeedbackScreen
import com.example.videotoaudioconverter.presentation.LanguageScreen.LanguageScreen
import com.example.videotoaudioconverter.presentation.RateUsScreen.RateUsScreen
import com.example.videotoaudioconverter.presentation.SetRingtoneScreen.SetRingtoneScreen
import com.example.videotoaudioconverter.presentation.each_video_preview_and_player_screen.EachVideoPreviewAndPlayerScreen
import com.example.videotoaudioconverter.presentation.main_screen.MainScreen
import com.example.videotoaudioconverter.presentation.setting_screen.SettingScreen
import com.example.videotoaudioconverter.presentation.splash_screen.SplashScreen
import com.example.videotoaudioconverter.presentation.video_to_audio_converter.VideoToAudioConverterScreen
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.example.videotoaudioconverter.presentation.Premium_Screen.Premium_Screen
import com.example.videotoaudioconverter.presentation.PrivacyPolicyScreen.PrivacyPolicyScreen
import com.example.videotoaudioconverter.presentation.audio_player_screen.AudioPlayerScreen
import com.example.videotoaudioconverter.presentation.videos_inside_the_folder_screen.VideosInsideTheFolderScreen
import com.example.videotoaudioconverter.presentation.success_screen.SuccessScreen
import com.example.videotoaudioconverter.presentation.video_player_screen.VideoPlayerScreen
import com.example.videotoaudioconverter.presentation.audio_cutter_screen.AudioCutterScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val appDestination = remember (navController){ AppDestination(navController) }
    NavHost(
        navController = navController,
        startDestination = Routes.SplashScreenRoute


    ) {
        composable<Routes.MainScreenRoute> { it ->
            MainScreen(navigateToSettingScreen = {
//                val data=it.toRoute<Routes.MainScreenRoute>()
                appDestination.navigateToSettingsScreen()
            },navigateToVideToAudioConverter={
                appDestination.navigateToVideoToAudioConverterScreen(it)
            }, navigateToSetRingtoneScreen = {
                appDestination.navigateToSetRingtoneScreen()
            },
                navigateToAudioPlayerScreen={
                    appDestination.navigateToAudioPlayerScreen()
                },
                navigateToAudioCutterSelection = {
                    appDestination.navigateToAudioCutterSelection()
                },
                navigateToPremiumScreen = {
                    appDestination.navigateToPremiumScreen()
                }
            )
        }
        composable<Routes.AudioPlayerRoute> {
            AudioPlayerScreen(
                navigateBack = {
                    appDestination.navigateBack()
                }
            )
        }
        composable<Routes.VideoToAudioConverterRoute> {
            val data=it.toRoute<Routes.VideoToAudioConverterRoute>()
            VideoToAudioConverterScreen(
                fromWhichScreen = data.fromWhichScreen,
                videoClicked={videoUri,videoTitle->
                  appDestination.navigateToEachVideoPreviewAndPlayerScreen(videoUri = videoUri.toString(),videoTitle=videoTitle)
                },
                navigateBack = {
                    appDestination.navigateBack()
                },
                navigateToFolderVideos={
                    appDestination.navigateToAllVideosOfFolderScreen(folderPath = it, fromWhichScreen =data.fromWhichScreen )
                },
                videoClickedForPlayer = {videoUri->
                    appDestination.navigateToVideoPlayerScreen(videoUri.toString())
                }
            )
        }
        composable<Routes.VideoPlayerRoute> {
            val data=it.toRoute<Routes.VideoPlayerRoute>()
            VideoPlayerScreen(videoUri = data.videoUri.toUri())
        }
        composable<Routes.VideosInsideTheFolderRoutes> {
            val data=it.toRoute<Routes.VideosInsideTheFolderRoutes>()
            VideosInsideTheFolderScreen(folderPath = data.folderPath, navigateBack={
                appDestination.navigateBack()
            }, videoClicked = { videoUri,videoTitle->
                appDestination.navigateToEachVideoPreviewAndPlayerScreen(videoUri = videoUri.toString(),videoTitle=videoTitle)
            }, videoClickedForPlayer = {
                appDestination.navigateToVideoPlayerScreen(it.toString())
            }, fromWhichScreen = data.fromWhichScreen)
        }
        composable<Routes.EachVideoPreviewAndPlayerRoute> {
            val videoUri = it.toRoute<Routes.EachVideoPreviewAndPlayerRoute>()
            EachVideoPreviewAndPlayerScreen(videoUri = videoUri.videoUriString.toUri(), fileName =videoUri.videoTitle,navigateToBack={
                appDestination.navigateBack()
            } ,navigateToSuccessScreen={fileName,filePath->
                appDestination.navigateToSuccessScreen(fileName,filePath)
            })
        }
        composable<Routes.SplashScreenRoute> {
            SplashScreen(navigateToHome = {
                appDestination.navigateToMainScreen()
            })
        }

        composable<Routes.SuccessScreenRoute> {
            val routeData=it.toRoute<Routes.SuccessScreenRoute>()
            SuccessScreen(fileName=routeData.fileName, filePath = routeData.filePath)
        }

        composable<Routes.SettingScreenRoute> {
            SettingScreen(navigateToLanguageScreen = { appDestination.navigateToLanguageScreen() },
                navigateToFeedbackScreen = { appDestination.navigateToFeedbackScreen() },
                navigateToRateUsScreen = { appDestination.navigateToRateUsScreen() },
                navigateBackToMainScreen = { appDestination.navigateToMainScreen() },
                navigateToPremiumScreen = { appDestination.navigateToPremiumScreen() },
                navigateToPrivacyPolicyScreen = { appDestination.navigateToPrivacyPolicyScreen() }
            )
        }

        composable<Routes.PremiumScreenRoute>{
            Premium_Screen()
        }
        composable<Routes.LanguageScreenRoute> {
            LanguageScreen(
                navigateBack = { appDestination.navigateToSettingsScreen() }
            )
        }
        composable<Routes.PrivacyPolicyScreenRoute>{
            PrivacyPolicyScreen(navController = navController)
        }

        composable<Routes.FeedbackScreenRoute> {
            FeedbackScreen(navigateBackToSettingsScreen = {
                appDestination.navigateToSettingsScreen()
            })
        }
        composable<Routes.SetRingtoneScreenRoute> {
            SetRingtoneScreen(
                navigateBackToMainScreen = {
                    appDestination.navigateToMainScreen()
                },
                navigateToAudioSelection = { ringtoneType ->
                    appDestination.navigateToAudioSelection(ringtoneType)
                }
            )
        }
        
        composable<Routes.AudioSelectionRoute> {
            val data = it.toRoute<Routes.AudioSelectionRoute>()
            AudioPlayerScreen(
                navigateBack = {
                    appDestination.navigateBack()
                },
                isSetRingtone = true,
                ringtoneType = data.ringtoneType
            )
        }
        
        composable<Routes.AudioCutterSelectionRoute> {
            val data = it.toRoute<Routes.AudioCutterSelectionRoute>()
            AudioPlayerScreen(
                navigateBack = {
                    appDestination.navigateBack()
                },
                isAudioCutter = true,
                cutterType = data.cutterType,
                onAudioSelected = { audioPath, audioTitle ->
                    appDestination.navigateToAudioCutterScreen(audioPath, audioTitle)
                }
            )
        }
        
        composable<Routes.AudioCutterScreenRoute> {
            val data = it.toRoute<Routes.AudioCutterScreenRoute>()
            AudioCutterScreen(
                audioPath = data.audioPath,
                audioTitle = data.audioTitle,
                navigateBack = {
                    appDestination.navigateBack()
                }, navigateToSuccess = { outputPath ->
                    navController.navigate("success_screen/$outputPath")
                }
            )
        }

        composable<Routes.RateUsScreenRoute> {
            RateUsScreen(navigateBackToSettingScreen = { appDestination.navigateToSettingsScreen() },
                navigateToSettingScreen = { appDestination.navigateToSettingsScreen() },
                navigateToFeedbackScreen = { appDestination.navigateToFeedbackScreen() },
                navigateToPlayStore = {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://play.google.com/store/apps/details?id=com.example.yourapp")
                        setPackage("com.android.vending")
                    }
                    context.startActivity(intent)
                }
            )
        }

    }
}