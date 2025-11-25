# Patas y Colas 🐾

Patas y Colas es una solución integral compuesta por una aplicación móvil Android nativa y un backend en Spring Boot, diseñada para gestionar la salud y el historial de vacunación de mascotas de manera eficiente.

## 1. Integrantes del Proyecto
* **Samuel Mansilla**
* **Francisco Mardones**


## 2. Funcionalidades Principales

### Aplicación Móvil & Backend
* **Seguridad y Autenticación:** Registro e inicio de sesión seguro utilizando JWT (JSON Web Tokens) para proteger la comunicación entre la app y el servidor.
* **Gestión de Perfiles de Mascotas:** Creación y almacenamiento de fichas con datos como nombre, raza, edad, peso y género.
* **Historial Médico:** Visualización y control de las mascotas registradas por cada usuario.
* **Persistencia en la Nube:** Base de datos Oracle gestionada a través de una API RESTful desarrollada en Spring Boot.
* **Datos Curiosos:** Integración con API externa para mostrar datos aleatorios sobre gatos (CatFacts).



## 3. Endpoints Utilizados

### A. Microservicio Propio (Spring Boot)
Estos son los endpoints expuestos por el backend `backend_movil`:

**Base URL:** (URL de despliegue, ej: `https://tu-backend-render.com`)

#### 🔐 Autenticación (`AuthController`)
* **POST** `/auth/register`
    * *Descripción:* Registra un nuevo usuario en la base de datos.
    * *Body:* JSON con nombre, apellido, email, contraseña, etc.
* **POST** `/auth/login`
    * *Descripción:* Verifica credenciales y devuelve el token JWT de acceso.
    * *Body:* JSON con email y password.

#### 🐶 Gestión de Mascotas (`PetController`)
* **POST** `/api/pets`
    * *Descripción:* Crea una nueva ficha de mascota asociada al usuario autenticado.
* **GET** `/api/pets/user/{userId}`
    * *Descripción:* Obtiene la lista de todas las mascotas pertenecientes a un usuario específico.
* **GET** `/api/pets/{id}`
    * *Descripción:* Obtiene los detalles de una mascota específica por su ID.
* **PUT** `/api/pets/{id}`
    * *Descripción:* Actualiza la información de una mascota existente.
* **DELETE** `/api/pets/{id}`
    * *Descripción:* Elimina el registro de una mascota del sistema.

### B. API Externa (Pública)
Utilizada directamente por la aplicación móvil para contenido dinámico.
* **GET** `https://catfact.ninja/fact`
    * *Descripción:* Obtiene un dato curioso aleatorio sobre gatos.



## 4. Pasos para ejecutar el proyecto

### Backend (Servidor)
1.  **Configuración de BD:** Asegúrate de tener la Wallet de Oracle configurada en `src/main/resources/wallet` y las credenciales correctas en `application.properties`.
2.  **Compilar:** Ejecuta el comando `./mvnw clean package` en la raíz del proyecto `backend_movil`.
3.  **Ejecutar:** Corre el archivo JAR generado o utiliza `./mvnw spring-boot:run`.

### Aplicación Móvil
1.  **Clonar:** Clona este repositorio.
2.  **Abrir en Android Studio:** Selecciona la carpeta `patasycolasmovil`.
3.  **Sincronizar:** Espera a que Gradle descargue las dependencias.
4.  **Ejecutar:** Conecta tu dispositivo Android o inicia un emulador y presiona "Run" (▶️).



## 5. Evidencia: APK Firmado y KeyStore

A continuación se adjunta la evidencia de la generación del APK firmado (`app-release.apk`) y el archivo de claves (`keystore.jks`) utilizados para la distribución.

<img width="788" height="172" alt="Evidencia APK Firmado y JKS" src="https://github.com/user-attachments/assets/7f195783-726f-4080-a0fb-b30900bbfe84" />
