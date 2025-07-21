package br.com.imversaoavancada.projections

import br.com.imversaoavancada.entities.Account
import br.com.imversaoavancada.entities.Bank
import br.com.imversaoavancada.entities.History
import br.com.imversaoavancada.enums.AccountType
import io.quarkus.hibernate.orm.panache.common.NestedProjectedClass
import io.quarkus.hibernate.orm.panache.common.ProjectedFieldName
import io.quarkus.runtime.annotations.RegisterForReflection
import java.time.Instant

/**
 * @author Eduardo Folly
 */
@RegisterForReflection
class HistoryListProjection(
    @ProjectedFieldName("history.id")
    val id: Long?,
    @ProjectedFieldName("history.name")
    val name: String?,
    val paymentDate: Instant?,
    val amount: Int?,
    val account: NestedAccount?,
) {
    companion object {
        fun parse(history: History?): Map<String, Any?> =
            mapOf(
                "id" to history?.id?.toInt(),
                "name" to history?.name,
                "paymentDate" to history?.paymentDate?.toString(),
                "amount" to history?.amount,
                "account" to NestedAccount.parse(history?.account),
            )
    }

    @NestedProjectedClass
    class NestedAccount(
        val name: String?,
        val type: AccountType?,
        val bank: NestedBank?,
        val branch: String?,
        val number: String?,
        val creditLimit: Int?,
    ) {
        companion object {
            fun parse(a: Account?): Map<String, Any?> =
                mapOf(
                    "name" to a?.name,
                    "type" to a?.type?.name,
                    "bank" to NestedBank.parse(a?.bank),
                    "branch" to a?.branch,
                    "number" to a?.number,
                    "creditLimit" to a?.creditLimit,
                )
        }

        @NestedProjectedClass
        class NestedBank(
            val code: String?,
            val name: String?,
        ) {
            companion object {
                fun parse(bank: Bank?): Map<String, Any?> =
                    mapOf(
                        "code" to bank?.code,
                        "name" to bank?.name,
                    )
            }
        }
    }
}
