package br.com.zup.edu.dto

import br.com.zup.edu.SearchKeyResponse
import br.com.zup.edu.shared.toLocalDateTime
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT
import java.time.LocalDateTime

data class DetailPixResponse(
    @JsonInclude(NON_DEFAULT)
    val id: Long?,
    val idCliente: String?,
    val pix: PixKeyDetail,
    val dataCriacao: LocalDateTime
)

data class PixKeyDetail(
    val tipoChave: KeyTypeDto,
    val chave: String,
    val titular: TitularResponse,
    val conta: ContaResponse
)

data class TitularResponse(
    val nome: String,
    val cpf: String,
)

data class ContaResponse(
    val instituicao: String,
    val agencia: String,
    val numeroConta: String,
    val tipoConta: AccountTypeDto
)

fun SearchKeyResponse.toDto(): DetailPixResponse{

    return DetailPixResponse(
        id = idPix,
        idCliente = idCliente,
        pix = PixKeyDetail(
            tipoChave = KeyTypeDto.valueOf(pix.tipoChave.name),
            chave = pix.chave,
            titular = TitularResponse(
                nome = pix.nome.nome,
                cpf = pix.nome.cpf
            ),
            conta = ContaResponse(
                instituicao = pix.conta.intituicao,
                agencia = pix.conta.agencia,
                numeroConta = pix.conta.numeroConta,
                tipoConta = AccountTypeDto.valueOf(pix.conta.tipoConta.name)
            )
        ),
        dataCriacao = dataCriacao.toLocalDateTime()
    )
}
