package com.yofish.apollo.service;

import com.yofish.apollo.domain.App;
import com.yofish.apollo.domain.AppEnvCluster;
import com.yofish.apollo.domain.AppNamespace;
import com.yofish.apollo.domain.Namespace;
import com.yofish.apollo.repository.AppEnvClusterRepository;
import com.yofish.apollo.repository.NamespaceRepository;
import common.dto.NamespaceDTO;
import common.exception.BadRequestException;
import common.exception.ServiceException;
import common.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @author WangSongJun
 * @date 2019-12-11
 */
@Service
public class NamespaceService {
    @Autowired
    private NamespaceRepository namespaceRepository;
    @Autowired
    private AppNamespaceService appNamespaceService;
    @Autowired
    private AppEnvClusterRepository appEnvClusterRepository;
    @Autowired
    private ServerConfigService serverConfigService;


    public NamespaceDTO createNamespace(String env, NamespaceDTO dto) {
        Namespace entity = BeanUtils.transform(Namespace.class, dto);
        Namespace managedEntity = this.namespaceRepository.findOne(Example.of(new Namespace(dto.getAppId(), env, dto.getClusterName(), dto.getNamespaceName()))).orElse(null);
        if (managedEntity != null) {
            throw new BadRequestException("namespace already exist.");
        }

        entity = this.namespaceRepository.save(entity);

        return BeanUtils.transform(NamespaceDTO.class, entity);
    }

    public boolean isNamespaceUnique(Long appId, String env, String cluster, String namespace) {
        Objects.requireNonNull(appId, "AppId must not be null");
        Objects.requireNonNull(cluster, "AppEnvCluster must not be null");
        Objects.requireNonNull(namespace, "Namespace must not be null");
        return Objects.isNull(namespaceRepository.findByAppIdAAndEnvAndClusterNameAndNamespaceName(appId, env, cluster, namespace));
    }

    @Transactional
    public void instanceOfAppNamespaces(Long appId, String clusterName) {

        List<AppNamespace> appNamespaces = appNamespaceService.findByAppId(appId);

        for (AppNamespace appNamespace : appNamespaces) {
            Namespace ns = new Namespace();
            ns.setAppId(appId);
            ns.setClusterName(clusterName);
            ns.setNamespaceName(appNamespace.getName());
            namespaceRepository.save(ns);
        }

    }

    public void createNamespaceForAppNamespaceInAllCluster(Long appId, String namespaceName) {
        List<AppEnvCluster> appEnvClusters = this.appEnvClusterRepository.findByAppAndParentClusterId(new App(appId), 0L);

        List<String> activeEnvs = this.serverConfigService.getActiveEnvs();
        for (String env : activeEnvs) {
            for (AppEnvCluster appEnvCluster : appEnvClusters) {

                // in case there is some dirty data, e.g. public namespace deleted in other app and now created in this app
                if (!this.isNamespaceUnique(appId, env, appEnvCluster.getName(), namespaceName)) {
                    continue;
                }

                Namespace namespace = new Namespace(appId,env, appEnvCluster.getName(),namespaceName);
                this.save(namespace);
            }
        }
    }
    @Transactional
    public Namespace save(Namespace entity) {
        if (!isNamespaceUnique(entity.getAppId(), entity.getEnv(), entity.getClusterName(), entity.getNamespaceName())) {
            throw new ServiceException("namespace not unique");
        }
        //protection
        entity.setId(0L);
        Namespace namespace = namespaceRepository.save(entity);

        return namespace;
    }

}