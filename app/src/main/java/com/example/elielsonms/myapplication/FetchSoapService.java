package com.example.elielsonms.myapplication;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.provider.ContactsContract.CommonDataKinds.Identity.NAMESPACE;
import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class FetchSoapService extends IntentService {

    public static final String PEDIDO = "PEDIDO";
    public static final String RESULTADO = "RESULTADO";

    public static final String ACTION_HORARIOS = "HORARIOS";
    public static final String ACTION_MARCAR_CONSULTA = "MARCAR_CONSULTA";

    private static  String SOAP_ACTION = "";

    private static  String METODO_HORARIOS = "obterHorarios";
    private static  String METODO_MARCAR_CONSULTA = "registrarConsulta";

    private static  String NAMESPACE = "http://webservice.vetpet.com/";
    private static  String URL = "http://elielsonms.com:8080/ClinicaVeterinaria/MarcarConsulta?wsdl";

    public FetchSoapService() {
        super("FetchSoapService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            System.out.println("Service called");
            PendingIntent pedido = intent.getParcelableExtra(PEDIDO);
            try {
                try {
                    System.out.println("Requesting webservice "+intent.getAction());
                    SoapObject request = new SoapObject(NAMESPACE, intent.getAction().equals(ACTION_HORARIOS) ? METODO_HORARIOS : METODO_MARCAR_CONSULTA);

                    SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    soapEnvelope.setOutputSoapObject(request);
                    if(!intent.getAction().equals(ACTION_HORARIOS)){
                        request.addProperty("arg0",intent.getStringExtra("cpf"));
                        request.addProperty("arg1",intent.getStringExtra("animal"));
                        request.addProperty("arg2",intent.getLongExtra("idHorario",-1));
                    }

                    HttpTransportSE transport = new HttpTransportSE(URL);

                    transport.call(SOAP_ACTION, soapEnvelope);
                    SoapObject resultObj = (SoapObject) soapEnvelope.bodyIn;

                    Intent resultado = new Intent();

                    resultado.putExtra(RESULTADO, intent.getAction().equals(ACTION_HORARIOS) ? (Serializable) montarHorarios(resultObj) : ((SoapPrimitive)resultObj.getProperty(0)).toString());
                    if(!intent.getAction().equals(ACTION_HORARIOS)){
                        System.out.println(((SoapPrimitive)resultObj.getProperty(0)).toString());
                        System.out.println(((SoapPrimitive)resultObj.getProperty(0)));
                    }
                    pedido.send(this, 1, resultado);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    pedido.send(0);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            System.out.println("Intent empty");
        }
    }

    private List<HorarioDTO> montarHorarios(SoapObject soapObject){
        List<HorarioDTO> horarios = new ArrayList<HorarioDTO>();
        for(int i=0; i < soapObject.getPropertyCount(); i++){
            SoapObject a = (SoapObject)soapObject.getProperty(i);
            HorarioDTO h = new HorarioDTO();
            h.setIdHorario(Long.parseLong(((SoapPrimitive)a.getProperty("idHorario")).getValue().toString()));
            h.setHorario((String) ((SoapPrimitive)a.getProperty("horario")).getValue());
            h.setMedico((String)((SoapPrimitive)a.getProperty("medico")).getValue());

            horarios.add(h);
        }
        return horarios;
    }

    public void diveInProperty(int nivel,SoapObject soapObject){
        printEle(nivel,soapObject.getName());
        nivel++;
        for(int i=0; i < soapObject.getPropertyCount(); i++){
            Object a = soapObject.getProperty(i);
            if(a instanceof  SoapObject) {
                diveInProperty(nivel, (SoapObject) a);
            }else{
                printEle(nivel,a.toString());
            }
        }
    }

    public void printEle(int nivel,String st){
        for(int a =0; a < nivel; a++){
            System.out.print("-");
        }
        System.out.print(nivel);
        System.out.println(st);
    }

}
