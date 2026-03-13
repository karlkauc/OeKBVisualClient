# Fonds-Download

Auf dieser Seite laden Sie Fondsdaten über LEI oder OeNB-ID von der FDP herunter. Öffnen Sie die Seite über **Fund Data** im Menüabschnitt **DOWNLOAD**.

![Fonds-Download](screenshots/12-fonds-download.png)

## Eingabebereich

| Feld | Beschreibung |
|------|-------------|
| **LEI / OeNB-ID** | Eine oder mehrere IDs, getrennt durch Leerzeichen oder Komma. LEIs und OeNB-IDs können gemischt eingegeben werden. |
| **Select File** | Alternativ: IDs aus einer Textdatei laden (eine ID pro Zeile) |

Nach dem Laden einer Datei zeigt das Label den Dateinamen und die Anzahl der geladenen IDs an. Mit **Clear** setzen Sie die Eingabe zurück.

## Download-Parameter

| Parameter | Beschreibung | Standard |
|-----------|-------------|----------|
| **Date** | Stichtag für die Fondsdaten | Heute |
| **Profile** | Datenprofil für den Download | `all` |
| **Block Size** | Anzahl der gleichzeitig abgefragten IDs (max. 10) | `10` |

### Verfügbare Profile

| Profil | Beschreibung |
|--------|-------------|
| `all` | Alle verfügbaren Daten |
| `PKG` | PKG-Profil |
| `Vendor` | Vendor-Profil |
| `OeNB` | OeNB-Profil |
| `OENB_Meldungen` | OeNB-Meldungsprofil |

## Download starten

1. Geben Sie eine oder mehrere IDs ein (oder laden Sie eine Datei).
2. Wählen Sie Datum, Profil und Block Size.
3. Klicken Sie auf **Download starten**.
4. Der Fortschritt wird angezeigt (z. B. „Downloading… Download completed for X fund(s)").
5. Das Ergebnis (XML) wird im Ergebnisbereich angezeigt.

## Ergebnisbereich

Der Ergebnisbereich zeigt das heruntergeladene XML schreibgeschützt an. Bei mehreren IDs werden die Ergebnisse zusammengeführt.

→ Siehe auch: [ShareClass-Download](13-shareclass-download.md) · [Dokument-Download](14-dokument-download.md)
