# Neue Informationen

Diese Seite zeigt neue Datenanlieferungen auf der FDP an. Öffnen Sie die Seite über **New Information** im Menüabschnitt **MONITORING**.

![Neue Informationen](screenshots/07-neue-informationen.png)

## Filteroptionen

| Filter | Beschreibung |
|--------|-------------|
| **Content Date** | Inhaltsdatum (Stichtag der Daten) |
| **Upload From** | Upload-Zeitraum Beginn (Standard: 7 Tage vor heute) |
| **Upload To** | Upload-Zeitraum Ende (Standard: heute) |
| **Content Type** | Art der Daten: `All`, `FUND - Fund Data`, `DOC - Documents`, `REG - Regulatory Reporting` |
| **Search** | Volltextsuche über alle Spalten |

### Aktionsschaltflächen

- **Load New Information** — Daten mit den gewählten Filtern laden
- **Clear Filters** — Alle Filter zurücksetzen
- **Clear Search** — Suchfeld leeren

## Tabelle

| Spalte | Beschreibung |
|--------|-------------|
| **Type** | Art der Daten, farblich markiert: `FUND` (blau), `DOC` (grün), `REG` (rot) |
| **Content Date** | Inhaltsdatum der Daten |
| **Upload Time** | Zeitpunkt des Uploads (Format: `yyyy-MM-dd HH:mm`) |
| **Data Supplier** | Kurzcode des Datenlieferanten |
| **Identifiers** | Zusammenfassung der enthaltenen Identifier (LEI, OeNB-ID, ISIN) |
| **Details** | Weitere Informationen (bei langen Texten wird ein Tooltip angezeigt) |
| **Profiles** | Zugehörige Datenprofile |

## Statusanzeige

Am unteren Rand werden der Ladestatus und die Anzahl der angezeigten Einträge angezeigt.

→ Siehe auch: [Verfügbare Daten](08-verfuegbare-daten.md) · [Aktivitätsjournal](06-aktivitaetsjournal.md)
