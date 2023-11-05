package org.springframework.samples.petclinic.hex;

import org.springframework.samples.petclinic.model.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "hexs")
public class Hex extends BaseEntity {

    private Integer value;

    private Boolean occupied;
    
}
