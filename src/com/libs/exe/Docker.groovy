package com.libs.exe

// https://www.jenkins.io/doc/book/pipeline/docker/
def build() {
    args.credential           = args.credential ?: "JFrogAuth"
    args.url                  = args.url        ?: "https://iherb-docker-local.jfrog.io"
    args.image                = args.image
    args.tag                  = args.tag
    args.dockerfile_path      = args.dockerfile_path      ?: "./Dockerfile"
    args.docker_build_context = args.docker_build_context ?: "."

    log.title("Docker")

    docker.withRegistry(args.url, args.credential) {
        log.info("Build")
        def docker_image = docker.build("${args.url}/${args.image}:${args.tag}", "-f ${args.dockerfile_path} ${args.docker_build_context}")

        /* Run Commands in the container*/
        // log.info("Unit_Test")
        // docker_image.inside {
        //     sh 'make test'
        // }

        /* Push the container to the custom Registry */
        log.info("Push")
        docker_image.push()
    }
}