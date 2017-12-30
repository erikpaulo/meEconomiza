package com.softb.savefy.account.service;

import com.softb.savefy.account.model.AssetPrice;
import com.softb.savefy.account.repository.AssetPriceRepository;
import com.softb.system.errorhandler.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This class, put together all services user can do with indexes
 */
@Service
public class IndexService {

    @Autowired
    private AssetPriceRepository indexRepository;

    public void delete(AssetPrice index, Integer groupId) {
        AssetPrice curIndex =  indexRepository.findOne(index.getId());
        if (curIndex != null && !curIndex.getGroupId().equals(groupId)){
            throw new BusinessException("This index doesn't belong to current user.");
        }
        indexRepository.delete(index);
    }

    public AssetPrice save(AssetPrice index, Integer groupId){
        return indexRepository.save(index);
    }

    public List<AssetPrice> getAll(Integer accountId){
        return indexRepository.findAllByInvestment(accountId);
    }

    public void delIndexValue(Integer id, Integer groupId) {
        AssetPrice index = indexRepository.findOne(id);
        if (!index.getGroupId().equals(groupId)) throw new BusinessException("This index doesn't belong to currentuser");

        indexRepository.delete(id);
    }
}
