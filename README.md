# App Universitaria - Frontend Android

App Android pensada para simplificar el acceso a informacion de UNSAdA.

## Que hace

- Muestra contenido por categorias
- Tiene asistente simple para llevar al usuario a una seccion
- Consulta datos reales desde el backend Spring Boot

## Stack

- Kotlin
- Android Views + ViewBinding
- Retrofit
- RecyclerView

## Conexion con el backend

La app hoy consume esta URL base:

- `http://10.0.2.2:8081/`

Esa configuracion esta en:

- `app/src/main/java/com/example/app_infounsada/ApiFactory.kt`

## Importante sobre `10.0.2.2`

`10.0.2.2` funciona solo cuando la app corre en el emulador de Android Studio.

Significa:

- emulador Android -> `10.0.2.2`
- tu PC host -> `localhost`
- backend Spring Boot -> `localhost:8081`

## Si usas emulador

No tenes que cambiar nada si el backend corre en tu PC en `8081`.

## Si usas celular fisico

Tenes que cambiar la URL base en `ApiFactory.kt`.

Ejemplo:

```kotlin
private const val API_BASE_URL = "http://192.168.0.15:8081/"
```

Donde `192.168.0.15` seria la IP local de tu PC dentro de la misma red WiFi.

## Como levantar todo

Primero backend:

- Proyecto backend:
  `C:\Users\Paul\Downloads\AppInfoUNSAdA-master\app_universitaria-main\app_universitaria-main`
- Levantar MySQL con Docker
- Levantar Spring Boot en puerto `8081`

Despues frontend:

1. Abrir este proyecto en Android Studio
2. Esperar sincronizacion Gradle
3. Ejecutar en emulador Android

## Pantallas

- Home limpia con:
  - titulo
  - boton hamburguesa
  - chatbot / acceso guiado
- Cada categoria abre su propia pantalla

Categorias actuales:

- Calendario Academico
- Becas
- Plataformas
- Tutorias
- Ingresantes
- Tramites
- Carreras

## Compilar por consola

Windows:

```bash
.\gradlew.bat :app:assembleDebug
```

Git Bash / Linux / macOS:

```bash
./gradlew :app:assembleDebug
```

## Si no carga datos

Revisa esto:

- backend levantado en `8081`
- base de datos levantada
- seed cargado
- si usas emulador: mantener `10.0.2.2`
- si usas telefono fisico: cambiar la URL base por la IP local de tu PC
