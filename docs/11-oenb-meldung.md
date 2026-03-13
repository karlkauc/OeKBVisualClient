# OeNB Meldung

Die OeNB-Meldungsseite ermöglicht die Abfrage von OeNB-Aggregierungen, Securities-by-Securities-Daten und Validierungsprüfungen. Öffnen Sie die Seite über **OeNB Meldung** im Menüabschnitt **UPLOAD**.

![OeNB Meldung](screenshots/11-oenb-meldung.png)

## Eingabebereich

| Feld | Beschreibung |
|------|-------------|
| **OeNB-ID** | Eine oder mehrere OeNB-IDs, getrennt durch Leerzeichen oder Komma |
| **Select File** | Alternativ: OeNB-IDs aus einer Textdatei laden (eine ID pro Zeile) |
| **Date** | Stichtag für die Abfrage |

Nach dem Laden einer Datei zeigt das Label den Dateinamen und die Anzahl der geladenen IDs an. Mit **Clear** setzen Sie die Eingabe zurück.

## Tab 1: OeNB Aggregierung

Aggregierte Fondsdaten gemäß OeNB-Standard.

| Element | Beschreibung |
|---------|-------------|
| **Exclude Invalid Data** | Wenn aktiviert, werden ungültige Datensätze aus dem Ergebnis ausgeschlossen |
| **Download Aggregierung** | Startet die Abfrage |
| **Ergebnisbereich** | Zeigt das XML-Ergebnis der Aggregierung |

## Tab 2: OeNB SecBySec

Securities-by-Securities-Daten (Einzelwertpapier-Ebene).

| Element | Beschreibung |
|---------|-------------|
| **Exclude Invalid Data** | Wenn aktiviert, werden ungültige Datensätze ausgeschlossen |
| **SecBySec Download** | Startet die Abfrage |
| **Ergebnisbereich** | Zeigt das XML-Ergebnis |

## Tab 3: OeNB Check

Validierungsprüfung der gemeldeten Daten.

| Element | Beschreibung |
|---------|-------------|
| **Filterauswahl** | `Alle` — Alle Ergebnisse anzeigen |
| | `Nur valide` — Nur gültige Datensätze anzeigen |
| | `Nur invalide` — Nur ungültige Datensätze anzeigen |
| **Download Check** | Startet die Validierungsprüfung |
| **Ergebnisbereich** | Zeigt das Validierungsergebnis |

## Fortschrittsanzeige

Während der Abfrage wird ein Fortschrittsindikator angezeigt. Der Status wird im Statuslabel aktualisiert.

→ Siehe auch: [Daten-Upload](10-daten-upload.md)
