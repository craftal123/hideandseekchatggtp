# Hardest Hide and Seek — Fabric 1.21.8 Prototype

This is a playable, expandable Fabric mod scaffold for a competitive "hardest hide and seek" game mode.

## Implemented in this prototype

- Server-side match state
- Hider / hunter assignment commands
- Hiding phase timer
- Hider READY command and client button concept
- Server lock for hider after ready
- Live client minimap HUD scaffold
- Full map keybind scaffold
- Categorized question menu scaffold
- Hider response flow scaffold
- Token reward system
- Manual powerup menu scaffold
- Blackout reveal region model, synced to clients
- Config class for balancing

## Commands

```mcfunction
/hhs assign hider <player>
/hhs assign hunter <player>
/hhs start
/hhs ready
/hhs stop
/hhs tokens
/hhs ask <question_id>
/hhs answer <answer>
/hhs powerup <powerup_id>
```

Example:
```mcfunction
/hhs assign hider Steve
/hhs assign hunter Alex
/hhs start
```

## Build

Use Java 21.

```bash
./gradlew build
```

The mod jar will be in:

```text
build/libs/
```

## Important note

The minimap/world blackout renderer is intentionally built as a clear extension point.
The server logic and sync model are included, while the actual high-performance terrain-map rasterizer should be added next.
