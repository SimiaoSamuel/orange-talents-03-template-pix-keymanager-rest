package br.com.zup.edu.endpoint

import br.com.zup.edu.*
import br.com.zup.edu.dto.*
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
import java.awt.print.Pageable
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.ConstraintViolationException
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Controller("/api")
@OpenClass
@Validated
class KeyManagerController(@Inject val grpcClient: KeymanagergrpcServiceGrpc.KeymanagergrpcServiceBlockingStub) {

    @Post("/clientes/{idCliente}/pix")
    fun savePix(
        @Valid saveRequest: SavePixRequest,
        @PathVariable @ValidUUID idCliente: UUID
    ): HttpResponse<Any> {
        val request = saveRequest.converte(idCliente)

        val response = grpcClient.cria(request)
        val location = HttpResponse.uri("api/clientes/$idCliente/pix/${response.idPix}")

        return HttpResponse.created(location)
    }

    @Delete("/clientes/{idCliente}/pix/{idPix}")
    fun deletePix(@PathVariable @ValidUUID idCliente: UUID, @PathVariable idPix: Long): MutableHttpResponse<Any> {
        val request = DeleteKeyRequest.newBuilder()
            .setIdCliente(idCliente.toString())
            .setIdPix(idPix)
            .build()

        grpcClient.deleta(request)

        return HttpResponse.noContent()
    }

    @Get("/clientes/{idCliente}/pix/{idPix}")
    fun getPix(
        @PathVariable @ValidUUID idCliente: UUID,
        @PathVariable idPix: Long
    ): MutableHttpResponse<DetailPixResponse> {
        val request = SearchKeyRequest.newBuilder()
            .setPixCliente(
                SearchKeyRequest.SearchPixId.newBuilder()
                    .setIdPix(idPix)
                    .setIdCliente(idCliente.toString())
                    .build()
            )
            .build()
        val response = grpcClient.busca(request)

        return HttpResponse.ok(response.toDto())
    }

    @Get("/public/pix/{key}")
    fun getPixByChave(@PathVariable @NotBlank key: String): MutableHttpResponse<DetailPixResponse> {
        val request = SearchKeyRequest.newBuilder()
            .setPixValue(key)
            .build()
        val response = grpcClient.busca(request)

        return HttpResponse.ok(response.toDto())
    }

    @Get("/clientes/{idCliente}/pix")
    fun getAllPix(@PathVariable @ValidUUID idCliente: UUID): MutableHttpResponse<ListaPixDtos> {
        val request = ListKeyRequest.newBuilder()
            .setIdCliente(idCliente.toString())
            .build()
        val response = grpcClient.lista(request)

        return HttpResponse.ok(response.toDto())
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
