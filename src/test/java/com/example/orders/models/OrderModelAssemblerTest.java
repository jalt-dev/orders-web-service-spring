package com.example.orders.models;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Links;

import static org.junit.jupiter.api.Assertions.*;

public class OrderModelAssemblerTest {

    @InjectMocks
    private OrderModelAssembler assembler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testToModelCompleted() {
        Order sampleOrder = new Order("Test Order", Status.COMPLETED);
        sampleOrder.setId(2L);

        EntityModel<Order> orderModel = assembler.toModel(sampleOrder);

        Links links = orderModel.getLinks();

        assertTrue(links.hasLink("self"));
        assertTrue(links.hasLink("orders"));

        assertFalse(links.hasLink("cancel"));
        assertFalse(links.hasLink("complete"));

        assertEquals(sampleOrder, orderModel.getContent());
    }



    @Test
    public void testModelInProgress() {
        Order sampleOrder = new Order("Test Order", Status.IN_PROGRESS);
        sampleOrder.setId(2L);

        EntityModel<Order> orderModel = assembler.toModel(sampleOrder);

        Links links = orderModel.getLinks();

        assertTrue(links.hasLink("self"));
        assertTrue(links.hasLink("orders"));

        assertTrue(links.hasLink("cancel"));
        assertTrue(links.hasLink("complete"));

        assertEquals(sampleOrder, orderModel.getContent());
    }

}
