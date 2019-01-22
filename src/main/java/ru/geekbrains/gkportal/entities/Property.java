package ru.geekbrains.gkportal.entities;

import lombok.Data;

import javax.persistence.*;

@Entity(name = "properties")
@Data
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "property_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "value")
    private String value;

    @Column (name ="type")
    @Enumerated(EnumType.STRING)
    private PropertyType propertyType;

}
