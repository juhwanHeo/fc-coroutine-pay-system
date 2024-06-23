package com.fastcampus.payment.advanced.service

import com.fastcampus.payment.advanced.config.extension.query
import com.fastcampus.payment.advanced.config.extension.toLocalDate
import com.fastcampus.payment.advanced.controller.order.dto.QryOrderHistory
import com.fastcampus.payment.advanced.model.Order
import com.fastcampus.payment.advanced.model.PgStatus
import com.fastcampus.payment.advanced.model.PgStatus.CAPTURE_REQUEST
import com.fastcampus.payment.advanced.model.PgStatus.CAPTURE_RETRY
import com.fastcampus.payment.advanced.model.PgStatus.CAPTURE_SUCCESS
import kotlinx.coroutines.flow.toList
import mu.KotlinLogging
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Service
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

@Service
class OrderHistoryService(
    private val dbClient: DatabaseClient
) {

    suspend fun getHistories(request: QryOrderHistory): List<Order> {
        val param = HashMap<String, Any>().apply {
            put("userId", request.userId)
            put("pgStatus", listOf(CAPTURE_REQUEST, CAPTURE_RETRY, CAPTURE_SUCCESS).map { it.name })
            put("limit", request.limit)
            put("offset", (request.page - 1) * request.limit)
        }

        var sql = dbClient.sql(
            """
            SELECT  id, 
                    user_id, 
                    description, 
                    amount,
                    pg_order_id,
                    pg_key,
                    pg_status,
                    pg_retry_count,
                    created_at,
                    updated_at
            FROM    TB_ORDER
            WHERE   user_id = :userId
            AND     pg_status IN (:pgStatus)
            
            ${
                request.keyword.query {
                    val keywords = it.trim().split(" ")
                    if (keywords.isEmpty()) "" else {
                        val lines = ArrayList<String>()
                        repeat(keywords.size) { i ->
                            val key = "keyword_$i"
                            param[key] = keywords[i].let { "%$it%" }
                            lines.add("description LIKE :keyword_$i")
                        }
                        "AND ( ${lines.joinToString(" AND ")} )"
                    }
                }
            }
                ${
                request.fromDate.query {
                    param["fromDate"] = it.toLocalDate()
                    "AND   created_at >= :fromDate"
                }
            }
                ${
                request.toDate.query {
                    param["toDate"] = it.toLocalDate().plusDays(1)
                    "AND   created_at < :toDate"
                }
            }
                ${
                request.fromAmount.query {
                    param["fromAmount"] = it
                    "AND  amount >= :fromAmount"
                }
            }
                ${
                request.toAmount.query {
                    param["toAmount"] = it
                    "AND  amount <= :toAmount"
                }
            }
            ORDER BY created_at DESC
            LIMIT :limit
            OFFSET :offset
        """.trimIndent())

        param.forEach { (key, value) -> sql = sql.bind(key, value) }
        return sql.map { row ->
            Order(
                id = row.get("id") as Long,
                userId = row.get("user_id") as Long,
                description = row.get("description") as? String,
                amount = row.get("amount") as Long,
                pgStatus = (row.get("pg_status") as String).let { PgStatus.valueOf(it) },
            ).apply {
                createdAt = row.get("created_at") as? LocalDateTime
                updatedAt = row.get("updated_at") as? LocalDateTime
            }
        }
        .flow()
        .toList()
    }
}
