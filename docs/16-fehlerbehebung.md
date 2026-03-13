# Fehlerbehebung & FAQ

Lösungen für häufige Probleme mit dem OeKB Visual Client.

## Häufige Probleme

### „Weiterleitung zu Einstellungen" beim Start

**Ursache:** Es sind keine gültigen OeKB-Zugangsdaten konfiguriert.

**Lösung:** Tragen Sie Benutzername, Passwort und DDS-Code auf der [Einstellungsseite](03-einstellungen.md) ein und klicken Sie **Save Settings**.

---

### „Verbindung fehlgeschlagen" / Keine Serververbindung

**Ursache:** Die Verbindung zum OeKB-Server konnte nicht hergestellt werden.

**Lösung:**
1. Prüfen Sie, ob Ihre Internetverbindung funktioniert.
2. Überprüfen Sie die **Proxy-Einstellungen** in den [Einstellungen](03-einstellungen.md).
3. Falls Ihr Netzwerk einen Proxy verwendet: Aktivieren Sie **Use System Proxy Settings** oder tragen Sie Host und Port manuell ein.
4. Bei NTLM-Authentifizierung: Verwenden Sie das Format `DOMAIN\username`.
5. Prüfen Sie, ob die richtige **Serverumgebung** (PRODUCTION / DEVELOPMENT) gewählt ist.

---

### „ID already exists" beim Upload

**Ursache:** Ein Datensatz mit dieser ID existiert bereits auf der FDP.

**Lösung:** Aktivieren Sie die Option **Overwrite data without asking** in den [Einstellungen](03-einstellungen.md), um bestehende Daten automatisch zu überschreiben.

---

### „OFFLINE MODUS" in der Statusleiste

**Ursache:** Der File-System-Modus ist aktiviert. Die Anwendung arbeitet mit lokalen Mock-Daten.

**Lösung:**
1. Öffnen Sie die [Einstellungen](03-einstellungen.md).
2. Deaktivieren Sie **Use File System Mode (Mock XML Data)**.
3. Stellen Sie sicher, dass gültige OeKB-Zugangsdaten konfiguriert sind.
4. Klicken Sie **Save Settings**.

---

### „Upload-Zeitraum max. 7 Tage" bei Verfügbare Daten

**Ursache:** Die Abfrage auf der Seite [Verfügbare Daten](08-verfuegbare-daten.md) erlaubt maximal einen 7-Tage-Zeitraum.

**Lösung:** Reduzieren Sie den Zeitraum zwischen **Upload Date From** und **Upload Date To** auf maximal 7 Tage. Alternativ setzen Sie ein **Content Date** — in diesem Fall werden die Upload-Daten ignoriert.

---

### Leere Ergebnisse bei Downloads

**Ursache:** Mehrere mögliche Gründe.

**Lösung:**
1. Prüfen Sie den **DDS-Code** in der Statusleiste — stimmt er mit Ihrem Institut überein?
2. Prüfen Sie die **Serverumgebung** — Testdaten sind nur auf DEVELOPMENT verfügbar, Echtdaten nur auf PRODUCTION.
3. Stellen Sie sicher, dass Sie über die entsprechenden **Zugriffsrechte** verfügen (→ [Zugriffsrechte erhalten](04-zugriffsrechte-erhalten.md)).
4. Prüfen Sie das gewählte **Datum** — für manche Daten gibt es nur bestimmte Stichtage.

---

### Keine Daten geladen / „No data available"

**Prüfen Sie der Reihe nach:**
1. Zugangsdaten in den [Einstellungen](03-einstellungen.md)
2. Netzwerk- und Proxy-Einstellungen
3. Serververbindung (PRODUCTION / DEVELOPMENT)

## Proxy-Probleme im Detail

### System-Proxy wird nicht erkannt

Deaktivieren Sie **Use System Proxy Settings** und konfigurieren Sie den Proxy manuell:
- **Proxy Host**: z. B. `proxy.meinefirma.at`
- **Proxy Port**: z. B. `8080`

### NTLM-Authentifizierung schlägt fehl

Stellen Sie sicher, dass der Benutzername im korrekten Format eingegeben ist:
- Format: `DOMAIN\username` (Backslash, nicht Forward-Slash)
- Beispiel: `MEINEFIRMA\m.mustermann`

### Proxy-Passwort wird nicht gespeichert

Das Proxy-Passwort wird zusammen mit den anderen Einstellungen in `settings.xml` gespeichert. Prüfen Sie, ob die Datei Schreibrechte hat.

## Einstellungsdatei zurücksetzen

Bei schwerwiegenden Konfigurationsproblemen können Sie die Einstellungsdatei zurücksetzen:

1. Schließen Sie die Anwendung.
2. Löschen Sie die Datei `settings.xml` im Anwendungsverzeichnis.
3. Starten Sie die Anwendung neu — eine neue Standardkonfiguration wird erstellt.
4. Konfigurieren Sie Ihre Zugangsdaten und Proxy-Einstellungen erneut.

## Log-Dateien

Die Anwendung schreibt detaillierte Protokolle in das Verzeichnis `logs/`. Diese Dateien können bei der Fehleranalyse hilfreich sein.

- **Speicherort**: `logs/` im Anwendungsverzeichnis
- **Format**: Textdateien mit Zeitstempeln
- **Konfiguration**: `log4j2.properties` (bei Bedarf anpassbar)

## Server-URLs

| Umgebung | Verwendung |
|----------|-----------|
| **PRODUCTION** | Echtbetrieb mit Produktivdaten |
| **DEVELOPMENT** | Test- und Entwicklungsumgebung |

Die Server-URLs sind fest in der Anwendung hinterlegt und können nicht manuell geändert werden. Der Wechsel erfolgt über die Schaltflächen am unteren Rand des Seitenmenüs.

→ Siehe auch: [Einstellungen](03-einstellungen.md) · [Glossar](17-glossar.md)
