# **Soulbound**

Soulbound is a **dynamic Soul Alignment system** for Minecraft Fabric where players absorb soul fragments from the mobs they kill, gradually transforming how the world's creatures perceive and react to them. Kill enough zombies and the undead begin to hesitate—or flee. Slaughter Endermen and reality bends around you. Every kill shapes your identity, unlocking tiered passive effects, altering mob AI, changing villager trade prices, and eventually reaching Apex and Transcendent states of power.

---

## **Key Features**

- **7 Soul Alignments** — Decay, Precision, Volatility, Void, Instinct, Order, and Savagery. Each alignment is earned by killing specific mob types and grants unique passive effects, combat bonuses, and world interactions.
- **6-Tier Progression** — Progress from Dormant → Fledgling → Attuned → Resonant → Apex → Transcendent, with escalating power at each stage.
- **Mob Perception System** — Mobs dynamically react to your alignment. Zombies hesitate to attack Decay players, Endermen become neutral toward Void players, Creepers delay their fuse for Volatility players, and more.
- **Fracture System** — Spreading your kills too evenly across alignments fractures your soul, inflicting escalating debuffs, reality tears that spawn hostile mobs, and visual corruption.
- **Apex & Transcendent States** — Reaching high alignment values unlocks powerful abilities: Void Blink teleportation, Savagery berserker healing, Order aura buffs for nearby allies, and more.
- **Soul Resonance** — Nearby players with the same or synergistic alignments amplify each other's power.
- **Soul Echoes** — Leave behind alignment-infused zones that grant effects to anyone who enters.
- **Dimensional Affinity** — Each alignment has a home dimension where it grows faster and grants bonus effects.
- **Momentum System** — Consecutive kills of the same alignment type build a streak multiplier up to 2.5x.
- **Soul Whispers** — Immersive alignment-themed messages appear as your power grows.
- **Anti-Grinder Protection** — Spatial detection and kill-window tracking prevent farming exploits.
- **PvP Integration** — Opposing alignments deal bonus damage to each other; synergistic alignments deal reduced damage.
- **Visual Particles** — Alignment-specific particles orbit players, with special effects for Apex, Transcendent, and Fractured states.
- **Villager Trade Modification** — Order players get discounts; Decay and Savagery players pay more.
- **Persistent Data** — Alignment data persists across death, dimensions, and server restarts via NBT.
- **Multiplayer-Safe** — Per-player alignment data works correctly on servers.
- **Fully Configurable** — Every threshold, multiplier, radius, and toggle is exposed in the config file.

---

## **How It Works**

### **Soul Alignments**

Every mob in Minecraft is mapped to one of seven soul alignments. When you kill a mob, you absorb its soul fragment, increasing your alignment value for that type:

| Alignment | Color | Mobs | Lore |
|-----------|-------|------|------|
| **Decay** | 🟢 Dark Green | Zombie, Husk, Drowned, Phantom, Zoglin | "The grave whispers your name." |
| **Precision** | ⬜ White | Skeleton, Stray, Wither Skeleton | "Your aim sharpens beyond mortal limits." |
| **Volatility** | 🟩 Green | Creeper, Ghast, Blaze, Magma Cube | "Chaos pulses through your veins." |
| **Void** | 🟣 Dark Purple | Enderman, Endermite, Shulker | "The space between worlds embraces you." |
| **Instinct** | 🔴 Dark Red | Spider, Cave Spider, Silverfish, Guardian, Wolf | "You feel the web of life tighten." |
| **Order** | 🟡 Gold | Villager, Iron Golem, Snow Golem, Wandering Trader | "Civilization's light burns within you." |
| **Savagery** | ⚪ Gray | Pillager, Vindicator, Evoker, Ravager, Witch, Hoglin, Piglin | "The frenzy of battle consumes you." |

### **Alignment Relationships**

Alignments have opposing and synergistic relationships that affect gameplay:

| Alignment | Opposes | Synergizes With |
|-----------|---------|-----------------|
| **Decay** | Order | Void |
| **Precision** | Volatility | Order |
| **Volatility** | Precision | Savagery |
| **Void** | Instinct | Decay |
| **Instinct** | Void | Savagery |
| **Order** | Decay, Savagery | Precision |
| **Savagery** | Order | Volatility |

- **Synergy Bonus**: Having a synergistic secondary alignment grants up to +50% effective strength.
- **Conflict Penalty**: Having an opposing secondary alignment reduces effective strength by up to 30%.
- **Opposing Decay**: Gaining alignment in one type automatically decays its opposing type by 30% of the amount gained.

### **Tier Progression**

Your dominant alignment value determines your tier, which unlocks escalating passive effects:

| Tier | Threshold | Description | Effects Unlocked |
|------|-----------|-------------|------------------|
| **Dormant** | 0 | "Your soul is unaligned." | None |
| **Fledgling** | 10 | "A faint echo stirs within." | Basic passive stat boosts |
| **Attuned** | 30 | "The alignment takes root." | Enhanced passives, mob perception changes |
| **Resonant** | 60 | "Your soul resonates deeply." | Strong passives, combat modifiers |
| **Apex** | 100 | "You have become one with the alignment." | Apex abilities, mob avoidance |
| **Transcendent** | 150 | "Reality itself bends to your nature." | Ultimate powers, visual aura |

---

## **Passive Effects by Alignment**

Each alignment grants different stat bonuses and status effects as you progress through tiers:

### **Decay — The Undying**
| Tier | Effect |
|------|--------|
| Fledgling | +Armor |
| Attuned | +Knockback Resistance |
| Resonant | +Max Health |
| Apex | Resistance I, Absorption I |
| Transcendent | Regeneration II, Absorption III |

### **Precision — The Sharpshooter**
| Tier | Effect |
|------|--------|
| Fledgling | +Movement Speed |
| Attuned | +Attack Speed |
| Resonant | +Attack Damage |
| Apex | Night Vision (while using items) |
| Transcendent | +Bonus Damage, Glowing |

### **Volatility — The Pyromaniac**
| Tier | Effect |
|------|--------|
| Fledgling | Fire Resistance |
| Attuned | +Armor |
| Resonant | +Attack Damage |
| Apex | Strength I |
| Transcendent | Strength III, Haste II |

### **Void — The Wanderer**
| Tier | Effect |
|------|--------|
| Fledgling | +Movement Speed |
| Attuned | Slow Falling |
| Apex | Night Vision in the End |
| Transcendent | Persistent Slow Falling, Invisibility |

### **Instinct — The Predator**
| Tier | Effect |
|------|--------|
| Fledgling | +Movement Speed (night) |
| Attuned | Night Vision (night) |
| Resonant | +Attack Damage (night) |
| Apex | Jump Boost II (night) |
| Transcendent | Speed III, +Bonus Damage (night) |

### **Order — The Guardian**
| Tier | Effect |
|------|--------|
| Fledgling | Regeneration I |
| Attuned | +Max Health |
| Resonant | Hero of the Village |
| Apex | +Armor |
| Transcendent | Absorption, Resistance, ally aura (Resistance + Regeneration in 12-block radius) |

### **Savagery — The Berserker**
| Tier | Effect |
|------|--------|
| Fledgling | +Attack Damage |
| Attuned | +Attack Speed |
| Resonant | +Movement Speed |
| Apex | Strength II |
| Transcendent | Strength IV + Haste II below 25% HP |

---

## **Apex Abilities**

At Apex tier (100+ alignment value), each alignment grants unique active abilities:

| Alignment | Apex Ability |
|-----------|-------------|
| **Decay** | Armor boost, Resistance, Absorption |
| **Precision** | Speed boost, Night Vision, bonus damage at Transcendent |
| **Volatility** | Fire Resistance, damage boost, Strength + Haste at Transcendent |
| **Void** | Void Blink — automatic teleport when below 30% HP (16-block range) |
| **Instinct** | Night-empowered speed and damage, Jump Boost |
| **Order** | Hero of the Village, Regeneration, Armor; ally aura at Transcendent |
| **Savagery** | Strength II, speed and damage boost; berserker rage below 50% HP at Transcendent |

---

## **Mob Perception**

Your alignment changes how mobs react to you through five mixin-driven behaviors:

| Behavior | Alignment | Effect |
|----------|-----------|--------|
| **Target Avoidance** | Decay, Precision, Instinct, Volatility | Matching mob types may refuse to target you |
| **Attack Delay** | Decay, Savagery, Instinct, Volatility | Matching mobs hesitate before attacking |
| **Creeper Fuse Extension** | Volatility | Creepers take longer to explode near you |
| **Enderman Neutrality** | Void | Endermen ignore eye contact |
| **Spider Day Passivity** | Instinct | Spiders won't attack during daytime |
| **Passive Mob Fear** | Savagery, Decay | Animals flee from you |
| **Trade Price Modification** | Order (cheaper), Decay/Savagery (more expensive) | Villager prices adjust based on your alignment |
| **Aggro Radius** | All | Your alignment alters mob detection range (Order reduces, Savagery increases) |

### **Combat Damage Modifiers**

| Condition | Damage Modifier |
|-----------|----------------|
| Attacking mob of opposing alignment | +15% per strength |
| Attacking mob of synergistic alignment | -10% per strength |
| Transcendent state | +10% flat bonus |

---

## **The Fracture System**

Spreading your kills across too many alignment types causes your soul to fracture. Fracture occurs when your top 3 alignment values are too close together.

| Fracture Level | Severity | Effects |
|----------------|----------|---------|
| **1–20** | Minor | Random whispers, occasional minor debuffs (Hunger, Slowness, Mining Fatigue) |
| **20–50** | Moderate | Stronger debuffs, Darkness at night, increased Phantom spawning |
| **50–80** | Severe | Frequent debuffs, Wither I, Reality Tears (hostile mobs spawn around you) |
| **80–100** | Critical | Persistent Darkness, Slowness, Mining Fatigue II, Wither, soul fracture warnings |

### **Reality Tears**

At severe fracture levels, reality tears open around you, spawning hostile mobs:

| Escalation | Mobs Spawned | Mob Types |
|------------|-------------|-----------|
| Low (0–7) | 1 | Zombie, Skeleton, Silverfish |
| Medium (8–14) | 1–2 | Skeleton, Spider, Creeper, Zombie |
| High (15+) | 1–4 | Wither Skeleton, Blaze, Enderman, Witch |

---

## **Soul Resonance**

When nearby players share the same (or synergistic) dominant alignment, everyone gets a resonance bonus:

| Factor | Details |
|--------|---------|
| **Detection Radius** | 32 blocks (configurable) |
| **Same Alignment** | +15% bonus per nearby player |
| **Synergistic Alignment** | +7.5% bonus per nearby player |
| **Maximum Bonus** | 60% (configurable) |
| **Effect** | Multiplies alignment gain rate |

---

## **Soul Echoes**

When certain conditions are met, a Soul Echo is created at your location:

| Aspect | Details |
|--------|---------|
| **Radius** | 8 blocks (configurable) |
| **Duration** | 5 minutes (6000 ticks) |
| **Minimum Strength** | 20% alignment |
| **Effect** | Grants alignment-appropriate status effect to anyone nearby |

Each alignment grants a different echo effect:

| Alignment | Echo Effect |
|-----------|-------------|
| Decay | Resistance |
| Precision | Speed |
| Volatility | Fire Resistance |
| Void | Slow Falling |
| Instinct | Night Vision |
| Order | Regeneration |
| Savagery | Strength |

---

## **Dimensional Affinity**

Each alignment has a home dimension where it receives bonuses:

| Alignment | Home Dimension | Gain Bonus |
|-----------|----------------|------------|
| **Decay** | Nether | 1.35x |
| **Precision** | Overworld | 1.25x |
| **Volatility** | Nether | 1.35x |
| **Void** | End | 1.5x |
| **Instinct** | Overworld | 1.25x |
| **Order** | Overworld | 1.25x |
| **Savagery** | Nether | 1.35x |

- **Home Dimension**: Bonus status effects (Fire Resistance in Nether, Slow Falling + Night Vision in End, Regeneration in Overworld).
- **Hostile Dimension**: Occasional Weakness debuff.

---

## **Momentum System**

Consecutive kills of the same alignment type build a momentum streak:

| Streak | Multiplier | Effect |
|--------|------------|--------|
| 1 | 1.0x | Baseline |
| 5 | 1.5x | 🔥 Momentum notification |
| 10 | 2.0x | 🔥 Momentum notification |
| 15 | 2.5x (max) | 🔥 Momentum notification |

- Killing a mob of a different alignment resets the streak to 1.
- Momentum decays naturally over time.

---

## **Anti-Grinder Protection**

The mod detects and penalizes mob farming:

| Protection | Mechanic |
|------------|----------|
| **Kill Cooldown** | Same mob type within 5 seconds → 75% gain reduction |
| **Kill Window** | More than 5 kills of same type within 5 minutes → 75% gain reduction |
| **Spatial Detection** | 10+ kills within 16-block radius → 85% gain reduction |
| **Boss Bonus** | Boss mobs (Ender Dragon, Wither) grant 3x alignment |
| **Elite Bonus** | Elite mobs (Wither Skeleton, Elder Guardian, etc.) grant 1.8x alignment |

---

## **Commands**

All commands are under `/soul`:

| Command | Permission | Description |
|---------|------------|-------------|
| `/soul` | All | Show alignment summary (dominant, tier, states, momentum, resonance) |
| `/soul details` | All | Show all alignment values with tiers |
| `/soul history` | All | Show recent alignment shift history |
| `/soul stats` | All | Show kill statistics by alignment |
| `/soul lore` | All | Show alignment lore and tier description |
| `/soul help` | All | Show command help |
| `/soul trait <alignment> <amount>` | OP (Level 2) | Grant alignment points (0.1–500) |

---

## **Visual Indicators**

### **Particles by State**

| State | Particle Effect |
|-------|----------------|
| Ambient (40%+ strength) | Alignment-specific particles around player |
| Apex | Enhanced primary + secondary particles |
| Transcendent | Orbiting particles + End Rod + Enchant particles |
| Fractured | Soul Fire Flame; +Smoke at 40%; +Damage Indicator at 70% |
| Tier Up | 16-point burst ring + End Rod + Totem of Undying particles |

### **Alignment Particles**

| Alignment | Primary Particle | Apex Secondary |
|-----------|-----------------|----------------|
| Decay | Soul | Soul Fire Flame |
| Precision | Crit | Enchanted Hit |
| Volatility | Flame | Lava |
| Void | Portal | Reverse Portal |
| Instinct | Enchant | Glow |
| Order | Composter | Wax On |
| Savagery | Angry Villager | Sweep Attack |

### **Soul Whispers**

Contextual messages appear based on your alignment:

| Alignment | Example Whispers |
|-----------|-----------------|
| Decay | "The soil hungers beneath your feet..." |
| Precision | "Your aim steadies. Every breath counts." |
| Volatility | "Fire is not your enemy. It is your instrument." |
| Void | "Space bends around your presence..." |
| Instinct | "The night is your hunting ground now." |
| Order | "You bring peace where you walk." |
| Savagery | "Your blood sings for battle." |

---

## **Configuration**

Config file location: `.minecraft/config/soulbound.json`

```json
{
  "globalEnabled": true,
  "alignmentGainRate": 1.0,
  "alignmentDecayRate": 0.05,
  "apexThreshold": 100.0,
  "transcendentThreshold": 150.0,
  "fractureThreshold": 10.0,
  "effectStrengthMultiplier": 1.0,
  "hardcoreMultiplier": 1.5,
  "bossAlignmentMultiplier": 3.0,
  "eliteAlignmentMultiplier": 1.8,
  "grinderPenaltyFactor": 0.25,
  "grinderCooldownTicks": 100,
  "maxKillsPerTypePerWindow": 5,
  "killWindowTicks": 6000,
  "spatialGrinderRadius": 16.0,
  "spatialKillThreshold": 10,
  "spatialPenaltyFactor": 0.15,
  "decayIntervalTicks": 24000,
  "fractureDecayRate": 0.1,
  "momentumEnabled": true,
  "maxMomentumMultiplier": 2.5,
  "momentumGainPerKill": 0.1,
  "momentumDecayPerCycle": 1,
  "opposingDecayEnabled": true,
  "opposingDecayFactor": 0.3,
  "dimensionalBonusEnabled": true,
  "dimensionalOverworldBonus": 1.25,
  "dimensionalNetherBonus": 1.35,
  "dimensionalEndBonus": 1.5,
  "resonanceEnabled": true,
  "resonanceRadius": 32.0,
  "resonanceBonusPerPlayer": 0.15,
  "maxResonanceBonus": 0.6,
  "resonanceCheckInterval": 200,
  "soulEchoEnabled": true,
  "soulEchoDurationTicks": 6000,
  "soulEchoRadius": 8.0,
  "fractureSeverityMultiplier": 1.0,
  "fractureRealityTearEnabled": true,
  "fractureEffectInterval": 200,
  "particlesEnabled": true,
  "particleInterval": 40,
  "soulWhispersEnabled": true,
  "whisperCooldownTicks": 2400,
  "pvpAlignmentModifiersEnabled": true,
  "opposingPvpDamageBonus": 0.15,
  "synergyPvpDamageReduction": 0.1,
  "commandEnabled": true,
  "secondaryAlignmentEnabled": true,
  "secondaryAlignmentRatio": 0.35,
  "passiveMobFearEnabled": true,
  "passiveMobFearRadius": 8.0
}
```

### **Config Options Explained**

#### **Core Settings**
| Option | Default | Description |
|--------|---------|-------------|
| `globalEnabled` | `true` | Master toggle for the entire mod |
| `alignmentGainRate` | `1.0` | Global multiplier for alignment gain |
| `alignmentDecayRate` | `0.05` | Rate at which unused alignments decay |
| `apexThreshold` | `100.0` | Alignment value needed for Apex state |
| `transcendentThreshold` | `150.0` | Alignment value needed for Transcendent state |
| `effectStrengthMultiplier` | `1.0` | Global multiplier for all effect strengths |
| `hardcoreMultiplier` | `1.5` | Alignment gain multiplier in Hardcore mode |

#### **Anti-Grinder**
| Option | Default | Description |
|--------|---------|-------------|
| `grinderPenaltyFactor` | `0.25` | Gain reduction when grinder detected (75% penalty) |
| `grinderCooldownTicks` | `100` | Cooldown between kills of same type (5 seconds) |
| `maxKillsPerTypePerWindow` | `5` | Max kills before window penalty applies |
| `killWindowTicks` | `6000` | Kill tracking window duration (5 minutes) |
| `spatialGrinderRadius` | `16.0` | Radius for spatial kill detection (blocks) |
| `spatialKillThreshold` | `10` | Kills in radius before spatial penalty |
| `spatialPenaltyFactor` | `0.15` | Gain reduction for spatial grinding (85% penalty) |

#### **Boss & Elite**
| Option | Default | Description |
|--------|---------|-------------|
| `bossAlignmentMultiplier` | `3.0` | Bonus alignment from boss mobs (Ender Dragon, Wither) |
| `eliteAlignmentMultiplier` | `1.8` | Bonus alignment from elite mobs |

#### **Alignment Decay**
| Option | Default | Description |
|--------|---------|-------------|
| `decayIntervalTicks` | `24000` | Time between decay cycles (20 minutes) |
| `fractureDecayRate` | `0.1` | Rate at which fracture level decreases when focused |

#### **Momentum**
| Option | Default | Description |
|--------|---------|-------------|
| `momentumEnabled` | `true` | Enable/disable momentum streak multiplier |
| `maxMomentumMultiplier` | `2.5` | Maximum momentum multiplier |
| `momentumGainPerKill` | `0.1` | Multiplier increase per consecutive kill |
| `momentumDecayPerCycle` | `1` | Streak reduction per decay cycle |

#### **Opposing & Synergy**
| Option | Default | Description |
|--------|---------|-------------|
| `opposingDecayEnabled` | `true` | Gaining one alignment decays its opposite |
| `opposingDecayFactor` | `0.3` | Fraction of gained value applied as opposing decay |
| `secondaryAlignmentEnabled` | `true` | Some mobs grant a secondary alignment type |
| `secondaryAlignmentRatio` | `0.35` | Secondary alignment gain as fraction of primary |

#### **Dimensional Bonus**
| Option | Default | Description |
|--------|---------|-------------|
| `dimensionalBonusEnabled` | `true` | Enable dimension-based alignment bonuses |
| `dimensionalOverworldBonus` | `1.25` | Gain multiplier in home Overworld |
| `dimensionalNetherBonus` | `1.35` | Gain multiplier in home Nether |
| `dimensionalEndBonus` | `1.5` | Gain multiplier in home End |

#### **Soul Resonance**
| Option | Default | Description |
|--------|---------|-------------|
| `resonanceEnabled` | `true` | Enable nearby player resonance bonus |
| `resonanceRadius` | `32.0` | Detection range for nearby aligned players |
| `resonanceBonusPerPlayer` | `0.15` | Bonus per resonant nearby player (15%) |
| `maxResonanceBonus` | `0.6` | Maximum resonance bonus (60%) |
| `resonanceCheckInterval` | `200` | Ticks between resonance checks (10 seconds) |

#### **Soul Echo**
| Option | Default | Description |
|--------|---------|-------------|
| `soulEchoEnabled` | `true` | Enable soul echo zones |
| `soulEchoDurationTicks` | `6000` | Echo zone duration (5 minutes) |
| `soulEchoRadius` | `8.0` | Echo zone radius in blocks |

#### **Fracture**
| Option | Default | Description |
|--------|---------|-------------|
| `fractureThreshold` | `10.0` | Difference threshold between top alignments to trigger fracture |
| `fractureSeverityMultiplier` | `1.0` | Global multiplier for fracture effect intensity |
| `fractureRealityTearEnabled` | `true` | Enable reality tear mob spawning |
| `fractureEffectInterval` | `200` | Ticks between fracture effect applications (10 seconds) |

#### **Visual & Feedback**
| Option | Default | Description |
|--------|---------|-------------|
| `particlesEnabled` | `true` | Enable alignment particles around players |
| `particleInterval` | `40` | Ticks between particle spawns (2 seconds) |
| `soulWhispersEnabled` | `true` | Enable immersive whisper messages |
| `whisperCooldownTicks` | `2400` | Minimum ticks between whispers (2 minutes) |

#### **PvP**
| Option | Default | Description |
|--------|---------|-------------|
| `pvpAlignmentModifiersEnabled` | `true` | Enable alignment-based PvP modifiers |
| `opposingPvpDamageBonus` | `0.15` | Bonus damage against opposing-aligned players |
| `synergyPvpDamageReduction` | `0.1` | Damage reduction against synergy-aligned players |

#### **Mob Behavior**
| Option | Default | Description |
|--------|---------|-------------|
| `passiveMobFearEnabled` | `true` | Animals flee from Savagery/Decay players |
| `passiveMobFearRadius` | `8.0` | Fear detection radius for passive mobs |

---

## **Mob Alignment Registry**

### **Decay Mobs**
| Mob | Weight | Secondary |
|-----|--------|-----------|
| Zombie | 1.0 | — |
| Zombie Villager | 1.2 | — |
| Husk | 1.4 | — |
| Drowned | 1.4 | — |
| Zombified Piglin | 1.6 | — |
| Zoglin | 2.0 | — |
| Phantom | 1.8 | Void |

### **Precision Mobs**
| Mob | Weight | Secondary |
|-----|--------|-----------|
| Skeleton | 1.0 | — |
| Stray | 1.4 | — |
| Wither Skeleton | 2.0 | Decay |

### **Volatility Mobs**
| Mob | Weight | Secondary |
|-----|--------|-----------|
| Creeper | 1.5 | — |
| Ghast | 2.2 | Void |
| Blaze | 1.8 | — |
| Magma Cube | 1.2 | — |

### **Void Mobs**
| Mob | Weight | Secondary |
|-----|--------|-----------|
| Enderman | 1.5 | — |
| Endermite | 0.8 | — |
| Shulker | 2.0 | Precision |

### **Instinct Mobs**
| Mob | Weight | Secondary |
|-----|--------|-----------|
| Spider | 1.0 | — |
| Cave Spider | 1.4 | Decay |
| Silverfish | 0.8 | — |
| Bee | 0.6 | — |
| Wolf | 0.8 | — |
| Guardian | 1.6 | — |
| Elder Guardian | 2.5 | — |

### **Order Mobs**
| Mob | Weight | Secondary |
|-----|--------|-----------|
| Villager | 2.0 | — |
| Iron Golem | 2.5 | — |
| Snow Golem | 1.0 | — |
| Wandering Trader | 1.5 | — |

### **Savagery Mobs**
| Mob | Weight | Secondary |
|-----|--------|-----------|
| Pillager | 1.5 | — |
| Vindicator | 1.8 | — |
| Evoker | 2.2 | Void |
| Ravager | 2.8 | Instinct |
| Witch | 1.5 | — |
| Vex | 1.0 | — |
| Illusioner | 2.0 | — |
| Hoglin | 1.6 | — |
| Piglin | 1.3 | — |
| Piglin Brute | 2.2 | — |

---

## **Multiplayer Compatibility**

This mod is **server-side** and works on both singleplayer and multiplayer:

| Environment | Compatibility |
|-------------|---------------|
| Singleplayer | ✅ Full support |
| Dedicated Server | ✅ Requires mod on server |
| LAN Worlds | ✅ Full support |
| Client-Only | ❌ Not supported |

- Alignment data is stored per-player and persists across death via `copyFrom`
- Soul Resonance detects nearby aligned players for multiplayer bonuses
- PvP modifiers apply per-player alignment comparisons
- Soul Echoes affect all players who enter the zone

---

## **Data Storage**

Persistent data is stored in the world save via player NBT:

| Data Type | Storage Location |
|-----------|------------------|
| Config | `.minecraft/config/soulbound.json` |
| Alignment Data | Player NBT (per-player, per-world) |
| Kill History | Player NBT (per-player) |
| Momentum/Resonance | Player NBT (per-player) |
| Fracture State | Player NBT (per-player) |

Data is saved automatically when players disconnect and when the server stops. Alignment data survives player death.

---

## **Installation**

1. Install **Fabric Loader** (0.15.6 or newer) for Minecraft **1.20.1**.
2. Install **Fabric API** (0.92.0+1.20.1 or newer).
3. Download the mod JAR file.
4. Place the JAR into your `.minecraft/mods` folder (or server mods folder).
5. Launch Minecraft with the Fabric profile.

---

## **Building from Source**

Clone the repository and run the Gradle build:

```bash
git clone https://github.com/AaravVishal1/SoulBound
cd soulbound
./gradlew build
```

The compiled JAR will be in `build/libs/`.

For development:

```bash
./gradlew runClient    # Run Minecraft client
./gradlew runServer    # Run dedicated server
```

---

## **Requirements**

| Dependency | Version |
|------------|---------|
| Minecraft | 1.20.1 |
| Fabric Loader | 0.15.6+ |
| Fabric API | 0.92.0+1.20.1 |
| Java | 17+ |

---

## **Troubleshooting**

### Alignment isn't changing when I kill mobs

1. Check that `globalEnabled` is `true` in the config.
2. Verify the mob type is registered (not all mobs grant alignment—passive mobs don't).
3. Check anti-grinder protections aren't triggering (try killing varied mobs in different locations).
4. If `alignmentGainRate` is set very low, increases may be imperceptible.

### No passive effects are appearing

1. Your alignment value may be too low—you need at least 10 (Fledgling tier) for the first effects.
2. Check that `effectStrengthMultiplier` isn't set to 0.
3. Some effects are alignment-specific and conditional (e.g., Instinct effects only activate at night).

### Fracture effects are too harsh

- Reduce `fractureSeverityMultiplier` (e.g., 0.5).
- Set `fractureRealityTearEnabled` to `false` to disable mob spawning.
- Increase `fractureThreshold` to make fracture harder to trigger.
- Focus your kills on one or two alignments to reduce fracture.

### Mob behavior isn't changing

1. Your alignment strength may be too low—most perception changes require 25%+ of Apex threshold.
2. Verify individual toggles like `passiveMobFearEnabled` are `true`.
3. Mob perception is probabilistic—it won't trigger 100% of the time.

### No particles showing

1. Check that `particlesEnabled` is `true` in the config.
2. Your alignment strength must be at least 20% for ambient particles, or 40% for visible effects.
3. Particles are server-side and should be visible to all players.

### Performance issues

- Increase `particleInterval` and `resonanceCheckInterval` values.
- Reduce `resonanceRadius` and `soulEchoRadius`.
- Disable individual systems you don't want (`resonanceEnabled`, `soulEchoEnabled`, `particlesEnabled`).
- Increase `fractureEffectInterval` if fracture processing is heavy.

---

## **License**

This project is licensed under the **MIT License**. See [LICENSE](LICENSE) for details.
