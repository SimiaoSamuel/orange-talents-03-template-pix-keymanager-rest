package br.com.zup.edu.endpoint

import br.com.zup.edu.*
import br.com.zup.edu.dto.DetailPixResponse
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
class BuscaPixEndpoint {
    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Inject
    lateinit var grpcClient: KeymanagergrpcServiceGrpc.KeymanagergrpcServiceBlockingStub

    val conta = Conta.newBuilder()
        .setIntituicao("60701190")
        .setAgencia("0001")
        .setNumeroConta("291900")
        .setTipoConta(AccountType.CONTA_CORRENTE)
        .build()

    val titular = Titular.newBuilder()
        .setNome("Rafael")
        .setCpf("02467781054")
        .build()

    val pix = PixKey.newBuilder()
        .setNome(titular)
        .setChave("02467781054")
        .setConta(conta)
        .setTipoChave(KeyType.CPF)

    @AfterEach
    fun tearDown(){
        Mockito.reset(grpcClient)
    }

    @Test
    fun `deve retornar um pix em caso de dados validos`(){
        val request = SearchKeyRequest.newBuilder()
            .setPixValue("02467781054")
            .build()

        val responseGrpc = SearchKeyResponse.newBuilder()
            .setPix(pix)
            .build()

        Mockito.`when`(grpcClient.busca(request)).thenReturn(responseGrpc)

        val response = client.toBlocking()
            .exchange<Any, DetailPixResponse>(HttpRequest.GET("/api/public/pix/${request.pixValue}"))

        println(response.body())

        with(response){
            Assertions.assertEquals(HttpStatus.OK.code, status.code)
        }
    }

    @Test
    fun `deve retornar um pix em caso de dados validos com cliente id`(){
        val request = SearchKeyRequest.newBuilder()
            .setPixCliente(
                SearchKeyRequest.SearchPixId.newBuilder()
                    .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
                    .setIdPix(1L)
                    .build()
            )
            .build()

        val responseGrpc = SearchKeyResponse.newBuilder()
            .setIdCliente(request.pixCliente.idCliente)
            .setIdPix(request.pixCliente.idPix)
            .setPix(pix)
            .build()

        Mockito.`when`(grpcClient.busca(request)).thenReturn(responseGrpc)

        val response = client.toBlocking()
            .exchange<Any, DetailPixResponse>(
                HttpRequest.GET("/api/clientes/${request.pixCliente.idCliente}/pix/${request.pixCliente.idPix}")
            )

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