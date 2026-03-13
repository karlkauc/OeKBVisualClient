# Daten-Upload

Auf dieser Seite laden Sie XML-Dateien an die OeKB Fonds Daten Portal hoch. Öffnen Sie die Seite über **Data Upload** im Menüabschnitt **UPLOAD**.

![Daten-Upload](screenshots/10-daten-upload.png)

## Upload-Bereich

Der zentrale Bereich der Seite zeigt eine Drag-&-Drop-Zone:

- **Ziehen Sie XML-Dateien** aus dem Dateimanager in den markierten Bereich, oder
- **klicken Sie** auf den Bereich, um einen Datei-Auswahldialog zu öffnen.

Es werden ausschließlich **XML-Dateien** akzeptiert.

## Upload-Protokoll

Oberhalb der Drop-Zone befindet sich das Upload-Protokoll (TextArea). Hier werden für jede hochgeladene Datei Statusmeldungen angezeigt:

- Dateiname und Upload-Fortschritt
- Erfolgsmeldungen
- Fehlermeldungen mit Details

### Info-Schaltfläche

Die Info-Schaltfläche in der Kopfzeile zeigt zusätzliche Informationen zum Upload-Prozess an.

## OFI-Erkennung

Wenn eine hochgeladene XML-Datei als OFI-Datei (Offene Fonds Information) erkannt wird, erfolgt die Verarbeitung entsprechend dem OFI-Standard.

## Fehlermeldungen

| Meldung | Ursache | Lösung |
|---------|---------|--------|
| „ID already exists" | Ein Datensatz mit dieser ID existiert bereits | Aktivieren Sie **Overwrite data without asking** in den [Einstellungen](03-einstellungen.md) |
| Verbindungsfehler | Keine Verbindung zum Server | Prüfen Sie Zugangsdaten und Proxy-Einstellungen |

## Hinweise

- Stellen Sie sicher, dass die korrekte **Serverumgebung** (PRODUCTION / DEVELOPMENT) gewählt ist, bevor Sie Daten hochladen.
- Überprüfen Sie den gewählten **DDS-Code** in der Statusleiste.
- Im **File-System-Modus** ist der Upload nicht verfügbar.

→ Siehe auch: [OeNB Meldung](11-oenb-meldung.md) · [Einstellungen](03-einstellungen.md)
