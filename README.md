# CardioTrack TensionApp

## Descripción
Aplicación Android que permite registrar y visualizar lecturas de presión arterial y frecuencia cardíaca. Está construida con **Kotlin**, **Android Jetpack (ViewModel, LiveData, Navigation Component)** y **Material Design**.

## Problema que resuelve
Facilita el seguimiento de la salud cardiovascular, proporcionando una interfaz clara y responsive para usuarios que necesitan monitorear sus signos vitales.

## Arquitectura
- **MVVM** (Model‑View‑ViewModel) con separación de responsabilidades.
- **Navigation Component** para manejo de fragmentos.
- **Repository pattern** para acceso a datos locales (Room) (si existiese).

## Tecnologías
| Categoría | Herramientas |
|-----------|-------------|
| Lenguaje | Kotlin |
| UI | AndroidX, Material Components |
| Build | Gradle Kotlin DSL |
| Versionado | Semantic Versioning |
| Control de versiones | Git (Conventional Commits) |

## Requisitos
- Android Studio Flamingo (2022.2.1) o superior.
- SDK Android 21+.
- Java 11 (para Gradle).

## Instalación
```bash
# Clonar el repositorio
git clone https://github.com/nicoprog4/CardioTrack-TensionApp.git
cd CardioTrack-TensionApp
# Abrir en Android Studio y dejar que Gradle sincronice
```

## Ejecución
1. Selecciona el dispositivo/emulador.
2. Ejecuta **Run > app** en Android Studio.
3. La aplicación mostrará la pantalla principal con navegación inferior.

## Uso básico
- **Dashboard**: muestra los últimos valores de presión y pulso.
- **Perfil**: permite editar datos del usuario.
- **Historial**: (próxima versión) visualizar registros previos.

## Estructura del proyecto
```
app/
├─ src/main/java/com/tension_app/      # Código Kotlin
├─ src/main/res/layout/               # XML layouts
├─ src/main/res/menu/                 # Menú de navegación
└─ src/main/AndroidManifest.xml
```

## Contribuciones
Lee el archivo [CONTRIBUTING.md](CONTRIBUTING.md) para saber cómo colaborar.

## Licencia
Este proyecto está bajo la licencia MIT – ver el archivo [LICENSE](LICENSE).

## Autor
**Nicolás** – <nico@example.com>
