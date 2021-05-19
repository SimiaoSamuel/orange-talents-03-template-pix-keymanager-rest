package br.com.zup.edu.validator

import br.com.zup.edu.KeyType
import br.com.zup.edu.dto.KeyTypeDto
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullAndEmptySource
import org.junit.jupiter.params.provider.ValueSource

class KeyTypeTest {
    @ParameterizedTest
    @ValueSource(strings = ["487", "3741116505"])
    @NullAndEmptySource
    fun `deve retornar false se chave invalida referente ao cpf `(key: String?) {
        val cpf = KeyTypeDto.CPF
        with(cpf) {
            Assertions.assertFalse(this.valida(key))
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["37411165050", "11070524085"])
    fun `deve retornar true se chave for valida referente ao cpf `(key: String?) {
        val cpf = KeyTypeDto.CPF
        with(cpf) {
            Assertions.assertTrue(this.valida(key))
        }
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = ["samuel" ,"xpto@gmail", "xpto@gmail.combr", "xpto@gmailcombr"])
    fun `deve retornar false se chave for invalida referente ao email `(key: String?) {
        val email = KeyTypeDto.EMAIL
        with(email) {
            Assertions.assertFalse(this.valida(key))
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["xpto@gmail.com.br", "xpto@gmail.com", "xpto@gmail.com.eu", "xpto@gmail.com.us" ])
    fun `deve retornar true se chave for valida referente ao email `(key: String) {
        val email = KeyTypeDto.EMAIL
        with(email) {
            Assertions.assertTrue(this.valida(key))
        }
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = ["40028922", "4", "5511940028922"])
    fun `deve retornar false se chave for invalida referente ao phone `(key: String?) {
        val phone = KeyTypeDto.PHONE
        with(phone) {
            Assertions.assertFalse(this.valida(key))
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["+5511940028922", "+5511982218922"])
    fun `deve retornar true se chave for valida referente ao phone `(key: String) {
        val phone = KeyTypeDto.PHONE
        with(phone) {
            Assertions.assertTrue(this.valida(key))
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["bfibdsf", "dhhsoisd", "askjpdjsadpasdpa"])
    fun `deve retornar false se chave for invalida referente ao random `(key: String) {
        val random = KeyTypeDto.RANDOM
        with(random) {
            Assertions.assertFalse(this.valida(key))
        }
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = [" "])
    fun `deve retornar true se chave for valida referente ao random `(key: String?) {
        val random = KeyTypeDto.RANDOM
        with(random) {
            Assertions.assertTrue(this.valida(key))
        }
    }

    @Test
    fun `deve converter para Random`(){
        val random = KeyTypeDto.RANDOM
        with(random) {
            Assertions.assertEquals(KeyType.RANDOM ,this.converte())
        }
    }

    @Test
    fun `deve converter para CPF`(){
        val random = KeyTypeDto.CPF
        with(random) {
            Assertions.assertEquals(KeyType.CPF ,this.converte())
        }
    }

    @Test
    fun `deve converter para PHONE`(){
        val random = KeyTypeDto.PHONE
        with(random) {
            Assertions.assertEquals(KeyType.PHONE ,this.converte())
        }
    }

    @Test
    fun `deve converter para EMAIL`(){
        val random = KeyTypeDto.EMAIL
        with(random) {
            Assertions.assertEquals(KeyType.EMAIL ,this.converte())
        }
    }
}