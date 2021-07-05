package com.libs.exe

def gitFetch() {
    def git = [ Depth: 1,
                Timeout: 600,
                Result: "",
                Credential: "${}",
                Url: "${}"
              ]

    stage 'Git Fetch'
    log.title("Start fetching code from: ${GIT_PROJECT}")

    retry(3) {
        timeout(time: git.Timeout , unit: 'SECONDS') {
            git.Result = checkout([$class                           : 'GitSCM',
                                   branches                         : [[name: GIT_BRANCH]],
                                   doGenerateSubmoduleConfigurations: false,
                                   extensions                       : [[$class: 'CloneOption', noTags: true, reference: '', shallow: true, depth: git.Depth]],
                                   submoduleCfg                     : [],
                                   userRemoteConfigs                : [[credentialsId: git.Credential, url: git.Url + GIT_PROJECT + '.git']]])
        }
    }

    return git.Result
}