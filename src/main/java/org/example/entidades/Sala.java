package org.example.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "salas")
@Getter
@ToString(exclude = {"departamento", "citas"})
public class Sala implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true, length = 20)
    private String numero;


    @Column(nullable = false, length = 100)
    private String tipo;


    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "departamento_id", nullable = false)
    private final Departamento departamento;


    @OneToMany(mappedBy = "sala", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cita> citas = new ArrayList<>();


    private Sala(SalaBuilder builder) {
        this.citas = new ArrayList<>();
        this.numero = validarString(builder.numero, "El número de sala no puede ser nulo ni vacío");
        this.tipo = validarString(builder.tipo, "El tipo de sala no puede ser nulo ni vacío");
        this.departamento = Objects.requireNonNull(builder.departamento, "El departamento no puede ser nulo");
    }


    public static class SalaBuilder {
        private String numero;
        private String tipo;
        private Departamento departamento;


        public SalaBuilder numero(String numero) {
            this.numero = numero;
            return this;
        }


        public SalaBuilder tipo(String tipo) {
            this.tipo = tipo;
            return this;
        }


        public SalaBuilder departamento(Departamento departamento) {
            this.departamento = departamento;
            return this;
        }


        public Sala build() {
            return new Sala(this);
        }
    }


    public static SalaBuilder builder() {
        return new SalaBuilder();
    }


    public void addCita(Cita cita) {
        this.citas.add(cita);
    }


    public List<Cita> getCitas() {
        return Collections.unmodifiableList(new ArrayList<>(citas));
    }


    private String validarString(String valor, String mensajeError) {
        Objects.requireNonNull(valor, mensajeError);
        if (valor.trim().isEmpty()) {
            throw new IllegalArgumentException(mensajeError);
        }
        return valor;
    }

}
