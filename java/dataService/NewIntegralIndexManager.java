package com.zf.qqcy.dataService.vehicle.site.newCar.service;

import com.cea.core.mapper.IgnoreHibernateLazyMapper;
import com.cea.core.modules.entity.dto.SearchFilter;
import com.cea.core.modules.persistence.query.JpaQuery;
import com.cea.identity.remote.dto.DictionaryDto;
import com.zf.qqcy.dataService.common.client.ClientUtils;
import com.zf.qqcy.dataService.common.constants.Constants;
import com.zf.qqcy.dataService.member.entity.Member;
import com.zf.qqcy.dataService.member.service.MemberManager;
import com.zf.qqcy.dataService.vehicle.site.newCar.dao.NewIntegralIndexDao;
import com.zf.qqcy.dataService.vehicle.site.newCar.entity.DealerResource;
import com.zf.qqcy.dataService.vehicle.site.newCar.entity.NewIntegralIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by wys on 2015/12/2.
 */
@Component
@Transactional(readOnly = true)
public class NewIntegralIndexManager {

    @Autowired
    private NewIntegralIndexDao newIntegralIndexDao;

    @Autowired
    private DealerResourceManager dealerResourceManager;

    @Autowired
    private MemberManager memberManager;

    public Page<NewIntegralIndex> findNewIntegralIndexByFilter(Specification<NewIntegralIndex> spec, Pageable pageRequest) {
        return newIntegralIndexDao.findAll(spec, pageRequest);
    }

    @Transactional
    public void updateNewIntegralIndex(){

        newIntegralIndexDao.deleteAll();
        List<Member> list =  memberManager.findQytMember(Constants.KeyValueEnum.MEMBER_QYTDJ_APPLY_JOIN.getKey(),Constants.ONE);
        List<NewIntegralIndex> result = new ArrayList<NewIntegralIndex>();
        SearchFilter search = new SearchFilter();
        for(Member member :list){
            search.setSize(Constants.VEHICLEINDEX_QYT);
            search.addFilter("EQ_publishStatus", Constants.KeyValueEnum.VEHICLE_STATE_DS.getKey());
            search.addFilter("EQ_dealer.id", member.getId());
            //search.setSortField("optime").setSortDir("desc");
            JpaQuery<DealerResource> query = new JpaQuery<DealerResource>(search);
            Page<DealerResource> page = dealerResourceManager.findByFilter(query.getSpec(), query.getPageRequest());
            for(DealerResource dealerResource :page.getContent()){
                if(result.size() < Constants.VEHICLEINDEX_PAGESIZE){
                    NewIntegralIndex newIntegralIndex = getNewIntegralIndex(dealerResource, result.size());
                    result.add(newIntegralIndex);
                }
            }
        }
        if(result.size() < Constants.VEHICLEINDEX_PAGESIZE){

            search = new SearchFilter();
            search.setSize(Constants.VEHICLEINDEX_PAGESIZE);
            search.addFilter("EQ_publishStatus", Constants.KeyValueEnum.VEHICLE_STATE_DS.getKey());
            JpaQuery<DealerResource> query = new JpaQuery<DealerResource>(search);
            Page<DealerResource> page = dealerResourceManager.findByFilter(query.getSpec(), query.getPageRequest());
            for(DealerResource dealerResource :page.getContent()){
                if(result.size() < Constants.VEHICLEINDEX_PAGESIZE){
                    NewIntegralIndex newIntegralIndex = getNewIntegralIndex(dealerResource, result.size());
                    boolean ifYou=false;
                    for(NewIntegralIndex index:result){
                        if(index.getIntegralid().equals(newIntegralIndex.getIntegralid())){
                            ifYou = true;
                            break;
                        }
                    }
                    if(!ifYou){
                        result.add(newIntegralIndex);
                    }
                }else{
                    break;
                }
            }
        }
        if(result.size()>0) {
            newIntegralIndexDao.save(result);
            /*for (NewIntegralIndex entity : result) {
                NewIntegralIndex newIntegralIndex = newIntegralIndexDao.getNewIntegralIndexByIntegralid(entity.getIntegralid());
                if (newIntegralIndex == null || newIntegralIndex.getId() == null) {
                    newIntegralIndexDao.save(entity);
                }
            }*/
        }
    }

    private NewIntegralIndex getNewIntegralIndex(DealerResource entity,int resultSize){

        NewIntegralIndex newIntegralIndex = new NewIntegralIndex();
        if(entity.getDealer() != null){
            if(entity.getDealer().getQytdj() == Constants.KeyValueEnum.MEMBER_QYTDJ_APPLY_JOIN.getKey()){// 亲友团发车
                newIntegralIndex.setSfqyt(Constants.KeyValueEnum.MEMBER_QYTDJ_APPLY_JOIN.getKey());
            }else{// 不是亲友团发车
                newIntegralIndex.setSfqyt(Constants.KeyValueEnum.MEMBER_QYTDJ_NOT_JOIN.getKey());
            }
            newIntegralIndex.setMemberId(entity.getDealer().getId());
        }
        newIntegralIndex.setIntegralid(entity.getIntegral().getSubBrand().getId());
        newIntegralIndex.setIntegralname(entity.getIntegral().getSubBrand().getTreeName());

        if(entity.getIntegral() != null ){
            if(entity.getIntegral().getVehicleType() != null) {
                try {
//                    Dictionary  dictionary = IgnoreHibernateLazyMapper.map(dto, CurrentAccount.class);
                    String vehicleType = entity.getIntegral().getVehicleType();
                    DictionaryDto dictionaryDto = ClientUtils.getBaseInfoClient().findDictionaryById(vehicleType);
                    if(dictionaryDto != null) {
                        dictionaryDto = IgnoreHibernateLazyMapper.map(dictionaryDto, DictionaryDto.class);
                        newIntegralIndex.setVehicleType(dictionaryDto.getId());
                        newIntegralIndex.setVehicleTypeName(dictionaryDto.getName());
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            if(entity.getIntegral().getMainPicture() != null) {
                newIntegralIndex.setMainPicture(entity.getIntegral().getMainPicture());
            }
        }

        if (entity.getIntegral().getGuidePriceLow() != null && entity.getIntegral().getGuidePriceHigh() != null) {
            newIntegralIndex.setXszdj(entity.getIntegral().getGuidePriceLow() + "-" + entity.getIntegral().getGuidePriceHigh() + "万");
        } else {
            if (entity.getIntegral().getGuidePriceLow() != null) {
                newIntegralIndex.setXszdj(entity.getIntegral().getGuidePriceLow() + "万");
            }
            if (entity.getIntegral().getGuidePriceHigh() != null) {
                newIntegralIndex.setXszdj(entity.getIntegral().getGuidePriceHigh() + "万");
            }
        }
        newIntegralIndex.setPrice(entity.getPrice());
        newIntegralIndex.setResourceType(entity.getResourceType());
        if(entity.getResourceType().equals(Constants.DEALER_RESOURCE_TYPE_RECOMMEND)){
            newIntegralIndex.setResourceTypeName(Constants.DEALER_RESOURCE_TYPE_RECOMMEND_NAME);
        }else if(entity.getResourceType().equals(Constants.DEALER_RESOURCE_TYPE_SALE)){
            newIntegralIndex.setResourceTypeName(Constants.DEALER_RESOURCE_TYPE_SALE_NAME);
        }
        newIntegralIndex.setTs(getTs(entity.getOptime()));
        newIntegralIndex.setNum(resultSize+1);

        return newIntegralIndex;
    }

    public String getTs(Date fbrq) {
        String result = "--";
        if(fbrq != null){
            Date now = new Date();
            long days = (now.getTime() - fbrq.getTime()) / (1000 * 60 * 60 * 24);
            int daysInt=(int)days;
            if( daysInt == 0){
                result = "今天";
            } else if(daysInt > 0 && daysInt < 8){
                result = days + "天前";
            } else if(daysInt >= 8 && daysInt < 14){
                result = "一周前";
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String fbrqStr = sdf.format(fbrq);
                result = fbrqStr;
            }
        }
        return result;
    }
}
