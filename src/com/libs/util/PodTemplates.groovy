package com.libs.util

def Create(def containers) {
    def templates = []
    containers.each { i ->
        i.name            = i.name            ?: ''
        i.image           = i.image           ?: ''
        i.command         = i.command         ?: 'cat'
        i.args            = i.args            ?: ''
        i.ttyEnabled      = i.ttyEnabled      ?: true
        i.privileged      = i.privileged      ?: true
        i.alwaysPullImage = i.alwaysPullImage ?: false

        // Add containerTemplate to list, and then return
        templates.add(containerTemplate(name:            i.name, 
                                        image:           i.image, 
                                        command:         i.command,
                                        args:            i.args,
                                        ttyEnabled:      i.ttyEnabled, 
                                        privileged:      i.privileged,
                                        alwaysPullImage: i.alwaysPullImage))
    }
    return templates
}