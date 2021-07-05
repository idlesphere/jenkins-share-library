package com.libs.util

import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import hudson.model.*;
import groovy.json.*;

def loadJson(file_or_string) {
    def data

    if(fileExists(file_or_string)){
        data = readJSON file : file_or_string
    }else {
        data = readJSON text : file_or_string
    }

    return data
}

def saveJson(file_or_string, tofile_path) {
    def data

    if(fileExists(file_or_string)) {
        data = readJSON file : file_or_string
    }else {
        def jsonSlurper = new JsonSlurper(file_or_string)
        def new_json_object = jsonSlurper.parseText
        data = new_json_object
    }
    writeJSON file: tofile_path, json: data
}

def loadProperties(properties_file) {
     def data = readProperties interpolate: true, file: properties_file
     data.each {
        println ( it.key + " = " + it.value )
     }
}

def loadYaml(file_or_string) {
    def data

    if(fileExists(file_or_string)){
        data = readYaml file : file_or_string
    }else {
        data = readYaml text : file_or_string
    }

    return data
}

def saveYaml(data, yaml_path) {
    writeYaml file: yaml_path , data: data
}


def zipBase64(String s){
    def targetStream = new ByteArrayOutputStream()
    def zipStream = new GZIPOutputStream(targetStream)
    zipStream.write(s.getBytes('UTF-8'))
    zipStream.close()
    def zippedBytes = targetStream.toByteArray()
    targetStream.close()
    return zippedBytes.encodeBase64().toString()
}

def unzipBase64(String compressed){
    def inflaterStream = new GZIPInputStream(new ByteArrayInputStream(compressed.decodeBase64()))
    def uncompressedStr = inflaterStream.getText('UTF-8')
    return uncompressedStr
}



def toList(value) {
    [value].flatten().findAll { it != null }
}

def list_map_value(List data,String key) {
    def new_data = []
    for (i in data) {
        new_data.add(i[key])
    }
    return new_data
}

def list_map_find_other(List data, String key_1, String value_1, String key_2) {
    for (i in data) {
        new_data.add(i[key])
        if (i[key_1] == value_1 ) {
            if (i[key_2]) {
                def value_2 = i[key_2]
                return value_2
            } else {
                log.error("Fail to fetch ${key_2}")
                return false
            }
        }
    }
}

def check_repeated_step(List data) {
    def parallel_step = list_map_value(data, "step")
    
    if (parallel_step.clone().unique().size() != parallel_step.size()) {
        log.error("Exists repeated parallel.step ${parallel_step}")
        return true
    }
    return false
}

def translateStage(Map data) {
    // verify the data
    def result     = [:]
    def containers = [:]
    def data_new   = [:]

    // do the LowerCase first
    for (String key : data.keySet()) {
        String item_lower = key.toLowerCase();
        if (item_lower == "kind") {
            data_new.put(item_lower, data.get(key).toLowerCase());
        } else {
            data_new.put(item_lower, data.get(key));
        }
    }

    // toLowerCase
    def registered_kind = list_map_value(env.ENV_GLOBAL.registered_kind, "name")

    // verify_kind && generate containers
    if (data.kind) {
        if (data.kind in registered_kind) {
            if (! data.image) {   
                def kind_image = list_map_find_other(env.ENV_GLOBAL.registered_kind, "name", data.kind, "image")
                containers[kind_image] = UUID.randomUUID().toString().replaceAll("-", "").take(6)
                data_new["image"] = kind_image
            }
        } else {
            log.error("${data.kind} is invalid!")
            return false
        }
    }

    if (data.image) {
        containers[(data.image)] = UUID.randomUUID().toString().replaceAll("-", "").take(6)
    }

    result["containers"] = containers
    result["data"]       = data_new

    return result
}

def translateMap(List data) {
    // Do the lint and translate
    def config     = [:]
    def job        = []
    def containers = []

    def activate_tasks = toList(env.TASKS)

    // check repeated step name
    if (check_repeated_step(data)) {
        return false
    }

    for (key in data) {
        // handle key via tasks
        if (activate_tasks != null && !activate_tasks.contains(key.step) && !activate_tasks.contains("all")) {
            continue
        }

        if (key.parallel && key.kind) {
            log.error("Detect kind and parallel coexist in one step!")
            break
        }

        if (key.parallel) {
            // check repeated step name in parallel
            if (check_repeated_step(key.parallel)) {
                break
            }

            def parallel_job  = []
            def parallel_list = key.parallel
            // verify_kind && generate containers
            for (parallel_key in parallel_list) {
                def data_translated = translateStage(parallel_key)
                parallel_job.add(data_translated.data)
                containers.add(data_translated.containers)
            }
            key[parallel] = parallel_job
            job.add(key)
        } else {
            // verify_kind && generate containers
            def data_translated = translateStage(key)
            job.add(data_translated.data)
            containers.add(data_translated.containers)
        }
    }

    config["job"] = job
    config["containers"] = containers

    if (config) {
        log.prettyPrint(config)
        return config
    }

    return false
}

