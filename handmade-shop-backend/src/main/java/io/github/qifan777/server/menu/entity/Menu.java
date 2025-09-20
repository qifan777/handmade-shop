package io.github.qifan777.server.menu.entity;

import io.github.qifan777.server.dict.model.DictConstants;
import io.github.qifan777.server.infrastructure.jimmer.BaseEntity;
import io.github.qifan777.server.role.entity.RoleMenuRel;
import io.qifan.infrastructure.generator.core.*;
import jakarta.validation.constraints.Null;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.OneToMany;

import java.util.List;

@GenEntity
@Entity
public interface Menu extends BaseEntity {

    @GenField(value = "菜单名称", order = 0)
    String name();

    @GenTextField(label = "父菜单Id", order = 1)
    @Null
    String parentId();

    @GenTextField(label = "路由路径", order = 2)
    String path();

    @GenNumberField(label = "排序号", order = 3)
    Integer orderNum();

    @GenDictField(label = "菜单类型", dictEnName = DictConstants.MENU_TYPE, order = 4)
    DictConstants.MenuType menuType();

    @GenImageField(label = "图标", order = 5)
    @Null
    String icon();

    @OneToMany(mappedBy = "menu")
    List<RoleMenuRel> roles();


    @GenField(value = "是否可见")
    boolean visible();
}
