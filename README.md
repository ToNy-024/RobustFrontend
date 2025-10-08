# RobustFrontend: Aplicación Android de Tareas Colaborativas

![Kotlin](https://img.shields.io/badge/Kotlin-100%25-7F52FF?style=flat-square)
![Arquitectura](https://img.shields.io/badge/Arquitectura-MVVM-orange?style=flat-square)
![CI](https://img.shields.io/badge/CI-Android%20Studio-3DDC84?style=flat-square)

**RobustFrontend** es una aplicación cliente para Android que implementa un sistema de gestión de tareas y hábitos en grupo. La aplicación permite a los usuarios unirse a grupos, proponer actividades, votar por ellas de forma democrática y realizar un seguimiento de su progreso a través de un sistema de puntuación en un tablero Kanban.

## ✨ Características Principales

- **Autenticación Segura:** Inicio de sesión rápido y seguro utilizando cuentas de Google a través de Firebase Authentication.
- **Gestión Completa de Grupos:**
    - **Crear y Editar:** Los usuarios pueden crear sus propios grupos con un nombre y descripción. El creador tiene permisos para editar esta información más tarde.
    - **Unirse a un Grupo:** Mediante un sistema de códigos de invitación únicos, los usuarios pueden unirse a grupos existentes.
    - **Eliminación Segura:** El creador de un grupo puede eliminarlo, mostrando un diálogo de confirmación para evitar borrados accidentales.
- **Tablero de Tareas Estilo Kanban:**
    - **Columnas de Estado:** Las actividades se organizan visualmente en cuatro columnas: `Pendientes`, `Por Hacer`, `En Progreso` y `Hechas`.
    - **Arrastrar y Soltar:** Los usuarios pueden mover las actividades entre las columnas `Por Hacer`, `En Progreso` y `Hechas` con un simple gesto de deslizar (`swipe`).
- **Sistema Democrático de Actividades:**
    - **Proponer Actividades:** Cualquier miembro del grupo puede proponer nuevas actividades, definiendo su nombre, descripción, frecuencia, dificultad y "nivel de desagrado".
    - **Votación:** Las actividades propuestas aparecen en la columna `Pendientes`, donde los miembros del grupo pueden votar a favor o en contra.
    - **Aprobación Automática:** Una actividad se mueve a la columna `Por Hacer` una vez que alcanza un umbral de votos definido por el backend.
- **Roles y Permisos:**
    - **Administrador:** Los usuarios con rol de administrador tienen acceso a una sección especial para gestionar a todos los usuarios de la aplicación.
    - **Creador:** El creador de un grupo o actividad tiene permisos exclusivos para editarlo o eliminarlo.
- **Dashboard de Usuario:**
    - **Gráfico de Rendimiento:** Muestra un gráfico de barras con el puntaje obtenido por el usuario en los últimos días.
- **Internacionalización (i18n):**
    - La aplicación está preparada para múltiples idiomas, con soporte para **Español, Inglés y Chino Simplificado**.
- **Interfaz de Usuario Moderna:**
    - Uso de componentes de Material Design, avatares de usuario reales (cargados con Coil) y una interfaz limpia y coherente.

## 🛠️ Arquitectura y Tecnologías

Este proyecto sigue las guías de arquitectura recomendadas por Google y utiliza un stack de tecnologías moderno.

- **Arquitectura:** **MVVM (Model-View-ViewModel)** para separar la lógica de la interfaz de usuario de la lógica de negocio.
- **Lenguaje:** 100% **Kotlin**.
- **Asincronía:** **Kotlin Coroutines** para gestionar las llamadas de red y otras operaciones en segundo plano de forma eficiente.
- **Capa de Red:**
    - **Retrofit:** Para realizar las llamadas a la API REST de forma declarativa y segura.
    - **OkHttp (Logging Interceptor):** Para depurar las llamadas de red, registrando todas las peticiones y respuestas en el Logcat.
- **Inyección de Dependencias:** Manual, a través de `ViewModels` y el singleton `RetrofitInstance`.
- **Componentes de Android Jetpack:**
    - **ViewModel:** Para almacenar y gestionar los datos relacionados con la UI de forma consciente del ciclo de vida.
    - **LiveData:** Para notificar a la vista de los cambios en los datos de forma reactiva.
    - **ViewBinding:** Para una interacción segura y eficiente con las vistas XML.
- **Interfaz de Usuario:**
    - **Material Design Components:** Para una apariencia moderna y consistente.
    - **RecyclerView:** Para mostrar listas eficientes de grupos, usuarios y actividades.
    - **Coil (Coroutine Image Loader):** Para cargar y cachear las imágenes de perfil de los usuarios de forma rápida y sencilla.
    - **MPAndroidChart:** Para renderizar el gráfico de barras en el Dashboard.
- **Autenticación:** **Firebase Authentication** (integrado con Google Sign-In).

## 🚀 Cómo Empezar

Para clonar y ejecutar este proyecto en tu máquina local, sigue estos pasos:

1.  **Clona el repositorio:**
2. **Espera a que gradle sincronize**
3. **Cambia las variables de firebase por la propia**
4. **Levanta la API en tu dispositivo** (Recomiendo a la mia, https://github.com/ToNy-024/robustBackend)
5. **Cambia las variables de la API por la propia**
