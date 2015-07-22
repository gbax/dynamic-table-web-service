package ru.gbax.restest.entity;

import javax.persistence.*;

/**
 * Заказы
 * Created by GBAX on 22.07.2015.
 */
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "order_number")
    private String orderNumber;

    @Column
    private Integer cost;

    public Order() {}

    public Order(String orderNumber, Integer cost) {
        this.orderNumber = orderNumber;
        this.cost = cost;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderNumber='" + orderNumber + '\'' +
                ", cost=" + cost +
                '}';
    }
}
