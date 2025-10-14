package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import org.example.entidades.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("===== SISTEMA DE GESTIÓN HOSPITALARIA CON JPA =====\n");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hospital-persistence-unit");
        EntityManager em = emf.createEntityManager();

        try {
            // 1. Inicializar y persistir hospital
            inicializarYPersistirDatos(em);

            // 2. Consultar y mostrar datos
            consultarYMostrarDatos(em);

            // 3. Actualizar estado de citas
            actualizarEstadoCitas(em);

            // 4. Mostrar estadísticas
            mostrarEstadisticas(em);

            System.out.println("\n===== SISTEMA EJECUTADO EXITOSAMENTE =====");

        } catch (Exception e) {
            System.err.println("Error en el sistema: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }

    private static void inicializarYPersistirDatos(EntityManager em) {
        System.out.println("Inicializando y persistiendo datos del hospital...\n");

        em.getTransaction().begin();

        try {
            // Crear hospital
            Hospital hospital = Hospital.builder()
                    .nombre("Hospital Central")
                    .direccion("Av. Libertador 1234")
                    .telefono("011-4567-8901")
                    .build();

            // Crear departamentos
            Departamento cardiologia = Departamento.builder()
                    .nombre("Cardiología")
                    .especialidad(EspecialidadMedica.CARDIOLOGIA)
                    .build();

            Departamento pediatria = Departamento.builder()
                    .nombre("Pediatría")
                    .especialidad(EspecialidadMedica.PEDIATRIA)
                    .build();

            Departamento traumatologia = Departamento.builder()
                    .nombre("Traumatología")
                    .especialidad(EspecialidadMedica.TRAUMATOLOGIA)
                    .build();

            // Asignar departamentos al hospital
            hospital.agregarDepartamento(cardiologia);
            hospital.agregarDepartamento(pediatria);
            hospital.agregarDepartamento(traumatologia);

            // Crear salas
            Sala salaCard101 = Sala.builder()
                    .numero("CARD-101")
                    .tipo("Consultorio")
                    .departamento(cardiologia)
                    .build();

            Sala salaCard102 = Sala.builder()
                    .numero("CARD-102")
                    .tipo("Quirófano")
                    .departamento(cardiologia)
                    .build();

            Sala salaPed201 = Sala.builder()
                    .numero("PED-201")
                    .tipo("Consultorio")
                    .departamento(pediatria)
                    .build();

            Sala salaTrauma301 = Sala.builder()
                    .numero("TRAUMA-301")
                    .tipo("Emergencias")
                    .departamento(traumatologia)
                    .build();

            // Crear médicos
            Medico cardiologo = Medico.builder()
                    .nombre("Carlos")
                    .apellido("González")
                    .dni("12345678")
                    .fechaNacimiento(LocalDate.of(1975, 5, 15))
                    .tipoSangre(TipoSangre.A_POSITIVO)
                    .numeroMatricula("MP-12345")
                    .especialidad(EspecialidadMedica.CARDIOLOGIA)
                    .build();

            Medico pediatra = Medico.builder()
                    .nombre("Ana")
                    .apellido("Martínez")
                    .dni("23456789")
                    .fechaNacimiento(LocalDate.of(1980, 8, 22))
                    .tipoSangre(TipoSangre.O_NEGATIVO)
                    .numeroMatricula("MP-23456")
                    .especialidad(EspecialidadMedica.PEDIATRIA)
                    .build();

            Medico traumatologo = Medico.builder()
                    .nombre("Luis")
                    .apellido("Rodríguez")
                    .dni("34567890")
                    .fechaNacimiento(LocalDate.of(1978, 3, 10))
                    .tipoSangre(TipoSangre.B_POSITIVO)
                    .numeroMatricula("MP-34567")
                    .especialidad(EspecialidadMedica.TRAUMATOLOGIA)
                    .build();

            // Asignar médicos a departamentos
            cardiologia.agregarMedico(cardiologo);
            pediatria.agregarMedico(pediatra);
            traumatologia.agregarMedico(traumatologo);

            // Crear pacientes
            Paciente pacienteCardiaco = Paciente.builder()
                    .nombre("María")
                    .apellido("López")
                    .dni("11111111")
                    .fechaNacimiento(LocalDate.of(1985, 12, 5))
                    .tipoSangre(TipoSangre.A_POSITIVO)
                    .telefono("011-1111-1111")
                    .direccion("Calle Falsa 123")
                    .build();

            Paciente pacientePediatrico = Paciente.builder()
                    .nombre("Pedro")
                    .apellido("García")
                    .dni("22222222")
                    .fechaNacimiento(LocalDate.of(2010, 6, 15))
                    .tipoSangre(TipoSangre.O_POSITIVO)
                    .telefono("011-2222-2222")
                    .direccion("Av. Siempreviva 456")
                    .build();

            Paciente pacienteTraumatologico = Paciente.builder()
                    .nombre("Elena")
                    .apellido("Fernández")
                    .dni("33333333")
                    .fechaNacimiento(LocalDate.of(1992, 9, 28))
                    .tipoSangre(TipoSangre.AB_NEGATIVO)
                    .telefono("011-3333-3333")
                    .direccion("Belgrano 789")
                    .build();

            // Asignar pacientes al hospital
            hospital.agregarPaciente(pacienteCardiaco);
            hospital.agregarPaciente(pacientePediatrico);
            hospital.agregarPaciente(pacienteTraumatologico);

            // Usar las historias clínicas auto-generadas (creadas en el constructor de Paciente)
            HistoriaClinica historiaCardiaca = pacienteCardiaco.getHistoriaClinica();
            historiaCardiaca.agregarDiagnostico("Hipertensión arterial");
            historiaCardiaca.agregarTratamiento("Enalapril 10mg");
            historiaCardiaca.agregarAlergia("Penicilina");

            HistoriaClinica historiaPediatrica = pacientePediatrico.getHistoriaClinica();
            historiaPediatrica.agregarDiagnostico("Control pediátrico rutinario");
            historiaPediatrica.agregarTratamiento("Vacunas al día");

            HistoriaClinica historiaTraumatologica = pacienteTraumatologico.getHistoriaClinica();
            historiaTraumatologica.agregarDiagnostico("Fractura de muñeca");
            historiaTraumatologica.agregarTratamiento("Inmovilización y fisioterapia");
            historiaTraumatologica.agregarAlergia("Ibuprofeno");

            // Crear citas
            LocalDateTime fechaBase = LocalDateTime.now().plusDays(1);

            Cita citaCardiologica = Cita.builder()
                    .paciente(pacienteCardiaco)
                    .medico(cardiologo)
                    .sala(salaCard101)
                    .fechaHora(fechaBase.withHour(10).withMinute(0))
                    .costo(new BigDecimal("150000.00"))
                    .estado(EstadoCita.PROGRAMADA)
                    .observaciones("Paciente con antecedentes de hipertensión")
                    .build();

            Cita citaPediatrica = Cita.builder()
                    .paciente(pacientePediatrico)
                    .medico(pediatra)
                    .sala(salaPed201)
                    .fechaHora(fechaBase.plusDays(1).withHour(14).withMinute(30))
                    .costo(new BigDecimal("80000.00"))
                    .estado(EstadoCita.PROGRAMADA)
                    .observaciones("Control de rutina - vacunas")
                    .build();

            Cita citaTraumatologica = Cita.builder()
                    .paciente(pacienteTraumatologico)
                    .medico(traumatologo)
                    .sala(salaTrauma301)
                    .fechaHora(fechaBase.plusDays(2).withHour(9).withMinute(15))
                    .costo(new BigDecimal("120000.00"))
                    .estado(EstadoCita.PROGRAMADA)
                    .observaciones("Seguimiento post-fractura")
                    .build();

            pacienteCardiaco.addCita(citaCardiologica);
            pacientePediatrico.addCita(citaPediatrica);
            pacienteTraumatologico.addCita(citaTraumatologica);

            cardiologo.addCita(citaCardiologica);
            pediatra.addCita(citaPediatrica);
            traumatologo.addCita(citaTraumatologica);

            salaCard101.addCita(citaCardiologica);
            salaPed201.addCita(citaPediatrica);
            salaTrauma301.addCita(citaTraumatologica);

            // Persistir todas las entidades (cascade hará el resto)
            em.persist(hospital);
            em.persist(salaCard101);
            em.persist(salaCard102);
            em.persist(salaPed201);
            em.persist(salaTrauma301);
            em.persist(cardiologo);
            em.persist(pediatra);
            em.persist(traumatologo);
            em.persist(historiaCardiaca);
            em.persist(historiaPediatrica);
            em.persist(historiaTraumatologica);
            em.persist(citaCardiologica);
            em.persist(citaPediatrica);
            em.persist(citaTraumatologica);

            em.getTransaction().commit();

            System.out.println("✓ Datos inicializados y persistidos exitosamente\n");

        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    private static void consultarYMostrarDatos(EntityManager em) {
        System.out.println("===== CONSULTANDO DATOS PERSISTIDOS =====\n");

        // Consultar todos los hospitales
        TypedQuery<Hospital> queryHospitales = em.createQuery(
                "SELECT h FROM Hospital h", Hospital.class);
        List<Hospital> hospitales = queryHospitales.getResultList();

        for (Hospital hospital : hospitales) {
            System.out.println("Hospital: " + hospital.getNombre());
            System.out.println("Dirección: " + hospital.getDireccion());
            System.out.println("Teléfono: " + hospital.getTelefono());
            System.out.println("Departamentos: " + hospital.getDepartamentos().size());
            System.out.println("Pacientes: " + hospital.getPacientes().size());
            System.out.println();
        }

        // Consultar todos los médicos con sus especialidades
        TypedQuery<Medico> queryMedicos = em.createQuery(
                "SELECT m FROM Medico m", Medico.class);
        List<Medico> medicos = queryMedicos.getResultList();

        System.out.println("===== MÉDICOS REGISTRADOS =====");
        for (Medico medico : medicos) {
            System.out.println("Dr. " + medico.getNombreCompleto() +
                    " - " + medico.getEspecialidad().getDescripcion() +
                    " | Matrícula: " + medico.getMatricula().getNumero() +
                    " | DNI: " + medico.getDni());
        }
        System.out.println();

        // Consultar pacientes
        TypedQuery<Paciente> queryPacientes = em.createQuery(
                "SELECT p FROM Paciente p", Paciente.class);
        List<Paciente> pacientes = queryPacientes.getResultList();

        System.out.println("===== PACIENTES REGISTRADOS =====");
        for (Paciente paciente : pacientes) {
            System.out.println(paciente.getNombreCompleto() +
                    " | DNI: " + paciente.getDni() +
                    " | Edad: " + paciente.getEdad() + " años" +
                    " | Tipo sangre: " + paciente.getTipoSangre().getDescripcion());
        }
        System.out.println();

        // Consultar citas programadas
        TypedQuery<Cita> queryCitas = em.createQuery(
                "SELECT c FROM Cita c ORDER BY c.fechaHora", Cita.class);
        List<Cita> citas = queryCitas.getResultList();

        System.out.println("===== CITAS PROGRAMADAS =====");
        for (Cita cita : citas) {
            System.out.println("Fecha: " + cita.getFechaHora());
            System.out.println("  Paciente: " + cita.getPaciente().getNombreCompleto());
            System.out.println("  Médico: Dr. " + cita.getMedico().getNombreCompleto());
            System.out.println("  Sala: " + cita.getSala().getNumero());
            System.out.println("  Costo: $" + cita.getCosto());
            System.out.println("  Estado: " + cita.getEstado());
            System.out.println("  Observaciones: " + cita.getObservaciones());
            System.out.println();
        }
    }

    private static void actualizarEstadoCitas(EntityManager em) {
        System.out.println("===== ACTUALIZANDO ESTADOS DE CITAS =====\n");

        em.getTransaction().begin();

        try {
            // Buscar la primera cita y cambiar su estado
            TypedQuery<Cita> query = em.createQuery(
                    "SELECT c FROM Cita c ORDER BY c.fechaHora", Cita.class);
            query.setMaxResults(1);
            List<Cita> citas = query.getResultList();

            if (!citas.isEmpty()) {
                Cita cita = citas.get(0);
                cita.setEstado(EstadoCita.COMPLETADA);
                em.merge(cita);
                System.out.println("✓ Cita actualizada a estado COMPLETADA");
                System.out.println("  Paciente: " + cita.getPaciente().getNombreCompleto());
            }

            em.getTransaction().commit();
            System.out.println();

        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    private static void mostrarEstadisticas(EntityManager em) {
        System.out.println("===== ESTADISTICAS DEL SISTEMA =====\n");

        // Contar médicos por especialidad
        System.out.println("Médicos por especialidad:");
        for (EspecialidadMedica especialidad : EspecialidadMedica.values()) {
            Long count = em.createQuery(
                            "SELECT COUNT(m) FROM Medico m WHERE m.especialidad = :esp", Long.class)
                    .setParameter("esp", especialidad)
                    .getSingleResult();
            if (count > 0) {
                System.out.println("  " + especialidad.getDescripcion() + ": " + count);
            }
        }
        System.out.println();

        // Contar citas por estado
        System.out.println("Citas por estado:");
        for (EstadoCita estado : EstadoCita.values()) {
            Long count = em.createQuery(
                            "SELECT COUNT(c) FROM Cita c WHERE c.estado = :estado", Long.class)
                    .setParameter("estado", estado)
                    .getSingleResult();
            if (count > 0) {
                System.out.println("  " + estado + ": " + count);
            }
        }
        System.out.println();

        // Total de salas
        Long totalSalas = em.createQuery("SELECT COUNT(s) FROM Sala s", Long.class)
                .getSingleResult();
        System.out.println("Total de salas: " + totalSalas);

        // Total de pacientes
        Long totalPacientes = em.createQuery("SELECT COUNT(p) FROM Paciente p", Long.class)
                .getSingleResult();
        System.out.println("Total de pacientes: " + totalPacientes);

        System.out.println();
    }
}