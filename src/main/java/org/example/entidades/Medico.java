package org.example.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "medicos")
@Getter
@SuperBuilder
@NoArgsConstructor
@ToString(exclude = {"departamento", "citas"}, callSuper = true)
public class Medico extends Persona implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Embedded
    private Matricula matricula;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EspecialidadMedica especialidad;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id")
    @Setter
    private Departamento departamento;


    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL, orphanRemoval = true)
    @lombok.Builder.Default
    private List<Cita> citas = new ArrayList<>();


    protected Medico(MedicoBuilder<?, ?> builder) {
        // Llamar constructor de Persona para inicializar campos heredados
        super(builder);
        // Crear matrícula validando formato (delegado a constructor de Matricula)
        this.matricula = new Matricula(builder.numeroMatricula);
        // Validar que especialidad no sea nula
        this.especialidad = Objects.requireNonNull(builder.especialidad, "La especialidad no puede ser nula");
        // CRÍTICO: Inicialización explícita requerida con @SuperBuilder
        this.citas = new ArrayList<>();
    }


    public static abstract class MedicoBuilder<C extends Medico, B extends MedicoBuilder<C, B>> extends PersonaBuilder<C, B> {
        private String numeroMatricula;
        private EspecialidadMedica especialidad;


        public B numeroMatricula(String numeroMatricula) {
            this.numeroMatricula = numeroMatricula;
            return self();
        }


        public B especialidad(EspecialidadMedica especialidad) {
            this.especialidad = especialidad;
            return self();
        }
    }


    public void setDepartamento(Departamento departamento) {
        if (this.departamento != departamento) {
            this.departamento = departamento;
        }
    }


    public void addCita(Cita cita) {
        this.citas.add(cita);
    }


    public List<Cita> getCitas() {
        return Collections.unmodifiableList(new ArrayList<>(citas));
    }

}

