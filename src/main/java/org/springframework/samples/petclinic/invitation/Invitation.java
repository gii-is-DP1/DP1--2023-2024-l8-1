package org.springframework.samples.petclinic.invitation;

import org.springframework.samples.petclinic.model.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Invitation extends BaseEntity{

    @NotNull
    Boolean isAccepted;
    
    @NotEmpty
    InvitationType discriminator;

}
