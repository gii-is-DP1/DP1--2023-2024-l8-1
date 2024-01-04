package org.springframework.samples.petclinic.hex;

import java.util.List;

import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.ship.Ship;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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

    @Min(0)
    @Max(3)
    @NotNull
    Integer puntos;

    @NotNull
    Boolean occuped = false;

    @Min(0)
    @Max(42)
    Integer position;

    @ManyToMany
    @JsonIgnore
    @JoinTable(
        name = "hexs_adyacentes",
        joinColumns = @JoinColumn(name = "hex_id"),
        inverseJoinColumns = @JoinColumn(name = "adyacente_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = { "hex_id", "adyacente_id" })
    )
    List<Hex> adyacentes;

    @OneToMany(mappedBy = "hex")
    List<Ship> ships;
    
}
