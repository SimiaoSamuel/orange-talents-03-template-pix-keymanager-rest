package br.com.zup.edu.endpoint

import br.com.zup.edu.CreateKeyRequest
import br.com.zup.edu.KeymanagergrpcServiceGrpc
import br.com.zup.edu.dto.SavePixRequest
import br.com.zup.edu.dto.SavePixResponse
import br.com.zup.edu.handler.ErrorDto
import br.com.zup.edu.handler.validation.ValidUUID
import br.com.zup.edu.shared.OpenClass
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.ConstraintViolationException
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Controller("/api/clientes/{idCliente}/pix")
@OpenClass
@Validated
class KeyManagerController(@Inject val grpcClient: KeymanagergrpcServiceGrpc.KeymanagergrpcServiceBlockingStub) {

    @Post
    fun savePix(@Valid saveRequest: SavePixRequest,
                @PathVariable @ValidUUID idCliente: UUID): HttpResponse<Any> {
        val request = saveRequest.converte(idCliente)

        val response = grpcClient.cria(request)
        val location = HttpResponse.uri("api/clientes/$idCliente/pix/${response.idPix}")

        return HttpResponse.created(location)
    }

    @Error(global = true)
    fun error(request: HttpRequest<*>, e: Exception): HttpResponse<*> =
        when (e) {
            is ConstraintViolationException -> HttpResponse.badRequest(e.constraintViolations.map {
                ErrorDto(
                    message = it.message,
                    code = HttpStatus.BAD_REQUEST.code
                )
            })
            is StatusRuntimeException -> grpcError(e)
            else -> HttpResponse.serverError(
                ErrorDto(
                    message = "Erro no servidor",
                    code = 500
                )
            )
        }

    private fun grpcError(e: StatusRuntimeException): HttpResponse<Any> {
        return when (e.status.code) {
            Status.NOT_FOUND.code -> HttpResponse.notFound(
                ErrorDto(e.message, HttpStatus.NOT_FOUND.code)
            )
            Status.PERMISSION_DENIED.code -> HttpResponse.status<Any>(HttpStatus.UNAUTHORIZED)
                .body(ErrorDto(e.message, HttpStatus.UNAUTHORIZED.code))
            Status.INVALID_ARGUMENT.code -> HttpResponse.badRequest(
                ErrorDto(e.message, HttpStatus.BAD_REQUEST.code)
            )
            Status.ALREADY_EXISTS.code -> HttpResponse.unprocessableEntity<Any>()
                .body(ErrorDto(e.message, HttpStatus.UNPROCESSABLE_ENTITY.code))
            else -> HttpResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
        }
    }
}
