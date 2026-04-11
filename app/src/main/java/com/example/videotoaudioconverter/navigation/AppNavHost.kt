package com.example.videotoaudioconverter.navigation

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.videotoaudioconverter.presentation.audioCutterScreen.AudioCutterScreen
import com.example.videotoaudioconverter.presentation.audioMergeScreen.AudioMergerScreen
import com.example.videotoaudioconverter.presentation.audioPlayerScreen.AudioPlayerScreen
import com.example.videotoaudioconverter.presentation.eachVideoPreviewAndPlayerScreen.EachVideoPreviewAndPlayerScreen
import com.example.videotoaudioconverter.presentation.feedbackScreen.FeedbackScreen
import com.example.videotoaudioconverter.presentation.languageScreen.LanguageScreen
import com.example.videotoaudioconverter.presentation.mainScreen.MainScreen
import com.example.videotoaudioconverter.presentation.premiumScreen.Premium_Screen
import com.example.videotoaudioconverter.presentation.privacyPolicyScreen.PrivacyPolicyScreen
import com.example.videotoaudioconverter.presentation.rateUsScreen.RateUsScreen
import com.example.videotoaudioconverter.presentation.setRingtoneScreen.SetRingtoneScreen
import com.example.videotoaudioconverter.presentation.settingScreen.SettingScreen
import com.example.videotoaudioconverter.presentation.splashScreen.SplashScreen
import com.example.videotoaudioconverter.presentation.successScreen.SuccessScreen
import com.example.videotoaudioconverter.presentation.videoCutterScreen.VideoCutterScreen
import com.example.videotoaudioconverter.presentation.videoPlayerScreen.VideoPlayerScreen
import com.example.videotoaudioconverter.presentation.videoToAudioConverter.VideoToAudioConverterScreen
import com.example.videotoaudioconverter.presentation.videosInsideTheFolderScreen.VideosInsideTheFolderScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val appDestination = remember(navController) { AppDestination(navController) }
    NavHost(
        navController = navController,
        startDestination = Routes.MainScreenRoute


    ) {

        composable<Routes.MainScreenRoute> { it ->
            MainScreen(
                navigateToSettingScreen = {
//                val data=it.toRoute<Routes.MainScreenRoute>()
                appDestination.navigateToSettingsScreen()
            }, navigateToVideToAudioConverter = {
                appDestination.navigateToVideoToAudioConverterScreen(it)
            }, navigateToSetRingtoneScreen = {
                appDestination.navigateToSetRingtoneScreen()
            },
                navigateToAudioPlayerScreen = {
                    appDestination.navigateToAudioPlayerScreen()
                },
                navigateToAudioCutterSelection = {
                    appDestination.navigateToAudioCutterSelection()
                },
                navigateToPremiumScreen = {
                    appDestination.navigateToPremiumScreen()
                },
                navigateToAudioMergeScreen = {
                    appDestination.navigateToAudioMergeScreen()
                },
                navigateTOVideoCutterScreen = {
                    appDestination.navigateToVideoToAudioConverterScreen("video_cutter")
                }
            )
        }

        composable<Routes.VideoCutterScreenRoute> {
            val data = it.toRoute<Routes.VideoCutterScreenRoute>()

            VideoCutterScreen(
                videoUri = data.videoUri.toUri(),
                navigateToSuccessScreen = { filePath ->
                    val fileName = filePath.substringAfterLast("/")
                    appDestination.navigateToSuccessScreen(fileName, filePath)
                },
                navigateBackToHomeScreen = {
                    appDestination.navigateToMainScreen()
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
            val data = it.toRoute<Routes.VideoToAudioConverterRoute>()
            VideoToAudioConverterScreen(
                fromWhichScreen = data.fromWhichScreen,
                videoClicked = { videoUri, videoTitle ->
                    appDestination.navigateToEachVideoPreviewAndPlayerScreen(
                        videoUri = videoUri.toString(),
                        videoTitle = videoTitle
                    )
                },
                navigateBack = {
                    appDestination.navigateBack()
                },
                navigateToFolderVideos = {
                    appDestination.navigateToAllVideosOfFolderScreen(
                        folderPath = it,
                        fromWhichScreen = data.fromWhichScreen
                    )
                },
                videoClickedForPlayer = { videoUri ->

                    if (data.fromWhichScreen == "video_cutter") {
                        appDestination.navigateToVideoCutterScreen(videoUri.toString())
                    } else {
                        appDestination.navigateToVideoPlayerScreen(videoUri.toString())
                    }
                }
            )
        }
        composable<Routes.VideoPlayerRoute> {
            val data = it.toRoute<Routes.VideoPlayerRoute>()
            VideoPlayerScreen(videoUri = data.videoUri.toUri())
        }
        composable<Routes.VideosInsideTheFolderRoutes> {
            val data = it.toRoute<Routes.VideosInsideTheFolderRoutes>()
            VideosInsideTheFolderScreen(folderPath = data.folderPath, navigateBack = {
                appDestination.navigateBack()
            }, videoClicked = { videoUri, videoTitle ->
                appDestination.navigateToEachVideoPreviewAndPlayerScreen(
                    videoUri = videoUri.toString(),
                    videoTitle = videoTitle
                )
            }, videoClickedForPlayer = {
                appDestination.navigateToVideoPlayerScreen(it.toString())
            }, fromWhichScreen = data.fromWhichScreen)
        }
        composable<Routes.EachVideoPreviewAndPlayerRoute> {
            val videoUri = it.toRoute<Routes.EachVideoPreviewAndPlayerRoute>()
            EachVideoPreviewAndPlayerScreen(
                videoUri = videoUri.videoUriString.toUri(),
                fileName = videoUri.videoTitle,
                navigateToBack = {
                    appDestination.navigateToMainScreen()
                },
                navigateToSuccessScreen = { fileName, filePath ->
                    appDestination.navigateToSuccessScreen(fileName, filePath)
                })
        }

        composable<Routes.SuccessScreenRoute> {
            val routeData = it.toRoute<Routes.SuccessScreenRoute>()
            SuccessScreen(fileName = routeData.fileName, filePath = routeData.filePath)
        }

        composable<Routes.SettingScreenRoute> {
            SettingScreen(
                navigateToLanguageScreen = { appDestination.navigateToLanguageScreen() },
                navigateToFeedbackScreen = { appDestination.navigateToFeedbackScreen() },
                navigateToRateUsScreen = { appDestination.navigateToRateUsScreen() },
                navigateBackToMainScreen = { appDestination.navigateToMainScreen() },
                navigateToPremiumScreen = { appDestination.navigateToPremiumScreen() },
                navigateToPrivacyPolicyScreen = { appDestination.navigateToPrivacyPolicyScreen() }
            )
        }

        composable<Routes.PremiumScreenRoute> {
            Premium_Screen()
        }
        composable<Routes.LanguageScreenRoute> {
            LanguageScreen(
                navigateBack = { appDestination.navigateToSettingsScreen() }
            )
        }
        composable<Routes.PrivacyPolicyScreenRoute> {
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
                navigateBackToHomeScreen = {
                    appDestination.navigateToMainScreen()
                }, navigateToSuccess = { outputPath, fileName ->
                    appDestination.navigateToSuccessScreen(
                        fileName = fileName,
                        filePath = outputPath
                    )
                }
            )
        }

        composable<Routes.RateUsScreenRoute> {
            RateUsScreen(
                navigateBackToSettingScreen = { appDestination.navigateToSettingsScreen() },
                navigateToSettingScreen = { appDestination.navigateToSettingsScreen() },
                navigateToFeedbackScreen = { appDestination.navigateToFeedbackScreen() },
                navigateToPlayStore = {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data =
                            Uri.parse("https://play.google.com/store/apps/details?id=com.example.yourapp")
                        setPackage("com.android.vending")
                    }
                    context.startActivity(intent)
                }
            )
        }

        composable<Routes.AudioMergeScreenRoute> {
            AudioMergerScreen(
                onBackClick = { appDestination.navigateToMainScreen() },
                navigateToSuccess = { outputPath, fileName ->
                    appDestination.navigateToSuccessScreen(
                        fileName = fileName,
                        filePath = outputPath
                    )
                }
            )
        }

    }
}

