package com.axemblr.provisionr.amazon.activities;

import static com.google.common.base.Preconditions.checkNotNull;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.services.ec2.model.BlockDeviceMapping;
import com.amazonaws.services.ec2.model.EbsBlockDevice;
import com.amazonaws.services.ec2.model.LaunchSpecification;
import com.amazonaws.services.ec2.model.RequestSpotInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.SpotInstanceType;
import com.axemblr.provisionr.amazon.ProcessVariables;
import com.axemblr.provisionr.amazon.core.ImageTable;
import com.axemblr.provisionr.amazon.core.ImageTableQuery;
import com.axemblr.provisionr.amazon.core.KeyPairs;
import com.axemblr.provisionr.amazon.core.ProviderClientCache;
import com.axemblr.provisionr.amazon.core.SecurityGroups;
import com.axemblr.provisionr.amazon.options.ProviderOptions;
import com.axemblr.provisionr.amazon.options.SoftwareOptions;
import com.axemblr.provisionr.api.hardware.BlockDevice;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.provider.Provider;
import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import net.schmizz.sshj.common.Base64;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.VariableScope;

public abstract class RunInstances extends AmazonActivity {

    public static final String DEFAULT_ARCH = "amd64";
    public static final String DEFAULT_TYPE = "instance-store";


    protected RunInstances(ProviderClientCache providerClientCache) {
        super(providerClientCache);
    }

    protected RunInstancesRequest createOnDemandInstancesRequest(Pool pool, DelegateExecution execution) 
            throws IOException {
        return (RunInstancesRequest) createRequest(pool, execution, false);
    }

    protected RequestSpotInstancesRequest createSpotInstancesRequest(Pool pool, DelegateExecution execution) 
            throws IOException {
        return (RequestSpotInstancesRequest) createRequest(pool, execution, true);
    }

    private AmazonWebServiceRequest createRequest(Pool pool, DelegateExecution execution, boolean spot) 
            throws IOException {
        final String businessKey = execution.getProcessBusinessKey();

        final String securityGroupName = SecurityGroups.formatNameFromBusinessKey(businessKey);
        final String keyPairName = KeyPairs.formatNameFromBusinessKey(businessKey);

        final String instanceType = pool.getHardware().getType();
        final String imageId = getImageIdFromProcessVariablesOrQueryImageTable(
            execution, pool.getProvider(), instanceType);

        final String userData = Resources.toString(Resources.getResource(RunInstances.class,
            "/com/axemblr/provisionr/amazon/userdata.sh"), Charsets.UTF_8);

        List<BlockDevice> blockDevices = pool.getHardware().getBlockDevices();
        List<BlockDeviceMapping> blockDeviceMappings = Lists.newArrayList();
        if (blockDevices != null && blockDevices.size() > 0) {
            for (BlockDevice device : blockDevices) {
                blockDeviceMappings.add(new BlockDeviceMapping()
                        .withDeviceName(device.getName())
                        .withEbs(new EbsBlockDevice()
                            .withVolumeSize(device.getSize())
                            .withDeleteOnTermination(true)
                         ));
            }
        }

        if (spot) {
            Calendar validUntil = Calendar.getInstance();
            validUntil.add(Calendar.MINUTE, 10);
            final String spotPrice = checkNotNull(pool.getProvider().getOption(ProviderOptions.SPOT_BID),
                    "The bid for spot instances was not specified");
            LaunchSpecification ls = new LaunchSpecification()
                .withInstanceType(instanceType)
                .withKeyName(keyPairName)
                .withImageId(imageId)
                .withBlockDeviceMappings(blockDeviceMappings)
                .withSecurityGroups(Lists.newArrayList(securityGroupName))
                .withUserData(Base64.encodeBytes(userData.getBytes(Charsets.UTF_8)));
            return new RequestSpotInstancesRequest()
                .withSpotPrice(spotPrice)
                .withLaunchSpecification(ls)
                .withLaunchGroup(businessKey)
                .withInstanceCount(pool.getExpectedSize())
                .withType(SpotInstanceType.OneTime)
                .withValidUntil(validUntil.getTime());
        } else {
            return new RunInstancesRequest()
                .withClientToken(businessKey)
                .withSecurityGroups(securityGroupName)
                .withKeyName(keyPairName)
                .withInstanceType(instanceType)
                .withImageId(imageId)
                .withBlockDeviceMappings(blockDeviceMappings)
                .withMinCount(pool.getMinSize())
                .withMaxCount(pool.getExpectedSize())
                .withUserData(Base64.encodeBytes(userData.getBytes(Charsets.UTF_8)));
        }
    }

    private String getImageIdFromProcessVariablesOrQueryImageTable(
            VariableScope execution, Provider provider, String instanceType
        ) {
            final String imageId = (String) execution.getVariable(ProcessVariables.CACHED_IMAGE_ID);
            if (imageId != null) {
                return imageId;
            }

            ImageTable imageTable;
            try {
                imageTable = ImageTable.fromCsvResource("/com/axemblr/provisionr/amazon/ubuntu.csv");
            } catch (IOException e) {
                throw Throwables.propagate(e);
            }

            final String region = provider.getOptionOr(ProviderOptions.REGION, ProviderOptions.DEFAULT_REGION);
            final String version = provider.getOptionOr(SoftwareOptions.BASE_OPERATING_SYSTEM_VERSION,
                SoftwareOptions.DEFAULT_BASE_OPERATING_SYSTEM_VERSION);

            ImageTableQuery query = imageTable.query()
                .filterBy("region", region)
                .filterBy("version", version)
                .filterBy("arch", DEFAULT_ARCH);

            if (instanceType.equals("t1.micro")) {
                query.filterBy("type", "ebs");
            } else {
                query.filterBy("type", DEFAULT_TYPE);
            }

            return query.singleResult();
        }
}
