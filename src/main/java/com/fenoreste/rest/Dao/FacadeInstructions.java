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
import com.fenoreste.rest.Entidades.Persona;
import com.fenoreste.rest.Entidades.Productos;
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
            String tipotransferencia,
            String cuentaorigen,
            String cuentadestino,
            Double montoTransferencia,
            String comentario,
            String propcuentadestino,
            String fechaejecucion,
            String tipoejecucion) {
        System.out.println("FechaEjecucion:" + fechaejecucion);
        System.out.println("monto de la transferencia:" + montoTransferencia);
        boolean bandera = false;
        Calendar c1 = Calendar.getInstance();
        String dia = Integer.toString(c1.get(5));
        String mes = Integer.toString(c1.get(2) + 1);
        String annio = Integer.toString(c1.get(1));
        String FechaTiempoReal = String.format("%04d", Integer.parseInt(annio)) + "-" + String.format("%02d", Integer.parseInt(mes)) + "-" + String.format("%02d", Integer.parseInt(dia));

        validateMonetaryInstructionDTO dto = null;
        EntityManager em = emf.createEntityManager();
        String[] fees = new String[0];
        String validationId = "";
        Query queryf = em.createNativeQuery("SELECT date(now())");
        String fe = String.valueOf(queryf.getSingleResult());
        Date hoy = new Date();

        try {
            //Busca la cuenta y busca si tiene saldo
            if (findAccount(cuentaorigen, customerId) && findBalance(cuentaorigen, montoTransferencia)) {
                validationId = RandomAlfa().toUpperCase();
                //si es una pago de servicio Nomas la guarda porque no se esta habilitado el servicio pero si debe validar que exista la cuenta origen y que tenga saldo
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
        }
        return dto;
    }

    public validateMonetaryInstructionDTO validacionesOrdenSPEI(OrderWsSPEI orden) {
        EntityManager em = emf.createEntityManager();
        try {
            String validationId="";
            //Validamos que exista el solicitante
            if(validarTransferenciaSPEI(orden).toUpperCase().contains("EXITO")){
              validationId=RandomAlfa().toUpperCase();
            }else{
                validationId=validarTransferenciaSPEI(orden);
            }
        }catch (Exception e) {
            
        }
        
        return null;
        
    }

    public String executeMonetaryInstruction(String validationId) {

        boolean bandera = false;
        Calendar c1 = Calendar.getInstance();
        String dia = Integer.toString(c1.get(Calendar.DATE));
        String mes = Integer.toString(c1.get(Calendar.MONTH) + 1);
        String annio = Integer.toString(c1.get(Calendar.YEAR));
        EntityManager em = emf.createEntityManager();
        String mensaje = "";
        try {
            String consulta = "SELECT * FROM v_transferenciassiscoop WHERE validationid='" + validationId + "' ORDER BY fechaejecucion DESC LIMIT 1";
            System.out.println("Siscoop");
            Query query = em.createNativeQuery(consulta, validaciones_transferencias_siscoop.class);
            validaciones_transferencias_siscoop vlt = (validaciones_transferencias_siscoop) query.getSingleResult();
            System.out.println("dfdsf");

            Query queryf = em.createNativeQuery("SELECT date(now())");
            String fe = String.valueOf(queryf.getSingleResult()).replace("-", "/");
            System.out.println("fe:" + fe);
            Date hoy = stringToDate(fe);
            System.out.println("vali:" + vlt.getCuentaorigen());
            String c2 = "SELECT saldo -" + vlt.getMonto() + " FROM auxiliares a where replace(to_char(a.idorigenp,'099999')||to_char(a.idproducto,'09999')||to_char(idauxiliar,'09999999'),' ','')='" + vlt.getCuentaorigen() + "'";
            System.out.println("c2:" + c2);
            Query query1 = em.createNativeQuery(c2);
            Double saldo = Double.parseDouble(String.valueOf(query1.getSingleResult()));
            if (findBalance(vlt.getCuentaorigen(), vlt.getMonto())) {
                EntityTransaction tr = em.getTransaction();
                tr.begin();
                transferencias_completadas_siscoop vl = new transferencias_completadas_siscoop();
                vl.setCuentaorigen(vlt.getCuentaorigen());
                vl.setCuentadestino(vlt.getCuentadestino());
                vl.setTipotransferencia(vlt.getTipotransferencia());
                vl.setComentario1(vlt.getComentario1());
                vl.setComentario2(vlt.getComentario2());
                vl.setCustomerId(vlt.getCustomerId());
                vl.setFechaejecucion(vlt.getFechaejecucion());
                vl.setMonto(vlt.getMonto());
                vl.setTipoejecucion(vlt.getTipoejecucion());
                vl.setEstatus(true);
                vl.setRunningBalance(saldo);
                bandera = true;
                if (bandera && aplicarCargos(vlt.getCuentaorigen(), vlt.getMonto(), 0)) {
                    em.persist(vl);
                    tr.commit();

                    mensaje = "completed";
                }

            }

        } catch (Exception e) {
            System.out.println("Error en execute:" + e.getMessage());
            e.printStackTrace();
            em.close();
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
        try {
            String consulta = "SELECT p.nombre||' '||p.appaterno||' '||p.apmaterno as nombre FROM auxiliares a "
                    + " INNER JOIN personas p USING(idorigen,idgrupo,idsocio)"
                    + " WHERE replace(to_char(a.idorigenp,'099999')||to_char(a.idproducto,'09999')||to_char(a.idauxiliar,'09999999'),' ','')='" + accountId + "'";
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

    private boolean findAccount(String accountId, String customerId) {
        EntityManager em = emf.createEntityManager();
        boolean bandera = false;
        try {
            String consulta = "SELECT * FROM auxiliares a "
                    + "WHERE replace(to_char(a.idorigenp,'099999')||to_char(a.idproducto,'09999')||to_char(a.idauxiliar,'09999999'),' ','')='" + accountId + "'"
                    + " AND  replace(to_char(a.idorigen,'099999')||to_char(a.idgrupo,'09')||to_char(a.idsocio,'099999'),' ','')='" + customerId + "' AND estatus=2";
            Query query = em.createNativeQuery(consulta, Auxiliares.class);
            System.out.println("consulta:" + consulta);
            Auxiliares a = (Auxiliares) query.getSingleResult();
            if (a != null) {
                bandera = true;
            }
        } catch (Exception e) {
            System.out.println("Error en find opa:" + e.getMessage());
            return bandera;
        } finally {
            em.close();
        }
        return bandera;
    }
    
    private boolean validarTransferenciaEntreMisCuentas(String socio, String opaOrigen, Double montoTransferencia, String opaDestino) {
        EntityManager em = emf.createEntityManager();
        String cuentaOrigen = "SELECT * FROM auxiliares a "
                            + " WHERE replace(to_char(a.idorigenp,'099999')||to_char(a.idproducto,'09999')||to_char(a.idauxiliar,'09999999'),' ','') = '" + opaOrigen + "'"
                            + " AND replace(to_char(a.idorigen,'099999')||to_char(a.idgrupo,'09')||to_char(a.idsocio,'099999'),' ','') = '" + socio + "' AND estatus = 2";
        String cuentaDestino = "SELECT * FROM auxiliares a "
                            + " WHERE replace(to_char(a.idorigenp,'099999')||to_char(a.idproducto,'09999')||to_char(a.idauxiliar,'09999999'),' ','') = '" + opaDestino + "'"
                            + " AND replace(to_char(a.idorigen,'099999')||to_char(a.idgrupo,'09')||to_char(a.idsocio,'099999'),' ','') = '" + socio + "' AND estatus = 2";
        boolean bandera = false;
        
        try {
            Auxiliares ctaOrigen = null;
            boolean bOrigen = false;
            
            try {
                Query query = em.createNativeQuery(cuentaOrigen, Auxiliares.class);
                ctaOrigen = (Auxiliares) query.getSingleResult();
                bOrigen = true;
            } catch (Exception e) {
                System.out.println("No Existe Cuenta Origen");
                bOrigen = false;
            }
            
            if (bOrigen) {
                Double saldo = Double.parseDouble(ctaOrigen.getSaldo().toString());
                Productos prOrigen = em.find(Productos.class, ctaOrigen.getAuxiliaresPK().getIdproducto());
                    //si el producto no es un prestamo
                    if (prOrigen.getTipoproducto() == 0) {
                        //Verifico el estatus de la cuenta origen
                        if (ctaOrigen.getEstatus() == 2) {
                            //verifico que el saldo del producto origen es mayor o igual a lo que se intenta transferir
                            if (saldo >= montoTransferencia) {
                                Auxiliares ctaDestino = null;
                                boolean bDestino = false;
                                //Busco la cuenta destino
                                System.out.println("CuentaDestino:" + cuentaDestino);
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
                                            if (productoDestino.getTipoproducto() == 0) {
                                                //Valido que realmente el producto destino pertenezca al mismo socio 
                                                if (ctaOrigen.getIdorigen() == ctaDestino.getIdorigen() && ctaOrigen.getIdgrupo() == ctaDestino.getIdgrupo() && ctaOrigen.getIdsocio() == ctaDestino.getIdsocio()) {
                                                    //Si se puede realizar la transferencia
                                                    bandera = true;
                                                } else {
                                                    System.out.println("PRODUCTO DESTINO NO PERTENECE AL MISMO SOCIO");
                                                }
                                            } else {
                                                System.out.println("PRODUCTO DESTINO NO ACEPTA SOBRECARGOS");
                                            }
                                    } else {
                                        System.out.println("PRODUCTO DESTINO ESTA INACTIVA");
                                    }
                                } else {
                                    System.out.println("NO SE ENCONTRO PRODUCTO DESTINO");
                                }
                            } else {
                                System.out.println("FONDOS INSUFICIENTES PARA COMPLETAR LA TRANSACCION");
                            }
                        } else {
                            System.out.println("PRODUCTO ORIGEN INACTIVO");
                        }
                    } else {
                        System.out.println("PRODUCTO ORIGEN NO PERMITE SOBRECARGOS");
                    }
            } else {
                System.out.println("Producto origen no pertenece al socio");
            }
            
        } catch (Exception e) {
            em.close();
            System.out.println("Error al realizar tranferencia entre mis cuentas");
            return bandera;
        } finally {
            em.close();
        }

        return bandera;
    }

    private boolean aplicarCargos(String accountId, Double monto, int tipocargo) {
        EntityManager em = emf.createEntityManager();
        String ba = "SELECT * FROM auxiliares a WHERE replace(to_char(a.idorigenp,'099999')||to_char(a.idproducto,'09999')||to_char(a.idauxiliar,'09999999'),' ','')='" + accountId + "'";
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
        boolean bandera = false;
        System.out.println("Llegooooooooooooooo");
        try {
            String consulta = "SELECT * FROM auxiliares a "
                    + "WHERE replace(to_char(a.idorigenp,'099999')||to_char(a.idproducto,'09999')||to_char(a.idauxiliar,'09999999'),' ','')='" + accountId + "' AND estatus=2 AND saldo>=" + monto;
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
    
    public String validarTransferenciaSPEI(OrderWsSPEI orden){
        EntityManager em=emf.createEntityManager();
        String mensaje="";
        try {
           String busquedaSolicitante="SELECT * FROM personas WHERE replace(to_char(idorigen,'099999')||to_char(idgrupo,'09')||to_char(idsocio,'099999'),' ','')='"+orden.getCIF()+"'";
           Query queryBusquedaSolicitante=em.createNativeQuery(busquedaSolicitante,Persona.class);
           Persona p=(Persona) queryBusquedaSolicitante.getSingleResult();
           String cuentaOrigen="SELECT * FROM auxiliares a WHERE replace(to_char(idorigenp,'099999')||to_char(idproducto,'09999')||to_char(idauxiliar,'09999999'),' ','')='"+orden.getClabeSolicitante()+"'";
           if(p!=null){
               Query queryOrigen=em.createNativeQuery(cuentaOrigen,Auxiliares.class);
               Auxiliares a=(Auxiliares)queryOrigen.getSingleResult();
               if(a!=null){
                   //Validamos que pertenezca al socio
                   String opa=String.format("%06d",a.getIdorigen())+String.format("%02d",a.getIdgrupo())+String.format("%06d",a.getIdsocio());
                   if(opa.equals(orden.getClabeSolicitante())){
                       //Validamos el estatus
                       if(a.getEstatus()==2){
                           //Validar el tipo de producto
                           Productos pr=em.find(Productos.class,a.getAuxiliaresPK().getIdproducto());
                           if(pr.getTipoproducto()!=2){
                               //Solo validado para ahorro solo faltaria para inversion
                               if(pr.getTipoproducto()==1){
                                   
                               }else if(pr.getTipoproducto()==0){
                                   if(Double.parseDouble(a.getSaldo().toString())>=orden.getMonto()){
                                       //Validare el maximo para banca movil
                                       mensaje="EXITOSO";
                                   }else{
                                       mensaje="Fondos insuficientes";
                                   }
                               }
                           }else{
                               mensaje="No se puede transferir de un prestamo";
                           }     
                       }else{
                           mensaje="La cuenta esta inactiva";
                       }
                   }else{
                       mensaje="La cuenta no pertenece al socio";
                   }
               }else{
                   mensaje="Cuenta no existe";
               }
           }else{
               mensaje="Socio no existe";
           }
        } catch (Exception e) {
            mensaje=e.getMessage();
            System.out.println("Error en procesar la validacion:"+e.getMessage());
            return mensaje;
        }
        return mensaje;
    }
    public void cerrar() {
        emf.close();
    }

}
