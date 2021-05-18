package br.com.zup.edu.handler.validation

import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import java.util.UUID
import javax.inject.Singleton
import javax.validation.Constraint
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [UUIDValidator::class])
annotation class ValidUUID(
    val message: String = "UUID Invalido",
    val payload: Array<KClass<Payload>> = [],
    val groups: Array<KClass<Any>> = []
)

@Singleton
class UUIDValidator: ConstraintValidator<ValidUUID, UUID> {
    override fun isValid(
        value: UUID?,
        annotationMetadata: AnnotationValue<ValidUUID>,
        context: ConstraintValidatorContext
    ): Boolean {
        return value.toString().matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}".toRegex())
    }
}