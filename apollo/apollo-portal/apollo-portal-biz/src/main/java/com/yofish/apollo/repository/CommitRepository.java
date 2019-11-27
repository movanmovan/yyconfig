package com.yofish.apollo.repository;

import com.yofish.apollo.domain.App;
import com.yofish.apollo.domain.Commit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

/**
 * Created on 2018/2/5.
 *
 * @author zlf
 * @since 1.0
 */
@Component
public interface CommitRepository extends JpaRepository<Commit, Long> {




}