package org.example.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "pacientes")
@Getter
@SuperBuilder
@NoArgsConstructor
@ToString(exclude = {"historiaClinica", "hospital", "citas"}, callSuper = true)
public class Paciente extends Persona implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private HistoriaClinica historiaClinica;


    @Column(nullable = false, length = 20)
    private String telefono;


    @Column(nullable = false, length = 300)
    private String direccion;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;


    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    @lombok.Builder.Default
    private List<Cita> citas = new ArrayList<>();


    protected Paciente(PacienteBuilder<?, ?> builder) {
        // Llamar constructor de Persona para inicializar campos heredados
        super(builder);
        // Validar y asignar teléfono
        this.telefono = validarString(builder.telefono, "El teléfono no puede ser nulo ni vacío");
        // Validar y asignar dirección
        this.direccion = validarString(builder.direccion, "La dirección no puede ser nula ni vacía");
        // CRÍTICO: Inicialización explícita requerida con @SuperBuilder
        this.citas = new ArrayList<>();
        // CRÍTICO: Crear HistoriaClinica automáticamente para este paciente
        this.historiaClinica = HistoriaClinica.builder()
                .paciente(this)
                .build();
    }


    public static abstract class PacienteBuilder<C extends Paciente, B extends PacienteBuilder<C, B>> extends PersonaBuilder<C, B> {
        private String telefono;
        private String direccion;


        public B telefono(String telefono) {
            this.telefono = telefono;
            return self();
        }


        public B direccion(String direccion) {
            this.direccion = direccion;
            return self();
        }
    }


    public void setHospital(Hospital hospital) {
        if (this.hospital != hospital) {
            // Remover del hospital anterior si existe
            if (this.hospital != null) {
                this.hospital.getInternalPacientes().remove(this);
            }
            // Establecer nuevo hospital
            this.hospital = hospital;
            // Agregar al nuevo hospital si no es null
            if (hospital != null) {
                hospital.getInternalPacientes().add(this);
            }
        }
    }


    public void addCita(Cita cita) {
        this.citas.add(cita);
    }


    public List<Cita> getCitas() {
        return Collections.unmodifiableList(new ArrayList<>(citas));
    }


    private String validarString(String valor, String mensajeError) {
        // Validar que no sea null
        Objects.requireNonNull(valor, mensajeError);
        // Validar que no esté vacío (sin espacios)
        if (valor.trim().isEmpty()) {
            throw new IllegalArgumentException(mensajeError);
        }
        return valor;
    }

}
