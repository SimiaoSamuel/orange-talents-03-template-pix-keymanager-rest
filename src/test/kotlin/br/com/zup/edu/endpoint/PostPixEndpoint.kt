package br.com.zup.edu.endpoint

import br.com.zup.edu.CreateKeyResponse
import br.com.zup.edu.KeymanagergrpcServiceGrpc
import br.com.zup.edu.dto.AccountTypeDto
import br.com.zup.edu.dto.KeyTypeDto
import br.com.zup.edu.dto.SavePixRequest
import br.com.zup.edu.dto.SavePixResponse
import br.com.zup.edu.factory.GrpcFactory
import io.grpc.Status
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
class PostPixEndpoint {

    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Inject
    lateinit var grpcClient: KeymanagergrpcServiceGrpc.KeymanagergrpcServiceBlockingStub

    @AfterEach
    fun tearDown(){
        Mockito.reset(grpcClient)
    }

    @Test
    fun `salvando pix com dados valido`(){
        val savePixRequest = SavePixRequest(
            keyType = KeyTypeDto.CPF,
            key = "48710110110",
            accountType = AccountTypeDto.CONTA_CORRENTE
        )

        val idCliente = "c56dfef4-7901-44fb-84e2-a2cefb157890"

        val requestGrpc = savePixRequest.converte(idCliente = UUID.fromString(idCliente))

        val responseGrpc = CreateKeyResponse.newBuilder().setIdPix(1L).build()

        Mockito.`when`(grpcClient.cria(requestGrpc)).thenReturn(responseGrpc)

        val responseHttp = client.toBlocking()
            .exchange<SavePixRequest, SavePixResponse>(
                HttpRequest.POST("/api/clientes/$idCliente/pix", savePixRequest)
            )


        with(responseHttp){
            Assertions.assertEquals(HttpStatus.CREATED.code,this.code())
            Assertions.assertEquals("api/clientes/$idCliente/pix/1"
                ,this.header("Location"))
        }
    }

    @Test
    fun `requisicao com dados do body invalidos`(){
        val savePixRequest = SavePixRequest(
            keyType = null,
            key = "",
            accountType = null
        )

        val idCliente = "c56dfef4-7901-44fb-84e2-a2cefb157890"

        val assertThrows = assertThrows<HttpClientResponseException> {
            val responseHttp = client.toBlocking()
                .exchange<SavePixRequest, SavePixResponse>(
                    HttpRequest.POST("/api/clientes/$idCliente/pix", savePixRequest)
                )
        }

        with(assertThrows){
            Assertions.assertEquals("Bad Request", this.message)
            Assertions.assertEquals(HttpStatus.BAD_REQUEST.code, this.status.code)
        }
    }

    @Test
    fun `requisicao com uuid invalido`(){
        val savePixRequest = SavePixRequest(
            keyType = KeyTypeDto.CPF,
            key = "48710110110",
            accountType = AccountTypeDto.CONTA_CORRENTE
        )

        val idCliente = "c5-79-4b-82-a2ce"

        val assertThrows = assertThrows<HttpClientResponseException> {
            client.toBlocking()
                .exchange<SavePixRequest, SavePixResponse>(
                    HttpRequest.POST("/api/clientes/$idCliente/pix", savePixRequest)
                )
        }

        with(assertThrows){
            Assertions.assertEquals("UUID Invalido", this.message)
            Assertions.assertEquals(HttpStatus.BAD_REQUEST.code, this.status.code)
        }
    }

    @Test
    fun `se pix existe deve lancar already exists exception`(){
        val savePixRequest = SavePixRequest(
            keyType = KeyTypeDto.CPF,
            key = "48710110110",
            accountType = AccountTypeDto.CONTA_CORRENTE
        )

        val idCliente = "c56dfef4-7901-44fb-84e2-a2cefb157890"

        val requestGrpc = savePixRequest.converte(idCliente = UUID.fromString(idCliente))

        Mockito.`when`(grpcClient.cria(requestGrpc)).thenThrow(Status.ALREADY_EXISTS.asRuntimeException())

        val assertThrows = assertThrows<HttpClientResponseException> {
            client.toBlocking()
                .exchange<SavePixRequest, SavePixResponse>(
                    HttpRequest.POST("/api/clientes/$idCliente/pix", savePixRequest)
                )
        }

        with(assertThrows){
            Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.code,this.status.code)
        }
    }

    @Test
    fun `se usuario nao existe deve lancar not found exception`(){
        val savePixRequest = SavePixRequest(
            keyType = KeyTypeDto.CPF,
            key = "48710110110",
            accountType = AccountTypeDto.CONTA_CORRENTE
        )

        val idCliente = "c56dfef4-7901-44fb-84e2-a2cefb157891"

        val requestGrpc = savePixRequest.converte(idCliente = UUID.fromString(idCliente))

        Mockito.`when`(grpcClient.cria(requestGrpc)).thenThrow(Status.NOT_FOUND.asRuntimeException())

        val assertThrows = assertThrows<HttpClientResponseException> {
            client.toBlocking()
                .exchange<SavePixRequest, SavePixResponse>(
                    HttpRequest.POST("/api/clientes/$idCliente/pix", savePixRequest)
                )
        }

        with(assertThrows){
            Assertions.assertEquals(HttpStatus.NOT_FOUND.code,this.status.code)
        }
    }

    @Test
    fun `se usuario nao e dono da chave deve lancar permission denied exception`(){
        val savePixRequest = SavePixRequest(
            keyType = KeyTypeDto.CPF,
            key = "48710110110",
            accountType = AccountTypeDto.CONTA_CORRENTE
        )

        val idCliente = "c56dfef4-7901-44fb-84e2-a2cefb157890"

        val requestGrpc = savePixRequest.converte(idCliente = UUID.fromString(idCliente))

        Mockito.`when`(grpcClient.cria(requestGrpc)).thenThrow(Status.PERMISSION_DENIED.asRuntimeException())

        val assertThrows = assertThrows<HttpClientResponseException> {
            client.toBlocking()
                .exchange<SavePixRequest, SavePixResponse>(
                    HttpRequest.POST("/api/clientes/$idCliente/pix", savePixRequest)
                )
        }

        with(assertThrows){
            Assertions.assertEquals(HttpStatus.UNAUTHORIZED.code,this.status.code)
        }
    }

    @Test
    fun `se requisicao bater no grpc com dados invalidos deve lancar invalid argument exception`(){
        val savePixRequest = SavePixRequest(
            keyType = KeyTypeDto.CPF,
            key = "48710110110",
            accountType = AccountTypeDto.CONTA_CORRENTE
        )

        val idCliente = "c56dfef4-7901-44fb-84e2-a2cefb157890"

        val requestGrpc = savePixRequest.converte(idCliente = UUID.fromString(idCliente))

        Mockito.`when`(grpcClient.cria(requestGrpc)).thenThrow(Status.INVALID_ARGUMENT.asRuntimeException())

        val assertThrows = assertThrows<HttpClientResponseException> {
            client.toBlocking()
                .exchange<SavePixRequest, SavePixResponse>(
                    HttpRequest.POST("/api/clientes/$idCliente/pix", savePixRequest)
                )
        }

        with(assertThrows){
            Assertions.assertEquals(HttpStatus.BAD_REQUEST.code,this.status.code)
        }
    }

    @Test
    fun `se grpc nao estiver funcionado deve lancar unavailable exception`(){
        val savePixRequest = SavePixRequest(
            keyType = KeyTypeDto.CPF,
            key = "48710110110",
            accountType = AccountTypeDto.CONTA_CORRENTE
        )

        val idCliente = "c56dfef4-7901-44fb-84e2-a2cefb157890"

        val requestGrpc = savePixRequest.converte(idCliente = UUID.fromString(idCliente))

        Mockito.`when`(grpcClient.cria(requestGrpc)).thenThrow(Status.UNAVAILABLE.asRuntimeException())

        val assertThrows = assertThrows<HttpClientResponseException> {
            client.toBlocking()
                .exchange<SavePixRequest, SavePixResponse>(
                    HttpRequest.POST("/api/clientes/$idCliente/pix", savePixRequest)
                )
        }

        with(assertThrows){
            Assertions.assertEquals(HttpStatus.SERVICE_UNAVAILABLE.code,this.status.code)
        }
    }

    @Test
    fun `se retornar um erro inesperado deve lancar internal server error exception`(){
        val savePixRequest = SavePixRequest(
            keyType = KeyTypeDto.CPF,
            key = "48710110110",
            accountType = AccountTypeDto.CONTA_POUPANCA
        )

        val idCliente = "c56dfef4-7901-44fb-84e2-a2cefb157890"

        val requestGrpc = savePixRequest.converte(idCliente = UUID.fromString(idCliente))

        Mockito.`when`(grpcClient.cria(requestGrpc)).thenThrow(RuntimeException())

        val assertThrows = assertThrows<HttpClientResponseException> {
            client.toBlocking()
                .exchange<SavePixRequest, SavePixResponse>(
                    HttpRequest.POST("/api/clientes/$idCliente/pix", savePixRequest)
                )
        }

        with(assertThrows){
            Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.code,this.status.code)
        }
    }

    @Factory
    @Replaces(factory = GrpcFactory::class)
    internal class FactoryClientMock{
        @Singleton
        fun gRpcClient() = Mockito.mock(KeymanagergrpcServiceGrpc.KeymanagergrpcServiceBlockingStub::class.java)
    }
}