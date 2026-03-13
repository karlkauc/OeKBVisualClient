# Download-Statistiken

Diese Seite beantwortet die Frage: **Wer hat meine Daten heruntergeladen?** Öffnen Sie die Seite über **Download Stats** im Menüabschnitt **MONITORING**.

![Download-Statistiken](screenshots/09-download-statistiken.png)

## Filteroptionen

| Filter | Beschreibung |
|--------|-------------|
| **Date From** | Startzeitpunkt (Standard: 30 Tage vor heute) |
| **Date To** | Endzeitpunkt (Standard: heute) |
| **FDP Content** | Art der Daten: `FUND`, `DOC` oder `REG` (Standard: `FUND`) |
| **Identifier** | Optionaler Filter nach Identifier |
| **Search** | Volltextsuche über alle Spalten |

### Aktionsschaltflächen

- **Load Statistics** — Daten mit den gewählten Filtern laden
- **Clear Filters** — Alle Filter zurücksetzen
- **Clear Search** — Suchfeld leeren

## Tabelle

| Spalte | Beschreibung |
|--------|-------------|
| **Type** | Art der Daten, farblich markiert: `FUND` (blau), `DOC` (grün), `REG` (rot) |
| **Content Date** | Inhaltsdatum der Daten |
| **Download Time** | Zeitpunkt des Downloads (Format: `yyyy-MM-dd HH:mm`) |
| **Downloaded By** | Kurzcode(s) der Institute, die Ihre Daten heruntergeladen haben (rot hervorgehoben) |
| **Access** | Art des Zugriffs |
| **Identifiers** | Zusammenfassung der Identifier |
| **Details** | Weitere Informationen (bei langen Texten wird ein Tooltip angezeigt) |
| **Profiles** | Zugehörige Datenprofile |

## Statusanzeige

Am unteren Rand werden der Ladestatus und die Anzahl der angezeigten Einträge angezeigt.

→ Siehe auch: [Aktivitätsjournal](06-aktivitaetsjournal.md) · [Neue Informationen](07-neue-informationen.md)
