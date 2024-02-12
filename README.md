<h1>Orders Web Service</h1>
<p>This is an example RESTful web service using HATEOAS</p>
<hr>
<h3>How to run</h3>
<ol>
    <li>mvn install</li>
    <li>mvn spring-boot:run</li>
    <li><i>note: I've added a openAPI documents in the doc folder.</i></li>
</ol>

<hr>
<h3>Endpoint Paths</h3>

<i>Get all orders</i><br>
GET http://localhost:8080/orders

<i>Cancel an order</i><br>
DELETE http://localhost:8080/orders/{{id}}/cancel

<i>Complete an order</i><br>
PUT http://localhost:8080/orders/{{id}}/complete

<i>Get one order details</i><br>
GET http://localhost:8080/orders{{id}}

<i>Create a new order</i><br>
POST http://localhost:8080/orders

body:
<code>
{"description" : "Macbook"}
</code>