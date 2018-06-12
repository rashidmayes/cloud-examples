package com.rashidmayes.cloud.examples.userdata;

import java.io.File;

import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.KnownLinuxVirtualMachineImage;
import com.microsoft.azure.management.compute.VirtualMachine;
import com.microsoft.azure.management.compute.VirtualMachineSizeTypes;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.rest.LogLevel;

public class AzureUserData {
	
	public static void main(String[] args) {

		try {
			
			final File credFile = new File(System.getProperty("user.home"), ".azure/azure");

			Azure azure = Azure.configure()
					.withLogLevel(LogLevel.BODY_AND_HEADERS)
					.authenticate(credFile)
					.withDefaultSubscription();

			String userData = "python web_server.py &";

		    VirtualMachine linuxVM = azure.virtualMachines().define("myVM")
					.withRegion(Region.US_EAST2)
					.withNewResourceGroup("a")
					.withNewPrimaryNetwork("10.0.0.0/28")
					.withPrimaryPrivateIpAddressDynamic()
					.withNewPrimaryPublicIpAddress(Long.toString(System.currentTimeMillis(), Character.MAX_RADIX))
					.withPopularLinuxImage(KnownLinuxVirtualMachineImage.CENTOS_7_2)
					.withRootUsername("azureuser")
					.withRootPassword("@!Pw0rd")

					.withNewDataDisk(32)

					.defineNewExtension("Nephology")
					.withPublisher("Microsoft.OSTCExtensions")
					.withType("CustomScriptForLinux")
					.withVersion("1.4")
					.withMinorVersionAutoUpgrade()

					.withPublicSetting("fileUris", new String[] { "https://raw.githubusercontent.com/rashidmayes/cloud-examples/master/src/main/java/scripts/web_server.py" })
					.withPublicSetting("commandToExecute", userData)
					.attach()

					.withSize(VirtualMachineSizeTypes.BASIC_A0)
					.withTag("mytag", "mytagvalue").create();

			System.out.println(String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s", linuxVM.id(), linuxVM.name(), linuxVM.vmId(),
					linuxVM.powerState(), linuxVM.provisioningState(), linuxVM.getPrimaryPublicIpAddress().ipAddress(),
					linuxVM.osDiskId(), linuxVM.type(), linuxVM.resourceGroupName(), linuxVM.computerName()));
			
			System.out.println("http://"+ linuxVM.getPrimaryPublicIpAddress().fqdn());
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
