# Anwendungsübersicht

Der OeKB Visual Client besteht aus einem Hauptfenster mit drei Bereichen: Statusleiste, Seitenmenü und Inhaltsbereich.

![Anwendungsübersicht](screenshots/02-uebersicht.png)

## Aufbau des Hauptfensters

### Statusleiste (oben)

Die Statusleiste zeigt die aktuelle Konfiguration an:

- **Benutzername** — Der eingestellte OeKB-Benutzername
- **DDS** — Der gewählte Data Supplier Code
- **Serverumgebung** — PRODUCTION oder DEVELOPMENT

### Seitenmenü (links)

Das Seitenmenü gliedert sich in folgende Abschnitte:

#### Einstellungen
| Menüpunkt | Beschreibung |
|-----------|-------------|
| **Settings** | Zugangsdaten, Proxy, Anwendungsoptionen |

#### ACCESS RIGHTS
| Menüpunkt | Beschreibung |
|-----------|-------------|
| **Rights Received** | Empfangene Zugriffsrechte einsehen |
| **Grant Rights** | Zugriffsrechte an andere Institute vergeben |

#### MONITORING
| Menüpunkt | Beschreibung |
|-----------|-------------|
| **Activity Journal** | Upload- und Download-Aktivitäten protokollieren |
| **New Information** | Neue Datenanlieferungen einsehen |
| **Available Data** | Verfügbare Daten auf der FDP abfragen |
| **Download Stats** | Download-Statistiken eigener Daten einsehen |

#### UPLOAD
| Menüpunkt | Beschreibung |
|-----------|-------------|
| **Data Upload** | XML-Dateien per Drag & Drop hochladen |
| **OeNB Meldung** | OeNB-Aggregierung, SecBySec und Check |

#### DOWNLOAD
| Menüpunkt | Beschreibung |
|-----------|-------------|
| **Fund Data** | Fondsdaten herunterladen |
| **ShareClass** | ShareClass-Daten herunterladen |
| **Documents** | Dokumente herunterladen |
| **Regulatory** | Regulatorische Berichte herunterladen |

#### Info
| Menüpunkt | Beschreibung |
|-----------|-------------|
| **About** | Versionsinformationen und Lizenz |

### Inhaltsbereich (rechts)

Der Inhaltsbereich zeigt die jeweils gewählte Funktionsseite an. Klicken Sie auf einen Menüpunkt, um die entsprechende Seite zu laden.

## Serverumgebung umschalten

Am unteren Rand des Seitenmenüs befinden sich zwei Schaltflächen:

- **PRODUCTION** — Produktivserver für den Echtbetrieb
- **DEVELOPMENT** — Testserver für Entwicklungs- und Testzwecke

Die aktive Umgebung wird farblich hervorgehoben. Ein Wechsel der Umgebung wirkt sich auf alle nachfolgenden API-Aufrufe aus.

## Data Supplier (DDS) wechseln

Unterhalb der Serverschaltflächen wird der aktuell gewählte DDS-Code angezeigt. Um den DDS zu wechseln, navigieren Sie zu [Einstellungen](03-einstellungen.md) und ändern Sie den Wert im Feld **Data Supplier (DDS)**.
