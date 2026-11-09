# HeadSoccer - Versión uno (Presentación + Dominio)

## Cómo correrlo
- Abrir la carpeta `HeadSoccer` en VS Code (o el IDE que usen).
- Clase principal: `presentation.HeadSoccerGUI`.
- Las pruebas están en `src/test` y usan JUnit 5. En VS Code: pestaña Testing > "Enable Java Tests" > JUnit Jupiter (descarga el jar en `lib`).

## Controles
| Acción | Jugador 1 | Jugador 2 |
|---|---|---|
| Mover | A / D | ← / → |
| Saltar | W | ↑ |
| Patear | S | ↓ |
| Poder | Q | Ctrl derecho |
| Pausa | P o Esc | P o Esc |

## Qué incluye esta versión
- Configuración: nombre y personaje de cada jugador, tipo de partido (tiempo/goles) con su límite, resolución de la cancha.
- Juego Jugador vs Jugador: mover, saltar, patear, gol, rebotes, muerte súbita, pausa, terminar en cualquier momento, ganador y puntos.
- Vida, aturdimiento, barra de poder, aura y tiros de poder de los 4 países.
- Excepción propia (`HeadSoccerException`) y log de errores (`Log`, escribe `HeadSoccer.log`).
- Imágenes opcionales: varios estadios a elegir y poses por personaje (ver `images/LEEME.txt`).

## Pendiente (siguientes versiones)
- Guardar y abrir partidas (versión dos).
- Jugador vs máquina con perfiles (versión dos).
- Pantalla para mejorar estadísticas (el dominio ya lo soporta con `upgradeStat`) y mantener el progreso entre partidas.
- Ilusión de USA con 8 balones (por ahora la confusión invierte los controles del rival).

# Retropectiva

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