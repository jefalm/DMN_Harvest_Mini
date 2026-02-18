# DMN_Harvest_Mini

Minimal high-performance app that demonstrates a low-friction Glance widget
for quickly capturing a timestamp (a minimal "objectify" action). The widget
performs a short haptic handshake and stores a single timestamp in widget state.

Key choices to keep binary small and fast:
- Jetpack Glance for remote surface widgets (no heavy Activity lifecycle)
- Jetpack Compose for a very small activity UI
- No networking libraries
- R8/minify + resource shrinking enabled in `app/build.gradle.kts`

How to build (run in project root):
```powershell
.\gradlew.bat :app:assembleDebug
```
