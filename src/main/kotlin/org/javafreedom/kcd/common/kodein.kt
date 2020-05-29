package org.javafreedom.kcd.common

import io.ktor.application.Application
import org.kodein.di.Kodein
import org.kodein.di.ktor.KodeinKey

fun Application.subKodein(init: Kodein.MainBuilder.() -> Unit) =
    this.attributes.put(KodeinKey, org.kodein.di.subKodein(parentKodein = this.attributes[KodeinKey], init = init))
