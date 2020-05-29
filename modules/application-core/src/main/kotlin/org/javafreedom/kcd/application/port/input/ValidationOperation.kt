package org.javafreedom.kcd.application.port.input

interface ValidationOperation {

    fun isSupported(): Boolean

    fun validate():

}