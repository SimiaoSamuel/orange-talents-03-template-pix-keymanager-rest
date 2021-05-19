package br.com.zup.edu.endpoint

import br.com.zup.edu.*
import br.com.zup.edu.dto.ListaPixDtos
import br.com.zup.edu.factory.GrpcFactory
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
class ListaPixEndpoint {
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
    fun `deve retornar 200 em caso de sucesso na chamada`(){
        val request = ListKeyRequest.newBuilder()
            .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .build()

        val responseGrpc = ListKeyResponse.newBuilder()
            .setIdCliente(request.idCliente)
            .build()

        Mockito.`when`(grpcClient.lista(request)).thenReturn(responseGrpc)


        val response = client.toBlocking()
            .exchange<Any, ListaPixDtos>(HttpRequest.GET("/api/clientes/${request.idCliente}/pix"))

        with(response){
            Assertions.assertEquals(HttpStatus.OK.code, status.code)
        }
    }

    @Factory
    @Replaces(factory = GrpcFactory::class)
    internal class FactoryClientMock{
        @Singleton
        fun gRpcClient() = Mockito.mock(KeymanagergrpcServiceGrpc.KeymanagergrpcServiceBlockingStub::class.java)
    }
}