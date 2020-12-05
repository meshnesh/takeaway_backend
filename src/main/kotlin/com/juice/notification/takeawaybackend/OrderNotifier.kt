package com.juice.notification.takeawaybackend

import com.juice.notification.takeawaybackend.data.Order
import com.juice.notification.takeawaybackend.data.OrderItemStatus
import com.pusher.pushnotifications.PushNotifications
import com.pusher.rest.Pusher
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class OrderNotifier(
        @Value("\${pusher.beams.instance_id}") beamsInstanceId: String,
        @Value("\${pusher.beams.secret}") beamsSecretKey: String,
        @Value("\${pusher.channels.app_id}") channelsAppId: String,
        @Value("\${pusher.channels.key}") channelsKey: String,
        @Value("\${pusher.channels.secret}") channelsSecret: String,
        @Value("\${pusher.channels.cluster}") channelsCluster: String
) {

    private val beams: PushNotifications = PushNotifications(beamsInstanceId, beamsSecretKey)
    private val channels: Pusher = Pusher(channelsAppId, channelsKey, channelsSecret)

    init {
        channels.setCluster(channelsCluster)
        channels.setEncrypted(true)
    }

    fun notify(order: Order) {
        sendBeamsNotification(order)
        sendChannelsNotification(order)
    }

    private fun sendBeamsNotification(order: Order) {
        val itemStatusCounts = order.items.groupBy { it.status }
                .mapValues { it.value.size }

        beams.publish(listOf(order.id),
                mapOf(
                        "fcm" to mapOf(
                                "data" to mapOf(
                                        "order" to order.id,
                                        "status" to order.status.name,
                                        "itemsPending" to (itemStatusCounts[OrderItemStatus.PENDING] ?: 0).toString(),
                                        "itemsStarted" to (itemStatusCounts[OrderItemStatus.STARTED] ?: 0).toString(),
                                        "itemsFinished" to (itemStatusCounts[OrderItemStatus.FINISHED] ?: 0).toString()
                                )
                        )
                ))
    }

    private fun sendChannelsNotification(order: Order) {
        channels.trigger("orders", "order-update", mapOf(
                "order" to order.id,
                "status" to order.status.name
        ))
    }
}