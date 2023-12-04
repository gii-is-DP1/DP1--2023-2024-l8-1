package org.springframework.samples.petclinic.hex;

import org.springframework.samples.petclinic.model.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "hexs")
public class Hex extends BaseEntity{

    @NotEmpty
    @Min(0)
    @Max(3)
    Integer value;

    @NotNull
    Boolean occuped = false;
    
}
