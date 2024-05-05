package com.fastcampus.payment.common

import com.fastcampus.payment.repository.PIORepository
import com.fastcampus.payment.service.ProductService
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class Beans: ApplicationContextAware {

    companion object {
        lateinit var ctx: ApplicationContext
            private set

        fun <T: Any> getBean(byClass: KClass<T>, vararg arg: Any): T {
            return ctx.getBean(byClass.java, arg)
        }

        val beanProductInOrderRepository: PIORepository by lazy{ getBean(PIORepository::class) }
        val beanProductService: ProductService by lazy{ getBean(ProductService::class) }
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        ctx = applicationContext
    }
}