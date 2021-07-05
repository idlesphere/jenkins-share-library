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

