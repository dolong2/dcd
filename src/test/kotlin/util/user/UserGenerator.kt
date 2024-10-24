package util.user

import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.enums.Status
import com.dcd.server.core.domain.user.model.User

object UserGenerator {
    fun generateUser(
        email: String = "testEmail",
        password: String = "testPassword",
        name: String = "testName",
        roles: MutableList<Role> = mutableListOf(Role.ROLE_USER),
        status: Status = Status.CREATED
    ): User =
        User(
            email = email,
            password = password,
            name = name,
            roles = roles,
            status = status
        )
}