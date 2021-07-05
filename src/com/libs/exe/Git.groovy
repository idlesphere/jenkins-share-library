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

/*
For bitbucket only
*/
def updateStatus(Map args) {
    args.credential = args.credential ?: 'bitbucketOauth'
    args.status     = args.status     ?: '' // [ "INPROGRESS", "SUCCESSFUL", "FAILED" ]
    args.project    = args.project    ?: '' // Project name
    args.revision   = args.revision   ?: '' // Commit hash

    bitbucketStatusNotify(
            credentialsId : args.credential,
            buildState    : args.status,
            repoSlug      : args.project,
            commitId      : args.revision
    )
}

/*
Get revision
*/
def getRevision(Map args) {
    args.full  = args.full ?: false // default is false, so we get "git rev-parse --short"
    args.path  = args.path ?: '.'

    def command = ""
    if (!args.full) {command = "--short"}

    dir(args.path) {
        def revision = sh(
            script: "git rev-parse ${command} HEAD",
            returnStdout: true
        ).trim()
    }
    return revision
}


def tag(Map args) {
    args.credential = args.credential ?: 'bitbucket'
    args.tag        = args.tag        ?: ''
    args.url        = args.url        ?: ''
  
    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: args.credential, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
        sh("git remote -v; git tag -f ${args.tag}; git push ${args.url} --tags --force")
    }
}
