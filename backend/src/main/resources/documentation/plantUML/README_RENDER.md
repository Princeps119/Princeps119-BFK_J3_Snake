# UML-Diagramme rendern (PNG/SVG)

Die `.puml`-Dateien in diesem Ordner enthalten alle Diagramme. So kannst du sie auf deinem Rechner als **PNG** oder *
*SVG** rendern:

## Option A: PlantUML (Java)

1. Installiere **Java** (falls nicht vorhanden).
2. Lade `plantuml.jar` herunter (von plantuml.com).
3. Führe im Ordner mit den `.puml`-Dateien aus:
   ```bash
   java -jar plantuml.jar -tpng 01_packages.puml 02_main.puml 03_controller.puml 04_services.puml 05_data.puml 06_mongorepo.puml
   java -jar plantuml.jar -tsvg 01_packages.puml 02_main.puml 03_controller.puml 04_services.puml 05_data.puml 06_mongorepo.puml
   ```
   Danach findest du **PNG/SVG** mit gleichem Dateinamen im Ordner.

## Option B: VS Code + PlantUML-Extension

1. Installiere **VS Code**.
2. Installiere die Erweiterung **PlantUML**.
3. Öffne die `.puml`-Dateien und benutze `Alt+D` (Preview) und Export nach PNG/SVG.

## Option C: IntelliJ IDEA Plugin

1. Installiere das **PlantUML Integration** Plugin.
2. Rechtsklick auf `.puml` → Export.

---
**Hinweis:** Die Diagramme sind aus deinem Code generiert und für PPP (PowerPoint) optimiert. SVG ist ideal für
gestochen scharfe Darstellung, PNG für schnelle Einbindung.
