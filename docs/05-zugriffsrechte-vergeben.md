# Zugriffsrechte vergeben

Auf dieser Seite verwalten Sie die Zugriffsrechte, die Sie anderen Instituten einräumen. Öffnen Sie die Seite über **Grant Rights** im Menüabschnitt **ACCESS RIGHTS**.

![Zugriffsrechte vergeben](screenshots/05-zugriffsrechte-vergeben.png)

## Übersicht

Die Seite zeigt alle von Ihnen erstellten Zugriffsregeln als TreeTable an. In der Baumstruktur zeigt die oberste Ebene eine Zusammenfassung der Regel, die untergeordneten Einträge zeigen die einzelnen LEIs, OeNB-IDs und ISINs.

### Aktionsschaltflächen

| Schaltfläche | Beschreibung |
|-------------|-------------|
| **New Rule** | Neue Zugriffsregel erstellen |
| **Refresh** | Tabelle neu laden |
| **Export to Excel** | Tabelle als Excel-Datei exportieren |

### Tabellenspalten

| Spalte | Beschreibung |
|--------|-------------|
| **Rule ID** | Kennung der Regel (mit Bearbeiten-/Löschen-Buttons) |
| **Profile** | Datenprofil |
| **Content Type** | `FUND`, `DOC` oder `REG` |
| **Data Suppliers** | Absender und Empfänger |
| **LEI / OENB ID / ISIN** | Zugriffsobjekte (4 Unterspalten) |
| **Fund Name** | Fondsname |
| **From / To** | Gültigkeitszeitraum |
| **Frequency** | Aktualisierungsfrequenz |
| **Delay (Days)** | Verzögerung in Tagen |

### Suche

Das Suchfeld durchsucht alle Spalten der Tabelle. Die Anzahl der Treffer wird angezeigt.

## Regel bearbeiten oder löschen

In der Spalte **Rule ID** stehen bei Haupteinträgen (oberste Ebene) Schaltflächen zum Bearbeiten und Löschen zur Verfügung. Das Löschen einer Regel erfordert eine Bestätigung.

## Neue Regel erstellen

Klicken Sie auf **New Rule** oder bearbeiten Sie eine bestehende Regel, um den Bearbeitungsdialog zu öffnen.

---

## Bearbeitungsdialog (4 Tabs)

Der Dialog gliedert sich in vier Registerkarten.

### Tab 1: Basis-Daten

![Basis-Daten](screenshots/05-tab1-basisdaten.png)

| Feld | Pflicht | Beschreibung |
|------|---------|-------------|
| **Rule ID** | Ja | Eindeutige Kennung der Regel (min. 1 Zeichen) |
| **Content Type** | Ja | Art der Daten: `FUND`, `DOC` oder `REG` |
| **Data Suppliers (Recipients)** | Ja | Liste der Empfänger-Institute. Fügen Sie DDS-Codes über das Eingabefeld und die Schaltfläche **Add** hinzu. |
| **Profiles** | Ja | Liste der Datenprofile. Wählen Sie aus der Dropdown-Liste und klicken Sie **Add**. |
| **Usage** | Nein | Freitext-Beschreibung der Verwendung |
| **Costs paid by Data Supplier** | Nein | Wenn aktiviert, werden die Kosten vom Datenlieferanten getragen. |

**Verfügbare Profile:** `all` · `PKG` · `Vendor` · `allOhneSegmente` · `VendorOhneShareClassPositions` · `VendorMitShareClass`

### Tab 2: Access Objects

![Access Objects](screenshots/05-tab2-access-objects.png)

Definieren Sie hier, auf welche Daten die Regel Zugriff gewährt. Mindestens ein Zugriffsobjekt ist erforderlich.

| Bereich | Format | Beschreibung |
|---------|--------|-------------|
| **LEI** | 20 Zeichen (18 alphanumerisch + 2 Ziffern) | Fund Level Access über Legal Entity Identifier |
| **OeNB-ID** | 2–8 Ziffern | Fund Level Access über OeNB-Identifikationsnummer |
| **Segment ISIN** | 12 Zeichen (2 Buchstaben + 9 alphanumerisch + 1 Ziffer) | Zugriff auf Segment-Ebene |
| **ShareClass ISIN** | 12 Zeichen (2 Buchstaben + 9 alphanumerisch + 1 Ziffer) | Zugriff auf ShareClass-Ebene |

Für jeden Bereich: Geben Sie den Wert im Eingabefeld ein und klicken Sie **Add**. Zum Entfernen wählen Sie den Eintrag und klicken **Remove**.

### Tab 3: Schedule

![Schedule](screenshots/05-tab3-schedule.png)

| Feld | Beschreibung |
|------|-------------|
| **Date From** | Beginn des Gültigkeitszeitraums |
| **Date To** | Ende des Gültigkeitszeitraums |
| **Frequency** | Aktualisierungsfrequenz: `daily` oder `monthly` |
| **Access Delay in Days** | Verzögerung in Tagen (0–365) bis zur Datenfreigabe |

### Tab 4: Advanced

![Advanced](screenshots/05-tab4-advanced.png)

Diese Registerkarte ist nur relevant für die Content Types `DOC` und `REG`.

**Dokumenttypen** (für Content Type `DOC`):

`AIFMD` · `AnnualReport` · `AuditReport` · `Factsheet` · `KID` · `Prospectus` · `PRIIPS-KID`

**Regulatory Reportings** (für Content Type `REG`):

`EMIR` · `KIID` · `EMT` · `TPTSolvencyII` · `PRIIPS`

Wählen Sie die gewünschten Einträge aus der Liste.

## Speichern

Klicken Sie auf **Save & Upload**, um die Regel zu speichern und an die FDP zu übertragen. **Cancel** schließt den Dialog ohne Änderungen.

### Validierung

Beim Speichern werden folgende Pflichtfelder geprüft:

- Rule ID muss mindestens 1 Zeichen lang sein
- Content Type muss gewählt sein
- Mindestens ein Data Supplier (Empfänger) ist erforderlich
- Mindestens ein Profile ist erforderlich
- Mindestens ein Access Object (LEI, OeNB-ID oder ISIN) ist erforderlich

Bei fehlenden Pflichtfeldern wird eine Fehlermeldung angezeigt.

→ Siehe auch: [Zugriffsrechte erhalten](04-zugriffsrechte-erhalten.md)
