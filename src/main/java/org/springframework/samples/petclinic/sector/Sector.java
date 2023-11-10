package org.springframework.samples.petclinic.sector;

import org.springframework.samples.petclinic.model.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Sector extends BaseEntity{

    @NotNull
    Boolean chosen;

    @NotEmpty
    @Min(0)
    @Max(8)
    Integer position;

    @NotNull
    Boolean isTriPrime;
    
}
