# README

## Überblick
Dieses Programm beschreibt eine Desktop-Anwendung eines Texteditors mit **Front-End (UI)** und **Back-End (Logik)**.  
Der Fokus liegt auf **Dateiverwaltung** (*New/Open/Save/Export etc.*) und **Textbearbeitung** (*Suchen/Ersetzen, Undo/Redo, Änderungsstatus*).

---

## Ziel & Funktionsumfang

### Front-End (UI)
Das Front-End stellt die Benutzeroberfläche bereit und leitet Benutzeraktionen an das Back-End weiter.

#### Init Fenster
- Startet die Anwendung mit Hauptfenster (Editor-Ansicht).
- Stellt Basislayout bereit (z.B. Editorfläche, Statusleiste).

#### Init Menüleiste
- Erstellt Menüstruktur (z. B. **Datei**, **Bearbeiten**, **Suchen**).
- Verknüpft Menüeinträge mit Back-End-Operationen.

#### Init Symbolleiste
- Erstellt kurzwahl Symbolleiste (z. B. **Datei**, **Bearbeiten**, **Suchen**).
- Verknüpft Symbole mit Back-End-Operationen.

#### Init Popup „Suchen“
- Separates Dialogfenster oder Overlay für Suchoperationen.
- Navigation (nächstes/vorheriges Treffer)
- Optional:
  - Ersetzen

---

### Back-End (Logik)

#### File-Handling
Zuständig für alle Dateioperationen sowie den Austausch mit dem Text-Handling (z. B. Inhalte laden/speichern, Änderungsstatus abfragen).

##### New
- Erstellt ein neues, leeres Dokument.
- Setzt Dateipfad/Metadaten zurück, markiert Status als „unverändert“.

##### Open
- Öffnet bestehende Datei, lädt Inhalt in den Editor.
- Aktualisiert Dateipfad, Dateiname.
- Stimmt sich mit Text-Handling ab (z. B. Bearbeitung darf stattfinden).

##### Close
- Schließt aktuelles Dokument.
- Prüft vorher ungespeicherte Änderungen und fordert ggf. Speichern an.

##### Save
- Speichert aktuellen Inhalt an den bekannten Pfad.
- Falls kein Pfad existiert: delegiert an **Save as**.

##### Save as
- Speichert Inhalt unter neuem Pfad/Dateinamen.
- Aktualisiert internen „current path“.
