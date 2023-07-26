package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the buildType with id = 'ReleaseMinor'
accordingly, and delete the patch script.
*/
changeBuildType(RelativeId("ReleaseMinor")) {
    vcs {

        check(branchFilter == "+:<default>") {
            "Unexpected option value: branchFilter = $branchFilter"
        }
        branchFilter = """
            +:<default>
            +:refs/tags/*
        """.trimIndent()
    }

    expectSteps {
        gradle {
            tasks = "clean scripts:calculateNewVersion"
            buildFile = ""
            enableStacktrace = true
        }
        gradle {
            enabled = false
            tasks = "clean publishPlugin"
            buildFile = ""
            enableStacktrace = true
        }
        gradle {
            name = "Run Integrations"
            enabled = false
            tasks = "releaseActions"
        }
        gradle {
            name = "Slack Notification"
            enabled = false
            tasks = "slackNotification"
        }
    }
    steps {
        insert(0) {
            script {
                name = "Pull git tags"
                scriptContent = "git fetch --tags origin"
            }
        }
    }
}
