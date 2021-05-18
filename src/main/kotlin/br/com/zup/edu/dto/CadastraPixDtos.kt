package br.com.zup.edu.dto

import br.com.zup.edu.AccountType
import br.com.zup.edu.CreateKeyRequest
import br.com.zup.edu.KeyType
import br.com.zup.edu.handler.validation.ValidKey
import org.apache.commons.validator.routines.EmailValidator
import java.util.*
import javax.validation.constraints.Max
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidKey
data class SavePixRequest(
    @field:NotNull
    val keyType: KeyTypeRequest?,
    val key: String?,
    @field:NotNull
    val accountType: AccountTypeRequest?
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

enum class KeyTypeRequest {
    CPF{
        override fun valida(key: String?): Boolean {
            return !key.isNullOrBlank() && key.matches("^[0-9]{11}$".toRegex())
        }
    },
    PHONE{
        override fun valida(key: String?): Boolean {
            return !key.isNullOrBlank() && key.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
    },
    EMAIL{
        override fun valida(key: String?): Boolean {
            return !key.isNullOrBlank() && EmailValidator.getInstance().isValid(key)
        }
    },
    RANDOM{
        override fun valida(key: String?): Boolean {
            return key.isNullOrBlank()
        }
    };

    fun converte(): KeyType{
        return when(this){
            CPF -> KeyType.CPF
            RANDOM -> KeyType.RANDOM
            EMAIL -> KeyType.EMAIL
            PHONE -> KeyType.PHONE
        }
    }

    abstract fun valida(key: String?): Boolean
}

enum class AccountTypeRequest {
    CONTA_CORRENTE, CONTA_POUPANCA;

    fun converte(): AccountType{
        return when(this){
            CONTA_CORRENTE -> AccountType.CONTA_CORRENTE
            CONTA_POUPANCA -> AccountType.CONTA_POUPANCA
        }
    }
}
