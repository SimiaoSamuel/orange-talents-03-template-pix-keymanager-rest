package br.com.zup.edu.dto

import br.com.zup.edu.AccountType
import br.com.zup.edu.KeyType
import org.apache.commons.validator.routines.EmailValidator

enum class KeyTypeDto {
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

    fun converte(): KeyType {
        return when(this){
            CPF -> KeyType.CPF
            RANDOM -> KeyType.RANDOM
            EMAIL -> KeyType.EMAIL
            PHONE -> KeyType.PHONE
        }
    }

    abstract fun valida(key: String?): Boolean
}

enum class AccountTypeDto {
    CONTA_CORRENTE, CONTA_POUPANCA;

    fun converte(): AccountType {
        return when(this){
            CONTA_CORRENTE -> AccountType.CONTA_CORRENTE
            CONTA_POUPANCA -> AccountType.CONTA_POUPANCA
        }
    }
}