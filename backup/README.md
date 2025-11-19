# OeKB FDP Mock Data - Backup Directory

Dieses Verzeichnis enthält Mock XML-Dateien für die Offline-Entwicklung mit dem OeKB Visual Client.

## Verwendung

Um mit Mock-Daten zu arbeiten:

1. In der Anwendung auf die **Settings** Seite gehen
2. "Backup Directory" auf `backup` setzen (oder absoluter Pfad zu diesem Verzeichnis)
3. **File System** Modus aktivieren (Checkbox in der Sidebar)
4. Anwendung neu starten

Die Anwendung liest nun die Mock-Daten aus diesem Verzeichnis statt vom FDP-Server.

## Mock-Dateien Übersicht

### Journal (Activity Log)
**Datei:** `20251119_DOWNLOAD_JOURNAL.xml`
- **Schema:** FundsXML_Journal_1.0.3.xsd
- **Inhalt:** 10 Journal-Einträge
  - 5 Upload-Aktionen (UL): FXML_DATA, DOC, AR
  - 5 Download-Aktionen (DL): FXML_DATA, REG
  - Verschiedene Data Suppliers: FUM, 3BA, AIB, RZB, AMP, UniCredit, OeKB, FactSet, CAP, NOEVERS
- **Verwendung:** Activity Journal Seite
- **Features:**
  - Zeitstempel über 2 Tage verteilt
  - Verschiedene Dokumenttypen
  - Erfolgreiche und fehlgeschlagene Uploads
  - API Bulk Downloads

### NewInformation / Available Data
**Datei:** `20251119_DOWNLOAD_AVAILABLE_DATA.xml`
- **Schema:** FundsXML_NewInformation_1.0.2.xsd
- **Inhalt:** 9 Benachrichtigungen über neue Daten
  - 3 FUND Einträge (Fondsdaten)
  - 4 DOC Einträge (Prospectus, KID, Factsheet, Annual Report)
  - 2 REG Einträge (EMIR, PRIIPS)
- **Verwendung:** New Information Seite
- **Features:**
  - LEI und ISIN Identifiers
  - Multiple Data Suppliers
  - Document Types: Prospectus, KID, Factsheet, AnnualReport
  - Verschiedene Sprachen (de, en)
  - Profile-Zuordnungen

### Downloaded Information (Download Statistics)
**Datei:** `20251119_DOWNLOAD_OWN_DATA_DOWNLOADED.xml`
- **Schema:** FundsXML_DownloadedInformation_1.0.0.xsd
- **Inhalt:** 10 Download-Statistik Einträge
  - Zeigt WER (Data Supplier) WANN (DateTime) WAS (Content) heruntergeladen hat
  - Mix aus Access Rules und OWNER (eigene Daten)
  - FUND, DOC und REG Content Types
- **Verwendung:** Download Statistics / "Own Data Downloaded" Seite
- **Features:**
  - AccessRuleID vs. OWNER Unterscheidung
  - Multiple ISINs pro Entry
  - LEI und OeNB_Identnr
  - Document und Regulatory Types
  - Verschiedene Data Supplier Typen (CB, IC, Vendor)

### Upload Reply (Validierungsergebnisse)

#### Erfolgreicher Upload
**Datei:** `MOCK_UPLOAD_REPLY_SUCCESS.xml`
- **Schema:** FundsXML_Reply_3.0.1.xsd
- **Inhalt:** Erfolgreiche Validierung
  - OverallStatus: OK
  - 3 StatusInfo Einträge (2x OK, 1x OK_INFO)
  - Fund Management AG (FUM)
- **Verwendung:** Data Upload Seite (zukünftig)

#### Upload mit Fehlern
**Datei:** `MOCK_UPLOAD_REPLY_WITH_ERRORS.xml`
- **Schema:** FundsXML_Reply_3.0.1.xsd
- **Inhalt:** Fehlgeschlagene Validierung
  - OverallStatus: ERROR
  - 6 StatusInfo Einträge
    - 3x ERROR (Invalid ISIN, NAV out of range, Missing field)
    - 1x OK_INFO (Name updated)
    - 1x INFO (LEI verification)
    - 1x OK (Name validated)
- **Verwendung:** Data Upload Seite - Fehlerfall testen

#### Access Rules Upload
**Datei:** `MOCK_UPLOAD_REPLY_ACCESS_RULES.xml`
- **Schema:** FundsXML_Reply_3.0.1.xsd
- **Inhalt:** Access Rules Validierung
  - OverallStatus: OK_INFOS
  - 3 Access Rules
    - 2 erfolgreich (OK)
    - 1 fehlgeschlagen (ERROR - Invalid grantee)
- **Verwendung:** Access Rights Grant Seite

## Dateinamen-Konvention

Die Anwendung sucht nach Dateien mit folgenden Pattern:

- Journal: `*DOWNLOAD_JOURNAL.xml`
- NewInformation: `*DOWNLOAD_AVAILABLE_DATA.xml` oder `*DOWNLOAD_NEWINFORMATION.xml`
- DownloadedInformation: `*DOWNLOAD_OWN_DATA_DOWNLOADED.xml` oder `*DOWNLOADED_INFORMATION.xml`

Das Präfix (z.B. `20251119_`) dient der Versionierung - die Anwendung wählt automatisch die neueste Datei.

## Eigene Mock-Daten erstellen

1. Kopiere eine bestehende Mock-Datei
2. Benenne sie mit neuem Datumspräfix (z.B. `20251120_...`)
3. Bearbeite den XML-Inhalt
4. Validiere gegen das entsprechende XSD-Schema in `src/main/resources/xsd/`
5. Die Anwendung verwendet automatisch die neueste Datei

## Wichtige Hinweise

- **Nicht in Git committen:** Produktions-Daten gehören NICHT in dieses Verzeichnis
- Dieses Verzeichnis ist in `.gitignore` eingetragen (nur Mock-Dateien werden committed)
- Mock-Daten verwenden realistische, aber fiktive Werte
- LEI Codes folgen dem Format, sind aber nicht real
- ISINs folgen dem Format, sind aber nicht real

## Zurück zum Produktions-Modus

1. Settings Seite öffnen
2. **File System** Checkbox deaktivieren
3. FDP Server Credentials eingeben
4. Anwendung neu starten

Die Anwendung verbindet sich nun wieder mit dem echten OeKB FDP Server.

## Datenschutz

⚠️ **WICHTIG:** Speichere NIEMALS echte Produktionsdaten in diesem Verzeichnis!
- Verwende nur anonymisierte/synthetische Testdaten
- Keine echten LEI Codes von realen Unternehmen
- Keine echten ISINs von realen Fonds
- Keine echten Benutzernamen oder E-Mail-Adressen

## Unterstützte Funktionen

### ✅ Voll implementiert mit Mock-Daten
- **Activity Journal** - zeigt Upload/Download Aktivitäten
- **New Information** - zeigt neu verfügbare Daten
- **Download Statistics** - zeigt wer eigene Daten heruntergeladen hat

### ⏳ Noch nicht implementiert
- **Upload Reply Integration** - wird in Data Upload Seite integriert
- **OeNBCheck** - für OeNB Meldung Seite
- **AccessRules** - bereits Modelle vorhanden, UI-Integration steht aus

## Schema-Referenzen

Alle XML-Dateien basieren auf den offiziellen OeKB FDP XSD-Schemas:

- `FundsXML_Journal_1.0.3.xsd`
- `FundsXML_NewInformation_1.0.2.xsd`
- `FundsXML_DownloadedInformation_1.0.0.xsd`
- `FundsXML_Reply_3.0.1.xsd`

Die Schemas befinden sich in `src/main/resources/xsd/`.
