package ir.niopdc.policy.facade;

import ir.niopdc.policy.domain.fuel.FuelRepository;
import ir.niopdc.policy.dto.PolicyDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PolicyFacade {
    private FuelRepository fuelRepository;

    @Autowired
    public void setFuelTypeInfoRepository(FuelRepository fuelRepository) {
        this.fuelRepository = fuelRepository;
    }

    public PolicyDto getFuelTypePolicy() {
        PolicyDto policyDto = new PolicyDto();
        policyDto.setPolicyId(RandomStringUtils.random(3, true, true));
        policyDto.setVersion(RandomStringUtils.random(10, false, true));
        policyDto.setVersionName(RandomStringUtils.random(20, true, true));
        policyDto.getCsvList().addAll(fuelRepository.findAll()
                .stream()
                .map(item -> item.convertToCsv(item))
                .toList());
        return policyDto;
    }

    public PolicyDto getQuotaPolicy() {
        return getFuelTypePolicy();
    }

    public PolicyDto getLocalQuotaPolicy() {
        return getFuelTypePolicy();
    }

    public PolicyDto getBlackListPolicy() {
        return getFuelTypePolicy();
    }

    public PolicyDto getGrayListPolicy() {
        return getFuelTypePolicy();
    }

    public PolicyDto getCodingPolicy() {
        return getFuelTypePolicy();
    }

    public PolicyDto getTerminalSoftware() {
        return getFuelTypePolicy();
    }
}
