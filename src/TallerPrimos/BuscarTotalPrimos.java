/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TallerPrimos;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetInitiator;
import jade.proto.ContractNetResponder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;



/**
 *
 * @author shamuel
 */
public class BuscarTotalPrimos extends Agent   {
    
    // Rango inicial.
    private int vInicial;
 
    // Rango Final 
    private int vFinal;
    
    private Vector vector;

    public BuscarTotalPrimos() {
        this.vector = new Vector();
    }
    
    public Vector asignarTrabajo( int numAgent){  
        Vector vector = new Vector();
        int values = this.vFinal - this.vInicial;
        int rango = values/numAgent;       
        int aux = this.vInicial;
        
        for(int i=0; i<numAgent; i++){
            aux = this.vInicial + rango; 
            DatosBusqueda agentDatos = new DatosBusqueda("ag"+String.valueOf(i+2),this.vInicial, aux);
            vector.add(agentDatos);
            this.vInicial = aux + 1;
        }
        return vector;
    }
    
    protected void setup(){
        this.doWait(100);
    
        // Rango de numero para calcular los numeros primos.
        Object[] args = this.getArguments();
        
//        System.out.println( (String) args);
        
        if(args != null && args.length == 2){
            
            this.vInicial = Integer.parseInt(((String) args[0]));
            this.vFinal = Integer.parseInt(((String) args[1]));
            
            // B�squeda del servicio de contar Primos en las p�ginas amarillas.
            ServiceDescription servicio = new ServiceDescription();
            servicio.setType("ContarPrimos");
            servicio.setName("Contar Primos");
            
            DFAgentDescription descripcion = new DFAgentDescription();
            descripcion.addLanguages("castellano");
            descripcion.addServices(servicio);
            
            try{
                DFAgentDescription[] resultados = DFService.search(this, descripcion);
                if (resultados.length <= 0) {
                    System.out.println("No existen Agentes para ayudar con el calculo...");
                } else{
                    System.out.println("Caculando primos, tenemos " + resultados.length +" Agentes ayudando...");
                    
                    
                    ACLMessage mensajeCFP = new ACLMessage(ACLMessage.CFP);
                    for (DFAgentDescription agente:resultados) {
                        mensajeCFP.addReceiver(agente.getName());
                    }
                    mensajeCFP.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
                    
                    this.vector = this.asignarTrabajo(3);
                    
                    //Envio Datos Busqueda
                    mensajeCFP.setContentObject(this.vector);
 
                    // Se anade el comportamiento 
                    this.addBehaviour(new ManejoOpciones(this, mensajeCFP));
                    
                    this.doWait(600);
                    this.takeDown();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
           
        } else {
            System.out.println("ERROR: Parametros Incorrectos");
        }
        
    }
    
    protected void takeDown(){
        
        try {
            DFService.deregister(this);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
        // Close the GUI
//        this.dispose();
        // Printout a dismissal message
        System.out.println("Terminado el calculo"+getAID().getName());

    }  
        
    private class ManejoOpciones extends ContractNetInitiator {
        private int total;
        
        public ManejoOpciones(Agent agente, ACLMessage plantilla) {
            super(agente, plantilla);
            this.total = 0;
        }
 
        // M�todo colectivo llamado tras finalizar el tiempo de espera o recibir todas las propuestas.
        @Override
        protected void handleAllResponses(Vector respuestas, Vector aceptados) {
            
            for(Object respuesta: respuestas){
               ACLMessage res = (ACLMessage) respuesta;
               
                if (res.getPerformative() == ACLMessage.PROPOSE) {
                    int r = Integer.parseInt((String) res.getContent());
                    this.total += r;
                }
               
            }
           System.out.printf("%s: El numero total de primos es %d...\n", this.myAgent.getLocalName(), total );
           
        }

    }

}
