package circuit;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import abdiel.AbdielFactory;
import abdiel.Library;
import abdiel.PartSpecification;
import abdiel.PinSpecification;
import abdiel.PortSpecification;
import abdiel.PortWiringSpecification;

/**
 * The PartInitializer class carries out
 * concrete instance initialization of
 * pins and ports associated with parts.
 * It is invoked by GMF's ElementInitializers
 * class whenever a new concrete part is added
 * to a circuit diagram.  It loads the concrete
 * ABDIEL model in order to instantiate ports
 * and pins as per the part's specification.
 * 
 * @author Ramon Jimenez 
 *
 */
public class PartInitializer {
	
	// ATTRIBUTES
	
	/** The Singleton instance. */
	private static PartInitializer instance = new PartInitializer();
	
	/** The ABDIEL part library model instance. */
	protected Library lib;
	
	// CONSTRUCTORS
	
	private PartInitializer() {
		AbdielFactory.eINSTANCE.eClass();
		// Statically run constructor
		// Loads the Library model
		ResourceSet resSet = new ResourceSetImpl();
		Resource model = resSet.getResource(
				URI.createURI("file://C:/epsilon/workspace/abdiel/model/library.abdiel"), true);
		lib = (Library)model.getContents().get(0);
	}
	
	// GETTERS/SETTERS
	
	/** Returns the Singleton instance. */ 
	public static PartInitializer getInstance() {
		return instance;
	}	
	
	// METHODS	
	
	/**
	 * Initializes pins for the newly created part.
	 * Determines which is the PartSpecification
	 * (from the ABDIEL model) for the part being
	 * initialized, and adds as many pins as said
	 * specification dictates.
	 * 
	 * @param part Part being initialized
	 * @return Pins created as per the part's specification
	 */
	@SuppressWarnings("rawtypes")
	public List initializePins(Part part) {
		List<Pin> generatedPins = new ArrayList<Pin>();
		PartSpecification spec = getPartSpecification(part);
		if(spec != null)
			for(PinSpecification pinSpec : spec.getPins()) {
				Pin pin = CircuitFactory.eINSTANCE.createPin();
				pin.setName(pinSpec.getName());
				switch(pinSpec.getPolarity()) {
				case POSITIVE: pin.setPolarity(Polarity.POSITIVE); break;
				case NEGATIVE: pin.setPolarity(Polarity.NEGATIVE); break;
				case NEUTRAL: pin.setPolarity(Polarity.NEUTRAL); break;
				}
				generatedPins.add(pin);
			}
		return generatedPins;
	}
	
	/**
	 * Initializes ports for the newly created part.
	 * Determines which is the PartSpecification
	 * (from the ABDIEL model) for the part being
	 * initialized, and adds as many ports as said
	 * specification dictates.
	 * 
	 * @param part Part being initialized
	 * @return Ports created as per the part's specification
	 */
	@SuppressWarnings("rawtypes")
	public List initializePorts(Part part) {
		List<Port> generatedPorts = new ArrayList<Port>();
		PartSpecification spec = getPartSpecification(part);
		if(spec != null)
			for(PortSpecification portSpec : spec.getPorts()) {
				Port port = CircuitFactory.eINSTANCE.createPort();
				port.setName(portSpec.getName());
				port.setProtocol(portSpec.getProtocol());
				for(PortWiringSpecification wiringSpec : portSpec.getWiring()) {
					PortWiring wiring = CircuitFactory.eINSTANCE.createPortWiring();
					// TODO lookupPin should be a method of Part, from EMF
					wiring.setPin(lookupPin(part, wiringSpec.getPin()));
					wiring.setPort(port);
					wiring.setRole(wiringSpec.getAlias());
					// TODO will these wirings make it to the diagram?
				}
				generatedPorts.add(port);
			}
		return generatedPorts;
	}
	
	/**
	 * Utility method to determine the ABDIEL model
	 * part specification for the provided part.
	 * 
	 * @param part Part whose specification is required
	 * @return ABDIEL model part specification, <code>null</code>
	 * 	if no part specification has the same name as this part
	 */
	protected PartSpecification getPartSpecification(Part part) {
		String partClass = part.getClass().getSimpleName();
		partClass = partClass.substring(0, partClass.length() - 4); // Remove trailing "Impl"
		PartSpecification spec = null;
		for(PartSpecification eachSpec : lib.getSpecs())
			if(partClass.equals(eachSpec.getName())) {
				spec = eachSpec;
				break;
			}
		
		return spec; 
	}
	
	/**
	 * Utility method to get a pin given its name
	 * from the specified part.
	 * 
	 * @param part Part from which the pin is to be returned
	 * @param pinSpec Specification of the pin to retrieve
	 * @return pin, <code>null</code>
	 * 	if no pin has the name stated by the pin specification
	 */
	protected Pin lookupPin(Part part, PinSpecification pinSpec) {
		String pinName = pinSpec.getName();
		Pin pin = null;
		for(Pin eachPin : part.getPins())
			if(pinName.equals(eachPin.getName())) {
				pin = eachPin;
				break;
			}
		
		return pin;
	}
}
