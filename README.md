![Logo](https://cloud-9hg0lq1yj-hack-club-bot.vercel.app/0weightimage.png)
# Item Weights Mod
A server-sided Fabric Minecraft mod that  a dynamic weight system that affects player movement, mining speed, and fall damage.

For every item in a player's inventory, including armor, the player's weight increases, affecting their movement speed, mining speed, and fall damage. The player's weight is calculated by the sum of the weight of each item in their inventory. The weight of each item is determined by the item's material and the item's weight value.
## Features
- Slowness Effect
  - Carrying more items will slow the player down.
- Mining Speed Reduction
  - Increased weight decreases the player's breaking speed.
- Fall Damage Increase
  - The heavier the player, the more fall damage they will take.
- Environmental Changes
  - Rain, thunderstorms, and the Nether will also decrease the player's movement.
- Fully Configurable
  - Adjust item weight values, enable or disable all features above, and more...

## Usage
Download the mod in the [releases tab](https://github.com/William-Chapin/Weight-Mod/releases/tag/Fabric) and put it in your servers mod folder. 

### Item Weights Configuration
To configure item weights, go to the weight config file at ``/mods/weight/weight_config.json``. To change the item weight, replace the "item" value with the item's Minecraft ID, and replace the "percent" value with the percent slowness you want applied to the player (ex. 0.03 would be a 3% decrease per item).
```json
{
  "itemWeights": [
    {
      "item": "netherite",
      "percent": 0.003
    },
    {
      "item": "diamond",
      "percent": 0.0015
    },
    {
      "item": "diamond_chestplate",
      "percent": 0.012
    }
  ]
}
```

### Mod Configuration
Additionally, you can configure the mod's behavior in the ``/mods/weight/configuration.properties``file. This file contains the configuration for the whole mod and contains over 16 configuration options!
```properties
# Slowness multiplier for item weight (default 1)
slownessMultiplier=1

# Maximum weight (2 would be twice as slow as normal)
maxWeight=3

# Should it decrease the player's breaking speed? (same calculation and multiplier as slowness)
breakingSpeed=true

# Include equipped items in weight calculation (armor/offhand)
includeEquipped=true

# Default weight for all other items
defaultWeight=0.0003

# And much more...
```
## Demonstration
![Demonstration](https://cloud-77lk866m6-hack-club-bot.vercel.app/00201.gif)
