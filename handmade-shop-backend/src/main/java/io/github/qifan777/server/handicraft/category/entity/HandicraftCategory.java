package io.github.qifan777.server.handicraft.category.entity;

import io.github.qifan777.server.handicraft.root.entity.Handicraft;
import io.github.qifan777.server.infrastructure.jimmer.BaseEntity;
import io.qifan.infrastructure.generator.core.*;
import jakarta.validation.constraints.Null;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.OneToMany;

import java.util.List;

/**
 * 手工预约分类
 */
@GenEntity
@Entity
public interface HandicraftCategory extends BaseEntity {

    /**
     * 类别名称
     */
    @GenTextField(label = "类别名称", order = 0)
    String name();

    /**
     * 类别图标
     */
    @GenImageField(label = "类别图标", order = 1)
    @Null
    String icon();

    /**
     * 描述
     */
    @Null
    @GenTextAreaField(label = "类别描述", order = 2)
    String description();

    /**
     * 排序号
     */
    @GenNumberField(label = "排序", order = 3)
    int sortOrder();

    @OneToMany(mappedBy = "category")
    List<Handicraft> handicrafts();
}

