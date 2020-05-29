package org.javafreedom.kcd.domain.service

import org.javafreedom.kcd.domain.model.BloodSugarUnits
import org.javafreedom.kcd.domain.model.Element
import org.javafreedom.kcd.domain.model.ObservationTypes
import org.javafreedom.kcd.domain.model.Quantity
import org.valiktor.functions.isEqualTo
import org.valiktor.functions.isValid
import org.valiktor.functions.validate
import org.valiktor.validate

val validators = mapOf("BloodSugar" to BloodSugarValidator)

interface Validator<T> {

    fun isValid(elementToValidate: T)

}

object BloodSugarValidator : Validator<Element> {

    override fun isValid(elementToValidate: Element) {
        validate(elementToValidate) {
            validate(Element::type).isEqualTo(ObservationTypes.BLOOD_SUGAR.name)
            validate(Element::quantity).validate {
                validate(Quantity::unit).isEqualTo(BloodSugarUnits.MGDL.name)
                validate(Quantity::amount).isValid { it is Int }
            }
        }
    }

}
