# ShareClass-Download

Auf dieser Seite laden Sie ShareClass-Daten über ISIN von der FDP herunter. Öffnen Sie die Seite über **ShareClass** im Menüabschnitt **DOWNLOAD**.

![ShareClass-Download](screenshots/13-shareclass-download.png)

## Eingabebereich

| Feld | Beschreibung |
|------|-------------|
| **ISIN** | Eine oder mehrere ISINs, getrennt durch Leerzeichen oder Komma |
| **Select File** | Alternativ: ISINs aus einer Textdatei laden (eine ISIN pro Zeile) |

Nach dem Laden einer Datei zeigt das Label den Dateinamen und die Anzahl der geladenen ISINs an. Mit **Clear** setzen Sie die Eingabe zurück.

### ISIN-Format

Eine gültige ISIN besteht aus 12 Zeichen: 2 Buchstaben (Ländercode) + 9 alphanumerische Zeichen + 1 Prüfziffer. Beispiel: `AT0000A1Z456`.

## Download-Parameter

| Parameter | Beschreibung | Standard |
|-----------|-------------|----------|
| **Date** | Stichtag für die ShareClass-Daten | Heute |
| **Profile** | Datenprofil für den Download | `all` |
| **Block Size** | Anzahl der gleichzeitig abgefragten ISINs (max. 10) | `10` |

### Verfügbare Profile

`all` · `PKG` · `Vendor` · `OeNB` · `OENB_Meldungen`

## Download starten

1. Geben Sie eine oder mehrere ISINs ein (oder laden Sie eine Datei).
2. Wählen Sie Datum, Profil und Block Size.
3. Klicken Sie auf **Download starten**.
4. Das Ergebnis (XML) wird im Ergebnisbereich angezeigt.

## Ergebnisbereich

Der Ergebnisbereich zeigt das heruntergeladene XML schreibgeschützt an.

→ Siehe auch: [Fonds-Download](12-fonds-download.md) · [Dokument-Download](14-dokument-download.md)
