# Coal Jetpack

A craftable, coal-burning jetpack that gives you creative-style flight in survival.

![Coal Jetpack](src/main/resources/assets/coaljetpack/icon.png)

## How it works

- **Wear it** in your chestplate slot. With [Accessories](https://modrinth.com/mod/accessories) or [Trinkets](https://modrinth.com/mod/trinkets) installed, the back and cape slots work too.
- **Everyone can see it**: the jetpack shows on your back, with two fuel tanks and nozzles. If you wear it in an accessory slot over a chestplate, it sits on top of the armor.
- **Fly** the way you do in creative: double-tap jump to take off, hold jump or sneak to go up or down.
- **Fuel** comes from your inventory automatically. Keep coal, charcoal or blocks of coal anywhere in it, and the jetpack burns one whenever its tank runs dry.
  - Coal or charcoal: **1 minute** of flight.
  - Block of coal: **9 minutes** of flight.
- It only burns fuel while you're in the air. Standing on the ground costs nothing.
- The orange bar on the item shows how much fuel is left. When you run out mid-air, you'll hear a fizzle and see "Jetpack out of coal!"
- No fuel is needed in creative mode.

## Recipe

| | | |
|:-:|:-:|:-:|
| Iron Ingot | Redstone | Iron Ingot |
| Iron Ingot | Furnace | Iron Ingot |
| Flint and Steel |   | Flint and Steel |

The recipe unlocks when you pick up raw iron or iron ore.

## Requirements

- Minecraft **26.3** (Java Edition)
- [Fabric Loader](https://fabricmc.net/use/) 0.20.1 or newer
- [Fabric API](https://modrinth.com/mod/fabric-api)
- Optional: [Accessories](https://modrinth.com/mod/accessories) or [Trinkets](https://modrinth.com/mod/trinkets), for wearing the jetpack in a back or cape slot

Install it on both the client and the server.

## Installing

Download `coal-jetpack-<version>.jar` from the [releases page](https://github.com/DoctorKnepper/MC-Jetpack/releases), Modrinth or CurseForge, and put it in your `mods` folder along with Fabric API.

## Building from source

You need a Java 25 JDK.

```sh
./gradlew build    # the mod jar ends up in build/libs/
```

## Releasing

Publishing a GitHub release (Releases, then "Draft a new release", with a tag like `v1.0.0`) runs
`.github/workflows/release.yml`. It builds the jar, attaches it to the release and uploads it to Modrinth
and CurseForge. It needs these repository settings (Settings, then "Secrets and variables", then "Actions"):

| Name | Kind | Value |
|---|---|---|
| `MODRINTH_TOKEN` | secret | a Modrinth personal access token with the "Create versions" scope |
| `CURSEFORGE_TOKEN` | secret | a CurseForge API token (from your CurseForge account's API tokens page) |
| `MODRINTH_ID` | variable | the Modrinth project ID |
| `CURSEFORGE_ID` | variable | the CurseForge project ID |

If one platform's values aren't set, the workflow skips that platform. Update `mod_version` in
`gradle.properties` before tagging a new release.

## License

CC0 1.0 (public domain). See [LICENSE](LICENSE).
