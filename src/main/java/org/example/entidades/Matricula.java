package org.example.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@ToString
@NoArgsConstructor
public class Matricula implements Serializable {

    @Column(name = "numero_matricula", nullable = false, unique = true, length = 20)
    private String numero;


    public Matricula(String numero) {
        this.numero = validarMatricula(numero);
    }


    private String validarMatricula(String numero) {
        // Validar que no sea null
        Objects.requireNonNull(numero, "El número de matrícula no puede ser nulo");
        // Validar formato: MP- seguido de 4 a 6 dígitos
        if (!numero.matches("MP-\\d{4,6}")) {
            throw new IllegalArgumentException("Formato de matrícula inválido. Debe ser como MP-12345");
        }
        return numero;
    }

}

