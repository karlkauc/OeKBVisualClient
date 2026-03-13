# Einstellungen

Die Einstellungsseite konfiguriert die Verbindung zur OeKB Fonds Daten Portal und das Verhalten der Anwendung. Öffnen Sie die Seite über **Settings** im Seitenmenü.

![Einstellungen](screenshots/03-einstellungen.png)

## OeKB-Zugangsdaten

| Feld | Beschreibung |
|------|-------------|
| **Username** | Ihr OeKB-FDP-Benutzername |
| **Password** | Ihr OeKB-FDP-Passwort |
| **Data Supplier (DDS)** | Kurzcode Ihres Instituts |

### DDS-Auswahl

Das Feld **Data Supplier (DDS)** bietet eine Dropdown-Liste aller verfügbaren Institutscodes. Das Feld ist editierbar — Sie können auch direkt einen Code eintippen.

Verfügbare Codes:

`FUM` · `3BA` · `AIB` · `AMP` · `CAP` · `BAI` · `EAM` · `EIK` · `FTCFFS` · `GUT` · `CSP` · `KEP` · `CPI` · `CON` · `INV` · `HYP` · `PFSSICAV` · `RIK` · `RAI` · `SKW` · `SEC` · `ASL` · `VBI` · `Dolomiten` · `EDRF` · `EDR` · `Fenion` · `Finee` · `FOCUS` · `NOEVERS` · `RZB` · `UniCredit` · `ssat` · `FactSet` · `ThomsonReuters` · `OeKB`

## Proxy-Konfiguration

Falls Ihr Netzwerk einen Proxy-Server für den Internetzugang verwendet:

| Feld | Beschreibung |
|------|-------------|
| **Use System Proxy Settings** | Aktiviert: Proxy-Einstellungen des Betriebssystems werden automatisch übernommen. Die manuellen Felder werden deaktiviert. |
| **Proxy Host** | Hostname oder IP-Adresse des Proxy-Servers |
| **Proxy Port** | Port des Proxy-Servers |
| **Proxy Username** | Benutzername für die Proxy-Authentifizierung |
| **Proxy Password** | Passwort für die Proxy-Authentifizierung |

### NTLM-Authentifizierung

Wenn Ihr Proxy NTLM-Authentifizierung verwendet, tragen Sie den Benutzernamen im Format `DOMAIN\username` ein. Beispiel: `MEINEFIRMA\m.mustermann`.

## Anwendungsoptionen

| Option | Beschreibung |
|--------|-------------|
| **Overwrite data without asking** | Aktiviert: Beim Upload werden bestehende Daten ohne Rückfrage überschrieben. Andernfalls erscheint eine Fehlermeldung „ID already exists". |
| **Use new ID for Access Rights** | Aktiviert: Beim Erstellen neuer Zugriffsregeln wird automatisch eine neue ID generiert. |

## Development & Testing

| Option | Beschreibung |
|--------|-------------|
| **Use File System Mode (Mock XML Data)** | Aktiviert den Offline-Modus. Die Anwendung liest Daten aus lokalen XML-Dateien statt von der FDP. Primär für Tests gedacht. **Hinweis:** Im File-System-Modus zeigt die Statusleiste „OFFLINE MODUS" an und viele Funktionen sind nicht verfügbar. |
| **Backup Directory** | Verzeichnispfad für lokale Datensicherungen. |

## Einstellungen speichern

Klicken Sie auf **Save Settings**, um alle Änderungen zu speichern. Die Einstellungen werden in der Datei `settings.xml` im Anwendungsverzeichnis gespeichert.

## Einstellungsdatei

Die Konfiguration wird in der Datei `settings.xml` im Anwendungsverzeichnis gespeichert. Bei Problemen kann diese Datei gelöscht werden — die Anwendung erstellt beim nächsten Start eine neue Datei mit Standardwerten.

→ Siehe auch: [Fehlerbehebung](16-fehlerbehebung.md)
