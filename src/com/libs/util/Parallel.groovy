package com.libs.util

def parallelRun(Map args, maxParallel = 10) {
    def running = 0
    args.each { name, body ->
        if (body instanceof Closure) {
            args[name] = {
                while (!(running < maxParallel)) {
                    continue
                }
                running += 1
                body.call()
                running -= 1
            }
        }
    }
    if (args) {
        parallel args
    }
}
