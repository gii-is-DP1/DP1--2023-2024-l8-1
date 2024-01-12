package org.springframework.samples.petclinic.sector;

import java.util.List;

import org.springframework.samples.petclinic.hex.Hex;
import org.springframework.samples.petclinic.model.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "sectors")
public class Sector extends BaseEntity{

    @NotNull
    private Boolean choosen = false;
    
    
    @Min(0)
    @Max(7)
    private Integer position;

    @NotNull
    private Boolean isTriPrime = false;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @Size(max = 7)
    private List<Hex> hexs;    
}
