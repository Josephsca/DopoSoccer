# DopoSoccer

**Juego de fútbol 2D desarrollado en Java con interfaz gráfica Swing.**

DopoSoccer es un juego inspirado en Head Soccer en el que dos jugadores controlan personajes de diferentes países. Cada personaje cuenta con estadísticas, una barra de energía y un tiro especial.

El objetivo es marcar más goles que el rival antes de que termine el tiempo o alcanzar primero la cantidad de goles establecida.

## 1. Tecnologías utilizadas

- **Lenguaje:** Java.
- **Interfaz gráfica:** Java Swing y Java AWT.
- **Arquitectura:** separación entre presentación y dominio.
- **Pruebas:** JUnit.
- **Recursos gráficos:** imágenes PNG y JPG.

## 2. Controles

| Acción | Jugador 1 | Jugador 2 |
|---|---|---|
| Mover a la izquierda | A | ← |
| Mover a la derecha | D | → |
| Saltar | W | ↑ |
| Patear | S | ↓ |
| Activar poder | Q | Ctrl derecho |
| Pausar | P / Esc | P / Esc |

Los poderes especiales requieren energía suficiente para activarse.

## 3. Configuración de partidas

El juego permite configurar:

- Nombre de ambos jugadores.
- País y personaje de cada jugador.
- Tipo de partida: por tiempo o por goles.
- Duración de la partida o cantidad de goles necesarios para ganar.
- Resolución de la cancha.

También cuenta con una opción de inicio rápido que utiliza la configuración disponible.

## 4. Mecánicas del juego

### Movimiento y física

Los jugadores pueden desplazarse horizontalmente, saltar y patear el balón.

El balón cuenta con movimiento, gravedad, rebotes y detección de goles.

### Sistema de combate

Cada jugador dispone de:

- Vida.
- Estado de aturdimiento.
- Barra de energía.
- Aura visual al activar determinados poderes.
- Tiro especial asociado a su personaje.

### Finalización del partido

El partido puede terminar por tiempo o por alcanzar el límite de goles.

El juego determina el ganador y muestra el marcador y los puntos obtenidos. También contempla la muerte súbita para resolver empates según las reglas del partido.

**Los puntos se muestran al finalizar, pero su almacenamiento permanente todavía está pendiente.**

## 5. Personajes y poderes especiales

El juego incluye cuatro países:

| País | Poder |
|---|---|
| Corea del Sur | `BlueAura` |
| Camerún | `LightningShot` |
| Nigeria | `TornadoShot` |
| Estados Unidos | `IllusionShot` |

Los poderes se implementan mediante clases que heredan de `PowerShot`.

### Ilusión de Estados Unidos

El poder de Estados Unidos genera una ilusión de ocho balones en pantalla:

- Un balón es real.
- Siete balones son copias visuales.
- Las copias se desplazan y simulan rebotes.
- Únicamente el balón real participa en las colisiones y puede marcar goles.
- Las copias desaparecen cuando finaliza la duración del poder.

La activación del efecto corresponde a `IllusionShot`, su estado se administra en `Ball` y su representación gráfica se realiza en `GamePanel`.

## 6. Interfaz gráfica

La aplicación contiene tres pantallas principales:

**Menú principal:** permite navegar entre las opciones de juego, seleccionar el modo y controlar el sonido.

**Configuración:** permite seleccionar jugadores, personajes y condiciones del partido.

**Partido:** muestra el estadio, los personajes, el balón, el marcador, las barras de vida y energía y los efectos de los poderes.

## 7. Organización del código

### Dominio (`domain`)

Contiene las clases responsables de las reglas y del estado del juego.

Entre ellas:

- `DopoSoccer`: acceso principal a las operaciones del juego.
- `Match`: administración del partido.
- `Player`: información y estado de los jugadores.
- `Ball`: movimiento y estado del balón.
- `Stadium`: dimensiones y características de la cancha.
- `GameCharacter`: personajes y sus características.
- `PowerShot`: clase base de los tiros especiales.
- `DopoSoccerException`: manejo de errores del dominio.
- `Log`: registro de errores.

### Presentación (`presentation`)

Contiene la interfaz gráfica.

- `DopoSoccerGUI`: ventana principal, menú y configuración.
- `GamePanel`: representación gráfica del partido, jugadores, balón y efectos especiales.

El proyecto se encuentra en proceso de separación de responsabilidades para que los controladores gestionen las entradas y el flujo del juego sin concentrar toda la lógica en la interfaz.

## 8. Pruebas

El proyecto contempla pruebas unitarias con JUnit 5.

La ubicación prevista es:

`src/test/`

Para ejecutarlas en Visual Studio Code se requiere la extensión de pruebas de Java y las dependencias de JUnit 5 correctamente configuradas.

La disponibilidad y ejecución de las pruebas deben verificarse en la versión actual del proyecto.

## 9. Funcionalidades pendientes

### Persistencia

- Guardar el estado de una partida.
- Abrir una partida guardada y continuar desde ese punto.
- Almacenar los puntos acumulados por jugador.
- Conservar las mejoras de estadísticas entre partidas.

### Jugador contra máquina

Implementar el modo Jugador vs Máquina con diferentes comportamientos de inteligencia artificial:

- Torpe.
- Ofensiva.
- Defensiva.
- Experta.

### Mejoras de personajes

Agregar una pantalla para invertir puntos en estadísticas mediante la funcionalidad `upgradeStat` del dominio.

### Arquitectura y mantenimiento

- Completar la separación MVC.
- Centralizar el manejo del teclado en controladores.
- Separar la actualización de la física del dibujo de la interfaz.
- Ampliar las pruebas unitarias.

## 10. Estado del proyecto

**Versión actual: desarrollo en progreso.**

El juego cuenta con una base funcional para partidas entre dos jugadores, selección de personajes, sistema de goles, poderes especiales y representación gráfica.

Las siguientes etapas se enfocarán implementar la persistencia y desarrollar el modo contra la máquina.