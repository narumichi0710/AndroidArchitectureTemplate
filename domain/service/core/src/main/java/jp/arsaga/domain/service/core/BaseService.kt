package jp.arsaga.domain.service.core

interface BaseService<Dependency> {
    val dependency: Dependency
    interface Dependency {
        val command: Any?
        val query: Any?
    }
}