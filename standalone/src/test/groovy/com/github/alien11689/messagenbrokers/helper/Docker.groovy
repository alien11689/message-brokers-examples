package com.github.alien11689.messagenbrokers.helper

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@CompileStatic
@Slf4j
class Docker {
    static final boolean isRunning(String... containerNames) {
        StringBuffer sout = new StringBuffer()
        StringBuffer serr = new StringBuffer()
        Process proc = 'docker ps'.execute()
        proc.consumeProcessOutput(sout, serr)
        proc.waitFor()
        String output = sout.toString()
        log.debug('Docker ps: {}', output)
        return containerNames.collect { String container -> output.contains(container) }.unique() == [true]
    }
}
