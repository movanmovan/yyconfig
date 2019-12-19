package com.yofish.apollo.domain;

import com.google.common.collect.Maps;
import com.yofish.apollo.repository.ReleaseRepository;
import com.yofish.apollo.service.ReleaseHistoryService;
import com.yofish.apollo.service.ReleaseService;
import com.yofish.apollo.util.ReleaseKeyGenerator;
import common.constants.ReleaseOperation;
import common.constants.ReleaseOperationContext;
import common.exception.BadRequestException;
import common.exception.NotFoundException;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.List;
import java.util.Map;

import static com.yofish.gary.bean.StrategyNumBean.getBeanByClass;
import static com.yofish.gary.bean.StrategyNumBean.getBeanInstance;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/17 上午10:54
 */
@Data
@Entity
@DiscriminatorValue("Release4Main")
public class Release4Main extends Release {

    @Column(name = "Comment", nullable = false)
    private String comment;


    public Release4Main(AppEnvClusterNamespace namespace, String name, String comment, Map<String, String> configurations, boolean isEmergencyPublish) {
        super(namespace, name, comment, configurations, isEmergencyPublish);
        this.setReleaseKey(ReleaseKeyGenerator.generateReleaseKey(namespace));
        AppEnvClusterNamespace4Main appEnvClusterNamespace4Main = (AppEnvClusterNamespace4Main) this.getAppEnvClusterNamespace();
//        if(appEnvClusterNamespace4Main.hasBranchNamespace()){
//            publishStrategy = getBeanByClass(PublishStrategy4MainWithBranch.class);
//            return;
//        }
//        publishStrategy =  getBeanByClass(PublishStrategy4MainWithoutBranch.class);

    }


    @Override
    public Release publish() {

        Release release = publishStrategy.publish(this);


        return release;
    }

//        this.getAppEnvClusterNamespace().publish(null, comment, null, isEmergencyPublish());
//        return this;

    public Release4Branch getBranchRelease() {
        return null;
    }

    @Transactional
    public Release rollback() {

        if (isAbandoned()) {
            throw new BadRequestException("release is not active");
        }
        AppEnvClusterNamespace4Main namepsace = (AppEnvClusterNamespace4Main) this.getAppEnvClusterNamespace();

        PageRequest page = new PageRequest(0, 2);
        List<Release> twoLatestActiveReleases = namepsace.findLatestActiveReleases(page);
        if (twoLatestActiveReleases == null || twoLatestActiveReleases.size() < 2) {
//            throw new BadRequestException(String.format("Can't rollback appNamespace(appId=%s, clusterName=%s, namespaceName=%s) because there is only one active release",
//                    appId,
//                    clusterName,
//                    namespaceName));
        }

        setAbandoned(true);

        getBeanInstance(ReleaseRepository.class).save(this);

        getBeanInstance(ReleaseHistoryService.class).createReleaseHistory(this, twoLatestActiveReleases.get(1).getId(), ReleaseOperation.ROLLBACK, null);

        //publish child appNamespace if appNamespace has child 灰度回滚

        if (namepsace.hasBranchNamespace()) {
            getBeanInstance(ReleaseService.class).rollbackChildNamespace(this, twoLatestActiveReleases);
        }
        return this;
    }


}
