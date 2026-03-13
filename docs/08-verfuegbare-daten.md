# Verfügbare Daten

Auf dieser Seite fragen Sie ab, welche Daten auf der FDP für Ihr Institut verfügbar sind. Öffnen Sie die Seite über **Available Data** im Menüabschnitt **MONITORING**.

![Verfügbare Daten](screenshots/08-verfuegbare-daten.png)

## Abfrageparameter

| Feld | Beschreibung |
|------|-------------|
| **Content Date** | Inhaltsdatum (Stichtag). Wenn gesetzt, werden die Upload-Daten ignoriert. |
| **Upload Date From** | Upload-Zeitraum Beginn (Standard: 7 Tage vor heute) |
| **Upload Date To** | Upload-Zeitraum Ende (Standard: heute) |
| **FDP Content** | Art der Daten: `FUND`, `REG` oder `DOC` (Standard: `FUND`) |
| **Identifier** | Optionaler Filter nach Identifier (LEI, OeNB-ID oder ISIN) |

> **Hinweis:** Der Upload-Zeitraum ist auf maximal 7 Tage beschränkt. Wenn das Content Date gesetzt ist, werden die Upload-Zeiträume ignoriert.

### Aktionsschaltflächen

- **Download** — Abfrage starten und Ergebnisse laden
- **Clear Content Date** — Content Date zurücksetzen

## Ergebnis

Das Abfrageergebnis wird als XML-Text im Ergebnisbereich angezeigt. Dieser Bereich ist schreibgeschützt.

## Statusanzeige

Während der Abfrage wird ein Fortschrittsindikator angezeigt. Der Statustext zeigt den aktuellen Zustand der Abfrage an.

→ Siehe auch: [Neue Informationen](07-neue-informationen.md) · [Download-Statistiken](09-download-statistiken.md)
