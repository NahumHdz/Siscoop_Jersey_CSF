/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fenoreste.rest.Util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 *
 * @author wilmer
 */
public class UtilDate {
    public void utilDate(){
         LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();
        
        LocalDateTime fecha = LocalDateTime.of(hoy, ahora);
        
        System.out.println(fecha);
    }
}
