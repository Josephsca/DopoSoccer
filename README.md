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

# Retrospectiva

## 1. ¿Cuáles fueron los mini-ciclos definidos? Justifíquenlos.

### Mini-ciclo 1: Diseño
Hicimos el bosquejo de la interfaz en draw.io (menú, selección de jugadores, partido, pausa, guardar y pantalla de ganador) y el diagrama de clases en Astah. Empezamos por aquí para tener claro qué clases necesitábamos y cómo se iba a ver el juego antes de programar.

### Mini-ciclo 2: Dominio
Programamos la lógica del juego: el jugador (vida, estadísticas, barra de poder, aura, aturdimiento), el balón con gravedad y rebotes, la cancha en sus tres resoluciones, los arcos y los goles, y el partido por tiempo o por goles con muerte súbita. También hicimos los cuatro personajes (Corea del Sur, Camerún, Nigeria y Estados Unidos), cada uno con su tiro de poder, más la excepción propia del juego y el log de errores.

### Mini-ciclo 3: Presentación
Armamos la interfaz gráfica: el menú, la elección de modalidad, la configuración (nombres, personajes, tipo de partido, límite y resolución), la cancha con los controles de los dos jugadores en el mismo teclado, la pausa, la opción de terminar en cualquier momento y el mensaje con el ganador y los puntos. Le pusimos imágenes a los personajes, al balón y al estadio.

### Mini-ciclo 4: Pruebas
Hicimos las pruebas, separadas en cuatro clases (PlayerTest, MatchTest, DopoSoccerTest y StadiumTest). Revisamos tanto lo que el juego sí debe hacer (meter goles, hacer daño, cargar la barra, ganar por tiempo o por goles) como lo que no debe dejar (vida negativa, mejorar estadísticas sin puntos, valores inválidos en la configuración).


## 2. ¿Cuál es el estado actual del proyecto en términos de mini-ciclos? ¿Por qué?

La versión uno está casi terminada. Los cuatro mini-ciclos se hicieron: el juego Jugador vs Jugador funciona, todo compila y pasan las 38 pruebas. Falta una cosa: la Ilusión de Estados Unidos todavía no divide el balón en 8.

## 3. ¿Cuál fue el tiempo total invertido por cada uno de ustedes? (Horas/Hombre)

Cañon: 15 horas
Paez: 15 horas

## 4. ¿Cuál consideran fue el mayor logro? ¿Por qué?

Que el juego ya se pueda jugar de verdad entre dos personas, con los poderes, el daño, el aturdimiento y los dos tipos de partido. Además las 38 pruebas pasan, así que sabemos que la lógica funciona bien y no solo que “se ve bien”.

## 5. ¿Cuál consideran que fue el mayor problema técnico? ¿Qué hicieron para resolverlo?

Lograr que el movimiento se sintiera como en el juego original: que el balón cayera con gravedad, rebotara bien en el suelo, en los arcos y en los bordes, y que los jugadores no se salieran de la cancha. Como lo hicimos juntos, uno programaba mientras el otro probaba el juego y decía qué se veía raro. Ajustamos los valores hasta que quedó bien, y después hicimos pruebas de unidad para asegurarnos de que no se dañara con los siguientes cambios. 

## 6. ¿Qué hicieron bien como equipo? ¿Qué se comprometen a hacer para mejorar los resultados?

Nos reunimos seguido para trabajar en el proyecto y estuvimos en contacto todo el tiempo. Cuando uno avanzaba en algo, lo revisábamos juntos para que los dos entendiéramos cómo funcionaba y resolver los errores entre los dos. Para la próxima versión nos comprometemos a seguir trabajando así y a organizarnos mejor con el tiempo para no dejar cosas para el final.

## 7. Considerando las prácticas XP incluidas en los laboratorios, ¿cuál fue la más útil? ¿Por qué?

Las pruebas de unidad, porque cada vez que cambiábamos algo podíamos correrlas y ver rápido si habíamos dañado algo.


## 8. ¿Qué referencias usaron? ¿Cuál fue la más útil? Incluyan citas con estándares adecuados.

Escuela Colombiana de Ingeniería Julio Garavito. (2026). Desarrollo Orientado por Objetos — Proyecto Final DOPOSoccer 2026-2.
Oracle. (s. f.). Java Platform, Standard Edition API Specification. https://docs.oracle.com/en/java/javase/
The JUnit Team. (s. f.). JUnit 5 User Guide. https://junit.org/junit5/docs/current/user-guide/
TouchArcade. (s. f.). Head Soccer. https://toucharcade.com/games/head-soccer
D&D Dream. (s. f.). Head Soccer. https://dnddream.com/dnddream_web/app_game_06.htm
Anthropic. (2026). Claude [Modelo de lenguaje grande]. https://claude.ai/

La más útil fue el enunciado del proyecto, porque ahí están todas las reglas del juego, las fórmulas de daño y vida, y la tabla de personajes y poderes.
