# Regulatory Reporting

Auf dieser Seite laden Sie regulatorische Berichte von der FDP herunter. Öffnen Sie die Seite über **Regulatory** im Menüabschnitt **DOWNLOAD**.

![Regulatory Reporting](screenshots/15-regulatory-reporting.png)

## Eingabebereich

| Feld | Beschreibung |
|------|-------------|
| **Identifier Type** | Art des Identifiers: `LEI/OeNB-ID` oder `ISIN` (Standard: `LEI/OeNB-ID`) |
| **Identifier** | Eine oder mehrere IDs, getrennt durch Leerzeichen oder Komma |
| **Select File** | Alternativ: IDs aus einer Textdatei laden |

## Download-Parameter

| Parameter | Beschreibung | Standard |
|-----------|-------------|----------|
| **Date** | Stichtag für die Berichte | Heute |
| **Profile** | Datenprofil | `all` |
| **Reporting Type** | Art des regulatorischen Berichts | `all` |

### Verfügbare Reporting-Typen

| Typ | Beschreibung |
|-----|-------------|
| `all` | Alle verfügbaren Berichte |
| `EMIR` | European Market Infrastructure Regulation |
| `KIIDs` | Key Investor Information Documents |
| `EMT` | European MiFID Template |
| `TripartiteTemplateSolvencyII` | Tripartite Template gemäß Solvency II |
| `PRIIPS` | Packaged Retail and Insurance-based Investment Products |

## Download starten

1. Wählen Sie den **Identifier Type**.
2. Geben Sie eine oder mehrere IDs ein.
3. Wählen Sie Datum, Profil und Reporting Type.
4. Klicken Sie auf **Download starten**.
5. Das Ergebnis wird im Ergebnisbereich angezeigt.

## Ergebnisbereich

Der Ergebnisbereich zeigt das heruntergeladene XML schreibgeschützt an.

→ Siehe auch: [Dokument-Download](14-dokument-download.md) · [Fonds-Download](12-fonds-download.md)
