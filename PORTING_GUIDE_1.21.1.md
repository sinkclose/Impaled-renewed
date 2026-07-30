# Porting Impaled Fabric Mod from 1.19.3 to 1.21.1 (Fabric)

## Overview

This guide covers porting the **Impaled** Fabric mod from Minecraft **1.19.3 (Fabric 0.76.0)** to **Minecraft 1.21.1 (Fabric 26.2+)**.

### Key Version Changes

| Component | 1.19.3 (Current) | 1.21.1 (Target) |
|-----------|------------------|-----------------|
| Minecraft | 1.19.3 | **1.21.1** (Mojang version **26.2**) |
| Java | 17 | **21** (required for 1.21.1) |
| Yarn Mappings | 1.19.3+build.5 | **DEPRECATED** → Use Mojang Mappings |
| Fabric Loader | 0.15.2 | **≥0.19.0** |
| Fabric Loom | 1.4-SNAPSHOT | **1.5+** (latest stable) |
| Fabric API | 0.76.0+1.19.3 | **0.XX.0+1.21.1** (latest) |
| Gradle | 8.5 | **8.10+** (recommended) |

---

## Step 1: Update Build Configuration

### 1.1 Update `gradle.properties`

```properties
# Done to increase the memory available to gradle.
org.gradle.jvmargs=-Xmx2G

# Fabric Properties for 1.21.1
minecraft_version=1.21.1
# YARN MAPPINGS DEPRECATED FOR 1.21.1 - Use Mojang mappings instead
# yarn_mappings=1.21.1+build.X  <-- REMOVE THIS
loader_version=0.16.10
fabric_version=0.112.0+1.21.1

# Mod Properties
mod_version = 1.3.0
maven_group = io.github.ladysnake
archives_base_name = impaled

# Other Dependencies - UPDATE THESE TO 1.21.1 COMPATIBLE VERSIONS
mialeemisc_version = 1.0.XX  # Check https://maven.willbl.dev/releases for latest 1.21.1 version

# Publishing
owners = Ladysnake
license_header = ARR
curseforge_id = 478843
curseforge_versions = 1.21.1
cf_requirements = fabric-api
release_type = release
modrinth_id = AiHAhIP5
```

### 1.2 Update `build.gradle`

```gradle
plugins {
    id 'fabric-loom' version "1.5-SNAPSHOT"  // Use latest stable Loom
    id 'io.github.ladysnake.chenille' version '0.11.3'
}

sourceCompatibility = JavaVersion.VERSION_21
targetCompatibility = JavaVersion.VERSION_21

archivesBaseName = project.archives_base_name
version = project.mod_version
group = project.maven_group

chenille {
    configurePublishing {
        withCurseforgeRelease()
        withGithubRelease()
        withLadysnakeMaven()
        withModrinthRelease()
    }
}

repositories {
    mavenCentral()
    maven { url "https://maven.fabricmc.net/" }
    maven { url "https://maven.quiltmc.org/repository/release" }
    
    // MixinExtras
    maven {
        url "https://jitpack.io"
        content { includeGroupByRegex "com\\.github\\..*" }
    }
    // MialeeMisc
    maven { url "https://maven.willbl.dev/releases" }
    // Parchment mappings (recommended for parameter names)
    maven { url "https://maven.parchmentmc.org" }
}

dependencies {
    minecraft "com.mojang:minecraft:${project.minecraft_version}"
    
    // Use OFFICIAL MOJANG MAPPINGS (required for 1.21.1+)
    mappings loom.layered() {
        officialMojangMappings()
        // Optional: Parchment for better parameter names
        parchment("org.parchmentmc.data:parchment-1.21.1:2024.08.15@zip")
    }
    
    modImplementation "net.fabricmc:fabric-loader:${project.loader_version}"
    modImplementation "net.fabricmc.fabric-api:fabric-api:${project.fabric_version}"

    modImplementation include ("xyz.amymialee:mialeemisc:${project.mialeemisc_version}") {
        exclude group: "dev.emi"
    }
}

processResources {
    inputs.property "version", project.version
    filesMatching("fabric.mod.json") {
        expand "version": project.version
    }
}

java {
    withSourcesJar()
}

jar {
    from "LICENSE"
}

// Loom configuration for 1.21.1
loom {
    // Enable split environment source sets for client/server separation
    splitEnvironmentSourceSets()
    
    // Remap output archives
    remapArchives = true
    setupRemappedVariants = true
    enableTransitiveAccessWideners = true
    
    mixin {
        // Use legacy Mixin AP if needed for compatibility
        useLegacyMixinAp = true
        defaultRefmapName = "impaled.refmap.json"
    }
    
    runs {
        client {
            environment = "client"
            configName = "Minecraft Client"
            runDir = "run/client"
        }
        server {
            environment = "server"
            configName = "Minecraft Server"
            runDir = "run/server"
        }
    }
}
```

### 1.3 Update `settings.gradle`

```gradle
pluginManagement {
    repositories {
        maven { name = 'Fabric'; url = 'https://maven.fabricmc.net/' }
        maven { name = 'Quilt'; url = "https://maven.quiltmc.org/repository/release" }
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = 'impaled'
```

### 1.4 Update Gradle Wrapper

```bash
./gradlew wrapper --gradle-version 8.10
```

---

## Step 2: Update `fabric.mod.json`

```json
{
  "schemaVersion": 1,
  "id": "impaled",
  "version": "${version}",
  "name": "Impaled",
  "description": "Adds trident variants and a Sincere Loyalty enchantment upgrade for tridents.",
  "authors": [
    "doctor4t",
    "ArathainFarqoe",
    "Pyrofab"
  ],
  "contact": {
    "sources": "https://github.com/Ladysnake/Impaled",
    "issues": "https://github.com/Ladysnake/Impaled/issues"
  },
  "license": "ARR",
  "icon": "assets/impaled/impaled.png",
  "environment": "*",
  "entrypoints": {
    "main": [
      "ladysnake.impaled.common.Impaled",
      "ladysnake.sincereloyalty.SincereLoyalty"
    ],
    "client": [
      "ladysnake.impaled.client.ImpaledClient",
      "ladysnake.sincereloyalty.SincereLoyaltyClient::INSTANCE"
    ]
  },
  "mixins": [
    "impaled.mixins.json",
    "sincereloyalty.mixins.json",
    {
      "config": "impaled.client.mixins.json",
      "environment": "client"
    },
    {
      "config": "sincereloyalty.client.mixins.json",
      "environment": "client"
    }
  ],
  "depends": {
    "fabricloader": ">=0.19.0",
    "minecraft": "~26.2",
    "java": ">=21",
    "fabric-api": "*"
  },
  "accessWidener": "impaled.classtweaker"
}
```

---

## Step 3: Mapping Migration (Critical Step)

### 3.1 Why Mappings Changed

Starting with **Minecraft 1.20.5 (26.1+)**, Mojang released the game with **unobfuscated code**. Yarn mappings are **deprecated**. You **MUST** migrate to:
- **Official Mojang Mappings** (required)
- **Parchment Mappings** (recommended for parameter names + javadocs)

### 3.2 Migration Tools

#### Option A: Loom Built-in Migration (Kotlin NOT supported)
```bash
./gradlew migrateMappings --mappings loom.officialMojangMappings()
```

#### Option B: Ravel IntelliJ Plugin (Recommended, supports Kotlin)
1. Install **Ravel** plugin in IntelliJ IDEA
2. Right-click project → **Migrate Mappings**
3. Select **Mojang + Parchment**

### 3.3 Post-Migration Checklist

After migration, manually verify:
- [ ] All mixin targets still match (method names, field names)
- [ ] All `@Inject`, `@ModifyVariable`, `@Redirect` targets resolved
- [ ] Parameter names in mixins (Parchment provides these)
- [ ] No `yarn.` references remain in code

---

## Step 4: Code Changes for 1.21.1

### 4.1 Major API Changes in 1.21.1

#### Registry System (1.20.5+)
```java
// OLD (1.19.3)
Registry.register(Registries.ITEM, new Identifier("impaled", "pitchfork"), item);

// NEW (1.21.1) - Use Registries directly (mojang mappings)
Registries.ITEM.register(new Identifier("impaled", "pitchfork"), item);
// OR with RegistryKey
Registry.register(Registries.ITEM, RegistryKey.of(Registries.ITEM.getKey(), new Identifier("impaled", "pitchfork")), item);
```

#### Identifier → `net.minecraft.util.Identifier` (unchanged but use `Identifier.of()`)
```java
// OLD
new Identifier("impaled", "pitchfork");

// NEW (Mojang mappings)
Identifier.of("impaled", "pitchfork");
Identifier.ofVanilla("pitchfork"); // for vanilla IDs
```

#### Loot Tables & Data Generation
```java
// LootTableEvents.MODIFY registration changed
LootTableEvents.MODIFY.register((key, builder, source, registries) -> {
    if (key.matches(BASTION_TREASURE_CHEST_LOOT_TABLE_ID)) {
        builder.pool(LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1))
            .with(ItemEntry.builder(ImpaledItems.ANCIENT_TRIDENT))
            .conditionally(RandomChanceLootCondition.builder(0.6f))
            .build());
    }
});
```

#### Damage Sources (1.21+)
```java
// OLD
DamageSource damageSource = new DamageSource("hellfork_heat").setFire();

// NEW - Use DamageTypes registry
DamageSource damageSource = world.getDamageSources().create(DamageTypes.IN_FIRE); // or custom
// For custom damage types, register in DamageType registry
```

#### Particle Types
```java
// Particle registration changed - use ParticleType registry
```

#### Sound Events
```java
// SoundEvent registration uses RegistryKey
RegistryKey<SoundEvent> key = RegistryKey.of(Registries.SOUND_EVENT.getKey(), Identifier.of("impaled", "custom_sound"));
```

### 4.2 Update Mixin Configurations

**Rename mixin files for client/server separation:**

Create these files in `src/main/resources/`:

#### `impaled.mixins.json` (common)
```json
{
  "required": true,
  "package": "ladysnake.impaled.mixin",
  "compatibilityLevel": "JAVA_21",
  "mixins": [
    "impaling.ImpalingEnchantmentMixin",
    "impaling.TridentEntityMixin",
    "impaling.PlayerEntityMixin",
    "impaling.MobEntityMixin",
    "EntityMixin",
    "EnchantmentTargetMixin",
    "TridentRiptideFeatureRendererMixin",
    "TridentEntityAccessor",
    "LivingEntityMixin",
    "InGameOverlayRendererMixin",
    "EntityRendererMixin"
  ],
  "injectors": {
    "defaultRequire": 1
  }
}
```

#### `impaled.client.mixins.json` (client-only)
```json
{
  "required": true,
  "package": "ladysnake.impaled.mixin",
  "compatibilityLevel": "JAVA_21",
  "environment": "client",
  "mixins": [
    "InGameHudMixin"
  ],
  "injectors": {
    "defaultRequire": 1
  }
}
```

#### `sincereloyalty.mixins.json` (common)
```json
{
  "required": true,
  "package": "ladysnake.sincereloyalty.mixin",
  "compatibilityLevel": "JAVA_21",
  "mixins": [
    "TridentEntityMixin",
    "TridentItemMixin",
    "PlayerEntityMixin",
    "ItemMixin",
    "ItemEntityMixin",
    "ProjectileAccessor",
    "PlayerInventoryMixin",
    "ServerPlayNetworkHandlerMixin",
    "SmithingScreenHandlerMixin",
    "AnvilScreenHandlerMixin"
  ],
  "injectors": {
    "defaultRequire": 1
  }
}
```

#### `sincereloyalty.client.mixins.json` (client-only)
```json
{
  "required": true,
  "package": "ladysnake.sincereloyalty.mixin.client",
  "compatibilityLevel": "JAVA_21",
  "environment": "client",
  "mixins": [
    "ItemStackMixin",
    "MinecraftClientMixin",
    "PlayerEntityRendererMixin"
  ],
  "injectors": {
    "defaultRequire": 1
  }
}
```

---

## Step 5: Fix Common 1.21.1 Compilation Issues

### 5.1 Item Registration Changes

```java
// ImpaledItems.java - Update registration
public static void init() {
    // Use Registry.register with Registries.ITEM
    ELDER_GUARDIAN_EYE = Registry.register(Registries.ITEM, 
        Identifier.of(Impaled.MODID, "elder_guardian_eye"), 
        new Item(new Item.Settings().rarity(Rarity.UNCOMMON)));
    
    ANCIENT_TRIDENT = Registry.register(Registries.ITEM, 
        Identifier.of(Impaled.MODID, "ancient_trident"),
        new Item(new Item.Settings().rarity(Rarity.UNCOMMON).fireproof()));
    
    // ... etc
}
```

### 5.2 Entity Type Registration

```java
// ImpaledEntityTypes.java
public class ImpaledEntityTypes {
    public static final EntityType<PitchforkEntity> PITCHFORK = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier.of(Impaled.MODID, "pitchfork"),
        EntityType.Builder.<PitchforkEntity>create(PitchforkEntity::new, EntitySpawnGroup.MISC)
            .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
            .eyeHeight(0.25f)
            .maxTrackingRange(4)
            .trackingTickInterval(10)
            .build()
    );
    // ... register all entity types
}
```

### 5.3 Mixin Target Updates

After mapping migration, update all `@At` targets in mixins:

```java
// Example: Method names may have changed
@Inject(method = "tick", at = @At("HEAD"))
// becomes (verify with new mappings)
@Inject(method = "tick", at = @At("HEAD")) // verify method name
```

### 5.4 Accessor Interfaces

```java
// TridentEntityAccessor.java - verify field names in new mappings
public interface TridentEntityAccessor {
    // Field names changed from Yarn to Mojang
    // e.g., field_1234 -> loyalty or similar
    int getLoyalty();
    void setLoyalty(int loyalty);
}
```

---

## Step 6: Data Generation Setup

### 6.1 Enable Data Generation in `build.gradle`

```gradle
fabricApi {
    configureDataGeneration {
        client = true
        server = true
    }
}
```

### 6.2 Add Data Gen Entrypoint in `fabric.mod.json`

```json
"entrypoints": {
  "main": [...],
  "client": [...],
  "fabric-datagen": ["ladysnake.impaled.datagen.ImpaledDataGenerator"]
}
```

---

## Step 7: Build & Test

### 7.1 Generate Sources & Build

```bash
# Clean and generate sources
./gradlew clean genSources

# Build the mod
./gradlew build

# Run client
./gradlew runClient

# Run server
./gradlew runServer
```

### 7.2 Common Build Issues & Fixes

| Issue | Fix |
|-------|-----|
| `Cannot resolve method/field` | Run mapping migration, verify mixin targets |
| `Mixin apply failed` | Check `@At` targets, update to new method names |
| `Duplicate mixin` | Ensure client/server mixins separated by environment |
| `Java version mismatch` | Use JDK 21 (`java -version`) |
| `Fabric API version not found` | Check https://maven.fabricmc.net for latest 1.21.1 version |

---

## Step 8: Publishing Updates

### 8.1 Update CurseForge/Modrinth

- **CurseForge**: Game version = `1.21.1`, Loader = `Fabric 0.16.10`
- **Modrinth**: Game version = `1.21.1`, Loader = `fabric`

### 8.2 Update `gradle.properties`

```properties
curseforge_versions = 1.21.1
modrinth_id = AiHAhIP5
```

---

## Quick Reference: Version Matrix for 1.21.1

```properties
# Verified working versions (check for latest)
minecraft_version=1.21.1
loader_version=0.16.10
fabric_version=0.112.0+1.21.1
yarn_mappings=REMOVED - use loom.officialMojangMappings() + parchment
mialeemisc_version=CHECK_MAVEN_FOR_1.21.1_VERSION
```

---

## Migration Summary Checklist

- [ ] Update `gradle.properties` with 1.21.1 versions
- [ ] Update `build.gradle` with Mojang + Parchment mappings
- [ ] Update `settings.gradle` with Fabric/Quilt repos
- [ ] Update Gradle wrapper to 8.10+
- [ ] Update `fabric.mod.json` with new dependencies (minecraft ~26.2, java >=21)
- [ ] Split mixin configs by environment (client/server)
- [ ] Run `./gradlew migrateMappings` or use Ravel plugin
- [ ] Fix all compilation errors in mixins and code
- [ ] Update registry calls to use `Registries.X.register()`
- [ ] Update Identifier usage to `Identifier.of()`
- [ ] Test `./gradlew runClient` and `./gradlew runServer`
- [ ] Build with `./gradlew build`
- [ ] Update publishing metadata for 1.21.1

---

## Useful Resources

- **Fabric 1.21.1 Docs**: https://docs.fabricmc.net/1.21.1
- **Fabric Develop Docs**: https://docs.fabricmc.net/develop
- **Mapping Migration Guide**: https://docs.fabricmc.net/develop/porting/mappings
- **Fabric API Maven**: https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/
- **Parchment Mappings**: https://maven.parchmentmc.org/org/parchmentmc/data/parchment-1.21.1/
- **MialeeMisc**: https://maven.willbl.dev/releases/xyz/amymialee/mialeemisc/