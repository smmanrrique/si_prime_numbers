package TallerPrimos;

import jade.core.Agent;

import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author shamuel
 */
public class BuscarPrimosRango  extends Agent {
    
    protected void setup(){
        this.doWait(150);
        System.out.printf("%s: Esperando rangos para buscar primos... \n", this.getLocalName());
        
//      Starting service set name and type
        ServiceDescription servicio = new ServiceDescription();
        servicio.setType("ContarPrimos");
        servicio.setName("Contar Primos");

//        
        DFAgentDescription descripcion = new DFAgentDescription();
        System.out.printf("%s: ver que es System... \n",getAID());
        descripcion.setName(getAID());
        descripcion.addLanguages("castellano");
        descripcion.addServices(servicio);
        
        try {
            DFService.register(this, descripcion);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        
        // Se crea una plantilla que filtre los mensajes a recibir.
        MessageTemplate template = ContractNetResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
 
        // A�adimos los comportamientos ante mensajes recibidos
        this.addBehaviour(new CrearOferta(this, template));
        
        this.doWait(36000);
        this.takeDown();

    }
    
    public void takeDown(){
        
//        try {
//            DFService.deregister(this);
//        }
//        catch (FIPAException fe) {
//            fe.printStackTrace();
//        }
        // Close the GUI
//        this.dispose();
        // Printout a dismissal message
        System.out.println("Terminado el calculo asignado al agente"+getAID().getName());

    } 
    
    private class CrearOferta extends ContractNetResponder {
        
        private int resultado;
        
        private CrearOferta(Agent agente, MessageTemplate plantilla) {
           super(agente, plantilla);
           
        }
        
        private int countPrimos(int vInicial, int vFinal  ){   
        int numPrimos = 0;
        
        for( int i =  vInicial ; i< vFinal+1; i++){
            if (i >= 2 && esPrimo(i)){                
                numPrimos ++; 
            }
        }
        
        return numPrimos;
        }
    
        private boolean esPrimo(int n){   
        for( int i =  2; i< n; i++){
            if((n % i) == 0){
                return false;
            } 
        }
        return true;
        }
         
        protected ACLMessage prepareResponse(ACLMessage cfp) throws NotUnderstoodException, RefuseException {
            System.out.printf("%s: Peticion de calculo de primos recibida %s...\n", this.myAgent.getLocalName(), cfp.getSender().getLocalName());

            try {
                Vector vector = (Vector) cfp.getContentObject();
                
                for(Object agent: vector){
                    DatosBusqueda datos = (DatosBusqueda) agent;
                    if(datos.getName().equals(this.myAgent.getLocalName())){
                        this.resultado = this.countPrimos(datos.getvInicial(), datos.getvFInal());
                        break;
                    }
                }
                
            } catch (UnreadableException ex) {
                Logger.getLogger(BuscarPrimosRango.class.getName()).log(Level.SEVERE, null, ex);
            }
           
            // Se crea el mensaje
            ACLMessage oferta = cfp.createReply();
            oferta.setPerformative(ACLMessage.PROPOSE);
            oferta.setContent(String.valueOf(this.resultado));

            
            return oferta;
        }

    } 
    
}
