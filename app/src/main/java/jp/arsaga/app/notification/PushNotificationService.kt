package jp.arsaga.app.notification

import jp.co.arsaga.extensions.gateway.PushNotification

class PushNotificationService : PushNotification.AbstractService<PushNotificationType>() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }
}