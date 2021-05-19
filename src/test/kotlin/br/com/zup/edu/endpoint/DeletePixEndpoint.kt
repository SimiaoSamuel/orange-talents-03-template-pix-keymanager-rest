package br.com.zup.edu.endpoint

import br.com.zup.edu.DeleteKeyRequest
import br.com.zup.edu.DeleteKeyResponse
import br.com.zup.edu.KeymanagergrpcServiceGrpc
import br.com.zup.edu.factory.GrpcFactory
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
class DeletePixEndpoint {
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
    fun `deve remover um pix em caso de dados validos`(){
        val request = DeleteKeyRequest.newBuilder()
            .setIdPix(1L)
            .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .build()

        Mockito.`when`(grpcClient.deleta(request)).thenReturn(DeleteKeyResponse.newBuilder().build())


        val response = client.toBlocking()
            .exchange<Any, Any>(HttpRequest.DELETE("/api/clientes/${request.idCliente}/pix/${request.idPix}"))

        with(response){
            assertEquals(HttpStatus.NO_CONTENT.code, status.code)
        }

    }

    @Factory
    @Replaces(factory = GrpcFactory::class)
    internal class FactoryClientMock{
        @Singleton
        fun gRpcClient() = Mockito.mock(KeymanagergrpcServiceGrpc.KeymanagergrpcServiceBlockingStub::class.java)
    }
}