![Codecov](https://codecov.io/gh/USERNAME/REPOSITORY/branch/main/graph/badge.svg)

# Customer Service

Der Customer Service ist eine zentrale Komponente des GentleCorp-Ecosystems, die Kundenanfragen und -interaktionen effizient verwaltet. Dieses Dokument beschreibt die Einrichtung, Nutzung und Wartung des Customer Service.

## Features

- **Anfragenmanagement**: Organisiertes Erfassen, Nachverfolgen und Beantworten von Kundenanfragen.
- **Benachrichtigungen**: Automatische Benachrichtigungen bei Statusänderungen oder neuen Anfragen.
- **Integration**: Nahtlose Anbindung an andere Services innerhalb des GentleCorp-Ecosystems.
- **Berichtserstellung**: Erstellen von Berichten über Kundenanfragen und Bearbeitungszeiten.

## Voraussetzungen

### Systemanforderungen

- Java: Version 17 oder höher
- Gradle: Version 7 oder höher
- MongoDB: Für die Speicherung von Anfragen und Kundendaten

### Abhängigkeiten

Installierte globale Tools:

- Gradle für das Build-Management
- Docker (optional, falls Containerisierung verwendet wird)

## Installation

1. **Repository klonen**:
   ```bash
   git clone https://github.com/FlowCraft-AG/customer-service.git
   cd customer-service
   ```

2. **Datenbank konfigurieren**:
   Starte eine lokale MongoDB-Instanz oder verwende eine gehostete Lösung. Stelle sicher, dass die Verbindung in der `application.properties` korrekt konfiguriert ist:
   ```properties
   spring.data.mongodb.uri=mongodb://localhost:27017/Customer
   ```

3. **Build ausführen**:
   ```bash
   gradle clean build
   ```

4. **Applikation starten**:
   ```bash
   gradle bootRun
   ```

5. **Aufruf der Anwendung**:
   Die Anwendung ist standardmäßig unter `http://localhost:8080` erreichbar.

## Nutzung

### API-Endpunkte

| Methode | Endpunkt          | Beschreibung                   |
|---------|-------------------|---------------------------------|
| GET     | /api/customers    | Abrufen aller Kunden           |
| POST    | /api/customers    | Neuen Kunden hinzufügen        |
| GET     | /api/customers/:id| Abrufen eines spezifischen Kunden |
| PUT     | /api/customers/:id| Aktualisieren eines Kunden      |
| DELETE  | /api/customers/:id| Löschen eines Kunden            |

### Beispiele

**Neuen Kunden hinzufügen**:
```bash
curl -X POST http://localhost:8080/api/customers \
  -H 'Content-Type: application/json' \
  -d '{"name": "Max Mustermann", "email": "max@example.com"}'
```

## Tests

Tests können mit folgendem Befehl ausgeführt werden:
```bash
gradle test
```

## Deployment

1. **Docker-Image erstellen**:
   ```bash
   docker build -t customer-service .
   ```

2. **Deployment durchführen** (z. B. Docker):
   ```bash
   docker run -d -p 8080:8080 customer-service
   ```

## Hinweise zu MongoDB

- **Installation**: MongoDB kann lokal installiert werden oder als gehosteter Dienst wie MongoDB Atlas verwendet werden.
- **Konfiguration**: Stelle sicher, dass die URI in der `application.properties` oder `application.yml` korrekt gesetzt ist.
- **Datenmodellierung**: MongoDB eignet sich hervorragend für flexible Datenmodelle, was die Verwaltung verschiedener Kundendaten erleichtert.

## Mitwirken

Beiträge sind willkommen! Bitte erstelle einen Pull-Request oder öffne ein Issue, um Vorschläge zu machen.

## Lizenz

Dieses Projekt steht unter der [MIT-Lizenz](LICENSE).

