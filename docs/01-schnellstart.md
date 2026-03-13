# Schnellstart

Diese Anleitung führt Sie in wenigen Minuten durch die erste Nutzung des OeKB Visual Client.

## 1. Anwendung starten

Starten Sie die Anwendung über die Desktop-Verknüpfung oder die ausführbare Datei. Das Hauptfenster öffnet sich mit dem Seitenmenü auf der linken Seite.

![Hauptfenster](screenshots/01-hauptfenster.png)

## 2. Einstellungen konfigurieren

Beim ersten Start werden Sie automatisch zur Einstellungsseite weitergeleitet. Klicken Sie alternativ im Seitenmenü auf **Settings**.

### OeKB-Zugangsdaten eingeben

1. Tragen Sie **Username** und **Password** ein (Ihre OeKB-FDP-Zugangsdaten).
2. Wählen Sie im Feld **Data Supplier (DDS)** den Kurzcode Ihres Instituts aus der Dropdown-Liste (z. B. `FUM`, `CAP`, `RIK`).

### Proxy-Konfiguration (falls erforderlich)

Falls Ihr Unternehmen einen Proxy-Server verwendet:

- Aktivieren Sie **Use System Proxy Settings**, um die Systemeinstellungen automatisch zu übernehmen, oder
- tragen Sie **Proxy Host** und **Proxy Port** manuell ein.
- Bei NTLM-Authentifizierung verwenden Sie das Format `DOMAIN\username` im Feld **Proxy Username**.

### Speichern

Klicken Sie auf **Save Settings**. Die Einstellungen werden in der Datei `settings.xml` gespeichert.

→ Detaillierte Beschreibung: [Einstellungen](03-einstellungen.md)

## 3. Serverumgebung wählen

Am unteren Rand des Seitenmenüs finden Sie zwei Schaltflächen:

- **PRODUCTION** — Verbindung zum Produktivserver
- **DEVELOPMENT** — Verbindung zum Testserver

Wählen Sie die gewünschte Umgebung. Die aktive Umgebung wird farblich hervorgehoben.

## 4. Erste Abfrage durchführen

### Beispiel: Fonds-Download

1. Klicken Sie im Menü unter **DOWNLOAD** auf **Fund Data**.
2. Geben Sie eine **LEI** oder **OeNB-ID** in das Eingabefeld ein.
3. Wählen Sie ein **Datum** und ein **Profile** (Standard: `all`).
4. Klicken Sie auf **Download starten**.
5. Das Ergebnis (XML) wird im Textbereich unterhalb angezeigt.

→ Detaillierte Beschreibung: [Fonds-Download](12-fonds-download.md)

## 5. Statusleiste verstehen

Am unteren Rand des Hauptfensters zeigt die Statusleiste:

- Den aktuellen **Benutzernamen**
- Den gewählten **Data Supplier (DDS)**
- Die aktive **Serverumgebung** (PRODUCTION / DEVELOPMENT)

## Nächste Schritte

- [Anwendungsübersicht](02-uebersicht.md) — Alle Funktionsbereiche kennenlernen
- [Zugriffsrechte vergeben](05-zugriffsrechte-vergeben.md) — Datenfreigaben einrichten
- [Daten-Upload](10-daten-upload.md) — XML-Dateien hochladen
