package com.example.elielsonms.myapplication;

import java.io.Serializable;

/**
 * Created by elielsonms on 24/09/2017.
 */

public class HorarioDTO implements Serializable{

    private Long idHorario;
    private String horario;
    private String medico;

    public Long getIdHorario() {
        return idHorario;
    }

    public void setIdHorario(Long idHorario) {
        this.idHorario = idHorario;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getMedico() {
        return medico;
    }

    public void setMedico(String medico) {
        this.medico = medico;
    }
}
