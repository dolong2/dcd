package com.dcd.server.infrastructure.domain.application.adapter

import com.dcd.server.core.domain.application.event.ChangeApplicationStatusEvent
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.spi.ApplicationRemoteRepoPort
import com.dcd.server.core.domain.application.util.FailureCase
import org.eclipse.jgit.api.Git
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import java.io.File

@Component
class ApplicationGitRepoAdapter(
    private val eventPublisher: ApplicationEventPublisher
) : ApplicationRemoteRepoPort {
    private val log = LoggerFactory.getLogger(this::class.simpleName)

    override fun cloneApplicationRemoteRepo(application: Application) {
        try {
            application.gitRepoUrl 
                ?: throw IllegalArgumentException("Git Repo URL is null for application: ${application.name}")
            
            Git.cloneRepository()
                .setURI(application.gitRepoUrl)
                .setDirectory(File("./${application.directoryName}"))
                .call().use { log.debug("Git clone completed for application: ${application.name}") }
        } catch (e: Exception) {
            val cloneFailureEvent = ChangeApplicationStatusEvent(ApplicationStatus.FAILURE, application, FailureCase.CLONE_FAILURE, e.message)
            eventPublisher.publishEvent(cloneFailureEvent)
            throw e
        }
    }
}