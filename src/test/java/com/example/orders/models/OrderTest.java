package com.example.orders.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class OrderTest {

    @Test
    public void testOrderCreation() {
        // Create a new Order object
        Order order = new Order("Test Order", Status.IN_PROGRESS);

        // Check that the Order object was created correctly
        assertEquals("Test Order", order.getDescription());
        assertEquals(Status.IN_PROGRESS, order.getStatus());
        assertNull(order.getId()); // ID should be null until saved to a database
    }

    @Test
    public void testOrderSetterMethods() {
        // Create an Order object
        Order order = new Order();

        // Use setter methods to set the values
        order.setDescription("Updated Description");
        order.setStatus(Status.COMPLETED);

        // Check that the setter methods updated the values correctly
        assertEquals("Updated Description", order.getDescription());
        assertEquals(Status.COMPLETED, order.getStatus());
    }
}
