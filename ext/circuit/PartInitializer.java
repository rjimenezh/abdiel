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

public class PartInitializer {
	
	private static PartInitializer instance = new PartInitializer();
	
	public static PartInitializer getInstance() {
		return instance;
	}
	
	protected Library lib;
	
	private PartInitializer() {
		AbdielFactory.eINSTANCE.eClass();
		// Statically run constructor
		// Loads the Library model
		ResourceSet resSet = new ResourceSetImpl();
		Resource model = resSet.getResource(
				URI.createURI("file://C:/epsilon/workspace/abdiel/model/library.abdiel"), true);
		lib = (Library)model.getContents().get(0);
	}
	
	@SuppressWarnings("rawtypes")
	public List initializePins(Part part) {
		List<Pin> generatedPins = new ArrayList<Pin>();
		String partClass = part.getClass().getSimpleName();
		PartSpecification spec = null;
		for(PartSpecification eachSpec : lib.getSpecs())
			if(partClass.startsWith(eachSpec.getName())) {
				spec = eachSpec;
				break;
			}
		//
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
}
