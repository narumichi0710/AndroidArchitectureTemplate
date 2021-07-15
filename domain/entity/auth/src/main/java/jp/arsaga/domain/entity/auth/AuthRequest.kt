package jp.arsaga.domain.entity.auth

import java.io.Serializable

interface AuthRequest : Serializable {
    val password: String
}
