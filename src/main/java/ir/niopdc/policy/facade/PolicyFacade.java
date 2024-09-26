package ir.niopdc.policy.facade;

import ir.niopdc.common.utils.CsvUtils;
import ir.niopdc.policy.domain.blacklist.BlackList;
import ir.niopdc.policy.domain.blacklist.BlackListService;
import ir.niopdc.policy.domain.fuel.FuelService;
import ir.niopdc.policy.domain.quotarule.QuotaRuleService;
import ir.niopdc.policy.dto.DataDto;
import ir.niopdc.policy.dto.PolicyDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.crypto.Data;
import java.util.stream.Stream;

@Service
public class PolicyFacade {
    private FuelService fuelService;
    private QuotaRuleService quotaRuleService;
    private BlackListService blackListService;

    private CsvUtils csvUtils;

    @Autowired
    public void setFuelService(FuelService fuelService) {
        this.fuelService = fuelService;
    }

    @Autowired
    public void setQuotaRuleService(QuotaRuleService quotaRuleService) {
        this.quotaRuleService = quotaRuleService;
    }

    @Autowired
    public void setBlackListService(BlackListService blackListService) {
        this.blackListService = blackListService;
    }

    @Autowired
    public void setCsvUtils(CsvUtils csvUtils) {this.csvUtils = csvUtils;}

    public PolicyDto getFuelTypePolicy() {
        PolicyDto policyDto = getPolicyDto();
        policyDto.getCsvList().addAll(fuelService.findAll()
                .stream()
                .map(csvUtils::convertToCsv)
                .toList());
        return policyDto;
    }

    public PolicyDto getQuotaPolicy() {
        PolicyDto policyDto = getPolicyDto();
        policyDto.getCsvList().addAll(quotaRuleService.findAll()
                .stream()
                .map(csvUtils::convertToCsv)
                .toList());
        return policyDto;
    }

    public PolicyDto getLocalQuotaPolicy() {
        PolicyDto policyDto = getPolicyDto();
        policyDto.getCsvList().addAll(quotaRuleService.findAll()
                .stream()
                .map(csvUtils::convertToCsv)
                .toList());
        return policyDto;
    }

    @Transactional(readOnly = true)
    public DataDto getBlackListPolicy() {
        DataDto dto = new DataDto();
        try (Stream<BlackList> blackListStream = blackListService.streamAll()) {
            dto.setCsvList(blackListService.streamAll().map(csvUtils::convertToCsv));
        }
        return dto;
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

    private PolicyDto getPolicyDto() {
        PolicyDto policyDto = new PolicyDto();
        policyDto.setPolicyId(RandomStringUtils.random(3, true, true));
        policyDto.setVersion(RandomStringUtils.random(10, false, true));
        policyDto.setVersionName(RandomStringUtils.random(20, true, true));
        return policyDto;
    }
}
