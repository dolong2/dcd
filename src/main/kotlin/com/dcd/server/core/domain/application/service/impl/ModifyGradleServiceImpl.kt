package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.cmd.FileContent
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.service.ModifyGradleService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import org.springframework.stereotype.Service
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

@Service
class ModifyGradleServiceImpl(
    private val queryApplicationPort: QueryApplicationPort,
) : ModifyGradleService{
    override fun modifyGradleByApplicationId(id: String) {
        val application = queryApplicationPort.findById(id) ?: throw ApplicationNotFoundException()
        val name = application.name
        val fileName =
        if (File("./$name/build.gradle.kts").exists()) {
            "build.gradle.kts"
        } else {
            "build.gradle"
        }
        val buildGradle =
        if (fileName == "build.gradle.kts") {
            FileContent.getBuildGradleKtsFileContent(name)
        } else {
            FileContent.getBuildGradleFileContent(name)
        }
        FileWriter("./$name/$fileName", true).use { fileWriter ->
            BufferedWriter(fileWriter).use { bufferedWriter ->
                bufferedWriter.write(buildGradle)
                bufferedWriter.newLine()
                bufferedWriter.close()
            }
        }
    }

    override fun modifyGradleByApplication(application: Application) {
        val name = application.name
        val fileName =
            if (File("./$name/build.gradle.kts").exists()) {
                "build.gradle.kts"
            } else {
                "build.gradle"
            }
        val buildGradle =
        if (fileName == "build.gradle.kts") {
            FileContent.getBuildGradleKtsFileContent(name)
        } else {
            FileContent.getBuildGradleFileContent(name)
        }
        FileWriter("./$name/$fileName", true).use { fileWriter ->
            BufferedWriter(fileWriter).use { bufferedWriter ->
                bufferedWriter.write(buildGradle)
                bufferedWriter.newLine()
                bufferedWriter.close()
            }
        }
    }
}