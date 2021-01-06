package com.reporting.app.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "test_view", schema = "public")
public class TestEntity {
    @Id
    @Column(name = "id")
    private String ID;
    @Column(name = "CUSTOMER_NAME")
    private String customer_name;
    @Column(name = "CUSTOMER_SURNAME")
    private String customer_surname;
    @Column(name = "CUSTOMER_BIRTH_DATE")
    private String customer_birth_date;
    @Column(name = "CUSTOMER_IDENTY_NUMBER")
    private String customer_identy_number;
    @Column(name = "CUSTOMER_JOB")
    private String customer_job;
    @Column(name = "CUSTOMER_DESCRIPTION")
    private String customer_description;
}
