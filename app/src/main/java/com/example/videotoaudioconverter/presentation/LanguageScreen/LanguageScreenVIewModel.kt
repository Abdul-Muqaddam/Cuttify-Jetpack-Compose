//package com.example.videotoaudioconverter.presentation.LanguageScreen
//
//import androidx.lifecycle.ViewModel
//import com.example.videotoaudioconverter.data.AppPreference
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.update
//
//data class LanguageDetails(
//    val languages: List<LanguagesModel> = emptyList(),
//    val selectedLanguage: String = ""
//)
//
//class LanguageScreenViewModel(
//    private val appPreference: AppPreference
//) : ViewModel() {
//
//    private val allLanguages: List<LanguagesModel> = languageList // Keep master copy
//    private val _state = MutableStateFlow(LanguageDetails())
//    val state: StateFlow<LanguageDetails> = _state
//
//    fun onLanguageSelect(languageAbbr: String) {
//        val updatedList = allLanguages.map {
//            it.copy(isSelected = it.shortCode == languageAbbr)
//        }
//        _state.update { currentState ->
//            currentState.copy(
//                selectedLanguage = languageAbbr,
//                languages = updatedList
//            )
//        }
//        appPreference.setLanguageCode(languageAbbr) // Save preference
//    }
//
//    fun onLanguageSearch(query: String) {
//        val filtered = if (query.isBlank()) {
//            allLanguages
//        } else {
//            allLanguages.filter {
//                it.languageName.contains(query, ignoreCase = true)
//            }
//        }
//        _state.update { currentState ->
//            currentState.copy(languages = filtered)
//        }
//    }
//
//    init {
//        val savedLang = appPreference.getLanguageCode()
//        val updatedList = allLanguages.map {
//            it.copy(isSelected = it.shortCode == savedLang)
//        }
//        _state.update {
//            it.copy(
//                selectedLanguage = savedLang,
//                languages = updatedList
//            )
//        }
//    }
//}



package com.example.videotoaudioconverter.presentation.LanguageScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.videotoaudioconverter.data.AppPreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class LanguageDetails(
    val languages: List<LanguagesModel> = emptyList(),
    val selectedLanguage: String = ""
)

class LanguageScreenViewModel(private val appPreference: AppPreference) : ViewModel() {

    private val allLanguages: List<LanguagesModel> = languageList
    private val _state = MutableStateFlow(LanguageDetails(languages = allLanguages))
    val state: StateFlow<LanguageDetails> = _state

    init {
        val saved = appPreference.getLanguageCode()
        Log.d("LanguageVM", "init savedLang = $saved")
        val updated = allLanguages.map { it.copy(isSelected = it.shortCode == saved) }
        _state.update { it.copy(selectedLanguage = saved, languages = updated) }
    }

    fun onLanguageSelect(languageAbbr: String) {
        Log.d("LanguageVM", "onLanguageSelect -> $languageAbbr")
        val updated = allLanguages.map { it.copy(isSelected = it.shortCode == languageAbbr) }
        _state.update {
            it.copy(selectedLanguage = languageAbbr, languages = updated)
        }
        appPreference.setLanguageCode(languageAbbr)
    }

    fun onLanguageSearch(query: String) {
        val filtered = if (query.isBlank()) {
            allLanguages
        } else {
            allLanguages.filter {
                it.languageName.contains(query, ignoreCase = true) ||
                        it.nativeName.contains(query, ignoreCase = true)
            }
        }.map { it.copy(isSelected = it.shortCode == _state.value.selectedLanguage) }

        _state.update { it.copy(languages = filtered) }
    }
}
