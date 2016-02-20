package com.github.alien11689.messagenbrokers.helper

import groovy.transform.CompileStatic

@CompileStatic
class Endpoint {
    static boolean isOk(String address) {
        try {
            return new URL(address)?.text == 'OK'
        } catch (e) {
            return false
        }
    }
}
