# New Tiers 0.0.1 for Minecraft 1.21.11

JPMods release, based on Tiers 0.8.0 by Flavio6561 and contributors.
Original adaptation credits are preserved in NOTICE.

## Installation and configuration

1. Remove the original Tiers mod from this Minecraft instance.
2. Add `new-tiers-0.0.1+1.21.11.jar` to the instance's `mods` folder.
3. Use Fabric for Minecraft 1.21.11, Java 21 or later, and Fabric API.
4. Install Mod Menu to open **Mods > New Tiers > Configure**.
   The same settings are accessible with `/tiers -config`.

MCTiers data comes from `https://mctiers.net/api/v2/profile/{uuid}`.
Subtiers data comes from `https://subtiers.net/api/profile/{uuid}`.
The old mctiers.com host and PvPTiers provider are not used.

The original nametag, tab-list, chat and text-display integration, player search,
kit detection, selectable gamemodes, selected/highest/adaptive tier modes,
icon toggle, tier colors, left/right/off placement and keybinds are retained.
MCTiers starts on the left and Subtiers on the right. MCTiers and Subtiers have
8 and 12 selectable modes respectively. Classic and PvPTiers visual presets
are removed. Configuration is saved to `config/new-tiers.json`.

Default keys: Y detects the current kit, H opens the nearest player's profile,
and U/I cycle the left/right gamemodes. Edit these in Minecraft's controls.

## Building from source

Install JDK 21 and run `./gradlew build` (or `gradlew.bat build` on Windows).
The production mod is written to `build/libs/new-tiers-0.0.1+1.21.11.jar`.
Gradle downloads the Minecraft/Fabric/Mod Menu build dependencies.

The production JAR includes this complete source tree and build files at
`META-INF/new-tiers-source.zip`, along with the GPL-3.0 license and attribution.

## Changes from the original

- MCTiers lookups use mctiers.net.
- Removed PvPTiers requests, profiles, modes, columns, settings and artwork.
- Removed Classic and PvPTiers icon/color presets; retain MCTiers styling.
- Updated mod name, author, version and icon.
- Configuration screen returns to its parent Mod Menu screen when closed.
- Fixed gamemode cycling across the provider-specific mode arrays.
- Separate settings/cache paths, reliable settings writes, and nullable-peak
  handling for current API data.

See `NOTICE` for the original source revision and attribution.

## Source package

Extracted from the source bundle included in the supplied JPMods JAR.
The mod metadata and cover icon have been synchronized with that JAR.
Java source code, build configuration and original licence notices are retained.
Use JDK 21 to build this version.
