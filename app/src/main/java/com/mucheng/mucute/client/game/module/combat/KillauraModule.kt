package com.mucheng.mucute.client.game.module.combat

import com.mucheng.mucute.client.game.InterceptablePacket
import com.mucheng.mucute.client.game.Module
import com.mucheng.mucute.client.game.ModuleCategory
import com.mucheng.mucute.client.game.entity.*
import org.cloudburstmc.math.vector.Vector3f
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket
import kotlin.math.atan2
import kotlin.math.sqrt
import kotlin.random.Random

class KillauraModule : Module("killaura", ModuleCategory.Combat) {

    private var playersOnly by boolValue("players_only", true)
    private var mobsOnly by boolValue("mobs_only", false)
    private var smartRotations by boolValue("rotations", true)

    private var rangeValue by floatValue("range", 3.8f, 2f..6f)
    private var cpsValue by intValue("cps", 12, 1..20)
    private var boost by intValue("packets", 1, 1..5)

    private var lastAttackTime = 0L
    private var nextRandomDelay = 0L

    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        if (!isEnabled) return

        val packet = interceptablePacket.packet
        if (packet is PlayerAuthInputPacket) {
            val currentTime = System.currentTimeMillis()
            
            if (currentTime - lastAttackTime < nextRandomDelay) return

            val closestEntities = searchForClosestEntities()
            if (closestEntities.isEmpty()) return

            val target = closestEntities.first()

            // Наводка головы на цель
            if (smartRotations) {
                packet.rotation = calculateRotations(target)
            }

            // Удар по цели
            repeat(boost) {
                session.localPlayer.attack(target)
            }

            // Рандомизация задержки кликов (+-35 мс)
            val baseDelay = 1000L / cpsValue
            nextRandomDelay = baseDelay + Random.nextLong(-35, 35)
            
            lastAttackTime = currentTime
        }
    }

    // Точный расчет углов без палева для античита
    private fun calculateRotations(entity: Entity): Vector3f {
        val playerPos = session.localPlayer.vec3Position
        val targetPos = entity.vec3Position

        val diffX = targetPos.x - playerPos.x
        // Целимся чуть ниже головы (примерно в грудь/центр хитбокса), чтобы не палиться идеальной наводкой в глаза
        val diffY = (targetPos.y + 1.0f) - (playerPos.y + 1.62f)
        val diffZ = targetPos.z - playerPos.z

        val diffXZ = sqrt(diffX * diffX + diffZ * diffZ)

        // Исправленная тригонометрия под координаты Bedrock
        var yaw = Math.toDegrees(atan2(diffZ, diffX)).toFloat() - 90f
        val pitch = (-Math.toDegrees(atan2(diffY, diffXZ))).toFloat()

        if (yaw < 0) yaw += 360f

        // Для Бедрока возвращаем Vector3f(pitch, yaw, yaw), где третий параметр отвечает за поворот шеи
        return Vector3f.from(pitch, yaw, yaw)
    }

    private fun Entity.isTarget(): Boolean {
        return when (this) {
            is LocalPlayer -> false
            is Player -> {
                if (mobsOnly) false else !this.isBot()
            }
            is EntityUnknown -> {
                if (mobsOnly) isMob() else !playersOnly
            }
            else -> false
        }
    }

    private fun EntityUnknown.isMob(): Boolean {
        return this.identifier in MobList.mobTypes
    }

    private fun Player.isBot(): Boolean {
        if (this is LocalPlayer) return false
        val playerList = session.level.playerMap[this.uuid] ?: return true
        return playerList.name.isBlank()
    }

    private fun searchForClosestEntities(): List<Entity> {
        return session.level.entityMap.values
            .filter { entity -> entity.distance(session.localPlayer) < rangeValue && entity.isTarget() }
            .sortedBy { entity -> entity.distance(session.localPlayer) }
    }
}
