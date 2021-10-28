package ru.netologia.Factory

import androidx.work.DelegatingWorkerFactory
import ru.netologia.repository.IPostRepository
import javax.inject.Inject

class DependencyWorkerFactory @Inject constructor(
    repository: IPostRepository
) : DelegatingWorkerFactory() {
    init {
        addFactory(RefreshPostsWorkerFactory(repository))
        addFactory(RemovePostWorkerFactory(repository))
        addFactory(SavePostWorkerFactory(repository))
    }
}
