package org.springframework.samples.petclinic.sector;

import java.util.List;

import org.springframework.samples.petclinic.hex.Hex;
import org.springframework.samples.petclinic.model.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "sectors")
public class Sector extends BaseEntity{

    private Boolean choosen;
    
    private Integer position;

    private Boolean isTriPrime;

    @OneToMany
    private List<Hex> hexs;
    
}
