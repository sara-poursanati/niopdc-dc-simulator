package ir.nifss.policy.service;

import ir.nifss.policy.domain.fueltype.FuelTypeInfoRepository;
import ir.nifss.policy.dto.PolicyDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PolicyFacade {
    private FuelTypeInfoRepository fuelTypeInfoRepository;

    @Autowired
    public void setFuelTypeInfoRepository(FuelTypeInfoRepository fuelTypeInfoRepository) {
        this.fuelTypeInfoRepository = fuelTypeInfoRepository;
    }

    public PolicyDto getFuelTypePolicy() {
        PolicyDto policyDto = new PolicyDto();
        policyDto.setPolicyId(RandomStringUtils.random(3, true, true));
        policyDto.setVersion(RandomStringUtils.random(10, false, true));
        policyDto.setVersionName(RandomStringUtils.random(20, true, true));
        policyDto.setData(fuelTypeInfoRepository.findAll());
        return policyDto;
    }

    public PolicyDto getQuotaPolicy() {
        PolicyDto policyDto = new PolicyDto();
        policyDto.setPolicyId(RandomStringUtils.random(3, true, true));
        policyDto.setVersion(RandomStringUtils.random(10, false, true));
        policyDto.setVersionName(RandomStringUtils.random(20, true, true));
        policyDto.setData(fuelTypeInfoRepository.findAll());
        return policyDto;
    }

    public PolicyDto getBlackListPolicy() {
        PolicyDto policyDto = new PolicyDto();
        policyDto.setPolicyId(RandomStringUtils.random(3, true, true));
        policyDto.setVersion(RandomStringUtils.random(10, false, true));
        policyDto.setVersionName(RandomStringUtils.random(20, true, true));
        policyDto.setData(fuelTypeInfoRepository.findAll());
        return policyDto;
    }

    public PolicyDto getWhiteListPolicy() {
        PolicyDto policyDto = new PolicyDto();
        policyDto.setPolicyId(RandomStringUtils.random(3, true, true));
        policyDto.setVersion(RandomStringUtils.random(10, false, true));
        policyDto.setVersionName(RandomStringUtils.random(20, true, true));
        policyDto.setData(fuelTypeInfoRepository.findAll());
        return policyDto;
    }

    public PolicyDto getGrayListPolicy() {
        PolicyDto policyDto = new PolicyDto();
        policyDto.setPolicyId(RandomStringUtils.random(3, true, true));
        policyDto.setVersion(RandomStringUtils.random(10, false, true));
        policyDto.setVersionName(RandomStringUtils.random(20, true, true));
        policyDto.setData(fuelTypeInfoRepository.findAll());
        return policyDto;
    }

    public PolicyDto getCodingPolicy() {
        PolicyDto policyDto = new PolicyDto();
        policyDto.setPolicyId(RandomStringUtils.random(3, true, true));
        policyDto.setVersion(RandomStringUtils.random(10, false, true));
        policyDto.setVersionName(RandomStringUtils.random(20, true, true));
        policyDto.setData(fuelTypeInfoRepository.findAll());
        return policyDto;
    }

    public PolicyDto getTerminalSoftware() {
        PolicyDto policyDto = new PolicyDto();
        policyDto.setPolicyId(RandomStringUtils.random(3, true, true));
        policyDto.setVersion(RandomStringUtils.random(10, false, true));
        policyDto.setVersionName(RandomStringUtils.random(20, true, true));
        policyDto.setData(fuelTypeInfoRepository.findAll());
        return policyDto;
    }
}
