package jp.arsaga.app.lifecycleCallback

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import jp.co.arsaga.extensions.gateway.ConnectingApiStatus
import jp.co.arsaga.extensions.view.activity.HasProgressBarActivity

object ProgressLoadingHandler : HasProgressBarActivity.LoadingHandler() {

    override val loadingStatus: LiveData<Boolean?> = ConnectingApiStatus.Default.connectingCount.map {
        it != 0
    }
}