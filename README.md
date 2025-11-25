# Patas y Colas 🐾
Patas y Colas es una aplicación móvil Android diseñada para ayudarte a gestionar la información y la salud de tus mascotas. La aplicación permite llevar un registro detallado de cada una de tus mascotas, con un enfoque especial en el seguimiento de su calendario de vacunación a través de recordatorios y notificaciones.

Integrantes:
Samuel Mansilla
Francisco Mardones

trello:https://trello.com/b/4tdx7vMf/movil

🚀 ¿Qué hace la aplicación?
La aplicación permite a los usuarios:

Gestionar Múltiples Mascotas: Puedes añadir, editar o eliminar los perfiles de todas tus mascotas.

Crear Perfiles Detallados: Cada perfil de mascota incluye información esencial como nombre, especie (perro, gato u otro), raza, edad, peso y una foto de perfil.

Subir Fotos: Puedes seleccionar una foto desde la galería de tu dispositivo o tomar una nueva foto con la cámara.

Registro de Vacunas: La función más importante es el registro de vacunas. Puedes añadir múltiples vacunas para cada mascota, especificando el nombre de la vacuna y la fecha de aplicación o próxima dosis.

Recordatorios Automáticos: La aplicación programa automáticamente notificaciones locales. El sistema enviará un recordatorio un día antes de la fecha programada para cada vacuna, asegurando que no olvides las citas importantes.

🛠️ ¿Cómo funciona?
El flujo principal de la aplicación es el siguiente:

Pantalla de Bienvenida: Al iniciar la app, el usuario ve una pantalla de bienvenida (PortadaScreen).

Pantalla Principal (Menú): Al continuar, el usuario llega al MenuScreen. Esta pantalla tiene dos estados principales:

Vista de Recordatorios: Por defecto (en móviles), muestra un resumen de los próximos recordatorios de vacunas programadas.

Gestión de Mascotas: En la cabecera, un carrusel permite seleccionar una mascota existente para ver/editar su formulario, o pulsar el botón "Agregar" para crear una nueva.

Formulario de Mascota: Al agregar o seleccionar una mascota, se muestra el PetForm. Aquí es donde el usuario introduce todos los datos del animal y gestiona su lista de vacunas.

Notificaciones: Al guardar una mascota con fechas de vacunas futuras, el MenuViewModel utiliza un NotificationScheduler para programar una alarma (AlarmManager).

Receptor de Alarma: Un NotificationReceiver escucha esta alarma y, cuando se dispara (un día antes de la fecha), crea y muestra la notificación de recordatorio al usuario.

Persistencia: Toda la información de las mascotas y sus vacunas se guarda localmente en una base de datos Room.



APP firmada y archivo .jks
<img width="788" height="172" alt="image" src="https://github.com/user-attachments/assets/7f195783-726f-4080-a0fb-b30900bbfe84" />
