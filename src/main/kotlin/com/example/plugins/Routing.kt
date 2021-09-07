package com.example.plugins

import com.example.Customer
import com.example.customerStorage
import com.example.orderStorage
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Hello Dunia!")
        }
        customerRouting()
        getOrderRoute()
        totalizeOrderRoute()
    }
}

fun Route.customerRouting() {
    route("/customer") {
        get {
            if (customerStorage.isNotEmpty()){
                call.respond(customerStorage)
            }else {
                call.respondText("No customer found", status = HttpStatusCode.NotFound)
            }
        }
        get("{id}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )
            val customer = customerStorage.find { it.id == id } ?: return@get call.respondText(
                "No customer with id $id",
                status = HttpStatusCode.NotFound
            )
            call.respond(customer)
        }
        post {
            val customer = call.receive<Customer>()
            customerStorage.add(customer)
            call.respondText("Customer stored correctly", status = HttpStatusCode.Created)
        }
        delete("{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (customerStorage.removeIf { it.id == id }){
                call.respondText("Customer removed correctly", status = HttpStatusCode.Accepted)
            }else call.respondText(
                "Not Found", status = HttpStatusCode.NotFound
            )
        }
    }
}

fun Route.getOrderRoute(){
    get("/order/{id}"){
        val id = call.parameters["id"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
        val order = orderStorage.find { it.number == id } ?: return@get call.respondText(
            "Not Found",
            status = HttpStatusCode.NotFound
        )
        call.respond(order)
    }
}

fun Route.totalizeOrderRoute(){
    get("/order/{id}/total"){
        val id = call.parameters["id"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
        val order = orderStorage.find { it.number == id } ?: return@get call.respondText(
            "Not Found",
            status = HttpStatusCode.NotFound
        )
        val total = order.contents.map { it.price * it.amount }.sum()
        call.respond(total)
    }
}
