# Aquanite Helper

Hitting an aquanite with a slash attack severs its lure and drops its stab defence by 50
(60 -> 10). This plugin shows a flashing **"SLASH the lure!"** box while you're attacking an
aquanite or elder aquanite that still has its lure, and clears it when the game says
*"Your attack cuts off the aquanite's lure!"*.

## Features
- Warning box while attacking a lured aquanite / elder aquanite
- Clears on the lure-cut chat message, when the NPC turns into its no-lure form, or when you stop attacking
- Optional "switch to STAB" prompt after the lure is cut, with its own text and color
- Optional desktop notification
- Configurable warning text, color and flashing

## Running locally
Open the folder in IntelliJ IDEA (Gradle project, Java 11) and run `AquaniteHelperPluginTest`
from `src/test` with VM option `-ea`.

## Testing in-game
1. From this folder run `./gradlew run` (Windows: `gradlew.bat run`). It starts RuneLite in developer mode with the plugin loaded.
2. If you have a Jagex account, follow https://github.com/runelite/runelite/wiki/Using-Jagex-Accounts to log into the dev client.
3. Things to check:
   - Attack a lured aquanite with a stab style: the red box appears.
   - Switch to slash and hit (a miss still counts): the box clears, the green "switch to STAB" box shows briefly.
   - Attack an aquanite whose lure is already cut: no box.
   - Elder aquanite (superior) behaves the same way.
   - Walk away / stop attacking mid-fight: the box clears.
