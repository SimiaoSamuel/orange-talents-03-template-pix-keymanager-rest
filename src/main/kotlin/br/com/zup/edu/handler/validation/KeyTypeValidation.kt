package br.com.zup.edu.handler.validation

import br.com.zup.edu.dto.SavePixRequest
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [KeyValidator::class])
annotation class ValidKey(
    val message: String = "chave inválida para o tipo escolhido",
    val payload: Array<KClass<Payload>> = [],
    val groups: Array<KClass<Any>> = []
)

@Singleton
class KeyValidator: ConstraintValidator<ValidKey, SavePixRequest> {
    override fun isValid(
        value: SavePixRequest?,
        annotationMetadata: AnnotationValue<ValidKey>,
        context: ConstraintValidatorContext
    ): Boolean {
        if (value?.keyType == null)
            return false

        return value.keyType.valida(value.key)
    }
}