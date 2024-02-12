package com.example.orders.controllers;

import com.example.orders.models.Order;
import com.example.orders.models.OrderModelAssembler;
import com.example.orders.models.Status;
import com.example.orders.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;



import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private OrderModelAssembler assembler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAllOrders() throws Exception {
                Order order1 = new Order("Order 1", Status.IN_PROGRESS);
        order1.setId(1L);

        Order order2 = new Order("Order 2", Status.COMPLETED);
        order2.setId(2L);

        List<Order> sampleOrders = Arrays.asList(order1, order2);

                when(orderRepository.findAll()).thenReturn(sampleOrders);

                List<EntityModel<Order>> entityModels = sampleOrders.stream()
                .map(order -> EntityModel.of(order,
                        linkTo(methodOn(OrderController.class).one(order.getId())).withSelfRel(),
                        linkTo(methodOn(OrderController.class).all()).withRel("orders")))
                .toList();

        when(assembler.toModel(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            return entityModels.stream()
                    .filter(model -> model.getContent().getId().equals(order.getId()))
                    .findFirst()
                    .orElse(null);
        });

                mockMvc.perform(get("/orders").accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$._embedded.orderList").isArray())
                .andExpect(jsonPath("$._embedded.orderList.length()").value(sampleOrders.size()))
                .andExpect(jsonPath("$._embedded.orderList[0].description").value("Order 1"))
                .andExpect(jsonPath("$._embedded.orderList[0].status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$._embedded.orderList[1].description").value("Order 2"))
                .andExpect(jsonPath("$._embedded.orderList[1].status").value("COMPLETED"));

                verify(orderRepository, times(1)).findAll();
    }

    @Test
    public void testGetOrderById() throws Exception {
                Order sampleOrder = new Order("Test Order", Status.IN_PROGRESS);
        sampleOrder.setId(1L);

                when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));

                when(assembler.toModel(sampleOrder)).thenReturn(EntityModel.of(sampleOrder,
                linkTo(methodOn(OrderController.class).one(1L)).withSelfRel(),
                linkTo(methodOn(OrderController.class).all()).withRel("orders")));

                mockMvc.perform(MockMvcRequestBuilders.get("/orders/1")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Test Order"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("IN_PROGRESS"));

                verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    public void testOrderNotFound() throws Exception {
        String errorMessage = "Could not find order 33";

        mockMvc.perform(MockMvcRequestBuilders.get("/orders/33")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(errorMessage));

    }

    @Test
    public void testCreateNewOrder() throws Exception {
                Order sampleOrder = new Order("New Order", Status.IN_PROGRESS);
        sampleOrder.setId(1L);

                when(orderRepository.save(any(Order.class))).thenReturn(sampleOrder);

                when(assembler.toModel(sampleOrder)).thenReturn(EntityModel.of(sampleOrder,
                linkTo(methodOn(OrderController.class).one(1L)).withSelfRel(),
                linkTo(methodOn(OrderController.class).all()).withRel("orders")));

                mockMvc.perform(MockMvcRequestBuilders.post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"New Order\",\"status\":\"IN_PROGRESS\"}")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("New Order"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("IN_PROGRESS"));

                verify(orderRepository, times(1)).save(any(Order.class));
    }


    @Test
    public void testCancelOrderInProgress() {
                Long orderId = 1L;
        Order orderInProgress = new Order("Order in progress", Status.IN_PROGRESS);
        orderInProgress.setId(orderId);         OrderRepository repository = mock(OrderRepository.class);
        when(repository.findById(orderId)).thenReturn(Optional.of(orderInProgress));

        OrderController controller = new OrderController(repository, assembler);

                ResponseEntity<?> response = controller.cancel(orderId);

                assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Status.CANCELLED, orderInProgress.getStatus());
    }

    @Test
    public void testCancelOrderNotInProgress() {
                Long orderId = 1L;
        Order orderNotInProgress = new Order("Order completed", Status.COMPLETED);
        orderNotInProgress.setId(orderId);         OrderRepository repository = mock(OrderRepository.class);
        when(repository.findById(orderId)).thenReturn(Optional.of(orderNotInProgress));

        OrderController controller = new OrderController(repository, assembler);

                ResponseEntity<?> response = controller.cancel(orderId);

                assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
            }

    @Test
    public void testCompleteOrderInProgress() {
                Long orderId = 1L;
        Order orderInProgress = new Order("Order in progress", Status.IN_PROGRESS);
        orderInProgress.setId(orderId);         OrderRepository repository = mock(OrderRepository.class);
        when(repository.findById(orderId)).thenReturn(Optional.of(orderInProgress));

        OrderController controller = new OrderController(repository, assembler);

                ResponseEntity<?> response = controller.complete(orderId);

                assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Status.COMPLETED, orderInProgress.getStatus());
    }

    @Test
    public void testCompleteOrderNotInProgress() {
                Long orderId = 1L;
        Order orderNotInProgress = new Order("Order completed", Status.COMPLETED);
        orderNotInProgress.setId(orderId);         OrderRepository repository = mock(OrderRepository.class);
        when(repository.findById(orderId)).thenReturn(Optional.of(orderNotInProgress));

        OrderController controller = new OrderController(repository, assembler);

                ResponseEntity<?> response = controller.complete(orderId);

                assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
            }
}
