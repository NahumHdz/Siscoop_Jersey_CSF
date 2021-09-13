/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fenoreste.rest.Dao;

import DTO.AccountHoldersDTO;
import DTO.validateMonetaryInstructionDTO;
import com.fenoreste.rest.Util.AbstractFacade;
import com.fenoreste.rest.Entidades.Auxiliares;
import com.fenoreste.rest.Entidades.tipos_cuenta_siscoop;
import com.fenoreste.rest.Entidades.transferencias_completadas_siscoop;
import com.fenoreste.rest.Entidades.validaciones_transferencias_siscoop;
import DTO.MonetaryInstructionDTO;
import DTO.OrderWsSPEI;
import DTO.ogsDTO;
import DTO.opaDTO;
import com.fenoreste.rest.Entidades.AuxiliaresPK;
import com.fenoreste.rest.Entidades.Persona;
import com.fenoreste.rest.Entidades.Productos;
import com.fenoreste.rest.Entidades.Tablas;
import com.fenoreste.rest.Entidades.TablasPK;
import com.fenoreste.rest.Util.Util_OGS_OPA;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

/**
 *
 * @author Elliot
 */
public abstract class FacadeInstructions<T> {

    private static EntityManagerFactory emf;
    
    Util_OGS_OPA Util = new Util_OGS_OPA();

    public FacadeInstructions(Class<T> entityClass) {
        emf = AbstractFacade.conexion();//Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);    
    }

    public List<MonetaryInstructionDTO> monetaryInistruction(String customerId, String fechaInicio, String fechaFinal) {
        EntityManager em = emf.createEntityManager();
        List<MonetaryInstructionDTO> listaMonetary = new ArrayList<>();

        try {
            String consulta = "SELECT * FROM e_transferenciassiscoop WHERE customerId='" + customerId + "' AND fechaejecucion BETWEEN '" + fechaInicio + "' AND '" + fechaFinal + "' AND UPPER(comentario1) LIKE '%PROGRAMA%'";
            System.out.println("consulta:" + consulta);
            Query query = em.createNativeQuery(consulta, transferencias_completadas_siscoop.class);

            List<transferencias_completadas_siscoop> ListaTransferencias = query.getResultList();//new ArrayList<transferencias_completadas_siscoop>();
            //ListaTransferencias = query.getResultList();

            for (int i = 0; i < ListaTransferencias.size(); i++) {
                MonetaryInstructionDTO dtoMonetary = new MonetaryInstructionDTO();
                dtoMonetary.setDebitAccount(ListaTransferencias.get(i).getCuentaorigen());
                dtoMonetary.setCreditAccount(ListaTransferencias.get(i).getCuentadestino());
                dtoMonetary.setExecutionDate(dateToString(ListaTransferencias.get(i).getFechaejecucion()));
                dtoMonetary.setMonto(ListaTransferencias.get(i).getMonto());
                int idproducto = Integer.parseInt(ListaTransferencias.get(i).getCuentaorigen().substring(6, 11));
                tipos_cuenta_siscoop tps = em.find(tipos_cuenta_siscoop.class, idproducto);
                dtoMonetary.setTypeNameId(tps.getProducttypename().toUpperCase());
                dtoMonetary.setOriginatorTransactionType(ListaTransferencias.get(i).getTipotransferencia());
                dtoMonetary.setMonetaryId(ListaTransferencias.get(i).getId());
                listaMonetary.add(dtoMonetary);
            }
            System.out.println("ListaMonetary:" + listaMonetary);
        } catch (Exception e) {
            em.close();
        }
        em.close();
        return listaMonetary;

    }

    public validateMonetaryInstructionDTO validateMonetaryInstruction(String customerId,
            String cuentaorigen,
            String cuentadestino,
            Double montoTransferencia,
            String comentario,
            String propcuentadestino,
            String fechaejecucion,
            String tipoEjecucion,
            String tipoTransferencia, int identificadorTransferencia) {

        String mensageDinamico = "";

        validateMonetaryInstructionDTO validateMonetary = new validateMonetaryInstructionDTO();
        EntityManager em = emf.createEntityManager();
        //String[] fees = new String[0];
        String validationId = "";

        //String fxxe = String.valueOf(queryf.getSingleResult());
        Date hoy = new Date();
        validationId = RandomAlfa().toUpperCase();
        //si es una pago de servicio Nomas la guarda porque no se esta habilitado el servicio pero si debe validar que exista la cuenta origen y que tenga saldo
        if (identificadorTransferencia == 1) {//Transferencia entre mis cuentas
            mensageDinamico = validarTransferenciaEntreMisCuentas(customerId, cuentaorigen, montoTransferencia, cuentadestino);
        } else if (identificadorTransferencia == 2) {//Transferencia a terceros dentro de la entidad
            mensageDinamico = validarTransferenciasATercerosDE(customerId, cuentaorigen, montoTransferencia, cuentadestino);
        } else if (identificadorTransferencia == 3) {//Pago a prestamos dentro de la entidad
            mensageDinamico = validarPagoPrestamo(customerId, cuentaorigen, montoTransferencia, cuentadestino);
        } else if (identificadorTransferencia == 4) {//pago de servicios
            mensageDinamico = validarPagoServicio(customerId, cuentaorigen, montoTransferencia);
        } else if (identificadorTransferencia == 5) {//transferencias SPEI
            //mensageDinamico=validarTransferenciaSPEI(orden);
        } else if (identificadorTransferencia == 6) {
            mensageDinamico = validarTransferenciaEntreMisCuentas(customerId, cuentaorigen, montoTransferencia, cuentadestino);
        } else if (identificadorTransferencia == 7) {
            mensageDinamico = validarTransferenciasATercerosDE(customerId, cuentaorigen, montoTransferencia, cuentadestino);
        } else if (identificadorTransferencia == 8) {
            mensageDinamico = validarPagoPrestamo(customerId, cuentaorigen, montoTransferencia, cuentadestino);
        }

        System.out.println("Mensaje de validacion:" + mensageDinamico);
        try {
            if (mensageDinamico.toUpperCase().contains("EXITO")) {
                validaciones_transferencias_siscoop validacionesTransferencias = new validaciones_transferencias_siscoop();
                validacionesTransferencias.setValidationId(validationId);
                validacionesTransferencias.setTipotransferencia(tipoTransferencia);
                validacionesTransferencias.setTipoejecucion(tipoEjecucion);
                validacionesTransferencias.setMonto(montoTransferencia);
                validacionesTransferencias.setFechaejecucion(hoy);
                validacionesTransferencias.setEstatus(true);
                validacionesTransferencias.setCustomerId(customerId);
                validacionesTransferencias.setCuentaorigen(cuentaorigen);
                validacionesTransferencias.setCuentadestino(cuentadestino);
                validacionesTransferencias.setComentario1(comentario);

                validacionesTransferencias.setComentario2(comentario);

                validateMonetary.setValidationId(validacionesTransferencias.getValidationId());
                validateMonetary.setExecutionDate(fechaejecucion);

                em.getTransaction().begin();
                em.persist(validacionesTransferencias);
                em.getTransaction().commit();

                //transacction.setTipotransferencia(tipoejecucion);
            } else {
                validateMonetary.setValidationId(mensageDinamico);
                String fee[] = new String[2];
                validateMonetary.setFees(fee);
                validateMonetary.setExecutionDate("");
            }
        } catch (Exception e) {
            em.close();
            System.out.println("Error al generar validacion:" + e.getMessage());
            validateMonetary.setValidationId(e.getMessage());
            return validateMonetary;
        } finally {
            em.close();
        }

        /*try {
            //Busca la cuenta y busca si tiene saldo
            if (findAccount(cuentaorigen, customerId) && findBalance(cuentaorigen, montoTransferencia)) {
                validationId = RandomAlfa().toUpperCase();
                //si es una pago de servicio Nomas la guarda porque no se esta habilitado el servicio pero si debe validar que exista la cuenta origen y que tenga saldo
                if (identificadorTransferencia == 1) {//Transferencia entre mis cuentas
                    mensageDinamico= validarTransferenciaEntreMisCuentas("", cuentaorigen, montoTransferencia, cuentadestino);
                } else if (identificadorTransferencia == 2) {//Trasnsferencia a terceros dentro de la entidad

                } else if (identificadorTransferencia == 3) {//Pago a prestamos dentro de la entidad

                } else if (identificadorTransferencia == 4) {//pago de servicios

                } else if (identificadorTransferencia == 5) {//transferencias SPEI

                }

                if (tipotransferencia.toUpperCase().contains("BILL_PAYMENT")) {
                    EntityTransaction tr = em.getTransaction();
                    tr.begin();
                    validaciones_transferencias_siscoop vl = new validaciones_transferencias_siscoop();
                    vl.setCuentaorigen(cuentaorigen);
                    vl.setCuentadestino(cuentadestino);
                    vl.setTipotransferencia(tipotransferencia);
                    vl.setComentario1(comentario);
                    vl.setComentario2(propcuentadestino);
                    vl.setCustomerId(customerId);
                    vl.setMonto(montoTransferencia);
                    vl.setFechaejecucion(hoy);
                    vl.setTipoejecucion(tipoejecucion);
                    vl.setEstatus(false);
                    vl.setValidationId(validationId);
                    em.persist(vl);
                    tr.commit();
                    bandera = true;
                } else if (tipotransferencia.toUpperCase().contains("DOMESTIC_PAYMENT")) {
                    //Es un pago SPEI

                } else {
                    //Si es una programada
                    if (!FechaTiempoReal.equals(fechaejecucion)) {
                        EntityTransaction tr = em.getTransaction();
                        tr.begin();
                        validaciones_transferencias_siscoop vl = new validaciones_transferencias_siscoop();
                        vl.setCuentaorigen(cuentaorigen);
                        vl.setCuentadestino(cuentadestino);
                        vl.setTipotransferencia(tipotransferencia);
                        vl.setComentario1("Programada no en uso");
                        vl.setComentario2(propcuentadestino);
                        vl.setCustomerId(customerId);
                        vl.setMonto(montoTransferencia);
                        vl.setFechaejecucion(stringToDate(fechaejecucion.replace("-", "/")));
                        vl.setTipoejecucion(tipoejecucion);
                        vl.setEstatus(false);
                        vl.setValidationId(validationId);
                        em.persist(vl);
                        tr.commit();
                        bandera = true;
                    } else {
                        //Tranferencias entre cuentas propias
                        if (tipotransferencia.toUpperCase().contains("TRANSFER_OWN")) {
                            if (validarTransferenciaEntreMisCuentas(customerId, cuentaorigen, montoTransferencia, cuentadestino)) {
                                EntityTransaction tr = em.getTransaction();
                                tr.begin();
                                validaciones_transferencias_siscoop vl = new validaciones_transferencias_siscoop();
                                vl.setCuentaorigen(cuentaorigen);
                                vl.setCuentadestino(cuentadestino);
                                vl.setTipotransferencia(tipotransferencia);
                                vl.setComentario1(comentario);
                                vl.setComentario2(propcuentadestino);
                                vl.setCustomerId(customerId);
                                vl.setFechaejecucion(hoy);
                                vl.setMonto(montoTransferencia);
                                vl.setTipoejecucion(tipoejecucion);
                                vl.setEstatus(false);
                                vl.setValidationId(validationId);
                                em.persist(vl);
                                tr.commit();
                                bandera = true;
                            }
                        } else if (tipotransferencia.toUpperCase().contains("INTRABANK")) {//Si la cuenta destino es un tercero ya solo la manda
                            EntityTransaction tr = em.getTransaction();
                            tr.begin();
                            validaciones_transferencias_siscoop vl = new validaciones_transferencias_siscoop();
                            vl.setCuentaorigen(cuentaorigen);
                            vl.setCuentadestino(cuentadestino);
                            vl.setTipotransferencia(tipotransferencia);
                            vl.setComentario1(comentario);
                            vl.setComentario2(propcuentadestino);
                            vl.setCustomerId(customerId);
                            vl.setFechaejecucion(hoy);
                            vl.setMonto(montoTransferencia);
                            vl.setTipoejecucion(tipoejecucion);
                            vl.setEstatus(false);
                            vl.setValidationId(validationId);
                            em.persist(vl);
                            tr.commit();
                            bandera = true;
                        }
                    }

                }
            }
            if (bandera) {
                dto = new validateMonetaryInstructionDTO(validationId, fees, fe);
            }
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
            em.close();
        } finally {
            em.close();
        }*/
        return validateMonetary;
    }

    public String executeMonetaryInstruction(String validationId) {
        EntityManager em = emf.createEntityManager();
        String mensaje = "";
        try {
            //Buscamos la validacion guardada no ejecutada con el id que se nos proporciona
            String consulta = "SELECT * FROM v_transferenciassiscoop WHERE validationid='" + validationId + "' ORDER BY fechaejecucion DESC LIMIT 1";
            Query query = em.createNativeQuery(consulta, validaciones_transferencias_siscoop.class);
            validaciones_transferencias_siscoop validacion_guardada = (validaciones_transferencias_siscoop) query.getSingleResult();
            opaDTO opa = Util.opa(validacion_guardada.getCuentaorigen());
            String running_balance = "SELECT saldo -" + validacion_guardada.getMonto() + " FROM auxiliares a where "
                    + " and a.idorigenp = " + opa.getIdorigenp() + " and a.idproducto = " + opa.getIdproducto() + " and a.idauxiliar = " + opa.getIdauxiliar();
            Query query1 = em.createNativeQuery(running_balance);
            Double saldo = Double.parseDouble(String.valueOf(query1.getSingleResult()));
            
            transferencias_completadas_siscoop ejecutar_transferencia = new transferencias_completadas_siscoop();
            ejecutar_transferencia.setCuentaorigen(validacion_guardada.getCuentaorigen());
            ejecutar_transferencia.setCuentadestino(validacion_guardada.getCuentadestino());
            ejecutar_transferencia.setTipotransferencia(validacion_guardada.getTipotransferencia());
            ejecutar_transferencia.setComentario1(validacion_guardada.getComentario1());
            ejecutar_transferencia.setComentario2(validacion_guardada.getComentario2());
            ejecutar_transferencia.setCustomerId(validacion_guardada.getCustomerId());
            ejecutar_transferencia.setFechaejecucion(validacion_guardada.getFechaejecucion());
            ejecutar_transferencia.setMonto(validacion_guardada.getMonto());
            ejecutar_transferencia.setTipoejecucion(validacion_guardada.getTipoejecucion());
            ejecutar_transferencia.setEstatus(true);
            ejecutar_transferencia.setRunningBalance(saldo);
            em.getTransaction().begin();
            em.persist(ejecutar_transferencia);
            mensaje = "completed";
            em.getTransaction().commit();
            } catch (Exception e) {
            System.out.println("Error en execute:" + e.getMessage());
            em.close();
            em.getTransaction().rollback();
            return "rejected";
        }
        return mensaje;
    }

    public transferencias_completadas_siscoop detailsMonetary(String validationId) {
        EntityManager em = emf.createEntityManager();
        transferencias_completadas_siscoop transferencia = null;
        try {
            String consulta = "SELECT * FROM e_transferenciassiscoop WHERE id='" + validationId + "'";
            Query query = em.createNativeQuery(consulta, transferencias_completadas_siscoop.class);
            transferencia = (transferencias_completadas_siscoop) query.getSingleResult();
        } catch (Exception e) {
            System.out.println("Error en buscar detalles una tranferencia programada:" + e.getMessage());
            em.close();
            return null;
        } finally {
            em.close();
        }
        return transferencia;
    }

    public List<AccountHoldersDTO> accountHolders(String accountId) {
        EntityManager em = emf.createEntityManager();
        List<AccountHoldersDTO> listaDTO = new ArrayList<AccountHoldersDTO>();
        opaDTO opa = Util.opa(accountId);
        try {
            String consulta = "SELECT p.nombre||' '||p.appaterno||' '||p.apmaterno as nombre FROM auxiliares a "
                    + " INNER JOIN personas p USING(idorigen,idgrupo,idsocio)"
                    + " WHERE a.idorigenp = " + opa.getIdorigenp() + " AND a.idproducto = " + opa.getIdproducto() + " AND a.idauxiliar = " + opa.getIdauxiliar();
            Query query = em.createNativeQuery(consulta);
            System.out.println("Consulta:" + consulta);
            String nombre = (String) query.getSingleResult();
            System.out.println("Nombre:" + nombre);
            AccountHoldersDTO dto = null;
            if (!nombre.equals("")) {
                dto = new AccountHoldersDTO(nombre, "SOW");
            }
            listaDTO.add(dto);
            System.out.println("ListaDTO:" + listaDTO);

        } catch (Exception e) {
            em.close();
            System.out.println("Error:" + e.getMessage());
        } finally {
            em.close();
        }
        return listaDTO;
    }

    /*=============================== Validaciones =======================================*/
    public Date stringToDate(String cadena) {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");
        Date fechaDate = null;

        try {
            fechaDate = formato.parse(cadena);
        } catch (Exception ex) {
            System.out.println("Error fecha:" + ex.getMessage());
        }
        System.out.println("fechaDate:" + fechaDate);
        return fechaDate;
    }

    //Validar Trasnferencia entre mis cuentas
    private String validarTransferenciaEntreMisCuentas(String socio, String opaOrigen, Double montoTransferencia, String opaDestino) {
        EntityManager em = emf.createEntityManager();
        opaDTO opa_orig = Util.opa(opaOrigen);
        opaDTO opa_dest = Util.opa(opaDestino);
        String cuentaOrigen = "SELECT * FROM auxiliares a "
                + " WHERE a.idorigenp = " + opa_orig.getIdorigenp() + " AND a.idproducto = " + opa_orig.getIdproducto() + " AND a.idauxiliar = " + opa_orig.getIdauxiliar();

        String cuentaDestino = "SELECT * FROM auxiliares a "
                + " WHERE a.idorigenp = " + opa_dest.getIdorigenp() + " AND a.idproducto = " + opa_dest.getIdproducto() + " AND a.idauxiliar = " + opa_dest.getIdauxiliar();
        String mensage = "";

        try {
            Auxiliares ctaOrigen = null;
            boolean bOrigen = false;

            try {
                Query query = em.createNativeQuery(cuentaOrigen, Auxiliares.class);
                ctaOrigen = (Auxiliares) query.getSingleResult();
                bOrigen = true;
            } catch (Exception e) {
                System.out.println("No Existe Cuenta Origen");
                mensage = "NO EXISTE CUENTA ORIGEN";
                bOrigen = false;
            }
            String ogsCtaOrigen = String.format("%06d", ctaOrigen.getIdorigen()) + String.format("%02d", ctaOrigen.getIdgrupo()) + String.format("%06d", ctaOrigen.getIdsocio());

            if (bOrigen) {
                Double saldo = Double.parseDouble(ctaOrigen.getSaldo().toString());
                if (ogsCtaOrigen.equals(socio)) {
                    Productos prOrigen = em.find(Productos.class, ctaOrigen.getAuxiliaresPK().getIdproducto());
                    //si el producto no es un prestamo              
                    if (prOrigen.getTipoproducto() == 0) {//Falta regla de negocio si se permiten transferencias desde todas las cuentas
                        //Verifico el estatus de la cuenta origen
                        if (ctaOrigen.getEstatus() == 2) {
                            //verifico que el saldo del producto origen es mayor o igual a lo que se intenta transferir
                            if (saldo >= montoTransferencia) {
                                Auxiliares ctaDestino = null;
                                boolean bDestino = false;
                                //Busco la cuenta destino

                                try {
                                    Query queryDestino = em.createNativeQuery(cuentaDestino, Auxiliares.class);
                                    ctaDestino = (Auxiliares) queryDestino.getSingleResult();
                                    bDestino = true;
                                } catch (Exception e) {
                                    System.out.println("Error al encontrar productoDestino:" + e.getMessage());
                                    bDestino = false;
                                }

                                if (bDestino) {
                                    //Busco el producto destino
                                    Productos productoDestino = em.find(Productos.class, ctaDestino.getAuxiliaresPK().getIdproducto());
                                    //Valido que la cuenta destino este activa
                                    if (ctaDestino.getEstatus() == 2) {
                                        //Valido que el producto destino no sea un prestamo
                                        if (productoDestino.getTipoproducto() == 0) {//validar regla que todos los productos puedan recibir tranferencias excepto prestamos
                                            //Valido que realmente el producto destino pertenezca al mismo socio(porque es entre mis cuentas 
                                            if (ctaOrigen.getIdorigen() == ctaDestino.getIdorigen() && ctaOrigen.getIdgrupo() == ctaDestino.getIdgrupo() && ctaOrigen.getIdsocio() == ctaDestino.getIdsocio()) {
                                                //valido el minimo y maximo permitido para una transferencia
                                                if (minMax(montoTransferencia).toUpperCase().contains("VALIDO")) {
                                                    if (MaxPordia(opaOrigen, montoTransferencia)) {
                                                        mensage = "validado con exito";
                                                    } else {
                                                        mensage = "MONTO TRASPASA EL PERMITIDO DIARIO";
                                                    }
                                                } else {
                                                    mensage = "EL MONTO QUE INTENTA TRANFERIR ES:" + minMax(montoTransferencia) + " AL PERMITIDO";
                                                }
                                            } else {
                                                mensage = "PRODUCTO DESTINO NO PERTENECE AL MISMO SOCIO";
                                            }
                                        } else {
                                            mensage = "PRODUCTO DESTINO NO ACEPTA SOBRECARGOS";
                                        }
                                    } else {
                                        mensage = "PRODUCTO DESTINO ESTA INACTIVA";
                                    }
                                } else {
                                    mensage = "NO SE ENCONTRO PRODUCTO DESTINO";
                                }

                            } else {
                                mensage = "FONDOS INSUFICIENTES PARA COMPLETAR LA TRANSACCION";
                            }
                        } else {
                            mensage = "PRODUCTO ORIGEN INACTIVO";
                        }
                    } else {
                        mensage = "PRODUCTO ORIGEN NO PERMITE SOBRECARGOS";
                    }
                } else {
                    mensage = "CUENTA ORIGEN NO PERTENECE AL SOCIO:" + socio;
                }
            } else {
                mensage = "CUENTA ORIGEN NO EXISTE";
            }

        } catch (Exception e) {
            System.out.println("Error al validar transferencia entre mis cuentas:" + e.getMessage());
            if (!mensage.equals("NO EXISTE CUENTA ORIGEN")) {
                mensage = e.getMessage();
            }
            em.close();
            return mensage;
        } finally {
            em.close();
        }

        return mensage.toUpperCase();
    }

    //Validar Transferencia a terceros dentro de la entidad
    private String validarTransferenciasATercerosDE(String socio, String opaOrigen, Double montoTransferencia, String opaDestino) {
        EntityManager em = emf.createEntityManager();
        opaDTO opa_orig = Util.opa(opaOrigen);
        opaDTO opa_dest = Util.opa(opaDestino);
        
        String cuentaOrigen = "SELECT * FROM auxiliares a "
                + " WHERE a.idorigenp = " + opa_orig.getIdorigenp() + " AND a.idproducto = " + opa_orig.getIdproducto() + " AND a.idauxiliar = " + opa_orig.getIdauxiliar();

        String cuentaDestino = "SELECT * FROM auxiliares a "
                + " WHERE a.idorigenp = " + opa_dest.getIdorigenp() + " AND a.idproducto = " + opa_dest.getIdproducto() + " AND a.idauxiliar = " + opa_dest.getIdauxiliar();

        String mensage = "";
        try {
            Auxiliares ctaOrigen = null;
            boolean bOrigen = false;

            try {
                Query query = em.createNativeQuery(cuentaOrigen, Auxiliares.class);
                ctaOrigen = (Auxiliares) query.getSingleResult();
                bOrigen = true;
            } catch (Exception e) {
                System.out.println("No Existe Cuenta Origen");
                mensage = "NO EXISTE CUENTA ORIGEN";
                bOrigen = false;
            }

            if (bOrigen) {
                Double saldo = Double.parseDouble(ctaOrigen.getSaldo().toString());

                String ogsCtaOrigen = String.format("%06d", ctaOrigen.getIdorigen()) + String.format("%02d", ctaOrigen.getIdgrupo()) + String.format("%06d", ctaOrigen.getIdsocio());
                if (ogsCtaOrigen.equals(socio)) {
                    Productos prOrigen = em.find(Productos.class, ctaOrigen.getAuxiliaresPK().getIdproducto());
                    //si el producto no es un prestamo
                    if (prOrigen.getTipoproducto() == 0) {//Falta regla de negocio si se permiten transferencias desde todas las cuentas
                        //Verifico el estatus de la cuenta origen
                        if (ctaOrigen.getEstatus() == 2) {
                            //verifico que el saldo del producto origen es mayor o igual a lo que se intenta transferir
                            if (saldo >= montoTransferencia) {
                                Auxiliares ctaDestino = null;
                                boolean bDestino = false;
                                //Busco la cuenta destino

                                try {
                                    Query queryDestino = em.createNativeQuery(cuentaDestino, Auxiliares.class);
                                    ctaDestino = (Auxiliares) queryDestino.getSingleResult();
                                    bDestino = true;
                                } catch (Exception e) {
                                    System.out.println("Error al encontrar productoDestino:" + e.getMessage());
                                    bDestino = false;
                                }

                                if (bDestino) {
                                    //Busco el producto destino
                                    Productos productoDestino = em.find(Productos.class, ctaDestino.getAuxiliaresPK().getIdproducto());
                                    //Valido que la cuenta destino este activa
                                    if (ctaDestino.getEstatus() == 2) {
                                        //Valido que el producto destino no sea un prestamo
                                        if (productoDestino.getTipoproducto() == 0) {//validar regla que todos los productos puedan recibir tranferencias excepto prestamos

                                            //valido el minimo y maximo permitido para una transferencia
                                            if (minMax(montoTransferencia).toUpperCase().contains("VALIDO")) {
                                                if (MaxPordia(opaOrigen, montoTransferencia)) {
                                                    mensage = "validado con exito";
                                                } else {
                                                    mensage = "MONTO TRASPASA EL PERMITIDO DIARIO";
                                                }
                                            } else {
                                                mensage = "EL MONTO QUE INTENTA TRANFERIR ES:" + minMax(montoTransferencia) + " AL PERMITIDO";
                                            }
                                        } else {
                                            mensage = "PRODUCTO DESTINO NO ACEPTA SOBRECARGOS";
                                        }
                                    } else {
                                        mensage = "PRODUCTO DESTINO ESTA INACTIVA";
                                    }
                                } else {
                                    mensage = "NO SE ENCONTRO PRODUCTO DESTINO";
                                }

                            } else {
                                mensage = "FONDOS INSUFICIENTES PARA COMPLETAR LA TRANSACCION";
                            }
                        } else {
                            mensage = "PRODUCTO ORIGEN INACTIVO";
                        }
                    } else {
                        mensage = "PRODUCTO ORIGEN NO PERMITE SOBRECARGOS";
                    }
                } else {
                    mensage = "CUENTA DESTINO NO PERTENECE AL SOCIO:" + socio;
                }
            } else {
                mensage = "CUENTA ORIGEN NO EXISTE";
            }

        } catch (Exception e) {
            em.close();
            System.out.println("Error al realizar transferencia a tercero:" + e.getMessage());
            if (!mensage.contains("CUENTA ORIGEN")) {
                mensage = e.getMessage();
            }
            return mensage;
        } finally {
            em.close();
        }

        return mensage.toUpperCase();
    }

    //Validar pago de prestamo propio
    private String validarPagoPrestamo(String socio, String opaOrigen, Double montoTransferencia, String opaDestino) {
        EntityManager em = emf.createEntityManager();
        opaDTO opa_orig = Util.opa(opaOrigen);
        opaDTO opa_dest = Util.opa(opaDestino);
        String cuentaOrigen = "SELECT * FROM auxiliares a "
                + " WHERE a.idorigenp = " + opa_orig.getIdorigenp() + " AND a.idproducto = " + opa_orig.getIdproducto() + " AND a.idauxiliar = " + opa_orig.getIdauxiliar();

        String cuentaDestino = "SELECT * FROM auxiliares a "
                + " WHERE a.idorigenp = " + opa_dest.getIdorigenp() + " AND a.idproducto = " + opa_dest.getIdproducto() + " AND a.idauxiliar = " + opa_dest.getIdauxiliar();
        
        String mensage = "";
        try {
            Auxiliares ctaOrigen = null;
            boolean bOrigen = false;
            try {
                Query query = em.createNativeQuery(cuentaOrigen, Auxiliares.class);
                ctaOrigen = (Auxiliares) query.getSingleResult();
                bOrigen = true;
            } catch (Exception e) {
                System.out.println("No Existe Cuenta Origen");
                mensage = " NO EXISTE LA CUENTA ORIGEN";
                bOrigen = false;
            }

            if (bOrigen) {
                Double saldo = Double.parseDouble(ctaOrigen.getSaldo().toString());
                String ogsCtaOrigen = String.format("%06d", ctaOrigen.getIdorigen()) + String.format("%02d", ctaOrigen.getIdgrupo()) + String.format("%06d", ctaOrigen.getIdsocio());
                if (ogsCtaOrigen.equals(socio)) {
                    Productos prOrigen = em.find(Productos.class, ctaOrigen.getAuxiliaresPK().getIdproducto());
                    //si el producto no es un prestamo
                    if (prOrigen.getTipoproducto() == 0) {//Falta regla de negocio si se permiten transferencias desde todas las cuentas
                        //Verifico el estatus de la cuenta origen
                        if (ctaOrigen.getEstatus() == 2) {
                            //verifico que el saldo del producto origen es mayor o igual a lo que se intenta transferir
                            if (saldo >= montoTransferencia) {
                                Auxiliares ctaDestino = null;
                                boolean bDestino = false;
                                //Busco la cuenta destino
                                try {
                                    Query queryDestino = em.createNativeQuery(cuentaDestino, Auxiliares.class);
                                    ctaDestino = (Auxiliares) queryDestino.getSingleResult();
                                    bDestino = true;
                                } catch (Exception e) {
                                    System.out.println("Error al encontrar productoDestino:" + e.getMessage());
                                    bDestino = false;
                                }
                                if (bDestino) {
                                    //Busco el producto destino
                                    Productos productoDestino = em.find(Productos.class, ctaDestino.getAuxiliaresPK().getIdproducto());
                                    //Valido que la cuenta destino este activa
                                    if (ctaDestino.getEstatus() == 2) {
                                        //Valido que el producto destino no sea un prestamo
                                        if (productoDestino.getTipoproducto() == 2) {//validar regla que todos los productos puedan recibir tranferencias excepto prestamos
                                            //Valido que realmente el producto destino pertenezca al mismo socio(porque es entre mis cuentas 
                                            if (ctaOrigen.getIdorigen() == ctaDestino.getIdorigen() && ctaOrigen.getIdgrupo() == ctaDestino.getIdgrupo() && ctaOrigen.getIdsocio() == ctaDestino.getIdsocio()) {
                                                //valido el minimo y maximo permitido para una transferencia
                                                if (minMax(montoTransferencia).toUpperCase().contains("VALIDO")) {
                                                    if (MaxPordia(opaOrigen, montoTransferencia)) {
                                                        mensage = "validado con exito";
                                                    } else {
                                                        mensage = "MONTO TRASPASA EL PERMITIDO DIARIO";
                                                    }
                                                } else {
                                                    mensage = "EL MONTO QUE INTENTA TRANFERIR ES:" + minMax(montoTransferencia) + " AL PERMITIDO";
                                                }
                                            } else {
                                                mensage = "PRODUCTO DESTINO NO PERTENECE AL MISMO SOCIO";
                                            }
                                        } else {
                                            mensage = "PRODUCTO DESTINO NO ES UN PRESTAMO";
                                        }
                                    } else {
                                        mensage = "PRODUCTO DESTINO ESTA INACTIVA";
                                    }
                                } else {
                                    mensage = "NO SE ENCONTRO PRODUCTO DESTINO";
                                }

                            } else {
                                mensage = "FONDOS INSUFICIENTES PARA COMPLETAR LA TRANSACCION";
                            }
                        } else {
                            mensage = "PRODUCTO ORIGEN INACTIVO";
                        }
                    } else {
                        mensage = "PRODUCTO ORIGEN NO PERMITE SOBRECARGOS";
                    }
                } else {
                    mensage = "CUENTA ORIGEN NO PERTENCE AL SOCIO:" + socio;
                }
            } else {
                mensage = "CUENTA ORIGEN NO EXISTE";
            }

        } catch (Exception e) {
            em.close();
            System.out.println("Error al realizar pago de prestamo:" + e.getMessage());
            if (!mensage.contains("CUENTA ORIGEN")) {
                mensage = e.getMessage();
            }
            return mensage;
        } finally {
            em.close();
        }

        return mensage.toUpperCase();
    }

    //Validar pago de servicio(solo se valida la cuenta origen)
    private String validarPagoServicio(String socio, String opaOrigen, Double TotalPagoServicio) {
        EntityManager em = emf.createEntityManager();
        opaDTO opa_orig = Util.opa(opaOrigen);
        String cuentaOrigen = "SELECT * FROM auxiliares a "
                + " WHERE a.idorigenp = " + opa_orig.getIdorigenp() + " AND a.idproducto = " + opa_orig.getIdproducto() + " AND a.idauxiliar = " + opa_orig.getIdauxiliar();

        String mensage = "";
        try {
            Auxiliares ctaOrigen = null;
            boolean bOrigen = false;
            try {
                Query query = em.createNativeQuery(cuentaOrigen, Auxiliares.class);
                ctaOrigen = (Auxiliares) query.getSingleResult();
                bOrigen = true;
            } catch (Exception e) {
                System.out.println("No Existe Cuenta Origen");
                mensage = "NO EXISTE CUENTA ORIGEN";
                bOrigen = false;
            }

            if (bOrigen) {
                Double saldo = Double.parseDouble(ctaOrigen.getSaldo().toString());
                String ogsCtaOrigen = String.format("%06d", ctaOrigen.getIdorigen()) + String.format("%02d", ctaOrigen.getIdgrupo()) + String.format("%06d", ctaOrigen.getIdsocio());
                if (ogsCtaOrigen.equals(socio)) {
                    Productos prOrigen = em.find(Productos.class, ctaOrigen.getAuxiliaresPK().getIdproducto());
                    //si el producto no es un prestamo
                    if (prOrigen.getTipoproducto() == 0) {//Falta regla de negocio si se permiten transferencias desde todas las cuentas
                        //Verifico el estatus de la cuenta origen
                        if (ctaOrigen.getEstatus() == 2) {
                            //verifico que el saldo del producto origen es mayor o igual a lo que se intenta transferir
                            if (saldo >= TotalPagoServicio) {
                                //valido el minimo y maximo permitido para una transferencia
                                if (minMax(TotalPagoServicio).toUpperCase().contains("VALIDO")) {
                                    if (MaxPordia(opaOrigen, TotalPagoServicio)) {
                                        mensage = "validado con exito";
                                    } else {
                                        mensage = "MONTO TRASPASA EL PERMITIDO DIARIO";
                                    }
                                } else {
                                    mensage = "EL MONTO QUE INTENTA TRANFERIR ES:" + minMax(TotalPagoServicio) + " AL PERMITIDO";
                                }
                            } else {
                                mensage = "FONDOS INSUFICIENTES PARA COMPLETAR LA TRANSACCION";
                            }
                        } else {
                            mensage = "PRODUCTO ORIGEN INACTIVO";
                        }
                    } else {
                        mensage = "PRODUCTO ORIGEN NO PERMITE SOBRECARGOS";
                    }
                } else {
                    mensage = "CUENTA ORIGEN NO PERTENCE AL SOCIO:" + socio;
                }
            } else {
                mensage = "CUENTA ORIGEN NO EXISTE";
            }

        } catch (Exception e) {
            em.close();
            System.out.println("Error al realizar pago de prestamo:" + e.getMessage());
            if (!mensage.contains("CUENTA ORIGEN")) {
                mensage = e.getMessage();
            }
            return mensage;
        } finally {
            em.close();
        }

        return mensage.toUpperCase();
    }

    public validateMonetaryInstructionDTO validacionesOrdenSPEI(OrderWsSPEI orden) {
        EntityManager em = emf.createEntityManager();
        try {
            String validationId = "";
            //Validamos que exista el solicitante
            if (validarTransferenciaSPEI(orden).toUpperCase().contains("EXITO")) {
                validationId = RandomAlfa().toUpperCase();
            } else {
                validationId = validarTransferenciaSPEI(orden);
            }
        } catch (Exception e) {

        }

        return null;

    }

    //Valida el monto maximo permitido por dia
    public boolean MaxPordia(String opa, Double montoI) {
        EntityManager em = emf.createEntityManager();
        Calendar c1 = Calendar.getInstance();
        String dia = Integer.toString(c1.get(5));
        String mes = Integer.toString(c1.get(2) + 1);
        String annio = Integer.toString(c1.get(1));
        String fechaActual = String.format("%04d", Integer.parseInt(annio)) + "/" + String.format("%02d", Integer.parseInt(mes)) + "/" + String.format("%02d", Integer.parseInt(dia));
        TablasPK tbPk = new TablasPK("banca_movil", "montomaximo");
        Tablas tb = em.find(Tablas.class,
                tbPk);
        try {
            //Busco el total de monto de transferencias por dia
            String consultaTransferencias = "SELECT sum(monto) FROM e_transferenciassiscoop WHERE"
                    + " cuentaorigen='" + opa + "' AND to_char(date(fechaejecucion),'yyyy/MM/dd')='" + fechaActual + "'";
            Query query = em.createNativeQuery(consultaTransferencias);
            String montoObtenidodb = "";
            if (query.getSingleResult() != null) {
                montoObtenidodb = String.valueOf(query.getSingleResult());
            } else {
                montoObtenidodb = "0";
            }
            Double monto = Double.parseDouble(String.valueOf(montoObtenidodb)) + montoI;
            System.out.println("monto:" + monto);
            if (monto <= Double.parseDouble(tb.getDato1())) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            em.close();
            System.out.println("Error al validar permitido diario:" + e.getMessage());
        } finally {
            em.close();
        }
        return false;
    }

    private boolean aplicarCargos(String accountId, Double monto, int tipocargo) {
        EntityManager em = emf.createEntityManager();
        opaDTO opa = Util.opa(accountId);
        String ba = "SELECT * FROM auxiliares a WHERE a.idorigenp = " + opa.getIdorigenp() 
                + " AND a.idproducto = " + opa.getIdproducto() + " AND a.idauxiliar = " + opa.getIdauxiliar();
        Query query = em.createNativeQuery(ba, Auxiliares.class);
        Auxiliares a = (Auxiliares) query.getSingleResult();
        Double l = Double.parseDouble(monto.toString());
        Double s = Double.parseDouble(a.getSaldo().toString());
        boolean bandera = false;
        try {
            if (tipocargo == 0) {
                BigDecimal saldor = new BigDecimal(s - l);
                EntityTransaction tr = em.getTransaction();
                tr.begin();
                a.setSaldo(saldor);
                em.persist(a);
                tr.commit();
                bandera = true;
            } else if (tipocargo == 1) {
                BigDecimal saldor = new BigDecimal(s + l);
                EntityTransaction tr = em.getTransaction();
                tr.begin();
                a.setSaldo(saldor);
                em.persist(a);
                tr.commit();
                bandera = true;
            }
        } catch (Exception e) {
            em.close();
            System.out.println("Error en cargos:" + e.getMessage());
        } finally {
            em.close();
        }
        return bandera;
    }

    private boolean findBalance(String accountId, Double monto) {
        EntityManager em = emf.createEntityManager();
        opaDTO opa = Util.opa(accountId);
        boolean bandera = false;
        try {
            String consulta = "SELECT * FROM auxiliares a "
                    + " WHERE a.idorigenp = " + opa.getIdorigenp() + " AND a.idproducto = " + opa.getIdproducto() 
                    + " AND a.idauxiliar = " + opa.getIdauxiliar() + " AND estatus = 2 AND saldo >= " + monto;
            Query query = em.createNativeQuery(consulta, Auxiliares.class);
            Auxiliares a = (Auxiliares) query.getSingleResult();
            if (a != null) {
                bandera = true;
            }
        } catch (Exception e) {
            em.close();
            System.out.println("Error en find balance:" + e.getMessage());
            return bandera;
        }
        em.close();
        return bandera;
    }

    public String dateToString(Date cadena) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String cadenaStr = sdf.format(cadena);
        return cadenaStr;
    }

    public String RandomAlfa() {
        String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
        String CHAR_UPPER = CHAR_LOWER.toUpperCase();
        String NUMBER = "0123456789";

        String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER;
        SecureRandom random = new SecureRandom();

        String cadena = "";
        for (int i = 0; i < 15; i++) {
            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);
            cadena = cadena + rndChar;
        }
        System.out.println("Cadena:" + cadena);
        return cadena;
    }

    public String validarTransferenciaSPEI(OrderWsSPEI orden) {
        EntityManager em = emf.createEntityManager();
        ogsDTO ogs_or = Util.ogs(orden.getCIF());
        opaDTO opa_or = Util.opa(orden.getClabeSolicitante());
        String mensaje = "";
        try {
            String busquedaSolicitante = "SELECT * FROM personas WHERE idorigen = " + ogs_or.getIdorigen()
                    + " AND idgrupo = " + ogs_or.getIdgrupo() + " AND idsocio = " + ogs_or.getIdsocio();
            Query queryBusquedaSolicitante = em.createNativeQuery(busquedaSolicitante, Persona.class);
            Persona p = (Persona) queryBusquedaSolicitante.getSingleResult();
            String cuentaOrigen = "SELECT * FROM auxiliares a WHERE idorigenp = " + opa_or.getIdorigenp()
                    + " AND idproducto = " + opa_or.getIdproducto() + " AND idauxiliar = " + opa_or.getIdauxiliar();
            if (p != null) {
                Query queryOrigen = em.createNativeQuery(cuentaOrigen, Auxiliares.class);
                Auxiliares a = (Auxiliares) queryOrigen.getSingleResult();
                if (a != null) {
                    //Validamos que pertenezca al socio
                    String opa = String.format("%06d", a.getIdorigen()) + String.format("%02d", a.getIdgrupo()) + String.format("%06d", a.getIdsocio());
                    if (opa.equals(orden.getClabeSolicitante())) {
                        //Validamos el estatus
                        if (a.getEstatus() == 2) {
                            //Validar el tipo de producto
                            Productos pr = em.find(Productos.class, a.getAuxiliaresPK().getIdproducto());
                            if (pr.getTipoproducto() != 2) {
                                //Solo validado para ahorro solo faltaria para inversion
                                if (pr.getTipoproducto() == 1) {

                                } else if (pr.getTipoproducto() == 0) {
                                    if (Double.parseDouble(a.getSaldo().toString()) >= orden.getMonto()) {
                                        //Validare el maximo para banca movil
                                        mensaje = "EXITOSO";
                                    } else {
                                        mensaje = "Fondos insuficientes";
                                    }
                                }
                            } else {
                                mensaje = "No se puede transferir de un prestamo";
                            }
                        } else {
                            mensaje = "La cuenta esta inactiva";
                        }
                    } else {
                        mensaje = "La cuenta no pertenece al socio";
                    }
                } else {
                    mensaje = "Cuenta no existe";
                }
            } else {
                mensaje = "Socio no existe";
            }
        } catch (Exception e) {
            mensaje = e.getMessage();
            System.out.println("Error en procesar la validacion:" + e.getMessage());
            return mensaje;
        }
        return mensaje;
    }

    //Solo aplica para cnmx
    public boolean validarSaldosMinimoProducto(AuxiliaresPK auxPK, Double monto) {
        EntityManager em = emf.createEntityManager();
        /*try {
        Auxiliares a=em.find(Auxiliares.class, auxPK);
        Double saldoValidar=Double.parseDouble(a.getSaldo().toString())-monto;
        if(a.getAuxiliaresPK().getIdproducto()==100){
            
        }
        if(saldovalidar>=100)
            
        } catch (Exception e) {
            
        }finally{
            em.close();
        }*/
        return false;
    }

    //valida el monto para banca movil total de transferencias
    public String minMax(Double amount) {
        EntityManager em = emf.createEntityManager();
        String mensaje = "";
        try {
            TablasPK tbPk = new TablasPK("banca_movil", "montomaximominimo");
            Tablas tb = em.find(Tablas.class,
                    tbPk);
            if (amount > Double.parseDouble(tb.getDato1())) {
                mensaje = "MAYOR";
            } else if (amount < Double.parseDouble(tb.getDato2())) {
                mensaje = "MENOR";
            } else {
                mensaje = "VALIDO";
            }
        } catch (Exception e) {
            em.close();
            System.out.println("Error al validar monto min-max:" + e.getMessage());
        } finally {
            em.close();
        }
        return mensaje;
    }

    public void cerrar() {
        emf.close();
    }

}
