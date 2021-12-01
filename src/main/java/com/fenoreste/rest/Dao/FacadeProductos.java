package com.fenoreste.rest.Dao;

import com.fenoreste.rest.Entidades.Auxiliares;
import com.fenoreste.rest.Entidades.CuentasSiscoop;
import com.fenoreste.rest.Entidades.tipos_cuenta_siscoop;
import DTO.ProductsDTO;
import DTO.ogsDTO;
import com.fenoreste.rest.Util.AbstractFacade;
import com.fenoreste.rest.Util.TimerBeepClock;
import com.fenoreste.rest.Util.Utilidades;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

public abstract class FacadeProductos<T> {

    Utilidades Util = new Utilidades();

    public FacadeProductos(Class<T> entityClass) {

    }

    public List<ProductsDTO> getProductos(String accountType) {
        List<ProductsDTO> ListagetP = new ArrayList<>();
        EntityManager em = AbstractFacade.conexion();
        try {
            String consulta = "";
            if (!accountType.equals("")) {
                consulta = "SELECT * FROM tipos_cuenta_siscoop WHERE UPPER(producttypename) LIKE '%" + accountType.toUpperCase() + "%'";
            } else {
                consulta = "SELECT * FROM tipos_cuenta_siscoop";
            }
            System.out.println("consulta:" + consulta);
            Query query = em.createNativeQuery(consulta, CuentasSiscoop.class);
            List<CuentasSiscoop> Lista = query.getResultList();
            for (int i = 0; i < Lista.size(); i++) {
                CuentasSiscoop model = Lista.get(i);
                String c = "";
                c = model.getProducttypename().trim().toUpperCase();
                if (model.getProducttypename().trim().toUpperCase().contains("TIME")) {
                    c = "TIME";
                }

                ProductsDTO dto = new ProductsDTO(
                        String.valueOf(model.getIdproducto()),
                        model.getProducttypeid(),
                        c,
                        model.getDescripcion().trim().toUpperCase());
                ListagetP.add(dto);
            }
            System.out.println("ListaProd:" + ListagetP.size());
        } catch (Exception e) {
            em.close();
        }
        em.close();
        return ListagetP;
    }

    public boolean productRates(String accountType, String productCode) {
        EntityManager em = AbstractFacade.conexion();
        System.out.println("AccountType:" + accountType + ",ProductCode:" + productCode);
        try {
            String consulta = "SELECT count(*) FROM auxiliares a INNER JOIN tipos_cuenta_siscoop tps USING(idproducto) WHERE upper(tps.producttypename) LIKE '%" + accountType.toUpperCase() + "%' AND tps.idproducto=" + productCode;
            System.out.println("Consulta:" + consulta);
            Query query = em.createNativeQuery(consulta);
            int c = Integer.parseInt(String.valueOf(query.getSingleResult()));
            if (c > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error al buscar tasa de productos:" + e.getMessage());
        } finally {
            em.close();
        }
        return false;
    }

    public List<String> Rates(String accountType, int amount, String customerId, String productCode) {
        EntityManager em = AbstractFacade.conexion();
        ogsDTO ogs = Util.ogs(customerId);
        System.out.println("si llego");
        List<String> listaString = new ArrayList<>();
        try {
            String consulta = "SELECT * FROM auxiliares a INNER JOIN tipos_cuenta_siscoop tps USING(idproducto)"
                    + " WHERE a.idorigen = " + ogs.getIdorigen() + " AND a.idgrupo = " + ogs.getIdgrupo() + " AND a.idsocio = " + ogs.getIdsocio()
                    + " AND REPLACE(UPPER(tps.producttypename),' ','')='" + accountType.toUpperCase() + "' AND tps.idproducto=" + productCode;
            System.out.println("Consulta:" + consulta);
            Query query = em.createNativeQuery(consulta, Auxiliares.class);
            Auxiliares a = (Auxiliares) query.getSingleResult();
            String ven = "";
            tipos_cuenta_siscoop tps = (tipos_cuenta_siscoop) em.find(tipos_cuenta_siscoop.class, a.getAuxiliaresPK().getIdproducto());
            if (tps.getProducttypeid().intValue() == 3) {
                ven = "SELECT DATE(TRIM(TO_CHAR(DATE(a.fechaactivacion + INT4(a.plazo)),'dd/mm/yyyy'))) FROM auxiliares a WHERE idorigenp=" + a.getAuxiliaresPK().getIdorigenp() + " AND idproducto=" + a.getAuxiliaresPK().getIdproducto() + " AND idauxiliar=" + a.getAuxiliaresPK().getIdauxiliar() + " AND estatus=2";
                System.out.println("consulta2:" + ven);
            } else if (tps.getProducttypeid().intValue() == 5) {
                ven = "SELECT vence FROM amortizaciones WHERE idorigenp=" + a.getAuxiliaresPK().getIdorigenp() + " AND idproducto=" + a.getAuxiliaresPK().getIdproducto() + " AND idauxiliar=" + a.getAuxiliaresPK().getIdauxiliar() + " ORDER BY vence DESC LIMIT 1";
                System.out.println("Consulta 2:" + ven);
            }
            Query query2 = em.createNativeQuery(ven);
            String fecha = String.valueOf(query2.getSingleResult());
            listaString.add(String.valueOf(Double.parseDouble(String.valueOf(a.getTasaio()))));
            listaString.add(fecha);
            System.out.println("lista:" + listaString);
        } catch (Exception e) {
            System.out.println("Error al buscar tasa de productos:" + e.getMessage());
        }
        return listaString;
    }

    //Para eliminar PDF
    public void eliminarArchivosTemporaralesEstadosCuenta() {
        TimerBeepClock time = new TimerBeepClock();
        Toolkit.getDefaultToolkit().beep();

    }

    /*public boolean actividad_horario() {
        EntityManager em = AbstractFacade.conexion();
        boolean bandera_ = false;
        try {
            if (Util.actividad(em)) {
                bandera_ = true;
            }
        } catch (Exception e) {
            System.out.println("ERROR AL VERIFICAR EL HORARIO DE ACTIVIDAD");
        } finally {
            em.close();
        }

        return bandera_;
    }*/

    /*public void cerrar() {
        emf.close();
    }*/
}
