package ru.sla.pure.android.core.data.network

import kotlinx.serialization.json.Json

// Общая конфигурация транспорта: новые поля сервера не ломают старый клиент.
val ServerJson = Json {
  ignoreUnknownKeys = true
  explicitNulls = false
}
