# Network Management System

Prosta aplikacja Spring Boot, która wczytuje topologię sieci z pliku `topology.json`, przechowuje stan urządzeń w pamięci i udostępnia informacje o urządzeniach osiągalnych z wybranego urządzenia.

## Technologie

- Java 21
- Spring Boot 4.1.1
- Maven
- Jackson do odczytu `topology.json`
- Server-Sent Events (SSE)

## Wymagania

- JDK 21
- Windows: można użyć dołączonego Maven Wrappera (`mvnw.cmd`)

## Uruchomienie

W katalogu projektu uruchom:

```powershell
.\mvnw.cmd spring-boot:run
```

Aplikacja uruchamia się domyślnie na porcie `8080`.

## Frontend demonstracyjny

Projekt zawiera opcjonalny, prosty interfejs demonstracyjny napisany w React + Vite. Frontend znajduje się w katalogu `frontend/`.

Do jego uruchomienia wymagane są Node.js i npm.

Uruchom backend w pierwszym terminalu:

```powershell
.\mvnw.cmd spring-boot:run
```

Backend działa na porcie `8080`. W drugim terminalu uruchom frontend:

```powershell
cd frontend
npm install
npm run dev
```

Frontend Vite działa domyślnie na porcie `5173`.

Interfejs pozwala wybrać urządzenie do monitorowania, otwiera połączenie SSE, pokazuje aktualnie osiągalne urządzenia i pozwala zmienić stan urządzenia przez PATCH. Zmiany reachability są aktualizowane w interfejsie w czasie rzeczywistym na podstawie zdarzeń `INITIAL_STATE`, `ADDED` i `REMOVED`.

## Endpointy

### PATCH `/devices/{id}`

Zmienia stan aktywności urządzenia.

Przykładowe żądanie:

```http
PATCH http://localhost:8080/devices/15
Content-Type: application/json

{
  "active": false
}
```

Dla nieistniejącego urządzenia zwracany jest status `404`. Brak pola `active` albo wartość `null` powoduje status `400`.

### GET `/devices/{id}/reachable-devices`

Otwiera subskrypcję SSE z informacjami o urządzeniach osiągalnych z urządzenia o podanym identyfikatorze.

```http
GET http://localhost:8080/devices/7/reachable-devices
Accept: text/event-stream
```

Dla nieistniejącego urządzenia zwracany jest status `404`.

W ramach subskrypcji wysyłane są zdarzenia:

- `INITIAL_STATE` — początkowy zbiór osiągalnych urządzeń,
- `ADDED` — urządzenie stało się osiągalne,
- `REMOVED` — urządzenie przestało być osiągalne.

Reachability jest obliczane algorytmem BFS. Nieaktywne urządzenia nie są używane jako część ścieżki.

## Testy

Uruchomienie całego zestawu testów:

```powershell
.\mvnw.cmd clean test
```

Testy obejmują logikę reachability dla wymaganych scenariuszy oraz podstawowe przypadki API.

Wyniki czterech wymaganych scenariuszy demonstracyjnych znajdują się w pliku [DEMONSTRATION.md](src/test/java/com/networkmanagement/networkmanagementsystem/service/DEMONSTRATION.md).
