package br.com.zup.edu.dto

import br.com.zup.edu.ListKeyResponse
import br.com.zup.edu.shared.toLocalDateTime
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

data class ListaPixDtos(
    val idCliente: String,
    @JsonInclude(JsonInclude.Include.ALWAYS)
    val pixs: List<PixDto>
)

data class PixDto(
    val idPix: Long,
    val keyType: KeyTypeDto,
    val key: String,
    val accountType: AccountTypeDto,
    val criadoEm: LocalDateTime
)

fun ListKeyResponse.toDto(): ListaPixDtos {
    return ListaPixDtos(
        idCliente = idCliente,
        pixs = keyList.map {
            PixDto(
                idPix = it.idPix,
                keyType = KeyTypeDto.valueOf(it.tipoChave.name),
                key = it.chave,
                accountType = AccountTypeDto.valueOf(it.tipoConta.name),
                criadoEm = it.dataCriacao.toLocalDateTime()
            )
        }
    )
}