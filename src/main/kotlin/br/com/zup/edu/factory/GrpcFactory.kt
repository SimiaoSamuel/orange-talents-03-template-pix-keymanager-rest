package br.com.zup.edu.factory

import br.com.zup.edu.Keymanagergrpc
import br.com.zup.edu.KeymanagergrpcServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class GrpcFactory (@GrpcChannel(value = "keymanager") val channel: ManagedChannel){

    @Singleton
    fun pixClientStub():
            KeymanagergrpcServiceGrpc.KeymanagergrpcServiceBlockingStub {
        return KeymanagergrpcServiceGrpc.newBlockingStub(channel)
    }
}