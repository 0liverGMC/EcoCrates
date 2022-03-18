package com.willfp.ecocrates.crate.placed

import com.willfp.eco.core.integrations.hologram.HologramManager
import com.willfp.ecocrates.crate.Crate
import org.bukkit.Location
import org.bukkit.entity.Item
import org.bukkit.util.Vector

class PlacedCrate(
    val crate: Crate,
    blockLocation: Location
) {
    // Center the location, they're mutable because bukkit is bad at designing APIs.
    private val location = blockLocation.clone().apply {
        x += 0.5
        y += 0.5
        z += 0.5
    }

    private val world = location.world!!

    private val hologram = HologramManager.createHologram(
        location.clone().add(0.0, crate.hologramHeight, 0.0),
        crate.hologramLines
    )

    private var item: Item? = null

    internal fun tick(tick: Int) {
        tickRandomReward(tick)
    }

    internal fun tickAsync(tick: Int) {
        tickParticles(tick)
    }

    internal fun onRemove() {
        hologram.remove()
        item?.remove()
    }

    private fun spawnRandomReward() {
        if ((item == null || item?.isDead == true) && crate.isShowingRandomReward) {
            val entity = world.dropItem(
                location.clone().add(0.0, crate.randomRewardHeight, 0.0),
                crate.rewards.first().display
            )
            entity.velocity = Vector(0.0, 0.0, 0.0)
            entity.pickupDelay = Int.MAX_VALUE
            entity.setGravity(false)
            entity.isCustomNameVisible = true
            entity.customName = crate.randomRewardName
            item = entity
        }
    }

    private fun tickRandomReward(tick: Int) {
        if (tick % crate.randomRewardDelay == 0) {
            spawnRandomReward()
            item?.itemStack = crate.rewards.random().display
            item?.teleport(location.clone().add(0.0, crate.randomRewardHeight, 0.0))
        }
    }

    private fun tickParticles(tick: Int) {
        for ((particle, animation) in crate.particles) {
            animation.spawnParticle(location, tick, particle)
        }
    }
}
