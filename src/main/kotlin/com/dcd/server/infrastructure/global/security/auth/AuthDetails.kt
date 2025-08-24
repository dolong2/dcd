package com.dcd.server.infrastructure.global.security.auth

import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.user.model.enums.Status
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class AuthDetails(
    private val user: User
) : UserDetails{
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
        user.roles
            .map { SimpleGrantedAuthority(it.name) }
            .toMutableList()

    override fun getPassword(): String? = null

    override fun getUsername(): String = user.id

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = user.status != Status.PENDING

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = this.authorities.isNotEmpty()
}