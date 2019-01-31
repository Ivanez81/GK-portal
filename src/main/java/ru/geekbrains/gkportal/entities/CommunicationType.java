package ru.geekbrains.gkportal.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Entity(name = "communication_type")
@EqualsAndHashCode(callSuper = true)
public class CommunicationType extends AbstractEntity {

    @Column(name = "description")
    @NotNull(message = "Couldn't be empty!")
    private String description;

}
