package ru.gbax.restest.entity;

import javax.persistence.*;

/**
 * Заказчики
 * Created by GBAX on 22.07.2015.
 */
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String company;

    @Column
    private String serial;

    @Column
    private Integer countOrder;

    public Customer() {
    }

    public Customer(String firstName, String lastName, String company, String serial, Integer countOrder) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.company = company;
        this.serial = serial;
        this.countOrder = countOrder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public Integer getCountOrder() {
        return countOrder;
    }

    public void setCountOrder(Integer countOrder) {
        this.countOrder = countOrder;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", company='" + company + '\'' +
                ", serial='" + serial + '\'' +
                ", countOrder=" + countOrder +
                '}';
    }
}
