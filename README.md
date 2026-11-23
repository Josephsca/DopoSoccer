# DopoSoccer

Juego de fútbol 2D hecho en Java (Swing), inspirado en Head Soccer.  
Proyecto final de Desarrollo Orientado por Objetos (DOPO 2026-2) — Escuela Colombiana de Ingeniería.

**Autores:** Cañon – Paez

## Compilación y ejecución

Todos los comandos deben ejecutarse desde la carpeta raíz del proyecto (`DopoSoccer`) para que carguen las imágenes y los archivos de datos se guarden en la ruta correcta.

```powershell
# Limpiar compilación previa (en cmd: rmdir /s /q bin && mkdir bin)
Remove-Item -Recurse -Force bin -ErrorAction SilentlyContinue
mkdir bin

# Compilar dominio, presentación y pruebas
javac -encoding UTF-8 -d bin -cp "lib/*" -sourcepath src src/domain/*.java src/presentation/*.java src/test/*.java

# Correr pruebas unitarias (JUnit 5)
java -cp "bin;lib/*" org.junit.platform.console.ConsoleLauncher execute --scan-class-path

# Ejecutar el juego
java -cp bin presentation.DopoSoccerGUI
```

> En Linux o macOS, usa `:` en lugar de `;` en el classpath.

## Controles

| Acción | Jugador 1 | Jugador 2 |
|---|---|---|
| Mover izquierda | `A` | `←` |
| Mover derecha | `D` | `→` |
| Saltar | `W` | `↑` |
| Patear | `S` | `↓` |
| Activar poder | `Q` | `Ctrl derecho` |
| Pausar | `P` / `Esc` | `P` / `Esc` |

## Qué incluye el juego

- **Modos de partido:** 1 vs 1 local o contra la máquina (se escoge lado y perfil). Se puede jugar por tiempo (con muerte súbita si hay empate) o por límite de goles, en resolución `SMALL` (800×450), `MEDIUM` (1024×576) o `LARGE` (1280×720).
- **Personajes y poderes:** La barra de poder se llena en 10 s y suma 25 % al recibir un gol. Con la barra completa se activa el aura y el siguiente toque al balón lanza el tiro de poder (el efecto nuevo reemplaza al anterior; si el tiro no impacta al rival, no hace daño):
  - **Corea del Sur (`BlueAura`):** empuja al rival hacia su propio arco (1,5 s | 40 de daño).
  - **Camerún (`LightningShot`):** impide saltar y reduce la velocidad al 50 % (3 s | 60 de daño).
  - **Nigeria (`TornadoShot`):** eleva al rival sin dejarlo moverse, saltar ni patear (3 s | 40 de daño).
  - **Estados Unidos (`IllusionShot`):** genera 8 balones en cancha donde solo uno es real (2,5 s | 30 de daño).
- **Combate y estadísticas:** La patada normal tiene un cooldown de 0,3 s y solo daña si no golpea el balón (`10 × multiplicadorKick`). Al llegar a 0 de vida el jugador queda aturdido 3 s. Las estadísticas escalan con `vidaMax = 100(1 + 0,10(Survival - 1))` y `multiplicadorKick = 1 + 0,10(Kick - 1)`.
- **Perfiles de máquina:**
  - `ClumsyStrategy` (Torpe): elige movimientos al azar.
  - `OffensiveStrategy` (Ofensiva): persigue el balón y patea hacia el arco rival.
  - `DefensiveStrategy` (Defensiva): se mantiene cubriendo su arco.
  - `ExpertStrategy` (Experta): ataca si el rival está aturdido, defiende su mitad si va ganando y protege cerca del arco si va empatando o perdiendo.
- **Progreso y mejoras:** Al finalizar un partido el ganador recibe 100 puntos y el perdedor 25. Los perfiles se guardan por nombre en `progreso.dat` (ambos jugadores deben tener nombres distintos; las máquinas no guardan progreso). Desde **Mejoras** en el menú principal se suben `speed`, `jump`, `kick` y `survival` hasta nivel 10 (el primer nivel cuesta 500 puntos y el precio se duplica en cada mejora).
- **Guardado de partidas:** Desde el menú de pausa se guarda el partido en un archivo `.dat` para reanudarlo luego desde el menú principal. Cargar una partida vieja no sobreescribe los puntos ni las mejoras acumuladas después.

## Estructura del proyecto

```text
DopoSoccer/
├── src/
│   ├── domain/        # Lógica del juego, física, IA, persistencia y excepciones
│   ├── presentation/  # Interfaz en Swing (DopoSoccerGUI, GamePanel, Button)
│   └── test/          # 73 pruebas unitarias en JUnit 5 (DopoSoccer, Match, Player, Stadium)
├── images/            # Sprites y recursos gráficos
├── lib/               # Librerías externas (JUnit 5)
├── docs/              # Javadoc
└── DopoSoccer5.06.asta # Diagrama de clases y diagramas de secuencia (Astah)
```

### Notas de arquitectura

- `DopoSoccer` es el único punto de entrada entre la interfaz y el dominio. Siempre mantiene un `Match` instanciado (arranca en pausa) para evitar comprobaciones de `null`.
- El desplazamiento valida colisiones con `Match.willCollide` antes de mover al jugador para evitar que se atraviesen; `separatePlayers` queda como respaldo para saltos o empujes por poder.
- Para extender el juego basta con heredar o implementar `Character`, `PowerShot`, `MachineStrategy` o `WinCondition`.
- Las excepciones de dominio se manejan con `DopoSoccerException` y se registran en `DopoSoccer.log`.

## Archivos generados en ejecución

- `progreso.dat`: puntos y estadísticas guardadas por jugador.
- `*.dat`: partidas guardadas por el usuario.
- `DopoSoccer.log`: registro de errores y excepciones.

