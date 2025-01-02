package com.example.jetpackcompose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.api.WeatherApiService
import com.example.jetpackcompose.data.ForecastItem
import com.example.jetpackcompose.data.WeatherData
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WeatherViewModel : ViewModel() {

    private val _currentWeather = MutableStateFlow<WeatherData?>(null)
    val currentWeather: StateFlow<WeatherData?> = _currentWeather

    private val _forecast = MutableStateFlow<List<ForecastItem>>(emptyList())
    val forecast: StateFlow<List<ForecastItem>> = _forecast

    private val _iconUrl = MutableStateFlow<String?>(null)
    val iconUrl: StateFlow<String?> get() = _iconUrl

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    fun fetchWeatherData(city: String, apiKey: String) {
        viewModelScope.launch {
            try {
                val weatherResponse = WeatherApiService.fetchWeather(city, apiKey)
                if (weatherResponse != null) {
                    _currentWeather.value = weatherResponse
                    fetchWeatherIcon(weatherResponse.weather.firstOrNull()?.icon.orEmpty())
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = "Failed to fetch weather. Please check your API key or city name."
                }
            } catch (e: Exception) {
                _errorMessage.value = "An error occurred: ${e.localizedMessage}"
            }
        }
    }

    fun fetchForecastData(city: String, apiKey: String) {
        viewModelScope.launch {
            try {
                // Abrufen der Vorhersagedaten über den API-Service
                val forecastResponse = WeatherApiService.fetchForecast(city, apiKey)
                if (forecastResponse != null) {
                    // Speichern der erhaltenen Vorhersagedaten und Zurücksetzen von Fehlermeldungen
                    _forecast.value = forecastResponse.list
                    _errorMessage.value = null
                } else {
                    // Setzen einer Fehlermeldung, falls keine Daten empfangen wurden
                    _errorMessage.value = "Failed to fetch forecast. Please check your API key or city name."
                }
            } catch (e: Exception) {
                // Setzen einer allgemeinen Fehlermeldung bei einer Ausnahme
                _errorMessage.value = "An error occurred: ${e.localizedMessage}"
            }
        }
    }

    private fun fetchWeatherIcon(iconId: String) {
        if (iconId.isNotEmpty()) {
            _iconUrl.value = "https://openweathermap.org/img/wn/$iconId@2x.png"
        }
    }
}
