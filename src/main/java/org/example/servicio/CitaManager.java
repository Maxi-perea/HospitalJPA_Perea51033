package org.example.servicio;

import org.example.entidades.Cita;
import org.example.entidades.Medico;
import org.example.entidades.Paciente;
import org.example.entidades.Sala;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CitaManager implements CitaService {
    private final List<Cita> citas = new ArrayList<>();
    private final Map<Paciente, List<Cita>> citasPorPaciente = new ConcurrentHashMap<>();
    private final Map<Medico, List<Cita>> citasPorMedico = new ConcurrentHashMap<>();
    private final Map<Sala, List<Cita>> citasPorSala = new ConcurrentHashMap<>();

    @Override
    public Cita programarCita(Paciente paciente, Medico medico, Sala sala,
                              LocalDateTime fechaHora, BigDecimal costo) throws CitaException {
        validarCita(fechaHora, costo);

        if (!esMedicoDisponible(medico, fechaHora)) {
            throw new CitaException("El médico no está disponible en la fecha y hora solicitadas.");
        }

        if (!esSalaDisponible(sala, fechaHora)) {
            throw new CitaException("La sala no está disponible en la fecha y hora solicitadas.");
        }

        if (!medico.getEspecialidad().equals(sala.getDepartamento().getEspecialidad())) {
            throw new CitaException("La especialidad del médico no coincide con el departamento de la sala.");
        }

        Cita cita = Cita.builder()
                .paciente(paciente)
                .medico(medico)
                .sala(sala)
                .fechaHora(fechaHora)
                .costo(costo)
                .build();
        citas.add(cita);

        actualizarIndicePaciente(paciente, cita);
        actualizarIndiceMedico(medico, cita);
        actualizarIndiceSala(sala, cita);

        paciente.addCita(cita);
        medico.addCita(cita);
        sala.addCita(cita);

        return cita;
    }

    private void validarCita(LocalDateTime fechaHora, BigDecimal costo) throws CitaException {
        if (fechaHora.isBefore(LocalDateTime.now())) {
            throw new CitaException("No se puede programar una cita en el pasado.");
        }

        if (costo.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CitaException("El costo debe ser mayor que cero.");
        }
    }

    private boolean esMedicoDisponible(Medico medico, LocalDateTime fechaHora) {
        List<Cita> citasExistentes = citasPorMedico.get(medico);
        if (citasExistentes != null) {
            for (Cita citaExistente : citasExistentes) {
                if (Math.abs(citaExistente.getFechaHora().compareTo(fechaHora)) < 2) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean esSalaDisponible(Sala sala, LocalDateTime fechaHora) {
        List<Cita> citasExistentes = citasPorSala.get(sala);
        if (citasExistentes != null) {
            for (Cita citaExistente : citasExistentes) {
                if (Math.abs(citaExistente.getFechaHora().compareTo(fechaHora)) < 2) {
                    return false;
                }
            }
        }
        return true;
    }

    private void actualizarIndicePaciente(Paciente paciente, Cita cita) {
        List<Cita> citasPaciente = citasPorPaciente.get(paciente);
        if (citasPaciente == null) {
            citasPaciente = new ArrayList<>();
            citasPorPaciente.put(paciente, citasPaciente);
        }
        citasPaciente.add(cita);
    }

    private void actualizarIndiceMedico(Medico medico, Cita cita) {
        List<Cita> citasMedico = citasPorMedico.get(medico);
        if (citasMedico == null) {
            citasMedico = new ArrayList<>();
            citasPorMedico.put(medico, citasMedico);
        }
        citasMedico.add(cita);
    }

    private void actualizarIndiceSala(Sala sala, Cita cita) {
        List<Cita> citasSala = citasPorSala.get(sala);
        if (citasSala == null) {
            citasSala = new ArrayList<>();
            citasPorSala.put(sala, citasSala);
        }
        citasSala.add(cita);
    }

    @Override
    public List<Cita> getCitasPorPaciente(Paciente paciente) {
        List<Cita> citasPaciente = citasPorPaciente.get(paciente);
        if (citasPaciente != null) {
            return Collections.unmodifiableList(citasPaciente);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Cita> getCitasPorMedico(Medico medico) {
        List<Cita> citasMedico = citasPorMedico.get(medico);
        if (citasMedico != null) {
            return Collections.unmodifiableList(citasMedico);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Cita> getCitasPorSala(Sala sala) {
        List<Cita> citasSala = citasPorSala.get(sala);
        if (citasSala != null) {
            return Collections.unmodifiableList(citasSala);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void guardarCitas(String filename) throws IOException {

    }

    @Override
    public void cargarCitas(String filename, Map<String, Paciente> pacientes,
                            Map<String, Medico> medicos, Map<String, Sala> salas)
            throws IOException, ClassNotFoundException, CitaException {

        }
    }

