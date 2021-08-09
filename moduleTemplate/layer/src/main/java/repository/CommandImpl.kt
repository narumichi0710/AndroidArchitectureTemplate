package jp.arsaga.dataStore.repository.{Small}

import android.content.Context
import jp.arsaga.domain.useCase.{Small}.{Large}UseCase
import jp.arsaga.domain.useCase.core.ActivityCallback
import kotlinx.coroutines.CoroutineScope

class {Large}CommandImpl(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) : {Large}UseCase.Command<ActivityCallback> {

}