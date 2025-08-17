package com.example.videotoaudioconverter.presentation.LanguageScreen

data class LanguagesModel(
    val languageName: String,
    val nativeName: String,
    val shortCode: String,
    val isSelected: Boolean = false
)



val languageList = listOf(
    LanguagesModel("English (UK)", "English", "en"),
    LanguagesModel("Afrikaans", "Afrikaans", "af"),
    LanguagesModel("Albanian", "shqiptare", "sq"),
    LanguagesModel("Amharic", "አማርኛ", "am"),
    LanguagesModel("Arabic", "العربية", "ar"),
    LanguagesModel("Armenian", "հայերեն", "hy"),
    LanguagesModel("Azerbaijan", "Azərbaycan", "az"),
    LanguagesModel("Basque", "baque", "eu"),
    LanguagesModel("Bengali", "বাংলা", "bn"),
    LanguagesModel("Bosnian", "bosanski", "bs"),
    LanguagesModel("Bulgaria", "bulgarian", "bg")
)
