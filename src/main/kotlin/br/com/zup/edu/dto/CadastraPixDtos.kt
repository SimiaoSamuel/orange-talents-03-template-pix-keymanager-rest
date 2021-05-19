package br.com.zup.edu.dto

import br.com.zup.edu.AccountType
import br.com.zup.edu.CreateKeyRequest
import br.com.zup.edu.KeyType
import br.com.zup.edu.handler.validation.ValidKey
import org.apache.commons.validator.routines.EmailValidator
import java.util.*
import javax.validation.constraints.NotNull

@ValidKey
data class SavePixRequest(
    @field:NotNull
    val keyType: KeyTypeDto?,
    val key: String?,
    @field:NotNull
    val accountType: AccountTypeDto?
){
    fun converte(idCliente: UUID): CreateKeyRequest? {
        return CreateKeyRequest.newBuilder()
            .setTipoConta(accountType!!.converte())
            .setIdCliente(idCliente.toString())
            .setTipoChave(keyType!!.converte())
            .setChave(key)
            .build()
    }
}

data class SavePixResponse(
    val idPix: Long
)
