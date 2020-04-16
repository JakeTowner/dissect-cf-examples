package uk.ac.ljmu.fet.cs.cloud.examples.autoscaler;

import java.util.ArrayList;
import java.util.Iterator;

import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine.State;

public class FourVMScaler extends VirtualInfrastructure {

	public FourVMScaler(IaaSService cloud) {
		super(cloud);
	}

	@Override
	public void tick(long fires) {
		// a list of applications that need a virtual infrastructure 
		final Iterator<String> vmKinds = vmSetPerKind.keySet().iterator();
		while (vmKinds.hasNext()) {
			final String vmKind = vmKinds.next();
			final ArrayList<VirtualMachine> vmset = vmSetPerKind.get(vmKind);
			if (vmset.size() > 0) {
				if (vmset.size() < 3) {
					requestVM(vmKind);
				}
			}
			else if (vmset.isEmpty()) {
				requestVM(vmKind);
			}
			else for (VirtualMachine vm : vmset) {	
				if (vm.getState().equals(State.RUNNING)) {
					// Destroys the virtual machine if it has no tasks or is empty 
					if (vm.underProcessing.isEmpty() && vm.toBeAdded.isEmpty()) {
						destroyVM(vm);
					}
				}
			}
		}
	}
}
