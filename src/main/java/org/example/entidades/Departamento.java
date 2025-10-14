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
@Table(name = "departamentos")
@Getter
@ToString(exclude = {"hospital", "medicos", "salas"})
public class Departamento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, length = 150)
    private final String nombre;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private final EspecialidadMedica especialidad;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;


    @OneToMany(mappedBy = "departamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Medico> medicos = new ArrayList<>();


    @OneToMany(mappedBy = "departamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sala> salas = new ArrayList<>();


    private Departamento(DepartamentoBuilder builder) {
        // Inicializar colecciones vacías para evitar NullPointerException
        this.medicos = new ArrayList<>();
        this.salas = new ArrayList<>();

        // Validar y asignar campos obligatorios
        this.nombre = validarString(builder.nombre, "El nombre del departamento no puede ser nulo ni vacío");
        this.especialidad = Objects.requireNonNull(builder.especialidad, "La especialidad no puede ser nula");
    }


    public static class DepartamentoBuilder {
        private String nombre;
        private EspecialidadMedica especialidad;


        public DepartamentoBuilder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }


        public DepartamentoBuilder especialidad(EspecialidadMedica especialidad) {
            this.especialidad = especialidad;
            return this;
        }


        public Departamento build() {
            return new Departamento(this);
        }
    }


    public static DepartamentoBuilder builder() {
        return new DepartamentoBuilder();
    }


    public void setHospital(Hospital hospital) {
        if (this.hospital != hospital) {
            // Remover del hospital anterior si existe
            if (this.hospital != null) {
                this.hospital.getInternalDepartamentos().remove(this);
            }
            // Establecer nuevo hospital
            this.hospital = hospital;
            // Agregar al nuevo hospital si no es null
            if (hospital != null) {
                hospital.getInternalDepartamentos().add(this);
            }
        }
    }


    public void agregarMedico(Medico medico) {
        Objects.requireNonNull(medico, "El médico no puede ser nulo");
        if (!medico.getEspecialidad().equals(this.especialidad)) {
            throw new IllegalArgumentException("Especialidad incompatible");
        }
        if (!medicos.contains(medico)) {
            medicos.add(medico);
            medico.setDepartamento(this);
        }
    }



    public Sala crearSala(String numero, String tipo) {
        // Crear sala usando builder con este departamento
        Sala sala = Sala.builder()
                .numero(numero)
                .tipo(tipo)
                .departamento(this)
                .build();
        // Agregar a la colección interna
        salas.add(sala);
        return sala;
    }


    public List<Medico> getMedicos() {
        return Collections.unmodifiableList(medicos);
    }


    public List<Sala> getSalas() {
        return Collections.unmodifiableList(salas);
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
