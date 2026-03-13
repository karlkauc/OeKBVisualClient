# Dokument-Download

Auf dieser Seite laden Sie Dokumente nach Typ von der FDP herunter. Öffnen Sie die Seite über **Documents** im Menüabschnitt **DOWNLOAD**.

![Dokument-Download](screenshots/14-dokument-download.png)

## Eingabebereich

| Feld | Beschreibung |
|------|-------------|
| **Identifier Type** | Art des Identifiers: `LEI/OeNB-ID` oder `ISIN` (Standard: `LEI/OeNB-ID`) |
| **Identifier** | Eine oder mehrere IDs, getrennt durch Leerzeichen oder Komma |
| **Select File** | Alternativ: IDs aus einer Textdatei laden |

Der Identifier Type bestimmt, welche Art von IDs eingegeben werden kann. Bei `LEI/OeNB-ID` können LEIs und OeNB-IDs gemischt eingegeben werden. Bei `ISIN` werden ISINs erwartet.

## Download-Parameter

| Parameter | Beschreibung | Standard |
|-----------|-------------|----------|
| **Date** | Stichtag für die Dokumente | Heute |
| **Profile** | Datenprofil | `all` |
| **Document Type** | Art des Dokuments | `Factsheet` |

### Verfügbare Dokumenttypen

| Dokumenttyp | Beschreibung |
|-------------|-------------|
| `AIFMD` | Alternative Investment Fund Managers Directive |
| `AnnualReport` | Jahresbericht |
| `AuditReport` | Prüfbericht |
| `Factsheet` | Factsheet |
| `KID` | Key Information Document |
| `Prospectus` | Verkaufsprospekt |
| `PRIIPS-KID` | PRIIPS Key Information Document |
| `Custom (unlisted)` | Benutzerdefinierter Dokumenttyp |

### Benutzerdefinierter Dokumenttyp

Wenn Sie `Custom (unlisted)` wählen, wird ein zusätzliches Eingabefeld aktiviert, in das Sie den gewünschten Dokumenttyp manuell eingeben können.

## Download starten

1. Wählen Sie den **Identifier Type**.
2. Geben Sie eine oder mehrere IDs ein.
3. Wählen Sie Datum, Profil und Dokumenttyp.
4. Klicken Sie auf **Download starten**.
5. Das Ergebnis wird im Ergebnisbereich angezeigt.

## Ergebnisbereich

Der Ergebnisbereich zeigt das heruntergeladene XML schreibgeschützt an.

→ Siehe auch: [Fonds-Download](12-fonds-download.md) · [Regulatory Reporting](15-regulatory-reporting.md)
